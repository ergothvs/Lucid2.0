package handling.cashshop.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import constants.GameConstants;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.CharacterTransfer;
import handling.world.World;
import net.DatabaseConnection;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.cash.CashItem;
import server.cash.CashItemFactory;
import server.cash.CashItemInfo;
import server.quest.MapleQuest;
import server.quest.MapleQuestStatus;
import tools.FileoutputUtil;
import tools.Triple;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CSPacket;
import tools.packet.CWvsContext;

public class CashShopOperation {

    public static void LeaveCS(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());

        try {

            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());
            c.getSession().write(CField.getChannelChange(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1])));
        } finally {
            final String s = c.getSessionIPAddress();
            LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
            chr.saveToDB(false, true);
            c.setPlayer(null);
            c.setReceiving(false);
        }
    }

    public static void EnterCS(final CharacterTransfer transfer, final MapleClient c) {
        if (transfer == null) {
            c.getSession().close();
            return;
        }
        MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);

        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());

        if (!c.CheckIPAddress()) { // Remote hack
            c.getSession().close();
            return;
        }

        final int state = c.getLoginState();
        boolean allowLogin = false;
        if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL) {
            if (!World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
                allowLogin = true;
            }
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            return;
        }
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        CashShopServer.getPlayerStorage().registerPlayer(chr);
        c.getSession().write(CSPacket.warpCS(c));
        c.getSession().write(CSPacket.loadCategories());
        c.getSession().write(CSPacket.CS_Picture_Item());
        c.getSession().write(CSPacket.CS_Top_Items());
        c.getSession().write(CSPacket.CS_Special_Item());
        c.getSession().write(CSPacket.CS_Featured_Item());
        c.getSession().write(CSPacket.showNXMapleTokens(c.getPlayer()));
        //doCSPackets(c);
    }

    public static void CSUpdate(final MapleClient c) {
        doCSPackets(c);
    }

    private static boolean CouponCodeAttempt(final MapleClient c) {
        c.couponAttempt++;
        return c.couponAttempt > 5;
    }

    public static void CouponCode(final String code, final MapleClient c) {
        if (code.length() <= 0) {
            return;
        }
        Triple<Boolean, Integer, Integer> info = null;
        try {
            info = MapleCharacterUtil.getNXCodeInfo(code);
        } catch (SQLException e) {
        }
        if (info != null && info.left) {
            if (!CouponCodeAttempt(c)) {
                int type = info.mid, item = info.right;
                try {
                    MapleCharacterUtil.setNXCodeUsed(c.getPlayer().getName(), code);
                } catch (SQLException e) {
                }
                /*
                 * Explanation of type!
                 * Basically, this makes coupon codes do
                 * different things!
                 *
                 * Type 1: A-Cash,
                 * Type 2: Maple Points
                 * Type 3: Item.. use SN
                 * Type 4: Mesos
                 */
                Map<Integer, Item> itemz = new HashMap<>();
                int maplePoints = 0, mesos = 0;
                switch (type) {
                    case 1:
                    case 2:
                        c.getPlayer().modifyCSPoints(type, item, false);
                        maplePoints = item;
                        break;
                    case 3:
                        CashItemInfo itez = CashItemFactory.getInstance().getItem(item);
                        if (itez == null) {
                            c.getSession().write(CSPacket.sendCSFail(0));
                            return;
                        }
                        byte slot = MapleInventoryManipulator.addId(c, itez.getId(), (short) 1, "", "Cash shop: coupon code" + " on " + FileoutputUtil.CurrentReadable_Date());
                        if (slot < 0) {
                            c.getSession().write(CSPacket.sendCSFail(0));
                            return;
                        } else {
                            itemz.put(item, c.getPlayer().getInventory(GameConstants.getInventoryType(item)).getItem(slot));
                        }
                        break;
                    case 4:
                        c.getPlayer().gainMeso(item, false);
                        mesos = item;
                        break;
                }
                c.getSession().write(CSPacket.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
                doCSPackets(c);
            }
        } else {
            if (CouponCodeAttempt(c) == true) {
                c.getSession().write(CSPacket.sendCSFail(48)); //A1, 9F
            } else {
                c.getSession().write(CSPacket.sendCSFail(info == null ? 14 : 17)); //A1, 9F
            }
        }
    }

    public static void BuyCashItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int action = slea.readByte();
//        System.out.println("action " + action);
        if (action == 0) {
            slea.skip(2);
            CouponCode(slea.readMapleAsciiString(), c);
        } else if (action == 2) {
            boolean bUsingMaplePoints = slea.readByte() > 0; //If type is two (MapleCash being used), set to true. 
            int dwPurchaseOption = slea.readInt();
            
            slea.skip(1); //Unsure, new 176
            int sn = slea.readInt();
            
            final CashItem item = CashItemFactory.getInstance().getAllItem(sn);
            final int toCharge = slea.readInt();
            int nChkQuantity = slea.readInt(); //Quantity of the item, unused it seems. 
            //slea.readInt(); //Not sure what the final three Ints are, GMS has them zero'd out.
            //slea.readInt(); Commenting them out to prevent any type of error 38 just incase, since these are unused.
            //slea.readInt(); 
            
            if (item == null) {
                //Maybe add a check to compare quantity from packet to quantity of item retrieved from SN?
                c.getSession().write(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            chr.modifyCSPoints(dwPurchaseOption, -toCharge, true);
            Item itemz = chr.getCashInventory().toItem(item);
            if (itemz != null) {
                chr.getCashInventory().addToInventory(itemz);
                c.getSession().write(CSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
            } else {
                c.getSession().write(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
        } else if (action == 101) {//TODO BETTER idk what it is
//            System.out.println("action 101");//might be farm mesos? RITE NOW IS FREEH
            slea.skip(1);
            int type = slea.readInt();//type
            int sn = slea.readInt();
            final CashItem item = CashItemFactory.getInstance().getAllItem(sn);
            if (item == null) {
                c.getSession().write(CSPacket.sendCSFail(0));
            }
//            chr.modifyCSPoints(type, -toCharge, true);
            Item itemz = chr.getCashInventory().toItem(item);
            if (itemz != null) {
                chr.getCashInventory().addToInventory(itemz);
                c.getSession().write(CSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
            } else {
                c.getSession().write(CSPacket.sendCSFail(0));
            }
        } else if (action == 3) {
            slea.skip(1);
            final int toCharge = slea.readInt();
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());

            if (item != null && chr.getCSPoints(toCharge) >= item.getPrice()) {
                if (!item.genderEquals(c.getPlayer().getGender())/* && c.getPlayer().getAndroid() == null*/) {
                    c.getSession().write(CSPacket.sendCSFail(0xA7));
                    doCSPackets(c);
                    return;
                } else if (item.getId() == 5211046 || item.getId() == 5211047 || item.getId() == 5211048 || item.getId() == 5050100 || item.getId() == 5051001) {
                    c.getSession().write(CWvsContext.broadcastMsg(1, "You cannot purchase this item through cash shop."));
                    c.getSession().write(CWvsContext.enableActions());
                    doCSPackets(c);
                    return;
                } else if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                    c.getSession().write(CSPacket.sendCSFail(0xB2));
                    doCSPackets(c);
                    return;
                }
                for (int id : GameConstants.cashBlock) {
                    if (item.getId() == id) {
                        c.getSession().write(CWvsContext.broadcastMsg(1, "You cannot purchase this item through cash shop."));
                        c.getSession().write(CWvsContext.enableActions());
                        doCSPackets(c);
                        return;
                    }
                }
                chr.modifyCSPoints(toCharge, -item.getPrice(), false);
                Item itemz = chr.getCashInventory().toItem(item);
                if (itemz != null && itemz.getUniqueId() > 0 && itemz.getItemId() == item.getId() && itemz.getQuantity() == item.getCount()) {
                    chr.getCashInventory().addToInventory(itemz);
                    //c.getSession().write(CSPacket.confirmToCSInventory(itemz, c.getAccID(), item.getSN()));
                    c.getSession().write(CSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));

                } else {
                    c.getSession().write(CSPacket.sendCSFail(0));
                }
            } else {
                c.getSession().write(CSPacket.sendCSFail(0));
            }
        } else if (action == 4 /*|| action == 34*/) { //gift, package
            slea.readMapleAsciiString(); // pic
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            if (action == 4) {
                slea.skip(1);
            }
            String partnerName = slea.readMapleAsciiString();
            String msg = slea.readMapleAsciiString();
            if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || msg.length() > 73 || msg.length() < 1) { //dont want packet editors gifting random stuff =P
                c.getSession().write(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            Triple<Integer, Integer, Integer> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
            if (info == null || info.getLeft().intValue() <= 0 || info.getLeft().intValue() == c.getPlayer().getId() || info.getMid().intValue() == c.getAccID()) {
                c.getSession().write(CSPacket.sendCSFail(0xA2)); //9E v75
                doCSPackets(c);
                return;
            } else if (!item.genderEquals(info.getRight().intValue())) {
                c.getSession().write(CSPacket.sendCSFail(0xA3));
                doCSPackets(c);
                return;
            } else {
                //get the packets for that
                c.getPlayer().getCashInventory().gift(info.getLeft().intValue(), c.getPlayer().getName(), msg, item.getSN(), MapleInventoryIdentifier.getInstance());
                c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
                c.getSession().write(CSPacket.sendGift(item.getPrice(), item.getId(), item.getCount(), partnerName, action == 34));
            }
        } else if (action == 5) { //Increase Inventory Slot
            slea.skip(1);
            final int toCharge = slea.readInt();
            final boolean coupon = slea.readByte() > 0;
            if (coupon) {
                final MapleInventoryType type = getInventoryType(slea.readInt());
                if (chr.getCSPoints(toCharge) >= 6000 && chr.getInventory(type).getSlotLimit() < 89) {
                    chr.modifyCSPoints(toCharge, -6000, false);
                    chr.getInventory(type).addSlot((byte) 8);
                    chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(type).getSlotLimit());
                    c.getSession().write(CField.getCharInfo(chr));
                } else {
                    c.getSession().write(CSPacket.sendCSFail(0xA4));
                }
            } else {
                slea.skip(1); //176
                byte bType = slea.readByte();
                final MapleInventoryType type = MapleInventoryType.getByType(bType);
                if (chr.getCSPoints(toCharge) >= 2500 && chr.getInventory(type).getSlotLimit() < 93) {
                    chr.modifyCSPoints(toCharge, -2500, false);
                    chr.getInventory(type).addSlot((byte) 4);
                    String slotName = (type == MapleInventoryType.EQUIP ? "Equip" : type == MapleInventoryType.ETC ? "Etc" : type == MapleInventoryType.SETUP ? "Setup" : type == MapleInventoryType.USE ? "Use" : "Undefined " + type);
                    chr.dropMessage(1,  slotName + " slots have been increased to " + chr.getInventory(type).getSlotLimit());
                } else {
                    c.getSession().write(CSPacket.sendCSFail(0xA4));
                }
            }
        } else if (action == 6) { // Increase inv
            slea.skip(1);
            final int toCharge = slea.readInt();
            final boolean coupon = slea.readByte() > 0;
            if (coupon) {
                final MapleInventoryType type = getInventoryType(slea.readInt());
                if (chr.getCSPoints(toCharge) >= 6000 && chr.getInventory(type).getSlotLimit() < 89) {
                    chr.modifyCSPoints(toCharge, -6000, false);
                    chr.getInventory(type).addSlot((byte) 8);
                    chr.dropMessage(1, "Slots have been increased to " + chr.getInventory(type).getSlotLimit());
                    c.getSession().write(CField.getCharInfo(chr));
                } else {
                    c.getSession().write(CSPacket.sendCSFail(0xA4));
                }
            } else {
                final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
                if (chr.getCSPoints(toCharge) >= 2500 && chr.getInventory(type).getSlotLimit() < 93) {
                    chr.modifyCSPoints(toCharge, -2500, false);
                    chr.getInventory(type).addSlot((byte) 4);
                    chr.dropMessage(1, "Slots have been increased to " + chr.getInventory(type).getSlotLimit());
                    c.getSession().write(CField.getCharInfo(chr));
                } else {
                    c.getSession().write(CSPacket.sendCSFail(0xA4));
                }
            }

        } else if (action == 7) { // Increase slot space
            slea.skip(1);
            final int toCharge = slea.readInt();
            final int coupon = slea.readByte() > 0 ? 2 : 1;
            if (chr.getCSPoints(toCharge) >= 4000 * coupon && chr.getStorage().getSlots() < (49 - (4 * coupon))) {
                chr.modifyCSPoints(toCharge, -4000 * coupon, false);
                chr.getStorage().increaseSlots((byte) (4 * coupon));
                chr.getStorage().saveToDB();
                chr.dropMessage(1, "Storage slots increased to: " + chr.getStorage().getSlots());
                c.getSession().write(CField.getCharInfo(chr));
            } else {
                c.getSession().write(CSPacket.sendCSFail(0xA4));
            }
        } else if (action == 8) { //...10 = pendant slot expansion
            slea.skip(1);
            final int toCharge = slea.readInt();
            CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            int slots = c.getCharacterSlots();
            if (item == null || c.getPlayer().getCSPoints(toCharge) < item.getPrice() || slots > 15 || item.getId() != 5430000) {
                c.getSession().write(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            if (c.gainCharacterSlot()) {
                c.getPlayer().modifyCSPoints(toCharge, -item.getPrice(), false);
                chr.dropMessage(1, "Character slots increased to: " + (slots + 1));
            } else {
                c.getSession().write(CSPacket.sendCSFail(0));
            }
        } else if (action == 10) { //...10 = pendant slot expansion
            //Data: 00 01 00 00 00 DC FE FD 02
            slea.readByte(); //Action is short?
            slea.readInt(); //always 1 - No Idea
            final int sn = slea.readInt();
            CashItemInfo item = CashItemFactory.getInstance().getItem(sn);
            if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || item.getId() / 10000 != 555) {
                c.getSession().write(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            MapleQuestStatus marr = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
            if (marr != null && marr.getCustomData() != null && Long.parseLong(marr.getCustomData()) >= System.currentTimeMillis()) {
                c.getSession().write(CSPacket.sendCSFail(0));
            } else {
                c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT)).setCustomData(String.valueOf(System.currentTimeMillis() + ((long) item.getPeriod() * 24 * 60 * 60000)));
                c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
                chr.dropMessage(1, "Additional pendant slot gained.");
            }
        } else if (action == 15) { //get item from csinventory
            //uniqueid, 00 01 01 00, type->position(short)
            Item item = c.getPlayer().getCashInventory().findByCashId((int) slea.readLong());
//            Item item = MapleItemInformationProvider.getInstance().getEquipById(item);
            if (item != null && item.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                Item item_ = item.copy();
                short pos = MapleInventoryManipulator.addbyItem(c, item_, true);
                if (pos >= 0) {
                    if (item_.getPet() != null) {
                        item_.getPet().setInventoryPosition(pos);
                        c.getPlayer().addPet(item_.getPet());
                    }
                    c.getPlayer().getCashInventory().removeFromInventory(item);
                    c.getSession().write(CSPacket.confirmFromCSInventory(item_, pos));
                } else {
                    c.getSession().write(CSPacket.sendCSFail(0xB1));
                }
            } else {
                c.getSession().write(CSPacket.sendCSFail(0xB1));
            }
        } else if (action == 16) { //put item in cash inventory  
//            System.out.println("action 15");
            int uniqueid = (int) slea.readLong();
            MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
            Item item = c.getPlayer().getInventory(type).findByUniqueId(uniqueid);
            if (item != null && item.getQuantity() > 0 && item.getUniqueId() > 0 && c.getPlayer().getCashInventory().getItemsSize() < 100) {
                Item item_ = item.copy();
                MapleInventoryManipulator.removeFromSlot(c, type, item.getPosition(), item.getQuantity(), false);
                if (item_.getPet() != null) {
                    c.getPlayer().removePetCS(item_.getPet());
                }
                item_.setPosition((byte) 0);
                c.getPlayer().getCashInventory().addToInventory(item_);
                c.getSession().write(CSPacket.showBoughtCSItem(item, item.getUniqueId(), c.getAccID()));
                //warning: this d/cs
//                c.getSession().write(CSPacket.confirmToCSInventory(item, c.getAccID(), itemz.getSN()));
            } else {
                c.getSession().write(CSPacket.sendCSFail(0xB1));
            }
        } else if ((action == 33) || (action == 39)) {
          //  slea.skip(5);
            slea.readMapleAsciiString();
            int toCharge = slea.readInt();
            CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
     //       slea.skip(1);
            String partnerName = slea.readMapleAsciiString();
            String msg = slea.readMapleAsciiString();
            if ((item == null) || (!GameConstants.isEffectRing(item.getId())) || (c.getPlayer().getCSPoints(toCharge) < item.getPrice()) || (msg.length() > 73) || (msg.length() < 1)) {
                 c.getSession().write(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            if (!item.genderEquals(c.getPlayer().getGender())) {
                // c.getSession().write(MTSCSPacket.sendCSFail(166));
                doCSPackets(c);
                return;
            }
            if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                // c.getSession().write(MTSCSPacket.sendCSFail(177));
                doCSPackets(c);
                return;
            }

            Triple info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
            if ((info == null) || (((Integer) info.getLeft()).intValue() <= 0) || (((Integer) info.getLeft()).intValue() == c.getPlayer().getId())) {
                //   c.getSession().write(MTSCSPacket.sendCSFail(180));
                doCSPackets(c);
                return;
            }
            if (((Integer) info.getMid()).intValue() == c.getAccID()) {
                //    c.getSession().write(MTSCSPacket.sendCSFail(163));
                doCSPackets(c);
                return;
            }
            if ((((Integer) info.getRight()).intValue() == c.getPlayer().getGender()) && (action == 30)) {
                //  c.getSession().write(MTSCSPacket.sendCSFail(161));
                doCSPackets(c);
                return;
            }

            int err = MapleRing.createRing(item.getId(), c.getPlayer(), partnerName, msg, ((Integer) info.getLeft()).intValue(), item.getSN());

            if (err != 1) {
                //  c.getSession().write(MTSCSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
         //   c.getPlayer().modifyCSPoints(toCharge, -amount, false);
            c.getPlayer().dropMessage(1, "Purchase successful.");
            /*   Item itemz = chr.getCashInventory().toItem(item);
             if ((itemz != null) && (itemz.getUniqueId() > 0) && (itemz.getItemId() == item.getId()) && (itemz.getQuantity() == item.getCount())) {
             chr.getCashInventory().addToInventory(itemz);
             c.getSession().write(MTSCSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
             } else {
             c.getSession().write(MTSCSPacket.sendCSFail(0));
             }*/
        } else if (action == 36) {//Packages
            slea.skip(1);
            int unk = slea.readInt();//is1
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            final int toCharge = slea.readInt();
            List<Integer> ccc = null;
            if (item != null) {
                ccc = CashItemFactory.getInstance().getPackageItems(item.getId());
            }
            if (item == null || ccc == null || c.getPlayer().getCSPoints(toCharge) < item.getPrice()) {
                c.getSession().write(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            } else if (!item.genderEquals(c.getPlayer().getGender())) {
                c.getSession().write(CSPacket.sendCSFail(0xA6));
                doCSPackets(c);
                return;
            } else if (c.getPlayer().getCashInventory().getItemsSize() >= (100 - ccc.size())) {
                c.getSession().write(CSPacket.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }

            Map<Integer, Item> ccz = new HashMap<>();
            for (int i : ccc) {
                final CashItemInfo cii = CashItemFactory.getInstance().getSimpleItem(i);
                if (cii == null) {
                    continue;
                }
                Item itemz = c.getPlayer().getCashInventory().toItem(cii);
                if (itemz == null || itemz.getUniqueId() <= 0) {
                    continue;
                }
                for (int iz : GameConstants.cashBlock) {
                    if (itemz.getItemId() == iz) {
                    }
                }
                ccz.put(i, itemz);
                c.getPlayer().getCashInventory().addToInventory(itemz);
            }
            chr.modifyCSPoints(toCharge, -item.getPrice(), false);
            c.getSession().write(CSPacket.showBoughtCSPackage(ccz, c.getAccID()));

        } else if (action == 35 || action == 99) { //99 buy with mesos
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            if (item == null || !MapleItemInformationProvider.getInstance().isQuestItem(item.getId())) {
                c.getSession().write(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            } else if (c.getPlayer().getMeso() < item.getPrice()) {
                c.getSession().write(CSPacket.sendCSFail(0xB8));
                doCSPackets(c);
                return;
            } else if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getId())).getNextFreeSlot() < 0) {
                c.getSession().write(CSPacket.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }
            byte pos = MapleInventoryManipulator.addId(c, item.getId(), (short) item.getCount(), null, "Cash shop: quest item" + " on " + FileoutputUtil.CurrentReadable_Date());
            if (pos < 0) {
                c.getSession().write(CSPacket.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }
            chr.gainMeso(-item.getPrice(), false);
            c.getSession().write(CSPacket.showBoughtCSQuestItem(item.getPrice(), (short) item.getCount(), pos, item.getId()));
        } else if (action == 48) {
            c.getSession().write(CSPacket.updatePurchaseRecord());
        } else if (action == 91) { // Open random box.
            final int uniqueid = (int) slea.readLong();

            //c.getSession().write(CSPacket.sendRandomBox(uniqueid, new Item(1302000, (short) 1, (short) 1, (short) 0, 10), (short) 0));
            //} else if (action == 99) { //buy with mesos
            //    int sn = slea.readInt();
            //    int price = slea.readInt();
        } else {
            System.out.println("New Action: " + action + " Remaining: " + slea.toString());
            c.getSession().write(CSPacket.sendCSFail(0));
        }
        doCSPackets(c);
    }

    public static void SwitchCategory(final LittleEndianAccessor slea, final MapleClient c) {
        int Scategory = slea.readByte();
//        System.out.println("Scategory " + Scategory);
        if (Scategory == 103) {
            slea.skip(1);
            int itemSn = slea.readInt();
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO `wishlist` VALUES (?, ?)")) {
                ps.setInt(1, c.getPlayer().getId());
                ps.setInt(2, itemSn);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException ex) {
                System.out.println("error");
            }
            c.getSession().write(CSPacket.addFavorite(false, itemSn));
        } else if (Scategory == 105) {
            int item = slea.readInt();
            try {
                Connection con = DatabaseConnection.getConnection();
                try (PreparedStatement ps = con.prepareStatement("UPDATE cashshop_items SET likes = likes+" + 1 + " WHERE sn = ?")) {
                    ps.setInt(1, item);
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
            }
            c.getSession().write(CSPacket.Like(item));
        } else if (Scategory == 107) {//search results
            Set<Integer> searchList = new HashSet<Integer>();
            int resultSize = slea.readInt();
            for(int i = 0; i < resultSize; i++){
                searchList.add(slea.readInt());
            }
            c.getSession().write(CSPacket.showSearchResults(searchList));
        } else if (Scategory == 109) {
            c.getSession().write(CSPacket.Favorite(c.getPlayer()));
        } else if (Scategory == 112) {//click on special item TODO
            //int C8 - C9 - CA
        } else if (Scategory == 113) {//buy from cart inventory TODO
            //byte buy = 1 or gift = 0
            //byte amount
            //for each SN
        } else {
            int category = slea.readInt();
            if (category == 4000000) {
                c.getSession().write(CSPacket.CS_Top_Items());
                c.getSession().write(CSPacket.CS_Picture_Item());
            } else if (category == 1060100) {
                c.getSession().write(CSPacket.showNXChar(category));
                c.getSession().write(CSPacket.changeCategory(category));
            } else {
//                System.err.println(category);
                c.getSession().write(CSPacket.changeCategory(category));
            }
        }
    }

    private static MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 50200093:
                return MapleInventoryType.EQUIP;
            case 50200094:
                return MapleInventoryType.USE;
            case 50200197:
                return MapleInventoryType.SETUP;
            case 50200095:
                return MapleInventoryType.ETC;
            default:
                return MapleInventoryType.UNDEFINED;
        }
    }

    public static void doCSPackets(MapleClient c) {
        //c.getSession().write(CSPacket.getCSInventory(c));
        //c.getSession().write(CSPacket.doCSMagic());
        //c.getSession().write(CSPacket.getCSGifts(c));
        //c.getSession().write(CWvsContext.BuddylistPacket.updateBuddylist(c.getPlayer().getBuddylist().getBuddies()));
        //c.getSession().write(CSPacket.showNXMapleTokens(c.getPlayer()));
        //c.getSession().write(CSPacket.sendWishList(c.getPlayer(), false));
        c.getSession().write(CSPacket.showNXMapleTokens(c.getPlayer()));
        c.getSession().write(CSPacket.enableCSUse());
        c.getPlayer().getCashInventory().checkExpire(c);
    }
}
