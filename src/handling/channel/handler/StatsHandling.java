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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.PlayerStats;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import constants.GameConstants;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class StatsHandling {

    private static final short statLimit = 999;

    public static final void AutoAssignAP(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
    	slea.skip(4); // update tick
        slea.skip(4);
        if (slea.available() < 16L) {
            return;
        }
       // final int count = slea.readInt();
        int PrimaryStat = (int) slea.readLong();
        int amount = slea.readInt();
        int SecondaryStat =(int) slea.readLong();
        int amount2 = slea.readInt();
        if ((amount < 0) || (amount2 < 0)) {
            return;
        }

        PlayerStats playerst = chr.getStat();

        Map statupdate = new EnumMap(MapleStat.class);
        c.getSession().write(CWvsContext.updatePlayerStats(statupdate, true, chr));

        if (chr.getRemainingAp() == amount + amount2) {
            switch (PrimaryStat) {
                case 64:
                    if (playerst.getStr() + amount > 999) {
                        return;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount), chr);
                    statupdate.put(MapleStat.STR, Long.valueOf(playerst.getStr()));
                    break;
                case 128:
                    if (playerst.getDex() + amount > 999) {
                        return;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount), chr);
                    statupdate.put(MapleStat.DEX, Long.valueOf(playerst.getDex()));
                    break;
                case 256:
                    if (playerst.getInt() + amount > 999) {
                        return;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount), chr);
                    statupdate.put(MapleStat.INT, Long.valueOf(playerst.getInt()));
                    break;
                case 512:
                    if (playerst.getLuk() + amount > 999) {
                        return;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount), chr);
                    statupdate.put(MapleStat.LUK, Long.valueOf(playerst.getLuk()));
                    break;
                   case 1024: //Max Hp
                       System.out.println("Reading hp case."); 
                   if (playerst.getMaxHp() + (amount * 30) > 500000) {
                            return;
                        }
                        System.out.println("HP Didn't get added Sorry nigger."); 
                        playerst.setMaxHp((short) (playerst.getMaxHp() + amount * 30), chr);
                        statupdate.put(MapleStat.MAXHP, Long.valueOf(playerst.getMaxHp()));
                        break;
                default:
                    c.getSession().write(CWvsContext.enableActions());
                    return;
            }
            switch (SecondaryStat) {
                case 64:
                    if (playerst.getStr() + amount2 > 999) {
                        return;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount2), chr);
                    statupdate.put(MapleStat.STR, Long.valueOf(playerst.getStr()));
                    break;
                case 128:
                    if (playerst.getDex() + amount2 > 999) {
                        return;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount2), chr);
                    statupdate.put(MapleStat.DEX, Long.valueOf(playerst.getDex()));
                    break;
                case 256:
                    if (playerst.getInt() + amount2 > 999) {
                        return;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount2), chr);
                    statupdate.put(MapleStat.INT, Long.valueOf(playerst.getInt()));
                    break;
                case 512:
                    if (playerst.getLuk() + amount2 > 999) {
                        return;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount2), chr);
                    statupdate.put(MapleStat.LUK, Long.valueOf(playerst.getLuk()));
                    break;
               case 1024: //Max Hp
                   if (playerst.getMaxHp() + (amount2 * 30) > 500000) {
                            return;
                   }
                     System.out.println("HP Didn't get added Sorry nigger.");       
                        playerst.setMaxHp((short) (playerst.getMaxHp() + amount2 * 30), chr);
                        statupdate.put(MapleStat.MAXHP, Long.valueOf(playerst.getMaxHp()));
                        break;
                default:
                    c.getSession().write(CWvsContext.enableActions());
                    return;
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - (amount + amount2)));
            statupdate.put(MapleStat.AVAILABLEAP, Long.valueOf(chr.getRemainingAp()));
            c.getSession().write(CWvsContext.updatePlayerStats(statupdate, true, chr));
        }
    }

    public static void DistributeHyper(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
    	slea.skip(4); // update tick
        int skillid = slea.readInt();
        final Skill skill = SkillFactory.getSkill(skillid);
        final int remainingSp = chr.getRemainingHSp(skill.getHyper() - 1);

        final int maxlevel = 1;
        final int curLevel = chr.getSkillLevel(skill);

        if (skill.isInvisible() && chr.getSkillLevel(skill) == 0) {
            if (maxlevel <= 0) {
                c.getSession().write(CWvsContext.enableActions());
                //AutobanManager.getInstance().addPoints(c, 1000, 0, "Illegal distribution of SP to invisible skills (" + skillid + ")");
                return;
            }
        }

        for (int i : GameConstants.blockedSkills) {
            if (skill.getId() == i) {
                c.getSession().write(CWvsContext.enableActions());
                chr.dropMessage(1, "This skill has been blocked and may not be added.");
                return;
            }
        }

        if ((remainingSp >= 1 && curLevel == 0) && skill.canBeLearnedBy(chr.getJob())) {
            chr.setRemainingHSp(skill.getHyper() - 1, remainingSp - 1);
            chr.changeSingleSkillLevel(skill, (byte) 1, (byte) 1, -1L, true);
        } else {
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static void ResetHyper(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
    	slea.skip(4); // update tick
        short times = slea.readShort();
        if (times < 1 || times > 3) {
            times = 3;
        }
        long price = 10000L * (long) Math.pow(10, times);
        if (chr.getMeso() < price) {
            chr.dropMessage(1, "You do not have enough mesos for that.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        int ssp = 0;
        int spp = 0;
        int sap = 0;
        HashMap<Skill, SkillEntry> sa = new HashMap<>();
        for (Skill skil : SkillFactory.getAllSkills()) {
            if (skil.isHyper()) {
                sa.put(skil, new SkillEntry(0, (byte) 1, -1));
                if (skil.getHyper() == 1) {
                    ssp++;
                } else if (skil.getHyper() == 2) {
                    spp++;
                } else if (skil.getHyper() == 3) {
                    sap++;
                }
            }
        }
        chr.gainMeso(-price, false);
        chr.changeSkillsLevel(sa, true);
        chr.gainHSP(0, ssp);
        chr.gainHSP(1, spp);
        chr.gainHSP(2, sap);
    }
}
