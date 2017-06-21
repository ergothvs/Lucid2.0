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
package tools.packet;

import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import handling.SendPacketOpcode;
import handling.channel.handler.PlayerInteractionHandler;
import handling.world.MapleCharacterLook;
import server.MerchItemPackage;
import server.stores.AbstractPlayerStore.BoughtItem;
import server.stores.HiredMerchant;
import server.stores.IMaplePlayerShop;
import server.stores.MapleMiniGame;
import server.stores.MaplePlayerShop;
import server.stores.MaplePlayerShopItem;
import tools.Pair;
import tools.data.PacketWriter;

public class PlayerShopPacket {

    public static byte[] sendTitleBox() {
        return sendTitleBox(7); // SendOpenShopRequest
    }

    public static byte[] sendTitleBox(int mode) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeShort(SendPacketOpcode.ENTRUSTED_SHOP_CHECK_RESULT.getValue());
        pw.write(mode);
        if ((mode == 8) || (mode == 16)) {
            pw.writeInt(0);
            pw.write(0);
        } else if (mode == 13) {
            pw.writeInt(0);
        } else if (mode == 14) {
            pw.write(0);
        } else if (mode == 18) {
            pw.write(1);
            pw.writeMapleAsciiString("");
        }

        return pw.getPacket();
    }

    public static byte[] requestShopPic(final int oid) {
        final PacketWriter pw = new PacketWriter(17);

        pw.writeShort(SendPacketOpcode.ENTRUSTED_SHOP_CHECK_RESULT.getValue());
        pw.write(17);
        pw.writeInt(oid);
        pw.writeShort(0);
        pw.writeLong(0L);

        return pw.getPacket();
    }

    public static final byte[] addCharBox(final MapleCharacter c, final int type) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MINIROOM_BALLOON.getValue());
        pw.writeInt(c.getId());
        PacketHelper.addAnnounceBox(pw, c);

        return pw.getPacket();
    }

    public static final byte[] removeCharBox(final MapleCharacter c) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MINIROOM_BALLOON.getValue());
        pw.writeInt(c.getId());
        pw.write(0);

        return pw.getPacket();
    }

    public static final byte[] sendPlayerShopBox(final MapleCharacter c) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MINIROOM_BALLOON.getValue());
        pw.writeInt(c.getId());
        PacketHelper.addAnnounceBox(pw, c);

        return pw.getPacket();
    }

    public static byte[] getHiredMerch(MapleCharacter chr, HiredMerchant merch, boolean firstTime) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(20);//was11
        pw.write(6);
        pw.write(7);
        pw.writeShort(merch.getVisitorSlot(chr));
        pw.writeInt(merch.getItemId());
        pw.writeMapleAsciiString("Hired Merchant");
        for (Pair storechr : merch.getVisitors()) {
            pw.write(((Byte) storechr.left).byteValue());
            PacketHelper.addCharLook(pw, (MapleCharacterLook) storechr.right, false, false);
            pw.writeMapleAsciiString(((MapleCharacter) storechr.right).getName());
            pw.writeShort(((MapleCharacter) storechr.right).getJob());
        }
        pw.write(-1);
        pw.writeShort(0);
        pw.writeMapleAsciiString(merch.getOwnerName());
        if (merch.isOwner(chr)) {
            pw.writeInt(merch.getTimeLeft());
            pw.write(firstTime ? 1 : 0);
            pw.write(merch.getBoughtItems().size());
            for (final BoughtItem SoldItem : merch.getBoughtItems()) {
                pw.writeInt(SoldItem.id);
                pw.writeShort(SoldItem.quantity);
                pw.writeLong(SoldItem.totalPrice);
                pw.writeMapleAsciiString(SoldItem.buyer);
            }
            pw.writeLong(merch.getMeso());
        }
        pw.writeInt(263);
        pw.writeMapleAsciiString(merch.getDescription());
        pw.write(16);
        pw.writeLong(merch.getMeso());
        pw.write(merch.getItems().size());
        for (MaplePlayerShopItem item : merch.getItems()) {
            pw.writeShort(item.bundles);
            pw.writeShort(item.item.getQuantity());
            pw.writeLong(item.price);
            PacketHelper.addItemInfo(pw, item.item);
        }
        pw.writeShort(0);

        return pw.getPacket();
    }

    public static final byte[] getPlayerStore(final MapleCharacter chr, final boolean firstTime) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        IMaplePlayerShop ips = chr.getPlayerShop();
        pw.write(11);
        switch (ips.getShopType()) {
            case 2:
                pw.write(4);
                pw.write(4);
                break;
            case 3:
                pw.write(2);
                pw.write(2);
                break;
            case 4:
                pw.write(1);
                pw.write(2);
                break;
        }
        pw.writeShort(ips.getVisitorSlot(chr));
        PacketHelper.addCharLook(pw, ((MaplePlayerShop) ips).getMCOwner(), false, false);
        pw.writeMapleAsciiString(ips.getOwnerName());
        pw.writeShort(((MaplePlayerShop) ips).getMCOwner().getJob());
        for (final Pair<Byte, MapleCharacter> storechr : ips.getVisitors()) {
            pw.write(storechr.left);
            PacketHelper.addCharLook(pw, storechr.right, false, false);
            pw.writeMapleAsciiString(storechr.right.getName());
            pw.writeShort(storechr.right.getJob());
        }
        pw.write(255);
        pw.writeMapleAsciiString(ips.getDescription());
        pw.write(10);
        pw.write(ips.getItems().size());

        for (final MaplePlayerShopItem item : ips.getItems()) {
            pw.writeShort(item.bundles);
            pw.writeShort(item.item.getQuantity());
            pw.writeInt(item.price);
            PacketHelper.addItemInfo(pw, item.item);
        }
        return pw.getPacket();
    }

    public static final byte[] shopChat(final String message, final int slot) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(24);//was15
        pw.write(25);//was15
        pw.write(slot);
        pw.writeMapleAsciiString(message);

        return pw.getPacket();
    }

    public static final byte[] shopErrorMessage(final int error, final int type) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(28);//was18
        pw.write(type);
        pw.write(error);

        return pw.getPacket();
    }

    public static final byte[] spawnHiredMerchant(final HiredMerchant hm) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SPAWN_HIRED_MERCHANT.getValue());
        pw.writeInt(hm.getOwnerId());
        pw.writeInt(hm.getItemId());
        pw.writePos(hm.getTruePosition());
        pw.writeShort(0);
        pw.writeMapleAsciiString(hm.getOwnerName());
        PacketHelper.addInteraction(pw, hm);
//        System.err.println(hm.getItemId());
        return pw.getPacket();
    }

    public static final byte[] destroyHiredMerchant(final int id) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.DESTROY_HIRED_MERCHANT.getValue());
        pw.writeInt(id);

        return pw.getPacket();
    }

    public static final byte[] shopItemUpdate(final IMaplePlayerShop shop) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(77);//was50
        if (shop.getShopType() == 1) {
            pw.writeLong(0L);
        }
        pw.write(shop.getItems().size());
        for (final MaplePlayerShopItem item : shop.getItems()) {
            pw.writeShort(item.bundles);
            pw.writeShort(item.item.getQuantity());
            pw.writeLong(item.price);
            PacketHelper.addItemInfo(pw, item.item);
        }
        pw.writeShort(0);

        return pw.getPacket();
    }

    public static final byte[] shopVisitorAdd(final MapleCharacter chr, final int slot) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(PlayerInteractionHandler.Interaction.VISIT.action);
//        pw.write(19);//was10
        pw.write(slot);
        PacketHelper.addCharLook(pw, chr, false, false);
        pw.writeMapleAsciiString(chr.getName());
        pw.writeShort(chr.getJob());

        return pw.getPacket();
    }

    public static final byte[] shopVisitorLeave(final byte slot) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(19);
        pw.write(slot);

        return pw.getPacket();
    }

    public static final byte[] Merchant_Buy_Error(final byte message) {
        final PacketWriter pw = new PacketWriter();

        // 2 = You have not enough meso
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(44);
        pw.write(message);

        return pw.getPacket();
    }

    public static final byte[] updateHiredMerchant(final HiredMerchant shop) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.UPDATE_HIRED_MERCHANT.getValue());
        pw.writeInt(shop.getOwnerId());
        PacketHelper.addInteraction(pw, shop);

        return pw.getPacket();
    }

    public static final byte[] merchItem_Message(final int op) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MERCH_ITEM_MSG.getValue());
        pw.write(op);

        return pw.getPacket();
    }

    public static final byte[] merchItemStore(final byte op, final int days, final int fees) {
        final PacketWriter pw = new PacketWriter();

        // 40: This is currently unavailable.\r\nPlease try again later
        pw.writeShort(SendPacketOpcode.MERCH_ITEM_STORE.getValue());
        pw.write(op);
        switch (op) {
            case 39:
                pw.writeInt(999999999); // ? 
                pw.writeInt(999999999); // mapid
                pw.write(0); // >= -2 channel
                // if cc -1 or map = 999,999,999 : I don't think you have any items or money to retrieve here. This is where you retrieve the items and mesos that you couldn't get from your Hired Merchant. You'll also need to see me as the character that opened the Personal Store.
                //Your Personal Store is open #bin Channel %s, Free Market %d#k.\r\nIf you need me, then please close your personal store first before seeing me.
                break;
            case 38:
                pw.writeInt(days); // % tax or days, 1 day = 1%
                pw.writeInt(fees); // feees
                break;
        }

        return pw.getPacket();
    }

    public static final byte[] merchItemStore_ItemData(final MerchItemPackage pack) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MERCH_ITEM_STORE.getValue());
        pw.write(38);
        pw.writeInt(9030000); // Fredrick
        pw.write(16); // max items?
        pw.writeLong(126L); // ?
        pw.writeLong(pack.getMesos());
        pw.write(0);
        pw.write(pack.getItems().size());
        for (final Item item : pack.getItems()) {
            PacketHelper.addItemInfo(pw, item);
        }
        pw.write(new byte[3]);

        return pw.getPacket();
    }

    public static byte[] getMiniGame(MapleClient c, MapleMiniGame minigame) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(10);
        pw.write(minigame.getGameType());
        pw.write(minigame.getMaxSize());
        pw.writeShort(minigame.getVisitorSlot(c.getPlayer()));
        PacketHelper.addCharLook(pw, minigame.getMCOwner(), false, false);
        pw.writeMapleAsciiString(minigame.getOwnerName());
        pw.writeShort(minigame.getMCOwner().getJob());
        for (Pair visitorz : minigame.getVisitors()) {
            pw.write(((Byte) visitorz.getLeft()).byteValue());
            PacketHelper.addCharLook(pw, (MapleCharacterLook) visitorz.getRight(), false, false);
            pw.writeMapleAsciiString(((MapleCharacter) visitorz.getRight()).getName());
            pw.writeShort(((MapleCharacter) visitorz.getRight()).getJob());
        }
        pw.write(-1);
        pw.write(0);
        addGameInfo(pw, minigame.getMCOwner(), minigame);
        for (Pair visitorz : minigame.getVisitors()) {
            pw.write(((Byte) visitorz.getLeft()).byteValue());
            addGameInfo(pw, (MapleCharacter) visitorz.getRight(), minigame);
        }
        pw.write(-1);
        pw.writeMapleAsciiString(minigame.getDescription());
        pw.writeShort(minigame.getPieceType());
        return pw.getPacket();
    }

    public static byte[] getMiniGameReady(boolean ready) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(ready ? 56 : 60);
        return pw.getPacket();
    }

    public static byte[] getMiniGameExitAfter(boolean ready) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(ready ? 54 : 58);
        return pw.getPacket();
    }

    public static byte[] getMiniGameStart(int loser) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(62);
        pw.write(loser == 1 ? 0 : 1);
        return pw.getPacket();
    }

    public static byte[] getMiniGameSkip(int slot) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(64);

        pw.write(slot);
        return pw.getPacket();
    }

    public static byte[] getMiniGameRequestTie() {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(51);
        return pw.getPacket();
    }

    public static byte[] getMiniGameDenyTie() {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(50);
        return pw.getPacket();
    }

    public static byte[] getMiniGameFull() {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.writeShort(10);
        pw.write(2);
        return pw.getPacket();
    }

    public static byte[] getMiniGameMoveOmok(int move1, int move2, int move3) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(65);
        pw.writeInt(move1);
        pw.writeInt(move2);
        pw.write(move3);
        return pw.getPacket();
    }

    public static byte[] getMiniGameNewVisitor(MapleCharacter c, int slot, MapleMiniGame game) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(9);
        pw.write(slot);
        PacketHelper.addCharLook(pw, c, false, false);
        pw.writeMapleAsciiString(c.getName());
        pw.writeShort(c.getJob());
        addGameInfo(pw, c, game);
        return pw.getPacket();
    }

    public static void addGameInfo(PacketWriter pw, MapleCharacter chr, MapleMiniGame game) {
        pw.writeInt(game.getGameType());
        pw.writeInt(game.getWins(chr));
        pw.writeInt(game.getTies(chr));
        pw.writeInt(game.getLosses(chr));
        pw.writeInt(game.getScore(chr));
    }

    public static byte[] getMiniGameClose(byte number) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(18);
        pw.write(1);
        pw.write(number);
        return pw.getPacket();
    }

    public static byte[] getMatchCardStart(MapleMiniGame game, int loser) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(62);
        pw.write(loser == 1 ? 0 : 1);
        int times = game.getPieceType() == 2 ? 30 : game.getPieceType() == 1 ? 20 : 12;
        pw.write(times);
        for (int i = 1; i <= times; i++) {
            pw.writeInt(game.getCardId(i));
        }
        return pw.getPacket();
    }

    public static byte[] getMatchCardSelect(int turn, int slot, int firstslot, int type) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(69);
        pw.write(turn);
        pw.write(slot);
        if (turn == 0) {
            pw.write(firstslot);
            pw.write(type);
        }
        return pw.getPacket();
    }

    public static byte[] getMiniGameResult(MapleMiniGame game, int type, int x) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(63);
        pw.write(type);
        game.setPoints(x, type);
        if (type != 0) {
            game.setPoints(x == 1 ? 0 : 1, type == 2 ? 0 : 1);
        }
        if (type != 1) {
            if (type == 0) {
                pw.write(x == 1 ? 0 : 1);
            } else {
                pw.write(x);
            }
        }
        addGameInfo(pw, game.getMCOwner(), game);
        for (Pair visitorz : game.getVisitors()) {
            addGameInfo(pw, (MapleCharacter) visitorz.right, game);
        }

        return pw.getPacket();

    }

    public static final byte[] MerchantBlackListView(final List<String> blackList) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(39);
        pw.writeShort(blackList.size());
        for (String visit : blackList) {
            pw.writeMapleAsciiString(visit);
        }
        return pw.getPacket();
    }

    public static final byte[] MerchantVisitorView(List<String> visitor) {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        pw.write(38);
        pw.writeShort(visitor.size());
        for (String visit : visitor) {
            pw.writeMapleAsciiString(visit);
            pw.writeInt(1);
        }
        return pw.getPacket();
    }
}
