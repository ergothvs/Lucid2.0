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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SimpleTimeZone;

import client.*;
import client.inventory.*;
import com.google.common.collect.ArrayListMultimap;

import constants.GameConstants;
import constants.ItemConstants;
import handling.Buffstat;
import handling.world.MapleCharacterLook;
import server.MapleItemInformationProvider;
import server.cash.CashItem;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import server.quest.MapleQuestStatus;
import server.shops.MapleShop;
import server.shops.MapleShopItem;
import server.stores.AbstractPlayerStore;
import server.stores.IMaplePlayerShop;
import tools.BitTools;
import tools.HexTool;
import tools.KoreanDateUtil;
import tools.Pair;
import tools.StringUtil;
import tools.Triple;
import tools.data.PacketWriter;
import tools.packet.twostate.TSIndex;

public class PacketHelper {

    public static final long FT_UT_OFFSET = 116444592000000000L;
    public static final long MAX_TIME = 150842304000000000L;
    public static final long ZERO_TIME = 94354848000000000L;
    public static final long PERMANENT = 150841440000000000L;

    public static long getKoreanTimestamp(long realTimestamp) {
        return getTime(realTimestamp);
    }

    public static long getTime(long realTimestamp) {
        if (realTimestamp == -1L) { // 00 80 05 BB 46 E6 17 02, 1/1/2079
            return MAX_TIME;
        }
        if (realTimestamp == -2L) { // 00 40 E0 FD 3B 37 4F 01, 1/1/1900
            return ZERO_TIME;
        }
        if (realTimestamp == -3L) {
            return PERMANENT;
        }
        return realTimestamp * 10000L + 116444592000000000L;
    }

    public static long decodeTime(long fakeTimestamp) {
        if (fakeTimestamp == 150842304000000000L) {
            return -1L;
        }
        if (fakeTimestamp == 94354848000000000L) {
            return -2L;
        }
        if (fakeTimestamp == 150841440000000000L) {
            return -3L;
        }
        return (fakeTimestamp - 116444592000000000L) / 10000L;
    }

    public static long getFileTimestamp(long timeStampinMillis, boolean roundToMinutes) {
        if (SimpleTimeZone.getDefault().inDaylightTime(new Date())) {
            timeStampinMillis -= 3600000L;
        }
        long time;

        if (roundToMinutes) {
            time = timeStampinMillis / 1000L / 60L * 600000000L;
        } else {
            time = timeStampinMillis * 10000L;
        }
        return time + 116444592000000000L;
    }

    public static void addImageInfo(PacketWriter pw, byte[] image) {
        pw.writeInt(image.length);
        pw.write(image);
    }

    public static void addStartedQuestInfo(PacketWriter pw, MapleCharacter chr) {
        pw.write(1);
        final List<MapleQuestStatus> started = chr.getStartedQuests();
        pw.writeShort(started.size());
        for (MapleQuestStatus q : started) {
            pw.writeInt(q.getQuest().getId()); // 174.1 this is an integer now
            if (q.hasMobKills()) {
                StringBuilder sb = new StringBuilder();
                for (Iterator i$ = q.getMobKills().values().iterator(); i$.hasNext();) {
                    int kills = ((Integer) i$.next()).intValue();
                    sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
                }
                pw.writeMapleAsciiString(sb.toString());
            } else {
                pw.writeMapleAsciiString(q.getCustomData() == null ? "" : q.getCustomData());
            }
        }
        pw.writeShort(0);
        /*
         pw.writeShort(7);
         pw.writeMapleAsciiString("1NX5211068");
         pw.writeMapleAsciiString("1");
         pw.writeMapleAsciiString("SE20130619");
         pw.writeMapleAsciiString("20130626060823");
         pw.writeMapleAsciiString("99NX5533018");
         pw.writeMapleAsciiString("1");
         pw.writeMapleAsciiString("1NX1003792");
         pw.writeMapleAsciiString("1");
         pw.writeMapleAsciiString("1NX1702337");
         pw.writeMapleAsciiString("1");
         pw.writeMapleAsciiString("1NX9102857");
         pw.writeMapleAsciiString("1");
         pw.writeMapleAsciiString("SE20130116");
         pw.writeMapleAsciiString("1");
         */
    }

    public static void addCompletedQuestInfo(PacketWriter pw, MapleCharacter chr) {
        pw.write(1);
        final List<MapleQuestStatus> completed = chr.getCompletedQuests();
        pw.writeShort(completed.size());
        for (MapleQuestStatus q : completed) {
            pw.writeInt(q.getQuest().getId()); // 174.1 this is an integer now..
            pw.writeInt(KoreanDateUtil.getQuestTimestamp(q.getCompletionTime()));
        }
    }
 
    public static void addSkillInfo(PacketWriter pw, MapleCharacter chr) {
        pw.write(1);
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeInt(0); // new 178 idb, something with link skills

        /* ^ means written. This code is in the 178 idb
        if(byte ^){
            for(short ^){
                int1
                int
                if(sub(int1)){
                    int
                }
            }
        }else{
            for(short){
                int
                int
            }
            for(short){
             int
            }
            for(short){
                int
                long
            }
            for(short){
                int
            }
            for(short){
                int
                int
            }
            for(short){
                int
            }
        }
        if(short ^){ in 178, got moved from inside the first if, to outside the if.
            int
            short
        }
        if(int){ new in 178
            sub:
                int
                int
                int
                short
                long
        }
         */
        /*PacketWriter pw1 =  new PacketWriter();
         final Map<Skill, SkillEntry> skills = chr.getSkills();
         pw1.write(1);
         int hyper = 0;
         //for (Skill skill : skills.keySet()) {
         //    if (skill.isHyper()) hyper++;
         //}
         pw1.writeShort(skills.size() - hyper);
         boolean follow = false;
        
         for (Map.Entry<Skill, SkillEntry> skill : skills.entrySet()) {
         //if (((Skill) skill.getKey()).isHyper()) continue;
            
         if (follow) {
         follow = false;
         if (!GameConstants.isHyperSkill((Skill) skill.getKey()))
         pw1.writeInt(skill.getKey().getId());
         }
         pw1.writeInt(skill.getKey().getId());
         pw1.writeInt(((SkillEntry) skill.getValue()).skillevel);
         addExpirationTime(pw1, ((SkillEntry) skill.getValue()).expiration);

         if (GameConstants.isHyperSkill((Skill) skill.getKey())) {
         // pw1.writeInt(1110009);
         follow = true;
         } else if (((Skill) skill.getKey()).isFourthJob()) {
         pw1.writeInt(((SkillEntry) skill.getValue()).masterlevel);
         }
         //  addSingleSkill(pw, skill.getKey(), skill.getValue());
         }
         pw.write(pw1.getPacket());
         System.out.println(HexTool.toString(pw1.getPacket()));
         */
    }

//    public static void addSingleSkill(PacketWriter pw, Skill skill, SkillEntry ske) {
//        try {
//            // if (skill.getId() != 1001008) return;
//
//            PacketWriter pw1 = new PacketWriter();
//
//            pw1.writeInt(skill.getId());
//            pw1.writeInt(ske.skillevel);
//            addExpirationTime(pw1, ske.expiration);
//
//            if (GameConstants.isHyperSkill(skill)) {
//                //System.out.println("HYPER: " + ((Skill) skill.getKey()).getId());
//                pw1.writeInt(0);
//            } else if (((Skill) skill).isFourthJob()) {
//                pw1.writeInt(((SkillEntry) ske).masterlevel);
//            }
//            if (skill.getId() == 1001008) {
//                System.out.println(HexTool.toString(pw1.getPacket()));
//            }
//            pw.write(pw1.getPacket());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    public static void addCoolDownInfo(PacketWriter pw, MapleCharacter chr) {
        final List<MapleCoolDownValueHolder> cd = chr.getCooldowns();
        pw.writeShort(cd.size());
        for (MapleCoolDownValueHolder cooling : cd) {
            pw.writeInt(cooling.skillId);
            pw.writeInt((int) (cooling.length + cooling.startTime - System.currentTimeMillis()) / 1000);
        }
    }

    public static void addRocksInfo(PacketWriter pw, MapleCharacter chr) {
        int[] mapz = chr.getRegRocks();
        for (int i = 0; i < 5; i++) {
            pw.writeInt(mapz[i]);
        }

        int[] map = chr.getRocks();
        for (int i = 0; i < 10; i++) {
            pw.writeInt(map[i]);
        }

        int[] maps = chr.getHyperRocks();
        for (int i = 0; i < 13; i++) {
            pw.writeInt(maps[i]);
        }
        for (int i = 0; i < 13; i++) {
            pw.writeInt(maps[i]);
        }
    }

    public static void addUnk400Info(PacketWriter pw, MapleCharacter chr) {
        short size = 0;
        pw.writeShort(size);
        for (int i = 0; i < size; i++) {
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
        }
    }

    public static void addRingInfo(PacketWriter pw, MapleCharacter chr) {
        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> aRing = chr.getRings(true);
        List<MapleRing> cRing = aRing.getLeft();
        pw.writeShort(cRing.size());
        for (MapleRing ring : cRing) {
            pw.writeInt(ring.getPartnerChrId());
            pw.writeAsciiString(ring.getPartnerName(), 13);
            pw.writeLong(ring.getRingId());
            pw.writeLong(ring.getPartnerRingId());
        }
        List<MapleRing> fRing = aRing.getMid();
        pw.writeShort(fRing.size());
        for (MapleRing ring : fRing) {
            pw.writeInt(ring.getPartnerChrId());
            pw.writeAsciiString(ring.getPartnerName(), 13);
            pw.writeLong(ring.getRingId());
            pw.writeLong(ring.getPartnerRingId());
            pw.writeInt(ring.getItemId());
        }
        List<MapleRing> mRing = aRing.getRight();
        pw.writeShort(mRing.size());
        int marriageId = 30000;
        for (MapleRing ring : mRing) {
            pw.writeInt(marriageId);
            pw.writeInt(chr.getId());
            pw.writeInt(ring.getPartnerChrId());
            pw.writeShort(3);
            pw.writeInt(ring.getItemId());
            pw.writeInt(ring.getItemId());
            pw.writeAsciiString(chr.getName(), 13);
            pw.writeAsciiString(ring.getPartnerName(), 13);
        }
    }

    public static void addInventoryInfo(PacketWriter pw, MapleCharacter chr) {
    	pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);
    	// addPotionPotInfo(pw, chr);
        pw.writeInt(chr.getId());

        pw.writeInt(0); 
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);

        pw.writeInt(0);

        pw.write(0);
        pw.write(0);
        pw.write(0);

        pw.write(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit());
        pw.write(chr.getInventory(MapleInventoryType.USE).getSlotLimit());
        pw.write(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit());
        pw.write(chr.getInventory(MapleInventoryType.ETC).getSlotLimit());
        pw.write(chr.getInventory(MapleInventoryType.CASH).getSlotLimit());

        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(122700));
        if ((stat != null) && (stat.getCustomData() != null) && (Long.parseLong(stat.getCustomData()) > System.currentTimeMillis())) {
            pw.writeLong(getTime(Long.parseLong(stat.getCustomData())));
        } else {
            pw.writeLong(getTime(-2L));
        }
        pw.write(0);
        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
        final List<Item> equipped = iv.newList();
        Collections.sort(equipped);
        for (Item item : equipped) {
            if ((item.getPosition() < 0) && (item.getPosition() > -100)) {
                addItemPosition(pw, item, false, false);
                addItemInfo(pw, item, chr);
            }
        }
        pw.writeShort(0);
        for (Item item : equipped) {
            if ((item.getPosition() <= -100) && (item.getPosition() > -1000)) {
                addItemPosition(pw, item, false, false);
                addItemInfo(pw, item, chr);
            }
        }
        pw.writeShort(0);
        iv = chr.getInventory(MapleInventoryType.EQUIP);
        for (Item item : iv.list()) {
            addItemPosition(pw, item, false, false);
            addItemInfo(pw, item, chr);
        }
        pw.writeShort(0);
        for (Item item : equipped) {
            if ((item.getPosition() <= -1000) && (item.getPosition() > -1100)) {
                addItemPosition(pw, item, false, false);
                addItemInfo(pw, item, chr);
            }
        }
        pw.writeShort(0);
        for (Item item : equipped) {
            if ((item.getPosition() <= -1100) && (item.getPosition() > -1200)) {
                addItemPosition(pw, item, false, false);
                addItemInfo(pw, item, chr);
            }
        }
        pw.writeShort(0);
        pw.writeShort(0);
        for (Item item : equipped) {
            if (item.getPosition() <= -1200) {
                addItemPosition(pw, item, false, false);
                addItemInfo(pw, item, chr);
            }
        }
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeShort(0);
        for (Item item : equipped) {
            if ((item.getPosition() <= -5000) && (item.getPosition() >= -5003)) {
                addItemPosition(pw, item, false, false);
                addItemInfo(pw, item, chr);
            }
        }      
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeShort(0);
        pw.writeShort(0); // new 178 ?

        iv = chr.getInventory(MapleInventoryType.USE);
        for (Item item : iv.list()) {
            addItemPosition(pw, item, false, false);
            addItemInfo(pw, item, chr);
        }
        pw.write(0);
        iv = chr.getInventory(MapleInventoryType.SETUP);
        for (Item item : iv.list()) {
            addItemPosition(pw, item, false, false);
            addItemInfo(pw, item, chr);
        }
        pw.write(0);
        iv = chr.getInventory(MapleInventoryType.ETC);
        for (Item item : iv.list()) {
            if (item.getPosition() < 100) {
                addItemPosition(pw, item, false, false);
                addItemInfo(pw, item, chr);
            }
        }
        pw.write(0);
        iv = chr.getInventory(MapleInventoryType.CASH);
        for (Item item : iv.list()) {
            addItemPosition(pw, item, false, false);
            addItemInfo(pw, item, chr);
        }
        pw.write(0);
//        for (int i = 0; i < chr.getExtendedSlots().size(); i++) {
//            pw.writeInt(i);
//            pw.writeInt(chr.getExtendedSlot(i));
//            for (Item item : chr.getInventory(MapleInventoryType.ETC).list()) {
//                if ((item.getPosition() > i * 100 + 100) && (item.getPosition() < i * 100 + 200)) {
//                    addItemPosition(pw, item, false, true);
//                    addItemInfo(pw, item, chr);
//                }
//            }
//            pw.writeInt(-1);
//        }
        pw.write(new byte[21]); // new
//        pw.write(new byte[9]); // new (9) -> not needed anymore? (177 -> 178)
    }

    public static void addPotionPotInfo(PacketWriter pw, MapleCharacter chr) {
        if (chr.getPotionPots() == null) {
            pw.writeInt(0);
            return;
        }
        pw.writeInt(chr.getPotionPots().size());
        for (MaplePotionPot p : chr.getPotionPots()) {
            pw.writeInt(p.getId());
            pw.writeInt(p.getMaxValue());
            pw.writeInt(p.getHp());
            pw.writeInt(0);
            pw.writeInt(p.getMp());

            pw.writeLong(PacketHelper.getTime(p.getStartDate()));
            pw.writeLong(PacketHelper.getTime(p.getEndDate()));
        }
    }

    public static void addCharStats(PacketWriter pw, MapleCharacter chr) {
        pw.writeInt(chr.getId());
   
        // dwCharacterIDForLog
        pw.writeInt(chr.getId()); 
        
        // dwWorldIDForLog
        pw.writeInt(chr.getClient().getWorld()); 
        
        pw.writeAsciiString(chr.getName(), 13);
        pw.write(chr.getGender());
        pw.write(chr.getSkinColor());
        pw.writeInt(chr.getFace());
        pw.writeInt(chr.getHair());

        pw.write(-1); // nMixBaseHairColor
		pw.write(0); // nMixAddHairColor
		pw.write(0); // nMixHairBaseProb

        pw.write(chr.getLevel());
        pw.writeShort(chr.getJob());
        
        chr.getStat().connectData(pw);
        pw.writeShort(chr.getRemainingAp());
        if (GameConstants.isSeparatedSp(chr.getJob())) {
            int size = chr.getRemainingSpSize();
            pw.write(size);
            for (int i = 0; i < chr.getRemainingSps().length; i++) {
                if (chr.getRemainingSp(i) > 0) {
                    pw.write(i + 1);
                    pw.writeInt(chr.getRemainingSp(i));
                }
            }
        } else {
            pw.writeShort(chr.getRemainingSp());
        }
        pw.writeLong(chr.getExp());
        pw.writeInt(chr.getFame());
        pw.writeInt(0); // Waru points
        pw.writeInt(chr.getGachExp());
        pw.writeInt(chr.getMapId());
        pw.write(chr.getInitialSpawnpoint());
        pw.writeInt(0); // ?
        pw.writeShort(chr.getSubcategory());
        
        if (GameConstants.isDemonSlayer(chr.getJob()) || GameConstants.isXenon(chr.getJob()) || GameConstants.isDemonAvenger(chr.getJob())) {
            pw.writeInt(chr.getFaceMarking());
        }
        
        pw.write(chr.getFatigue());
        pw.writeInt(GameConstants.getCurrentDate());
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            pw.writeInt(chr.getTrait(t).getTotalExp());
        }
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            pw.writeShort(0); //today's stats
        }
        pw.write(0);
        pw.writeLong(getTime(-2));
        
        pw.writeInt(chr.getStat().pvpExp);
        pw.write(chr.getStat().pvpRank);
        pw.writeInt(chr.getBattlePoints());
        pw.write(5); // pvp mode level
        pw.write(6); // pvp mode type
        pw.writeInt(0); // event points
        
        addPartTimeJob(pw, MapleCharacter.getPartTime(chr.getId()));
        
        /*
         * Character Card
         * 
         */
        for (int i = 0; i < 9; i++) {
            pw.writeInt(0);
            pw.write(0);
            pw.writeInt(0);
        }
        
        pw.writeReversedLong(getTime(System.currentTimeMillis())); // account last login

        // more burning stuff, names + help by Novak
        pw.writeLong(0); // nBurningEventStartDate
        pw.writeLong(0); // nBurningEventEndDate
        pw.writeInt(0); // nBurningMinLvl
        pw.writeInt(0); // nBurningMaxLvl
        pw.writeInt(0); //nBurningEffectID?
        pw.write(0); // bBurning
        pw.writeInt(0); // nBurningItemID?
        pw.writeInt(0); // nBurningItemID?
        
//        for (int i = 0; i < 7; i++) {
//            pw.writeInt(0); // not sure what all these 0's are, v177
//        }
//        for(int i = 0; i < 8; i++){
//            pw.write(0); // more 0s, v178
//        }
    }

    public static void addCharLook(PacketWriter pw, MapleCharacterLook chr, boolean mega, boolean second) {
        pw.write(second ? chr.getSecondGender() : chr.getGender());
        pw.write(second ? chr.getSecondSkinColor() : chr.getSkinColor());
        pw.writeInt(second ? chr.getSecondFace() : chr.getFace());
        pw.writeInt(chr.getJob());
        pw.write(mega ? 0 : 1);
        pw.writeInt(second ? chr.getSecondHair() : chr.getHair());

        final Map<Byte, Integer> myEquip = new LinkedHashMap<>();
        final Map<Byte, Integer> maskedEquip = new LinkedHashMap<>();
        final Map<Byte, Integer> totemEquip = new LinkedHashMap<>();
        final Map<Byte, Integer> equip = second ? chr.getSecondEquips(true) : chr.getEquips(true);
        
        // System.out.println("There are " + equip.entrySet().size() + " equips.");
        for (final Entry<Byte, Integer> item : equip.entrySet()) {
            if ((item.getKey()).byteValue() < -127) {
	            continue;
	        }
		    
            byte pos = (byte) ((item.getKey()).byteValue() * -1);
		
		    if ((pos < 100) && (myEquip.get(Byte.valueOf(pos)) == null)) {
		        myEquip.put(Byte.valueOf(pos), item.getValue());
		    } else if ((pos > 100) && (pos != 111)) {
		        pos = (byte) (pos - 100);
		            if (myEquip.get(Byte.valueOf(pos)) != null) {
		                maskedEquip.put(Byte.valueOf(pos), myEquip.get(Byte.valueOf(pos)));
		                totemEquip.put(Byte.valueOf(pos), item.getValue());
		            }
		        myEquip.put(Byte.valueOf(pos), item.getValue());
		        totemEquip.put(Byte.valueOf(pos), item.getValue());
		    } else if (myEquip.get(Byte.valueOf(pos)) != null) {
		    	
		        maskedEquip.put(Byte.valueOf(pos), item.getValue());
		        totemEquip.put(Byte.valueOf(pos), item.getValue());
		    }
        }
        
        // System.out.println("There are " + chr.getTotems().entrySet().size() + " totems.");
    	for (final Entry<Byte, Integer> totem : chr.getTotems().entrySet()) {
	        byte pos = (byte) ((totem.getKey()).byteValue() * -1);
	            if (pos < 0 || pos > 2) { //3 totem slots
	            continue;
	        }
		    if (totem.getValue() < 1200000 || totem.getValue() >= 1210000) {
		        continue;
		    }
		    totemEquip.put(Byte.valueOf(pos), totem.getValue());
	    }
	
    	// System.out.println("There are " + myEquip.entrySet().size() + " pieces of equipment.");
	    for (Map.Entry entry : myEquip.entrySet()) {
	        int weapon = ((Integer) entry.getValue()).intValue();
		    if (GameConstants.getWeaponType(weapon) == (second ? MapleWeaponType.LONG_SWORD : MapleWeaponType.BIG_SWORD)) {
		        continue;
		    }
		    pw.write(((Byte) entry.getKey()).byteValue());
		    pw.writeInt(((Integer) entry.getValue()).intValue());
	    }
	    
	    pw.write(255);
	
	    for (Map.Entry entry : maskedEquip.entrySet()) {
	        pw.write(((Byte) entry.getKey()).byteValue());
	        pw.writeInt(((Integer) entry.getValue()).intValue());
	    }
	    
	    pw.write(255);
	
	    for (Map.Entry entry : totemEquip.entrySet()) {
	        pw.write(((Byte) entry.getKey()).byteValue());
	        pw.writeInt(((Integer) entry.getValue()).intValue());
	    }
	    
	    pw.write(255);

        Integer cWeapon = equip.get(Byte.valueOf((byte) -111));
        pw.writeInt(cWeapon != null ? cWeapon.intValue() : 0);
        
        Integer Weapon = equip.get(Byte.valueOf((byte) -11));
        pw.writeInt(Weapon != null ? Weapon.intValue() : 0);
        
        boolean zero = GameConstants.isZero(chr.getJob());
        Integer subWeapon = equip.get(Byte.valueOf((byte) -10));
        pw.writeInt(!zero && subWeapon != null ? subWeapon.intValue() : 0);
        
        pw.write(0); // mercedes ear
        
        // all 3 pets unique id
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);
        
	    if (GameConstants.isDemonSlayer(chr.getJob()) || GameConstants.isXenon(chr.getJob()) || GameConstants.isDemonAvenger(chr.getJob())) {
	        pw.writeInt(chr.getFaceMarking());
	    } else if (GameConstants.isZero(chr.getJob())) {
	        pw.write(1);
	    }
	    
	    /*
	     if (JobConstants.isBeastTamer(chr.getJob())) { // tale and ears
			pw.write(1);
			pw.writeInt(5010116);
			pw.write(1);
			pw.writeInt(5010119);
		 }
	     */
	    pw.write(0); // mixed hair color
	    pw.write(0); // mixed hair percent
	}

    public static void addExpirationTime(PacketWriter pw, long time) {
        pw.writeLong(getTime(time));
    }

    public static void addItemPosition(PacketWriter pw, Item item, boolean trade, boolean bagSlot) {
        if (item == null) {
            pw.write(0);
            return;
        }
        short pos = item.getPosition();
        if (pos <= -1) {
            pos = (short) (pos * -1);
            if ((pos > 100) && (pos < 1000)) {
                pos = (short) (pos - 100);
            }
        }
        if (bagSlot) {
            pw.writeInt(pos % 100 - 1);
        } else if ((!trade) && (item.getType() == 1)) {
            pw.writeShort(pos);
        } else {
            pw.write(pos);
        }
    }

    public static void addItemInfo(PacketWriter pw, Item item) {
        addItemInfo(pw, item, null);
    }

    public static void addItemInfo(final PacketWriter pw, final Item item, final MapleCharacter chr) {
        pw.write(item.getPet() != null ? 3 : item.getType());
        pw.writeInt(item.getItemId());
        boolean hasUniqueId = item.getUniqueId() > 0 && !GameConstants.isMarriageRing(item.getItemId()) && item.getItemId() / 10000 != 166;
        //marriage rings arent cash items so dont have uniqueids, but we assign them anyway for the sake of rings
        pw.write(hasUniqueId ? 1 : 0);
        if (hasUniqueId) {
            pw.writeLong(item.getUniqueId());
        }
        if (item.getPet() != null) { // Pet
            addPetItemInfo(pw, item, item.getPet(), true);
        } else {
            addExpirationTime(pw, item.getExpiration());
            pw.writeInt(chr == null ? -1 : chr.getExtendedSlots().indexOf(item.getItemId()));
            if (item.getType() == 1) {
                final Equip equip = Equip.calculateEquipStats((Equip) item);
                //final Equip equip = Equip.calculateEquipStatsTest((Equip) item);
                addEquipStats(pw, equip);
                //addEquipStatsTest(pw, equip);
                addEquipBonusStats(pw, equip, hasUniqueId);
            } else {
                pw.writeShort(item.getQuantity());
                pw.writeMapleAsciiString(item.getOwner());
                pw.writeShort(item.getFlag());
                if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId()) || item.getItemId() / 10000 == 287) {
                    pw.writeLong(item.getInventoryId() <= 0 ? -1 : item.getInventoryId());
                }
            }
        }
    }

    public static void addEquipStatsTest(PacketWriter pw, Equip equip) {
        int mask;
        int masklength = 2;
        for (int i = 1; i <= masklength; i++) {
            mask = 0;
            if (equip.getStatsTest().size() > 0) {
                for (EquipStat stat : equip.getStatsTest().keySet()) {
                    if (stat.getPosition() == i) {
                        mask += stat.getValue();
                    }
                }
            }
            pw.writeInt(mask);
            if (mask != 0) {
                for (EquipStat stat : equip.getStatsTest().keySet()) {
                    if (stat.getDatatype() == 8) {
                        pw.writeLong(equip.getStatsTest().get(stat));
                    } else if (stat.getDatatype() == 4) {
                        pw.writeInt(equip.getStatsTest().get(stat).intValue());
                    } else if (stat.getDatatype() == 2) {
                        pw.writeShort(equip.getStatsTest().get(stat).shortValue());
                    } else if (stat.getDatatype() == 1) {
                        pw.write(equip.getStatsTest().get(stat).byteValue());
                    }
                }
            }
        }
    }

    public static void addEquipStats(PacketWriter pw, Equip equip) {
        int head = 0;
        if (equip.getStats().size() > 0) {
            for (EquipStat stat : equip.getStats()) {
                head |= stat.getValue();
            }
        }
        pw.writeInt(head);
        if (head != 0) {
            if (equip.getStats().contains(EquipStat.SLOTS)) {
                pw.write(equip.getUpgradeSlots());
            }
            if (equip.getStats().contains(EquipStat.LEVEL)) {
                pw.write(equip.getLevel());
            }
            if (equip.getStats().contains(EquipStat.STR)) {
                pw.writeShort(equip.getStr());
            }
            if (equip.getStats().contains(EquipStat.DEX)) {
                pw.writeShort(equip.getDex());
            }
            if (equip.getStats().contains(EquipStat.INT)) {
                pw.writeShort(equip.getInt());
            }
            if (equip.getStats().contains(EquipStat.LUK)) {
                pw.writeShort(equip.getLuk());
            }
            if (equip.getStats().contains(EquipStat.MHP)) {
                pw.writeShort(equip.getHp());
            }
            if (equip.getStats().contains(EquipStat.MMP)) {
                pw.writeShort(equip.getMp());
            }
            if (equip.getStats().contains(EquipStat.WATK)) {
                pw.writeShort(equip.getWatk());
            }
            if (equip.getStats().contains(EquipStat.MATK)) {
                pw.writeShort(equip.getMatk());
            }
            if (equip.getStats().contains(EquipStat.WDEF)) {
                pw.writeShort(equip.getWdef());
            }
//            if (equip.getStats().contains(EquipStat.MDEF)) {
//                pw.writeShort(equip.getMdef());
//            }
//            if (equip.getStats().contains(EquipStat.ACC)) {
//                pw.writeShort(equip.getAcc());
//            }
//            if (equip.getStats().contains(EquipStat.AVOID)) {
//                pw.writeShort(equip.getAvoid());
//            }
            if (equip.getStats().contains(EquipStat.HANDS)) {
                pw.writeShort(equip.getHands());
            }
            if (equip.getStats().contains(EquipStat.SPEED)) {
                pw.writeShort(equip.getSpeed());
            }
            if (equip.getStats().contains(EquipStat.JUMP)) {
                pw.writeShort(equip.getJump());
            }
            if (equip.getStats().contains(EquipStat.FLAG)) {
                pw.writeShort(equip.getFlag());
            }
            if (equip.getStats().contains(EquipStat.INC_SKILL)) {
                pw.write(equip.getIncSkill() > 0 ? 1 : 0);
            }
            if (equip.getStats().contains(EquipStat.ITEM_LEVEL)) {
                pw.write(Math.max(equip.getBaseLevel(), equip.getEquipLevel())); // Item level
            }
            if (equip.getStats().contains(EquipStat.ITEM_EXP)) {
                pw.writeLong(equip.getExpPercentage() * 100000); // Item Exp... 10000000 = 100%
            }
            if (equip.getStats().contains(EquipStat.DURABILITY)) {
                pw.writeInt(equip.getDurability());
            }
            if (equip.getStats().contains(EquipStat.VICIOUS_HAMMER)) {
                pw.writeInt(equip.getViciousHammer());
            }
            if (equip.getStats().contains(EquipStat.PVP_DAMAGE)) {
                pw.writeShort(equip.getPVPDamage());
            }
            if (equip.getStats().contains(EquipStat.ENCHANT_BUFF)) {
                pw.writeShort(equip.getEnhanctBuff());
            }
            if (equip.getStats().contains(EquipStat.DURABILITY_SPECIAL)) {
                pw.writeInt(equip.getDurability());
            }
            if (equip.getStats().contains(EquipStat.REQUIRED_LEVEL)) {
                pw.write(equip.getReqLevel());
            }
            if (equip.getStats().contains(EquipStat.YGGDRASIL_WISDOM)) {
                pw.write(equip.getYggdrasilWisdom());
            }
            if (equip.getStats().contains(EquipStat.FINAL_STRIKE)) {
                pw.write(equip.getFinalStrike());
            }
            if (equip.getStats().contains(EquipStat.BOSS_DAMAGE)) {
                pw.write(equip.getBossDamage());
            }
            if (equip.getStats().contains(EquipStat.IGNORE_PDR)) {
                pw.write(equip.getIgnorePDR());
            }
        } else {
            /*
             *   if ( v3 >= 0 )
             *     v36 = 0;
             *   else
             *     v36 = (unsigned __int8)CInPacket::Decode1(a2);
             */
//            pw.write(0); //unknown
        }
        addEquipSpecialStats(pw, equip);
    }

    public static void addEquipSpecialStats(PacketWriter pw, Equip equip) {
        int head = 0;
        if (equip.getSpecialStats().size() > 0) {
            for (EquipSpecialStat stat : equip.getSpecialStats()) {
                head |= stat.getValue();
            }
        }
        pw.writeInt(head);
//        System.out.println("mask " + head);

        if (head != 0) {
            if (equip.getSpecialStats().contains(EquipSpecialStat.TOTAL_DAMAGE)) {
//                System.out.println("TOTAL_DAMAGE " + equip.getTotalDamage());
                pw.write(equip.getTotalDamage());
            }
            if (equip.getSpecialStats().contains(EquipSpecialStat.ALL_STAT)) {
//                System.out.println("ALL_STAT " + equip.getAllStat());
                pw.write(equip.getAllStat());
            }
            if (equip.getSpecialStats().contains(EquipSpecialStat.KARMA_COUNT)) {
//                System.out.println("KARMA_COUNT " + equip.getKarmaCount());
                pw.write(equip.getKarmaCount());
            }
            if (equip.getSpecialStats().contains(EquipSpecialStat.UNK8)) {
//                System.out.println("unk8 " + System.currentTimeMillis());
                pw.writeLong(System.currentTimeMillis());
            }
            if (equip.getSpecialStats().contains(EquipSpecialStat.CAN_ENHANCE)) {
//                System.out.println("unk10 " + 1);
                int reqLevel = ItemConstants.getLevelByEquip(equip);
                pw.writeInt(equip.getEnhance() < ItemConstants.getMaxStarsByItemLevel(reqLevel) ? 0x100 : 0);
                // Previously UNK10, this makes stars go blue if a != 1 in the int [00 0a 00 00]
            }
        }
    }

//    public static void addEquipBonusStats(PacketWriter pw, Equip equip, boolean hasUniqueId) {
//        pw.writeMapleAsciiString(equip.getOwner());
//        pw.write(equip.getState()); // 17 = rare, 18 = epic, 19 = unique, 20 = legendary, potential flags. special grade is 14 but it crashes
//        pw.write(equip.getEnhance());
//        pw.writeShort(equip.getPotential1());
//        pw.writeShort(equip.getPotential2());
//        pw.writeShort(equip.getPotential3());
//        pw.writeShort(equip.getBonusPotential1());
//        pw.writeShort(equip.getBonusPotential2());
//        pw.writeShort(equip.getBonusPotential3());
//        pw.writeShort(equip.getFusionAnvil() % 100000);
//        pw.writeShort(equip.getSocketState());
//        pw.writeShort(equip.getSocket1() % 10000); // > 0 = mounted, 0 = empty, -1 = none.
//        pw.writeShort(equip.getSocket2() % 10000);
//        pw.writeShort(equip.getSocket3() % 10000);
//        if (!hasUniqueId) {
//            pw.writeLong(equip.getInventoryId() <= 0 ? -1 : equip.getInventoryId()); //some tracking ID
//        }
//        pw.writeLong(getTime(-2));
//        pw.writeInt(-1); //?
//        
//    }
    public static void addEquipBonusStats(PacketWriter pw, Equip equip, boolean hasUniqueId) {
        pw.writeMapleAsciiString(equip.getOwner());
        pw.write(equip.getStateByPotential(equip.getPotential())); // 17 = rare, 18 = epic, 19 = unique, 20 = legendary, potential flags. special grade is 14 but it crashes
        pw.write(equip.getEnhance());
        pw.writeShort(equip.getPotentialByLine(0));
        pw.writeShort(equip.getPotentialByLine(1));
        pw.writeShort(equip.getPotentialByLine(2));
        pw.writeShort(equip.getBonusPotentialByLine(0));
        pw.writeShort(equip.getBonusPotentialByLine(1));
        pw.writeShort(equip.getBonusPotentialByLine(2));
        pw.writeShort(equip.getFusionAnvil() % 100000);
        pw.writeShort(equip.getSocketState());
        pw.writeShort(equip.getSocketByNmb(0) % 10000); // > 0 = mounted, 0 = empty, -1 = none.
        pw.writeShort(equip.getSocketByNmb(1) % 10000);
        pw.writeShort(equip.getSocketByNmb(2) % 10000);
        if (!hasUniqueId) {
            pw.writeLong(equip.getInventoryId() <= 0 ? -1 : equip.getInventoryId()); //some tracking ID
        }
        pw.writeLong(getTime(-2));
        pw.writeInt(-1); //?
        // new 142
        pw.writeLong(0);
        pw.writeLong(getTime(-2));
        pw.writeLong(0);
        pw.writeLong(0);
        pw.writeShort(0); // new
        pw.writeShort(0); // new
        pw.writeShort(0); // new
    }

    public static void serializeMovementList(PacketWriter lew, List<LifeMovementFragment> moves) {
        lew.write(moves.size());
        for (LifeMovementFragment move : moves) {
            move.serialize(lew);
        }
    }

    public static void addAnnounceBox(PacketWriter pw, MapleCharacter chr) {
        if ((chr.getPlayerShop() != null) && (chr.getPlayerShop().isOwner(chr)) && (chr.getPlayerShop().getShopType() != 1) && (chr.getPlayerShop().isAvailable())) {
            addInteraction(pw, chr.getPlayerShop());
        } else {
            pw.write(0);
        }
    }

    public static void addInteraction(PacketWriter pw, IMaplePlayerShop shop) {
        pw.write(shop.getGameType());
        pw.writeInt(((AbstractPlayerStore) shop).getObjectId());
        pw.writeMapleAsciiString(shop.getDescription());
        if (shop.getShopType() != 1) {
            pw.write(shop.getPassword().length() > 0 ? 1 : 0);
        }
        pw.write(shop.getItemId() % 10);
        pw.write(shop.getSize());
        pw.write(shop.getMaxSize());
        if (shop.getShopType() != 1) {
            pw.write(shop.isOpen() ? 0 : 1);
        }
    }

    public static void addCharacterInfo(PacketWriter pw, MapleCharacter chr) {
        long mask = 0xFF_FF_FF_FF_FF_FF_FF_FFL;
        pw.writeLong(mask);

        // combat orders
        pw.write(0);
        
        // pet active skill cool time
        for (int i = 0; i < 3; i++) {
            pw.writeInt(0);
        }
        
        pw.write(0);
        
        pw.write(0);
        
        pw.writeInt(0);
        
        // ?
        pw.write(0);
        
        if ((mask & 1) != 0) {
            addCharStats(pw, chr);

            pw.write(chr.getBuddylist().getCapacity());

            pw.write(chr.getBlessOfFairyOrigin() != null);
            if (chr.getBlessOfFairyOrigin() != null) {
                pw.writeMapleAsciiString(chr.getBlessOfFairyOrigin());
            }

            pw.write(chr.getBlessOfEmpressOrigin() != null);
            if (chr.getBlessOfEmpressOrigin() != null) {
                pw.writeMapleAsciiString(chr.getBlessOfEmpressOrigin());
            }

            MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER));
            pw.write((ultExplorer != null) && (ultExplorer.getCustomData() != null));
            if ((ultExplorer != null) && (ultExplorer.getCustomData() != null)) {
                pw.writeMapleAsciiString(ultExplorer.getCustomData());
            }
        }
        if ((mask & 2) != 0) {
        	pw.writeLong(chr.getMeso());
        }
        if ((mask & 8) != 0) {
            addInventoryInfo(pw, chr);
        }

        if ((mask & 0x100) != 0) {
            addSkillInfo(pw, chr);
//            pw.write(HexTool.getByteArrayFromHexString("01 09 00 22 A8 62 02 00 00 00 00 00 80 05 BB 46 E6 17 02 2C A8 62 02 10 00 00 00 00 80 05 BB 46 E6 17 02 80 B8 C4 04 01 00 00 00 00 80 05 BB 46 E6 17 02 8D A8 62 02 01 00 00 00 00 80 05 BB 46 E6 17 02 BA B4 C4 04 01 00 00 00 00 80 05 BB 46 E6 17 02 20 A8 62 02 01 00 00 00 00 80 05 BB 46 E6 17 02 BB B4 C4 04 01 00 00 00 00 80 05 BB 46 E6 17 02 21 A8 62 02 01 00 00 00 00 80 05 BB 46 E6 17 02 69 A8 62 02 00 00 00 00 00 80 05 BB 46 E6 17 02 00 00 01 00 00 00 84 E5 16 00 84 E5 16 00 03 B4 C4 04 01 00 00 40 E0 FD 3B 37 4F 01 "));
        }
        
        if ((mask & 0x8000) != 0) {
            addCoolDownInfo(pw, chr);
        }
        
        if ((mask & 0x200) != 0) {
            addStartedQuestInfo(pw, chr);
        }
        
        if ((mask & 0x4000) != 0) {
            addCompletedQuestInfo(pw, chr);
        }
        
        if ((mask & 0x400) != 0) {
            pw.writeShort(0);
        }
        
        if ((mask & 0x800) != 0) {
            addRingInfo(pw, chr);
        }
        
        if ((mask & 0x1000) != 0) {
            addRocksInfo(pw, chr);
        }
        
        if ((mask & 0x20000) != 0) {
            pw.writeInt(0);
        }
        
        if ((mask & 0x10000) != 0) {
            addMonsterBookInfo(pw, chr);
        }

        // ?
        pw.writeShort(0);
        
        // ?
        pw.writeInt(0);

        
        if ((mask & 0x800000) != 0) {
        	pw.writeShort(0);
        }
        
        if ((mask & 0x40000) != 0) {
            chr.QuestInfoPacket(pw);
        }
        
        if ((mask & 0x2000) != 0) {
        	pw.writeShort(0);
        }

        // new 178 idb
        pw.write(1);
        /*
        if(byte ^ && mask & 0x100){
            for(int){
                int
                String
            }
        }
         */
        // end new 178 idb

        if ((mask & 0x1000) != 0) {
        	pw.writeInt(0);
        }

        pw.writeInt(0); // new 178?
        
        if ((mask & 0x200000) != 0) {
            addJaguarInfo(pw, chr);
        }
        
        // 0x800
        if (GameConstants.isZero(chr.getJob())) {
            addZeroInfo(pw, chr);
            //chr.getStat().zeroData(pw, chr);
        }

        if ((mask & 0x4000000) != 0) {
        	pw.writeShort(0);
        }

        if ((mask & 0x10000000) != 0) {
            addStealSkills(pw, chr);
        }
        
        if ((mask & 0x80000000) != 0) {
            addAbilityInfo(pw, chr);
        }
        
        if ((mask & 0x10000) != 0) {
        	pw.writeShort(0);
        }

        // ...
        pw.writeInt(0);
        
        // ...
        pw.write(0);

        if ((mask & 0x1) != 0) {
        	pw.writeInt(chr.getHonorLevel()); // honor level
            pw.writeInt(chr.getHonourExp()); // honor xp
        }
        
        if ((mask & 0x2) != 0) {
        	pw.write(1);
        	pw.writeShort(0);
        }
        
        if ((mask & 0x4) != 0) {
        	pw.write(0);
        }
        
        // 0x08
        if (GameConstants.isAngelicBuster(chr.getJob())) {
	        pw.writeInt(21173); //face
	        pw.writeInt(37141); //hair
	        pw.writeInt(1051291);
        } else {
        	pw.writeInt(0);
        	pw.writeInt(0);
        	pw.writeInt(0);
        }
    	pw.write(0);
    	pw.writeInt(-1);
    	pw.writeInt(0);
        
    	pw.writeInt(0);
    	pw.writeInt(0);
        pw.writeInt(0);
        pw.writeLong(getTime(-2));

        // new 178 ?
        pw.writeShort(0);
        pw.writeInt(0);
        
//        if ((mask & 0x40000) != 0) {
//        	pw.writeInt(1);
//            pw.writeInt(0);
//            pw.writeLong(0);
//            pw.writeMapleAsciiString("");
//            pw.writeInt(0);
//        }

        // ? Core?
        if ((mask & 0x10) != 0) {
            pw.writeShort(0);
            pw.writeShort(0);
        }
        
        // FARM_POTENTIAL::Decode
        if ((mask & 0x20) != 0) {
        	pw.writeInt(0); // farm monsters length (if length > 1 for each monster int id and long expire)
        }
        
//        pw.writeInt(0); // removed 178?
//        pw.writeShort(0); // removed 178?

        // FarmUserInfo::Decode
        // FarmSubInfo::Decode
        if ((mask & 0x40) != 0) {
            addFarmInfo(pw, chr.getClient(), (byte) 2);
            pw.writeInt(-1);
            pw.writeInt(0);
        }

        // MemorialCubeInfo::Decode
        if ((mask & 0x80) != 0) {
        	pw.write(0);
        }

        // GW_LikePoint::Decode
        if ((mask & 0x400) != 0) {
        	pw.writeInt(0);
            pw.writeLong(getTime(-2));
            pw.writeInt(0);
        }

        // RunnerGameRecord::Decode
        if ((mask & 0x20000) != 0) {
            pw.writeInt(chr.getId());
            pw.writeInt(0); 
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeLong(getTime(-2));
            pw.writeInt(10);
        }
//        {
//            //new v177
//            pw.writeInt(0);
//            pw.writeInt(0);
//            pw.writeInt(0);
//            pw.writeInt(0);
//        }

        if((mask & 0x80000) != 0){
            pw.writeInt(0);
            pw.writeInt(0);
            /*
            for(int ^){
                int
                byte
                byte
                byte
            }
            int ^
             */
        }

        pw.writeShort(0);
        /*
        for(short ^){
            int
            string
        }
         */

        // wtf is this
        pw.write(HexTool.getByteArrayFromHexString("00 00 00 00 00 00 01 00 09 00 00 00 17 00 63 68 65 63 6B 31 3D 30 3B 63 44 61 74 65 3D 31 36 2F 31 30 2F 31 35"));

        // ...
        if((mask & 0x40000) != 0) {
            pw.writeShort(0);
//            pw.writeShort(2);
//
//            // Monster Collection (int -> string)
//            pw.writeInt(9);
//            pw.writeShort(0);//string
//
//            pw.writeInt(6);
//            pw.writeShort(0);//string
        }
        pw.write(0); // m_bFarmOnline

        
        // DecodeTextEquipInfo
        pw.writeInt(0);
        /*
        for(int ^){
            int
            String
        }
         */

        // EquipExt
        if((mask & 0x100000) != 0) {
            pw.writeShort(0);
            /*
            for(short ^){
                int
                int
            }
             */
        }

//        pw.writeInt(0); // new 178 idb

        // new 178 idb
        if((mask & 0x200000) != 0){
            pw.writeInt(chr.getVMatrixRecords().size());
            for(VMatrixRecord vmr : chr.getVMatrixRecords()){
                vmr.encode(pw);
            }
        }
        // end new 178 idb
        
        if ((mask & 0x8000000) != 0) {
        	pw.write(1);
        	pw.write(0);
        	pw.writeInt(1);
        	pw.writeInt(0);
        	pw.writeInt(100);
        	pw.writeLong(getTime(-1));
        	pw.writeShort(0);
            pw.writeShort(0);
        }
        
        if ((mask & 0x8000) != 0) {
        	pw.write(0);
        }
        
        if ((mask & 0x400000) != 0) { // chosen skills?
        	pw.writeInt(0);
        	pw.writeInt(0);
        }

        if ((mask & 0x10000000) != 0) { // 178: 2000 -> 10000000
            addCoreAura(pw, chr); //84 bytes + boolean (85 total)
//            addStealSkills(pw, chr);
            pw.write(1);
        }
        
        if ((mask & 0x1000000) != 0) { // some huge number, but mask is all 1s, so whatevs again
        	pw.writeShort(0); //for <short> length write 2 shorts
        }

        if((mask & 0x20000000) != 0) {
            // red leaf information
            pw.writeInt(chr.getAccountID());
            pw.writeInt(chr.getId());
            pw.writeInt(0);
            pw.writeInt(0);
            pw.write(new byte[32]);
//         addRedLeafInfo(pw, chr);
        }
    }

    public static int getSkillBook(final int i) {
        switch (i) {
            case 1:
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 2;
        }
        return 0;
    }

    public static void addAbilityInfo(final PacketWriter pw, MapleCharacter chr) {
        final List<InnerSkillValueHolder> skills = chr.getInnerSkills();
        pw.writeShort(skills.size());
        for (int i = 0; i < skills.size(); ++i) {
            pw.write(i + 1); // key
            pw.writeInt(skills.get(i).getSkillId()); //d 7000000 id ++, 71 = char cards
            pw.write(skills.get(i).getSkillLevel()); // level
            pw.write(skills.get(i).getRank()); //rank, C, B, A, and S
        }

    }

    public static void addCoreAura(PacketWriter pw, MapleCharacter chr) {
        /*MapleCoreAura aura = chr.getCoreAura();
        pw.writeInt(aura.getId()); // nvr change
        pw.writeInt(chr.getId());
        int level = chr.getSkillLevel(80001151) > 0 ? chr.getSkillLevel(80001151) : chr.getSkillLevel(1214);
        pw.writeInt(level);
        
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(aura.getExpire());//timer
        pw.writeInt(0);
        
        pw.writeInt(aura.getAtt());//wep att
        pw.writeInt(aura.getDex());//dex
        pw.writeInt(aura.getLuk());//luk
        pw.writeInt(aura.getMagic());//magic att
        pw.writeInt(aura.getInt());//int
        pw.writeInt(aura.getStr());//str
        
        pw.writeInt(0);
        pw.writeInt(aura.getTotal());//max
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeLong(getTime(System.currentTimeMillis() + 86400000L));
        pw.writeInt(0);
        pw.write(GameConstants.isJett(chr.getJob()) ? 1 : 0);*/
    	
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);

    	pw.writeLong(getTime(-2));
    	pw.write(0);
    }

    public static void addStolenSkills(PacketWriter pw, MapleCharacter chr, int jobNum, boolean writeJob) {
        if (writeJob) {
            pw.writeInt(jobNum);
        }
        int count = 0;
        if (chr.getStolenSkills() != null) {
            for (Pair<Integer, Boolean> sk : chr.getStolenSkills()) {
                if (GameConstants.getJobNumber(sk.left / 10000) == jobNum) {
                    pw.writeInt(sk.left);
                    count++;
                    if (count >= GameConstants.getNumSteal(jobNum)) {
                        break;
                    }
                }
            }
        }
        while (count < GameConstants.getNumSteal(jobNum)) { //for now?
            pw.writeInt(0);
            count++;
        }
    }

    public static void addChosenSkills(PacketWriter pw, MapleCharacter chr) {
        for (int i = 1; i <= 5; i++) {
            boolean found = false;
            if (chr.getStolenSkills() != null) {
                for (Pair<Integer, Boolean> sk : chr.getStolenSkills()) {
                    if (GameConstants.getJobNumber(sk.left / 10000) == i && sk.right) {
                        pw.writeInt(sk.left);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                pw.writeInt(0);
            }
        }
    }

    public static void addStealSkills(final PacketWriter pw, final MapleCharacter chr) {
        /*for (int i = 1; i <= 5; i++) {
            addStolenSkills(pw, chr, i, false); // 52
        }
        addChosenSkills(pw, chr); // 16*/
    	
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	
    	pw.writeInt(0);
    	pw.writeInt(0);
    	
    	pw.writeInt(0);
    	pw.writeInt(0);
    	
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	pw.writeInt(0);
    	
    }

    public static void addMonsterBookInfo(PacketWriter pw, MapleCharacter chr) {
        if (chr.getMonsterBook().getSetScore() > 0) {
            chr.getMonsterBook().writeFinished(pw);
        } else {
            chr.getMonsterBook().writeUnfinished(pw);
        }

        pw.writeInt(chr.getMonsterBook().getSet());
    }

    public static void addPetItemInfo(PacketWriter pw, Item item, MaplePet pet, boolean active) {
        if (item == null) {
            pw.writeLong(PacketHelper.getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            addExpirationTime(pw, item.getExpiration() <= System.currentTimeMillis() ? -1L : item.getExpiration());
        }
        pw.writeInt(-1);
        pw.writeAsciiString(pet.getName(), 13);
        pw.write(pet.getLevel());
        pw.writeShort(pet.getCloseness());
        pw.write(pet.getFullness());
        if (item == null) {
            pw.writeLong(PacketHelper.getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            addExpirationTime(pw, item.getExpiration() <= System.currentTimeMillis() ? -1L : item.getExpiration());
        }
        pw.writeShort(0);
        pw.writeShort(pet.getFlags());
        pw.writeInt((pet.getPetItemId() == 5000054) && (pet.getSecondsLeft() > 0) ? pet.getSecondsLeft() : 0);
        pw.writeShort(0);
        pw.write(active ? 0 : pet.getSummoned() ? pet.getSummonedValue() : 0);
        for (int i = 0; i < 4; i++) {
            pw.write(0);
        }
        pw.writeInt(-1); //new v140
        pw.writeShort(100); //new v140
    }

    public static void addShopInfo(PacketWriter pw, MapleShop shop, MapleClient c) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        pw.write(shop.getRanks().size() > 0 ? 1 : 0);

        if (shop.getRanks().size() > 0) {
            pw.write(shop.getRanks().size());
            for (Pair s : shop.getRanks()) {
                pw.writeInt(((Integer) s.left).intValue());
                pw.writeMapleAsciiString((String) s.right);
            }
        }
        pw.writeShort(shop.getItems().size() + c.getPlayer().getRebuy().size());
        for (MapleShopItem item : shop.getItems()) {
            addShopItemInfo(pw, item, shop, ii, null, c.getPlayer());
        }
        for (Item i : c.getPlayer().getRebuy()) {
            addShopItemInfo(pw, new MapleShopItem(i.getItemId(), (int) ii.getPrice(i.getItemId()), i.getQuantity(), i.getPosition()), shop, ii, i, c.getPlayer());
        }
    }

    /*
     * Categories:
     * 0 - No Tab
     * 1 - Equip
     * 2 - Use
     * 3 - Setup
     * 4 - Etc
     * 5 - Recipe
     * 6 - Scroll
     * 7 - Special
     * 8 - 8th Anniversary
     * 9 - Button
     * 10 - Invitation Ticket
     * 11 - Materials
     * 12 - Maple
     * 13 - Homecoming
     * 14 - Cores
     * 80 - JoeJoe
     * 81 - Hermoninny
     * 82 - Little Dragon
     * 83 - Ika
     */
    public static void addShopItemInfo(PacketWriter pw, MapleShopItem item, MapleShop shop, MapleItemInformationProvider ii, Item i, MapleCharacter chr) {
    	pw.writeInt(item.getItemId()); // nItemID
        pw.writeInt(item.getPrice());  // nPrice
        pw.writeInt(0); // nTokenItemID
        pw.writeInt(0); // nTokenPrice
        pw.writeInt(0); // nPointQuestID
        pw.writeInt(0); // nPointPrice
        pw.writeInt(0); // nStarCoin
        pw.writeInt(0); // nQuestExID 
        pw.writeMapleAsciiString(""); // sQuestExKey
        pw.writeInt(0); // nQuestExValue
        pw.writeInt(1440 * item.getExpiration()); // nItemPeriod
        pw.writeInt(item.getMinLevel()); // nLevelLimited
        pw.writeShort(0); // nShowLevMin
        pw.writeShort(0); // nShowLevMax
        pw.writeInt(0); // nQuestID
      //  pw.write(0);
        pw.writeLong(getTime(-2L)); // ftSellStart
        pw.writeLong(getTime(-1L)); // ftSellEnd
        pw.writeInt(item.getCategory()); // nTabIndex
        
       // pw.write(0); // bWorldBlock
        /*if (GameConstants.isEquip(item.getItemId())) { // // bWorldBlock
            pw.write(item.hasPotential() ? 1 : 0);
        } else {
            pw.write(0);
        }*/
        
        pw.writeInt(0); // nPotentialGrade
        pw.writeInt(item.getExpiration() > 0 ? 1 : 0);
        pw.write(0);
        if ((!GameConstants.isThrowingStar(item.getItemId())) && (!GameConstants.isBullet(item.getItemId()))) {
            pw.writeShort(item.getQuantity()); // nQuantity
            pw.writeShort(item.getBuyable()); // nMaxPerSlot
        } else {
            pw.writeAsciiString("333333");
            pw.writeShort(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
            pw.writeShort(ii.getSlotMax(item.getItemId()));
        }

        pw.write(i == null ? 0 : 1);
        if (i != null) {
            addItemInfo(pw, i);
        }
        if (shop.getRanks().size() > 0) {
            pw.write(item.getRank() >= 0 ? 1 : 0);
            if (item.getRank() >= 0) {
                pw.write(item.getRank());
            }
        }
        for (int j = 0; j < 4; j++) {
            pw.writeInt(0); // red leaf high price probably
        }
        addRedLeafInfo(pw, chr);
    }

    public static void addJaguarInfo(PacketWriter pw, MapleCharacter chr) {
    	if (!(chr.getJob() >= 3300 && chr.getJob() <= 3312))
    		return;
    	
        pw.write(chr.getIntNoRecord(GameConstants.JAGUAR));
        for (int i = 0; i < 5; i++) {
            pw.writeInt(0);
        }
    }

    public static void addZeroInfo(PacketWriter pw, MapleCharacter chr) {
        short mask = 0;
        pw.writeShort(mask);
        if ((mask & 1) != 0) {
            pw.write(0); //bool
        }
        if ((mask & 2) != 0) {
            pw.writeInt(0);
        }
        if ((mask & 4) != 0) {
            pw.writeInt(0);
        }
        if ((mask & 8) != 0) {
            pw.write(0);
        }
        if ((mask & 10) != 0) {
            pw.writeInt(0);
        }
        if ((mask & 20) != 0) {
            pw.writeInt(0);
        }
        if ((mask & 40) != 0) {
            pw.writeInt(0);
        }
        if (mask < 0) {
            pw.writeInt(0);
        }
        if ((mask & 100) != 0) {
            pw.writeInt(0);
        }
        if ((mask & 200) != 0) {
        	pw.writeInt(0);
        	pw.writeInt(0);
        	pw.writeInt(0);
        }
    }

    public static void addFarmInfo(PacketWriter pw, MapleClient c, byte gender) {
        pw.writeMapleAsciiString("Creating..."); 
        pw.writeInt(0); // nFarmPoint
        pw.writeInt(0); // nFarmLevel
        pw.writeInt(0); // nFarmExp
        pw.writeInt(0); // nDecoPoint
        pw.writeInt(0); // nFarmCash
        pw.write(gender); // nFarmGender
        pw.writeInt(0); // nFarmTheme
        pw.writeInt(0); // nSlotExtend
        pw.writeInt(1); // nLockerSlotCount
    }

    public static void addRedLeafInfo(PacketWriter pw, MapleCharacter chr) {
        for (int i = 0; i < 4; i++) {
            pw.writeInt(9410165 + i);
            pw.writeInt(chr.getFriendShipPoints()[i]);
        }
    }

    public static void addLuckyLogoutInfo(PacketWriter pw, boolean enable, CashItem item0, CashItem item1, CashItem item2) {
        pw.writeInt(enable ? 1 : 0);
        if (enable) {
            CSPacket.addCSItemInfo(pw, item0);
            CSPacket.addCSItemInfo(pw, item1);
            CSPacket.addCSItemInfo(pw, item2);
        }
    }

    public static void addPartTimeJob(PacketWriter pw, PartTimeJob parttime) {
        pw.write(parttime.getJob());
        if (parttime.getJob() > 0 && parttime.getJob() <= 5) {
            pw.writeReversedLong(parttime.getTime());
        } else {
            pw.writeReversedLong(-2);
        }
        pw.writeInt(parttime.getReward()); // duration
        pw.write(parttime.getReward() > 0);
    }

    public static <E extends Buffstat> void writeSingleMask(PacketWriter pw, E statup) {
        for (int i = GameConstants.MAX_BUFFSTAT; i >= 1; i--) {
            pw.writeInt(i == statup.getPosition() ? statup.getValue() : 0);
        }
    }

   public static <E extends Buffstat> void writeMask(PacketWriter pw, Collection<E> statups) {
        int[] mask = new int[17];
        for (Buffstat statup : statups) {
            mask[(statup.getPosition() - 1)] |= statup.getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            pw.writeInt(mask[(i - 1)]);
        }
    }

    public static <E extends Buffstat> void writeBuffMask(PacketWriter pw, Collection<Pair<E, Integer>> statups) {
        int[] mask = new int[17];
        for (Pair statup : statups) {
            mask[(((Buffstat) statup.left).getPosition() - 1)] |= ((Buffstat) statup.left).getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            pw.writeInt(mask[(i - 1)]);
        }
    }

    public static <E extends Buffstat> void writeBuffMask(PacketWriter pw, Map<E, Integer> statups) {
        int[] mask = new int[17];
        for (Buffstat statup : statups.keySet()) {
            mask[(statup.getPosition() - 1)] |= statup.getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            pw.writeInt(mask[(i - 1)]);
        }
    }
	
    public static void addStorageItems(PacketWriter pw, Collection<Item> items) {
            ArrayListMultimap<Byte, Item> itemmap = ArrayListMultimap.create();
            for (Item item : items) {
                itemmap.put((byte) Math.floor(item.getItemId() / 1000000), item);
            }
            for (byte i = 1; i <= 5; i++) {
                pw.write(itemmap.get(i).size());
                if (!itemmap.get(i).isEmpty()) 
                    for (Item item : itemmap.get(i))
                        addItemInfo(pw, item);
            }
    }
    
    public static void decodeForRemote(PacketWriter pw, MapleCharacter chr, Map<CharacterTemporaryStat, MapleBuffStatValueHolder> statups) {
    	for(Entry<CharacterTemporaryStat, MapleBuffStatValueHolder> stats : statups.entrySet()) {
			CharacterTemporaryStat stat = stats.getKey();
			MapleBuffStatValueHolder mbsvh = stats.getValue();
			
			int value = mbsvh.value;
			int buffid = 0;
			
			if (mbsvh.effect != null) {
				buffid = (mbsvh.effect.isSkill() ? mbsvh.effect.getSourceId() : -mbsvh.effect.getSourceId());
			}
			 

			if (stat == CharacterTemporaryStat.Speed) {
				pw.write(0);
			}
						
			if (stat == CharacterTemporaryStat.ComboCounter) {
				pw.write(0);
			}

			if (stat == CharacterTemporaryStat.WeaponCharge) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.ElementalCharge) {
				pw.writeShort(0);
			}

			if (stat == CharacterTemporaryStat.Stun) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Shock) {
				pw.write(0);
			}

			if (stat == CharacterTemporaryStat.Darkness) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Seal) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Weakness) {
				pw.writeShort(0);
				pw.writeInt(0);
			}
			
			if (stat == CharacterTemporaryStat.WeaknessMdamage) {
				pw.writeShort(0);
				pw.writeInt(0);
			}
			
			if (stat == CharacterTemporaryStat.Curse) {
				pw.writeShort(0);
				pw.writeInt(0);
			}
			
			if (stat == CharacterTemporaryStat.Slow) {
				pw.writeShort(0);
				pw.writeInt(0);
			}
			
			if (stat == CharacterTemporaryStat.PvPRaceEffect) {
				pw.writeShort(0);
				pw.writeInt(0);
			}
			
			if (stat == CharacterTemporaryStat.IceKnight) {
				pw.writeShort(0);
				pw.writeInt(0);
			}
			
			if (stat == CharacterTemporaryStat.TimeBomb) {
				pw.writeShort(0);
				pw.writeInt(0);
			}
			
			if (stat == CharacterTemporaryStat.Team) {
				pw.write(0);
			}
			
			if (stat == CharacterTemporaryStat.DisOrder) {
				pw.writeShort(0);
				pw.writeInt(0);
			}
			
			if (stat == CharacterTemporaryStat.Thread) {
				pw.writeShort(0);
				pw.writeInt(0);
			}
			
			if (stat == CharacterTemporaryStat.Poison) {
				pw.writeShort(0);
			}

			if (stat == CharacterTemporaryStat.Poison) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.ShadowPartner) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Morph) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Ghost) {
				pw.writeShort(0);
			}

			if (stat == CharacterTemporaryStat.Attract) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Magnet) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.MagnetArea) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.NoBulletConsume) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.BanMap) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Barrier) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.DojangShield) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.ReverseInput) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.RespectPImmune) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.RespectMImmune) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.DefenseAtt) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.DefenseState) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.DojangBerserk) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.RepeatEffect) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.StopPortion) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.StopMotion) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Fear) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.MagicShield) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Frozen) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Frozen2) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Web) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.DrawBack) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.FinalCut) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Cyclone) {
				pw.write(0);
			}

			if (stat == CharacterTemporaryStat.OnCapsule) {
				pw.write(0);
			}

			if (stat == CharacterTemporaryStat.Mechanic) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Inflation) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Explosion) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.DarkTornado) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.AmplifyDamage) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.HideAttack) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.DevilishPower) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.SpiritLink) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Event) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Event2) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.DeathMark) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.PainMark) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Lapidification) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.VampDeath) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.VampDeathSummon) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.VenomSnake) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.PyramidEffect) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.KillingPoint) {
				pw.write(0);
			}

			if (stat == CharacterTemporaryStat.PinkbeanRollingGrade) {
				pw.write(0);
			}

			if (stat == CharacterTemporaryStat.IgnoreTargetDEF) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Invisible) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Judgement) {
				pw.writeShort(0);
				pw.writeInt(0);
			}
			
			if (stat == CharacterTemporaryStat.KeyDownAreaMoving) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.StackBuff) {
				pw.writeShort(0);
			}

			if (stat == CharacterTemporaryStat.BlessOfDarkness) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Larkness) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.ReshuffleSwitch) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.SpecialAction) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.StopForceAtomInfo) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.SoulGazeCriDamR) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.PowerTransferGauge) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.AffinitySlug) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.SoulExalt) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.HiddenPieceOn) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.SmashStack) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.MobZoneState) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.GiveMeHeal) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.TouchMe) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Contagion) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Contagion) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.ComboUnlimited) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.IgnorePCounter) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.IgnoreAllCounter) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.IgnorePImmune) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.IgnoreAllImmune) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.FinalJudgement) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.KnightsAura) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.IceAura) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.FireAura) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.HeavensDoor) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.DamAbsorbShield) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.AntiMagicShell) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.NotDamaged) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.BleedingToxin) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.WindBreakerFinal) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.IgnoreMobDamR) {
				pw.writeShort(0);
				pw.writeInt(0);
			}
			
			if (stat == CharacterTemporaryStat.Asura) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.UnityOfPower) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Stimulate) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.ReturnTeleport) {
				pw.write(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.CapDebuff) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.OverloadCount) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.FireBomb) {
				pw.write(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.SurplusSupply) {
				pw.write(0);
			}

			if (stat == CharacterTemporaryStat.NewFlying) {	
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.NaviFlying) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.AmaranthGenerator) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.CygnusElementSkill) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.StrikerHyperElectric) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.EventPointAbsorb) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.EventAssemble) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Albatross) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Translucence) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.PoseType) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.LightOfSpirit) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.ElementSoul) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.GlimmeringTime) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Reincarnation) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Beholder) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.QuiverCatridge) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.ArmorPiercing) {
				pw.writeShort(0);
				pw.writeInt(0);
			}
			
			if (stat == CharacterTemporaryStat.ZeroAuraStr) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.ZeroAuraSpd) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.ImmuneBarrier) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.ImmuneBarrier) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.FullSoulMP) {
				pw.writeInt(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.AntiMagicShell) {
				pw.write(0);
			}

			if (stat == CharacterTemporaryStat.Dance) {
				pw.writeInt(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.SpiritGuard) {
				pw.writeInt(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.ComboTempest) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.HalfstatByDebuff) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.ComplusionSlant) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.JaguarSummoned) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.BMageAura) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.DarkLighting) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.AttackCountX) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.FireBarrier) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.KeyDownMoving) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.MichaelSoulLink) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.KinesisPsychicEnergeShield) {
				pw.writeInt(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.BladeStance) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.BladeStance) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Fever) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.AdrenalinBoost) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.RWBarrier) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.RWMagnumBlow) {
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.Stigma) {
				pw.writeShort(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.PoseType) {
				pw.write(0);
			}
		}
		
		pw.write(0); // nDefenseAtt
		pw.write(0); // nDefenseState
		pw.write(0); // nPVPDamage
		
		for(Entry<CharacterTemporaryStat, MapleBuffStatValueHolder> stats : statups.entrySet()) {
			CharacterTemporaryStat stat = stats.getKey();
			MapleBuffStatValueHolder mbsvh = stats.getValue();
			
			int value = mbsvh.value;
			int buffid = 0;
			
			if (mbsvh.effect != null)
				buffid = (mbsvh.effect.isSkill() ? mbsvh.effect.getSourceId() : -mbsvh.effect.getSourceId());

			if (stat == CharacterTemporaryStat.ZeroAuraStr) {
				pw.write(0);
			}

			if (stat == CharacterTemporaryStat.ZeroAuraSpd) {
				pw.write(0);
			}

			if (stat == CharacterTemporaryStat.BMageAura) {	
				pw.write(0);
			}

			if (stat == CharacterTemporaryStat.BattlePvP_Helena_Mark) {
				pw.writeInt(0);
				pw.writeInt(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.BattlePvP_LangE_Protection) {
				pw.writeInt(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.MichaelSoulLink) {
				pw.writeInt(0);
				pw.write(0);
				pw.writeInt(0);
				pw.writeInt(0);
			}

			if (stat == CharacterTemporaryStat.AdrenalinBoost) {
				pw.write(0);
			}

			if (stat == CharacterTemporaryStat.Stigma) {
				pw.writeInt(0);
			}
		}
		
		// SecondaryStat::StopForceAtom::Decode
		pw.writeInt(0); // nIdx
		pw.writeInt(0); // nCount
		pw.writeInt(0); // nWeaponID
		
		pw.writeInt(0); // v6, size
		// forEach(v6)
		// pw.writeInt(0); // nValue
		
		pw.writeInt(0); // nViperCharge
		
		TSIndex.encodeAll(pw, chr);
	
    }
}
