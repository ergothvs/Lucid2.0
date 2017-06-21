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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.world.World;
import net.DatabaseConnection;
import script.npc.NPCTalk;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MerchItemPackage;
import tools.Pair;
import tools.StringUtil;
import tools.data.LittleEndianAccessor;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;
import tools.packet.PlayerShopPacket;

public class HiredMerchantHandler {

    public static final boolean UseHiredMerchant(final MapleClient c, final boolean packet) {
        if (c.getPlayer().getMap() != null && c.getPlayer().getMap().allowPersonalShop()) {
            final byte state = checkExistance(c.getPlayer().getAccountID(), c.getPlayer().getId());

            switch (state) {
                case 1:
                    c.getPlayer().dropMessage(1, "Please claim your items from Fredrick first.");
                    break;
                case 0:
                    boolean merch = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
                    if (!merch) {
                        if (c.getChannelServer().isShutdown()) {
                            c.getPlayer().dropMessage(1, "The server is about to shut down.");
                            return false;
                        }
                        if (packet) {
                            c.getSession().write(PlayerShopPacket.sendTitleBox());
                        }
                        return true;
                    } else {
                        c.getPlayer().dropMessage(1, "Please close the existing store and try again.");
                    }
                    break;
                default:
                    c.getPlayer().dropMessage(1, "An unknown error occured.");
                    break;
            }
        } else {
            c.getSession().close();
        }
        return false;
    }

    private static byte checkExistance(final int accid, final int cid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ? OR characterid = ?")) {
                ps.setInt(1, accid);
                ps.setInt(2, cid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ps.close();
                        rs.close();
                        return 1;
                    }
                }
            }
            return 0;
        } catch (SQLException se) {
            return -1;
        }
    }

    /**
     * @param c
     */
    public static void displayMerch(MapleClient c) {
        final int conv = c.getPlayer().getConversation();
        boolean merch = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
        if (merch) {
            c.getPlayer().dropMessage(1, "Please close the existing store and try again.");
            c.getPlayer().setConversation(0);
        } else if (c.getChannelServer().isShutdown()) {
            c.getPlayer().dropMessage(1, "The world is going to shut down.");
            c.getPlayer().setConversation(0);
        } else if (conv == 3) { // Hired Merch
            final MerchItemPackage pack = loadItemFrom_Database(c.getPlayer().getAccountID());

            if (pack == null) {
            	NPCTalk talk = new NPCTalk((byte) 4, 9030000, (byte) 0);
            	talk.setText("I don't think you have any items or money to retrive here. This is where you retrieve the items and mesos that you couldn't get from your Hired Merchant. You'll also need to see me as the character that opened the Personal Store.");
                
            	c.getSession().write(NPCPacket.getNPCTalk(talk));
                c.getPlayer().setConversation(0);
            } else if (pack.getItems().size() <= 0) { //error fix for complainers.
                if (!check(c.getPlayer(), pack)) {
                    c.getSession().write(PlayerShopPacket.merchItem_Message((byte) 0x21));
                    return;
                }
                if (deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId())) {
                    //c.getPlayer().fakeRelog();
                    //c.getPlayer().gainMeso(pack.getMesos(), false);
                    c.getSession().write(PlayerShopPacket.merchItem_Message((byte) 0x1d));
                    c.getPlayer().setConversation(0);
                } else {
                    c.getPlayer().dropMessage(1, "An unknown error occured.");
                }
                c.getPlayer().setConversation(0);
            } else {
                c.getSession().write(PlayerShopPacket.merchItemStore_ItemData(pack));
                MapleInventoryManipulator.checkSpace(c, conv, conv, null);
                
                NPCTalk talk = new NPCTalk((byte) 4, 9030000, (byte) 0);
                for (final Item item : pack.getItems()) {
                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getItemId())).isFull()) {
                    	talk.setText("Please clean up your inventory.");
                    	
                        c.getSession().write(NPCPacket.getNPCTalk(talk));
                        c.getPlayer().setConversation(0);
                        break;
                    }
                    MapleInventoryManipulator.addFromDrop(c, item, true);
                    deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId());
                    //c.getPlayer().fakeRelog();
                    
                    talk.setText("Your items have been claimed.");
                    c.getSession().write(NPCPacket.getNPCTalk(talk));
                    c.getPlayer().setConversation(0);
                }

            }
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void MerchantItemStore(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer() == null) {
            return;
        }
        final byte operation = slea.readByte();
        if (operation == 27 || operation == 28) { // Request, Take out
            requestItems(c, operation == 27);
        } else if (operation == 30) { // Exit
            c.getPlayer().setConversation(0);
        }
    }

    private static void requestItems(final MapleClient c, final boolean request) {
        if (c.getPlayer().getConversation() != 3) {
            return;
        }
        boolean merch = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
        if (merch) {
            c.getPlayer().dropMessage(1, "Please close the existing store and try again.");
            c.getPlayer().setConversation(0);
            return;
        }
        final MerchItemPackage pack = loadItemFrom_Database(c.getPlayer().getAccountID());
        if (pack == null) {
            c.getPlayer().dropMessage(1, "An unknown error occured.");
            return;
        } else if (c.getChannelServer().isShutdown()) {
            c.getPlayer().dropMessage(1, "The world is going to shut down.");
            c.getPlayer().setConversation(0);
            return;
        }
        final int days = StringUtil.getDaysAmount(pack.getSavedTime(), System.currentTimeMillis()); // max 100%
        final double percentage = days / 100.0;
        final int fee = (int) Math.ceil(percentage * pack.getMesos()); // if no mesos = no tax
        if (request && days > 0 && percentage > 0 && pack.getMesos() > 0 && fee > 0) {
            c.getSession().write(PlayerShopPacket.merchItemStore((byte) 38, days, fee));
            return;
        }
        if (fee < 0) { // impossible
            c.getSession().write(PlayerShopPacket.merchItem_Message(33));
            return;
        }
        if (c.getPlayer().getMeso() < fee) {
            c.getSession().write(PlayerShopPacket.merchItem_Message(35));
            return;
        }
        if (!check(c.getPlayer(), pack)) {
            c.getSession().write(PlayerShopPacket.merchItem_Message(36));
            return;
        }
        if (deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId())) {
            if (fee > 0) {
                c.getPlayer().gainMeso(-fee, true);
            }
            c.getPlayer().gainMeso(pack.getMesos(), false);
            for (Item item : pack.getItems()) {
                MapleInventoryManipulator.addFromDrop(c, item, false);
            }
            c.getSession().write(PlayerShopPacket.merchItem_Message(32));
        } else {
            c.getPlayer().dropMessage(1, "An unknown error occured.");
        }
    }

    private static boolean check(final MapleCharacter chr, final MerchItemPackage pack) {
        if (chr.getMeso() + pack.getMesos() < 0) {
            return false;
        }
        byte eq = 0, use = 0, setup = 0, etc = 0, cash = 0;
        for (Item item : pack.getItems()) {
            final MapleInventoryType invtype = GameConstants.getInventoryType(item.getItemId());
            if (invtype == MapleInventoryType.EQUIP) {
                eq++;
            } else if (invtype == MapleInventoryType.USE) {
                use++;
            } else if (invtype == MapleInventoryType.SETUP) {
                setup++;
            } else if (invtype == MapleInventoryType.ETC) {
                etc++;
            } else if (invtype == MapleInventoryType.CASH) {
                cash++;
            }
            if (MapleItemInformationProvider.getInstance().isPickupRestricted(item.getItemId()) && chr.haveItem(item.getItemId(), 1)) {
                return false;
            }
        }
        return chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= eq && chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() >= use && chr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= setup && chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= etc && chr.getInventory(MapleInventoryType.CASH).getNumFreeSlot() >= cash;
    }

    private static boolean deletePackage(final int accid, final int packageid, final int chrId) {
        final Connection con = DatabaseConnection.getConnection();

        try {
            try (PreparedStatement ps = con.prepareStatement("DELETE from hiredmerch where accountid = ? OR packageid = ? OR characterid = ?")) {
                ps.setInt(1, accid);
                ps.setInt(2, packageid);
                ps.setInt(3, chrId);
                ps.executeUpdate();
            }
            ItemLoader.HIRED_MERCHANT.saveItems(null, packageid);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static final void showFredrick(MapleClient c) {
        final MerchItemPackage pack = HiredMerchantHandler.loadItemFrom_Database(c.getPlayer().getAccountID());
        c.getSession().write(PlayerShopPacket.merchItemStore_ItemData(pack));
    }

    private static MerchItemPackage loadItemFrom_Database(final int accountid) {
        final Connection con = DatabaseConnection.getConnection();

        try {
            ResultSet rs;

            final int packageid;
            final MerchItemPackage pack;
            try (PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ?")) {
                ps.setInt(1, accountid);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    ps.close();
                    rs.close();
                    return null;
                }   packageid = rs.getInt("PackageId");
                pack = new MerchItemPackage();
                pack.setPackageid(packageid);
                pack.setMesos(rs.getInt("Mesos"));
                pack.setSavedTime(rs.getLong("time"));
            }
            rs.close();

            Map<Long, Pair<Item, MapleInventoryType>> items = ItemLoader.HIRED_MERCHANT.loadItems(false, packageid);
            if (items != null) {
                List<Item> iters = new ArrayList<>();
                for (Pair<Item, MapleInventoryType> z : items.values()) {
                    iters.add(z.left);
                }
                pack.setItems(iters);
            }

            return pack;
        } catch (SQLException e) {
            return null;
        }
    }
}
