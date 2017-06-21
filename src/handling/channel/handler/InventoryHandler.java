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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;

import client.InnerAbillity;
import client.InnerSkillValueHolder;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.MapleDisease;
import client.MapleStat;
import client.MapleTrait.MapleTraitType;
import client.MonsterFamiliar;
import client.PlayerStats;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.*;
import client.inventory.Equip.ScrollResult;
import client.inventory.MaplePet.PetFlag;
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import script.npc.NPCScriptManager;
import script.npc.NPCTalk;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.RandomRewards;
import server.StructFamiliar;
import server.StructItemOption;
import server.StructRewardItem;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMist;
import server.quest.MapleQuest;
import server.quest.MapleQuestStatus;
import server.shops.MapleShopFactory;
import server.stores.HiredMerchant;
import server.stores.IMaplePlayerShop;
import tools.FileoutputUtil;
import tools.Pair;
import tools.Randomizer;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.NPCPacket;
import tools.packet.CSPacket;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InfoPacket;
import tools.packet.CWvsContext.InventoryPacket;
import tools.packet.MobPacket;
import tools.packet.PetPacket;
import tools.packet.PlayerShopPacket;

public class InventoryHandler {

    public static final void SwitchBag(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().hasBlockedInventory()) { //hack
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
        slea.skip(4);
        final short src = (short) slea.readInt();                                       //01 00
        final short dst = (short) slea.readInt();                                       //00 00
        if (src < 100 || dst < 100) {
            return;
        }
        MapleInventoryManipulator.move(c, MapleInventoryType.ETC, src, dst);
    }

    public static final void MoveBag(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().hasBlockedInventory()) { //hack
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
        slea.skip(4); // update tick
        final boolean srcFirst = slea.readInt() > 0;
        short dst = (short) slea.readInt();                                       //01 00
        if (slea.readByte() != 4) { //must be etc
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        short src = slea.readShort();                                             //00 00
        MapleInventoryManipulator.move(c, MapleInventoryType.ETC, srcFirst ? dst : src, srcFirst ? src : dst);
    }

    public static final void ItemSort(final LittleEndianAccessor slea, final MapleClient c) {
    	slea.skip(4); // update tick
        c.getPlayer().setScrolledPosition((short) 0);
        final MapleInventoryType pInvType = MapleInventoryType.getByType(slea.readByte());
        if (pInvType == MapleInventoryType.UNDEFINED || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final MapleInventory pInv = c.getPlayer().getInventory(pInvType); //Mode should correspond with MapleInventoryType
        boolean sorted = false;

        while (!sorted) {
            final byte freeSlot = (byte) pInv.getNextFreeSlot();
            if (freeSlot != -1) {
                byte itemSlot = -1;
                for (byte i = (byte) (freeSlot + 1); i <= pInv.getSlotLimit(); i++) {
                    if (pInv.getItem(i) != null) {
                        itemSlot = i;
                        break;
                    }
                }
                if (itemSlot > 0) {
                    MapleInventoryManipulator.move(c, pInvType, itemSlot, freeSlot);
                } else {
                    sorted = true;
                }
            } else {
                sorted = true;
            }
        }
        c.getSession().write(CWvsContext.finishedSort(pInvType.getType()));
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void ItemGather(final LittleEndianAccessor slea, final MapleClient c) {
        // [41 00] [E5 1D 55 00] [01]
        // [32 00] [01] [01] // Sent after

    	slea.skip(4); // update tick
        c.getPlayer().setScrolledPosition((short) 0);
        if (c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final byte mode = slea.readByte();
        final MapleInventoryType invType = MapleInventoryType.getByType(mode);
        MapleInventory Inv = c.getPlayer().getInventory(invType);

        final List<Item> itemMap = new LinkedList<>();
        for (Item item : Inv.list()) {
            itemMap.add(item.copy()); // clone all  items T___T.
        }
        for (Item itemStats : itemMap) {
            MapleInventoryManipulator.removeFromSlot(c, invType, itemStats.getPosition(), itemStats.getQuantity(), true, false);
        }

        final List<Item> sortedItems = sortItems(itemMap);
        for (Item item : sortedItems) {
            MapleInventoryManipulator.addFromDrop(c, item, false);
        }
        c.getSession().write(CWvsContext.finishedGather(mode));
        c.getSession().write(CWvsContext.enableActions());
        itemMap.clear();
        sortedItems.clear();
    }

    private static List<Item> sortItems(final List<Item> passedMap) {
        final List<Integer> itemIds = new ArrayList<>(); // empty list.
        for (Item item : passedMap) {
            itemIds.add(item.getItemId()); // adds all item ids to the empty list to be sorted.
        }
        Collections.sort(itemIds); // sorts item ids

        final List<Item> sortedList = new LinkedList<>(); // ordered list pl0x <3.

        for (Integer val : itemIds) {
            for (Item item : passedMap) {
                if (val == item.getItemId()) { // Goes through every index and finds the first value that matches
                    sortedList.add(item);
                    passedMap.remove(item);
                    break;
                }
            }
        }
        return sortedList;
    }

    public static boolean UseRewardItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        //System.out.println("[Reward Item] " + slea.toString());
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final boolean unseal = slea.readByte() > 0;
        return UseRewardItem(slot, itemId, unseal, c, chr);
    }

    public static boolean UseRewardItem(byte slot, int itemId, final boolean unseal, final MapleClient c, final MapleCharacter chr) {
        final Item toUse = c.getPlayer().getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
        c.getSession().write(CWvsContext.enableActions());
        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && !chr.hasBlockedInventory()) {
            if (chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.USE).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.ETC).getNextFreeSlot() > -1) {
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final Pair<Integer, List<StructRewardItem>> rewards = ii.getRewardItem(itemId);

                switch (itemId) {
                                    case 2290245:
                case 2290285:
                case 2290448:
                case 2290449:
                case 2290450:
                case 2290451:
                case 2290452:
                case 2290454:
                case 2290455:
                case 2290456:
                case 2290457:
                case 2290458:
                case 2290459:
                case 2290460:
                case 2290461:
                case 2290462:
                case 2290463:
                case 2290464:
                case 2290465:
                case 2290466:
                case 2290467:
                case 2290468:
                case 2290469:
                case 2290571:
                case 2290581:
                case 2290602:
                case 2290653:
                case 2290714:
                case 2290715:
                case 2290721:
                case 2290722:
                case 2290723:
                case 2290724:
                case 2290803:
                case 2290868:
                case 2290869:
                case 2290870:
                case 2290871:
                case 2290872:
                case 2290873:
                case 2290874:
                case 2290875:
                case 2290876:
                case 2290877:
                case 2290878:
                case 2290879:
                case 2290880:
                case 2290881:
                case 2290882:
                case 2290883:
                case 2290884:
                case 2290885:
                case 2290886:
                case 2290887:
                case 2290888:
                case 2290889:
                case 2290890:
                case 2290891:
                case 2290892:
                case 2290893:
                case 2290914:
                case 2290915:
                case 2291020:
            //    case 2291021:
              //  case 2430144: //smb
                    final int itemid = Randomizer.nextInt(999) + 2290000;
                    World.Broadcast.broadcastMessage(CField.getGameMessage("SMB.", (short) 8));
                    if (MapleItemInformationProvider.getInstance().itemExists(itemid) && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Special") && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Event")) {
                        MapleInventoryManipulator.addById(c, itemid, (short) 1, "Reward item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    }
                    break;
                }
                if (rewards != null && rewards.getLeft() > 0) {
                    while (true) {
                        for (StructRewardItem reward : rewards.getRight()) {
                            if (reward.prob > 0 && Randomizer.nextInt(rewards.getLeft()) < reward.prob) { // Total prob
                                if (GameConstants.getInventoryType(reward.itemid) == MapleInventoryType.EQUIP) {
                                    final Item item = ii.getEquipById(reward.itemid);
                                    if (reward.period > 0) {
                                        item.setExpiration(System.currentTimeMillis() + (reward.period * 60 * 60 * 10));
                                    }
                                    item.setGMLog("Reward item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                                    MapleInventoryManipulator.addbyItem(c, item);
                                } else {
                                    MapleInventoryManipulator.addById(c, reward.itemid, reward.quantity, "Reward item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                                }
                                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);

                                c.getSession().write(EffectPacket.showRewardItemAnimation(reward.itemid, reward.effect));
                                chr.getMap().broadcastMessage(chr, EffectPacket.showRewardItemAnimation(reward.itemid, reward.effect, chr.getId()), false);
                                return true;
                            }
                        }
                    }
                } else {
                    if (itemId == 2028162) { //custom test
                        List<Integer> items;
                        Integer[] itemArray = {1002140, 1302000, 1302001,
                            1302002, 1302003, 1302004, 1302005, 1302006,
                            1302007};
                        items = Arrays.asList(itemArray);
                        if (unseal) {
                            MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);
                            Item item = ii.getEquipById(items.get(Randomizer.nextInt(items.size())));
                            MapleInventoryManipulator.addbyItem(c, item);
                            c.getSession().write(CField.unsealBox(item.getItemId()));
                            c.getSession().write(EffectPacket.showRewardItemAnimation(2028162, "")); //sealed box
                        } else {
                            c.getSession().write(CField.sendSealedBox(slot, 2028162, items)); //sealed box
                        }
                    }
                    if (itemId >= 2028154 && itemId <= 2028156
                            || itemId >= 2028161 && itemId <= 2028165) {
                        //sealed box
                        List<Integer> items = GameConstants.getSealedBoxItems(itemId);
                        if (items.size() < 1) {
                            chr.dropMessage(6, "Failed to find rewards.");
                            return false;
                        }
                        if (unseal) {
                            MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);
                            Item item = ii.getEquipById(items.get(Randomizer.nextInt(items.size())));
                            MapleInventoryManipulator.addbyItem(c, item);
                            c.getSession().write(CField.unsealBox(item.getItemId()));
                            c.getSession().write(EffectPacket.showRewardItemAnimation(itemId, ""));
                        } else {
                            c.getSession().write(CField.sendSealedBox(slot, itemId, items));
                        }
                        return true;
                    }
                    switch (itemId) {
                         case 2291021:
                             chr.dropMessage(6, "Unknown error.");
                             break;
                        default:
                            chr.dropMessage(6, "Unknown error.");
                            break;
                    }
                }
            } else {
                chr.dropMessage(6, "Insufficient inventory slot.");
            }
        }
        return false;
    }

    public static void UseExpItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory() || chr.inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        slea.skip(4); // update tick
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (!MapleItemInformationProvider.getInstance().getEquipStats(itemId).containsKey("exp")) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        MapleItemInformationProvider.getInstance().getEquipStats(itemId).get("exp");
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
    }

    public static final void UseItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMapId() == 749040100 || chr.getMap() == null || chr.hasDisease(MapleDisease.POTION) || chr.hasBlockedInventory() || chr.inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final long time = System.currentTimeMillis();
        if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "You may not use this item yet.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        slea.skip(4); // update tick
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
         switch(toUse.getItemId()) {
        case 2290285:
            System.out.println("Return 1");
         }
        

        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) { //cwk quick hack
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
                if (chr.getMap().getConsumeItemCoolTime() > 0) {
                    chr.setNextConsume(time + (chr.getMap().getConsumeItemCoolTime() * 1000));
                }
            }

        } else {
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static final void UseCosmetic(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory() || chr.inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 254 || (itemId / 1000) % 10 != chr.getGender()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
    }

    public static final void UseReturnScroll(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (!chr.isAlive() || chr.getMapId() == 749040100 || chr.hasBlockedInventory() || chr.isInBlockedMap() || chr.inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        slea.skip(4); // update tick
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) {
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyReturnScroll(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
            } else {
                c.getSession().write(CWvsContext.enableActions());
            }
        } else {
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static final void UseAlienSocket(final LittleEndianAccessor slea, final MapleClient c) {
    	slea.skip(4); // update tick
        c.getPlayer().setScrolledPosition((short) 0);
        final Item alienSocket = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) slea.readShort());
        final int alienSocketId = slea.readInt();
        final Item toMount = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readShort());
        if (alienSocket == null || alienSocketId != alienSocket.getItemId() || toMount == null || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            return;
        }
        // Can only use once-> 2nd and 3rd must use NPC.
        final Equip eqq = (Equip) toMount;
        if (eqq.getSocketState() != 0) { // Used before
            c.getPlayer().dropMessage(1, "This item already has a socket.");
        } else {
            c.getSession().write(CSPacket.useAlienSocket(false));
            eqq.setSocketByNmb(0, 0); // First socket, GMS removed the other 2
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, alienSocket.getPosition(), (short) 1, false);
            c.getPlayer().forceReAddItem(toMount, MapleInventoryType.EQUIP);
        }
        c.getSession().write(CSPacket.useAlienSocket(true));
        //c.getPlayer().fakeRelog();
        //c.getPlayer().dropMessage(1, "Added 1 socket successfully to " + toMount);
    }

    public static final void UseNebulite(final LittleEndianAccessor slea, final MapleClient c) {
    	slea.skip(4); // update tick
        c.getPlayer().setScrolledPosition((short) 0);
        final Item nebulite = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) slea.readShort());
        final int nebuliteId = slea.readInt();
        final Item toMount = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readShort());
        if (nebulite == null || nebuliteId != nebulite.getItemId() || toMount == null || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            return;
        }
        final Equip eqq = (Equip) toMount;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        boolean success = false;
        if (eqq.getSocketByNmb(0) == 0/* || eqq.getSocket2() == 0 || eqq.getSocket3() == 0*/) { // GMS removed 2nd and 3rd sockets, we can put into npc.
            final StructItemOption pot = ii.getSocketInfo(nebuliteId);
            if (pot != null && GameConstants.optionTypeFits(pot.optionType, eqq.getItemId())) {
                //if (eqq.getSocket1() == 0) { // priority comes first
                eqq.setSocketByNmb(0, pot.opID);
                //}// else if (eqq.getSocket2() == 0) {
                //    eqq.setSocket2(pot.opID);
                //} else if (eqq.getSocket3() == 0) {
                //    eqq.setSocket3(pot.opID);
                //}
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, nebulite.getPosition(), (short) 1, false);
                c.getPlayer().forceReAddItem(toMount, MapleInventoryType.EQUIP);
                success = true;
            }
        }
        c.getPlayer().getMap().broadcastMessage(CField.showNebuliteEffect(c.getPlayer().getId(), success));
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseNebuliteFusion(final LittleEndianAccessor slea, final MapleClient c) {
    	slea.skip(4); // update tick
        c.getPlayer().setScrolledPosition((short) 0);
        final int nebuliteId1 = slea.readInt();
        final Item nebulite1 = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) slea.readShort());
        final int nebuliteId2 = slea.readInt();
        final Item nebulite2 = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) slea.readShort());
        final long mesos = slea.readInt();
        final int premiumQuantity = slea.readInt();
        if (nebulite1 == null || nebulite2 == null || nebuliteId1 != nebulite1.getItemId() || nebuliteId2 != nebulite2.getItemId() || (mesos == 0 && premiumQuantity == 0) || (mesos != 0 && premiumQuantity != 0) || mesos < 0 || premiumQuantity < 0 || c.getPlayer().hasBlockedInventory()) {
            c.getPlayer().dropMessage(1, "Failed to fuse Nebulite.");
            c.getSession().write(InventoryPacket.getInventoryFull());
            return;
        }
        final int grade1 = GameConstants.getNebuliteGrade(nebuliteId1);
        final int grade2 = GameConstants.getNebuliteGrade(nebuliteId2);
        final int highestRank = grade1 > grade2 ? grade1 : grade2;
        if (grade1 == -1 || grade2 == -1 || (highestRank == 3 && premiumQuantity != 2) || (highestRank == 2 && premiumQuantity != 1)
                || (highestRank == 1 && mesos != 5000) || (highestRank == 0 && mesos != 3000) || (mesos > 0 && c.getPlayer().getMeso() < mesos)
                || (premiumQuantity > 0 && c.getPlayer().getItemQuantity(4420000, false) < premiumQuantity) || grade1 >= 4 || grade2 >= 4
                || (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 1)) { // 4000 + = S, 3000 + = A, 2000 + = B, 1000 + = C, else = D
            c.getSession().write(CField.useNebuliteFusion(c.getPlayer().getId(), 0, false));
            return; // Most of them were done in client, so we just send the unsuccessfull packet, as it is only here when they packet edit.
        }
        final int avg = (grade1 + grade2) / 2; // have to revise more about grades.
        final int rank = Randomizer.nextInt(100) < 4 ? (Randomizer.nextInt(100) < 70 ? (avg != 3 ? (avg + 1) : avg) : (avg != 0 ? (avg - 1) : 0)) : avg;
        // 4 % chance to up/down 1 grade, (70% to up, 30% to down), cannot up to S grade. =)
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final List<StructItemOption> pots = new LinkedList<>(ii.getAllSocketInfo(rank).values());
        int newId = 0;
        while (newId == 0) {
            StructItemOption pot = pots.get(Randomizer.nextInt(pots.size()));
            if (pot != null) {
                newId = pot.opID;
            }
        }
        if (mesos > 0) {
            c.getPlayer().gainMeso(-mesos, true);
        } else if (premiumQuantity > 0) {
            MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4420000, premiumQuantity, false, false);
        }
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, nebulite1.getPosition(), (short) 1, false);
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, nebulite2.getPosition(), (short) 1, false);
        MapleInventoryManipulator.addById(c, newId, (short) 1, "Fused from " + nebuliteId1 + " and " + nebuliteId2 + " on " + FileoutputUtil.CurrentReadable_Date());
        c.getSession().write(CField.useNebuliteFusion(c.getPlayer().getId(), newId, true));
    }

    public static void UseGoldenHammer(final LittleEndianAccessor slea, final MapleClient c) {
        //[21 D5 10 04] [16 00 00 00] [7B B0 25 00] [01 00 00 00] [03 00 00 00]
    	slea.skip(4); // update tick
        byte slot = (byte) slea.readInt();
        int itemId = slea.readInt();
        slea.skip(4);
        byte equipslot = (byte) slea.readInt();
        Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        Equip equip = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(equipslot);
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        int success;
        if (itemId == 2470004 && Randomizer.nextInt(100) < 20) {
            equip.setUpgradeSlots((byte) (equip.getUpgradeSlots() + 1));
            success = 0;
        } else if ((itemId == 2470001 || itemId == 2470002) && Randomizer.nextInt(100) < 50) {
            equip.setUpgradeSlots((byte) (equip.getUpgradeSlots() + 1));
            success = 0;
        } else if (itemId == 2470000 || itemId == 2470003) {
            equip.setUpgradeSlots((byte) (equip.getUpgradeSlots() + 1));
            success = 0;
        } else {
            success = 1;
        }
        c.getSession().write(CSPacket.GoldenHammer((byte) 2, success));
        equip.setViciousHammer((byte) 1);
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, true);
    }

    public static void UseMagnify(final LittleEndianAccessor slea, final MapleClient c) {
    	slea.skip(4); // update tick
        c.getPlayer().setScrolledPosition((short) 0);
        final byte src = (byte) slea.readShort();
        final boolean insight = src == 127 && c.getPlayer().getTrait(MapleTraitType.sense).getLevel() >= 30;
        final Item magnify = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(src);
        byte eqSlot = (byte) slea.readShort();
        boolean equipped = eqSlot < 0;
        final Item toReveal = c.getPlayer().getInventory(equipped ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP).getItem(eqSlot);
        if (toReveal == null || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            System.out.println("Return 1");
            return;
        }
        final Equip eqq = (Equip) toReveal;
        final long price = GameConstants.getMagnifyPrice(eqq);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(eqq.getItemId()) / 10;
        if (eqq.getState() == 1 && (src == 0x7F && price != -1 && c.getPlayer().getMeso() >= price
                || insight || magnify.getItemId() == 2460003 || (magnify.getItemId() == 2460002 && reqLevel <= 12)
                || (magnify.getItemId() == 2460001 && reqLevel <= 7) || (magnify.getItemId() == 2460000 && reqLevel <= 3))) {
            final List<List<StructItemOption>> pots = new LinkedList<>(ii.getAllPotentialInfo().values());
            int lockedLine = -1;
            int locked = 0;
            for(int i = 0; i <= 2; i++){
                if(Math.abs(eqq.getPotentialByLine(i)) / 100000 > 0) {
                    lockedLine = i;
                    locked = Math.abs(eqq.getPotentialByLine(i));
                }
            }
//            if (Math.abs(eqq.getPotentialByLine(1)) / 100000 > 0) {
//                lockedLine = 1;
//                locked = Math.abs(eqq.getPotential1());
//            } else if (Math.abs(eqq.getPotential2()) / 100000 > 0) {
//                lockedLine = 2;
//                locked = Math.abs(eqq.getPotential2());
//            } else if (Math.abs(eqq.getPotential3()) / 100000 > 0) {
//                lockedLine = 3;
//                locked = Math.abs(eqq.getPotential3());
//            }
            int new_state = Math.abs(eqq.getPotentialByLine(0));
            if (lockedLine == 1) {
                new_state = locked / 10000 < 1 ? 17 : 16 + locked / 10000;
            }
            if (new_state > 20 || new_state < 17) { // incase overflow
                new_state = 17;
            }
            int lines = 2; // default
            if (eqq.getPotentialByLine(2) != 0) {
                lines++;
            }
            while (eqq.getState() != new_state) {
                //31001 = haste, 31002 = door, 31003 = se, 31004 = hb, 41005 = combat orders, 41006 = advanced blessing, 41007 = speed infusion
                for (int i = 0; i < lines; i++) { // minimum 2 lines, max 5
                    boolean rewarded = false;
                    while (!rewarded) {
                        StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                        if (pot != null && pot.reqLevel / 1 <= reqLevel && GameConstants.optionTypeFits(pot.optionType, eqq.getItemId()) && GameConstants.potentialIDFits(pot.opID, new_state, i)) { //optionType
                            //have to research optionType before making this truely official-like
                            if (isAllowedPotentialStat(eqq, pot.opID)) {
                                eqq.setPotentialByLine(i, pot.opID);
                                rewarded = true;
                            }
                        }
                    }
                }
            }
            if(lockedLine >= 0){
                eqq.setPotentialByLine(lockedLine, Math.abs(locked - lockedLine * 100000));
            }
//            switch (lockedLine) {
//                case 1:
//                    eqq.setPotential1(Math.abs(locked - lockedLine * 100000));
//                    break;
//                case 2:
//                    eqq.setPotential2(Math.abs(locked - lockedLine * 100000));
//                    break;
//                case 3:
//                    eqq.setPotential3(Math.abs(locked - lockedLine * 100000));
//                    break;
//            }
            c.getPlayer().getTrait(MapleTraitType.insight).addExp((src == 0x7F && price != -1 ? 10 : insight ? 10 : ((magnify.getItemId() + 2) - 2460000)) * 2, c.getPlayer());
            c.getPlayer().getMap().broadcastMessage(CField.showMagnifyingEffect(c.getPlayer().getId(), eqq.getPosition()));
            if (!insight && src != 0x7F) {
                c.getSession().write(InventoryPacket.scrolledItem(magnify, equipped ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, toReveal, false, true, equipped));
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, magnify.getPosition(), (short) 1, false);
                System.out.println("Return 2");
            } else {
                if (price != -1 && !insight) {
                    c.getPlayer().gainMeso(-price, false);
                }
                c.getPlayer().forceReAddItem(toReveal, eqSlot >= 0 ? MapleInventoryType.EQUIP : MapleInventoryType.EQUIPPED);
                System.out.println("Return 3");
            }
            c.getSession().write(CWvsContext.enableActions());
        } else {
            c.getSession().write(InventoryPacket.getInventoryFull());
            System.out.println("Return 4");
        }
    }

    public static boolean magnifyEquip(final MapleClient c, Item magnify, Item toReveal, byte eqSlot) {
        // isn't this the same as the above method?
        final boolean insight = c.getPlayer().getTrait(MapleTraitType.sense).getLevel() >= 30;
        final Equip eqq = (Equip) toReveal;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(eqq.getItemId()) / 10;
        if (eqq.getState() == 1 && (insight || magnify.getItemId() == 2460003 || (magnify.getItemId() == 2460002 && reqLevel <= 12) || (magnify.getItemId() == 2460001 && reqLevel <= 7) || (magnify.getItemId() == 2460000 && reqLevel <= 3))) {
            final List<List<StructItemOption>> pots = new LinkedList<>(ii.getAllPotentialInfo().values());
            int lockedLine = 0;
            int locked = 0;
            for(int i = 0; i <= 2; i++){
                if(Math.abs(eqq.getPotentialByLine(i)) / 100000 > 0) {
                    lockedLine = i;
                    locked = Math.abs(eqq.getPotentialByLine(i));
                }
            }
//            if (Math.abs(eqq.getPotential1()) / 100000 > 0) {
//                lockedLine = 1;
//                locked = Math.abs(eqq.getPotential1());
//            } else if (Math.abs(eqq.getPotential2()) / 100000 > 0) {
//                lockedLine = 2;
//                locked = Math.abs(eqq.getPotential2());
//            } else if (Math.abs(eqq.getPotential3()) / 100000 > 0) {
//                lockedLine = 3;
//                locked = Math.abs(eqq.getPotential3());
//            }
            int new_state = Math.abs(eqq.getPotentialByLine(0));
            if (lockedLine == 1) {
                new_state = locked / 10000 < 1 ? 17 : 16 + locked / 10000;
            }
            if (new_state > 20 || new_state < 17) { // incase overflow
                new_state = 17;
            }
            int lines = 2; // default
            if (eqq.getPotentialByLine(2) != 0) {
                lines++;
            }
            while (eqq.getState() != new_state) {
                //31001 = haste, 31002 = door, 31003 = se, 31004 = hb, 41005 = combat orders, 41006 = advanced blessing, 41007 = speed infusion
                for (int i = 0; i < lines; i++) { // minimum 2 lines, max 3
                    boolean rewarded = false;
                    while (!rewarded) {
                        StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                        if (pot != null && pot.reqLevel / 10 <= reqLevel && GameConstants.optionTypeFits(pot.optionType, eqq.getItemId()) && GameConstants.potentialIDFits(pot.opID, new_state, i)) { //optionType
                            //have to research optionType before making this truely official-like
                            if (isAllowedPotentialStat(eqq, pot.opID)) {
                                eqq.setPotentialByLine(i, pot.opID);
                                rewarded = true;
                            }
                        }
                    }
                }
            }
            if(lockedLine >= 0){
                eqq.setPotentialByLine(lockedLine, Math.abs(locked - lockedLine * 100000));
            }
//            switch (lockedLine) {
//                case 1:
//                    eqq.setPotential1(Math.abs(locked - lockedLine * 100000));
//                    break;
//                case 2:
//                    eqq.setPotential2(Math.abs(locked - lockedLine * 100000));
//                    break;
//                case 3:
//                    eqq.setPotential3(Math.abs(locked - lockedLine * 100000));
//                    break;
//            }
            c.getPlayer().getTrait(MapleTraitType.insight).addExp((insight ? 10 : ((magnify.getItemId() + 2) - 2460000)) * 2, c.getPlayer());
            c.getPlayer().getMap().broadcastMessage(CField.showMagnifyingEffect(c.getPlayer().getId(), eqq.getPosition()));
            if (!insight) {
                c.getSession().write(InventoryPacket.scrolledItem(magnify, eqSlot >= 0 ? MapleInventoryType.EQUIP : MapleInventoryType.EQUIPPED, toReveal, false, true, false));
            } else {
                c.getPlayer().forceReAddItem(toReveal, eqSlot >= 0 ? MapleInventoryType.EQUIP : MapleInventoryType.EQUIPPED);
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAllowedPotentialStat(Equip eqq, int opID) { //For now
        //if (GameConstants.isWeapon(eqq.getItemId())) {
        //    return !(opID > 60000) || (opID >= 1 && opID <= 4) || (opID >= 9 && opID <= 12) || (opID >= 10001 && opID <= 10006) || (opID >= 10011 && opID <= 10012) || (opID >= 10041 && opID <= 10046) || (opID >= 10051 && opID <= 10052) || (opID >= 10055 && opID <= 10081) || (opID >= 10201 && opID <= 10291) || (opID >= 210001 && opID <= 20006) || (opID >= 20011 && opID <= 20012) || (opID >= 20041 && opID <= 20046) || (opID >= 20051 && opID <= 20052) || (opID >= 20055 && opID <= 20081) || (opID >= 20201 && opID <= 20291) || (opID >= 30001 && opID <= 30006) || (opID >= 30011 && opID <= 30012) || (opID >= 30041 && opID <= 30046) || (opID >= 30051 && opID <= 30052) || (opID >= 30055 && opID <= 30081) || (opID >= 30201 && opID <= 30291) || (opID >= 40001 && opID <= 40006) || (opID >= 40011 && opID <= 40012) || (opID >= 40041 && opID <= 40046) || (opID >= 40051 && opID <= 40052) || (opID >= 40055 && opID <= 40081) || (opID >= 40201 && opID <= 40291);
        //}
        return opID < 60000;
    }

    public static boolean UseUpgradeScroll(final short slot, final short dst, final short ws, final MapleClient c, final MapleCharacter chr, final boolean legendarySpirit) {
        return UseUpgradeScroll(slot, dst, ws, c, chr, 0, legendarySpirit);
    }

    public static boolean UseUpgradeScroll(final short slot, final short dst, final short ws, final MapleClient c, final MapleCharacter chr, final int vegas, final boolean legendarySpirit) {
        boolean whiteScroll = false; // white scroll being used?
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        chr.setScrolledPosition((short) 0);
        if ((ws & 2) == 2) {
            whiteScroll = true;
        }
        Equip toScroll = null;
        if (dst < 0) {
            toScroll = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        } else /*if (legendarySpirit)*/ {//may want to create a boolean for strengthen ui? lol
            toScroll = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(dst);
        }
//        if (toScroll == null || c.getPlayer().hasBlockedInventory()) {//removed just in case :P
//            c.getSession().write(CWvsContext.enableActions());
//            return false;
//        }
        
        //07 00 F5 FF 01 00 00
        final byte oldLevel = toScroll.getLevel(); //07
        final byte oldEnhance = toScroll.getEnhance(); // 00
        final byte oldState = toScroll.getState(); // F5
        final short oldFlag = toScroll.getFlag(); // FF 01
        final short oldSlots = toScroll.getUpgradeSlots(); // v146+
        

        Item scroll = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (scroll == null) {
            scroll = chr.getInventory(MapleInventoryType.CASH).getItem(slot);
            if (scroll == null) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        }
        if (scroll.getItemId() == 5064200) { //TODO: test this
            Item item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(toScroll.getPosition());
            Equip equip = (Equip) item;
            int itemid = toScroll.getItemId();
            int potential1 = equip.getPotentialByLine(0);
            int potential2 = equip.getPotentialByLine(1);
            int potential3 = equip.getPotentialByLine(2);
            int bonuspotential1 = equip.getBonusPotentialByLine(0);
            int bonuspotential2 = equip.getBonusPotentialByLine(1);
            short position = toScroll.getPosition();
            chr.getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            Equip neweq = (Equip) ii.getEquipById(itemid);
            neweq.setPotentialByLine(0, potential1);
            neweq.setPotentialByLine(1, potential2);
            neweq.setPotentialByLine(2, potential3);
            neweq.setBonusPotentialByLine(0, bonuspotential1);
            neweq.setBonusPotentialByLine(1, bonuspotential2);
            neweq.setPosition(position);
            MapleInventoryManipulator.addbyItem(c, neweq);
        }
        if (GameConstants.isAzwanScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() < MapleItemInformationProvider.getInstance().getEquipStats(scroll.getItemId()).get("tuc")) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        }
        if (!GameConstants.isSpecialScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isPotentialScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() < 1) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        } else if (GameConstants.isEquipScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() >= 1 || toScroll.getEnhance() >= 100 || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        } else if (GameConstants.isPotentialScroll(scroll.getItemId())) {
            final boolean isEpic = scroll.getItemId() / 100 == 20497 && scroll.getItemId() < 2049750;
            final boolean isUnique = scroll.getItemId() / 100 == 20497 && scroll.getItemId() >= 2049750;
            if ((!isEpic && !isUnique && toScroll.getState() >= 1) || (isEpic && toScroll.getState() >= 18) || (isUnique && toScroll.getState() >= 19) || (toScroll.getLevel() == 0 && toScroll.getUpgradeSlots() == 0 && toScroll.getItemId() / 10000 != 135/* && !isEpic && !isUnique*/) || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        } else if (GameConstants.isSpecialScroll(scroll.getItemId())) {
            if (ii.isCash(toScroll.getItemId()) || toScroll.getEnhance() >= 12) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        }
        if (!GameConstants.canScroll(toScroll.getItemId()) && !GameConstants.isChaosScroll(toScroll.getItemId())) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if ((GameConstants.isCleanSlate(scroll.getItemId()) || GameConstants.isTablet(scroll.getItemId()) || GameConstants.isGeneralScroll(scroll.getItemId()) || GameConstants.isChaosScroll(scroll.getItemId())) && (vegas > 0 || ii.isCash(toScroll.getItemId()))) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if (GameConstants.isTablet(scroll.getItemId()) && toScroll.getDurability() < 0) { //not a durability item
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        } else if ((!GameConstants.isTablet(scroll.getItemId()) && !GameConstants.isPotentialScroll(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isSpecialScroll(scroll.getItemId()) && !GameConstants.isChaosScroll(scroll.getItemId())) && toScroll.getDurability() >= 0) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        Item wscroll = null;

        // Anti cheat and validation
        List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
        if (scrollReqs != null && scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }

        if (whiteScroll) {
            wscroll = chr.getInventory(MapleInventoryType.USE).findById(2340000);
            if (wscroll == null) {
                whiteScroll = false;
            }
        }
        if (GameConstants.isTablet(scroll.getItemId()) || GameConstants.isGeneralScroll(scroll.getItemId())) {
            switch (scroll.getItemId() % 1000 / 100) {
                case 0: //1h
                    if (GameConstants.isTwoHanded(toScroll.getItemId()) || !GameConstants.isWeapon(toScroll.getItemId())) {
                        c.getSession().write(CWvsContext.enableActions());
                        return false;
                    }
                    break;
                case 1: //2h
                    if (!GameConstants.isTwoHanded(toScroll.getItemId()) || !GameConstants.isWeapon(toScroll.getItemId())) {
                        c.getSession().write(CWvsContext.enableActions());
                        return false;
                    }
                    break;
                case 2: //armor
                    if (GameConstants.isAccessory(toScroll.getItemId()) || GameConstants.isWeapon(toScroll.getItemId())) {
                        c.getSession().write(CWvsContext.enableActions());
                        return false;
                    }
                    break;
                case 3: //accessory
                    if (!GameConstants.isAccessory(toScroll.getItemId()) || GameConstants.isWeapon(toScroll.getItemId())) {
                        c.getSession().write(CWvsContext.enableActions());
                        return false;
                    }
                    break;
            }
        } else if (!GameConstants.isAccessoryScroll(scroll.getItemId()) && !GameConstants.isChaosScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isPotentialScroll(scroll.getItemId()) && !GameConstants.isSpecialScroll(scroll.getItemId())) {
            if (!ii.canScroll(scroll.getItemId(), toScroll.getItemId())) {
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        }
        if (GameConstants.isAccessoryScroll(scroll.getItemId()) && !GameConstants.isAccessory(toScroll.getItemId())) {
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if (scroll.getQuantity() <= 0) {
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }

        if (legendarySpirit && vegas == 0) {
            if (chr.getSkillLevel(SkillFactory.getSkill(PlayerStats.getSkillByJob(1003, chr.getJob()))) <= 0) {
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        }

        // Scroll Success/ Failure/ Curse
        Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, scroll, whiteScroll, chr, vegas);
        ScrollResult scrollSuccess;
        if (scrolled == null) {
            if (ItemFlag.SHIELD_WARD.check(oldFlag)) {
                scrolled = toScroll;
                scrollSuccess = Equip.ScrollResult.FAIL;
                scrolled.setFlag((short) (oldFlag - ItemFlag.SHIELD_WARD.getValue()));
            } else {
                scrollSuccess = Equip.ScrollResult.CURSE;
            }
        } else if ((scroll.getItemId() / 100 == 20497 && scrolled.getState() == 1) || scrolled.getLevel() > oldLevel || scrolled.getEnhance() > oldEnhance || scrolled.getState() > oldState || scrolled.getFlag() > oldFlag) {
            scrollSuccess = Equip.ScrollResult.SUCCESS;
        } else if ((GameConstants.isCleanSlate(scroll.getItemId()) && scrolled.getUpgradeSlots() > oldSlots)) {
            scrollSuccess = Equip.ScrollResult.SUCCESS;
        } else if (c.getPlayer().isGM()) {
            scrollSuccess = Equip.ScrollResult.SUCCESS;
        } else {
            scrollSuccess = Equip.ScrollResult.FAIL;
        }
        // Update
        chr.getInventory(GameConstants.getInventoryType(scroll.getItemId())).removeItem(scroll.getPosition(), (short) 1, false);
        if (whiteScroll) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, wscroll.getPosition(), (short) 1, false, false);
        } else if (scrollSuccess == Equip.ScrollResult.FAIL && scrolled.getUpgradeSlots() < oldSlots && c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5640000) != null) {
            chr.setScrolledPosition(scrolled.getPosition());
            if (vegas == 0) {
                c.getSession().write(CWvsContext.pamSongUI());
            }
        }

        if (scrollSuccess == Equip.ScrollResult.CURSE) {
            c.getSession().write(InventoryPacket.scrolledItem(scroll, MapleInventoryType.EQUIP, toScroll, true, false, false));
            if (dst < 0) {
                chr.getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            } else {
                chr.getInventory(MapleInventoryType.EQUIP).removeItem(toScroll.getPosition());
            }
        } else if (vegas == 0) {
            c.getSession().write(InventoryPacket.scrolledItem(scroll, MapleInventoryType.EQUIP, scrolled, false, false, false));
        }

        chr.getMap().broadcastMessage(chr, CField.getScrollEffect(c.getPlayer().getId(), scrollSuccess, legendarySpirit, toScroll.getItemId(), scroll.getItemId()), vegas == 0);
        //toscroll
        //scroll
        c.getSession().write(CField.enchantResult(scrollSuccess == ScrollResult.SUCCESS ? 1 : scrollSuccess == ScrollResult.CURSE ? 2 : 0));
        //addToScrollLog(chr.getAccountID(), chr.getId(), scroll.getItemId(), itemID, oldSlots, (byte)(scrolled == null ? -1 : scrolled.getUpgradeSlots()), oldVH, scrollSuccess.name(), whiteScroll, legendarySpirit, vegas);
        // equipped item was scrolled and changed
        if (dst < 0 && (scrollSuccess == Equip.ScrollResult.SUCCESS || scrollSuccess == Equip.ScrollResult.CURSE) && vegas == 0) {
            chr.equipChanged();
        }
        return true;
    }

    public static boolean UseSkillBook(final byte slot, final int itemId, final MapleClient c, final MapleCharacter chr) {
        final Item toUse = chr.getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || chr.hasBlockedInventory()) {
            return false;
        }
        final Map<String, Integer> skilldata = MapleItemInformationProvider.getInstance().getEquipStats(toUse.getItemId());
        if (skilldata == null) { // Hacking or used an unknown item
            return false;
        }
        boolean canuse = false, success = false;
        int skill = 0, maxlevel = 0;

        final Integer SuccessRate = skilldata.get("success");
        final Integer ReqSkillLevel = skilldata.get("reqSkillLevel");
        final Integer MasterLevel = skilldata.get("masterLevel");

        byte i = 0;
        Integer CurrentLoopedSkillId;
        while (true) {
            CurrentLoopedSkillId = skilldata.get("skillid" + i);
            i++;
            if (CurrentLoopedSkillId == null || MasterLevel == null) {
                break; // End of data
            }
            final Skill CurrSkillData = SkillFactory.getSkill(CurrentLoopedSkillId);
            if (CurrSkillData != null && CurrSkillData.canBeLearnedBy(chr.getJob()) && (ReqSkillLevel == null || chr.getSkillLevel(CurrSkillData) >= ReqSkillLevel) && chr.getMasterLevel(CurrSkillData) < MasterLevel) {
                canuse = true;
                if (SuccessRate == null || Randomizer.nextInt(100) <= SuccessRate) {
                    success = true;
                    chr.changeSingleSkillLevel(CurrSkillData, chr.getSkillLevel(CurrSkillData), (byte) (int) MasterLevel);
                } else {
                    success = false;
                }
                MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(itemId), slot, (short) 1, false);
                break;
            }
        }

        c.getPlayer().getMap().broadcastMessage(CWvsContext.useSkillBook(chr, skill, maxlevel, canuse, success));
        c.getSession().write(CWvsContext.enableActions());
        return canuse;
    }

    public static void UseExpPotion(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        //slea: [F5 4F D6 2E] [60 00] [F4 06 22 00]
        System.err.println("eror");
        slea.skip(4); // update tick
        final byte slot = (byte) slea.readShort();
        int itemid = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1
                || toUse.getItemId() != itemid || chr.getLevel() >= 250
                || chr.hasBlockedInventory() || itemid / 10000 != 223) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (itemid != 2230004) { //for now
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        int level = chr.getLevel();
        chr.gainExp(chr.getNeededExp() - chr.getExp(), true, true, false);
        boolean first = false;
        boolean last = false;
        int potionDstLevel = 18;
        if (!chr.getInfoQuest(7985).contains("2230004=")) {
            first = true;
        } else {
            if (chr.getInfoQuest(7985).equals("2230004=" + potionDstLevel + "#384")) {
                last = true;
            }
        }
        c.getSession().write(CWvsContext.updateExpPotion(last ? 0 : 2, chr.getId(), itemid, first, level, potionDstLevel));
        if (first) {
            chr.updateInfoQuest(7985, "2230004=" + level + "#384");
        }
        if (last) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static void UseAbyssScroll(final LittleEndianAccessor slea, final MapleClient c) {
    	slea.skip(4); // update tick
        final byte scroll = (byte) slea.readShort();
        final byte equip = (byte) slea.readShort();
        slea.readByte(); //idk
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(scroll);
        final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(equip);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (toUse.getItemId() / 100 != 20485 && toUse.getItemId() / 100 != 20486
                || !GameConstants.isEquip(item.getItemId())
                || !ii.getEquipStats(toUse.getItemId()).containsKey("success")
                || !ii.getEquipStats(item.getItemId()).containsKey("reqLevel")) {
            System.out.println("error1 abyss scroll " + toUse.getItemId());
            c.getSession().write(CField.enchantResult(0));
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (item == null) {
            c.getSession().write(CField.enchantResult(0));
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final Integer success = ii.getEquipStats(toUse.getItemId()).get("success");
        if (success == null || Randomizer.nextInt(100) <= success) {
            final Equip eq = (Equip) item;
            if (toUse.getItemId() / 100 == 20485) {
                if (eq.getYggdrasilWisdom() > 0) {
                    System.out.println("error2 abyss scroll " + toUse.getItemId());
                    c.getSession().write(CField.enchantResult(0));
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
                int minLevel = 0;
                int maxLevel = 0;
                if (eq.getItemId() >= 2048500 && eq.getItemId() < 2048504) {
                    minLevel = 120;
                    maxLevel = 200;
                } else if (eq.getItemId() >= 2048504 && eq.getItemId() < 2048508) {
                    minLevel = 70;
                    maxLevel = 120;
                }
                int level = ii.getEquipStats(eq.getItemId()).get("reqLevel");
                if (level < minLevel || level > maxLevel) {
                    System.out.println("error3 abyss scroll " + toUse.getItemId());
                    c.getSession().write(CField.enchantResult(0));
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
                int stat = (eq.getItemId() % 10) + 1;
                if (stat > 4) {
                    stat -= 4;
                }
                eq.setYggdrasilWisdom((byte) stat);
                if (stat == 1) {
                    eq.setStr((short) (eq.getStr() + 3));
                } else if (stat == 2) {
                    eq.setDex((short) (eq.getDex() + 3));
                } else if (stat == 3) {
                    eq.setInt((short) (eq.getInt() + 3));
                } else if (stat == 4) {
                    eq.setLuk((short) (eq.getLuk() + 3));
                }
            } else if (toUse.getItemId() / 100 == 20486) {
                eq.setFinalStrike(true);
            }
            c.getSession().write(CField.enchantResult(1));
        } else {
            c.getSession().write(CField.enchantResult(0));
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static void UseCarvedSeal(final LittleEndianAccessor slea, final MapleClient c) {
        //slea: [90 64 C8 14] [04 00] [0F 00]
    	slea.skip(4); // update tick
        final short seal = slea.readShort();
        final short equip = slea.readShort();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(seal);
        final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(equip);
        if (toUse.getItemId() / 100 != 20495
                || MapleItemInformationProvider.getInventoryType(item.getItemId()) != MapleInventoryType.EQUIP
                || MapleItemInformationProvider.getInstance().getEquipStats(toUse.getItemId()).containsKey("success")) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final Integer success = MapleItemInformationProvider.getInstance().getEquipStats(toUse.getItemId()).get("success");
        if (success == null || Randomizer.nextInt(100) <= success) {
            if (item != null) {
                final Equip eq = (Equip) item;
                if (eq.getState() < 17) {
                    c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
                    return;
                }
                if (eq.getBonusPotentialByLine(2) != 0) {
                    c.getPlayer().dropMessage(5, "Cannot be used on this item.");
                    return;
                }
                int lines = 2; // default
                if (eq.getBonusPotentialByLine(2) != 0) {
                    lines++;
                }
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final List<List<StructItemOption>> pots = new LinkedList<>(ii.getAllPotentialInfo().values());
                final int reqLevel = ii.getReqLevel(eq.getItemId()) / 10;
                int new_state = Math.abs(eq.getBonusPotentialByLine(0));
                if (new_state > 20 || new_state < 17) { // incase overflow
                    new_state = 17;
                }
                while (eq.getStateByPotential(eq.getBonusPotential()) != new_state) {
                    //31001 = haste, 31002 = door, 31003 = se, 31004 = hb, 41005 = combat orders, 41006 = advanced blessing, 41007 = speed infusion
                    for (int i = 0; i < lines; i++) { // minimum 2 lines, max 3
                        boolean rewarded = false;
                        while (!rewarded) {
                            StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                            if (pot != null && pot.reqLevel / 10 <= reqLevel && GameConstants.optionTypeFits(pot.optionType, eq.getItemId()) && GameConstants.potentialIDFits(pot.opID, new_state, i)) { //optionType
                                if (isAllowedPotentialStat(eq, pot.opID)) {
                                    eq.setBonusPotentialByLine(i, pot.opID);
                                    rewarded = true;
                                }
                            }
                        }
                    }
                }
                c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, toUse.getItemId()));
                c.getSession().write(InventoryPacket.scrolledItem(toUse, MapleInventoryType.EQUIP, item, false, true, false));
                c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
            }
        } else {
            c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), false, toUse.getItemId()));
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static void UseCube(LittleEndianAccessor slea, MapleClient c) {
        //[47 80 12 04] [0B 00] [03 00]
    	slea.skip(4); // update tick
        final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(slea.readShort());
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slea.readShort());
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (toUse.getItemId() / 10000 != 271 || item == null || toUse == null
                || c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1
                || ii.getEquipStats(toUse.getItemId()).containsKey("success")) {
            c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), false, item.getItemId()));
            c.getSession().write(CField.enchantResult(0));
            return;
        }
        final Equip eq = (Equip) item;
        if (eq.getState() >= 17 && eq.getState() <= 20) {
            eq.renewPotential_OLD(0, 0, 0, false);
            c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, item.getItemId()));
            c.getSession().write(InventoryPacket.scrolledItem(toUse, MapleInventoryType.EQUIP, item, false, true, false));
            c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
            MapleInventoryManipulator.addById(c, 2430112, (short) 1, "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
            c.getSession().write(CField.enchantResult(1));
            c.getSession().write(CWvsContext.enableActions());
        } else {
            c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
        }
    }

    public static void UseCatchItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
    	slea.skip(4); // update tick
        c.getPlayer().setScrolledPosition((short) 0);
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt();
        final MapleMonster mob = chr.getMap().getMonsterByOid(slea.readInt());
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        final MapleMap map = chr.getMap();

        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mob != null && !chr.hasBlockedInventory() && itemid / 10000 == 227 && MapleItemInformationProvider.getInstance().getCardMobId(itemid) == mob.getId()) {
            if (!MapleItemInformationProvider.getInstance().isMobHP(itemid) || mob.getHp() <= mob.getMobMaxHp() / 2) {
                map.broadcastMessage(MobPacket.catchMonster(mob.getObjectId(), itemid, (byte) 1));
                map.killMonster(mob, chr, true, false, (byte) 1);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false, false);
                if (MapleItemInformationProvider.getInstance().getCreateId(itemid) > 0) {
                    MapleInventoryManipulator.addById(c, MapleItemInformationProvider.getInstance().getCreateId(itemid), (short) 1, "Catch item " + itemid + " on " + FileoutputUtil.CurrentReadable_Date());
                }
            } else {
                map.broadcastMessage(MobPacket.catchMonster(mob.getObjectId(), itemid, (byte) 0));
                c.getSession().write(CWvsContext.catchMob(mob.getId(), itemid, (byte) 0));
            }
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseMountFood(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
    	slea.skip(4); // update tick
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt(); //2260000 usually
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        final MapleMount mount = chr.getMount();

        if (itemid / 10000 == 226 && toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mount != null && !c.getPlayer().hasBlockedInventory()) {
            final int fatigue = mount.getFatigue();

            boolean levelup = false;
            mount.setFatigue((byte) -30);

            if (fatigue > 0) {
                mount.increaseExp();
                final int level = mount.getLevel();
                if (level < 30 && mount.getExp() >= GameConstants.getMountExpNeededForLevel(level + 1)) {
                    mount.setLevel((byte) (level + 1));
                    levelup = true;
                }
            }
            chr.getMap().broadcastMessage(CWvsContext.updateMount(chr, levelup));
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.getSession().write(CWvsContext.enableActions());
    }
    
    private static int[] dmgskinitem = {2431965, 2431966, 2432084, 2431967, 2432131, 2432153, 2432638, 2432659, 2432154, 2432637, 2432658, 2432207, 2432354, 2432355, 2432972, 2432465, 2432479, 2432526, 2432639, 2432660, 2432532, 2432592, 2432640, 2432661, 2432710, 2432836, 2432973};
    private static int[] dmgskinnum = {0, 1, 1, 2, 3, 4, 4, 4, 5, 5, 5, 6, 7, 8, 8, 9, 10, 11, 11, 11, 12, 13, 14, 14, 15, 16, 17};

    
    

    public static void UseScriptedNPCItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
    	slea.skip(4); // update tick
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
        long expiration_days = 0;
        int mountid = 0;
        //int npc = MapleItemInformationProvider.getInstance().getEquipStats(itemId).get("npc").intValue();
        //String script = MapleItemInformationProvider.getInstance().getEquipStats(itemId).get("script").toString();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int npc = 9010000; // for now
        String script = "consume_" + itemId; // for now

       
        
        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && !chr.hasBlockedInventory() && !chr.inPVP()) {
            switch (toUse.getItemId()) {
                case 2430007: { // Blank Compass
                    final MapleInventory inventory = chr.getInventory(MapleInventoryType.SETUP);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);

                    if (inventory.countById(3994102) >= 20 // Compass Letter "North"
                            && inventory.countById(3994103) >= 20 // Compass Letter "South"
                            && inventory.countById(3994104) >= 20 // Compass Letter "East"
                            && inventory.countById(3994105) >= 20) { // Compass Letter "West"
                        MapleInventoryManipulator.addById(c, 2430008, (short) 1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date()); // Gold Compass
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994102, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994103, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994104, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994105, 20, false, false);
                    } else {
                        MapleInventoryManipulator.addById(c, 2430007, (short) 1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date()); // Blank Compass
                    }
                    NPCScriptManager.getInstance().start(c, 2084001, null);
                    break;
                }
                
case 2431935: { 
                    Integer[] itemArray = {2290000,
                        2290002, 2290004, 2290006, 2290008, 2290010, 2290012, 2290014, 2290016, 2290018, 2290019, 2290020, 2290022, 2290024, 2290026, 2290028, 
                        2290030, 2290032,2290034,2290036, 2290038, 2290040, 2290042, 2290044, 2290046, 2290048, 2290050, 2290052, 2290054, 2290056, 2290058, 2290060, 
                        2290062, 2290064, 2290066, 2290068, 2290070, 2290072, 2290074, 2290076, 2290078, 2290080, 2290082, 2290084, 2290086, 2290088, 
                        2290090, 2290092, 2290094, 2290096, 2290097, 2290099, 2290101, 2290102, 2290104, 2290106, 2291022, 2291013, 2291011, 2291010, 2291008, 2291007, 
                        2291005, 2291003, 2291002, 2291000, 2290998, 2290995, 2290996, 2290993, 2290979, 2290977, 2290975, 2290973, 2290971, 2290969, 2290967, 
                        2290966, 2290964, 2290962, 2290960, 2290959, 2290957, 2290955, 2290953, 2290951, 2290949, 2290947, 2290945, 2290943, 2290941, 
                        2290939, 2290928, 2290798, 2290808, 2290809, 2290810, 2290811, 2290812, 2290813, 2290814, 2290815, 2290816, 2290817, 2290818, 
                        2290819, 2290820, 2290821, 2290822, 2290823, 2290824, 2290825, 2290826, 2290827, 2290828, 2290829, 2290830, 2290831, 2290832, 
                        2290833, 2290834, 2290835, 2290836, 2290837, 2290838, 2290839, 2290840, 2290841, 2290842, 2290843, 2290844, 2290845, 2290846, 
                        2290847, 2290848, 2290849, 2290850, 2290851, 2290852, 2290853, 2290854, 2290855, 2290856, 2290857, 2290858, 2290859, 2290860, 
                        2290861, 2290862, 2290863, 2290864, 2290865, 2290866, 2290867, 2290794, 2290795, 2290796, 2290791, 2290792, 2290789, 2290787, 
                        2290777, 2290775, 2290773, 2290771, 2290769, 2290767, 2290765, 2290751, 2290752, 2290753, 2290763, 2290749, 2290747, 2290745, 
                        2290743, 2290741, 2290739, 2290734, 2290732, 2290729, 2290730, 2290727, 2290725, 2290706, 2290707, 2290704, 2290689, 2290689, 
                        2290651, 2290649, 2290647, 2290645, 2290639, 2290641, 2290637, 2290635, 2290633, 2290631, 2290629, 2290627, 2290624, 2290625, 
                        2290622, 2290619, 2290620, 2290617, 2290615, 2290612, 2290613, 2290610, 2290608, 2290599, 2290597, 2290595, 2290593, 2290591, 
                        2290580, 2290589, 2290523, 2290521, 2290519, 2290516, 2290517, 2290514, 2290512, 2290446, 2290445, 2290443, 2290441, 2290438, 2290439, 
                        2290436, 2290434, 2290432, 2290430, 2290426, 2290427, 2290428, 2290424, 2290422, 2290420, 2290418, 2290416, 2290414, 2290412, 
                        2290378, 2290370, 2290366, 2290363, 2290364, 
                        2290361, 2290358, 2290359, 2290356, 2290349, 2290354, 2290333, 2290331, 2290328, 2290329, 2290326, 2290324, 2290322, 2290292, 2290290, 
                        2290284, 2290281, 2290282, 2290279, 2290277, 2290275, 2290246, 2290244, 2290242, 2290240, 2290238, 2290236, 2290234, 2290232, 
                        2290230, 2290228, 2290226, 2290206, 2290204, 2290205, 2290153, 2290154, 2290155, 2290156, 2290151, 2290150, 2290148, 2290146, 2290144, 2290142, 2290140, 2290138, 2290136, 2290134, 2290132, 2290130, 2290128, 2290126, 2290123, 2290124, 2290121, 2290119, 2290117, 2290115, 2290114, 2290112, 2290110};         
                    int randomizer = Randomizer.nextInt(itemArray.length);
                    int reward = itemArray[randomizer];
                        if (MapleItemInformationProvider.getInstance().itemExists(reward)) {
                            MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);
                            MapleInventoryManipulator.addById(c, reward, (short) 1, "Reward item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                        }
                break;
                }
                
                case 2430121: {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300166), c.getPlayer().getPosition());
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    break;
                }
                case 2430112: //miracle cube fragment
                    if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430112) >= 25) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049400, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 25, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049400, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430112) >= 10) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049400, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 10, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049401, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 10 Fragments for a Potential Scroll, 25 for Advanced Potential Scroll.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430481: //super miracle cube fragment
                    if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430481) >= 30) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049701, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 30, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049701, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430481) >= 20) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049300, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 20, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049300, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 20 Fragments for a Advanced Equip Enhancement Scroll, 30 for Epic Potential Scroll 80%.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430691: // nebulite diffuser fragment
                    if (c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430691) >= 10) {
                            if (MapleInventoryManipulator.checkSpace(c, 5750001, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 10, true, false)) {
                                MapleInventoryManipulator.addById(c, 5750001, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 10 Fragments for a Nebulite Diffuser.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430748: // premium fusion ticket 
                    if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430748) >= 20) {
                            if (MapleInventoryManipulator.checkSpace(c, 4420000, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 20, true, false)) {
                                MapleInventoryManipulator.addById(c, 4420000, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 20 Fragments for a Premium Fusion Ticket.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430692: // nebulite box
                    if (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430692) >= 1) {
                            final int rank = Randomizer.nextInt(100) < 30 ? (Randomizer.nextInt(100) < 4 ? 2 : 1) : 0;
 //                           final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                            final List<StructItemOption> pots = new LinkedList<>(ii.getAllSocketInfo(rank).values());
                            int newId = 0;
                            while (newId == 0) {
                                StructItemOption pot = pots.get(Randomizer.nextInt(pots.size()));
                                if (pot != null) {
                                    newId = pot.opID;
                                }
                            }
                            if (MapleInventoryManipulator.checkSpace(c, newId, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, newId, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                                c.getSession().write(InfoPacket.getShowItemGain(newId, (short) 1, true));
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "You do not have a Nebulite Box.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 5680019: {//starling hair 
                    //if (c.getPlayer().getGender() == 1) {
                    int hair = 32150 + (c.getPlayer().getHair() % 10);
                    c.getPlayer().setHair(hair);
                    c.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (byte) 1, false);
                    //}
                    break;
                }
                case 5680020: {//starling hair 
                    //if (c.getPlayer().getGender() == 0) {
                    int hair = 32160 + (c.getPlayer().getHair() % 10);
                    c.getPlayer().setHair(hair);
                    c.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (byte) 1, false);
                    //}
                    break;
                }
                case 3994225:
                    c.getPlayer().dropMessage(5, "Please bring this item to the NPC.");
                    break;
                case 2430212: //energy drink
                    MapleQuestStatus marr = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.ENERGY_DRINK));
                    if (marr.getCustomData() == null) {
                        marr.setCustomData("0");
                    }
                    long lastTime = Long.parseLong(marr.getCustomData());
                    if (lastTime + (600000) > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                    } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 5);
                    }
                    break;
                case 2430213: //energy drink
                    marr = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.ENERGY_DRINK));
                    if (marr.getCustomData() == null) {
                        marr.setCustomData("0");
                    }
                    lastTime = Long.parseLong(marr.getCustomData());
                    if (lastTime + (600000) > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                    } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 10);
                    }
                    break;
                case 2430220: //energy drink
                case 2430214: //energy drink
                    if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 30);
                    }
                    break;
                case 2430227: //energy drink
                    if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 50);
                    }
                    break;
                case 2430231: //energy drink
                    marr = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.ENERGY_DRINK));
                    if (marr.getCustomData() == null) {
                        marr.setCustomData("0");
                    }
                    lastTime = Long.parseLong(marr.getCustomData());
                    if (lastTime + (600000) > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                    } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 40);
                    }
                    break;
                case 2430144: //smb
                    final int itemid = Randomizer.nextInt(999) + 2290000;
                    if (MapleItemInformationProvider.getInstance().itemExists(itemid) && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Special") && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Event")) {
                        MapleInventoryManipulator.addById(c, itemid, (short) 1, "Reward item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    }
                    break;
                case 2430370:
                    if (MapleInventoryManipulator.checkSpace(c, 2028062, (short) 1, "")) {
                        MapleInventoryManipulator.addById(c, 2028062, (short) 1, "Reward item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    }
                    break;
                case 2430158: //lion king
                    if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000630) >= 100) {
                            if (MapleInventoryManipulator.checkSpace(c, 4310010, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, 4310010, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000630) >= 50) {
                            if (MapleInventoryManipulator.checkSpace(c, 4310009, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, 4310009, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "The corrupted power of the medal is too strong. To purify the medal, you need at least #r50#k #bPurification Totems#k.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430159:
                    MapleQuest.getInstance(3182).forceComplete(c.getPlayer(), 2161004);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    break;
                case 2430200: //thunder stone
                    if (c.getPlayer().getQuestStatus(31152) != 2) {
                        c.getPlayer().dropMessage(5, "You have no idea how to use it.");
                    } else {
                        if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                            if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000660) >= 1 && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000661) >= 1 && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000662) >= 1 && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000663) >= 1) {
                                if (MapleInventoryManipulator.checkSpace(c, 4032923, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false) && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000660, 1, true, false) && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000661, 1, true, false) && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000662, 1, true, false) && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000663, 1, true, false)) {
                                    MapleInventoryManipulator.addById(c, 4032923, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                                } else {
                                    c.getPlayer().dropMessage(5, "Please make some space.");
                                }
                            } else {
                                c.getPlayer().dropMessage(5, "There needs to be 1 of each Stone for a Dream Key.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "Please make some space.");
                        }
                    }
                    break;
                case 2430130:
                    if (GameConstants.isResistance(c.getPlayer().getJob())) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().gainExp(20000 + (c.getPlayer().getLevel() * 50 * c.getChannelServer().getExpRate(c.getPlayer().getWorld())), true, true, false);
                    } else {
                        c.getPlayer().dropMessage(5, "You may not use this item.");
                    }
                    break;
                case 2430131: //energy charge
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    c.getPlayer().gainExp(20000 + (c.getPlayer().getLevel() * 50 * c.getChannelServer().getExpRate(c.getPlayer().getWorld())), true, true, false);
                    break;
                case 2430132:
                case 2430134: //resistance box
                    if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getJob() == 3200 || c.getPlayer().getJob() == 3210 || c.getPlayer().getJob() == 3211 || c.getPlayer().getJob() == 3212) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                            MapleInventoryManipulator.addById(c, 1382101, (short) 1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                        } else if (c.getPlayer().getJob() == 3300 || c.getPlayer().getJob() == 3310 || c.getPlayer().getJob() == 3311 || c.getPlayer().getJob() == 3312) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                            MapleInventoryManipulator.addById(c, 1462093, (short) 1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                        } else if (c.getPlayer().getJob() == 3500 || c.getPlayer().getJob() == 3510 || c.getPlayer().getJob() == 3511 || c.getPlayer().getJob() == 3512) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                            MapleInventoryManipulator.addById(c, 1492080, (short) 1, "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                        } else {
                            c.getPlayer().dropMessage(5, "You may not use this item.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Make some space.");
                    }
                    break;
                case 2430182:
                    //TODO:Fix it
                    break;
                case 2430218:
                case 2430230:
                case 2430473:
                case 2430479:
                case 2430632:
                case 2430697:
                case 2430979:
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    c.getPlayer().gainExp(GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) - c.getPlayer().getExp(), true, true, false);
                    break;
                case 2430036: //croco 1 day
                    mountid = 1027;
                    expiration_days = 1;
                    break;
                case 2430170: //croco 7 day
                    mountid = 1027;
                    expiration_days = 7;
                    break;
                case 2430037: //black scooter 1 day
                    mountid = 1028;
                    expiration_days = 1;
                    break;
                case 2430038: //pink scooter 1 day
                    mountid = 1029;
                    expiration_days = 1;
                    break;
                case 2430039: //clouds 1 day
                    mountid = 1030;
                    expiration_days = 1;
                    break;
                case 2430040: //balrog 1 day
                    mountid = 1031;
                    expiration_days = 1;
                    break;
                case 2430223: //balrog 1 day
                    mountid = 1031;
                    expiration_days = 15;
                    break;
                case 2430259: //balrog 1 day
                    mountid = 1031;
                    expiration_days = 3;
                    break;
                case 2430242: //motorcycle
                    mountid = 80001018;
                    expiration_days = 10;
                    break;
                case 2430243: //power suit
                    mountid = 80001019;
                    expiration_days = 10;
                    break;
                case 2430261: //power suit
                    mountid = 80001019;
                    expiration_days = 3;
                    break;
                case 2430249: //motorcycle
                    mountid = 80001027;
                    expiration_days = 3;
                    break;
                case 2430225: //balrog 1 day
                    mountid = 1031;
                    expiration_days = 10;
                    break;
                case 2430053: //croco 30 day
                    mountid = 1027;
                    expiration_days = 1;
                    break;
                case 2430054: //black scooter 30 day
                    mountid = 1028;
                    expiration_days = 30;
                    break;
                case 2430055: //pink scooter 30 day
                    mountid = 1029;
                    expiration_days = 30;
                    break;
                case 2430257: //pink
                    mountid = 1029;
                    expiration_days = 7;
                    break;
                case 2430056: //mist rog 30 day
                    mountid = 1035;
                    expiration_days = 30;
                    break;
                case 2430057:
                    mountid = 1033;
                    expiration_days = 30;
                    break;
                case 2430072: //ZD tiger 7 day
                    mountid = 1034;
                    expiration_days = 7;
                    break;
                case 2430073: //lion 15 day
                    mountid = 1036;
                    expiration_days = 15;
                    break;
                case 2430074: //unicorn 15 day
                    mountid = 1037;
                    expiration_days = 15;
                    break;
                case 2430272: //low rider 15 day
                    mountid = 1038;
                    expiration_days = 3;
                    break;
                case 2430275: //spiegelmann
                    mountid = 80001033;
                    expiration_days = 7;
                    break;
                case 2430075: //low rider 15 day
                    mountid = 1038;
                    expiration_days = 15;
                    break;
                case 2430076: //red truck 15 day
                    mountid = 1039;
                    expiration_days = 15;
                    break;
                case 2430077: //gargoyle 15 day
                    mountid = 1040;
                    expiration_days = 15;
                    break;
                case 2430080: //shinjo 20 day
                    mountid = 1042;
                    expiration_days = 20;
                    break;
                case 2430082: //orange mush 7 day
                    mountid = 1044;
                    expiration_days = 7;
                    break;
                case 2430260: //orange mush 7 day
                    mountid = 1044;
                    expiration_days = 3;
                    break;
                case 2430091: //nightmare 10 day
                    mountid = 1049;
                    expiration_days = 10;
                    break;
                case 2430092: //yeti 10 day
                    mountid = 1050;
                    expiration_days = 10;
                    break;
                case 2430263: //yeti 10 day
                    mountid = 1050;
                    expiration_days = 3;
                    break;
                case 2430093: //ostrich 10 day
                    mountid = 1051;
                    expiration_days = 10;
                    break;
                case 2430101: //pink bear 10 day
                    mountid = 1052;
                    expiration_days = 10;
                    break;
                case 2430102: //transformation robo 10 day
                    mountid = 1053;
                    expiration_days = 10;
                    break;
                case 2430103: //chicken 30 day
                    mountid = 1054;
                    expiration_days = 30;
                    break;
                case 2430266: //chicken 30 day
                    mountid = 1054;
                    expiration_days = 3;
                    break;
                case 2430265: //chariot
                    mountid = 1151;
                    expiration_days = 3;
                    break;
                case 2430258: //law officer
                    mountid = 1115;
                    expiration_days = 365;
                    break;
                case 2430117: //lion 1 year
                    mountid = 1036;
                    expiration_days = 365;
                    break;
                case 2430118: //red truck 1 year
                    mountid = 1039;
                    expiration_days = 365;
                    break;
                case 2430119: //gargoyle 1 year
                    mountid = 1040;
                    expiration_days = 365;
                    break;
                case 2430120: //unicorn 1 year
                    mountid = 1037;
                    expiration_days = 365;
                    break;
                case 2430271: //owl 30 day
                    mountid = 1069;
                    expiration_days = 3;
                    break;
                case 2430136: //owl 30 day
                    mountid = 1069;
                    expiration_days = 30;
                    break;
                case 2430137: //owl 1 year
                    mountid = 1069;
                    expiration_days = 365;
                    break;
                case 2430145: //mothership
                    mountid = 1070;
                    expiration_days = 30;
                    break;
                case 2430146: //mothership
                    mountid = 1070;
                    expiration_days = 365;
                    break;
                case 2430147: //mothership
                    mountid = 1071;
                    expiration_days = 30;
                    break;
                case 2430148: //mothership
                    mountid = 1071;
                    expiration_days = 365;
                    break;
                case 2430135: //os4
                    mountid = 1065;
                    expiration_days = 15;
                    break;
                case 2430149: //leonardo 30 day
                    mountid = 1072;
                    expiration_days = 30;
                    break;
                case 2430262: //leonardo 30 day
                    mountid = 1072;
                    expiration_days = 3;
                    break;
                case 2430179: //witch 15 day
                    mountid = 1081;
                    expiration_days = 15;
                    break;
                case 2430264: //witch 15 day
                    mountid = 1081;
                    expiration_days = 3;
                    break;
                case 2430201: //giant bunny 60 day
                    mountid = 1096;
                    expiration_days = 60;
                    break;
                case 2430228: //tiny bunny 60 day
                    mountid = 1101;
                    expiration_days = 60;
                    break;
                case 2430276: //tiny bunny 60 day
                    mountid = 1101;
                    expiration_days = 15;
                    break;
                case 2430277: //tiny bunny 60 day
                    mountid = 1101;
                    expiration_days = 365;
                    break;
                case 2430283: //trojan
                    mountid = 1025;
                    expiration_days = 10;
                    break;
                case 2430291: //hot air
                    mountid = 1145;
                    expiration_days = -1;
                    break;
                case 2430293: //nadeshiko
                    mountid = 1146;
                    expiration_days = -1;
                    break;
                case 2430295: //pegasus
                    mountid = 1147;
                    expiration_days = -1;
                    break;
                case 2430297: //dragon
                    mountid = 1148;
                    expiration_days = -1;
                    break;
                case 2430299: //broom
                    mountid = 1149;
                    expiration_days = -1;
                    break;
                case 2430301: //cloud
                    mountid = 1150;
                    expiration_days = -1;
                    break;
                case 2430303: //chariot
                    mountid = 1151;
                    expiration_days = -1;
                    break;
                case 2430305: //nightmare
                    mountid = 1152;
                    expiration_days = -1;
                    break;
                case 2430307: //rog
                    mountid = 1153;
                    expiration_days = -1;
                    break;
                case 2430309: //mist rog
                    mountid = 1154;
                    expiration_days = -1;
                    break;
                case 2430311: //owl
                    mountid = 1156;
                    expiration_days = -1;
                    break;
                case 2430313: //helicopter
                    mountid = 1156;
                    expiration_days = -1;
                    break;
                case 2430315: //pentacle
                    mountid = 1118;
                    expiration_days = -1;
                    break;
                case 2430317: //frog
                    mountid = 1121;
                    expiration_days = -1;
                    break;
                case 2430319: //turtle
                    mountid = 1122;
                    expiration_days = -1;
                    break;
                case 2430321: //buffalo
                    mountid = 1123;
                    expiration_days = -1;
                    break;
                case 2430323: //tank
                    mountid = 1124;
                    expiration_days = -1;
                    break;
                case 2430325: //viking
                    mountid = 1129;
                    expiration_days = -1;
                    break;
                case 2430327: //pachinko
                    mountid = 1130;
                    expiration_days = -1;
                    break;
                case 2430329: //kurenai
                    mountid = 1063;
                    expiration_days = -1;
                    break;
                case 2430331: //horse
                    mountid = 1025;
                    expiration_days = -1;
                    break;
                case 2430333: //tiger
                    mountid = 1034;
                    expiration_days = -1;
                    break;
                case 2430335: //hyena
                    mountid = 1136;
                    expiration_days = -1;
                    break;
                case 2430337: //ostrich
                    mountid = 1051;
                    expiration_days = -1;
                    break;
                case 2430339: //low rider
                    mountid = 1138;
                    expiration_days = -1;
                    break;
                case 2430341: //napoleon
                    mountid = 1139;
                    expiration_days = -1;
                    break;
                case 2430343: //croking
                    mountid = 1027;
                    expiration_days = -1;
                    break;
                case 2430346: //lovely
                    mountid = 1029;
                    expiration_days = -1;
                    break;
                case 2430348: //retro
                    mountid = 1028;
                    expiration_days = -1;
                    break;
                case 2430350: //f1
                    mountid = 1033;
                    expiration_days = -1;
                    break;
                case 2430352: //power suit
                    mountid = 1064;
                    expiration_days = -1;
                    break;
                case 2430354: //giant rabbit
                    mountid = 1096;
                    expiration_days = -1;
                    break;
                case 2430356: //small rabit
                    mountid = 1101;
                    expiration_days = -1;
                    break;
                case 2430358: //rabbit rickshaw
                    mountid = 1102;
                    expiration_days = -1;
                    break;
                case 2430360: //chicken
                    mountid = 1054;
                    expiration_days = -1;
                    break;
                case 2430362: //transformer
                    mountid = 1053;
                    expiration_days = -1;
                    break;
                case 2430292: //hot air
                    mountid = 1145;
                    expiration_days = 90;
                    break;
                case 2430294: //nadeshiko
                    mountid = 1146;
                    expiration_days = 90;
                    break;
                case 2430296: //pegasus
                    mountid = 1147;
                    expiration_days = 90;
                    break;
                case 2430298: //dragon
                    mountid = 1148;
                    expiration_days = 90;
                    break;
                case 2430300: //broom
                    mountid = 1149;
                    expiration_days = 90;
                    break;
                case 2430302: //cloud
                    mountid = 1150;
                    expiration_days = 90;
                    break;
                case 2430304: //chariot
                    mountid = 1151;
                    expiration_days = 90;
                    break;
                case 2430306: //nightmare
                    mountid = 1152;
                    expiration_days = 90;
                    break;
                case 2430308: //rog
                    mountid = 1153;
                    expiration_days = 90;
                    break;
                case 2430310: //mist rog
                    mountid = 1154;
                    expiration_days = 90;
                    break;
                case 2430312: //owl
                    mountid = 1156;
                    expiration_days = 90;
                    break;
                case 2430314: //helicopter
                    mountid = 1156;
                    expiration_days = 90;
                    break;
                case 2430316: //pentacle
                    mountid = 1118;
                    expiration_days = 90;
                    break;
                case 2430318: //frog
                    mountid = 1121;
                    expiration_days = 90;
                    break;
                case 2430320: //turtle
                    mountid = 1122;
                    expiration_days = 90;
                    break;
                case 2430322: //buffalo
                    mountid = 1123;
                    expiration_days = 90;
                    break;
                case 2430326: //viking
                    mountid = 1129;
                    expiration_days = 90;
                    break;
                case 2430328: //pachinko
                    mountid = 1130;
                    expiration_days = 90;
                    break;
                case 2430330: //kurenai
                    mountid = 1063;
                    expiration_days = 90;
                    break;
                case 2430332: //horse
                    mountid = 1025;
                    expiration_days = 90;
                    break;
                case 2430334: //tiger
                    mountid = 1034;
                    expiration_days = 90;
                    break;
                case 2430336: //hyena
                    mountid = 1136;
                    expiration_days = 90;
                    break;
                case 2430338: //ostrich
                    mountid = 1051;
                    expiration_days = 90;
                    break;
                case 2430340: //low rider
                    mountid = 1138;
                    expiration_days = 90;
                    break;
                case 2430342: //napoleon
                    mountid = 1139;
                    expiration_days = 90;
                    break;
                case 2430344: //croking
                    mountid = 1027;
                    expiration_days = 90;
                    break;
                case 2430347: //lovely
                    mountid = 1029;
                    expiration_days = 90;
                    break;
                case 2430349: //retro
                    mountid = 1028;
                    expiration_days = 90;
                    break;
                case 2430351: //f1
                    mountid = 1033;
                    expiration_days = 90;
                    break;
                case 2430353: //power suit
                    mountid = 1064;
                    expiration_days = 90;
                    break;
                case 2430355: //giant rabbit
                    mountid = 1096;
                    expiration_days = 90;
                    break;
                case 2430357: //small rabit
                    mountid = 1101;
                    expiration_days = 90;
                    break;
                case 2430359: //rabbit rickshaw
                    mountid = 1102;
                    expiration_days = 90;
                    break;
                case 2430361: //chicken
                    mountid = 1054;
                    expiration_days = 90;
                    break;
                case 2430363: //transformer
                    mountid = 1053;
                    expiration_days = 90;
                    break;
                case 2430324: //high way
                    mountid = 1158;
                    expiration_days = -1;
                    break;
                case 2430345: //high way
                    mountid = 1158;
                    expiration_days = 90;
                    break;
                case 2430367: //law off
                    mountid = 1115;
                    expiration_days = 3;
                    break;
                case 2430365: //pony
                    mountid = 1025;
                    expiration_days = 365;
                    break;
                case 2430366: //pony
                    mountid = 1025;
                    expiration_days = 15;
                    break;
                case 2430369: //nightmare
                    mountid = 1049;
                    expiration_days = 10;
                    break;
                case 2430392: //speedy
                    mountid = 80001038;
                    expiration_days = 90;
                    break;
                case 2430476: //red truck? but name is pegasus?
                    mountid = 1039;
                    expiration_days = 15;
                    break;
                case 2430477: //red truck? but name is pegasus?
                    mountid = 1039;
                    expiration_days = 365;
                    break;
                case 2430232: //fortune
                    mountid = 1106;
                    expiration_days = 10;
                    break;
                case 2430511: //spiegel
                    mountid = 80001033;
                    expiration_days = 15;
                    break;
                case 2430512: //rspiegel
                    mountid = 80001033;
                    expiration_days = 365;
                    break;
                case 2430536: //buddy buggy
                    mountid = 80001114;
                    expiration_days = 365;
                    break;
                case 2430537: //buddy buggy
                    mountid = 80001114;
                    expiration_days = 15;
                    break;
                case 2430229: //bunny rickshaw 60 day
                    mountid = 1102;
                    expiration_days = 60;
                    break;
                case 2430199: //santa sled
                    mountid = 1102;
                    expiration_days = 60;
                    break;
                case 2430206: //race
                    mountid = 1089;
                    expiration_days = 7;
                    break;
                case 2430211: //race
                    mountid = 80001009;
                    expiration_days = 30;
                    break;
                case 2430611: {
                    NPCScriptManager.getInstance().start(c, 9010010, "consume_2430611");
                    break;
                }
                case 2430690: {
                    if (c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() >= 1 && c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= 1) {
                        if (Randomizer.nextInt(100) < 30) { //30% for Hilla's Pet
                            if (MapleInventoryManipulator.checkSpace(c, 5000217, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, 5000217, (short) 1, "", MaplePet.createPet(5000217, "Blackheart", 1, 0, 100, MapleInventoryIdentifier.getInstance(), 0, (short) 0), 45, false, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(0, "Please make more space");
                            }
                        } else { //70% for Hilla's Pet's earrings
                            if (MapleInventoryManipulator.checkSpace(c, 1802354, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, 1802354, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(0, "Please make more space");
                            }
                        }
                    } else {
                        c.getPlayer().dropMessage(0, "Please make more space");
                    }
                    break;
                }
                case 2431855: {//First Explorer Gift Box
                    if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= 2 && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 2) {
                        if (MapleInventoryManipulator.checkSpace(c, 1052646, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                            MapleInventoryManipulator.addById(c, 1052646, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            MapleInventoryManipulator.addById(c, 1072850, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            MapleInventoryManipulator.addById(c, 2000013, (short) 50, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            MapleInventoryManipulator.addById(c, 2000014, (short) 50, "Scripted item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                        } else {
                            c.getPlayer().dropMessage(0, "Please make more space");
                        }
                    } else {
                        c.getPlayer().dropMessage(0, "Please make more space");
                    }
                    break;
                }
                case 2432251: {//Kobold Musk
                    MapleQuest.getInstance(59051).forceComplete(c.getPlayer(), 0);
                    NPCScriptManager.getInstance().start(c, 9390315, "BeastTamerQuestLine2");
                    break;
                }
                
                
                //damageskins
                case 2431965: // base damage Skin: 0                 
                 case 2431966: // digital Sunrise Skin Damage: 1
                 case 2432084: // digital Sunrise damage the skin (no)
                 case 2431967: // Critias Skin Damage: 2
                 case 2432131: // Party Quest Skin Damage: 3
                 case 2432153: // Creative Impact Damage Skin: 4
                 case 2432638: // Creative Impact Damage Skin
                 case 2432659: // Creative Impact Damage Skin
                 case 2432154: // sweet traditional Han Skin Damage: 5
                 case 2432637: // sweet traditional one and damage the skin
                 case 2432658: // sweet traditional one and damage the skin
                 case 2432207: // Club Hennessy's damage Skin: 6
                 case 2432354: // Merry Christmas Skin Damage: 7
                 case 2432355: // Snow Blossom Skin Damage: 8
                 case 2432972: // Snow Blossom Skin Damage
                 case 2432465: // damage the skin of Alicia: 9
                 case 2432479: // Dorothy skin damage: 10
                 case 2432526: // Keyboard Warrior Skin Damage: 11
                 case 2432639: // Keyboard Warrior Skin Damage
                 case 2432660: // Keyboard Warrior Skin Damage
                 case 2432532: // spring breeze rustling skin damage: 12
                 case 2432592: // solo troops skin damage: 13
                 case 2432640: // Remy You Suns skin damage: 14
                 case 2432661: // Remy you damage the skin Suns
                 case 2432710: // Orange Mushroom Skin Damage: 15
                 case 2432836: // crown damage Skin: 16
                 case 2432973: // monotone skin damage: 17
                /*
                case 2431965: //ê¸°ë³¸ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 0
                case 2431966: //ë�?�?ì§€í„¸ë�¼ì�´ì¦ˆ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 1
                case 2432084: //ë�?�?ì§€í„¸ë�¼ì�´ì¦ˆ ë�°ë¯¸ì§€ ìŠ¤í‚¨ (no)
                case 2431967: //í�¬ë¦¬í‹°ì•„ìŠ¤ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 2
                case 2432131: //íŒŒí‹° í€˜ìŠ¤íŠ¸ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 3
                case 2432153: //ìž„íŒ©í‹°ë¸Œ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 4
                case 2432638: //ìž„íŒ©í‹°ë¸Œ ë�°ë¯¸ì§€ ìŠ¤í‚¨
                case 2432659: //ìž„íŒ©í‹°ë¸Œ ë�°ë¯¸ì§€ ìŠ¤í‚¨
                case 2432154: //ë‹¬ì½¤í•œ ì „í†µ í•œê³¼ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 5
                case 2432637: //ë‹¬ì½¤í•œ ì „í†µ í•œê³¼ ë�°ë¯¸ì§€ ìŠ¤í‚¨
                case 2432658: //ë‹¬ì½¤í•œ ì „í†µ í•œê³¼ ë�°ë¯¸ì§€ ìŠ¤í‚¨
                case 2432207: //í�´ëŸ½ í—¤ë„¤ì‹œìŠ¤ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 6
                case 2432354: //ë©�?ë¦¬ í�¬ë¦¬ìŠ¤ë§ˆìŠ¤ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 7
                case 2432355: //ëˆˆ ê½ƒì†¡ì�´ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 8
                case 2432972: //ëˆˆ ê½ƒì†¡ì�´ ë�°ë¯¸ì§€ ìŠ¤í‚¨
                case 2432465: //ì•Œë¦¬ìƒ¤ì�˜ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 9
                case 2432479: //ë�„ë¡œì‹œì�˜ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 10
                case 2432526: //í‚¤ë³´ë“œ ì›Œë¦¬ì–´ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 11
                case 2432639: //í‚¤ë³´ë“œ ì›Œë¦¬ì–´ ë�°ë¯¸ì§€ ìŠ¤í‚¨
                case 2432660: //í‚¤ë³´ë“œ ì›Œë¦¬ì–´ ë�°ë¯¸ì§€ ìŠ¤í‚¨
                case 2432532: //ì‚´ëž‘ì‚´ëž‘ ë´„ë°�?ëžŒ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 12
                case 2432592: //ì†�?ë¡œë¶€ëŒ€ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 13
                case 2432640: //ë ˆë¯¸ë„ˆì„ ìŠ¤ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 14
                case 2432661: //ë ˆë¯¸ë„ˆì„ ìŠ¤ ë�°ë¯¸ì§€ ìŠ¤í‚¨
                case 2432710: //ì£¼í™©ë²„ì„¯ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 15
                case 2432836: //ì™•ê´€ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 16
                case 2432973: //ëª¨ë…¸í†¤ ë�°ë¯¸ì§€ ìŠ¤í‚¨ : 17
                    */
                 {
                    if (!GameConstants.isZero(chr.getJob())) {
                        int itemidd = toUse.getItemId();
                        MapleQuest quest = MapleQuest.getInstance(7291);
                        MapleQuestStatus queststatus = new MapleQuestStatus(quest, (byte) 1);
                        int skinnum = GameConstants.getDamageSkinNumberByItem(itemidd);
                        String skinString = String.valueOf(skinnum);
                        queststatus.setCustomData(skinString == null ? "0" : skinString);
                        c.getPlayer().updateQuest(queststatus);
                        c.getSession().write(CWvsContext.showQuestMsg("Skin damage has been changed"));
                        
                        chr.getMap().broadcastMessage(chr, CWvsContext.showForeignDamageSkin(chr, skinnum), false);
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    } else {
                         c.getSession().write(CWvsContext.showQuestMsg("Zero occupation can not get used to damage the skin also has no effect"));
                       
                    }
                    break;
                }
                case 2435719:
                case 2435902:
                    //
                    MapleInventory mi = chr.getInventory(MapleInventoryType.USE);
                    Item item = mi.getItem(slot);
                    if(item != null && (item.getItemId() == 2435902 || item.getItemId() == 2435719)) {
                        List<Integer> skillIds = new LinkedList<>();
                        if(Randomizer.isSuccess(GameConstants.OWN_JOB_V_SKILL_CHANCE)){
                            skillIds.addAll(SkillFactory.getVSkillsByJob(chr.getJob()));
                        }else if(Randomizer.isSuccess(GameConstants.BEGINNER_V_SKILL_CHANCE)){
                            skillIds.addAll(SkillFactory.getBeginnerVSkills());
                        }else{
                            skillIds.addAll(SkillFactory.getAllVSkills());
                        }
                        // could use an array
                        int skillid1 = skillIds.get(Randomizer.nextInt(skillIds.size()));
                        int skillid2 = 0, skillid3 = 0;
                        Skill skill = SkillFactory.getSkill(skillid1);
                        int iconid = skill.getvSkillIconID();
                        // not the most beautiful of code, but meh.
                        if(skill.getVskill() == 2){
                            skillIds.clear();
                            int randomJob = skill.getBoostjob();
                            List<Integer> randomSkills = SkillFactory.getVSkillsByJob(randomJob);
                            skillIds.addAll(randomSkills);
                            int toBeRemoved = 0;
                            for(Integer i : skillIds){
                                if(i/10000 % 10 != 0){
                                    toBeRemoved = i;
                                }
                            }
                            skillIds.remove(new Integer(toBeRemoved));
                            skillIds.remove(new Integer(skillid1));
                            skillid2 = skillIds.get(Randomizer.nextInt(skillIds.size()));
                            skillIds.remove(new Integer(skillid2));
                            skillid3 = skillIds.get(Randomizer.nextInt(skillIds.size()));
                        }
                        VMatrixRecord entry = new VMatrixRecord(iconid, skillid1, skillid2, skillid3);
                        chr.addVMatrixRecord(entry);
                        c.getSession().write(CWvsContext.nodestoneResult(iconid, entry.getSkillID1(), entry.getSkillID2(), entry.getSkillID3()));
                        chr.removeItem(itemId, 1);
                        chr.dropMessage(6, "You used a Nodestone and got a new Node.");
//                        Map<MapleStat, Long> statMap = new HashMap<>();
//                        statMap.put(MapleStat.SKIN, (long) 0);
//                        c.getSession().write(CWvsContext.updatePlayerStats(statMap, chr)); // SendCharacterStat(1, 0); ?
                        c.getSession().write(CField.itemEffect(chr.getId(), itemId));
                        c.getSession().write(CWvsContext.updateVMatrix(chr.getVMatrixRecords()));
                    }else{
                        chr.dropMessage(6, "You tried to open a nodestone without having a nodestone.");
                        // TODO Anti-cheat measures? Like account flag for example
                    }
                    break;
                
                
                //
                
                case 5680021:
                    int expiration;
                    final int chance = Randomizer.nextInt(100);
                    if (chance < 25) {
                        expiration = -1;
                    } else if (chance < 45) {
                        expiration = 90;
                    } else {
                        expiration = 30;
                    }
                    //int chair = GameConstants.chairGachapon();
                    //MapleInventoryManipulator.addById(c, chair, (short) 1, null, null, expiration, "Chair Gachapon");
                    break;
                default:
                    NPCScriptManager.getInstance().startItemScript(c, npc, script); //maple admin as default npc
                    break;
            }
        }
        if (mountid > 0) {
            mountid = PlayerStats.getSkillByJob(mountid, c.getPlayer().getJob());
            final int fk = GameConstants.getMountItem(mountid, c.getPlayer());
            if (fk > 0 && mountid < 80001000) { //TODO Jump
                for (int i = 80001001; i < 80001999; i++) {
                    final Skill skill = SkillFactory.getSkill(i);
                    if (skill != null && GameConstants.getMountItem(skill.getId(), c.getPlayer()) == fk) {
                        mountid = i;
                        break;
                    }
                }
            }
            if (c.getPlayer().getSkillLevel(mountid) > 0) {
                c.getPlayer().dropMessage(5, "You already have this skill.");
            } else if (SkillFactory.getSkill(mountid) == null || GameConstants.getMountItem(mountid, c.getPlayer()) == 0) {
                c.getPlayer().dropMessage(5, "The skill could not be gained.");
            } else if (expiration_days > 0) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(mountid), (byte) 1, (byte) 1, System.currentTimeMillis() + expiration_days * 24 * 60 * 60 * 1000);
                c.getPlayer().dropMessage(5, "The skill has been attained.");
            }
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static void ResetCoreAura(int slot, MapleClient c, MapleCharacter chr) {
        Item starDust = chr.getInventory(MapleInventoryType.USE).getItem((byte) slot);
        if ((starDust == null) || (c.getPlayer().hasBlockedInventory())) {
            c.getSession().write(CWvsContext.InventoryPacket.getInventoryFull());
        }
    }

    public static final void useInnerCirculator(LittleEndianAccessor slea, MapleClient c) {
        System.out.println("Circ used");
        int itemid = slea.readInt();
        System.out.println("ItemID Int");
        short slot = (short) slea.readInt();
        System.out.println("slot Int");
        Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (item.getItemId() == itemid) {
            List<InnerSkillValueHolder> newValues = new LinkedList<>();
            int i = 0;
            for (InnerSkillValueHolder isvh : c.getPlayer().getInnerSkills()) {
                if (!isvh.isLocked()) {
                    if (i == 0 && c.getPlayer().getInnerSkills().size() > 1 && itemid == 2702000) { //Ultimate Circulator
                        newValues.add(InnerAbillity.getInstance().renewSkill(isvh.getRank(), itemid, true, false));
                    } else {
                        newValues.add(InnerAbillity.getInstance().renewSkill(isvh.getRank(), itemid, false, false));
                    }
                    //c.getPlayer().changeSkillLevel(SkillFactory.getSkill(isvh.getSkillId()), (byte) 0, (byte) 0);
                } else {
                    newValues.add(isvh);
                }
                i++;
            }
            c.getPlayer().getInnerSkills().clear();
            byte ability = 1;
            for (InnerSkillValueHolder isvh : newValues) {
                c.getPlayer().getInnerSkills().add(isvh);
                c.getSession().write(CField.updateInnerPotential(ability, isvh.getSkillId(), isvh.getSkillLevel(), isvh.getRank()));
                ability++;
                //c.getPlayer().changeSkillLevel(SkillFactory.getSkill(isvh.getSkillId()), isvh.getSkillLevel(), isvh.getSkillLevel());
            }
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);

            //c.getSession().write(CField.gameMsg("Inner Potential has been reconfigured.")); //not sure if it's working
            c.getPlayer().dropMessage(5, "Inner Potential has been reconfigured.");
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseSummonBag(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (!chr.isAlive() || chr.hasBlockedInventory() || chr.inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        slea.skip(4); // update tick
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && (c.getPlayer().getMapId() < 910000000 || c.getPlayer().getMapId() > 910000022)) {
            final Map<String, Integer> toSpawn = MapleItemInformationProvider.getInstance().getEquipStats(itemId);

            if (toSpawn == null) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            MapleMonster ht = null;
            int type = 0;
            for (Entry<String, Integer> i : toSpawn.entrySet()) {
                if (i.getKey().startsWith("mob") && Randomizer.nextInt(99) <= i.getValue()) {
                    ht = MapleLifeFactory.getMonster(Integer.parseInt(i.getKey().substring(3)));
                    chr.getMap().spawnMonster_sSack(ht, chr.getPosition(), type);
                }
            }
            if (ht == null) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }

            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseTreasureChest(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final short slot = slea.readShort();
        final int itemid = slea.readInt();

        final Item toUse = chr.getInventory(MapleInventoryType.ETC).getItem((byte) slot);
        if (toUse == null || toUse.getQuantity() <= 0 || toUse.getItemId() != itemid || chr.hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        int reward;
        int keyIDforRemoval = 0;
        String box;

        switch (toUse.getItemId()) {
            case 4280000: // Gold box
                reward = RandomRewards.getGoldBoxReward();
                keyIDforRemoval = 5490000;
                box = "Gold";
                break;
            case 4280001: // Silver box
                reward = RandomRewards.getSilverBoxReward();
                keyIDforRemoval = 5490001;
                box = "Silver";
                break;
            default: // Up to no good
                return;
        }

        // Get the quantity
        int amount = 1;
        switch (reward) {
            case 2000004:
                amount = 200; // Elixir
                break;
            case 2000005:
                amount = 100; // Power Elixir
                break;
        }
        if (chr.getInventory(MapleInventoryType.CASH).countById(keyIDforRemoval) > 0) {
            final Item item = MapleInventoryManipulator.addbyId_Gachapon(c, reward, (short) amount);

            if (item == null) {
                chr.dropMessage(5, "Please check your item inventory and see if you have a Master Key, or if the inventory is full.");
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, (byte) slot, (short) 1, true);
            MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, keyIDforRemoval, 1, true, false);
            c.getSession().write(InfoPacket.getShowItemGain(reward, (short) amount, true));

            if (GameConstants.gachaponRareItem(item.getItemId()) > 0) {
                World.Broadcast.broadcastSmega(CWvsContext.getGachaponMega(c.getPlayer().getName(), " : got a(n)", c.getChannel(), item, (byte) 2, "[" + box + " Chest]"));
            }
        } else {
            chr.dropMessage(5, "Please check your item inventory and see if you have a Master Key, or if the inventory is full.");
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static final void UseCashItem(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null || c.getPlayer().inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        slea.skip(4); // update tick
        c.getPlayer().setScrolledPosition((short) 0);
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();

        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slot);
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1 || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        boolean used = false, cc = false;

        switch (itemId) {
            case 5043001: // NPC Teleport Rock
            case 5043000: { // NPC Teleport Rock
                final short questid = slea.readShort();
                final int npcid = slea.readInt();
                final MapleQuest quest = MapleQuest.getInstance(questid);

                if (c.getPlayer().getQuest(quest).getStatus() == 1 && quest.canComplete(c.getPlayer(), npcid)) {
                    final int mapId = MapleLifeFactory.getNPCLocation(npcid);
                    if (mapId != -1) {
                        final MapleMap map = c.getChannelServer().getMapFactory().getMap(mapId);
                        if (map.containsNPC(npcid) && !FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(map.getFieldLimit()) && !c.getPlayer().isInBlockedMap()) {
                            c.getPlayer().changeMap(map, map.getPortal(0));
                        }
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "Unknown error has occurred.");
                    }
                }
                break;
            }
            case 5041001:
            case 5040004:
            case 5040003:
            case 5040002:
            case 2320000: // The Teleport Rock
            case 5041000: // VIP Teleport Rock
            case 5040000: // The Teleport Rock
            case 5040001: { // Teleport Coke
                used = UseTeleRock(slea, c, itemId);
                break;
            }
            case 5450005: {
                c.getPlayer().setConversation(4);
                c.getPlayer().getStorage().sendStorage(c, 1022005);
                break;
            }
            case 5050000: { // AP Reset
                Map<MapleStat, Long> statupdate = new EnumMap<>(MapleStat.class);
                final int apto = (int) slea.readLong();
                final int apfrom = (int) slea.readLong();

                if (apto == apfrom) {
                    break; // Hack
                }
                final int job = c.getPlayer().getJob();
                final PlayerStats playerst = c.getPlayer().getStat();
                used = true;

                switch (apto) { // AP to
                    case 0x40: // str
                        if (playerst.getStr() >= 999) {
                            used = false;
                        }
                        break;
                    case 0x80: // dex
                        if (playerst.getDex() >= 999) {
                            used = false;
                        }
                        break;
                    case 0x100: // int
                        if (playerst.getInt() >= 999) {
                            used = false;
                        }
                        break;
                    case 0x200: // luk
                        if (playerst.getLuk() >= 999) {
                            used = false;
                        }
                        break;
                    case 0x800: // hp
                        if (playerst.getMaxHp() >= 500000) {
                            used = false;
                        }
                        break;
                    case 0x2000: // mp
                        if (playerst.getMaxMp() >= 500000) {
                            used = false;
                        }
                        break;
                }
                switch (apfrom) { // AP to
                    case 0x40: // str
                        if (playerst.getStr() <= 4 || (c.getPlayer().getJob() % 1000 / 100 == 1 && playerst.getStr() <= 35)) {
                            used = false;
                        }
                        break;
                    case 0x80: // dex
                        if (playerst.getDex() <= 4 || (c.getPlayer().getJob() % 1000 / 100 == 3 && playerst.getDex() <= 25) || (c.getPlayer().getJob() % 1000 / 100 == 4 && playerst.getDex() <= 25) || (c.getPlayer().getJob() % 1000 / 100 == 5 && playerst.getDex() <= 20)) {
                            used = false;
                        }
                        break;
                    case 0x100: // int
                        if (playerst.getInt() <= 4 || (c.getPlayer().getJob() % 1000 / 100 == 2 && playerst.getInt() <= 20)) {
                            used = false;
                        }
                        break;
                    case 0x200: // luk
                        if (playerst.getLuk() <= 4) {
                            used = false;
                        }
                        break;
                    case 0x800: // hp
                        if (/*playerst.getMaxMp() < ((c.getPlayer().getLevel() * 14) + 134) || */c.getPlayer().getHpApUsed() <= 0 || c.getPlayer().getHpApUsed() >= 10000) {
                            used = false;
                            c.getPlayer().dropMessage(1, "You need points in HP or MP in order to take points out.");
                        }
                        break;
                    case 0x2000: // mp
                        if (/*playerst.getMaxMp() < ((c.getPlayer().getLevel() * 14) + 134) || */c.getPlayer().getHpApUsed() <= 0 || c.getPlayer().getHpApUsed() >= 10000) {
                            used = false;
                            c.getPlayer().dropMessage(1, "You need points in HP or MP in order to take points out.");
                        }
                        break;
                }
                if (used) {
                    switch (apto) { // AP to
                        case 0x40: { // str
                            final long toSet = playerst.getStr() + 1;
                            playerst.setStr((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.STR, toSet);
                            break;
                        }
                        case 0x80: { // dex
                            final long toSet = playerst.getDex() + 1;
                            playerst.setDex((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.DEX, toSet);
                            break;
                        }
                        case 0x100: { // int
                            final long toSet = playerst.getInt() + 1;
                            playerst.setInt((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.INT, toSet);
                            break;
                        }
                        case 0x200: { // luk
                            final long toSet = playerst.getLuk() + 1;
                            playerst.setLuk((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.LUK, toSet);
                            break;
                        }
                        case 0x800: // hp
                            int maxhp = playerst.getMaxHp();
                            maxhp += GameConstants.getHpApByJob((short) job);
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + 1));
                            playerst.setMaxHp(maxhp, c.getPlayer());
                            statupdate.put(MapleStat.MAXHP, (long) maxhp);
                            break;

                        case 0x2000: // mp
                            int maxmp = playerst.getMaxMp();
                            if (GameConstants.isDemonSlayer(job) || GameConstants.isAngelicBuster(job) || GameConstants.isDemonAvenger(job)) {
                                break;
                            }
                            maxmp += GameConstants.getMpApByJob((short) job);
                            maxmp = Math.min(500000, Math.abs(maxmp));
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + 1));
                            playerst.setMaxMp(maxmp, c.getPlayer());
                            statupdate.put(MapleStat.MAXMP, (long) maxmp);
                            break;
                    }
                    switch (apfrom) { // AP from
                        case 0x40: { // str
                            final long toSet = playerst.getStr() - 1;
                            playerst.setStr((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.STR, toSet);
                            break;
                        }
                        case 0x80: { // dex
                            final long toSet = playerst.getDex() - 1;
                            playerst.setDex((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.DEX, toSet);
                            break;
                        }
                        case 0x100: { // int
                            final long toSet = playerst.getInt() - 1;
                            playerst.setInt((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.INT, toSet);
                            break;
                        }
                        case 0x200: { // luk
                            final long toSet = playerst.getLuk() - 1;
                            playerst.setLuk((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.LUK, toSet);
                            break;
                        }
                        case 0x800: // HP
                            int maxhp = playerst.getMaxHp();
                            maxhp -= GameConstants.getHpApByJob((short) job);
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() - 1));
                            playerst.setMaxHp(maxhp, c.getPlayer());
                            statupdate.put(MapleStat.MAXHP, (long) maxhp);
                            break;
                        case 0x2000: // MP
                            int maxmp = playerst.getMaxMp();
                            if (GameConstants.isDemonSlayer(job) || GameConstants.isAngelicBuster(job) || GameConstants.isDemonAvenger(job)) {
                                break;
                            }
                            maxmp -= GameConstants.getMpApByJob((short) job);
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() - 1));
                            playerst.setMaxMp(maxmp, c.getPlayer());
                            statupdate.put(MapleStat.MAXMP, (long) maxmp);
                            break;
                    }
                    c.getSession().write(CWvsContext.updatePlayerStats(statupdate, true, c.getPlayer()));
                }
                break;
            }
            //case 5051001: {
            //    
            //    break;
            //}
            case 5220083: {//starter pack
                used = true;
                for (Entry<Integer, StructFamiliar> f : MapleItemInformationProvider.getInstance().getFamiliars().entrySet()) {
                    if (f.getValue().itemid == 2870055 || f.getValue().itemid == 2871002 || f.getValue().itemid == 2870235 || f.getValue().itemid == 2870019) {
                        MonsterFamiliar mf = c.getPlayer().getFamiliars().get(f.getKey());
                        if (mf != null) {
                            if (mf.getVitality() >= 3) {
                                mf.setExpiry(Math.min(System.currentTimeMillis() + 90 * 24 * 60 * 60000L, mf.getExpiry() + 30 * 24 * 60 * 60000L));
                            } else {
                                mf.setVitality(mf.getVitality() + 1);
                                mf.setExpiry(mf.getExpiry() + 30 * 24 * 60 * 60000L);
                            }
                        } else {
                            mf = new MonsterFamiliar(c.getPlayer().getId(), f.getKey(), System.currentTimeMillis() + 30 * 24 * 60 * 60000L);
                            c.getPlayer().getFamiliars().put(f.getKey(), mf);
                        }
                        c.getSession().write(CField.registerFamiliar(mf));
                    }
                }
                break;
            }
            case 5220084: {//booster pack
                if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 3) {
                    c.getPlayer().dropMessage(5, "Make 3 USE space.");
                    break;
                }
                used = true;
                int[] familiars = new int[3];
                while (true) {
                    for (int i = 0; i < familiars.length; i++) {
                        if (familiars[i] > 0) {
                            continue;
                        }
                        for (Map.Entry<Integer, StructFamiliar> f : MapleItemInformationProvider.getInstance().getFamiliars().entrySet()) {
                            if (Randomizer.nextInt(500) == 0 && ((i < 2 && f.getValue().grade == 0 || (i == 2 && f.getValue().grade != 0)))) {
                                MapleInventoryManipulator.addById(c, f.getValue().itemid, (short) 1, "Booster Pack");
                                //c.getSession().write(CField.getBoosterFamiliar(c.getPlayer().getId(), f.getKey(), 0));
                                familiars[i] = f.getValue().itemid;
                                break;
                            }
                        }
                    }
                    if (familiars[0] > 0 && familiars[1] > 0 && familiars[2] > 0) {
                        break;
                    }
                }
                c.getSession().write(CSPacket.getBoosterPack(familiars[0], familiars[1], familiars[2]));
                c.getSession().write(CSPacket.getBoosterPackClick());
                c.getSession().write(CSPacket.getBoosterPackReveal());
                break;
            }
            case 5050001: // SP Reset (1st job)
            case 5050002: // SP Reset (2nd job)
            case 5050003: // SP Reset (3rd job)
            case 5050004:  // SP Reset (4th job)
            case 5050005: //evan sp resets
            case 5050006:
            case 5050007:
            case 5050008:
            case 5050009: {
                if (itemId >= 5050005 && !GameConstants.isEvan(c.getPlayer().getJob())) {
                    c.getPlayer().dropMessage(1, "This reset is only for Evans.");
                    break;
                } //well i dont really care other than this o.o
                if (itemId < 5050005 && GameConstants.isEvan(c.getPlayer().getJob())) {
                    c.getPlayer().dropMessage(1, "This reset is only for non-Evans.");
                    break;
                } //well i dont really care other than this o.o
                int skill1 = slea.readInt();
                int skill2 = slea.readInt();
                for (int i : GameConstants.blockedSkills) {
                    if (skill1 == i) {
                        c.getPlayer().dropMessage(1, "You may not add this skill.");
                        return;
                    }
                }

                Skill skillSPTo = SkillFactory.getSkill(skill1);
                Skill skillSPFrom = SkillFactory.getSkill(skill2);

                if (skillSPTo.isBeginnerSkill() || skillSPFrom.isBeginnerSkill()) {
                    c.getPlayer().dropMessage(1, "You may not add beginner skills.");
                    break;
                }
                if (GameConstants.getSkillBookForSkill(skill1) != GameConstants.getSkillBookForSkill(skill2)) { //resistance evan
                    c.getPlayer().dropMessage(1, "You may not add different job skills.");
                    break;
                }
                //if (GameConstants.getJobNumber(skill1 / 10000) > GameConstants.getJobNumber(skill2 / 10000)) { //putting 3rd job skillpoints into 4th job for example
                //    c.getPlayer().dropMessage(1, "You may not add skillpoints to a higher job.");
                //    break;
                //}
                if ((c.getPlayer().getSkillLevel(skillSPTo) + 1 <= skillSPTo.getMaxLevel()) && c.getPlayer().getSkillLevel(skillSPFrom) > 0 && skillSPTo.canBeLearnedBy(c.getPlayer().getJob())) {
                    if (skillSPTo.isFourthJob() && (c.getPlayer().getSkillLevel(skillSPTo) + 1 > c.getPlayer().getMasterLevel(skillSPTo))) {
                        c.getPlayer().dropMessage(1, "You will exceed the master level.");
                        break;
                    }
                    if (itemId >= 5050005) {
                        if (GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2 && GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2 + 1) {
                            c.getPlayer().dropMessage(1, "You may not add this job SP using this reset.");
                            break;
                        }
                    } else {
                        int theJob = GameConstants.getJobNumber(skill2 / 10000);
                        switch (skill2 / 10000) {
                            case 430:
                                theJob = 1;
                                break;
                            case 432:
                            case 431:
                                theJob = 2;
                                break;
                            case 433:
                                theJob = 3;
                                break;
                            case 434:
                                theJob = 4;
                                break;
                        }
                        if (theJob != itemId - 5050000) { //you may only subtract from the skill if the ID matches Sp reset
                            c.getPlayer().dropMessage(1, "You may not subtract from this skill. Use the appropriate SP reset.");
                            break;
                        }
                    }
                    final Map<Skill, SkillEntry> sa = new HashMap<>();
                    sa.put(skillSPFrom, new SkillEntry((byte) (c.getPlayer().getSkillLevel(skillSPFrom) - 1), c.getPlayer().getMasterLevel(skillSPFrom), SkillFactory.getDefaultSExpiry(skillSPFrom)));
                    sa.put(skillSPTo, new SkillEntry((byte) (c.getPlayer().getSkillLevel(skillSPTo) + 1), c.getPlayer().getMasterLevel(skillSPTo), SkillFactory.getDefaultSExpiry(skillSPTo)));
                    c.getPlayer().changeSkillsLevel(sa);
                    used = true;
                }
                break;
            }
            case 5500000: { // Magic Hourglass 1 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 1;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    boolean change = true;
                    for (String z : GameConstants.RESERVED) {
                        if (c.getPlayer().getName().indexOf(z) != -1 || item.getOwner().indexOf(z) != -1) {
                            change = false;
                        }
                    }
                    if (change) {
                        item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                        c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "It may not be used on this item.");
                    }
                }
                break;
            }
            case 5500001: { // Magic Hourglass 7 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 7;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    boolean change = true;
                    for (String z : GameConstants.RESERVED) {
                        if (c.getPlayer().getName().indexOf(z) != -1 || item.getOwner().indexOf(z) != -1) {
                            change = false;
                        }
                    }
                    if (change) {
                        item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                        c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "It may not be used on this item.");
                    }
                }
                break;
            }
            case 5500002: { // Magic Hourglass 20 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 20;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    boolean change = true;
                    for (String z : GameConstants.RESERVED) {
                        if (c.getPlayer().getName().indexOf(z) != -1 || item.getOwner().indexOf(z) != -1) {
                            change = false;
                        }
                    }
                    if (change) {
                        item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                        c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "It may not be used on this item.");
                    }
                }
                break;
            }
            case 5500005: { // Magic Hourglass 50 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 50;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    boolean change = true;
                    for (String z : GameConstants.RESERVED) {
                        if (c.getPlayer().getName().indexOf(z) != -1 || item.getOwner().indexOf(z) != -1) {
                            change = false;
                        }
                    }
                    if (change) {
                        item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                        c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "It may not be used on this item.");
                    }
                }
                break;
            }
            case 5500006: { // Magic Hourglass 99 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 99;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    boolean change = true;
                    for (String z : GameConstants.RESERVED) {
                        if (c.getPlayer().getName().indexOf(z) != -1 || item.getOwner().indexOf(z) != -1) {
                            change = false;
                        }
                    }
                    if (change) {
                        item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                        c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "It may not be used on this item.");
                    }
                }
                break;
            }
            case 5060000: { // Item Tag
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());

                if (item != null && item.getOwner().equals("")) {
                    boolean change = true;
                    for (String z : GameConstants.RESERVED) {
                        if (c.getPlayer().getName().indexOf(z) != -1) {
                            change = false;
                        }
                    }
                    if (change) {
                        item.setOwner(c.getPlayer().getName());
                        c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                        used = true;
                    }
                }
                break;
            }
            case 5680015: {
                if (c.getPlayer().getFatigue() > 0) {
                    c.getPlayer().setFatigue(0);
                    used = true;
                }
                break;
            }
            case 5534000: { //tims lab
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                if (item != null) {
                    final Equip eq = (Equip) item;
                    if (eq.getState() == 0) {
                        eq.resetPotential();
                        c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, itemId));
                        c.getSession().write(InventoryPacket.scrolledItem(toUse, MapleInventoryType.EQUIP, item, false, true, false));
                        c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                        c.getSession().write(CField.enchantResult(1));
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
                    }
                } else {
                    c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), false, itemId));
                    c.getSession().write(CField.enchantResult(0));
                }
                break;
            }
            case 5062009:
            case 5062000: { //miracle cube
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                    final Equip eq = (Equip) item;
                    if (eq.getState() >= 17 && eq.getState() != 20) {
                        boolean potLock = c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5067000) != null;
                        int line = potLock ? slea.readInt() : 0;
                        int toLock = potLock ? slea.readShort() : 0;
                        potLock = checkPotentialLock(c.getPlayer(), eq, line, toLock);
                        if (potLock) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5067000).getPosition(), (short) 1, false);
                        }
                        eq.renewPotential_OLD(0, line, toLock, false);
                        c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, itemId));
                        c.getSession().write(InventoryPacket.scrolledItem(toUse, MapleInventoryType.EQUIP, item, false, true, false));
                        c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                        MapleCharacter chr = c.getPlayer();
                        int tofind = 0;
                        if (chr.itemQuantity(2460003) > 0) {
                            tofind = 2460003;
                        } else if (chr.itemQuantity(2460002) > 0) {
                            tofind = 2460002;
                        } else if (chr.itemQuantity(2460001) > 0) {
                            tofind = 2460001;
                        } else if (chr.itemQuantity(2460000) > 0) {
                            tofind = 2460000;
                        }
                        if (tofind != 0) {
                            Item magnify = c.getPlayer().getInventory(MapleInventoryType.USE).findById(tofind);
                            if (magnifyEquip(c, magnify, item, (byte) item.getPosition())) {
                                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, magnify.getPosition(), (short) 1, false);
                                c.getSession().write(CField.getGameMessage("A Magnifying Glass (Premium) has been used.", (short) 7));
                            } else {
                                c.getSession().write(CField.getGameMessage("A Magnifying Glass was not found. The equipment will stay as Hidden Potential.", (short) 7));
                            }
                        } else {
                            c.getSession().write(CField.getGameMessage("A Magnifying Glass was not found. The equipment will stay as Hidden Potential.", (short) 7));
                        }
                        MapleInventoryManipulator.addById(c, 2430112, (short) 1, "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
                        c.getSession().write(CField.enchantResult(tofind == 0 ? 1 : 1));//3
                        c.getSession().write(CWvsContext.enableActions());
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
                    }
                } else {
                    c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), false, itemId));
                    c.getSession().write(CField.enchantResult(0));
                }
                break;
            }
            case 5062100:
            case 5062001: { //premium cube
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(1, "You may not use this until level 10.");
                } else {
                    final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                    if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                        final Equip eq = (Equip) item;
                        if (eq.getState() >= 17 && eq.getState() != 20) {
                            eq.renewPotential_OLD(1, 0, (short) 0, false);
                            c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, itemId));
                            c.getSession().write(InventoryPacket.scrolledItem(toUse, MapleInventoryType.EQUIP, item, false, true, false));
                            c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                            MapleCharacter chr = c.getPlayer();
                            int tofind = 0;
                            if (chr.itemQuantity(2460003) > 0) {
                                tofind = 2460003;
                            } else if (chr.itemQuantity(2460002) > 0) {
                                tofind = 2460002;
                            } else if (chr.itemQuantity(2460001) > 0) {
                                tofind = 2460001;
                            } else if (chr.itemQuantity(2460000) > 0) {
                                tofind = 2460000;
                            }
                            if (tofind != 0) {
                                Item magnify = c.getPlayer().getInventory(MapleInventoryType.USE).findById(tofind);
                                if (magnifyEquip(c, magnify, item, (byte) item.getPosition())) {
                                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, magnify.getPosition(), (short) 1, false);
                                    c.getSession().write(CField.getGameMessage("A Magnifying Glass (Premium) has been used.", (short) 7));
                                } else {
                                    c.getSession().write(CField.getGameMessage("A Magnifying Glass was not found. The equipment will stay as Hidden Potential.", (short) 7));
                                }
                            } else {
                                c.getSession().write(CField.getGameMessage("A Magnifying Glass was not found. The equipment will stay as Hidden Potential.", (short) 7));
                            }
                            MapleInventoryManipulator.addById(c, 2430112, (short) 1, "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
                            c.getSession().write(CField.enchantResult(tofind == 0 ? 1 : 1));//3
                            used = true;
                        } else {
                            c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
                        }
                    } else {
                        c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), false, itemId));
                        c.getSession().write(CField.enchantResult(0));
                    }
                }
                break;
            }
            case 5062002: { //super miracle cube
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(1, "You may not use this until level 10.");
                } else {
                    final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                    if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                        final Equip eq = (Equip) item;
                        if (eq.getState() >= 17) {
                            eq.renewPotential_OLD(3, 0, (short) 0, false);
                            c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, itemId));
                            c.getSession().write(InventoryPacket.scrolledItem(toUse, MapleInventoryType.EQUIP, item, false, true, false));
                            c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                            MapleCharacter chr = c.getPlayer();
                            int tofind = 0;
                            if (chr.itemQuantity(2460003) > 0) {
                                tofind = 2460003;
                            } else if (chr.itemQuantity(2460002) > 0) {
                                tofind = 2460002;
                            } else if (chr.itemQuantity(2460001) > 0) {
                                tofind = 2460001;
                            } else if (chr.itemQuantity(2460000) > 0) {
                                tofind = 2460000;
                            }
                            if (tofind != 0) {
                                Item magnify = c.getPlayer().getInventory(MapleInventoryType.USE).findById(tofind);
                                if (magnifyEquip(c, magnify, item, (byte) item.getPosition())) {
                                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, magnify.getPosition(), (short) 1, false);
                                    c.getSession().write(CField.getGameMessage("A Magnifying Glass (Premium) has been used.", (short) 7));
                                } else {
                                    c.getSession().write(CField.getGameMessage("A Magnifying Glass was not found. The equipment will stay as Hidden Potential.", (short) 7));
                                }
                            } else {
                                c.getSession().write(CField.getGameMessage("A Magnifying Glass was not found. The equipment will stay as Hidden Potential.", (short) 7));
                            }
                            MapleInventoryManipulator.addById(c, 2430481, (short) 1, "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
                            c.getSession().write(CField.enchantResult(tofind == 0 ? 1 : 1));//3
                            used = true;
                        } else {
                            c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
                        }
                    } else {
                        c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), false, itemId));
                        c.getSession().write(CField.enchantResult(0));
                    }
                }
                break;
            }
            case 5062003: { //revolutionary cube
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                    final Equip eq = (Equip) item;
                    if (eq.getState() >= 17) {
                        boolean potLock = c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5067000) != null;
                        int line = potLock ? slea.readInt() : 0;
                        short toLock = potLock ? slea.readShort() : 0;
                        potLock = checkPotentialLock(c.getPlayer(), eq, line, toLock);
                        if (potLock) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5067000).getPosition(), (short) 1, false);
                        }
                        eq.renewPotential_OLD(4, line, toLock, false);
                        c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, itemId));
                        c.getSession().write(InventoryPacket.scrolledItem(toUse, MapleInventoryType.EQUIP, item, false, true, false));
                        c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                        MapleCharacter chr = c.getPlayer();
                        int tofind = 0;
                        if (chr.itemQuantity(2460003) > 0) {
                            tofind = 2460003;
                        } else if (chr.itemQuantity(2460002) > 0) {
                            tofind = 2460002;
                        } else if (chr.itemQuantity(2460001) > 0) {
                            tofind = 2460001;
                        } else if (chr.itemQuantity(2460000) > 0) {
                            tofind = 2460000;
                        }
                        if (tofind != 0) {
                            Item magnify = c.getPlayer().getInventory(MapleInventoryType.USE).findById(tofind);
                            if (magnifyEquip(c, magnify, item, (byte) item.getPosition())) {
                                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, magnify.getPosition(), (short) 1, false);
                                c.getSession().write(CField.getGameMessage("A Magnifying Glass (Premium) has been used.", (short) 7));
                            } else {
                                c.getSession().write(CField.getGameMessage("A Magnifying Glass was not found. The equipment will stay as Hidden Potential.", (short) 7));
                            }
                        } else {
                            c.getSession().write(CField.getGameMessage("A Magnifying Glass was not found. The equipment will stay as Hidden Potential.", (short) 7));
                        }
                        MapleInventoryManipulator.addById(c, 2430481, (short) 1, "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
                        c.getSession().write(CField.enchantResult(tofind == 0 ? 1 : 1));//3
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
                    }
                } else {
                    c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), false, itemId));
                    c.getSession().write(CField.enchantResult(0));
                }
                break;
            }
            case 5062500:
            case 5062005: { //enlightening cube
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                    final Equip eq = (Equip) item;
                    if (eq.getState() >= 17) {
                        eq.renewPotential_OLD(5, 0, (short) 0, false);
                        c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, itemId));
                        c.getSession().write(InventoryPacket.scrolledItem(toUse, MapleInventoryType.EQUIP, item, false, true, false));
                        c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                        MapleCharacter chr = c.getPlayer();
                        int tofind = 0;
                        if (chr.itemQuantity(2460003) > 0) {
                            tofind = 2460003;
                        } else if (chr.itemQuantity(2460002) > 0) {
                            tofind = 2460002;
                        } else if (chr.itemQuantity(2460001) > 0) {
                            tofind = 2460001;
                        } else if (chr.itemQuantity(2460000) > 0) {
                            tofind = 2460000;
                        }
                        if (tofind != 0) {
                            Item magnify = c.getPlayer().getInventory(MapleInventoryType.USE).findById(tofind);
                            if (magnifyEquip(c, magnify, item, (byte) item.getPosition())) {
                                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, magnify.getPosition(), (short) 1, false);
                                c.getSession().write(CField.getGameMessage("A Magnifying Glass (Premium) has been used.", (short) 7));
                            } else {
                                c.getSession().write(CField.getGameMessage("A Magnifying Glass was not found. The equipment will stay as Hidden Potential.", (short) 7));
                            }
                        } else {
                            c.getSession().write(CField.getGameMessage("A Magnifying Glass was not found. The equipment will stay as Hidden Potential.", (short) 7));
                        }
                        MapleInventoryManipulator.addById(c, 2430759, (short) 1, "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
                        c.getSession().write(CField.enchantResult(tofind == 0 ? 1 : 1));//3
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
                    }
                } else {
                    c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), false, itemId));
                    c.getSession().write(CField.enchantResult(0));
                }
                break;
            }
            case 5062006: {
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                    final Equip eq = (Equip) item;
                    if (eq.getState() >= 17) {
                        eq.renewPotential_OLD(6, 0, (short) 0, false);
                        c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, itemId));
                        c.getSession().write(InventoryPacket.scrolledItem(toUse, MapleInventoryType.EQUIP, item, false, true, false));
                        c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                        MapleCharacter chr = c.getPlayer();
                        int tofind = 0;
                        if (chr.itemQuantity(2460003) > 0) {
                            tofind = 2460003;
                        } else if (chr.itemQuantity(2460002) > 0) {
                            tofind = 2460002;
                        } else if (chr.itemQuantity(2460001) > 0) {
                            tofind = 2460001;
                        } else if (chr.itemQuantity(2460000) > 0) {
                            tofind = 2460000;
                        }
                    if (tofind != 0) {
                        Item magnify = c.getPlayer().getInventory(MapleInventoryType.USE).findById(tofind);
                        if (magnifyEquip(c, magnify, item, (byte) item.getPosition())) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, magnify.getPosition(), (short) 1, false);
                            c.getSession().write(CField.getGameMessage("A Magnifying Glass (Premium) has been used.", (short) 7));
                        } else {
                            c.getSession().write(CField.getGameMessage("A Magnifying Glass was not found. The equipment will stay as Hidden Potential.", (short) 7));
                        }
                    } else {
                        c.getSession().write(CField.getGameMessage("A Magnifying Glass was not found. The equipment will stay as Hidden Potential.", (short) 7));
                    }
                    MapleInventoryManipulator.addById(c, 2430759, (short) 1, "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
                    c.getSession().write(CField.enchantResult(tofind == 0 ? 1 : 1));//3
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
                }
            } else {
                c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), false, itemId));
                c.getSession().write(CField.enchantResult(0));
                }
                break;
            }
            case 5062300: { //white awakening stamp
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                if (item != null) {
                    final Equip eq = (Equip) item;
                    if (eq.getState() < 17) {
                        c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
                        return;
                    }
                    if (eq.getPotentialByLine(2) != 0) {
                        c.getPlayer().dropMessage(5, "Cannot be used on this item.");
                        return;
                    }
                    final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    final List<List<StructItemOption>> pots = new LinkedList<>(ii.getAllPotentialInfo().values());
                    final int reqLevel = ii.getReqLevel(eq.getItemId()) / 10;
                    int new_state = Math.abs(eq.getPotentialByLine(0));
                    if (new_state > 20 || new_state < 17) { // incase overflow
                        new_state = 17;
                    }
                    boolean rewarded = false;
                    while (!rewarded) {
                        StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                        if (pot != null && pot.reqLevel / 10 <= reqLevel && GameConstants.optionTypeFits(pot.optionType, eq.getItemId()) && GameConstants.potentialIDFits(pot.opID, new_state, 3)) { //optionType
                            eq.setPotentialByLine(2, pot.opID);
                            rewarded = true;
                        }
                    }
                    c.getSession().write(InventoryPacket.scrolledItem(toUse, MapleInventoryType.EQUIP, item, false, true, false));
                    c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                    used = true;
                }
                break;
            }
            case 5062400:
            case 5062401:
            case 5062402:
            case 5062403: {
                short appearance = (short) slea.readInt();
                short function = (short) slea.readInt();
                Equip appear = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(appearance);
                Equip equip = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(function);
                if (equip.getFusionAnvil() != 0) {
                    return;
                } else if (GameConstants.isEquip(appear.getItemId()) || GameConstants.isEquip(equip.getItemId())) {
                    if (appear.getItemId() / 10000 != equip.getItemId() / 10000) {
                        return;
                    }
                } else if (appear.getItemId() / 100000 != equip.getItemId() / 100000) {
                    return;
                }
                equip.setFusionAnvil(appear.getItemId());
                c.getPlayer().forceReAddItem_NoUpdate(equip, MapleInventoryType.EQUIP);
                used = true;
                break;
            }
            case 5750000: { //alien cube
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(1, "You may not use this until level 10.");
                } else {
                    final Item item = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) slea.readInt());
                    if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1 && c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= 1) {
                        final int grade = GameConstants.getNebuliteGrade(item.getItemId());
                        if (grade != -1 && grade < 4) {
                            final int rank = Randomizer.nextInt(100) < 7 ? (Randomizer.nextInt(100) < 2 ? (grade + 1) : (grade != 3 ? (grade + 1) : grade)) : grade;
                            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                            final List<StructItemOption> pots = new LinkedList<>(ii.getAllSocketInfo(rank).values());
                            int newId = 0;
                            while (newId == 0) {
                                StructItemOption pot = pots.get(Randomizer.nextInt(pots.size()));
                                if (pot != null) {
                                    newId = pot.opID;
                                }
                            }
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, item.getPosition(), (short) 1, false);
                            MapleInventoryManipulator.addById(c, newId, (short) 1, "Upgraded from alien cube on " + FileoutputUtil.CurrentReadable_Date());
                            MapleInventoryManipulator.addById(c, 2430691, (short) 1, "Alien Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
                            used = true;
                        } else {
                            c.getPlayer().dropMessage(1, "Grade S Nebulite cannot be added.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "You do not have sufficient inventory slot.");
                    }
                }
                break;
            }
            case 5750001: { // socket diffuser
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(1, "You may not use this until level 10.");
                } else {
                    final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                    if (item != null) {
                        final Equip eq = (Equip) item;
                        if (eq.getSocketByNmb(0) > 0) { // first slot only.
                            eq.setSocketByNmb(0, 0);
                            c.getSession().write(InventoryPacket.scrolledItem(toUse, MapleInventoryType.EQUIP, item, false, true, false));
                            c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                            used = true;
                        } else {
                            c.getPlayer().dropMessage(5, "This item do not have a socket.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "This item's nebulite cannot be removed.");
                    }
                }
                break;
            }
            case 5521000: { // Karma
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());

                if (item != null && !ItemFlag.KARMA_ACC.check(item.getFlag())
                        && !ItemFlag.KARMA_ACC_USE.check(item.getFlag())
                        && GameConstants.isEquip(item.getItemId())
                        && ((Equip) item).getKarmaCount() != 0) {
                    Equip eq = (Equip) item;
                    if (MapleItemInformationProvider.getInstance().isShareTagEnabled(item.getItemId())) {
                        short flag = item.getFlag();
                        if (ItemFlag.UNTRADABLE.check(flag)) {
                            flag -= ItemFlag.UNTRADABLE.getValue();
                        } else if (type == MapleInventoryType.EQUIP) {
                            flag |= ItemFlag.KARMA_ACC.getValue();
                        } else {
                            flag |= ItemFlag.KARMA_ACC_USE.getValue();
                        }
                        item.setFlag(flag);
                        eq.setKarmaCount((byte) (eq.getKarmaCount() - 1));
                        c.getPlayer().forceReAddItem_NoUpdate(item, type);
                        c.getSession().write(InventoryPacket.updateSpecialItemUse(item, type.getType(), item.getPosition(), true, c.getPlayer()));
                        used = true;
                    }
                }
                break;
            }
            case 5520001: //p.karma
            case 5520000: { // Karma
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());

                if (item != null && !ItemFlag.KARMA_EQ.check(item.getFlag())
                        && !ItemFlag.KARMA_USE.check(item.getFlag())
                        && GameConstants.isEquip(item.getItemId())
                        && ((Equip) item).getKarmaCount() != 0) {
                    Equip eq = (Equip) item;
                    if ((itemId == 5520000 && MapleItemInformationProvider.getInstance().isKarmaEnabled(item.getItemId())) || (itemId == 5520001 && MapleItemInformationProvider.getInstance().isPKarmaEnabled(item.getItemId()))) {
                        short flag = item.getFlag();
                        if (ItemFlag.UNTRADABLE.check(flag)) {
                            flag -= ItemFlag.UNTRADABLE.getValue();
                        } else if (type == MapleInventoryType.EQUIP) {
                            flag |= ItemFlag.KARMA_EQ.getValue();
                        } else {
                            flag |= ItemFlag.KARMA_USE.getValue();
                        }
                        item.setFlag(flag);
                        eq.setKarmaCount((byte) (eq.getKarmaCount() - 1));
                        c.getPlayer().forceReAddItem_NoUpdate(item, type);
                        c.getSession().write(InventoryPacket.updateSpecialItemUse(item, type.getType(), item.getPosition(), true, c.getPlayer()));
                        used = true;
                    }
                }
                break;
            }
            case 5570000: { // Vicious Hammer
                slea.readInt(); // Inventory type, Hammered eq is always EQ.
                final Equip item = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
                // another int here, D3 49 DC 00
                if (item != null) {
                    if (GameConstants.canHammer(item.getItemId()) && MapleItemInformationProvider.getInstance().getSlots(item.getItemId()) > 0 && item.getViciousHammer() < 2) {
                        item.setViciousHammer((byte) (item.getViciousHammer() + 1));
                        item.setUpgradeSlots((byte) (item.getUpgradeSlots() + 1));
                        c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIP);
                        c.getSession().write(CSPacket.ViciousHammer(true, item.getViciousHammer()));
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(5, "You may not use it on this item.");
                        c.getSession().write(CSPacket.ViciousHammer(true, (byte) 0));
                    }
                }
                break;
            }
            case 5610001:
            case 5610000: { // Vega 30
                slea.readInt(); // Inventory type, always eq
                final short dst = (short) slea.readInt();
                slea.readInt(); // Inventory type, always use
                final short src = (short) slea.readInt();
                used = UseUpgradeScroll(src, dst, (short) 2, c, c.getPlayer(), itemId, false); //cannot use ws with vega but we dont care
                cc = used;
                break;
            }
            case 5060001: { // Sealing Lock
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);

                    c.getPlayer().forceReAddItem_Flag(item, type);
                    used = true;
                }
                break;
            }
            case 5061000: { // Sealing Lock 7 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);
                    item.setExpiration(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));

                    c.getPlayer().forceReAddItem_Flag(item, type);
                    used = true;
                }
                break;
            }
            case 5061001: { // Sealing Lock 30 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);

                    item.setExpiration(System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000));

                    c.getPlayer().forceReAddItem_Flag(item, type);
                    used = true;
                }
                break;
            }
            case 5061002: { // Sealing Lock 90 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);

                    item.setExpiration(System.currentTimeMillis() + (90 * 24 * 60 * 60 * 1000));

                    c.getPlayer().forceReAddItem_Flag(item, type);
                    used = true;
                }
                break;
            }
            case 5061003: { // Sealing Lock 365 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);

                    item.setExpiration(System.currentTimeMillis() + (365 * 24 * 60 * 60 * 1000));

                    c.getPlayer().forceReAddItem_Flag(item, type);
                    used = true;
                }
                break;
            }
            case 5063000: {
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getType() == 1) { //equip
                    short flag = item.getFlag();
                    flag |= ItemFlag.LUCKY_DAY.getValue();
                    item.setFlag(flag);

                    c.getPlayer().forceReAddItem_Flag(item, type);
                    used = true;
                }
                break;
            }
            case 5064000: {
                //System.out.println("slea..." + slea.toString());
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getType() == 1) { //equip
                    if (((Equip) item).getEnhance() >= 12) {
                        break; //cannot be used
                    }
                    short flag = item.getFlag();
                    flag |= ItemFlag.SHIELD_WARD.getValue();
                    item.setFlag(flag);

                    c.getPlayer().forceReAddItem_Flag(item, type);
                    used = true;
                }
                break;
            }

            case 5060003:
            case 5060004:
            case 5060005:
            case 5060006:
            case 5060007: {
                Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).findById(itemId == 5060003 ? 4170023 : 4170024);
                if (item == null || item.getQuantity() <= 0) { // hacking{
                    return;
                }
                if (getIncubatedItems(c, itemId)) {
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, item.getPosition(), (short) 1, false);
                    used = true;
                }
                break;
            }

            case 5070000: { // Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    c.getPlayer().getMap().broadcastMessage(CWvsContext.broadcastMsg(2, sb.toString()));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5071000: { // Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    c.getChannelServer().broadcastSmegaPacket(CWvsContext.broadcastMsg(2, sb.toString()));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5077000: { // 3 line Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final byte numLines = slea.readByte();
                    if (numLines > 3) {
                        return;
                    }
                    final List<String> messages = new LinkedList<>();
                    String message;
                    for (int i = 0; i < numLines; i++) {
                        message = slea.readMapleAsciiString();
                        if (message.length() > 65) {
                            break;
                        }
                        messages.add(c.getPlayer().getName() + " : " + message);
                    }
                    final boolean ear = slea.readByte() > 0;

                    World.Broadcast.broadcastSmega(CWvsContext.tripleSmega(messages, ear, c.getChannel()));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5079004: { // Heart Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    World.Broadcast.broadcastSmega(CWvsContext.echoMegaphone(c.getPlayer().getName(), message));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5073000: { // Heart Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = slea.readByte() != 0;
                    World.Broadcast.broadcastSmega(CWvsContext.broadcastMsg(9, c.getChannel(), sb.toString(), ear));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5074000: { // Skull Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = slea.readByte() != 0;

                    World.Broadcast.broadcastSmega(CWvsContext.broadcastMsg(22, c.getChannel(), sb.toString(), ear));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5072000: { // Super Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = slea.readByte() != 0;

                    World.Broadcast.broadcastSmega(CWvsContext.broadcastMsg(3, c.getChannel(), sb.toString(), ear));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5076000: { // Item Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = slea.readByte() > 0;

                    Item item = null;
                    if (slea.readByte() == 1) { //item
                        byte invType = (byte) slea.readInt();
                        byte pos = (byte) slea.readInt();
                        if (pos <= 0) {
                            invType = -1;
                        }
                        item = c.getPlayer().getInventory(MapleInventoryType.getByType(invType)).getItem(pos);
                    }
                    World.Broadcast.broadcastSmega(CWvsContext.itemMegaphone(sb.toString(), ear, c.getChannel(), item));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5079000: {
                break;
            }
            case 5079001:
            case 5079002: {
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = slea.readMapleAsciiString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = slea.readByte() != 0;

                    World.Broadcast.broadcastSmega(CWvsContext.broadcastMsg(24 + itemId % 10, c.getChannel(), sb.toString(), ear));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5075000: // MapleTV Messenger
            case 5075001: // MapleTV Star Messenger
            case 5075002: { // MapleTV Heart Messenger
                c.getPlayer().dropMessage(5, "There are no MapleTVs to broadcast the message to.");
                break;
            }
            case 5075003:
            case 5075004:
            case 5075005: {
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                int tvType = itemId % 10;
                if (tvType == 3) {
                    slea.readByte(); //who knows
                }
                boolean ear = tvType != 1 && tvType != 2 && slea.readByte() > 1; //for tvType 1/2, there is no byte. 
                MapleCharacter victim = tvType == 1 || tvType == 4 ? null : c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString()); //for tvType 4, there is no string.
                if (tvType == 0 || tvType == 3) { //doesn't allow two
                    victim = null;
                } else if (victim == null) {
                    c.getPlayer().dropMessage(1, "That character is not in the channel.");
                    break;
                }
                String message = slea.readMapleAsciiString();
                World.Broadcast.broadcastSmega(CWvsContext.broadcastMsg(3, c.getChannel(), c.getPlayer().getName() + " : " + message, ear));
                used = true;
                break;
            }
            case 5090100: // Wedding Invitation Card
            case 5090000: { // Note
                final String sendTo = slea.readMapleAsciiString();
                final String msg = slea.readMapleAsciiString();
                if (c.getChannelServer().getPlayerStorage().getCharacterByName(sendTo) != null) {
                    c.getSession().write(CSPacket.OnMemoResult((byte) 5, (byte) 0));
                    break;
                }
                c.getPlayer().sendNote(sendTo, msg);
                c.getSession().write(CSPacket.OnMemoResult((byte) 4, (byte) 0));
                used = true;
                break;
            }
            case 5100000: { // Congratulatory Song
                c.getPlayer().getMap().broadcastMessage(CField.musicChange("Jukebox/Congratulation"));
                used = true;
                break;
            }
            case 5190001:
            case 5190002:
            case 5190003:
            case 5190004:
            case 5190005:
            case 5190006:
            case 5190007:
            case 5190008:
            case 5190000: { // Pet Flags
                final int uniqueid = (int) slea.readLong();
                MaplePet pet = c.getPlayer().getPet(0);
                int slo = 0;

                if (pet == null) {
                    break;
                }
                if (pet.getUniqueId() != uniqueid) {
                    pet = c.getPlayer().getPet(1);
                    slo = 1;
                    if (pet != null) {
                        if (pet.getUniqueId() != uniqueid) {
                            pet = c.getPlayer().getPet(2);
                            slo = 2;
                            if (pet != null) {
                                if (pet.getUniqueId() != uniqueid) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
                PetFlag zz = PetFlag.getByAddId(itemId);
                if (zz != null && !zz.check(pet.getFlags())) {
                    pet.setFlags(pet.getFlags() | zz.getValue());
                    c.getSession().write(PetPacket.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
                    c.getSession().write(CWvsContext.enableActions());
                    c.getSession().write(CSPacket.changePetFlag(uniqueid, true, zz.getValue()));
                    used = true;
                }
                break;
            }
            case 5191001:
            case 5191002:
            case 5191003:
            case 5191004:
            case 5191000: { // Pet Flags
                final int uniqueid = (int) slea.readLong();
                MaplePet pet = c.getPlayer().getPet(0);
                int slo = 0;

                if (pet == null) {
                    break;
                }
                if (pet.getUniqueId() != uniqueid) {
                    pet = c.getPlayer().getPet(1);
                    slo = 1;
                    if (pet != null) {
                        if (pet.getUniqueId() != uniqueid) {
                            pet = c.getPlayer().getPet(2);
                            slo = 2;
                            if (pet != null) {
                                if (pet.getUniqueId() != uniqueid) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
                PetFlag zz = PetFlag.getByDelId(itemId);
                if (zz != null && zz.check(pet.getFlags())) {
                    pet.setFlags(pet.getFlags() - zz.getValue());
                    c.getSession().write(PetPacket.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
                    c.getSession().write(CWvsContext.enableActions());
                    c.getSession().write(CSPacket.changePetFlag(uniqueid, false, zz.getValue()));
                    used = true;
                }
                break;
            }
            case 5501001:
            case 5501002: { //expiry mount
                final Skill skil = SkillFactory.getSkill(slea.readInt());
                if (skil == null || skil.getId() / 10000 != 8000 || c.getPlayer().getSkillLevel(skil) <= 0 || !skil.isTimeLimited() || GameConstants.getMountItem(skil.getId(), c.getPlayer()) <= 0) {
                    break;
                }
                final long toAdd = (itemId == 5501001 ? 30 : 60) * 24 * 60 * 60 * 1000L;
                final long expire = c.getPlayer().getSkillExpiry(skil);
                if (expire < System.currentTimeMillis() || expire + toAdd >= System.currentTimeMillis() + (365 * 24 * 60 * 60 * 1000L)) {
                    break;
                }
                c.getPlayer().changeSingleSkillLevel(skil, c.getPlayer().getSkillLevel(skil), c.getPlayer().getMasterLevel(skil), expire + toAdd);
                used = true;
                break;
            }
            case 5170000: { // Pet name change
                final int uniqueid = (int) slea.readLong();
                MaplePet pet = c.getPlayer().getPet(0);
                int slo = 0;

                if (pet == null) {
                    break;
                }
                if (pet.getUniqueId() != uniqueid) {
                    pet = c.getPlayer().getPet(1);
                    slo = 1;
                    if (pet != null) {
                        if (pet.getUniqueId() != uniqueid) {
                            pet = c.getPlayer().getPet(2);
                            slo = 2;
                            if (pet != null) {
                                if (pet.getUniqueId() != uniqueid) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
                String nName = slea.readMapleAsciiString();
                for (String z : GameConstants.RESERVED) {
                    if (pet.getName().indexOf(z) != -1 || nName.indexOf(z) != -1) {
                        break;
                    }
                }
                if (MapleCharacterUtil.canChangePetName(nName)) {
                    pet.setName(nName);
                    c.getSession().write(PetPacket.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
                    c.getSession().write(CWvsContext.enableActions());
                    c.getPlayer().getMap().broadcastMessage(CSPacket.changePetName(c.getPlayer(), nName, slo));
                    used = true;
                }
                break;
            }
            case 5700000: {
                slea.skip(8);
                if (c.getPlayer().getAndroid() == null) {
                    break;
                }
                String nName = slea.readMapleAsciiString();
                for (String z : GameConstants.RESERVED) {
                    if (c.getPlayer().getAndroid().getName().indexOf(z) != -1 || nName.indexOf(z) != -1) {
                        break;
                    }
                }
                if (MapleCharacterUtil.canChangePetName(nName)) {
                    c.getPlayer().getAndroid().setName(nName);
                    c.getPlayer().setAndroid(c.getPlayer().getAndroid()); //respawn it
                    used = true;
                }
                break;
            }
            case 5230001:
            case 5230000: {// owl of minerva
                final int itemSearch = slea.readInt();
                final List<HiredMerchant> hms = c.getChannelServer().searchMerchant(itemSearch);
                if (hms.size() > 0) {
                    c.getSession().write(CWvsContext.getOwlSearched(itemSearch, hms));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(1, "Unable to find the item.");
                }
                break;
            }
            case 5281001: //idk, but probably
            case 5280001: // Gas Skill
            case 5281000: { // Passed gas
                Rectangle bounds = new Rectangle((int) c.getPlayer().getPosition().getX(), (int) c.getPlayer().getPosition().getY(), 1, 1);
                MapleMist mist = new MapleMist(bounds, c.getPlayer());
                c.getPlayer().getMap().spawnMist(mist, 10000, true);
                c.getSession().write(CWvsContext.enableActions());
                used = true;
                break;
            }
            case 5370001:
            case 5370000: { // Chalkboard
                for (MapleEventType t : MapleEventType.values()) {
                    final MapleEvent e = ChannelServer.getInstance(c.getChannel()).getEvent(t);
                    if (e.isRunning()) {
                        for (int i : e.getType().mapids) {
                            if (c.getPlayer().getMapId() == i) {
                                c.getPlayer().dropMessage(5, "You may not use that here.");
                                c.getSession().write(CWvsContext.enableActions());
                                return;
                            }
                        }
                    }
                }
                c.getPlayer().setChalkboard(slea.readMapleAsciiString());
                break;
            }
            case 5390000: // Diablo Messenger
            case 5390001: // Cloud 9 Messenger
            case 5390002: // Loveholic Messenger
            case 5390003: // New Year Messenger 1
            case 5390004: // New Year Messenger 2
            case 5390005: // Cute Tiger Messenger
            case 5390006: // Tiger Roar's Messenger
            case 5390007:
            case 5390008:
            case 5390009: {
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final List<String> lines = new LinkedList<>();
                    if (itemId == 5390009) { //friend finder megaphone
                        lines.add("I'm looking for ");
                        lines.add("friends! Send a ");
                        lines.add("Friend Request if ");
                        lines.add("you're intetested!");
                    } else {
                        for (int i = 0; i < 4; i++) {
                            final String text = slea.readMapleAsciiString();
                            if (text.length() > 55) {
                                continue;
                            }
                            lines.add(text);
                        }
                    }
                    final boolean ear = slea.readByte() != 0;
                    World.Broadcast.broadcastSmega(CWvsContext.getAvatarMega(c.getPlayer(), c.getChannel(), itemId, lines, ear));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5452001:
            case 5450006:
            case 5450007:
            case 5450013:
            case 5450003:
            case 5450000: { // Mu Mu the Travelling Merchant
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "You must be over level 10 to use this.");
                } else if (c.getPlayer().hasBlockedInventory() || c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000) {
                    c.getPlayer().dropMessage(5, "You may not use this here.");
                } else if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                    c.getPlayer().dropMessage(5, "You may not use this here.");
                } else {
                    MapleShopFactory.getInstance().getShop(9090000).sendShop(c);
                }
                //used = true;
                break;
            }
            case 5300000:
            case 5300001:
            case 5300002: { // Cash morphs
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                ii.getItemEffect(itemId).applyTo(c.getPlayer());
                used = true;
                break;
            }
            case 5781000: { //pet color dye
                slea.readInt();
                slea.readInt();
                int color = slea.readInt();

                break;
            }
            default:
                if (itemId / 10000 == 524 || itemId / 10000 == 546) { //Pet food & snacks
                    used = UsePetFood(c, itemId);
                    break;
                    }  
                if (itemId / 10000 == 512) {
                    final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    String msg = ii.getMsg(itemId);
                    final String ourMsg = slea.readMapleAsciiString();
                    if (!msg.contains("%s")) {
                        msg = ourMsg;
                    } else {
                        msg = msg.replaceFirst("%s", c.getPlayer().getName());
                        if (!msg.contains("%s")) {
                            msg = ii.getMsg(itemId).replaceFirst("%s", ourMsg);
                        } else {
                            try {
                                msg = msg.replaceFirst("%s", ourMsg);
                            } catch (Exception e) {
                                msg = ii.getMsg(itemId).replaceFirst("%s", ourMsg);
                            }
                        }
                    }
                    c.getPlayer().getMap().startMapEffect(msg, itemId);

                    final int buff = ii.getStateChangeItem(itemId);
                    if (buff != 0) {
                        for (MapleCharacter mChar : c.getPlayer().getMap().getCharacters()) {
                            ii.getItemEffect(buff).applyTo(mChar);
                        }
                    }
                    used = true;
                } else if (itemId / 10000 == 510) {
                    c.getPlayer().getMap().startJukebox(c.getPlayer().getName(), itemId);
                    used = true;
                } else if (itemId / 10000 == 520) {
                    final int mesars = MapleItemInformationProvider.getInstance().getMeso(itemId);
                    if (mesars > 0 && c.getPlayer().getMeso() < (Integer.MAX_VALUE - mesars)) {
                        used = true;
                        if (Math.random() > 0.1) {
                            final int gainmes = Randomizer.nextInt(mesars);
                            c.getPlayer().gainMeso(gainmes, false);
                            c.getSession().write(CSPacket.sendMesobagSuccess(gainmes));
                        } else {
                            c.getSession().write(CSPacket.sendMesobagFailed(false)); // not random
                        }
                    }
                } else if (itemId / 10000 == 562) {
                    if (UseSkillBook(slot, itemId, c, c.getPlayer())) {
                        c.getPlayer().gainSP(1);
                    } //this should handle removing
                } else if (itemId / 10000 == 553) {
                    UseRewardItem(slot, itemId, false, c, c.getPlayer());// this too
                } else if (itemId / 10000 != 519) {
                    System.out.println("Unhandled CS item : " + itemId);
                    System.out.println(slea.toString(true));
                }
                break;
        }

        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (short) 1, false, true);
        }
        c.getSession().write(CWvsContext.enableActions());
        if (cc) {
            if (!c.getPlayer().isAlive() || c.getPlayer().getEventInstance() != null || FieldLimitType.ChannelSwitch.check(c.getPlayer().getMap().getFieldLimit())) {
                c.getPlayer().dropMessage(1, "Auto relog failed.");
                return;
            }
            c.getPlayer().dropMessage(5, "Auto relogging. Please wait.");
            c.getPlayer().fakeRelog();
            if (c.getPlayer().getScrolledPosition() != 0) {
                c.getSession().write(CWvsContext.pamSongUI());
            }
        }
    }
    
    public static final boolean UsePetFood(MapleClient c, int itemId) {
        MaplePet pet = c.getPlayer().getPet(0);
        if (pet == null) {
            return false;
        }
        if (!pet.canConsume(itemId)) {
            pet = c.getPlayer().getPet(1);
            if (pet != null) {
                if (!pet.canConsume(itemId)) {
                    pet = c.getPlayer().getPet(2);
                    if (pet != null) {
                        if (!pet.canConsume(itemId)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        final byte petindex = c.getPlayer().getPetIndex(pet);
        pet.setFullness(100);
        if (pet.getCloseness() < 30000) {
            if (pet.getCloseness() + (100 * c.getChannelServer().getTraitRate()) > 30000) {
                pet.setCloseness(30000);
            } else {
                pet.setCloseness(pet.getCloseness() + (100 * c.getChannelServer().getTraitRate()));
            }
            if (pet.getCloseness() >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                pet.setLevel(pet.getLevel() + 1);
                c.getSession().write(EffectPacket.showOwnPetLevelUp(c.getPlayer().getPetIndex(pet)));
                c.getPlayer().getMap().broadcastMessage(PetPacket.showPetLevelUp(c.getPlayer(), petindex));
            }
        }
        c.getSession().write(PetPacket.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()), true));
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PetPacket.commandResponse(c.getPlayer().getId(), (byte) 1, petindex, true, true), true);
        return true;
    }

        public static final void Pickup_Player(final LittleEndianAccessor slea, MapleClient c, final MapleCharacter chr) {
        if (c.getPlayer().hasBlockedInventory()) { //hack
            return;
        }
        slea.skip(4); // update tick
        c.getPlayer().setScrolledPosition((short) 0);
        slea.skip(1); // or is this before tick?
        final Point Client_Reportedpos = slea.readPos();
        if (chr == null || chr.getMap() == null) {
            return;
        }
        final MapleMapObject ob = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.ITEM);

        if (ob == null) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final MapleMapItem mapitem = (MapleMapItem) ob;
        final Lock lock = mapitem.getLock();
        lock.lock();
        try {
            if (mapitem.isPickedUp()) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            if (mapitem.getQuest() > 0 && chr.getQuestStatus(mapitem.getQuest()) != 1) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && chr.getMap().getEverlast()))) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            final double Distance = Client_Reportedpos.distanceSq(mapitem.getPosition());
            if (mapitem.getMeso() > 0) {
                if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                    final List<MapleCharacter> toGive = new LinkedList<>();
                    final int splitMeso = mapitem.getMeso() * 40 / 100;
                    for (MaplePartyCharacter z : chr.getParty().getMembers()) {
                        MapleCharacter m = chr.getMap().getCharacterById(z.getId());
                        if (m != null && m.getId() != chr.getId()) {
                            toGive.add(m);
                        }
                    }
                    for (final MapleCharacter m : toGive) {
                        int mesos = splitMeso / toGive.size();
                        if (mapitem.getDropper() instanceof MapleMonster && m.getStat().incMesoProp > 0) {
                            mesos += Math.floor((m.getStat().incMesoProp * mesos) / 100.0f);
                        }
                        m.gainMeso(mesos, true);
                    }
                    int mesos = mapitem.getMeso() - splitMeso;
                    if (mapitem.getDropper() instanceof MapleMonster && chr.getStat().incMesoProp > 0) {
                        mesos += Math.floor((chr.getStat().incMesoProp * mesos) / 100.0f);
                    }
                    chr.gainMeso(mesos, true);
                } else {
                    int mesos = mapitem.getMeso();
                    if (mapitem.getDropper() instanceof MapleMonster && chr.getStat().incMesoProp > 0) {
                        mesos += Math.floor((chr.getStat().incMesoProp * mesos) / 100.0f);
                    }
                    chr.gainMeso(mesos, true);
                }
                removeItem(chr, mapitem, ob);
            } else {
                if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId())) {
                    c.getSession().write(CWvsContext.enableActions());
                    c.getPlayer().dropMessage(5, "This item cannot be picked up.");
                } else if (c.getPlayer().inPVP() && Integer.parseInt(c.getPlayer().getEventInstance().getProperty("ice")) == c.getPlayer().getId()) {
                    c.getSession().write(InventoryPacket.getInventoryFull());
                    c.getSession().write(InventoryPacket.getShowInventoryFull());
                    c.getSession().write(CWvsContext.enableActions());
                } else if (useItem(c, mapitem.getItemId())) {
                    removeItem(c.getPlayer(), mapitem, ob);
                    //another hack
                    if (mapitem.getItemId() / 10000 == 291) {
                        c.getPlayer().getMap().broadcastMessage(CField.getCapturePosition(c.getPlayer().getMap()));
                        c.getPlayer().getMap().broadcastMessage(CField.resetCapture());
                    }
                } else if (mapitem.getItemId() / 10000 != 291 && MapleInventoryManipulator.checkSpace(c, mapitem.getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                    if (mapitem.getItem().getQuantity() >= 50 && mapitem.getItemId() == 2340000) {
                        c.setMonitored(true); //hack check
                    }
                    if (!GameConstants.isPet(mapitem.getItemId())) {
                        MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster);
                        removeItem(chr, mapitem, ob);
                    } else {
                        MapleInventoryManipulator.addById(c, mapitem.getItemId(), (short) 1, "", MaplePet.createPet(mapitem.getItemId(), MapleItemInformationProvider.getInstance().getName(mapitem.getItemId()), 1, 0, 100, MapleInventoryIdentifier.getInstance(), 0, (short) 0), 90, false, null);
                        removeItem_Pet(chr, mapitem, mapitem.getItemId());
                    }
                } else {
                    c.getSession().write(InventoryPacket.getInventoryFull());
                    c.getSession().write(InventoryPacket.getShowInventoryFull());
                    c.getSession().write(CWvsContext.enableActions());
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static final void Pickup_Pet(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        //System.out.println("PETS: " + slea.toString());
        c.getPlayer().setScrolledPosition((short) 0);
        final byte petz = (byte) c.getPlayer().getPetIndex((int) slea.readLong());
        final MaplePet pet = chr.getPet(petz);
        slea.skip(1); // [4] Zero, [4] Seems to be tickcount, [1] Always zero
        slea.skip(4); // update tick
        final Point Client_Reportedpos = slea.readPos();
        final MapleMapObject ob = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.ITEM);

        if (ob == null || pet == null) {
            //System.out.println("Ob or pet is null");
            return;
        }
        final MapleMapItem mapitem = (MapleMapItem) ob;
        final Lock lock = mapitem.getLock();
        lock.lock();
        try {
            if (mapitem.isPickedUp()) {
                c.getSession().write(InventoryPacket.getInventoryFull());
//                System.err.println("Return 1");
                return;
            }
            if (mapitem.getOwner() != chr.getId() && mapitem.isPlayerDrop()) {
//                System.err.println("Return 2");
                return;
            }
            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && chr.getMap().getEverlast()))) {
//                System.err.println("Return 3");
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
            	// System.err.println("Return 4");
                c.getSession().write(CWvsContext.enableActions());
                return;
            }

            // System.err.println("Petdrop 5");
            if (mapitem.getMeso() > 0) {
                if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                    final List<MapleCharacter> toGive = new LinkedList<>();
                    final int splitMeso = mapitem.getMeso() * 40 / 100;
                    for (MaplePartyCharacter z : chr.getParty().getMembers()) {
                        MapleCharacter m = chr.getMap().getCharacterById(z.getId());
                        if (m != null && m.getId() != chr.getId()) {
                            toGive.add(m);
                        }
                    }
                    for (final MapleCharacter m : toGive) {
                        m.gainMeso(splitMeso / toGive.size(), true);
                    }
                    chr.gainMeso(mapitem.getMeso() - splitMeso, true);
                } else {
                    chr.gainMeso(mapitem.getMeso(), true);
                }
//                System.err.println("Return 8");
                removeItem_Pet(chr, mapitem, petz);
            } else {
                if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId()) || mapitem.getItemId() / 10000 == 291) {
                    c.getSession().write(CWvsContext.enableActions());
                } else if (useItem(c, mapitem.getItemId())) {
                    removeItem_Pet(chr, mapitem, petz);
                } else if (MapleInventoryManipulator.checkSpace(c, mapitem.getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                    if (mapitem.getItem().getQuantity() >= 50 && mapitem.getItemId() == 2340000) {
                        c.setMonitored(true); //hack check
                    }
//                    System.err.println("Return 12");
                    MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster);
                    removeItem_Pet(chr, mapitem, petz);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static final boolean useItem(final MapleClient c, final int id) {
        if (GameConstants.isUse(id)) { // TO prevent caching of everything, waste of mem
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final MapleStatEffect eff = ii.getItemEffect(id);
            if (eff == null) {
                return false;
            }
            //must hack here for ctf
            if (id / 10000 == 291) {
                boolean area = false;
                for (Rectangle rect : c.getPlayer().getMap().getAreas()) {
                    if (rect.contains(c.getPlayer().getTruePosition())) {
                        area = true;
                        break;
                    }
                }
                if (!c.getPlayer().inPVP() || (c.getPlayer().getTeam() == (id - 2910000) && area)) {
                    return false; //dont apply the consume
                }
            }
            final int consumeval = eff.getConsume();

            if (consumeval > 0) {
                consumeItem(c, eff);
                consumeItem(c, ii.getItemEffectEX(id));
                c.getSession().write(InfoPacket.getShowItemGain(id, (byte) 1));
                return true;
            }
        }
        return false;
    }

    public static final void consumeItem(final MapleClient c, final MapleStatEffect eff) {
        if (eff == null) {
            return;
        }
        if (eff.getConsume() == 2) {
            if (c.getPlayer().getParty() != null && c.getPlayer().isAlive()) {
                for (final MaplePartyCharacter pc : c.getPlayer().getParty().getMembers()) {
                    final MapleCharacter chr = c.getPlayer().getMap().getCharacterById(pc.getId());
                    if (chr != null && chr.isAlive()) {
                        eff.applyTo(chr);
                    }
                }
            } else {
                eff.applyTo(c.getPlayer());
            }
        } else if (c.getPlayer().isAlive()) {
            eff.applyTo(c.getPlayer());
        }
    }

    public static final void removeItem_Pet(final MapleCharacter chr, final MapleMapItem mapitem, int pet) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 5, chr.getId(), pet));
        chr.getMap().removeMapObject(mapitem);
        if (mapitem.isRandDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }

    private static void removeItem(final MapleCharacter chr, final MapleMapItem mapitem, final MapleMapObject ob) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 2, chr.getId()), mapitem.getPosition());
        chr.getMap().removeMapObject(ob);
        if (mapitem.isRandDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }

    private static void addMedalString(final MapleCharacter c, final StringBuilder sb) {
        final Item medal = c.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -49);
        if (medal != null) { // Medal
            sb.append("<");
                sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
            }
            sb.append("> ");
        }

    private static boolean getIncubatedItems(MapleClient c, int itemId) {
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 2 || c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 2 || c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 2) {
            c.getPlayer().dropMessage(5, "Please make room in your inventory.");
            return false;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int id1 = RandomRewards.getPeanutReward(), id2 = RandomRewards.getPeanutReward();
        while (!ii.itemExists(id1)) {
            id1 = RandomRewards.getPeanutReward();
        }
        while (!ii.itemExists(id2)) {
            id2 = RandomRewards.getPeanutReward();
        }
        c.getSession().write(CWvsContext.getPeanutResult(id1, (short) 1, id2, (short) 1, itemId));
        MapleInventoryManipulator.addById(c, id1, (short) 1, ii.getName(itemId) + " on " + FileoutputUtil.CurrentReadable_Date());
        MapleInventoryManipulator.addById(c, id2, (short) 1, ii.getName(itemId) + " on " + FileoutputUtil.CurrentReadable_Date());

        NPCTalk talk = new NPCTalk((byte) 4, 1090000, (byte) 0);
        talk.setText("You have obtained the following items:\r\n#i" + id1 + "##z" + id1 + "#\r\n#i" + id2 + "##z" + id2 + "#");
        c.getSession().write(NPCPacket.getNPCTalk(talk));
        return true;
    }

    public static final void OwlMinerva(final LittleEndianAccessor slea, final MapleClient c) {
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && itemid == 2310000 && !c.getPlayer().hasBlockedInventory()) {
            final int itemSearch = slea.readInt();
            final List<HiredMerchant> hms = c.getChannelServer().searchMerchant(itemSearch);
            if (hms.size() > 0) {
                c.getSession().write(CWvsContext.getOwlSearched(itemSearch, hms));
                MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, true, false);
            } else {
                c.getPlayer().dropMessage(1, "Unable to find the item.");
            }
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void Owl(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().haveItem(5230000, 1, true, false) || c.getPlayer().haveItem(2310000, 1, true, false)) {
            if (c.getPlayer().getMapId() >= 910000000 && c.getPlayer().getMapId() <= 910000022) {
                c.getSession().write(CWvsContext.getOwlOpen());
            } else {
                c.getPlayer().dropMessage(5, "This can only be used inside the Free Market.");
                c.getSession().write(CWvsContext.enableActions());
            }
        }
    }
    public static final int OWL_ID = 2; //don't change. 0 = owner ID, 1 = store ID, 2 = object ID

    public static final void OwlWarp(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.getPlayer().isAlive()) {
            c.getSession().write(CWvsContext.getOwlMessage(4));
            return;
        } else if (c.getPlayer().getTrade() != null) {
            c.getSession().write(CWvsContext.getOwlMessage(7));
            return;
        }
        if (c.getPlayer().getMapId() >= 910000000 && c.getPlayer().getMapId() <= 910000022 && !c.getPlayer().hasBlockedInventory()) {
            final int id = slea.readInt();
            final int map = slea.readInt();
            if (map >= 910000001 && map <= 910000022) {
                c.getSession().write(CWvsContext.getOwlMessage(0));
                final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(map);
                c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                HiredMerchant merchant = null;
                List<MapleMapObject> objects;
                switch (OWL_ID) {
                    case 0:
                        objects = mapp.getAllMerchant();
                        for (MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop) {
                                final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                                if (ips instanceof HiredMerchant) {
                                    final HiredMerchant merch = (HiredMerchant) ips;
                                    if (merch.getOwnerId() == id) {
                                        merchant = merch;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case 1:
                        objects = mapp.getAllMerchant();
                        for (MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop) {
                                final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                                if (ips instanceof HiredMerchant) {
                                    final HiredMerchant merch = (HiredMerchant) ips;
                                    if (merch.getStoreId() == id) {
                                        merchant = merch;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        final MapleMapObject ob = mapp.getMapObject(id, MapleMapObjectType.HIRED_MERCHANT);
                        if (ob instanceof IMaplePlayerShop) {
                            final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                            if (ips instanceof HiredMerchant) {
                                merchant = (HiredMerchant) ips;
                            }
                        }
                        break;
                }
                if (merchant != null) {
                    if (merchant.isOwner(c.getPlayer())) {
                        merchant.setOpen(false);
                        merchant.removeAllVisitors((byte) 16, (byte) 0);
                        c.getPlayer().setPlayerShop(merchant);
                        c.getSession().write(PlayerShopPacket.getHiredMerch(c.getPlayer(), merchant, false));
                    } else {
                        if (!merchant.isOpen() || !merchant.isAvailable()) {
                            c.getPlayer().dropMessage(1, "The owner of the store is currently undergoing store maintenance. Please try again in a bit.");
                        } else {
                            if (merchant.getFreeSlot() == -1) {
                                c.getPlayer().dropMessage(1, "You can't enter the room due to full capacity.");
                            } else if (merchant.isInBlackList(c.getPlayer().getName())) {
                                c.getPlayer().dropMessage(1, "You may not enter this store.");
                            } else {
                                c.getPlayer().setPlayerShop(merchant);
                                merchant.addVisitor(c.getPlayer());
                                c.getSession().write(PlayerShopPacket.getHiredMerch(c.getPlayer(), merchant, false));
                            }
                        }
                    }
                } else {
                    c.getPlayer().dropMessage(1, "The room is already closed.");
                }
            } else {
                c.getSession().write(CWvsContext.getOwlMessage(23));
            }
        } else {
            c.getSession().write(CWvsContext.getOwlMessage(23));
        }
    }

    public static final void PamSong(LittleEndianAccessor slea, MapleClient c) {
        final Item pam = c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5640000);
        if (slea.readByte() > 0 && c.getPlayer().getScrolledPosition() != 0 && pam != null && pam.getQuantity() > 0) {
            final MapleInventoryType inv = c.getPlayer().getScrolledPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
            final Item item = c.getPlayer().getInventory(inv).getItem(c.getPlayer().getScrolledPosition());
            c.getPlayer().setScrolledPosition((short) 0);
            if (item != null) {
                final Equip eq = (Equip) item;
                eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() + 1));
                c.getPlayer().forceReAddItem_Flag(eq, inv);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, pam.getPosition(), (short) 1, true, false);
                c.getPlayer().getMap().broadcastMessage(CField.pamsSongEffect(c.getPlayer().getId()));
            }
        } else {
            c.getPlayer().setScrolledPosition((short) 0);
        }
    }

    public static final void TeleRock(LittleEndianAccessor slea, MapleClient c) {
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 232 || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        boolean used = UseTeleRock(slea, c, itemId);
        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.getSession().write(CWvsContext.enableActions());
    }

  /* public static final boolean UseTeleRock(LittleEndianAccessor slea, MapleClient c, int itemId) {
        boolean used = false;
        if (itemId == 5041001 || itemId == 5040004) {
            slea.readByte(); //useless
        }
        if (slea.readByte() == 0) { // Rocktype
            final MapleMap target = c.getChannelServer().getMapFactory().getMap(slea.readInt());
            if ((itemId == 5041000 && c.getPlayer().isRockMap(target.getId())) || (itemId != 5041000 && c.getPlayer().isRegRockMap(target.getId())) || ((itemId == 5040004 || itemId == 5041001) && (c.getPlayer().isHyperRockMap(target.getId()) || GameConstants.isHyperTeleMap(target.getId())))) {
                if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(target.getFieldLimit()) && !c.getPlayer().isInBlockedMap()) { //Makes sure this map doesn't have a forced return map
                    c.getPlayer().changeMap(target, target.getPortal(0));
                    used = true;
                }
            }
        } else {
            c.getPlayer().dropMessage(1, "You cannot go to that place.");
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
            if (victim != null && !victim.isIntern() && c.getPlayer().getEventInstance() == null && victim.getEventInstance() == null) {
                if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(c.getChannelServer().getMapFactory().getMap(victim.getMapId()).getFieldLimit()) && !victim.isInBlockedMap() && !c.getPlayer().isInBlockedMap()) {
                    if (itemId == 5041000 || itemId == 5040004 || itemId == 5041001 || (victim.getMapId() / 100000000) == (c.getPlayer().getMapId() / 100000000)) { // Viprock or same continent
                        c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestPortal(victim.getTruePosition()));
                        used = true;
                    }
                }
            }
        }
        return used && itemId != 5041001 && itemId != 5040004;
    }
      */

    public static boolean checkPotentialLock(MapleCharacter chr, Equip eq, int line, int potential) {
        if (line == 0 || potential == 0) {
            return false;
        }
        if (line < 0 || line > 3) {
            System.out.println("[Hacking Attempt] " + MapleCharacterUtil.makeMapleReadable(chr.getName()) + " Tried to lock potential line which does not exists.");
            return false;
        }
        if (line == 1 && eq.getPotentialByLine(0) != potential - line * 100000
         || line == 2 && eq.getPotentialByLine(1) != potential - line * 100000
         || line == 3 && eq.getPotentialByLine(2) != potential - line * 100000) {
            System.out.println("[Hacking Attempt] " + MapleCharacterUtil.makeMapleReadable(chr.getName()) + " Tried to lock potential which equip doesn't have.");
            return false;
        }
        return true;
    }
    
 
public static final boolean UseTeleRock(LittleEndianAccessor lea, MapleClient c, int itemId) {
        boolean used = false;
        if (itemId == 5040004) {
            lea.readByte();
        } 
        if ((itemId == 5040004) || itemId == 5041001)
        {
            if(lea.readByte() == 0)
            {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(lea.readInt());
                if (target != null){ //Premium and Hyper rocks are allowed to go anywhere. Blocked maps are checked below. 
                    if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(target.getFieldLimit()) && !c.getPlayer().isInBlockedMap()) { //Makes sure this map doesn't have a forced return map
                        c.getPlayer().changeMap(target, target.getPortal(0));
                        if(itemId == 5041001) used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "You cannot go to that place.");
                    }
                } else {
                    c.getPlayer().dropMessage(1, "The place you want to go to does not exist."); 
                }  
            } else {
                final String name = lea.readMapleAsciiString();
                final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
                if (victim != null && !victim.isIntern() && c.getPlayer().getEventInstance() == null && victim.getEventInstance() == null) {
                    if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(c.getChannelServer().getMapFactory().getMap(victim.getMapId()).getFieldLimit()) && !victim.isInBlockedMap() && !c.getPlayer().isInBlockedMap()) {
                        c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestPortal(victim.getTruePosition()));
                        if(itemId == 5041001) used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "You cannot go to where that person is.");
                    }
                } else {
                    if(victim == null) {
                        c.getPlayer().dropMessage(1, "(" +name + ") is either offline or in a different channel.");
                    }
                    else {
                        c.getPlayer().dropMessage(1, "(" +name + ") is currently difficult to locate, so the teleport will not take place.");
                    }
                }
            }
        } else {
            if (itemId == 5040004) {
                c.getPlayer().dropMessage(1, "You are not able to use this teleport rock.");
            }
            else{
                c.getPlayer().dropMessage(1, "This teleport rock is currently disabled.");
            }
        }
        return used;
    }
    
}
