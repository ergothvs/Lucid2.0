/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.channel.handler;

import java.util.Arrays;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.world.World;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleTrade;
import server.maps.FieldLimitType;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.stores.HiredMerchant;
import server.stores.IMaplePlayerShop;
import server.stores.MapleMiniGame;
import server.stores.MaplePlayerShop;
import server.stores.MaplePlayerShopItem;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.PlayerShopPacket;

public class PlayerInteractionHandler {

    public enum Interaction {

//        SET_ITEMS(0),
//        UPDATE_MESO(4),
//        SET_MESO(5),
//        SET_MESO_1(6),//was 4
//        SET_MESO_2(7),//was 4
//
//        CREATE(16),
//        INVITE_TRADE(21),//was11
//        DENY_TRADE(12),
//        VISIT(19),//was 9
        HIRED_MERCHANT_MAINTENANCE(31),//was 21//25
//        //        CHAT_RECV(14),
//        CHAT(24),
//        EXIT(28),//was 18
//        OPEN(17),
//        CONFIRM_TRADE(8),//was 2
//        START_TRADE(20),
//        PLAYER_SHOP_ADD_ITEM(40),
//        BUY_ITEM_PLAYER_SHOP(22),
//        ADD_ITEM(23),
//        BUY_ITEM_STORE(27),//was 24
//        BUY_ITEM_HIREDMERCHANT(26),
//        REMOVE_ITEM(30),
//        MAINTANCE_OFF(31), //This is misspelled...
//        MAINTANCE_ORGANISE(32),
//        CLOSE_MERCHANT(52), //was33
//        TAKE_MESOS(35),
//        ADMIN_STORE_NAMECHANGE(37),
//        VIEW_MERCHANT_VISITOR(38),
//        VIEW_MERCHANT_BLACKLIST(39),
//        MERCHANT_BLACKLIST_ADD(40),
//        MERCHANT_BLACKLIST_REMOVE(41),
//        REQUEST_TIE(51),
//        ANSWER_TIE(53),//was52
//        GIVE_UP(53),
//        REQUEST_REDO(55),
//        ANSWER_REDO(56),
//        EXIT_AFTER_GAME(57),
//        CANCEL_EXIT(58),
//        READY(59),
//        UN_READY(60),
//        EXPEL(61),
//        START(62),
//        SKIP(64),
//        MOVE_OMOK(65),
//        SELECT_CARD(68);
        
        SET_ITEMS(0),
        SET_ITEMS2(1),
        SET_ITEMS3(2),
        SET_ITEMS4(3),
        UPDATE_MESO(4),
        SET_MESO2(5),
        SET_MESO3(6),
        SET_MESO4(7),
        CONFIRM_TRADE(8),
        CONFIRM_TRADE2(9),
        CONFIRM_TRADE_MESO(10),
        CONFIRM_TRADE_MESO2(11),
        CREATE(16),
        VISIT(19),
        INVITE_TRADE(21),
        DENY_TRADE(22),
        CHAT(24),
        EXIT(28),
        OPEN1(25),
        OPEN2(26),
        OPEN3(80),
        ADD_ITEM1(33),
        ADD_ITEM2(34),
        ADD_ITEM3(35),
        ADD_ITEM4(36),
        BUY_ITEM_HIREDMERCHANT(37),
        BUY_ITEM_STORE1(38),
        BUY_ITEM_STORE2(39),
        BUY_ITEM_STORE3(40),
        MAINTANCE_OFF(50),//41
        REMOVE_ITEM(49), //+10 
        //MERCHANT_EXIT(50), //+10 v192
        MAINTANCE_ORGANISE(51), //+10 v192
        CLOSE_MERCHANT(52), //+10 v192
        MESO_BACK(53), //+10 v192
        VIEW_MERCHANT_VISITOR(57),//55
        VIEW_MERCHANT_BLACKLIST(59),//56
        MERCHANT_BLACKLIST_ADD(60),//57
        MERCHANT_BLACKLIST_REMOVE(61), //+10 v192 //58
        ADMIN_STORE_NAMECHANGE(62),//59
        START_ROCK_PAPER_SCISSORS1(96),
        START_ROCK_PAPER_SCISSORS2(97),
        START_ROCK_PAPER_SCISSORS3(98),
        INVITE_ROCK_PAPER_SCISSORS(112),
        FINISH_ROCK_PAPER_SCISSORS(113),
        //            
        //            REQUEST_TIE(0x30),
        //            ANSWER_TIE(0x31),
        GIVE_UP(114),
        //            REQUEST_REDO(0x34),
        //            ANSWER_REDO(0x35),
        //            EXIT_AFTER_GAME(0x36),
        //            CANCEL_EXIT(0x37),
        //            READY(0x38),
        //            UN_READY(0x39),
        //            EXPEL(0x3A),
        START(125);
        public int action;

        private Interaction(int action) {
            this.action = action;
        }

        public static Interaction getByAction(int i) {
            for (Interaction s : Interaction.values()) {
                if (s.action == i) {
                    return s;
                }
            }
            return null;
        }
    }

    public static final void PlayerInteraction(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) throws Exception {
//        System.err.println(slea.toString());
        final Interaction action = Interaction.getByAction(slea.readByte());
        if (chr == null || action == null) {
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
//System.err.println(action);
        switch (action) { // Mode
            case CREATE: {
                if (chr.getPlayerShop() != null || c.getChannelServer().isShutdown() || chr.hasBlockedInventory()) {
                    System.err.println("amq");
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
                final byte createType = slea.readByte();
                if (createType == 4) { // trade//was 3
                    MapleTrade.startTrade(chr);
                } else if (createType == 1 || createType == 2 || createType == 3 || createType == 5 || createType == 6) { // shop
                    //if (createType == 4 && !chr.isIntern()) { //not hired merch... blocked playershop
                    //    c.getSession().write(CWvsContext.enableActions());
                    //    return;
                    //}
                    if (!chr.getMap().getMapObjectsInRange(chr.getTruePosition(), 20000, Arrays.asList(MapleMapObjectType.SHOP, MapleMapObjectType.HIRED_MERCHANT)).isEmpty() || !chr.getMap().getPortalsInRange(chr.getTruePosition(), 20000).isEmpty()) {
                        chr.dropMessage(1, "You may not establish a store here.");
                        c.getSession().write(CWvsContext.enableActions());
                        return;
                    } else if (createType == 1 || createType == 2) {
                        if (FieldLimitType.Minigames.check(chr.getMap().getFieldLimit()) || chr.getMap().allowPersonalShop()) {
                            chr.dropMessage(1, "You may not use minigames here.");
                            c.getSession().write(CWvsContext.enableActions());
                            return;
                        }
                    }
                    final String desc = slea.readMapleAsciiString();
                    String pass = "";
                    if (slea.readByte() > 0) {
                        pass = slea.readMapleAsciiString();
                    }
                    if (createType == 1 || createType == 2) {
                        final int piece = slea.readByte();
                        final int itemId = createType == 1 ? (4080000 + piece) : 4080100;
                        if (!chr.haveItem(itemId) || (c.getPlayer().getMapId() >= 910000001 && c.getPlayer().getMapId() <= 910000022)) {
                            return;
                        }
                        MapleMiniGame game = new MapleMiniGame(chr, itemId, desc, pass, createType); //itemid
                        game.setPieceType(piece);
                        chr.setPlayerShop(game);
                        game.setAvailable(true);
                        game.setOpen(true);
                        game.send(c);
                        chr.getMap().addMapObject(game);
                        game.update();
                    } else if (chr.getMap().allowPersonalShop()) {
                        Item shop = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) slea.readShort());
                        if (shop == null || shop.getQuantity() <= 0 || shop.getItemId() != slea.readInt() || c.getPlayer().getMapId() < 910000001 || c.getPlayer().getMapId() > 910000022) {
                            return;
                        }
                        if (createType == 3) {
                            //MaplePlayerShop mps = new MaplePlayerShop(chr, shop.getItemId(), desc);
                            //chr.setPlayerShop(mps);
                            //chr.getMap().addMapObject(mps);
                            //c.getSession().write(PlayerShopPacket.getPlayerStore(chr, true));
                        } else if (HiredMerchantHandler.UseHiredMerchant(chr.getClient(), false)) {
                            final HiredMerchant merch = new HiredMerchant(chr, shop.getItemId(), desc);
                            chr.setPlayerShop(merch);
                            chr.getMap().addMapObject(merch);
                            c.getSession().write(PlayerShopPacket.getHiredMerch(chr, merch, true));
                        }
                    }
                }
                break;
            }
            case INVITE_TRADE: {
                if (chr.getMap() == null) {
                    return;
                }
                MapleCharacter chrr = chr.getMap().getCharacterById(slea.readInt());
                if (chrr == null || c.getChannelServer().isShutdown() || chrr.hasBlockedInventory()) {
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
                MapleTrade.inviteTrade(chr, chrr);
                break;
            }
            case DENY_TRADE: {
                MapleTrade.declineTrade(chr);
                break;
            }
            case VISIT: {
                if (c.getChannelServer().isShutdown()) {
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
                if (chr.getTrade() != null && chr.getTrade().getPartner() != null && !chr.getTrade().inTrade()) {
                    MapleTrade.visitTrade(chr, chr.getTrade().getPartner().getChr());
                } else if (chr.getMap() != null && chr.getTrade() == null) {
                    final int obid = slea.readInt();
                    MapleMapObject ob = chr.getMap().getMapObject(obid, MapleMapObjectType.HIRED_MERCHANT);
                    if (ob == null) {
                        ob = chr.getMap().getMapObject(obid, MapleMapObjectType.SHOP);
                    }

                    if (ob instanceof IMaplePlayerShop && chr.getPlayerShop() == null) {
                        final IMaplePlayerShop ips = (IMaplePlayerShop) ob;

                        if (ob instanceof HiredMerchant) {
                            final HiredMerchant merchant = (HiredMerchant) ips;
                            /*if (merchant.isOwner(chr) && merchant.isOpen() && merchant.isAvailable()) {
                             merchant.setOpen(false);
                             merchant.removeAllVisitors((byte) 16, (byte) 0);
                             chr.setPlayerShop(ips);
                             c.getSession().write(PlayerShopPacket.getHiredMerch(chr, merchant, false));
                             } else {*/
                            if (!merchant.isOpen() || !merchant.isAvailable()) {
                                chr.dropMessage(1, "This shop is in maintenance, please come by later.");
                            } else {
                                if (ips.getFreeSlot() == -1) {
                                    chr.dropMessage(1, "This shop has reached it's maximum capacity, please come by later.");
                                } else if (merchant.isInBlackList(chr.getName())) {
                                    chr.dropMessage(1, "You have been banned from this store.");
                                } else {
                                    chr.setPlayerShop(ips);
                                    merchant.addVisitor(chr);
                                    c.getSession().write(PlayerShopPacket.getHiredMerch(chr, merchant, false));
                                }
                            }
                            //}
                        } else {
                            if (ips instanceof MaplePlayerShop && ((MaplePlayerShop) ips).isBanned(chr.getName())) {
                                chr.dropMessage(1, "You have been banned from this store.");
                            } else {
                                if (ips.getFreeSlot() < 0 || ips.getVisitorSlot(chr) > -1 || !ips.isOpen() || !ips.isAvailable()) {
                                    c.getSession().write(PlayerShopPacket.getMiniGameFull());
                                } else {
                                    if (slea.available() > 0 && slea.readByte() > 0) { //a password has been entered
                                        String pass = slea.readMapleAsciiString();
                                        if (!pass.equals(ips.getPassword())) {
                                            c.getPlayer().dropMessage(1, "The password you entered is incorrect.");
                                            return;
                                        }
                                    } else if (ips.getPassword().length() > 0) {
                                        c.getPlayer().dropMessage(1, "The password you entered is incorrect.");
                                        return;
                                    }
                                    chr.setPlayerShop(ips);
                                    ips.addVisitor(chr);
                                    if (ips instanceof MapleMiniGame) {
                                        ((MapleMiniGame) ips).send(c);
                                    } else {
                                        c.getSession().write(PlayerShopPacket.getPlayerStore(chr, false));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case HIRED_MERCHANT_MAINTENANCE: {
                if (c.getChannelServer().isShutdown() || chr.getMap() == null || chr.getTrade() != null) {
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
                slea.skip(1); // 9?
                byte type = slea.readByte(); // 5?
                if (type != 5) {
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
                final String password = slea.readMapleAsciiString();
                //if (!c.CheckSecondPassword(password) || password.length() < 6 || password.length() > 16) {
                //	chr.dropMessage(5, "Please enter a valid PIC.");
                //	c.getSession().write(CWvsContext.enableActions());
                //	return;
                //}				
                final int obid = slea.readInt();
                MapleMapObject ob = chr.getMap().getMapObject(obid, MapleMapObjectType.HIRED_MERCHANT);
                if (ob == null || chr.getPlayerShop() != null) {
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
                if (ob instanceof IMaplePlayerShop && ob instanceof HiredMerchant) {
                    final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                    final HiredMerchant merchant = (HiredMerchant) ips;
                    if (merchant.isOwner(chr) && merchant.isAvailable()) {
                        merchant.setOpen(false);
                        merchant.removeAllVisitors((byte) 16, (byte) 0);
                        chr.setPlayerShop(ips);
                        c.getSession().write(PlayerShopPacket.getHiredMerch(chr, merchant, false));
                    } else {
                        c.getSession().write(CWvsContext.enableActions());
                    }
                }
                break;
            }
            case CHAT: {
            	slea.skip(4); // update tick
                final String message = slea.readMapleAsciiString();
                if (chr.getTrade() != null) {
                    chr.getTrade().chat(message);
                } else if (chr.getPlayerShop() != null) {
                    final IMaplePlayerShop ips = chr.getPlayerShop();
                    ips.broadcastToVisitors(PlayerShopPacket.shopChat(chr.getName() + " : " + message, ips.getVisitorSlot(chr)));
                    if (chr.getClient().isMonitored()) { //Broadcast info even if it was a command.
                        World.Broadcast.broadcastGMMessage(CWvsContext.broadcastMsg(6, chr.getName() + " said in " + ips.getOwnerName() + " shop : " + message));
                    }
                }
                break;
            }
            case EXIT: {
                if (chr.getTrade() != null) {
                    MapleTrade.cancelTrade(chr.getTrade(), chr.getClient(), chr);
                } else {
                    final IMaplePlayerShop ips = chr.getPlayerShop();
                    if (ips == null) { //should be null anyway for owners of hired merchants (maintenance_off)
                        return;
                    }
                    if (ips.isOwner(chr) && ips.getShopType() != 1) {
                        ips.closeShop(false, ips.isAvailable()); //how to return the items?
                    } else {
                        ips.removeVisitor(chr);
                    }
                    chr.setPlayerShop(null);
                }
                break;
            }
            case OPEN3:
            case OPEN2:
            case OPEN1: {
                // c.getPlayer().haveItem(mode, 1, false, true)
                final IMaplePlayerShop shop = chr.getPlayerShop();
                if (shop != null && shop.isOwner(chr) && shop.getShopType() < 3 && !shop.isAvailable()) {
                    if (chr.getMap().allowPersonalShop()) {
                        if (c.getChannelServer().isShutdown()) {
                            chr.dropMessage(1, "The server is about to shut down.");
                            c.getSession().write(CWvsContext.enableActions());
                            shop.closeShop(shop.getShopType() == 1, false);
                            return;
                        }

                        if (shop.getShopType() == 1 && HiredMerchantHandler.UseHiredMerchant(chr.getClient(), false)) {
                            final HiredMerchant merchant = (HiredMerchant) shop;
                            merchant.setStoreid(c.getChannelServer().addMerchant(merchant));
                            merchant.setOpen(true);
                            merchant.setAvailable(true);
                            chr.getMap().broadcastMessage(PlayerShopPacket.spawnHiredMerchant(merchant));
                            chr.setPlayerShop(null);

                        } else if (shop.getShopType() == 2) {
                            shop.setOpen(true);
                            shop.setAvailable(true);
                            shop.update();
                        }
                    } else {
                        c.getSession().close();
                    }
                }

                break;
            }
            case SET_ITEMS4: 
            case SET_ITEMS3: 
            case SET_ITEMS2: 
            case SET_ITEMS: {
//            case SET_ITEMS: {
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final MapleInventoryType ivType = MapleInventoryType.getByType(slea.readByte());
                final Item item = chr.getInventory(ivType).getItem((byte) slea.readShort());
                final short quantity = slea.readShort();
                final byte targetSlot = slea.readByte();

                if (chr.getTrade() != null && item != null) {
                    if ((quantity <= item.getQuantity() && quantity >= 0) || GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                        chr.getTrade().setItems(c, item, targetSlot, quantity);
                    }
                }
                break;
            }
//            case UPDATE_MESO:
//            case SET_MESO:
//            case SET_MESO_1:
//            case SET_MESO_2: {
            case SET_MESO4:
            case SET_MESO3:
            case SET_MESO2:
            case UPDATE_MESO: {
                final MapleTrade trade = chr.getTrade();
                if (trade != null) {
                    trade.setMeso((int) slea.readLong());
                }
                break;
            }
//            case PLAYER_SHOP_ADD_ITEM:
//            case ADD_ITEM: {
            case ADD_ITEM4:
            case ADD_ITEM3:
            case ADD_ITEM2:
            case ADD_ITEM1: {
                final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
                final byte slot = (byte) slea.readShort();
                final short bundles = slea.readShort(); // How many in a bundle
                final short perBundle = slea.readShort(); // Price per bundle
                final int price = slea.readInt();

                if (price <= 0 || bundles <= 0 || perBundle <= 0) {
                    return;
                }
                final IMaplePlayerShop shop = chr.getPlayerShop();

                if (shop == null || !shop.isOwner(chr) || shop instanceof MapleMiniGame) {
                    return;
                }
                final Item ivItem = chr.getInventory(type).getItem(slot);
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (ivItem != null) {
                    long check = bundles * perBundle;
                    if (check > 32767 || check <= 0) { //This is the better way to check.
                        return;
                    }
                    final short bundles_perbundle = (short) (bundles * perBundle);
//                    if (bundles_perbundle < 0) { // int_16 overflow
//                        return;
//                    }
                    if (ivItem.getQuantity() >= bundles_perbundle) {
                        final short flag = ivItem.getFlag();
                        if (ItemFlag.UNTRADABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                            c.getSession().write(CWvsContext.enableActions());
                            return;
                        }
                        if (ii.isDropRestricted(ivItem.getItemId()) || ii.isAccountShared(ivItem.getItemId())) {
                            if (!(ItemFlag.KARMA_EQ.check(flag) || ItemFlag.KARMA_USE.check(flag))) {
                                c.getSession().write(CWvsContext.enableActions());
                                return;
                            }
                        }
                        if (bundles_perbundle >= 50 && ivItem.getItemId() == 2340000) {
                            c.setMonitored(true); //hack check
                        }
                        if (GameConstants.getLowestPrice(ivItem.getItemId()) > price) {
                            c.getPlayer().dropMessage(1, "The lowest you can sell this for is " + GameConstants.getLowestPrice(ivItem.getItemId()));
                            c.getSession().write(CWvsContext.enableActions());
                            return;
                        }
                        if (GameConstants.isThrowingStar(ivItem.getItemId()) || GameConstants.isBullet(ivItem.getItemId())) {
                            // Ignore the bundles
                            MapleInventoryManipulator.removeFromSlot(c, type, slot, ivItem.getQuantity(), true);

                            final Item sellItem = ivItem.copy();
                            shop.addItem(new MaplePlayerShopItem(sellItem, (short) 1, price));
                        } else {
                            MapleInventoryManipulator.removeFromSlot(c, type, slot, bundles_perbundle, true);

                            final Item sellItem = ivItem.copy();
                            sellItem.setQuantity(perBundle);
                            shop.addItem(new MaplePlayerShopItem(sellItem, bundles, price));
                        }
                        c.getSession().write(PlayerShopPacket.shopItemUpdate(shop));
                    }
                }
                break;
            }
//            case CONFIRM_TRADE:
//            case BUY_ITEM_PLAYER_SHOP:
//            case BUY_ITEM_STORE:
//            case BUY_ITEM_HIREDMERCHANT: { // Buy and Merchant buy
            case CONFIRM_TRADE:
            case CONFIRM_TRADE2:
            case CONFIRM_TRADE_MESO:
            case CONFIRM_TRADE_MESO2:
            case BUY_ITEM_STORE3:
            case BUY_ITEM_STORE2:
            case BUY_ITEM_STORE1:
            case BUY_ITEM_HIREDMERCHANT: { // Buy and Merchant buy
                if (chr.getTrade() != null) {
                    MapleTrade.completeTrade(chr);
                    break;
                }
                final int item = slea.readByte();
                final short quantity = slea.readShort();
                //slea.skip(4);
                final IMaplePlayerShop shop = chr.getPlayerShop();

                if (shop == null || shop.isOwner(chr) || shop instanceof MapleMiniGame || item >= shop.getItems().size()) {
                    return;
                }
                final MaplePlayerShopItem tobuy = shop.getItems().get(item);
                if (tobuy == null) {
                    return;
                }
                long check = tobuy.bundles * quantity;
                long check2 = tobuy.price * quantity;
                long check3 = tobuy.item.getQuantity() * quantity;
                if (check <= 0 || check2 > 9999999999L || check2 <= 0 || check3 > 32767 || check3 < 0) { //This is the better way to check.
                    return;
                }
                if (tobuy.bundles < quantity || (tobuy.bundles % quantity != 0 && GameConstants.isEquip(tobuy.item.getItemId())) // Buying
                        || chr.getMeso() - (check2) < 0 || chr.getMeso() - (check2) > 9999999999L || shop.getMeso() + (check2) < 0 || shop.getMeso() + (check2) > 9999999999L) {
                    return;
                }
                if (quantity >= 50 && tobuy.item.getItemId() == 2340000) {
                    c.setMonitored(true); //hack check
                }
                shop.buy(c, item, quantity);
                shop.broadcastToVisitors(PlayerShopPacket.shopItemUpdate(shop));
                break;
            }
            case REMOVE_ITEM: {
                slea.skip(1); // ?
                int slot = slea.readShort(); //0
                final IMaplePlayerShop shop = chr.getPlayerShop();

                if (shop == null || !shop.isOwner(chr) || shop instanceof MapleMiniGame || shop.getItems().size() <= 0 || shop.getItems().size() <= slot || slot < 0) {
                    return;
                }
                final MaplePlayerShopItem item = shop.getItems().get(slot);

                if (item != null) {
                    if (item.bundles > 0) {
                        Item item_get = item.item.copy();
                        long check = item.bundles * item.item.getQuantity();
                        if (check < 0 || check > 32767) {
                            return;
                        }
                        item_get.setQuantity((short) check);
                        if (item_get.getQuantity() >= 50 && item.item.getItemId() == 2340000) {
                            c.setMonitored(true); //hack check
                        }
                        if (MapleInventoryManipulator.checkSpace(c, item_get.getItemId(), item_get.getQuantity(), item_get.getOwner())) {
                            MapleInventoryManipulator.addFromDrop(c, item_get, false);
                            item.bundles = 0;
                            shop.removeFromSlot(slot);
                        }
                    }
                }
                c.getSession().write(PlayerShopPacket.shopItemUpdate(shop));
                break;
            }
            case MAINTANCE_OFF: {
                final IMaplePlayerShop shop = chr.getPlayerShop();
                if (shop != null && shop instanceof HiredMerchant && shop.isOwner(chr) && shop.isAvailable()) {
                    shop.setOpen(true);
                    shop.removeAllVisitors(-1, -1);
                }
                break;
            }
            case MAINTANCE_ORGANISE: {
                final IMaplePlayerShop imps = chr.getPlayerShop();
                if (imps != null && imps.isOwner(chr) && !(imps instanceof MapleMiniGame)) {
                    for (int i = 0; i < imps.getItems().size(); i++) {
                        if (imps.getItems().get(i).bundles == 0) {
                            imps.getItems().remove(i);
                        }
                    }
                    if (chr.getMeso() + imps.getMeso() > 0) {
                        chr.gainMeso(imps.getMeso(), false);
                        imps.setMeso(0);
                    }
                    c.getSession().write(PlayerShopPacket.shopItemUpdate(imps));
                }
                break;
            }
            case CLOSE_MERCHANT: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr) && merchant.isAvailable()) {
                    merchant.removeVisitor(chr);
                    merchant.removeAllVisitors(-1, -1);
                    chr.setPlayerShop(null);
                    merchant.closeShop(true, true);
                    c.getSession().write(CWvsContext.broadcastMsg(1, "Please visit Fredrick for your items."));
                    c.getSession().write(CWvsContext.enableActions());
                }
                break;
            }
            case ADMIN_STORE_NAMECHANGE: { // Changing store name, only Admin
                // 01 00 00 00
                break;
            }
            case VIEW_MERCHANT_VISITOR: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    ((HiredMerchant) merchant).sendVisitor(c);
                }
                break;
            }
            case VIEW_MERCHANT_BLACKLIST: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    ((HiredMerchant) merchant).sendBlackList(c);
                }
                break;
            }
            case MERCHANT_BLACKLIST_ADD: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    ((HiredMerchant) merchant).addBlackList(slea.readMapleAsciiString());
                }
                break;
            }
            case MERCHANT_BLACKLIST_REMOVE: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    ((HiredMerchant) merchant).removeBlackList(slea.readMapleAsciiString());
                }
                break;
            }
            case GIVE_UP: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    game.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game, 0, game.getVisitorSlot(chr)));
                    game.nextLoser();
                    game.setOpen(true);
                    game.update();
                    game.checkExitAfterGame();
                }
                break;
            }
//            case EXPEL: {
//                final IMaplePlayerShop ips = chr.getPlayerShop();
//                if (ips != null && ips instanceof MapleMiniGame) {
//                    if (!((MapleMiniGame) ips).isOpen()) {
//                        break;
//                    }
//                    ips.removeAllVisitors(3, 1); //no msg
//                }
//                break;
//            }
//            case READY:
//            case UN_READY: {
//                final IMaplePlayerShop ips = chr.getPlayerShop();
//                if (ips != null && ips instanceof MapleMiniGame) {
//                    MapleMiniGame handling = (MapleMiniGame) ips;
//                    if (!handling.isOwner(chr) && handling.isOpen()) {
//                        handling.setReady(handling.getVisitorSlot(chr));
//                        handling.broadcastToVisitors(PlayerShopPacket.getMiniGameReady(handling.isReady(handling.getVisitorSlot(chr))));
//                    }
//                }
//                break;
//            }
            case START: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOwner(chr) && game.isOpen()) {
                        for (int i = 1; i < ips.getSize(); i++) {
                            if (!game.isReady(i)) {
                                return;
                            }
                        }
                        game.setGameType();
                        game.shuffleList();
                        if (game.getGameType() == 1) {
                            game.broadcastToVisitors(PlayerShopPacket.getMiniGameStart(game.getLoser()));
                        } else {
                            game.broadcastToVisitors(PlayerShopPacket.getMatchCardStart(game, game.getLoser()));
                        }
                        game.setOpen(false);
                        game.update();
                    }
                }
                break;
            }
//            case REQUEST_TIE: {
//                final IMaplePlayerShop ips = chr.getPlayerShop();
//                if (ips != null && ips instanceof MapleMiniGame) {
//                    MapleMiniGame handling = (MapleMiniGame) ips;
//                    if (handling.isOpen()) {
//                        break;
//                    }
//                    if (handling.isOwner(chr)) {
//                        handling.broadcastToVisitors(PlayerShopPacket.getMiniGameRequestTie(), false);
//                    } else {
//                        handling.getMCOwner().getClient().getSession().write(PlayerShopPacket.getMiniGameRequestTie());
//                    }
//                    handling.setRequestedTie(handling.getVisitorSlot(chr));
//                }
//                break;
//            }
//            case ANSWER_TIE: {
//                final IMaplePlayerShop ips = chr.getPlayerShop();
//                if (ips != null && ips instanceof MapleMiniGame) {
//                    MapleMiniGame handling = (MapleMiniGame) ips;
//                    if (handling.isOpen()) {
//                        break;
//                    }
//                    if (handling.getRequestedTie() > -1 && handling.getRequestedTie() != handling.getVisitorSlot(chr)) {
//                        if (slea.readByte() > 0) {
//                            handling.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(handling, 1, handling.getRequestedTie()));
//                            handling.nextLoser();
//                            handling.setOpen(true);
//                            handling.update();
//                            handling.checkExitAfterGame();
//                        } else {
//                            handling.broadcastToVisitors(PlayerShopPacket.getMiniGameDenyTie());
//                        }
//                        handling.setRequestedTie(-1);
//                    }
//                }
//                break;
//            }
//            case SKIP: {
//                final IMaplePlayerShop ips = chr.getPlayerShop();
//                if (ips != null && ips instanceof MapleMiniGame) {
//                    MapleMiniGame handling = (MapleMiniGame) ips;
//                    if (handling.isOpen()) {
//                        break;
//                    }
//                    if (handling.getLoser() != ips.getVisitorSlot(chr)) {
//                        ips.broadcastToVisitors(PlayerShopPacket.shopChat("Turn could not be skipped by " + chr.getName() + ". Loser: " + handling.getLoser() + " Visitor: " + ips.getVisitorSlot(chr), ips.getVisitorSlot(chr)));
//                        return;
//                    }
//                    ips.broadcastToVisitors(PlayerShopPacket.getMiniGameSkip(ips.getVisitorSlot(chr)));
//                    handling.nextLoser();
//                }
//                break;
//            }
//            case MOVE_OMOK: {
//                final IMaplePlayerShop ips = chr.getPlayerShop();
//                if (ips != null && ips instanceof MapleMiniGame) {
//                    MapleMiniGame handling = (MapleMiniGame) ips;
//                    if (handling.isOpen()) {
//                        break;
//                    }
//                    if (handling.getLoser() != handling.getVisitorSlot(chr)) {
//                        handling.broadcastToVisitors(PlayerShopPacket.shopChat("Omok could not be placed by " + chr.getName() + ". Loser: " + handling.getLoser() + " Visitor: " + handling.getVisitorSlot(chr), handling.getVisitorSlot(chr)));
//                        return;
//                    }
//                    handling.setPiece(slea.readInt(), slea.readInt(), slea.readByte(), chr);
//                }
//                break;
//            }
//            case SELECT_CARD: {
//                final IMaplePlayerShop ips = chr.getPlayerShop();
//                if (ips != null && ips instanceof MapleMiniGame) {
//                    MapleMiniGame handling = (MapleMiniGame) ips;
//                    if (handling.isOpen()) {
//                        break;
//                    }
//                    if (handling.getLoser() != handling.getVisitorSlot(chr)) {
//                        handling.broadcastToVisitors(PlayerShopPacket.shopChat("Card could not be placed by " + chr.getName() + ". Loser: " + handling.getLoser() + " Visitor: " + handling.getVisitorSlot(chr), handling.getVisitorSlot(chr)));
//                        return;
//                    }
//                    if (slea.readByte() != handling.getTurn()) {
//                        handling.broadcastToVisitors(PlayerShopPacket.shopChat("Omok could not be placed by " + chr.getName() + ". Loser: " + handling.getLoser() + " Visitor: " + handling.getVisitorSlot(chr) + " Turn: " + handling.getTurn(), handling.getVisitorSlot(chr)));
//                        return;
//                    }
//                    final int slot = slea.readByte();
//                    final int turn = handling.getTurn();
//                    final int fs = handling.getFirstSlot();
//                    if (turn == 1) {
//                        handling.setFirstSlot(slot);
//                        if (handling.isOwner(chr)) {
//                            handling.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, turn), false);
//                        } else {
//                            handling.getMCOwner().getClient().getSession().write(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, turn));
//                        }
//                        handling.setTurn(0); //2nd turn nao
//                        return;
//                    } else if (fs > 0 && handling.getCardId(fs + 1) == handling.getCardId(slot + 1)) {
//                        handling.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, handling.isOwner(chr) ? 2 : 3));
//                        handling.setPoints(handling.getVisitorSlot(chr)); //correct.. so still same loser. diff turn tho
//                    } else {
//                        handling.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, fs, handling.isOwner(chr) ? 0 : 1));
//                        handling.nextLoser();//wrong haha
//
//                    }
//                    handling.setTurn(1);
//                    handling.setFirstSlot(0);
//
//                }
//                break;
//            }
//            case EXIT_AFTER_GAME:
//            case CANCEL_EXIT: {
//                final IMaplePlayerShop ips = chr.getPlayerShop();
//                if (ips != null && ips instanceof MapleMiniGame) {
//                    MapleMiniGame handling = (MapleMiniGame) ips;
//                    if (handling.isOpen()) {
//                        break;
//                    }
//                    handling.setExitAfter(chr);
//                    handling.broadcastToVisitors(PlayerShopPacket.getMiniGameExitAfter(handling.isExitAfter(chr)));
//                }
//                break;
//            }
            default: {
                //some idiots try to send huge amounts of data to this (:
                //System.out.println("Unhandled interaction action by " + chr.getName() + " : " + action + ", " + slea.toString());
                //19 (0x13) - 00 OR 01 -> itemid(maple leaf) ? who knows what this is
                break;
            }
        }
    }
}
