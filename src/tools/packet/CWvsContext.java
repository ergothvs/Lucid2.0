package tools.packet;

import java.awt.Point;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import client.BuddylistEntry;
import client.CharacterTemporaryStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.MapleMarriage;
import client.MapleReward;
import client.MapleStat;
import client.MapleStat.Temp;
import client.MapleTrait;
import client.MapleTrait.MapleTraitType;
import client.MonsterFamiliar;
import client.Skill;
import client.SkillEntry;
import client.inventory.*;
import constants.GameConstants;
import handling.SendPacketOpcode;
import handling.channel.DojoRankingsData;
import handling.channel.MapleGeneralRanking.CandyRankingInfo;
import handling.channel.MapleGuildRanking;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.exped.MapleExpedition;
import handling.world.exped.PartySearch;
import handling.world.exped.PartySearchType;
import handling.world.guild.MapleBBSThread;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildAlliance;
import handling.world.guild.MapleGuildCharacter;
import handling.world.guild.MapleGuildSkill;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.StructFamiliar;
import server.life.MapleMonster;
import server.life.PlayerNPC;
import server.quest.MapleQuestStatus;
import server.stores.HiredMerchant;
import server.stores.MaplePlayerShopItem;
import tools.HexTool;
import tools.Pair;
import tools.Randomizer;
import tools.StringUtil;
import tools.data.PacketWriter;
import tools.packet.enums.BuddyType;
import tools.packet.enums.EffectType;
import tools.packet.enums.PartyType;

public class CWvsContext {

    public static byte[] enableActions() {
        return updatePlayerStats(new EnumMap<MapleStat, Long>(MapleStat.class), true, null);
    }

    public static byte[] updatePlayerStats(Map<MapleStat, Long> stats, MapleCharacter chr) {
        return updatePlayerStats(stats, false, chr);
    }

    public static byte[] updatePlayerStats(Map<MapleStat, Long> mystats, boolean itemReaction, MapleCharacter chr) {
        // OnStatUpdate
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.STAT_CHANGED.getValue());
        pw.write(itemReaction ? 1 : 0);
        long updateMask = 0L;
        for (MapleStat statupdate : mystats.keySet()) {
            updateMask |= statupdate.getValue();
        }
        pw.writeLong(updateMask);
        for (final Entry<MapleStat, Long> statupdate : mystats.entrySet()) {
            switch (statupdate.getKey()) {
                case SKIN:
                case LEVEL:
                case FATIGUE:
                case BATTLE_RANK:
                case ICE_GAGE: // not sure..
                    pw.write((statupdate.getValue()).byteValue());
                    break;
                case STR:
                case DEX:
                case INT:
                case LUK:
                case AVAILABLEAP:
                    pw.writeShort((statupdate.getValue()).shortValue());
                    break;
                case JOB:
                	pw.writeShort((statupdate.getValue()).shortValue());
                	pw.writeShort(chr.getSubcategory()); // not sure about it..
                case AVAILABLESP:
                    if (GameConstants.isSeparatedSp(chr.getJob())) {
                        pw.write(chr.getRemainingSpSize());
                        for (int i = 0; i < chr.getRemainingSps().length; i++) {
                            if (chr.getRemainingSp(i) > 0) {
                                pw.write(i + 1);
                                pw.writeInt(chr.getRemainingSp(i));
                            }
                        }
                    } else {
                        pw.writeShort(chr.getRemainingSp());
                    }
                    break;
                case EXP:
                case MESO:
                    pw.writeLong((statupdate.getValue()).longValue());
                    pw.write(-1);
                	pw.writeInt(0);
                    break;
                case TRAIT_LIMIT:
    				Long statup = mystats.get(MapleStat.CHARISMA);
    				pw.writeShort(statup != null ? statup.shortValue() : 0);
    				statup = mystats.get(MapleStat.INSIGHT);
    				pw.writeShort(statup != null ? statup.shortValue() : 0);
    				statup = mystats.get(MapleStat.WILL);
    				pw.writeShort(statup != null ? statup.shortValue() : 0);
    				statup = mystats.get(MapleStat.CRAFT);
    				pw.writeShort(statup != null ? statup.shortValue() : 0);
    				statup = mystats.get(MapleStat.SENSE);
    				pw.writeShort(statup != null ? statup.shortValue() : 0);
    				statup = mystats.get(MapleStat.CHARM);
    				pw.writeShort(statup != null ? statup.shortValue() : 0);
    				pw.write(0);
    				pw.writeLong(PacketHelper.getTime(-2));
                    break;
                case PET:
                    pw.write(-1);
                    pw.write(0);
                    pw.write(0);
                    pw.writeLong((statupdate.getValue()).intValue());
                    pw.writeLong((statupdate.getValue()).intValue());
                    pw.writeLong((statupdate.getValue()).intValue());
                    break;
                case BATTLE_POINTS:
                case VIRTUE:
                    pw.writeLong((statupdate.getValue()).longValue());
                    break;
                case CHARM: // also other trait values?
                    pw.write(statupdate.getValue().byteValue()); //LOBYTE(nCharmOld) = CInPacket::Decode1(retaddr);
                default:
                    pw.writeInt((statupdate.getValue()).intValue());
            }
        }

        // if ((updateMask == 0L) && (!itemReaction)) { pw.write(1); }
        pw.write(0);
        pw.write(0);
        pw.write(0);
        pw.write(0);
        pw.write(0);
        return pw.getPacket();
    }

    public static byte[] setTemporaryStats(short str, short dex, short _int, short luk, short watk, short matk, short acc, short avoid, short speed, short jump) {
        Map<Temp, Integer> stats = new EnumMap<>(MapleStat.Temp.class);

        stats.put(MapleStat.Temp.STR, Integer.valueOf(str));
        stats.put(MapleStat.Temp.DEX, Integer.valueOf(dex));
        stats.put(MapleStat.Temp.INT, Integer.valueOf(_int));
        stats.put(MapleStat.Temp.LUK, Integer.valueOf(luk));
        stats.put(MapleStat.Temp.WATK, Integer.valueOf(watk));
        stats.put(MapleStat.Temp.MATK, Integer.valueOf(matk));
        stats.put(MapleStat.Temp.ACC, Integer.valueOf(acc));
        stats.put(MapleStat.Temp.AVOID, Integer.valueOf(avoid));
        stats.put(MapleStat.Temp.SPEED, Integer.valueOf(speed));
        stats.put(MapleStat.Temp.JUMP, Integer.valueOf(jump));

        return temporaryStats(stats);
    }

    public static byte[] temporaryStats_Aran() {
        Map<Temp, Integer> stats = new EnumMap<>(MapleStat.Temp.class);

        stats.put(MapleStat.Temp.STR, Integer.valueOf(999));
        stats.put(MapleStat.Temp.DEX, Integer.valueOf(999));
        stats.put(MapleStat.Temp.INT, Integer.valueOf(999));
        stats.put(MapleStat.Temp.LUK, Integer.valueOf(999));
        stats.put(MapleStat.Temp.WATK, Integer.valueOf(255));
        stats.put(MapleStat.Temp.ACC, Integer.valueOf(999));
        stats.put(MapleStat.Temp.AVOID, Integer.valueOf(999));
        stats.put(MapleStat.Temp.SPEED, Integer.valueOf(140));
        stats.put(MapleStat.Temp.JUMP, Integer.valueOf(120));

        return temporaryStats(stats);
    }

    public static byte[] temporaryStats_Balrog(MapleCharacter chr) {
        Map<Temp, Integer> stats = new EnumMap<>(MapleStat.Temp.class);

        int offset = 1 + (chr.getLevel() - 90) / 20;
        stats.put(MapleStat.Temp.STR, Integer.valueOf(chr.getStat().getTotalStr() / offset));
        stats.put(MapleStat.Temp.DEX, Integer.valueOf(chr.getStat().getTotalDex() / offset));
        stats.put(MapleStat.Temp.INT, Integer.valueOf(chr.getStat().getTotalInt() / offset));
        stats.put(MapleStat.Temp.LUK, Integer.valueOf(chr.getStat().getTotalLuk() / offset));
        stats.put(MapleStat.Temp.WATK, Integer.valueOf(chr.getStat().getTotalWatk() / offset));
        stats.put(MapleStat.Temp.MATK, Integer.valueOf(chr.getStat().getTotalMagic() / offset));

        return temporaryStats(stats);
    }

    public static byte[] temporaryStats(Map<MapleStat.Temp, Integer> mystats) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.FORCED_STAT_SET.getValue());
        int updateMask = 0;
        for (MapleStat.Temp statupdate : mystats.keySet()) {
            updateMask |= statupdate.getValue();
        }
        pw.writeInt(updateMask);
        for (final Entry<MapleStat.Temp, Integer> statupdate : mystats.entrySet()) {
            switch (statupdate.getKey()) {
                case SPEED:
                case JUMP:
                case UNKNOWN:
                    pw.write((statupdate.getValue()).byteValue());
                    break;
                default:
                    pw.writeShort((statupdate.getValue()).shortValue());
            }
        }

        return pw.getPacket();
    }

    public static byte[] temporaryStats_Reset() {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.FORCED_STAT_RESET.getValue());

        return pw.getPacket();
    }

    
    /**
     * @function OnChangeSkillRecordResult
     * param SetExclRequestSent
     * param bShowResult
     * param bRemoveLinkSkill
     * param bSN
     * @return
     */
    public static byte[] updateLinkSkill(Map<Skill, SkillEntry> update, boolean request, boolean result, boolean remove) {
    	PacketWriter pw = new PacketWriter();
    	pw.writeShort(SendPacketOpcode.CHANGE_SKILL_RECORD_RESULT.getValue());
    	pw.write(request);
    	pw.write(result);
    	pw.write(remove);
    	pw.writeShort(update.size());
    	update.entrySet().forEach(entry -> {
    		pw.writeInt(((Skill) entry.getKey()).getId());
    		pw.writeInt(((SkillEntry) entry.getValue()).skillevel);
    		pw.writeInt(((SkillEntry) entry.getValue()).masterlevel);
    		PacketHelper.addExpirationTime(pw, ((SkillEntry) entry.getValue()).expiration);
    	});
    	pw.write(0); // pw.write(hyper ? 0x0C : 4); ??????
    	return pw.getPacket();
    }

    public static byte[] giveFameErrorResponse(int op) {
        return OnFameResult(op, null, true, 0);
    }

    public static byte[] OnFameResult(int op, String charname, boolean raise, int newFame) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.GIVE_POPULARITY_RESULT.getValue());
        pw.write(op);
        if ((op == 0) || (op == 5)) {
            pw.writeMapleAsciiString(charname == null ? "" : charname);
            pw.write(raise ? 1 : 0);
            if (op == 0) {
                pw.writeInt(newFame);
            }
        }

        return pw.getPacket();
    }

    public static byte[] bombLieDetector(boolean error, int mapid, int channel) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.ANTI_MACRO_RESULT.getValue());
        pw.write(error ? 2 : 1);
        pw.writeInt(mapid);
        pw.writeInt(channel);

        return pw.getPacket();
    }

    public static byte[] sendLieDetector(final byte[] image) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.ANTI_MACRO_RESULT.getValue());
        pw.write(6); // 1 = not attacking, 2 = tested, 3 = going through 

        pw.write(4); // 2 give invalid pointer (suppose to be admin macro) 
        pw.write(1); // the time >0 is always 1 minute 
        if (image == null) {
            pw.writeInt(0);
            return pw.getPacket();
        }
        pw.writeInt(image.length);
        pw.write(image);

        return pw.getPacket();
    }

    public static byte[] LieDetectorResponse(final byte msg) {
        return LieDetectorResponse(msg, (byte) 0);
    }

    public static byte[] LieDetectorResponse(final byte msg, final byte msg2) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.ANTI_MACRO_RESULT.getValue());
        pw.write(msg); // 1 = not attacking, 2 = tested, 3 = going through 
        pw.write(msg2);

        return pw.getPacket();
    }

    public static byte[] getLieDetector(byte type, String tester) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.ANTI_MACRO_RESULT.getValue()); // 2A 00 01 00 00 00
        pw.write(type); // 1 = not attacking, 2 = tested, 3 = going through, 4 save screenshot 
        switch (type) {
            case 4: //save screen shot 
                pw.write(0);
                pw.writeMapleAsciiString(""); // file name 
                break;
            case 5:
                pw.write(1); // 2 = save screen shot 
                pw.writeMapleAsciiString(tester); // me or file name 
                break;
            case 6:
                pw.write(4); // 2 or anything else, 2 = with maple admin picture, basicaly manager's skill? 
                pw.write(1); // if > 0, then time = 60,000..maybe try < 0? 
                //pw.writeInt(size);
                //pw.write(byte); // bytes 
                break;
            case 7://send this if failed 
                // 2 = You have been appointed as a auto BOT program user and will be restrained. 
                pw.write(4); // default 
                break;
            case 9:
                // 0 = passed lie detector test 
                // 1 = reward 5000 mesos for not botting. 
                // 2 = thank you for your cooperation with administrator. 
                pw.write(0);
                break;
            case 8: // save screen shot.. it appears that you may be using a macro-assisted program
                pw.write(0); // 2 or anything else , 2 = show msg, 0 = none 
                pw.writeMapleAsciiString(""); // file name 
                break;
            case 10: // no save 
                pw.write(0); // 2 or anything else, 2 = show msg 
                pw.writeMapleAsciiString(""); // ?? // hi_You have passed the lie detector test 
                break;
            default:
                pw.write(0);
                break;
        }
        return pw.getPacket();
    }

    public static byte[] lieDetector(byte mode, byte action, byte[] image, String str1, String str2, String str3) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.ANTI_MACRO_RESULT.getValue());
        pw.write(mode);
        pw.write(action); //2 = show msg/save screenshot/maple admin picture(mode 6)
        if (mode == 6) {
            pw.write(1); //if true time is 60:00
            PacketHelper.addImageInfo(pw, image);
        }
        if (mode == 7 || mode == 9) {
        }
        if (mode == 4) { //save screenshot
            pw.writeMapleAsciiString(str1); //file name
        }
        if (mode != 5) {
            if (mode == 10) {
                pw.writeMapleAsciiString(str2); //passed lie detector message
            } else {
                if (mode != 8) {
                }
                pw.writeMapleAsciiString(str2); //failed lie detector, file name (for screenshot)
            }
        }
        pw.writeMapleAsciiString(str3); //file name for screenshot

        return pw.getPacket();
    }

    public static byte[] report(int mode) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.CLAIM_RESULT.getValue());
        pw.write(mode);
        if (mode == 2) {
            pw.write(0);
            pw.writeInt(1); // times left to report
        }

        return pw.getPacket();
    }

    public static byte[] OnSetClaimSvrAvailableTime(int from, int to) {
        PacketWriter pw = new PacketWriter(4);

        pw.writeShort(SendPacketOpcode.SET_CLAIM_SVR_AVAILABLE_TIME.getValue());
        pw.write(from);
        pw.write(to);

        return pw.getPacket();
    }

    public static byte[] OnClaimSvrStatusChanged(boolean enable) {
        PacketWriter pw = new PacketWriter(3);

        pw.writeShort(SendPacketOpcode.CLAIM_SVR_STATUS_CHANGED.getValue());
        pw.write(enable ? 1 : 0);

        return pw.getPacket();
    }

    public static byte[] updateMount(MapleCharacter chr, boolean levelup) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SET_TAMING_MOB_INFO.getValue());
        pw.writeInt(chr.getId());
        pw.writeInt(chr.getMount().getLevel());
        pw.writeInt(chr.getMount().getExp());
        pw.writeInt(chr.getMount().getFatigue());
        pw.write(levelup ? 1 : 0);

        return pw.getPacket();
    }

    public static byte[] showQuestCompletion(int id) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.QUEST_CLEAR.getValue());
        pw.writeShort(id);

        return pw.getPacket();
    }

    public static byte[] useSkillBook(MapleCharacter chr, int skillid, int maxlevel, boolean canuse, boolean success) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SKILL_LEARN_ITEM_RESULT.getValue());
        pw.write(0);
        pw.writeInt(chr.getId());
        pw.write(1);
        pw.writeInt(skillid);
        pw.writeInt(maxlevel);
        pw.write(canuse ? 1 : 0);
        pw.write(success ? 1 : 0);

        return pw.getPacket();
    }

    public static byte[] useAPSPReset(boolean spReset, int cid) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(spReset ? SendPacketOpcode.SKILL_RESET_ITEM_RESULT.getValue() : SendPacketOpcode.ABILITY_RESET_ITEM_RESULT.getValue());
        pw.write(1);
        pw.writeInt(cid);
        pw.write(1);

        return pw.getPacket();
    }

    public static byte[] expandCharacterSlots(int mode) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.CHAR_SLOT_INC_RESULT.getValue());
        pw.writeInt(mode);
        pw.write(0);

        return pw.getPacket();
    }

    public static byte[] finishedGather(int type) {
        return gatherSortItem(true, type);
    }

    public static byte[] finishedSort(int type) {
        return gatherSortItem(false, type);
    }

    public static byte[] gatherSortItem(boolean gather, int type) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(gather ? SendPacketOpcode.GATHER_ITEM_RESULT.getValue() : SendPacketOpcode.SORT_ITEM_RESULT.getValue());
        pw.write(1);
        pw.write(type);

        return pw.getPacket();
    }

    public static byte[] updateExpPotion(int mode, int id, int itemId, boolean firstTime, int level, int potionDstLevel) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.EXP_CONSUME_ITEM_RESULT.getValue());
        pw.write(mode);
        pw.write(1); //bool for get_update_time
        pw.writeInt(id);
        if (id != 0) {
            pw.write(1); //not even being read how rude of nexon
            if (mode == 1) {
                pw.writeInt(0);
            }
            if (mode == 2) {
                pw.write(firstTime ? 1 : 0); //1 on first time then it turns 0
                pw.writeInt(itemId);
                if (itemId != 0) {
                    pw.writeInt(level); //level, confirmed
                    pw.writeInt(potionDstLevel); //max level with potion
                    pw.writeLong(384); //random, more like potion id
                }
            }
        }

        return pw.getPacket();
    }

    public static byte[] updateGender(MapleCharacter chr) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.UPDATE_GENDER.getValue());
        pw.write(chr.getGender());

        return pw.getPacket();
    }

    public static byte[] charInfo(MapleCharacter chr, boolean isSelf) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.CHARACTER_INFO.getValue());
        pw.writeInt(chr.getId());
        pw.write(0);
        /*
        if above is true
	    v4 = CInPacket::Decode1(iPacket);
	    v5 = v4 != 0;
	    LOBYTE(bFromStarUserList) = v4 != 0;
	    LOBYTE(bOnline) = CInPacket::Decode1(iPacket) != 0;
	    CInPacket::DecodeStr(iPacket, &sUserName);
	    v66 = 0;
	    if ( !v5 || TSingleton<CUIStarPlanetUserList>::ms_pInstance )
	    {
	      CWvsContext::UI_Close(v2, 234);
	      v48 = bOnline;
	      v47 = bFromStarUserList;
	      nSense = &v46;
	      v46._m_pStr = 0;
	      ZXString<char>::operator=(&v46, &sUserName);
	      v6 = v2->m_dwCharacterID;
	      LOBYTE(v66) = 1;
	      v45 = v3 == v6;
	      v7 = TSingleton<CUIUserInfo_StarPlanet>::CreateInstance();
	      LOBYTE(v66) = 0;
	      CUIUserInfo_StarPlanet::SetUserInfo(v7, v45, v3, v46, v47, v48);
	      if ( v5 )
	      {
	        CUIStarPlanetUserList::AttachUserInfo(TSingleton<CUIStarPlanetUserList>::ms_pInstance, 1);
	        v8 = TSingleton<CUIUserInfo_StarPlanet>::ms_pInstance;
	        v9 = TSingleton<CUIUserInfo_StarPlanet>::ms_pInstance->vfptr;
	        v10 = &TSingleton<CUIStarPlanetUserList>::ms_pInstance->vfptr;
	        v11 = TSingleton<CUIStarPlanetUserList>::ms_pInstance->vfptr;
	        bOnline = TSingleton<CUIStarPlanetUserList>::ms_pInstance->m_width;
	        v12 = v11->GetAbsTop(&TSingleton<CUIStarPlanetUserList>::ms_pInstance->vfptr);
	        v48 = v12;
	        v13 = (*(*v10 + 60))(v10, v12);
	        v48 = bFromStarUserList + v13;
	        (v9[11].Update)(v8, bFromStarUserList + v13);
	      }
	      CUIUserInfo_StarPlanet::OnPacket(TSingleton<CUIUserInfo_StarPlanet>::ms_pInstance, iPacket);
	    }
	    v66 = -1;
	    if ( sUserName._m_pStr )
	    {
	      v14 = sUserName._m_pStr - 12;
	      if ( InterlockedDecrement(sUserName._m_pStr - 3) <= 0 )
	        ZAllocEx<ZAllocStrSelector<char>>::Free(&ZAllocEx<ZAllocStrSelector<char>>::_s_alloc, v14);
	    }
	    return;
	  }
         */
        pw.write(chr.getLevel());
        pw.writeShort(chr.getJob());
        pw.writeShort(chr.getSubcategory());
        pw.write(chr.getStat().pvpRank);
        pw.writeInt(chr.getFame());

        final MapleMarriage marriage = chr.getMarriage();
        pw.write(marriage != null && marriage.getId() != 0);
        if (marriage != null && marriage.getId() != 0) {
            pw.writeInt(marriage.getId()); //marriage id
            pw.writeInt(marriage.getHusbandId()); //husband char id
            pw.writeInt(marriage.getWifeId()); //wife char id
            pw.writeShort(3); //msg type
            pw.writeInt(chr.getMarriageItemId()); //ring id husband
            pw.writeInt(chr.getMarriageItemId()); //ring id wife
            pw.writeAsciiString(marriage.getHusbandName(), 13); //husband name
            pw.writeAsciiString(marriage.getWifeName(), 13); //wife name
        }

        List<Integer> prof = chr.getProfessions();
        pw.write(prof.size());
        for (Integer profession : prof) {
            pw.writeShort(profession);
        }
        MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
        if (gs != null) {
            MapleGuildAlliance allianceName = World.Alliance.getAlliance(gs.getAllianceId());
            pw.writeMapleAsciiString(gs.getName());
            pw.writeMapleAsciiString(allianceName != null ? allianceName.getName() : "");
        } else {
            pw.writeMapleAsciiString("-");
            pw.writeMapleAsciiString("");
        }
        pw.write(-1);//nForcedPetIdx
        pw.write(!isSelf);
        pw.write(!chr.getSummonedPets().isEmpty());
        pw.write(!chr.getSummonedPets().isEmpty());
        if (!chr.getSummonedPets().isEmpty()) {
            for (MaplePet pet : chr.getSummonedPets()) {
                byte index = 1;
                pw.writeInt(0);//gotta look into it
                pw.writeInt(pet.getPetItemId());
                pw.writeMapleAsciiString(pet.getName());
                pw.write(pet.getLevel());
                pw.writeShort(pet.getCloseness());
                pw.write(pet.getFullness());
                pw.writeShort(0);
                Item inv = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) (byte) (index == 2 ? -130 : index == 1 ? -114 : -138));
                pw.writeInt(inv == null ? 0 : inv.getItemId());
                //pw.writeInt(pet.getColor());
                pw.writeInt(0);
                pw.write(chr.getSummonedPets().size() > index);
                index++;
            }
        }

        final int wishlistSize = chr.getWishlistSize();
        pw.write(wishlistSize);
        if (wishlistSize > 0) {
            int[] wishlist = chr.getWishlist();
            for (int x = 0; x < wishlistSize; x++) {
                pw.writeInt(wishlist[x]);
            }
        }
        // Medal
        final Item medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) 0);
        //final Item medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) EquipSlotType.Medal.getSlot());
        pw.writeInt(medal == null ? 0 : medal.getItemId());

        // Medal quests
        List<Pair<Integer, Long>> medalQuests = chr.getCompletedMedals();
        pw.writeShort(medalQuests.size());
        for (Pair<Integer, Long> x : medalQuests) {
            pw.writeInt(x.left);
            pw.writeLong(x.right); // Time.
        }

        // Added on v176
        pw.write(1); // This is probably the size 
        pw.writeInt(0);
        pw.writeInt(2431965); // Basic damage skin id
        pw.write(0);
        pw.writeMapleAsciiString("This is a basic Damage Skin.\r\n\r\n\r\n\r\n\r\n");
        pw.writeInt(-1);
        pw.writeInt(0);
        pw.write(1);
        pw.writeMapleAsciiString("");
        pw.writeShort(0);
        pw.writeShort(0);
        // End

        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            pw.write(chr.getTrait(t).getLevel());
        }

        pw.writeInt(chr.getAccountID());
        PacketHelper.addFarmInfo(pw, chr.getClient(), (byte) 0);
        pw.writeInt(1);
        pw.writeInt(0);

        // Chairs
        final List<Integer> chairs = new ArrayList<>();
        for (Item i : chr.getInventory(MapleInventoryType.SETUP).newList()) {
            if (i.getItemId() / 10000 == 301 && !chairs.contains(i.getItemId())) {
                chairs.add(i.getItemId());
            }
        }
        pw.writeInt(chairs.size());
        for (Integer chair : chairs) {
            pw.writeInt(chair);
        }
        pw.writeInt(0);
        pw.writeInt(30); // No idea, or 0x1E
        pw.writeInt(0);

        return pw.getPacket();
    }

    public static byte[] spawnPortal(int townId, int targetId, int skillId, Point pos) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.TOWN_PORTAL.getValue());
        pw.writeInt(townId);
        pw.writeInt(targetId);
        if ((townId != 999999999) && (targetId != 999999999)) {
            pw.writeInt(skillId);
            pw.writePos(pos);
        }

        return pw.getPacket();
    }

    public static byte[] mechPortal(Point pos) {
        PacketWriter pw = new PacketWriter();

        // pw.writeShort(SendPacketOpcode.MECH_PORTAL.getValue());
        pw.writePos(pos);

        return pw.getPacket();
    }

    public static byte[] echoMegaphone(String name, String message) {
        PacketWriter pw = new PacketWriter();

        // pw.writeShort(SendPacketOpcode.ECHO_MESSAGE.getValue());
        pw.write(0);
        pw.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        pw.writeMapleAsciiString(name);
        pw.writeMapleAsciiString(message);

        return pw.getPacket();
    }

    public static byte[] showQuestMsg(String msg) {
        return broadcastMsg(5, msg);
    }

    public static byte[] Mulung_Pts(int recv, int total) {
        return showQuestMsg(new StringBuilder().append("You have received ").append(recv).append(" training points, for the accumulated total of ").append(total).append(" training points.").toString());
    }

    public static byte[] broadcastMsg(String message) {
        return broadcastMessage(4, 0, message, false);
    }

    public static byte[] broadcastMsg(int type, String message) {
        return broadcastMessage(type, 0, message, false);
    }

    public static byte[] broadcastMsg(int type, int channel, String message) {
        return broadcastMessage(type, channel, message, false);
    }

    public static byte[] broadcastMsg(int type, int channel, String message, boolean smegaEar) {
        return broadcastMessage(type, channel, message, smegaEar);
    }

    private static byte[] broadcastMessage(int type, int channel, String message, boolean megaEar) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.BROADCAST_MESSAGE.getValue());
        pw.write(type);
        if (type == 4) {
            pw.write(1);
        }
        if ((type != 23) && (type != 24)) {
            pw.writeMapleAsciiString(message);
        }
        switch (type) {
            case 3:
            case 22:
            case 25:
            case 26:
                pw.write(channel - 1);
                pw.write(megaEar ? 1 : 0);
                break;
            case 9:
                pw.write(channel - 1);
                break;
            case 12:
                pw.writeInt(channel);
                break;
            case 6:
            case 11:
            case 20:
                pw.writeInt((channel >= 1000000) && (channel < 6000000) ? channel : 0);
                break;
            case 24:
                pw.writeShort(0);
            case 4:
            case 5:
            case 7:
            case 8:
            case 10:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 21:
            case 23:
        }
        return pw.getPacket();
    }

    public static byte[] getGachaponMega(String name, String message, int channel, Item item, byte rareness, String gacha) {
        return getGachaponMega(name, message, channel, item, rareness, false, gacha);
    }

    public static byte[] getGachaponMega(String name, String message, int channel, Item item, byte rareness, boolean dragon, String gacha) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.BROADCAST_MESSAGE.getValue());
        pw.write(24);
        pw.writeMapleAsciiString(new StringBuilder().append(name).append(message).toString());
        if (!dragon) {
        pw.writeInt(channel -1);
        pw.writeInt(0);
        }
        pw.writeMapleAsciiString(gacha);
        PacketHelper.addItemInfo(pw, item);

        return pw.getPacket();
    }

    public static byte[] getEventEnvelope(int questID, int time) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.BROADCAST_MESSAGE.getValue());
        pw.write(23);
        pw.writeShort(questID);
        pw.writeInt(time);

        return pw.getPacket();
    }

    public static byte[] tripleSmega(List<String> message, boolean ear, int channel) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.BROADCAST_MESSAGE.getValue());
        pw.write(10);
        if (message.get(0) != null) {
            pw.writeMapleAsciiString((String) message.get(0));
        }
        pw.write(message.size());
        for (int i = 1; i < message.size(); i++) {
            if (message.get(i) != null) {
                pw.writeMapleAsciiString((String) message.get(i));
            }
        }
        pw.write(channel - 1);
        pw.write(ear ? 1 : 0);

        return pw.getPacket();
    }

    public static byte[] itemMegaphone(String msg, boolean whisper, int channel, Item item) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.BROADCAST_MESSAGE.getValue());
        pw.write(8);
        pw.writeMapleAsciiString(msg);
        pw.write(channel - 1);
        pw.write(whisper ? 1 : 0);
        PacketHelper.addItemPosition(pw, item, true, false);
        if (item != null) {
            PacketHelper.addItemInfo(pw, item);
        }

        return pw.getPacket();
    }

    public static byte[] getPeanutResult(int itemId, short quantity, int itemId2, short quantity2, int ourItem) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.INCUBATOR_RESULT.getValue());
        pw.writeInt(itemId);
        pw.writeShort(quantity);
        pw.writeInt(ourItem);
        pw.writeInt(itemId2);
        pw.writeInt(quantity2);
        pw.write(0);
        pw.write(0);

        return pw.getPacket();
    }

    public static byte[] getOwlOpen() {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SHOP_SCANNER_RESULT.getValue());
        pw.write(9);
        pw.write(GameConstants.owlItems.length);
        for (int i : GameConstants.owlItems) {
            pw.writeInt(i);
        }

        return pw.getPacket();
    }

    public static byte[] getOwlSearched(int itemSearch, List<HiredMerchant> hms) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SHOP_SCANNER_RESULT.getValue());
        pw.write(8);
        pw.writeInt(0);
        pw.writeInt(itemSearch);
        int size = 0;

        for (HiredMerchant hm : hms) {
            size += hm.searchItem(itemSearch).size();
        }

        pw.writeInt(size);
        for (HiredMerchant hm : hms) {
            for (Iterator<HiredMerchant> i = hms.iterator(); i.hasNext();) {
                hm = (HiredMerchant) i.next();
                final List<MaplePlayerShopItem> items = hm.searchItem(itemSearch);
                for (MaplePlayerShopItem item : items) {
                    pw.writeMapleAsciiString(hm.getOwnerName());
                    pw.writeInt(hm.getMap().getId());
                    pw.writeMapleAsciiString(hm.getDescription());
                    pw.writeInt(item.item.getQuantity());
                    pw.writeInt(item.bundles);
                    pw.writeInt(item.price);
                    switch (2) {
                        case 0:
                            pw.writeInt(hm.getOwnerId());
                            break;
                        case 1:
                            pw.writeInt(hm.getStoreId());
                            break;
                        default:
                            pw.writeInt(hm.getObjectId());
                    }

                    pw.write(hm.getFreeSlot() == -1 ? 1 : 0);
                    pw.write(GameConstants.getInventoryType(itemSearch).getType());
                    if (GameConstants.getInventoryType(itemSearch) == MapleInventoryType.EQUIP) {
                        PacketHelper.addItemInfo(pw, item.item);
                    }
                }
            }
        }
        return pw.getPacket();
    }

    public static byte[] getOwlMessage(int msg) {
        PacketWriter pw = new PacketWriter(3);

        pw.writeShort(SendPacketOpcode.SHOP_LINK_RESULT.getValue());
        pw.write(msg);

        return pw.getPacket();
    }

    public static byte[] sendEngagementRequest(String name, int cid) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MARRIAGE_REQUEST.getValue());

        pw.write(0);
        pw.writeMapleAsciiString(name);
        pw.writeInt(cid);

        return pw.getPacket();
    }

    public static byte[] sendEngagement(byte msg, int item, MapleCharacter male, MapleCharacter female) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MARRIAGE_RESULT.getValue());
        pw.write(msg);
        if (msg == 9 || msg >= 11 && msg <= 14) {
            pw.writeInt(0);
            pw.writeInt(male.getId());
            pw.writeInt(female.getId());
            pw.writeShort(1);
            pw.writeInt(item);
            pw.writeInt(item);
            pw.writeAsciiString(male.getName(), 13);
            pw.writeAsciiString(female.getName(), 13);
        } else if (msg == 10 || msg >= 15 && msg <= 16) {
            pw.writeAsciiString("Male", 13);
            pw.writeAsciiString("Female", 13);
            pw.writeShort(0);
        }

        return pw.getPacket();
    }

    public static byte[] sendWeddingGive() {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.WEDDING_GIFT_RESULT.getValue());
        pw.write(9);
        pw.write(0);

        return pw.getPacket();
    }

    public static byte[] sendWeddingReceive() {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.WEDDING_GIFT_RESULT.getValue());
        pw.write(10);
        pw.writeLong(-1L);
        pw.writeInt(0);
        pw.write(0);

        return pw.getPacket();
    }

    public static byte[] giveWeddingItem() {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.WEDDING_GIFT_RESULT.getValue());
        pw.write(11);
        pw.write(0);
        pw.writeLong(0L);
        pw.write(0);

        return pw.getPacket();
    }

    public static byte[] receiveWeddingItem() {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.WEDDING_GIFT_RESULT.getValue());
        pw.write(15);
        pw.writeLong(0L);
        pw.write(0);

        return pw.getPacket();
    }

    public static byte[] sendCashPetFood(boolean success, byte index) {
        PacketWriter pw = new PacketWriter(3 + (success ? 1 : 0));

        pw.writeShort(SendPacketOpcode.CASH_PET_FOOD_RESULT.getValue());
        pw.write(success ? 0 : 1);
        if (success) {
            pw.write(index);
        }

        return pw.getPacket();
    }

    public static byte[] yellowChat(String msg) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SET_WEEK_EVENT_MESSAGE.getValue());
        pw.write(-1);
        pw.writeMapleAsciiString(msg);

        return pw.getPacket();
    }

    public static byte[] shopDiscount(int percent) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SET_POTION_DISCOUNT_RATE.getValue());
        pw.write(percent);

        return pw.getPacket();
    }

    public static byte[] catchMob(int mobid, int itemid, byte success) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.BRIDLE_MOB_CATCH_FAIL.getValue());
        pw.write(success);
        pw.writeInt(itemid);
        pw.writeInt(mobid);

        return pw.getPacket();
    }

    public static byte[] spawnPlayerNPC(PlayerNPC npc) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.IMITATED_NPC_DATA.getValue());
        pw.write(1);
        pw.writeInt(npc.getId());
        pw.writeMapleAsciiString(npc.getName());
        PacketHelper.addCharLook(pw, npc, true, false);

        return pw.getPacket();
    }

    public static byte[] disabledNPC(List<Integer> ids) {
        PacketWriter pw = new PacketWriter(3 + ids.size() * 4);

        pw.writeShort(SendPacketOpcode.LIMITED_NPC_DISABLE_INFO.getValue());
        pw.write(ids.size());
        for (Integer i : ids) {
            pw.writeInt(i.intValue());
        }

        return pw.getPacket();
    }

    public static byte[] getCard(int itemid, int level) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MONSTER_BOOK_SET_CARD.getValue());
        pw.write(itemid > 0 ? 1 : 0);
        if (itemid > 0) {
            pw.writeInt(itemid);
            pw.writeInt(level);
        }
        return pw.getPacket();
    }

    public static byte[] changeCardSet(int set) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MONSTER_BOOK_SET_COVER.getValue());
        pw.writeInt(set);

        return pw.getPacket();
    }

    public static byte[] upgradeBook(Item book, MapleCharacter chr) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.BOOK_STATS.getValue());
        pw.writeInt(book.getPosition());
        PacketHelper.addItemInfo(pw, book, chr);

        return pw.getPacket();
    }

    public static byte[] getCardDrops(int cardid, List<Integer> drops) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.CARD_DROPS.getValue());
        pw.writeInt(cardid);
        pw.writeShort(drops == null ? 0 : drops.size());
        if (drops != null) {
            for (Integer de : drops) {
                pw.writeInt(de.intValue());
            }
        }

        return pw.getPacket();
    }

    public static byte[] getFamiliarInfo(MapleCharacter chr) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.FAMILIAR_INFO.getValue());
        pw.writeInt(chr.getFamiliars().size());
        
        for (MonsterFamiliar mf : chr.getFamiliars().values()) {
            mf.writeRegisterPacket(pw, true);
        }
        
        List<Pair<Integer, Long>> size = new ArrayList<>();
        for (Item i : chr.getInventory(MapleInventoryType.USE).list()) {
            if (i.getItemId() / 10000 == 287) {
                StructFamiliar f = MapleItemInformationProvider.getInstance().getFamiliarByItem(i.getItemId());
                if (f != null) {
                    size.add(new Pair<>(f.familiar, i.getInventoryId()));
                }
            }
        }
        pw.writeInt(size.size());
        for (Pair<?, ?> s : size) {
            pw.writeInt(chr.getId());
            pw.writeInt(((Integer) s.left));
            pw.writeLong(((Long) s.right));
            pw.write(0);
        }
        size.clear();

        return pw.getPacket();
    }

    public static byte[] updateWebBoard(boolean result) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.WEB_BOARD_AUTHKEY_UPDATE.getValue());
        pw.writeBoolean(result);

        return pw.getPacket();
    }

    public static byte[] MulungEnergy(int energy) {
        return sendPyramidEnergy("energy", String.valueOf(energy));
    }

    public static byte[] sendPyramidEnergy(String type, String amount) {
        return sendString(1, type, amount);
    }

    public static byte[] sendGhostPoint(String type, String amount) {
        return sendString(2, type, amount);
    }

    public static byte[] sendGhostStatus(String type, String amount) {
        return sendString(3, type, amount);
    }

    public static byte[] sendString(int type, String object, String amount) {
        PacketWriter pw = new PacketWriter();

        switch (type) {
            case 1:
                pw.writeShort(SendPacketOpcode.SESSION_VALUE.getValue());
                break;
            case 2:
                pw.writeShort(SendPacketOpcode.PARTY_VALUE.getValue());
                break;
            case 3:
                pw.writeShort(SendPacketOpcode.FIELD_VALUE.getValue());
        }

        pw.writeMapleAsciiString(object);
        pw.writeMapleAsciiString(amount);

        return pw.getPacket();
    }

    public static byte[] fairyPendantMessage(int termStart, int incExpR) {
        PacketWriter pw = new PacketWriter(14);

        pw.writeShort(SendPacketOpcode.BONUS_EXP_RATE_CHANGED.getValue());
        pw.writeInt(17);
        pw.writeInt(0);

        pw.writeInt(incExpR);

        return pw.getPacket();
    }

    public static byte[] sendLevelup(boolean family, int level, String name) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.NOTIFY_LEVEL_UP.getValue());
        pw.write(family ? 1 : 2);
        pw.writeInt(level);
        pw.writeMapleAsciiString(name);

        return pw.getPacket();
    }

    public static byte[] sendMarriage(boolean family, String name) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.NOTIFY_WEDDING.getValue());
        pw.write(family ? 1 : 0);
        pw.writeMapleAsciiString(name);

        return pw.getPacket();
    }

    //mark packet
     public static byte[] giveMarkOfTheif(int cid, int oid, int skillid, List<MapleMonster> monsters, Point p1, Point p2, int javelin) {
        PacketWriter pw =new PacketWriter();
        pw.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
        pw.write(1);
        pw.writeInt(cid);
        pw.writeInt(oid);
        pw.writeInt(11); //type
        pw.write(1);
        pw.writeInt(monsters.size());
        for (MapleMonster monster : monsters) {
            pw.writeInt(monster.getObjectId());
        }
        pw.writeInt(skillid); //skillid
        for (int i = 0; i < monsters.size(); i++) {
            pw.write(1);
            pw.writeInt(i + 2);
            pw.writeInt(1);
            pw.writeInt(Randomizer.rand(0x2A, 0x2B));
            pw.writeInt(Randomizer.rand(0x03, 0x04));
            pw.writeInt(Randomizer.rand(0x43, 0xF5));
            pw.writeInt(200);
            pw.writeLong(0);
            pw.writeInt(Randomizer.nextInt());
            pw.writeInt(0);
        }
        pw.write(0);
        //for (Point p : pos) {
        pw.writeInt(p1.x);
        pw.writeInt(p1.y);
        pw.writeInt(p2.x);
        pw.writeInt(p2.y);
        //}
        pw.writeInt(javelin);
        //System.out.println(pw.toString());
        
        // pw.writeZeroBytes(69); //We might need this =p
        return pw.getPacket();
    }
    
    //
    
    
    public static byte[] sendJobup(boolean family, int jobid, String name) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.NOTIFY_JOB_CHANGE.getValue());
        pw.write(family ? 1 : 0);
        pw.writeInt(jobid);
        pw.writeMapleAsciiString(new StringBuilder().append(!family ? "> " : "").append(name).toString());

        return pw.getPacket();
    }

    public static byte[] getAvatarMega(MapleCharacter chr, int channel, int itemId, List<String> text, boolean ear) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SET_AVATAR_MEGAPHONE.getValue());
        pw.writeInt(itemId);
        pw.writeMapleAsciiString(chr.getName());
        for (String i : text) {
            pw.writeMapleAsciiString(i);
        }
        pw.writeInt(channel - 1);
        pw.write(ear ? 1 : 0);
        PacketHelper.addCharLook(pw, chr, true, false);

        return pw.getPacket();
    }

    public static byte[] GMPoliceMessage(boolean dc) {
        PacketWriter pw = new PacketWriter(3);

        pw.writeShort(SendPacketOpcode.GM_POLICE.getValue());
        pw.write(dc ? 10 : 0);

        return pw.getPacket();
    }

    public static byte[] GMPoliceMessage(String msg) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PROGRESS_MESSAGE_FONT.getValue());
        pw.writeMapleAsciiString(msg);

        return pw.getPacket();
    }

    public static byte[] pendantSlot(boolean p) { // slot -59
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SET_BUY_EQUIP_EXT.getValue());
        pw.write(p ? 1 : 0);
        return pw.getPacket();
    }

    public static byte[] followRequest(int chrid) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SET_PASSENGER_REQUEST.getValue());
        pw.writeInt(chrid);

        return pw.getPacket();
    }

    public static byte[] getTopMsg(String msg) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SCRIPT_PROGRESS_MESSAGE.getValue());
        pw.writeMapleAsciiString(msg);

        return pw.getPacket();
    }

    public static byte[] getMidMsg(String msg, boolean keep, int index) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.STATIC_SCREEN_MESSAGE.getValue());
        pw.write(index);
        pw.writeMapleAsciiString(msg);
        pw.write(keep ? 0 : 1);

        return pw.getPacket();
    }

    public static byte[] clearMidMsg() {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.OFF_STATIC_SCREEN_MESSAGE.getValue());

        return pw.getPacket();
    }

    public static byte[] getSpecialMsg(String msg, int type, boolean show) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.WEATHER_EFFECT_NOTICE.getValue());
        pw.writeMapleAsciiString(msg);
        pw.writeInt(type);
        pw.writeInt(show ? 0 : 1);
        // pw.writeInt(0);
        pw.write(0);
        return pw.getPacket();
    }

    public static byte[] gmBoard(int increnement, String url) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.GM_STORY_BOARD.getValue());
        pw.writeInt(increnement); //Increnement number
        pw.writeMapleAsciiString(url);

        return pw.getPacket();
    }

    public static byte[] updateJaguar(MapleCharacter from) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.WILD_HUNTER_INFO.getValue());
        PacketHelper.addJaguarInfo(pw, from);

        return pw.getPacket();
    }

    public static byte[] loadInformation(byte mode, int location, int birthday, int favoriteAction, int favoriteLocation, boolean success) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.YOUR_INFORMATION.getValue());
        pw.write(mode);
        if (mode == 2) {
            pw.writeInt(location);
            pw.writeInt(birthday);
            pw.writeInt(favoriteAction);
            pw.writeInt(favoriteLocation);
        } else if (mode == 4) {
            pw.write(success ? 1 : 0);
        }

        return pw.getPacket();
    }

    public static byte[] saveInformation(boolean fail) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.YOUR_INFORMATION.getValue());
        pw.write(4);
        pw.write(fail ? 0 : 1);

        return pw.getPacket();
    }

    public static byte[] findFriendResult(byte mode, List<MapleCharacter> friends, int error, MapleCharacter chr) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.FIND_FRIEND.getValue());
        pw.write(mode);
        switch (mode) {
            case 6:
                pw.writeInt(0);
                pw.writeInt(0);
                break;
            case 8:
                pw.writeShort(friends.size());
                for (MapleCharacter mc : friends) {
                    pw.writeInt(mc.getId());
                    pw.writeMapleAsciiString(mc.getName());
                    pw.write(mc.getLevel());
                    pw.writeShort(mc.getJob());
                    pw.writeInt(0);
                    pw.writeInt(0);
                }
                break;
            case 9:
                pw.write(error);
                break;
            case 11:
                pw.writeInt(chr.getId());
                PacketHelper.addCharLook(pw, chr, true, false);
                break;
        }

        return pw.getPacket();
    }

    public static byte[] sendPinkBeanChoco() {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PINKBEAN_CHOCO.getValue());
        pw.writeInt(0);
        pw.write(1); // show the dragonball box
        pw.write(0);
        pw.write(0); // allowed to summon the dragon lord
        pw.writeInt(0);

        return pw.getPacket();
    }

    public static byte[] pamSongUI() {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.PAM_SONG.getValue());
        return pw.getPacket();
    }

    public static byte[] ultimateExplorer() {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.ULTIMATE_EXPLORER.getValue());
        return pw.getPacket();
    }

    public static byte[] professionInfo(String skil, int level1, int level2, int chance) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.RESULT_INSTANCE_TABLE.getValue());
        pw.writeMapleAsciiString(skil);
        pw.writeInt(level1);
        pw.writeInt(level2);
        pw.write(1);
        pw.writeInt((skil.startsWith("9200")) || (skil.startsWith("9201")) ? 100 : chance);

        return pw.getPacket();
    }

    public static byte[] updateAzwanFame(int level, int fame, boolean levelup) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.CHARACTER_HONOR_EXP.getValue());
        pw.writeInt(level);
        pw.writeInt(fame);
        pw.write(levelup ? 1 : 0);

        return pw.getPacket();
    }

    public static byte[] showAzwanKilled() {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.READY_FOR_RESPAWN.getValue());

        return pw.getPacket();
    }

    public static byte[] showSilentCrusadeMsg(byte type, short chapter) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.CROSS_HUNTER_COMPLETE_RESULT.getValue());
        pw.write(type);
        pw.writeShort(chapter - 1);

        /* type:
         * 0 - open ui (short is chapter)
         * 2 - not enough inventory space
         * 3 - failed due to unknown error
         */
        return pw.getPacket();
    }

    public static byte[] getSilentCrusadeMsg(byte type) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.CROSS_HUNTER_SHOP_RESULT.getValue());
        pw.write(type);

        return pw.getPacket();
    }

    public static byte[] showSCShopMsg(byte type) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.CROSS_HUNTER_SHOP_RESULT.getValue());
        pw.write(type);

        return pw.getPacket();
    }

    public static byte[] updateImpTime() {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.COOL_TIME_SET.getValue());
        pw.writeInt(0);
        pw.writeLong(0L);

        return pw.getPacket();
    }

    public static byte[] updateImp(MapleImp imp, int mask, int index, boolean login) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.ITEM_POT_CHANGE.getValue());
        pw.write(login ? 0 : 1);
        pw.writeInt(index + 1);
        pw.writeInt(mask);
        if ((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) {
            Pair<?, ?> i = MapleItemInformationProvider.getInstance().getPot(imp.getItemId());
            if (i == null) {
                return enableActions();
            }
            pw.writeInt(((Integer) i.left).intValue());
            pw.write(imp.getLevel());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.STATE.getValue()) != 0)) {
            pw.write(imp.getState());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.FULLNESS.getValue()) != 0)) {
            pw.writeInt(imp.getFullness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CLOSENESS.getValue()) != 0)) {
            pw.writeInt(imp.getCloseness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CLOSENESS_LEFT.getValue()) != 0)) {
            pw.writeInt(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MINUTES_LEFT.getValue()) != 0)) {
            pw.writeInt(0);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.LEVEL.getValue()) != 0)) {
            pw.write(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.FULLNESS_2.getValue()) != 0)) {
            pw.writeInt(imp.getFullness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.UPDATE_TIME.getValue()) != 0)) {
            pw.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CREATE_TIME.getValue()) != 0)) {
            pw.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.AWAKE_TIME.getValue()) != 0)) {
            pw.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.SLEEP_TIME.getValue()) != 0)) {
            pw.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_CLOSENESS.getValue()) != 0)) {
            pw.writeInt(100);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_DELAY.getValue()) != 0)) {
            pw.writeInt(1000);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_FULLNESS.getValue()) != 0)) {
            pw.writeInt(1000);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_ALIVE.getValue()) != 0)) {
            pw.writeInt(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_MINUTES.getValue()) != 0)) {
            pw.writeInt(10);
        }
        pw.write(0);

        return pw.getPacket();
    }

    public static byte[] getMulungRanking() {
        final PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MULUNG_DOJO_RANKING.getValue());
        DojoRankingsData data = DojoRankingsData.loadLeaderboard();
        pw.writeInt(data.totalCharacters); // size
        for (int i = 0; i < data.totalCharacters; i++) {
            pw.writeShort(data.ranks[i]); // rank
            pw.writeMapleAsciiString(data.names[i]); // Character name
            pw.writeLong(data.times[i]); // time in seconds
        }
        return pw.getPacket();
    }

    public static byte[] getCandyRanking(MapleClient c, List<CandyRankingInfo> all) {
        PacketWriter pw = new PacketWriter(10);

        pw.writeShort(SendPacketOpcode.GUILD_CONTENT_RANK.getValue());
        pw.writeInt(all.size());
        for (CandyRankingInfo info : all) {
            pw.writeShort(info.getRank());
            pw.writeMapleAsciiString(info.getName());
        }
        return pw.getPacket();
    }

    public static byte[] showForeignDamageSkin(MapleCharacter chr, int skinid) {
       PacketWriter pw =new PacketWriter();
        pw.writeShort(SendPacketOpcode.SHOW_DAMAGE_SKIN.getValue());
        pw.writeInt(chr.getId());
        pw.writeInt(skinid);
        return pw.getPacket();
    }

    public static byte[] onRedCubeResult(int charId, boolean hasRankedUp, int itemId, short dst, Equip equip) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.RED_CUBE_RESULT.getValue());
        pw.writeInt(charId);
        pw.write(hasRankedUp ? 1 : 0);
        pw.writeInt(itemId);
        pw.writeInt(dst);
        PacketHelper.addItemInfo(pw, equip);

        return pw.getPacket();
    }

    public static byte[] onBlackCubeRequest(boolean bUpgrade, int itemId, short src, short dst, Equip equip) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.BLACK_CUBE_RESULT.getValue());
        pw.writeLong(equip.getUniqueId()); //liSN
        pw.writeBoolean(bUpgrade);
        if(bUpgrade) {
            PacketHelper.addItemInfo(pw, equip);
            pw.writeInt(itemId);
            pw.writeInt(dst);
        }
        pw.writeInt(src);
        return pw.getPacket();
    }

    public static class AlliancePacket {

        public static byte[] getAllianceInfo(MapleGuildAlliance alliance) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(12);
            pw.write(alliance == null ? 0 : 1);
            if (alliance != null) {
                addAllianceInfo(pw, alliance);
            }

            return pw.getPacket();
        }

        private static void addAllianceInfo(PacketWriter pw, MapleGuildAlliance alliance) {
            pw.writeInt(alliance.getId());
            pw.writeMapleAsciiString(alliance.getName());
            for (int i = 1; i <= 5; i++) {
                pw.writeMapleAsciiString(alliance.getRank(i));
            }
            pw.write(alliance.getNoGuilds());
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                pw.writeInt(alliance.getGuildId(i));
            }
            pw.writeInt(alliance.getCapacity());
            pw.writeMapleAsciiString(alliance.getNotice());
        }

        public static byte[] getGuildAlliance(MapleGuildAlliance alliance) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(13);
            if (alliance == null) {
                pw.writeInt(0);
                return pw.getPacket();
            }
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            pw.writeInt(noGuilds);
            for (MapleGuild gg : g) {
                CWvsContext.GuildPacket.getGuildInfo(pw, gg);
            }
            return pw.getPacket();
        }

        public static byte[] allianceMemberOnline(int alliance, int gid, int id, boolean online) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(14);
            pw.writeInt(alliance);
            pw.writeInt(gid);
            pw.writeInt(id);
            pw.write(online ? 1 : 0);

            return pw.getPacket();
        }

        public static byte[] removeGuildFromAlliance(MapleGuildAlliance alliance, MapleGuild expelledGuild, boolean expelled) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(16);
            addAllianceInfo(pw, alliance);
            CWvsContext.GuildPacket.getGuildInfo(pw, expelledGuild);
            pw.write(expelled ? 1 : 0);

            return pw.getPacket();
        }

        public static byte[] addGuildToAlliance(MapleGuildAlliance alliance, MapleGuild newGuild) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(18);
            addAllianceInfo(pw, alliance);
            pw.writeInt(newGuild.getId());
            CWvsContext.GuildPacket.getGuildInfo(pw, newGuild);
            pw.write(0);

            return pw.getPacket();
        }

        public static byte[] sendAllianceInvite(String allianceName, MapleCharacter inviter) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(3);
            pw.writeInt(inviter.getGuildId());
            pw.writeMapleAsciiString(inviter.getName());
            pw.writeMapleAsciiString(allianceName);

            return pw.getPacket();
        }

        public static byte[] getAllianceUpdate(MapleGuildAlliance alliance) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(23);
            addAllianceInfo(pw, alliance);

            return pw.getPacket();
        }

        public static byte[] createGuildAlliance(MapleGuildAlliance alliance) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(15);
            addAllianceInfo(pw, alliance);
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            for (MapleGuild gg : g) {
                CWvsContext.GuildPacket.getGuildInfo(pw, gg);
            }
            return pw.getPacket();
        }

        public static byte[] updateAlliance(MapleGuildCharacter mgc, int allianceid) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(24);
            pw.writeInt(allianceid);
            pw.writeInt(mgc.getGuildId());
            pw.writeInt(mgc.getId());
            pw.writeInt(mgc.getLevel());
            pw.writeInt(mgc.getJobId());

            return pw.getPacket();
        }

        public static byte[] updateAllianceLeader(int allianceid, int newLeader, int oldLeader) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(25);
            pw.writeInt(allianceid);
            pw.writeInt(oldLeader);
            pw.writeInt(newLeader);

            return pw.getPacket();
        }

        public static byte[] allianceRankChange(int aid, String[] ranks) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(26);
            pw.writeInt(aid);
            for (String r : ranks) {
                pw.writeMapleAsciiString(r);
            }

            return pw.getPacket();
        }

        public static byte[] updateAllianceRank(MapleGuildCharacter mgc) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(27);
            pw.writeInt(mgc.getId());
            pw.write(mgc.getAllianceRank());

            return pw.getPacket();
        }

        public static byte[] changeAllianceNotice(int allianceid, String notice) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(28);
            pw.writeInt(allianceid);
            pw.writeMapleAsciiString(notice);

            return pw.getPacket();
        }

        public static byte[] disbandAlliance(int alliance) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(29);
            pw.writeInt(alliance);

            return pw.getPacket();
        }

        public static byte[] changeAlliance(MapleGuildAlliance alliance, boolean in) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(1);
            pw.write(in ? 1 : 0);
            pw.writeInt(in ? alliance.getId() : 0);
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < noGuilds; i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            pw.write(noGuilds);
            for (int i = 0; i < noGuilds; i++) {
                pw.writeInt(g[i].getId());

                Collection<MapleGuildCharacter> members = g[i].getMembers();
                pw.writeInt(members.size());
                for (MapleGuildCharacter mgc : members) {
                    pw.writeInt(mgc.getId());
                    pw.write(in ? mgc.getAllianceRank() : 0);
                }
            }

            return pw.getPacket();
        }

        public static byte[] changeAllianceLeader(int allianceid, int newLeader, int oldLeader) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(2);
            pw.writeInt(allianceid);
            pw.writeInt(oldLeader);
            pw.writeInt(newLeader);

            return pw.getPacket();
        }

        public static byte[] changeGuildInAlliance(MapleGuildAlliance alliance, MapleGuild guild, boolean add) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(4);
            pw.writeInt(add ? alliance.getId() : 0);
            pw.writeInt(guild.getId());
            Collection<MapleGuildCharacter> members = guild.getMembers();
            pw.writeInt(members.size());
            for (MapleGuildCharacter mgc : members) {
                pw.writeInt(mgc.getId());
                pw.write(add ? mgc.getAllianceRank() : 0);
            }

            return pw.getPacket();
        }

        public static byte[] changeAllianceRank(int allianceid, MapleGuildCharacter player) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.ALLIANCE_RESULT.getValue());
            pw.write(5);
            pw.writeInt(allianceid);
            pw.writeInt(player.getId());
            pw.writeInt(player.getAllianceRank());

            return pw.getPacket();
        }
    }

    public static class BuddylistPacket {

        public static byte[] declinedRequest(String declinedName) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.FRIEND_RESULT.getValue());
            pw.write(BuddyType.DECLINE_MESSAGE.getValue());
            pw.writeMapleAsciiString(declinedName);

            return pw.getPacket();
        }

        public static byte[] updateBuddylist(Collection<BuddylistEntry> buddylist) {
            return updateBuddylist(buddylist, false, false);
        }

        public static byte[] updateBuddylist(Collection<BuddylistEntry> buddylist, boolean deleted, boolean add) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.FRIEND_RESULT.getValue());
            pw.write(deleted ? BuddyType.UPDATE.getValue() : add ? BuddyType.UPDATE.getValue() : BuddyType.UPDATE.getValue()); // TODO find add/delete values
            pw.writeInt(buddylist.size());
            for (BuddylistEntry buddy : buddylist) {
                buddy.encode(pw);
            }
            for (int x = 0; x < buddylist.size(); x++) {
                pw.writeInt(0);
            }

            return pw.getPacket();
        }

        public static byte[] updateBuddyChannel(int characterid, int channel) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.FRIEND_RESULT.getValue());
            pw.write(20);
            pw.writeInt(characterid);
            pw.write(0);
            pw.writeInt(channel);

            return pw.getPacket();
        }

        public static byte[] requestMessage(String toName) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.FRIEND_RESULT.getValue());
            pw.write(BuddyType.REQUEST_SENT.getValue());
            pw.writeMapleAsciiString(toName);

            return pw.getPacket();
        }

//        @Deprecated
//        public static byte[] requestBuddylistAdd(int cidFrom, String nameFrom, int levelFrom, int jobFrom) {
//            PacketWriter pw = new PacketWriter();
//
//            pw.writeShort(SendPacketOpcode.FRIEND_RESULT.getValue());
//            pw.write(BuddyType.REQUEST_ADD.getValue());
//            // need 317?
//            pw.writeInt(cidFrom);
//            pw.writeMapleAsciiString(nameFrom);
//            pw.writeInt(levelFrom);
//            pw.writeInt(jobFrom);
//            pw.writeInt(0);//v115
//            pw.writeInt(cidFrom);
//            pw.writeAsciiString(nameFrom, 13);
//            pw.write(1);
//            pw.writeInt(0);
//            pw.writeAsciiString("ETC", 16);
//            pw.writeShort(0);//was1
//            return pw.getPacket();
//        }

        public static byte[] requestBuddyAdd(boolean inShop, int id, int accId, String name, int level, int job, int subJob, BuddylistEntry ble) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.FRIEND_RESULT.getValue());
            pw.write(BuddyType.RECEIVE_REQUEST.getValue());
            pw.write(inShop);
            pw.writeInt(id);
            pw.writeInt(accId);
            pw.writeMapleAsciiString(name);
            pw.writeInt(level);
            pw.writeInt(job);
            pw.writeInt(subJob);

            ble.encode(pw);

            return pw.getPacket();
        }

        public static byte[] updateBuddyCapacity(int capacity) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.FRIEND_RESULT.getValue());
            pw.write(BuddyType.UPDATE_FRIEND_MAX.getValue());
            pw.write(capacity);

            return pw.getPacket();
        }

        public static byte[] buddylistMessage(byte message) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.FRIEND_RESULT.getValue());
            pw.write(message);

            return pw.getPacket();
        }

        public static byte[] addBuddy(BuddylistEntry ble) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.FRIEND_RESULT.getValue());
            pw.write(BuddyType.ADD.getValue());
            ble.encode(pw);

            return pw.getPacket();
        }
    }

    public static byte[] giveKilling(int x) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
        PacketHelper.writeSingleMask(pw, CharacterTemporaryStat.KILL_COUNT);
//        pw.writeInt(0);
//        pw.write(0);
//        pw.writeInt(x);
//        pw.writeZeroBytes(6);
        pw.writeShort(0);
        pw.write(0);
        pw.writeInt(x);
        return pw.getPacket();
    }

    public static class ExpeditionPacket {

        public static byte[] expeditionStatus(MapleExpedition me, boolean created, boolean silent) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.EXPEDITION_RESULT.getValue());
            pw.write(created ? 86 : silent ? 72 : 76);//74
            pw.writeInt(me.getType().exped);
            pw.writeInt(0);
            for (int i = 0; i < 6; i++) {
                if (i < me.getParties().size()) {
                    MapleParty party = World.Party.getParty((me.getParties().get(i)).intValue());

                    CWvsContext.PartyPacket.addPartyStatus(-1, party, pw, false, true);
                } else {
                    CWvsContext.PartyPacket.addPartyStatus(-1, null, pw, false, true);
                }

            }

            return pw.getPacket();
        }

        public static byte[] expeditionError(int errcode, String name) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.EXPEDITION_RESULT.getValue());
            pw.write(100);//88
            pw.writeInt(errcode);
            pw.writeMapleAsciiString(name);

            return pw.getPacket();
        }

        public static byte[] expeditionMessage(int code) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.EXPEDITION_RESULT.getValue());
            pw.write(code);

            return pw.getPacket();
        }

        public static byte[] expeditionJoined(String name) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.EXPEDITION_RESULT.getValue());
            pw.write(87);//75
            pw.writeMapleAsciiString(name);

            return pw.getPacket();
        }

        public static byte[] expeditionLeft(String name) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.EXPEDITION_RESULT.getValue());
            pw.write(91);//79
            pw.writeMapleAsciiString(name);

            return pw.getPacket();
        }

        public static byte[] expeditionLeaderChanged(int newLeader) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.EXPEDITION_RESULT.getValue());
            pw.write(96);//84
            pw.writeInt(newLeader);

            return pw.getPacket();
        }

        public static byte[] expeditionUpdate(int partyIndex, MapleParty party) {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.EXPEDITION_RESULT.getValue());
            pw.write(97);//85
            pw.writeInt(0);
            pw.writeInt(partyIndex);

            CWvsContext.PartyPacket.addPartyStatus(-1, party, pw, false, true);

            return pw.getPacket();
        }

        public static byte[] expeditionInvite(MapleCharacter from, int exped) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.EXPEDITION_RESULT.getValue());
            pw.write(99);//87
            pw.writeInt(from.getLevel());
            pw.writeInt(from.getJob());
            pw.writeInt(0);
            pw.writeMapleAsciiString(from.getName());
            pw.writeInt(exped);

            return pw.getPacket();
        }
    }

    public static class PartyPacket {

        public static byte[] partyCreated(MapleParty party) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.PARTY_RESULT.getValue());
            pw.write(PartyType.CREATE.getValue());
            pw.writeInt(party.getId());
            pw.writeInt(999999999); // dwTownID
            pw.writeInt(999999999); // dwFieldID
            pw.writeInt(0); // nGrade
            pw.writeShort(0); // pt.x
            pw.writeShort(0); // pt.y
            pw.write(party.isAppliable());
            pw.write(0); // dwCID
            pw.write(1); // sApplierName._m_pStr
            pw.writeMapleAsciiString(party.getName());
            
            return pw.getPacket();
        }

        public static byte[] partyInviteOld(MapleCharacter from) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.PARTY_RESULT.getValue());
            pw.write(25); // 18
            pw.writeInt(from.getParty() == null ? 0 : from.getParty().getId());
            pw.writeMapleAsciiString(from.getName());
            pw.writeInt(from.getLevel());
            pw.writeInt(from.getJob());
            pw.write(0);
            pw.writeInt(0);
            return pw.getPacket();
        }

        public static byte[] partyInvite(MapleCharacter from) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.PARTY_RESULT.getValue());
            pw.write(PartyType.PARTY_INVITE.getValue()); // 18
            pw.writeInt(from.getParty() == null ? 0 : from.getParty().getId()); // idk
            pw.writeMapleAsciiString(from.getName());
            pw.writeInt(from.getLevel());
            pw.writeInt(from.getJob());
            pw.writeInt(from.getSubcategory());
            pw.write(0); // idk
            pw.write(0); // something with party member (accessed as &v219 to get B_nPartyID)
            boolean unk = false;
            pw.write(unk);
            if(unk){
                pw.write(0);
            }
//            CWvsContext.PartyPacket.addPartyStatus(from.getClient().getChannel(), from.getParty(), pw, false);

            return pw.getPacket();
        }

        public static byte[] partyRequestInvite(MapleCharacter from) { // does not seems to exist anywhere
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.PARTY_RESULT.getValue());
            pw.write(PartyType.REQUEST_INVITE.getValue());
            pw.writeInt(from.getId());
            pw.writeMapleAsciiString(from.getName());
            pw.writeInt(from.getLevel());
            pw.writeInt(from.getJob());

            return pw.getPacket();
        }

        public static byte[] partyStatusMessage(int message, String charname) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.PARTY_RESULT.getValue());
            pw.write(message);
            if (message == PartyType.INVITE_MESSAGE.getValue()) {
                pw.writeMapleAsciiString(charname);
            }

            return pw.getPacket();
        }

        public static void addPartyStatus(int forchannel, MapleParty party, PacketWriter lew, boolean leaving) {
            addPartyStatus(forchannel, party, lew, leaving, false);
        }

        public static void addPartyStatus(int forchannel, MapleParty party, PacketWriter pw, boolean leaving, boolean exped) {
            List<MaplePartyCharacter> partymembers;
            if (party == null) {
                partymembers = new ArrayList<>();
            } else {
                partymembers = new ArrayList<>(party.getMembers());
            }
            while (partymembers.size() < 6) {
                partymembers.add(new MaplePartyCharacter());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                pw.writeInt(partychar.getId());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                pw.writeAsciiString(partychar.getName(), 13);
            }
            for (MaplePartyCharacter partychar : partymembers) {
                pw.writeInt(partychar.getJobId());
            }
            for(MaplePartyCharacter partychar : partymembers) {
                pw.writeInt(60); // unk
            }
            for (MaplePartyCharacter partychar : partymembers) {
                pw.writeInt(partychar.getLevel());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                pw.writeInt(partychar.isOnline() ? partychar.getChannel() - 1 : -2);
            }
            for(MaplePartyCharacter partychar : partymembers) {
                pw.writeInt(90); // unk
            }
            for(MaplePartyCharacter partychar : partymembers) {
                pw.writeInt(30); // unk
            }
            
            pw.writeInt(party == null ? 0 : party.getLeader().getId());
            if (exped) {
                return;
            }
            for (MaplePartyCharacter partychar : partymembers) {
                pw.writeInt(partychar.getChannel() == forchannel ? partychar.getMapid() : 999999999);
            }
            for (MaplePartyCharacter partychar : partymembers) {
                if ((partychar.getChannel() == forchannel) && (!leaving)) {
                    pw.writeInt(partychar.getDoorTown());
                    pw.writeInt(partychar.getDoorTarget());
                    pw.writeInt(partychar.getDoorSkill());
                    pw.writeInt(partychar.getDoorPosition().x);
                    pw.writeInt(partychar.getDoorPosition().y);
                } else {
                    pw.writeInt(leaving ? 999999999 : 0);
                    pw.writeLong(leaving ? 999999999L : 0L);
                    pw.writeLong(leaving ? -1L : 0L);
                }
            }
            pw.write(party.isAppliable());
            pw.write(party.isUnkBool());
            pw.writeMapleAsciiString(party.getName());
        }

        public static byte[] updateParty(int forChannel, MapleParty party, PartyOperation op, MaplePartyCharacter target) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.PARTY_RESULT.getValue());
            switch (op) {
                case DISBAND:
                case EXPEL:
                case LEAVE:
                    pw.write(PartyType.LEAVE.getValue());
                    pw.writeInt(party.getId());
                    pw.writeInt(target.getId());
                    pw.write(op == PartyOperation.DISBAND ? 0 : 1);
                    if (op == PartyOperation.DISBAND) {
                        break;
                    }
                    pw.write(op == PartyOperation.EXPEL ? 1 : 0);
                    pw.writeMapleAsciiString(target.getName());
                    addPartyStatus(forChannel, party, pw, op == PartyOperation.LEAVE);
                    break;
                case JOIN:
                    pw.write(PartyType.JOIN.getValue());
                    pw.writeInt(party.getId()); // new 179
                    pw.writeMapleAsciiString(target.getName());
                    pw.write(0); // ? new 179
                    pw.writeInt(0); // ? new 179
                    addPartyStatus(forChannel, party, pw, false);
                    break;
                case SILENT_UPDATE:
                case LOG_ONOFF:
                    pw.write(PartyType.UPDATE.getValue());
                    pw.writeInt(party.getId());
                    addPartyStatus(forChannel, party, pw, op == PartyOperation.LOG_ONOFF);
                    break;
                case CHANGE_LEADER:
                case CHANGE_LEADER_DC:
                    pw.write(48); // 45
                    pw.writeInt(target.getId());
                    pw.write(op == PartyOperation.CHANGE_LEADER_DC ? 1 : 0);
            }
            return pw.getPacket();
        }

        public static byte[] partyPortal(int townId, int targetId, int skillId, Point position, boolean animation) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.PARTY_RESULT.getValue());
            pw.write(61);
            pw.write(animation ? 0 : 1);
            pw.writeInt(townId);
            pw.writeInt(targetId);
            pw.writeInt(skillId);
            pw.writePos(position);

            return pw.getPacket();
        }

        public static byte[] partyMemberCandidateResult(MapleCharacter chr){
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.PARTY_MEMBER_CANDIDATE_RESULT.getValue());
            Set<MapleCharacter> charList = new HashSet<>(chr.getMap().getCharacters());
            charList.remove(chr);
            pw.write(charList.size());
            for(MapleCharacter mapleCharacter : charList){
                pw.writeInt(mapleCharacter.getId());
                pw.writeMapleAsciiString(mapleCharacter.getName());
                pw.writeShort(mapleCharacter.getJob());
                pw.writeShort(mapleCharacter.getSubcategory());
                pw.write(mapleCharacter.getLevel());

            }

            return pw.getPacket();
        }

        public static byte[] getPartyListing(PartySearchType pst) {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.PARTY_RESULT.getValue());
            pw.write(147);
            pw.writeInt(pst.id);
            final List<PartySearch> parties = World.Party.searchParty(pst);
            pw.writeInt(parties.size());
            for (PartySearch party : parties) {
                pw.writeInt(0);
                pw.writeInt(2);
                if (pst.exped) {
                    MapleExpedition me = World.Party.getExped(party.getId());
                    pw.writeInt(me.getType().maxMembers);
                    pw.writeInt(party.getId());
                    pw.writeAsciiString(party.getName(), 48);
                    for (int i = 0; i < 5; i++) {
                        if (i < me.getParties().size()) {
                            MapleParty part = World.Party.getParty((me.getParties().get(i)).intValue());
                            if (part != null) {
                                addPartyStatus(-1, part, pw, false, true);
                            } else {
                                pw.write(new byte[202]);
                            }
                        } else {
                            pw.write(new byte[202]);
                        }
                    }
                } else {
                    pw.writeInt(0);
                    pw.writeInt(party.getId());
                    pw.writeAsciiString(party.getName(), 48);
                    addPartyStatus(-1, World.Party.getParty(party.getId()), pw, false, true);
                }

                pw.writeShort(0);
            }

            return pw.getPacket();
        }

        public static byte[] partyListingAdded(PartySearch ps) {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.PARTY_RESULT.getValue());
            pw.write(93);
            pw.writeInt(ps.getType().id);
            pw.writeInt(0);
            pw.writeInt(1);
            if (ps.getType().exped) {
                MapleExpedition me = World.Party.getExped(ps.getId());
                pw.writeInt(me.getType().maxMembers);
                pw.writeInt(ps.getId());
                pw.writeAsciiString(ps.getName(), 48);
                for (int i = 0; i < 5; i++) {
                    if (i < me.getParties().size()) {
                        MapleParty party = World.Party.getParty((me.getParties().get(i)).intValue());
                        if (party != null) {
                            addPartyStatus(-1, party, pw, false, true);
                        } else {
                            pw.write(new byte[202]);
                        }
                    } else {
                        pw.write(new byte[202]);
                    }
                }
            } else {
                pw.writeInt(0);
                pw.writeInt(ps.getId());
                pw.writeAsciiString(ps.getName(), 48);
                addPartyStatus(-1, World.Party.getParty(ps.getId()), pw, false, true);
            }
            pw.writeShort(0);

            return pw.getPacket();
        }

        public static byte[] showPartySearch(List<MapleParty> chr) {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.PARTY_CANDIDATE_RESULT.getValue());
            pw.write(chr.size());
            for (MapleParty c : chr) {
                pw.writeInt(c.getId());
                pw.writeMapleAsciiString(c.getLeader().getName());
                pw.write(c.getLeader().getLevel());
                pw.write(c.getLeader().isOnline() ? 1 : 0);
                pw.write(c.getMembers().size());
                for (MaplePartyCharacter ch : c.getMembers()) {
                    pw.writeInt(ch.getId());
                    pw.writeMapleAsciiString(ch.getName());
                    pw.writeShort(ch.getJobId());
                    pw.write(ch.getLevel());
                    pw.write(ch.isOnline() ? 1 : 0);
                }
            }
            return pw.getPacket();
        }
    }

    public static class GuildPacket {
    	
    	// Probably going to need to create a new class for all of this information.

        public static byte[] guildInvite(int gid, String charName, int levelFrom, int jobFrom) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(7); // updated
            pw.writeInt(gid);
            pw.writeMapleAsciiString(charName);
            pw.writeInt(levelFrom);
            pw.writeInt(jobFrom);
            pw.writeInt(0);
            return pw.getPacket();
        }

        public static byte[] showGuildInfo(MapleCharacter c) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(49); // was 32
            if ((c == null) || (c.getMGC() == null)) {
                pw.write(0);
                return pw.getPacket();
            }
            MapleGuild g = World.Guild.getGuild(c.getGuildId());
            if (g == null) {
                pw.write(0);
                return pw.getPacket();
            }
            pw.write(0);
            pw.write(1);
            getGuildInfo(pw, g);
            
            // The amount of guild exp needed per level.
            pw.writeInt(0);

            return pw.getPacket();
        }

        private static void getGuildInfo(PacketWriter pw, MapleGuild guild) {
            pw.writeInt(guild.getId());
            pw.writeMapleAsciiString(guild.getName());
            for (int i = 1; i <= 5; i++) {
                pw.writeMapleAsciiString(guild.getRankTitle(i));
            }
            guild.addMemberData(pw);

            pw.writeShort(0); // ?
            pw.writeInt(guild.getCapacity());
            pw.writeShort(guild.getLogoBG());
            pw.write(guild.getLogoBGColor());
            pw.writeShort(guild.getLogo());
            pw.write(guild.getLogoColor());
            pw.writeMapleAsciiString(guild.getNotice());
            
            pw.writeInt(guild.getGP());
            pw.writeInt(guild.getGP());
            pw.writeInt(guild.getAllianceId() > 0 ? guild.getAllianceId() : 0);
            pw.write(guild.getLevel());
            pw.writeShort(0); // nRank
            pw.writeInt(0); // nGGP (global guild points)
            
            pw.writeShort(guild.getSkills().size());
            for (MapleGuildSkill i : guild.getSkills()) {
                pw.writeInt(i.skillID);
                pw.writeShort(i.level);
                pw.writeLong(PacketHelper.getTime(i.timestamp));
                pw.writeMapleAsciiString(i.purchaser);
                pw.writeMapleAsciiString(i.activator);
            }
            pw.write(0); // ?
        }

        public static byte[] newGuildInfo(MapleCharacter c) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(56); // was 38
            if ((c == null) || (c.getMGC() == null)) {
                return genericGuildMessage((byte) 37);
            }
            MapleGuild g = World.Guild.getGuild(c.getGuildId());
            if (g == null) {
                return genericGuildMessage((byte) 37);
            }
            getGuildInfo(pw, g);

            return pw.getPacket();
        }

        public static byte[] newGuildMember(MapleGuildCharacter mgc) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(45);
            pw.writeInt(mgc.getGuildId());
            pw.writeInt(mgc.getId());
            
            pw.writeAsciiString(mgc.getName(), 13);
            pw.writeInt(mgc.getJobId());
            pw.writeInt(mgc.getLevel());
            pw.writeInt(mgc.getGuildRank());
            pw.writeInt(mgc.isOnline() ? 1 : 0);
            pw.writeInt(mgc.getAllianceRank());
            pw.writeInt(mgc.getGuildContribution());

            return pw.getPacket();
        }

        public static byte[] memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(bExpelled ? 53 : 50);
            pw.writeInt(mgc.getGuildId());
            pw.writeInt(mgc.getId());
            pw.writeMapleAsciiString(mgc.getName());

            return pw.getPacket();
        }

        public static byte[] guildDisband(int gid) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(81); // was 56
            pw.writeInt(gid);

            return pw.getPacket();
        }

        public static byte[] guildCapacityChange(int gid, int capacity) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(90); // 64
            pw.writeInt(gid);
            pw.write(capacity);

            return pw.getPacket();
        }
        
        public static byte[] guildContribution(int gid, int cid, int c) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(100); // was 72
            pw.writeInt(gid);
            pw.writeInt(cid);
            pw.writeInt(510);
            pw.writeInt(510);
            pw.writeInt(c);
            pw.writeLong(PacketHelper.getTime(-2));

            return pw.getPacket();
        }

        public static byte[] changeRank(MapleGuildCharacter mgc) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(98); // was 70
            pw.writeInt(mgc.getGuildId());
            pw.writeInt(mgc.getId());
            pw.write(mgc.getGuildRank());

            return pw.getPacket();
        }

        public static byte[] rankTitleChange(int gid, String[] ranks) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(96); // was 68
            pw.writeInt(gid);
            for (String r : ranks) {
                pw.writeMapleAsciiString(r);
            }

            return pw.getPacket();
        }

        public static byte[] guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(101); // 73																					
            pw.writeInt(gid);
            pw.writeShort(bg);
            pw.write(bgcolor);
            pw.writeShort(logo);
            pw.write(logocolor);

            return pw.getPacket();
        }

        public static byte[] updateGP(int gid, int GP, int glevel) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(107); // was 79
            pw.writeInt(gid);
            pw.writeInt(0); // nHonor
            pw.writeInt(GP);
            pw.writeInt(glevel);

            return pw.getPacket();
        }

        public static byte[] guildNotice(int gid, String notice) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(75);
            pw.writeInt(gid);
            pw.writeMapleAsciiString(notice);

            return pw.getPacket();
        }

        public static byte[] guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(66);
            pw.writeInt(mgc.getGuildId());
            pw.writeInt(mgc.getId());
            pw.writeInt(mgc.getLevel());
            pw.writeInt(mgc.getJobId());

            return pw.getPacket();
        }

        public static byte[] guildMemberOnline(int gid, int cid, boolean bOnline) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(67);
            pw.writeInt(gid);
            pw.writeInt(cid);
            pw.write(bOnline ? 1 : 0);

            return pw.getPacket();
        }

        public static byte[] showGuildRanks(int npcid, List<MapleGuildRanking.GuildRankingInfo> all) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(80);
            pw.writeInt(npcid);
            pw.writeInt(all.size());
            for (MapleGuildRanking.GuildRankingInfo info : all) {
                pw.writeShort(0);
                pw.writeMapleAsciiString(info.getName());
                pw.writeInt(info.getGP());
                pw.writeInt(info.getLogo());
                pw.writeInt(info.getLogoColor());
                pw.writeInt(info.getLogoBg());
                pw.writeInt(info.getLogoBgColor());
            }

            return pw.getPacket();
        }

        public static byte[] guildSkillPurchased(int gid, int sid, int level, long expiration, String purchase, String activate) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(85);
            pw.writeInt(gid);
            pw.writeInt(sid);
            pw.writeShort(level);
            pw.writeLong(PacketHelper.getTime(expiration));
            pw.writeMapleAsciiString(purchase);
            pw.writeMapleAsciiString(activate);

            return pw.getPacket();
        }

        public static byte[] guildLeaderChanged(int gid, int oldLeader, int newLeader, int allianceId) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(123); // 89
            pw.writeInt(gid);
            pw.writeInt(oldLeader);
            pw.writeInt(newLeader);
            pw.write(1);
            pw.writeInt(allianceId);

            return pw.getPacket();
        }

        public static byte[] denyGuildInvitation(String charname) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(86); // was 61
            pw.writeMapleAsciiString(charname);

            return pw.getPacket();
        }

        public static byte[] genericGuildMessage(byte code) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GUILD_RESULT.getValue());
            pw.write(code); //30 = cant find in ch
            if (code == 87) {
                pw.writeInt(0);
            }
            if ((code == 3) || (code == 59) || (code == 60) || (code == 61) || (code == 84) || (code == 87)) {
                pw.writeMapleAsciiString("");
            }

            return pw.getPacket();
        }

        public static byte[] BBSThreadList(List<MapleBBSThread> bbs, int start) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.BBS_OPERATION.getValue());
            pw.write(6);
            if (bbs == null) {
                pw.write(0);
                pw.writeLong(0L);
                return pw.getPacket();
            }
            int threadCount = bbs.size();
            MapleBBSThread notice = null;
            for (MapleBBSThread b : bbs) {
                if (b.isNotice()) {
                    notice = b;
                    break;
                }
            }
            
            pw.write(notice == null ? 0 : 1);
            if (notice != null) {
                addThread(pw, notice);
            }
            
            if (threadCount < start) {
                start = 0;
            }
            
            pw.writeInt(threadCount);
            
            int pages = Math.min(10, threadCount - start);
            pw.writeInt(pages);
            
            for (int i = 0; i < pages; i++) {
                addThread(pw, (MapleBBSThread) bbs.get(start + i));
            }

            return pw.getPacket();
        }

        private static void addThread(PacketWriter pw, MapleBBSThread rs) {
            pw.writeInt(rs.localthreadID);
            pw.writeInt(rs.ownerID);
            pw.writeMapleAsciiString(rs.name);
            pw.writeLong(PacketHelper.getKoreanTimestamp(rs.timestamp));
            pw.writeInt(rs.icon);
            pw.writeInt(rs.getReplyCount());
        }

        public static byte[] showThread(MapleBBSThread thread) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.BBS_OPERATION.getValue());
            pw.write(7);
            pw.writeInt(thread.localthreadID);
            pw.writeInt(thread.ownerID);
            pw.writeLong(PacketHelper.getKoreanTimestamp(thread.timestamp));
            pw.writeMapleAsciiString(thread.name);
            pw.writeMapleAsciiString(thread.text);
            pw.writeInt(thread.icon);
            pw.writeInt(thread.getReplyCount());
            for (MapleBBSThread.MapleBBSReply reply : thread.replies.values()) {
                pw.writeInt(reply.replyid);
                pw.writeInt(reply.ownerID);
                pw.writeLong(PacketHelper.getKoreanTimestamp(reply.timestamp));
                pw.writeMapleAsciiString(reply.content);
            }

            return pw.getPacket();
        }
    }

    public static class InfoPacket {
    	
    	public static byte[] updateQuest(MapleQuestStatus quest) {
    		return showStatusInfo(quest, null, 1, 0, 0, 0, 0, 0, null, null, null);
    	}
    	
    	public static byte[] getExpiredMessage(int itemid) {
    		return showStatusInfo(3, 0, 0, 0, 0, itemid, null, null, null);
    	}
    	
    	public static byte[] getSpMessage(int job, int sp) {
    		return showStatusInfo(5, 0, job, sp, 0, 0, null, null, null);
    	}
    	
    	public static byte[] getShowFameGain(int gain) {
    		return showStatusInfo(6, 0, 0, 0, gain, 0, null, null, null);
    	}
    	
    	public static byte[] getMesoGain(int gain) {
    		return showStatusInfo(7, 0, 0, 0, gain, 0, null, null, null);
    	}
    	
    	public static byte[] getGPMessage(int gain) {
    		return showStatusInfo(8, 0, 0, 0, gain, 0, null, null, null);
    	}
    	
    	public static byte[] getGPContribution(int gain) {
    		return showStatusInfo(9, 0, 0, 0, gain, 0, null, null, null);
    	}
    	
    	public static byte[] getStatusMsg(int itemid) {
    		return showStatusInfo(10, 0, 0, 0, 0, itemid, null, null, null);
    	}
    	
    	public static byte[] getMessage(String message) {
    		return showStatusInfo(11, 0, 0, 0, 0, 0, message, null, null);
    	}
    	
    	public static byte[] updateInfoQuest(int questid, String data) {
    		return showStatusInfo(13, questid, 0, 0, 0, 0, data, null, null);
    	}
    	
    	public static byte[] showExpireMessage(List<Integer> items) {
    		return showStatusInfo(15, 0, 0, 0, 0, 0, null, items, null);
    	}
    	
    	public static byte[] showItemReplaceMessage(List<String> messages) {
    		return showStatusInfo(null, null, 16, 0, 0, 0, 0, 0, null, null, messages);
    	}
    	
    	public static byte[] showTraitGain(MapleTraitType trait, int amount) {
    		return showStatusInfo(null, trait, 19, 0, 0, 0, amount, 0, null, null, null);
    	}
    	
    	public static byte[] showTraitMaxed(MapleTraitType trait) {
    		return showStatusInfo(null, trait, 20, 0, 0, 0, 0, 0, null, null, null);
    	}
    	
    	public static byte[] showBattleMessage(int amount) {
    		return showStatusInfo(24, 0, 0, 0, amount, 0, null, null, null);
    	}
    	
    	public static byte[] onCollectionRecordMessage(int index, String data) {
    		return showStatusInfo(40, 0, 0, 0, index, 0, data, null, null);
    	}
    	
    	private static byte[] showStatusInfo(int mode, int questid, int job, int sp, int amount, int itemid, 
    			String data, List<Integer> items, List<String> messages) {
    		return showStatusInfo(null, null, mode, questid, job, sp, amount, itemid, data, items, messages);
    	}
    	
    	public static byte[] showStatusInfo(MapleQuestStatus status, MapleTraitType trait, int mode, int questid, int job, 
    			int sp, int amount, int itemid, String data, List<Integer> items, List<String> messages) {
    		
    		PacketWriter pw = new PacketWriter();
    		pw.writeShort(SendPacketOpcode.MESSAGE.getValue());
    		
    		pw.write(mode);
    		switch(mode) {
    		
    			case 1: // OnQuestRecordMessage
    				pw.writeInt(status.getQuest().getId());
    				pw.write(status.getStatus());
    				switch(status.getStatus()) {
	    				case 0:
	    					pw.write(0);
	    					break;
	    				case 1:
	    					pw.writeMapleAsciiString(status.getCustomData() != null ? status.getCustomData() : "");
	    					break;
	    				case 2:
	    					pw.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
	    					break;
    				}
    				break;
    				
    			case 3: // OnCashItemExpireMessage
    				pw.writeInt(itemid);
    				break;
    		
    			case 5: // OnIncSPMessage
    				pw.writeShort(job);
    				pw.write(sp);
    				break;
    			
    			case 6: // OnIncPOPMessage
    				pw.writeInt(amount);
    				break;
    				
    			case 7: // OnIncMoneyMessage
    				pw.writeInt(amount);
    				pw.writeInt(-1);
    				
    				/** You earned <amount> mesos from <sClientName> for using your extractor. **/
    				// pw.writeInt(24)
    				// pw.writeMapleAsciiString(sClientName);
    				break;
    			
    			case 8: // OnIncGPMessage
    				pw.writeInt(amount);
    				break;
    				
    			case 9: // OnIncCommitmentMessage
    				pw.writeInt(amount);
    				break;
    				
    			case 10: // OnGiveBuffMessage
    				pw.writeInt(itemid);
    				break;
    				
    			case 12: // OnSystemMessage
    				pw.writeMapleAsciiString(data);
    				break;
    				
    			case 13: // OnQuestRecordExMessage
    				pw.writeInt(questid);
    				pw.writeMapleAsciiString(data);
    				break;
    				
    			case 15: // OnItemProtectExpireMessage
    				pw.write(items.size());
    				for(Integer i : items) {
    					pw.writeInt(i);
    				}
    				break;
    				
    			case 16: // OnItemExpireReplaceMessage
    				pw.write(messages.size());
    				for(String s : messages) {
    					pw.writeMapleAsciiString(s);
    				}
    				break;
    				
    			case 19: // OnIncNonCombatStatEXPMessage
    				pw.writeLong(trait.getStat().getValue());
    				pw.writeInt(amount);
    				break;
    			
    			case 20: // OnLimitNonCombatStatEXPMessage
    				pw.writeLong(trait.getStat().getValue());
    				break;
    				
    			case 24: // OnIncPvPPointMessage
    				pw.writeInt(amount); // points
    				pw.writeInt(0); // exp
    				break;

    			case 40: // OnCollectionRecordMessage
    				pw.writeInt(amount); // nCollectionIndex
    				pw.writeMapleAsciiString(data);
    				break;
    			
    		}
    		
    		return pw.getPacket();
    	}

        public static byte[] showMesoGain(long gain, boolean inChat) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.MESSAGE.getValue());
            if (!inChat) {
                pw.write(0);
                pw.write(1);
                pw.write(0);
                pw.writeLong(gain);
                pw.writeShort(0);
            } else {
                pw.write(6);
                pw.writeLong(gain);
                pw.writeInt(-1);
            }

            return pw.getPacket();
        }

        public static byte[] getShowInventoryStatus(int mode) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.MESSAGE.getValue());
            pw.write(0);
            pw.write(mode);
            pw.writeInt(0);
            pw.writeInt(0);

            return pw.getPacket();
        }

        public static byte[] getShowItemGain(int itemId, short quantity) {
            return getShowItemGain(itemId, quantity, false);
        }

        public static byte[] getShowItemGain(int itemId, short quantity, boolean inChat) {
            PacketWriter pw = new PacketWriter();

            if (inChat) {
                pw.writeShort(SendPacketOpcode.EFFECT.getValue());
                pw.write(EffectType.SHOW_ITEM_GAIN.getValue());
                pw.writeInt(0);
                pw.write(1);
                pw.writeInt(itemId);
                pw.writeInt(quantity);
            } else {
                pw.writeShort(SendPacketOpcode.MESSAGE.getValue());
                pw.writeShort(0);
                pw.writeInt(itemId);
                pw.writeInt(quantity);
            }

            return pw.getPacket();
        }

        public static byte[] updateQuestMobKills(MapleQuestStatus status) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.MESSAGE.getValue());
            pw.write(1);
            pw.writeInt(status.getQuest().getId());
            pw.write(1);
            StringBuilder sb = new StringBuilder();
            for (Iterator<?> i$ = status.getMobKills().values().iterator(); i$.hasNext();) {
                int kills = ((Integer) i$.next()).intValue();
                sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
            }
            pw.writeMapleAsciiString(sb.toString());
            pw.writeLong(0L);

            return pw.getPacket();
        }

        public static byte[] GainEXP_Monster(int gain, boolean white, int partyinc, int Class_Bonus_EXP, int Equipment_Bonus_EXP, int Premium_Bonus_EXP) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.MESSAGE.getValue());
            pw.write(4);
            pw.write(white ? 1 : 0);
            pw.writeInt(gain);
            pw.write(0);
            pw.writeInt(0);
            pw.write(0);
            pw.write(0);
            pw.writeInt(0);
            pw.write(0);
            pw.writeInt(partyinc);

            pw.writeInt(Equipment_Bonus_EXP);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.write(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);

            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(Premium_Bonus_EXP);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            return pw.getPacket();
        }

        public static byte[] GainEXP_Others(long gain, boolean inChat, boolean white) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.MESSAGE.getValue());
            pw.write(4);
            pw.write(white ? 1 : 0);
            pw.writeLong(gain);
            pw.write(inChat ? 1 : 0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            if (inChat) {
                pw.writeLong(0L);
            } else {
                pw.writeShort(0);
                pw.write(0);
            }
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.write(0);
            return pw.getPacket();
        }
    }

    public static class BuffPacket {

        public static byte[] giveHoming(int skillid, int mobid, int x) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            PacketHelper.writeSingleMask(pw, CharacterTemporaryStat.StopForceAtomInfo);
            pw.writeShort(0);
            pw.write(0);
            pw.writeInt(1);
            pw.writeLong(skillid);
            pw.write(0);
            pw.writeLong(mobid);
            pw.writeShort(0);
            pw.writeShort(0);
            pw.write(0);
            pw.write(0);//v112
            return pw.getPacket();
        }

      public static byte[] giveMount(int buffid, int skillid, Map<CharacterTemporaryStat, Integer> statups) {
        PacketWriter pw =new PacketWriter();
        pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
        pw.writeLong(CharacterTemporaryStat.RideVehicle.getValue());
        pw.writeLong(0);
        pw.writeLong(0);
        pw.writeLong(0);
        pw.writeLong(0); //v192 //144
        pw.writeInt(0);
        pw.writeInt(10);
        pw.writeInt(10);
        pw.writeInt(skillid); // skillid
        pw.write(HexTool.getByteArrayFromHexString("00 C2 EB 0B"));
        pw.writeInt(10);
        pw.writeInt(skillid); // skillid
        pw.write(HexTool.getByteArrayFromHexString("00 C2 EB 0B"));
        pw.writeInt(0);
        pw.writeInt(0);
        pw.write(0);
        pw.writeInt(buffid); // 1902000 saddle
        pw.writeInt(skillid); // skillid
        pw.write(0);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.write(1);
        pw.writeInt(4);
        pw.write(0);
        return pw.getPacket();
    }
        
      
    public static byte[] showMonsterRiding(int cid, Map<CharacterTemporaryStat, Integer> statups, int buffid, int skillId) {
         PacketWriter pw =new PacketWriter();
        pw.writeShort(SendPacketOpcode.SET_TEMPORARY_STAT_REMOTE.getValue());
        pw.writeInt(cid);
        pw.writeLong(CharacterTemporaryStat.RideVehicle.getValue());
        pw.writeLong(0);
        pw.writeLong(0);
        pw.writeLong(0);
        pw.write(new byte[39]); //v192 4byte. /144
        pw.writeInt(buffid); // 1902000 saddle
        pw.writeInt(skillId); // skillid
        pw.write(new byte[7]);
        return pw.getPacket();
    }

        public static byte[] givePirate(Map<CharacterTemporaryStat, Integer> statups, int duration, int skillid) {
            return giveForeignPirate(statups, duration, -1, skillid);
        }

        public static byte[] giveForeignPirate(Map<CharacterTemporaryStat, Integer> statups, int duration, int cid, int skillid) {
            final boolean infusion = skillid == 5121009 || skillid == 15111005;
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.SET_TEMPORARY_STAT_REMOTE.getValue());
            pw.writeInt(cid);
            PacketHelper.writeBuffMask(pw, statups);
            pw.writeShort(0);
            pw.write(0);
            for (Integer stat : statups.values()) {
                pw.writeInt(stat.intValue());
                pw.writeLong(skillid);
                pw.write(new byte[infusion ? 6 : 1]);
                pw.writeShort(duration);//duration... seconds
            }
            pw.writeShort(0);
            pw.writeShort(0);
            pw.write(1);
            pw.write(1);
            return pw.getPacket();
        }

        public static byte[] giveArcane(int skillid, Map<Integer, Integer> statups) {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            PacketHelper.writeSingleMask(pw, CharacterTemporaryStat.ArcaneAim);
            pw.writeShort(statups.size());
            pw.writeInt(skillid);
            pw.writeInt(5000);
            pw.writeShort(0);
            pw.write(0);
            pw.writeShort(0);
            pw.writeShort(0);
            pw.write(0);
            pw.write(0);
            pw.write(new byte[9]);
            return pw.getPacket();
        }

        public static byte[] giveEnergyChargeTest(int bar, int bufflength) {
            return giveEnergyChargeTest(-1, bar, bufflength);
        }

        public static byte[] giveEnergyChargeTest(int cid, int bar, int bufflength) {
            if (true) {
                return CWvsContext.enableActions();
            }
            PacketWriter pw = new PacketWriter();

            if (cid == -1) {
                pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            } else {
                pw.writeShort(SendPacketOpcode.SET_TEMPORARY_STAT_REMOTE.getValue());
                pw.writeInt(cid);
            }
            PacketHelper.writeSingleMask(pw, CharacterTemporaryStat.EnergyCharged);
            pw.writeShort(0);
            pw.write(0);
            pw.writeInt(Math.min(bar, 10000));
            pw.writeLong(0L);
            pw.write(0);

            pw.writeInt(bar >= 10000 ? bufflength : 0);
            pw.write(0);
            pw.write(6);
            return pw.getPacket();
        }
        
        public static byte[] giveDice(int buffid, int bufflength, int roll) {
        	EnumMap<CharacterTemporaryStat, Integer> statups = new EnumMap<CharacterTemporaryStat, Integer>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.Dice, roll);
            
            int[] dice = new int[22];
    		int num = ((roll & 1) > 0 ? 5 : 15);
    		
    		if (roll == 2) {
    			dice[8] = num << 1;
    		} else if (roll == 3) {
    			dice[0] = num << 2;
    			dice[1] = num << 2;
    		} else if (roll == 4) {
    			dice[2] = num << 0;
        	} else if (roll == 5) {
    			dice[12] = num << 2;
		    } else if (roll == 6) {
    			dice[17] = num << 1;
		    }
        	
        	return giveBuff(buffid, bufflength, statups, null, dice);
        }
        
        
        /**
         * This handles the morph gauge for the kaiser class. Using basic attacks will fill the morph gauge.
         * @param effect
         * @param amount
         * @return
         */
        public static byte[] giveBuff(MapleStatEffect effect, int amount) {
        	EnumMap<CharacterTemporaryStat, Integer> statups = new EnumMap<CharacterTemporaryStat, Integer>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.SmashStack, amount);
            
        	return giveBuff(0, 0, statups, effect, null);
        }
        
        public static byte[] giveBuff(int buffid, int bufflength, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect) {
        	return giveBuff(buffid, bufflength, statups, effect, null);
        }
        
        public static byte[] giveBuff(int buffid, int bufflength, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect, int[] dice) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            
            // SecondaryStat::DecodeForLocal
            PacketHelper.writeBuffMask(pw, statups);

            for (Map.Entry<CharacterTemporaryStat, Integer> stat : statups.entrySet()) {
            	
            	if (stat.getKey().isIndie()) continue;
                    
                if (stat.getKey().isEnDecode4Byte()) {
                    pw.writeInt(stat.getValue());
                } else {
                    pw.writeShort(stat.getValue());
                }
                
                pw.writeInt(buffid);
                pw.writeInt(bufflength);
                
                if (stat.getKey() == CharacterTemporaryStat.SoulMP) {
                	pw.writeInt(effect.getX()); // xSoulMP
                	pw.writeInt(0); // rSoulMP
                }
                
                if (stat.getKey() == CharacterTemporaryStat.FullSoulMP) {
                	pw.writeInt(effect.getX()); // xFullSoulMP
                }
            }

            pw.writeShort(0); // the size of the following structure.
            // pw.writeInt(0); // key
            // pw.write(0); // bEnable
            
            pw.write(0); // nDefenseAtt
            pw.write(0); // nDefenseState
            pw.write(0); // nPVPDamage
            
            for (Map.Entry<CharacterTemporaryStat, Integer> stat : statups.entrySet()) {
                
            	if (stat.getKey() == CharacterTemporaryStat.Dice) {
            		for(int i = 0; i < dice.length; i++) {
            			pw.writeInt(dice[i]);
            		}
                }
            	
            	if (stat.getKey() == CharacterTemporaryStat.KillingPoint) {
            		pw.write(0); // nKillingPoint
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.PinkbeanRollingGrade) {
            		pw.write(0); // nPinkbeanRollingGrade
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.Judgement) {
            		pw.writeInt(effect.getX()); // xJudgement
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.StackBuff) {
            		pw.write(0); // mStackBuff
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.Trinity) {
            		pw.write(0); // mTrinity
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.ElementalCharge) {
            		pw.write(0); // mElementalCharge
            		pw.writeShort(0); // wElementalCharge
            		pw.write(0); // uElementalCharge
            		pw.write(0); // zElementalCharge
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.LifeTidal) {
            		pw.writeInt(0); // mLifeTidal
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.AntiMagicShell) {
            		pw.write(0); // bAntiMagicShell
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.Larkness) {
            		
            		for(int i = 0; i < 2; i++) {
            			
            			// LarknessInfo::Decode
            			pw.writeInt(0);
            			pw.writeInt(0);
            			
            		}
            		
            		pw.writeInt(0); // dgLarkness
            		pw.writeInt(0); // lgLarkness
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.IgnoreTargetDEF) {
            		pw.writeInt(0); // mIgnoreTargetDEF
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.StopForceAtomInfo) {
            		
            		// StopForceAtom::Decode
            		pw.writeInt(0); // nIdx
            		pw.writeInt(0); // nCount
            		pw.writeInt(0); // nWeaponID
            		
            		pw.writeInt(0); // the size of the structure below.
            		// pw.writeInt(0);
            		
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.SmashStack) {
            		pw.writeInt(effect.getX()); // xSmashStack
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.MobZoneState) {
            		pw.writeInt(0); // ?
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.Slow) {
            		pw.write(0); // bSlowIgnoreMoveSkill
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.IceAura) {
            		pw.write(0); // bIceAura
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.KnightsAura) {
            		pw.write(0); // bKnightsAura
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.IgnoreMobpdpR) {
            		pw.write(0); // bIgnoreMobpdpR
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.BDR) {
            		pw.write(0); // bBdR
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.DropRIncrease) {
            		pw.writeInt(effect.getX()); // xDropRIncrease
            		pw.write(0); // bDropRIncrease
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.PoseType) {
            		pw.write(0); // bPoseType
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.Beholder) {
            		pw.writeInt(0); // sBeholder
            		pw.writeInt(0); // ssBeholder
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.CrossOverChain) {
            		pw.writeInt(effect.getX()); // xCrossOverChain
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.Reincarnation) {
            		pw.writeInt(effect.getX()); // xReincarnation
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.ExtremeArchery) {
            		pw.writeInt(0); // bExtremeArchery
            		pw.writeInt(0); // xExtremeArchery
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.QuiverCatridge) {
            		pw.writeInt(effect.getX()); // xQuiverCatridge
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.ImmuneBarrier) {
            		pw.writeInt(effect.getX()); // xImmuneBarrier
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.ZeroAuraStr) {
            		pw.write(0); // bZeroAuraStr
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.ZeroAuraSpd) {
            		pw.write(0); // bZeroAuraSpd
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.ArmorPiercing) {
            		pw.writeInt(0); // bArmorPiercing
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.SharpEyes) {
            		pw.writeInt(0); // mSharpEyes
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.AdvancedBless) {
            		pw.writeInt(effect.getX()); // xAdvancedBless
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.DotHealHPPerSecond) {
            		pw.writeInt(effect.getX()); // xDotHealHPPerSecond 
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.SpiritGuard) {
            		pw.writeInt(0); // nSpiritGuard
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.KnockBack) {
            		pw.writeInt(0); // nKnockBack
            		pw.writeInt(0); // bKnockBack
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.ShieldAttack) {
            		pw.writeInt(effect.getX()); // xShieldAttack
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.SSFShootingAttack) {
            		pw.writeInt(0); // xSSFShootingAttack
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.BMageAura) {
            		pw.writeInt(0); // xBMageAura
            		pw.write(0); // bBMageAura
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.BattlePvP_Helena_Mark) {
            		pw.writeInt(0); // cBattlePvP_Helena_Mark
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.PinkbeanAttackBuff) {
            		pw.writeInt(0); // bPinkbeanAttackBuff
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.RoyalGuardState) {
            		pw.writeInt(0); // bRoyalGuardState
            		pw.writeInt(0); // xRoyalGuardState
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.MichaelSoulLink) {
            		pw.writeInt(0); // xMichaelSoulLink
            		pw.writeInt(0); // bMichaelSoulLink
            		pw.writeInt(0); // cMichaelSoulLink
            		pw.writeInt(0); // yMichaelSoulLink
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.AdrenalinBoost) {
            		pw.write(0); // cAdrenalinBoost
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.RWCylinder) {
            		pw.write(0); // bRWCylinder
            		pw.writeShort(0); // cRWCylinder
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.RWMagnumBlow) {
            		pw.writeShort(0); // bRWMagnumBlow
            		pw.write(effect.getX()); // xRWMagnumBlow
            	}
            }
            
            pw.writeInt(0); // nViperEnergyCharge

            for (Map.Entry<CharacterTemporaryStat, Integer> stat : statups.entrySet()) {
            	
            	if (stat.getKey() == CharacterTemporaryStat.BladeStance) {
            		pw.writeInt(effect.getX()); // xBladeStance
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.DarkSight) {
            		pw.writeInt(0); // cDarkSight
            	}
            	
            	if (stat.getKey() == CharacterTemporaryStat.Stigma) {
            		pw.writeInt(0); // bStigma
            	}
            }

            /**
             * TODO: Some missing information here. So this needs
             * to be looked at in the future when more time is available. :(
             */
            for(int i = 0; i < 8; i++) {
            	CharacterTemporaryStat stat = CharacterTemporaryStat.getCTSFromTSIndex(i);
            	
            	if (stat == null)
            		continue;

            	if (statups.containsKey(stat)) {
            		pw.writeInt(0); // mValue
            		pw.writeInt(0); // mReason
            		pw.write(0);
            		pw.writeInt(0);
            	}
            }
            
            
            /**
             * @method SecondaryStat::DecodeIndieTempStat
             * 
             * @nReason The skillid associated with the buff stat.
             * @nValue
             * @nKey Some sort of tick validation.
             * @nDuration The amount of time elapsed from the initial cast.
             * @tTerm The total static duration of the buff.
             */
            Map<CharacterTemporaryStat, Integer> stats = statups.entrySet().stream()
            		.filter(stat -> stat.getKey().isIndie())
            		.collect(Collectors.toMap(stat -> stat.getKey(), stat -> stat.getValue()));
            
            for(Map.Entry<CharacterTemporaryStat, Integer> stat : stats.entrySet()) {
            	pw.writeInt(1); // the size of the array.
            	pw.writeInt(buffid); // nReason
            	pw.writeInt(effect.getLevel()); // nValue
            	pw.writeInt(Integer.MAX_VALUE); // nKey
            	pw.writeInt(1); // tCur - nDuration
            	pw.writeInt(bufflength); // tTerm
            	
            	pw.writeInt(0); // size
            	// pw.writeInt(0); // nMValueKey
            	// pw.writeInt(0); // nMValue
            }
            
            
            if(statups.containsKey(CharacterTemporaryStat.UsingScouter))
            	pw.writeInt(0); // nUsingScouter
            
            pw.writeShort(1);
            pw.write(0);
            pw.write(0); // bJustBuffCheck
            pw.write(0); // bFirstSet
            
            pw.writeInt(0); // 174.1
            
            boolean isMovementAffectingStat = statups.entrySet().stream()
            		.anyMatch(stat -> stat.getKey().isMovementAffectingStat());
            
            if (isMovementAffectingStat)
            	pw.write(0);
            
            System.err.printf("SkillID: (%s)  Packet: %s%n", buffid, pw.toString());
            return pw.getPacket();
        }
        
        public static byte[] giveDebuff(MapleDisease statups, int x, int skillid, int level, int duration) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            PacketHelper.writeSingleMask(pw, statups);
            pw.writeShort(x);
            pw.writeShort(skillid);
            pw.writeShort(level);
            pw.writeInt(duration);
            pw.writeShort(0);
            pw.writeShort(0);
            //pw.write(1);
            pw.write(0);
            //pw.write(1);
            pw.write(new byte[30]);
            //System.out.println(HexTool.toString(pw.getPacket()));
            return pw.getPacket();
        }

        public static byte[] cancelBuff(List<CharacterTemporaryStat> statups) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_RESET.getValue());

            PacketHelper.writeMask(pw, statups);

            statups.stream()
            	.filter(stat -> stat.isIndie())
            	.collect(Collectors.toList())
            	.forEach(stat -> pw.writeInt(0));

            pw.writeShort(0);
            pw.write(0);
            pw.write(0);
            pw.write(0);

            return pw.getPacket();
        }

        public static byte[] cancelDebuff(MapleDisease mask) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_RESET.getValue());

            PacketHelper.writeSingleMask(pw, mask);
            pw.write(3);
            pw.write(1);
            pw.writeLong(0);
            pw.write(0);//v112
            return pw.getPacket();
        }

        public static byte[] cancelHoming() {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_RESET.getValue());

            PacketHelper.writeSingleMask(pw, CharacterTemporaryStat.StopForceAtomInfo);
            pw.write(0);//v112

            return pw.getPacket();
        }
        
        public static byte[] giveAriaBuff(Map<CharacterTemporaryStat, Integer> statups, int bufflevel, int buffid, int bufflength) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            PacketHelper.writeBuffMask(pw, statups);
            // pw.write(HexTool.getByteArrayFromHexString("00 00 00 00 00 00 00 80 00 00 00 00 00 00 00 00 00 00 40 00 00 00 00 00 00 00 00 00 00 00 00 00"));         
            for (Map.Entry stat : statups.entrySet()) {
                pw.writeShort(((Integer) stat.getValue()).intValue());
                pw.writeInt(buffid);
                pw.writeInt(bufflength);
            }
            pw.write(new byte[3]);
            pw.writeShort(0); // not sure..
            pw.write(0);
            pw.writeShort(0);
            return pw.getPacket();
        }
        
     public static byte[] giveForeignBuff(int cid, Map<CharacterTemporaryStat, Integer> statups, MapleStatEffect effect) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.SET_TEMPORARY_STAT_REMOTE.getValue());
        pw.writeInt(cid);
        PacketHelper.writeBuffMask(pw, (Map) statups);
            for (Entry<CharacterTemporaryStat, Integer> statup : statups.entrySet()) {
                if (statup.getKey() == CharacterTemporaryStat.ShadowPartner || statup.getKey() == CharacterTemporaryStat.Mechanic || statup.getKey() == CharacterTemporaryStat.BMageAura || statup.getKey() == CharacterTemporaryStat.BMageAura || statup.getKey() == CharacterTemporaryStat.BMageAura || statup.getKey() == CharacterTemporaryStat.Inflation || statup.getKey() == CharacterTemporaryStat.SpiritLink || statup.getKey() == CharacterTemporaryStat.PYRAMID_PQ || statup.getKey() == CharacterTemporaryStat.WeaponCharge || statup.getKey() == CharacterTemporaryStat.DamR || statup.getKey() == CharacterTemporaryStat.Morph || statup.getKey() == CharacterTemporaryStat.DamAbsorbShield || statup.getKey() == CharacterTemporaryStat.DARK_METAMORPHOSIS) {
                    pw.writeShort(statup.getValue().shortValue());
                    pw.writeInt(effect.isSkill() ? effect.getSourceId() : -effect.getSourceId());
                } else if (statup.getKey() == CharacterTemporaryStat.FAMILIAR_SHADOW) {
                    pw.writeInt(statup.getValue());
                    pw.writeInt(effect.getCharColor());
                } else {
                    pw.writeShort(statup.getValue().shortValue());
                }
            }
        pw.writeShort(1);//was 0, ourstory does 1?
        pw.write(0);
        if (effect.getSourceId() == 13101024) {
            pw.writeLong(0);
            pw.writeLong(0);
            pw.writeLong(0);
            pw.write(new byte[6]);
            pw.write(1);
            pw.write(new byte[22]);
        } else if (effect.getSourceId() == 4001003 || effect.getSourceId() == 23111005) { 
            pw.writeLong(0);
            pw.writeLong(0);
            pw.write(new byte[3]);
        } else if (/*effect.getSourceId() == 1101013 || effect.getSourceId() == 1120003 ||*/ effect.getSourceId() == 11111001 || effect.getSourceId() == 11110005) { //ì½¤ë³´ ì–´íƒ�
            pw.writeLong(1);
            pw.writeLong(0);
            pw.write(new byte[4]);
         } else if (effect.getSourceId() == 15001004) {
            pw.writeInt(0);
            pw.write(0);
            pw.writeShort(23);
            pw.writeShort(20);
            pw.write(0);
            pw.write(HexTool.getByteArrayFromHexString("AB E5 E4 00"));
            pw.writeInt(0);
            pw.write(0);
            pw.writeShort(23);
            pw.writeShort(0);
        } else if (effect.getSourceId() == 61120008 || effect.getSourceId() == 61111008 || effect.getSourceId() == 61121053) {// KAISER BUFFS!
           /* pw.writeLong(0); // old kaiser
            pw.writeLong(0);
            pw.writeZeroBytes(5);*/
            pw.writeInt(2);
            pw.write(new byte[13]);
            pw.writeShort(600);
            pw.write(new byte[20]);
        } else if (effect.getSourceId() == 21101006) {
            pw.writeShort(0);
            pw.write(7);
            pw.writeLong(0);
            pw.writeLong(0);
            pw.write(208);
            pw.write(2);
        }/* else if (effect.getSourceId() == 3101004 || effect.getSourceId() == 3201004 || effect.getSourceId() == 13101003 || effect.getSourceId() == 33101003) {
            pw.writeLong(0);
            pw.writeLong(0);
        }*/ else if (effect.getSourceId() == 30001001 || effect.getSourceId() == 30011001 || effect.getSourceId() == 2311009) {
            pw.writeLong(0);
            pw.writeLong(0);
            pw.write(0);
        } else if (effect.getSourceId() == 1221004 || effect.getSourceId() == 1211006 || effect.getSourceId() == 1211008 || effect.getSourceId() == 1211004) {
            pw.writeShort(0);
            pw.writeLong(4);
            pw.writeLong(0);
            pw.write(0);
            pw.writeShort(602);
        } else if (effect.getSourceId() == 32120000 || effect.getSourceId() == 32001003 || effect.getSourceId() == 32110000 || effect.getSourceId() == 32111012 || effect.getSourceId() == 32120001 || effect.getSourceId() == 32101003) { //ì˜¤ë�¼
            pw.writeLong(0);
            pw.writeLong(0);
            pw.write(new byte[5]);
        } else {
            pw.writeLong(0);
            pw.writeLong(0);
            pw.write(new byte[6]);
        }
        System.out.println("Sent foreign Efftect: "+effect.getSourceId()+" as packet: "+pw.toString());
        return pw.getPacket();
    }

        public static byte[] giveForeignDebuff(int cid, final MapleDisease statups, int skillid, int level, int x) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.SET_TEMPORARY_STAT_REMOTE.getValue());
            pw.writeInt(cid);

            PacketHelper.writeSingleMask(pw, statups);
            if (skillid == 125) {
                pw.writeShort(0);
                pw.write(0); //todo test
            }
            pw.writeShort(x);
            pw.writeShort(skillid);
            pw.writeShort(level);
            pw.writeShort(0); // same as give_buff
            pw.writeShort(0); //Delay
            pw.write(1);
            pw.write(1);
            pw.write(0);//v112
            pw.write(new byte[20]);
            return pw.getPacket();
        }

        public static byte[] cancelForeignBuff(int cid, List<CharacterTemporaryStat> statups) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.RESET_TEMPORARY_STAT_REMOTE.getValue());
            pw.writeInt(cid);
            PacketHelper.writeMask(pw, statups);
            pw.write(3);
            pw.write(1);
            pw.write(0);
            pw.write(new byte[20]);

            return pw.getPacket();
        }
        
        public static byte[] cancelForeignRiding(int cid, List<CharacterTemporaryStat> statups) {
        PacketWriter pw =new PacketWriter();
        pw.writeShort(SendPacketOpcode.RESET_TEMPORARY_STAT_REMOTE.getValue());
        pw.writeInt(cid);
        pw.writeLong(CharacterTemporaryStat.RideVehicle.getValue());
        pw.writeLong(0);
        pw.writeLong(0);
        pw.writeLong(0);
        pw.writeLong(0); // v181
        pw.write(1);
        return pw.getPacket();
    }

        public static byte[] cancelForeignDebuff(int cid, MapleDisease mask) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.RESET_TEMPORARY_STAT_REMOTE.getValue());
            pw.writeInt(cid);

            PacketHelper.writeSingleMask(pw, mask);//48 bytes
            //pw.write(3);
            pw.write(1);
            //pw.write(0);//v112
            return pw.getPacket();
        }

        public static byte[] giveCard(int cid, int oid, int skillid) {
            PacketWriter writer = new PacketWriter();
            writer.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
            writer.write(0);
            writer.writeInt(cid);
            writer.writeInt(1);
            writer.writeInt(oid);
            writer.writeInt(skillid);
            writer.write(1);
            writer.writeInt(2);
            writer.writeInt(1);
            writer.writeInt(21);
            writer.writeInt(8);
            writer.writeInt(8);
            writer.write(0);
            return writer.getPacket();
        }
    }

    public static class InventoryPacket {

        public static byte[] addInventorySlot(MapleInventoryType type, Item item) {
            return addInventorySlot(type, item, false);
        }

        public static byte[] addInventorySlot(MapleInventoryType type, Item item, boolean fromDrop) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(fromDrop ? 1 : 0);
            pw.write(1);
            pw.write(0);

            pw.write(GameConstants.isInBag(item.getPosition(), type.getType()) ? 9 : 0);
            pw.write(type.getType());
            pw.writeShort(item.getPosition());
            PacketHelper.addItemInfo(pw, item);
            pw.write(0); // new 178?
            return pw.getPacket();
        }

        public static byte[] updateInventorySlot(MapleInventoryType type, Item item, boolean fromDrop) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(fromDrop ? 1 : 0);
            pw.write(1);
            pw.write(0);

            pw.write(GameConstants.isInBag(item.getPosition(), type.getType()) ? 6 : 1);
            pw.write(type.getType());
            pw.writeShort(item.getPosition());
            pw.writeShort(item.getQuantity());
            pw.write(0); // new 178?

            return pw.getPacket();
        }

        public static byte[] moveInventoryItem(MapleInventoryType type, short src, short dst, boolean bag, boolean bothBag) {
            return moveInventoryItem(type, src, dst, (byte) -1, bag, bothBag);
        }

        public static byte[] moveInventoryItem(MapleInventoryType type, short src, short dst, short equipIndicator, boolean bag, boolean bothBag) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(1);
            pw.write(1);
            pw.write(0);

            pw.write(bag ? 5 : bothBag ? 8 : 2);
            pw.write(type.getType());
            pw.writeShort(src);
            pw.writeShort(dst);
            if (bag) {
                pw.writeShort(0);
            }
            if (equipIndicator != -1) {
//                pw.write(equipIndicator);
                pw.write(0);
            }
            boolean unequip = src < 0 && dst > 0;
            boolean equip = src > 0 && dst < 0;
            pw.write(unequip ? 0x0D : equip ? 0x0E : 0); // new 178?

            return pw.getPacket();
        }

        public static byte[] moveAndMergeInventoryItem(MapleInventoryType type, short src, short dst, short total, boolean bag, boolean switchSrcDst, boolean bothBag) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(1);
            pw.write(2);
            pw.write(0);

            pw.write((bag) && ((switchSrcDst) || (bothBag)) ? 7 : 3);
            pw.write(type.getType());
            pw.writeShort(src);

            pw.write((bag) && ((!switchSrcDst) || (bothBag)) ? 6 : 1);
            pw.write(type.getType());
            pw.writeShort(dst);
            pw.writeShort(total);
            pw.write(new byte[30]); // new 178?

            return pw.getPacket();
        }

        public static byte[] moveAndMergeWithRestInventoryItem(MapleInventoryType type, short src, short dst, short srcQ, short dstQ, boolean bag, boolean switchSrcDst, boolean bothBag) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(1);
            pw.write(2);
            pw.write(0);

            pw.write((bag) && ((switchSrcDst) || (bothBag)) ? 6 : 1);
            pw.write(type.getType());
            pw.writeShort(src);
            pw.writeShort(srcQ);

            pw.write((bag) && ((!switchSrcDst) || (bothBag)) ? 6 : 1);
            pw.write(type.getType());
            pw.writeShort(dst);
            pw.writeShort(dstQ);
            pw.write(0); // new 178?

            return pw.getPacket();
        }

        public static byte[] clearInventoryItem(MapleInventoryType type, short slot, boolean fromDrop) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(fromDrop ? 1 : 0);
            pw.write(1);
            pw.write(0);

            pw.write((slot > 100) && (type == MapleInventoryType.ETC) ? 7 : 3);
            pw.write(type.getType());
            pw.writeShort(slot);
            pw.write(0); // new 178?

            return pw.getPacket();
        }

        public static byte[] updateSpecialItemUse(Item item, byte invType, MapleCharacter chr) {
            return updateSpecialItemUse(item, invType, item.getPosition(), false, chr);
        }

        public static byte[] updateSpecialItemUse(Item item, byte invType, short pos, boolean theShort, MapleCharacter chr) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(0);
            pw.write(2);
            pw.write(0);

            pw.write(GameConstants.isInBag(pos, invType) ? 7 : 3);
            pw.write(invType);
            pw.writeShort(pos);

            pw.write(0);
            pw.write(invType);
            if ((item.getType() == 1) || (theShort)) {
                pw.writeShort(pos);
            } else {
                pw.write(pos);
            }
            PacketHelper.addItemInfo(pw, item, chr);
            if (pos < 0) {
                pw.write(2);
            }
            pw.write(0); // new 178?

            return pw.getPacket();
        }

        public static byte[] updateSpecialItemUse_(Item item, byte invType, MapleCharacter chr) {
            return updateSpecialItemUse_(item, invType, item.getPosition(), chr);
        }

        public static byte[] updateSpecialItemUse_(Item item, byte invType, short pos, MapleCharacter chr) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(0);
            pw.write(1);
            pw.write(0);

            pw.write(0);
            pw.write(invType);
            if (item.getType() == 1) {
                pw.writeShort(pos);
            } else {
                pw.write(pos);
            }
            PacketHelper.addItemInfo(pw, item, chr);
            if (pos < 0) {
                pw.write(1);
            }
            pw.write(0); // new 178?

            return pw.getPacket();
        }

        public static byte[] updateEquippedItem(MapleCharacter chr, Equip eq, short pos) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(0);
            pw.write(1);
            pw.write(0);

            pw.write(0);
            pw.write(1);
            pw.writeShort(pos);
            PacketHelper.addItemInfo(pw, eq, chr);
            pw.write(0); // new 178?

            return pw.getPacket();
        }

        public static byte[] scrolledItem(Item scroll, MapleInventoryType inv, Item item, boolean destroyed, boolean potential, boolean equipped) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(1);
            pw.write(destroyed ? 2 : 3);
            pw.write(0);

            pw.write(scroll.getQuantity() > 0 ? 1 : 3);
            pw.write(GameConstants.getInventoryType(scroll.getItemId()).getType());
            pw.writeShort(scroll.getPosition());
            if (scroll.getQuantity() > 0) {
                pw.writeShort(scroll.getQuantity());
            }

            pw.write(3);
            pw.write(inv.getType());
            pw.writeShort(item.getPosition());
            if (!destroyed) {
                pw.write(0);
                pw.write(inv.getType());
                pw.writeShort(item.getPosition());
                PacketHelper.addItemInfo(pw, item);
            }
            if (!potential) {
                pw.write(1);
            }
            if (equipped) {
                pw.write(8);
            }
            pw.write(0); // new 178?

            return pw.getPacket();
        }

        public static byte[] moveAndUpgradeItem(MapleInventoryType type, Item item, short oldpos, short newpos, MapleCharacter chr) {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(1);
            pw.write(3);
            pw.write(0);

            pw.write(GameConstants.isInBag(newpos, type.getType()) ? 7 : 3);
            pw.write(type.getType());
            pw.writeShort(oldpos);

            pw.write(0);
            pw.write(1);
            pw.writeShort(oldpos);
            PacketHelper.addItemInfo(pw, item, chr);

            pw.write(2);
            pw.write(type.getType());
            pw.writeShort(oldpos);
            pw.writeShort(newpos);
            pw.write(0);
            pw.write(0); // new 178?

            return pw.getPacket();
        }

        public static byte[] dropInventoryItem(MapleInventoryType type, short src) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(1);
            pw.write(1);
            pw.write(0);

            pw.write(3);
            pw.write(type.getType());
            pw.writeShort(src);
            if (src < 0) {
                pw.write(1);
            }
            pw.write(0); // new 178?

            return pw.getPacket();
        }

        public static byte[] dropInventoryItemUpdate(MapleInventoryType type, Item item) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(1);
            pw.write(1);
            pw.write(0);

            pw.write(1);
            pw.write(type.getType());
            pw.writeShort(item.getPosition());
            pw.writeShort(item.getQuantity());
            pw.write(0); // new 178?

            return pw.getPacket();
        }

        public static byte[] getInventoryFull() {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(1);
            pw.write(0);
            pw.write(0);
            pw.write(0); // new 178?

            return pw.getPacket();
        }

        public static byte[] getInventoryStatus() {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
            pw.write(0);
            pw.write(0);
            pw.write(0);
            pw.write(0); // new 178?

            return pw.getPacket();
        }

        public static byte[] getSlotUpdate(byte invType, byte newSlots) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INVENTORY_GROW.getValue());
            pw.write(invType);
            pw.write(newSlots);

            return pw.getPacket();
        }

        public static byte[] getShowInventoryFull() {
            return CWvsContext.InfoPacket.getShowInventoryStatus(255);
        }

        public static byte[] showItemUnavailable() {
            return CWvsContext.InfoPacket.getShowInventoryStatus(254);
        }
    }

    public static byte[] updateHyperSp(int mode, int remainSp) {
        return updateSpecialStat("hyper", 0x1C, mode, remainSp);
    }

    public static byte[] updateSpecialStat(String stat, int array, int mode, int amount) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.RESULT_INSTANCE_TABLE.getValue());
        pw.writeMapleAsciiString(stat);
        pw.writeInt(array);
        pw.writeInt(mode);
        pw.write(1);
        pw.writeInt(amount);

        return pw.getPacket();
    }

    public static byte[] updateMaplePoint(int mp) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SET_MAPLE_POINT.getValue());
        pw.writeInt(mp);

        return pw.getPacket();
    }

    public static byte[] updateCrowns(int[] titles) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.REQUEST_EVENT_LIST.getValue());
        for (int i = 0; i < 5; i++) {
            pw.writeMapleAsciiString("");
            if (titles.length < i + 1) {
                pw.write(-1);
            } else {
     //           pw.write(titles[i]);
            }
        }

        return pw.getPacket();
    }

    public static byte[] magicWheel(int type, List<Integer> items, String data, int endSlot) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MAGIC_WHEEL.getValue());
        pw.write(type);
        switch (type) {
            case 3:
                pw.write(items.size());
                for (int item : items) {
                    pw.writeInt(item);
                }
                pw.writeMapleAsciiString(data); // nexon encrypt the item and then send the string
                pw.write(endSlot);
                break;
            case 5:
                //<Character Name> got <Item Name>.
                break;
            case 6:
                //You don't have a Magic Gachapon Wheel in your Inventory.
                break;
            case 7:
                //You don't have any Inventory Space.\r\n You must have 2 or more slots available\r\n in each of your tabs.
                break;
            case 8:
                //Please try this again later.
                break;
            case 9:
                //Failed to delete Magic Gachapon Wheel item.
                break;
            case 0xA:
                //Failed to receive Magic Gachapon Wheel item.
                break;
            case 0xB:
                //You cannot move while Magic Wheel window is open.
                break;
        }

        return pw.getPacket();
    }

    public static class Reward {

        public static byte[] receiveReward(int id, byte mode, int quantity) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.REWARD.getValue());
            pw.write(mode); // mode
            switch (mode) { // mode
                case 9:
                    pw.writeInt(0);
                    break;
                case 0x0B:
                    pw.writeInt(id);
                    pw.writeInt(quantity); //quantity
                    //Popup: You have received the Maple Points.\r\n( %d maple point )
                    break;
                case 0x0C:
                    pw.writeInt(id);
                    //Popup You have received the Game item.
                    break;
                case 0x0E:
                    pw.writeInt(id);
                    pw.writeInt(quantity); //quantity
                    //Popup: You have received the Mesos.\r\n( %d meso )
                    break;
                case 0x0F:
                    pw.writeInt(id);
                    pw.writeInt(quantity); //quantity
                    //Popup: You have received the Exp.\r\n( %d exp )
                    break;
                case 0x14:
                    //Popup: Failed to receive the Maple Points.
                    break;
                case 0x15:
                    pw.write(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x16:
                    pw.write(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x17:
                    //Popup: Failed to receive the Mesos.
                    break;
                case 0x18:
                    //Popup: Failed to receive the Exp.
                    break;
                case 0x21:
                    pw.write(0); //66
                    //No inventory space
                    break;
            }

            return pw.getPacket();
        }

        public static byte[] updateReward(int id, byte mode, List<MapleReward> rewards, int option) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.REWARD.getValue());
            pw.write(mode); // mode
            switch (mode) { // mode
                case 9:
                    pw.writeInt(rewards.size());
                    if (rewards.size() > 0) {
                        for (int i = 0; i < rewards.size(); i++) {
                            MapleReward reward = rewards.get(i);
                            boolean empty = reward.getId() < 1;
                            pw.writeInt(empty ? 0 : reward.getId()); // 0 = blank 1+ = gift
                            if (!empty) {
                                if ((option & 1) != 0) {
                                    pw.writeLong(reward.getReceiveDate()); //start time
                                    pw.writeLong(reward.getExpireDate()); //end time
                                    pw.writeLong(reward.getReceiveDate()); //start time
                                    pw.writeLong(reward.getExpireDate()); //end time
                                }
                                if ((option & 2) != 0) { //nexon do here a3 & 2 when a3 is 9
                                    pw.writeInt(0);
                                    pw.writeInt(0);
                                    pw.writeInt(0);
                                    pw.writeInt(0);
                                    pw.writeInt(0);
                                    pw.writeInt(0);
                                    pw.writeMapleAsciiString("");
                                    pw.writeMapleAsciiString("");
                                    pw.writeMapleAsciiString("");
                                }
                                pw.writeInt(reward.getType()); //type 3 = maple point 4 = mesos 5 = exp
                                pw.writeInt(reward.getItem()); // item id
                                pw.writeInt(/*itemQ*/reward.getItem() > 0 ? 1 : 0); // item quantity (?)
                                pw.writeInt(0);
                                pw.writeLong(0L);
                                pw.writeInt(0);
                                pw.writeInt(reward.getMaplePoints()); // maple point amount
                                pw.writeInt(reward.getMeso()); // mesos amount
                                pw.writeInt(reward.getExp()); // exp amount
                                pw.writeInt(0);
                                pw.writeInt(0);
                                pw.writeMapleAsciiString("");
                                pw.writeMapleAsciiString("");
                                pw.writeMapleAsciiString("");
                                pw.writeMapleAsciiString(reward.getDesc());
                            }
                        }
                    }
                    break;
                case 0x0B:
                    pw.writeInt(id);
                    pw.writeInt(0); //quantity
                    //Popup: You have received the Maple Points.\r\n( %d maple point )
                    break;
                case 0x0C:
                    pw.writeInt(id);
                    //Popup You have received the Game item.
                    break;
                case 0x0E:
                    pw.writeInt(id);
                    pw.writeInt(0); //quantity
                    //Popup: You have received the Mesos.\r\n( %d meso )
                    break;
                case 0x0F:
                    pw.writeInt(id);
                    pw.writeInt(0); //quantity
                    //Popup: You have received the Exp.\r\n( %d exp )
                    break;
                case 0x14:
                    //Popup: Failed to receive the Maple Points.
                    break;
                case 0x15:
                    pw.write(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x16:
                    pw.write(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x17:
                    //Popup: Failed to receive the Mesos.
                    break;
                case 0x18:
                    //Popup: Failed to receive the Exp.
                    break;
                case 0x21:
                    pw.write(0); //66
                    //No inventory space
                    break;
            }

            return pw.getPacket();
        }
    }

    public static byte[] updateVMatrix(List<VMatrixRecord> matrixRecords){
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.VMATRIX_UPDATE.getValue());
        pw.writeInt(matrixRecords.size());
        for(VMatrixRecord vmr : matrixRecords){
            vmr.encode(pw);
        }
        pw.write(0);
        return pw.getPacket();
    }

    public static byte[] nodestoneResult(int itemID, int skillID1, int skillID2, int skillID3){
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.NODESTONE_RESULT.getValue());
        pw.writeInt(itemID);
        pw.writeInt(1);
        pw.writeInt(skillID1);
        pw.writeInt(skillID2);
        pw.writeInt(skillID3);
        return pw.getPacket();
    }
}