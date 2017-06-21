/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.packet;

import client.CharacterTemporaryStat;
import client.MapleCharacter;
import handling.SendPacketOpcode;
import java.awt.Point;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import server.MapleStatEffect;
import tools.Randomizer;
import tools.data.PacketWriter;
import tools.packet.enums.EffectType;

/**
 *
 * @author Itzik
 */
public class JobPacket {

    public static class WindArcherPacket {

        public static byte[] giveWindArcherBuff(int buffid, int bufflength, Map<CharacterTemporaryStat, Integer> statups,
                MapleStatEffect effect, MapleCharacter chr) {
            PacketWriter pw = new PacketWriter();
            int skillLevel = chr.getSkillLevel(buffid);
            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            PacketHelper.writeBuffMask(pw, statups);
            try {
                byte count = 0;
                StringBuilder statValue = new StringBuilder();
                Map.Entry[] stat = new Map.Entry[statups.size()];
                for (Map.Entry temp : statups.entrySet()) {
                    stat[count] = temp;
                    statValue.append((int) stat[count].getValue()).append(" - ");
                    count++;
                }
                switch (buffid) {
                    case 13001022: // Storm Elemental
                        pw.writeShort((int) stat[0].getValue());
                        pw.writeInt(buffid);
                        pw.writeInt(bufflength);

                        pw.write(new byte[9]);
                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[1].getValue());
                        pw.writeInt(145154873);
                        pw.writeInt(1);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);
                        pw.write(1);
                        pw.writeInt(0);
                        break;
                    case 13101024:// Sylvan Aid
                        pw.writeShort((int) stat[0].getValue());
                        pw.writeInt(buffid);
                        pw.writeInt(bufflength);

                        pw.writeShort((int) stat[1].getValue());
                        pw.writeInt(buffid);
                        pw.writeInt(bufflength);

                        pw.write(new byte[9]);
                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[2].getValue());
                        pw.writeInt(144927281);
                        pw.writeInt(1);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(0);
                        pw.write(1);
                        pw.writeInt(0);
                        break;
                    case 13111023: // Albatross
                        pw.writeShort(skillLevel);
                        pw.writeInt(buffid);
                        pw.writeInt(bufflength);

                        pw.writeInt(0);
                        pw.write(0);
                        pw.writeInt(0);

                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[0].getValue());
                        pw.writeInt(141472705);
                        pw.writeInt(0);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[1].getValue());
                        pw.writeInt(141472705);
                        pw.writeInt(1);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[2].getValue());
                        pw.writeInt(141472705);
                        pw.writeInt(0);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[3].getValue());
                        pw.writeInt(141472705);
                        pw.writeInt(0);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(0);
                        pw.write(1);
                        pw.writeInt(0);
                        break;
                    case 13120008:// Albatross Max
                        System.out.println("size: " + stat.length);
                        pw.writeShort((int) stat[7].getValue());
                        pw.writeInt(buffid);
                        pw.writeInt(bufflength);

                        pw.writeShort(skillLevel);
                        pw.writeInt(buffid);
                        pw.writeInt(bufflength);

                        pw.write(new byte[13]);

                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[0].getValue());
                        pw.writeInt(138530003);
                        pw.writeInt(1);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[1].getValue());
                        pw.writeInt(138530003);
                        pw.writeInt(1);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[2].getValue());
                        pw.writeInt(138530003);
                        pw.writeInt(1);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[3].getValue());
                        pw.writeInt(138530003);
                        pw.writeInt(1);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[4].getValue());
                        pw.writeInt(138530003);
                        pw.writeInt(1);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[5].getValue());
                        pw.writeInt(138530003);
                        pw.writeInt(1);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[6].getValue());
                        pw.writeInt(138530003);
                        pw.writeInt(1);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(0);
                        pw.write(1);
                        pw.writeInt(0);
                        break;
                    case 13121004:// Touch of the Wind
                        //A0 7A 03 00 0C 00 EC 35 C8 00 A0 7A 03 00 00 00 00 00 00 00 00 00 00 01 00 00 00 EC 35 C8 00 14 00 00 00 9D E4 95 08 00 00 00 00 A0 7A 03 00 00 00 00 00 01 00 00 00 EC 35 C8 00 0A 00 00 00 9D E4 95 08 00 00 00 00 A0 7A 03 00 00 00 00 00 00 00 00 00 01 00 00 00 00
                        pw.writeShort(skillLevel);
                        pw.writeInt(buffid);
                        pw.writeInt(bufflength);

                        pw.writeShort((int) stat[0].getValue());
                        pw.writeInt(buffid);
                        pw.writeInt(bufflength);

                        pw.writeShort((int) stat[1].getValue());
                        pw.writeInt(buffid);
                        pw.writeInt(bufflength);

                        pw.write(new byte[9]);
                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[2].getValue());
                        pw.writeInt(143868227);
                        pw.writeInt(1);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(1);
                        pw.writeInt(buffid);
                        pw.writeInt((int) stat[3].getValue());
                        pw.writeInt(143868227);
                        pw.writeInt(1);
                        pw.writeInt(bufflength);
                        pw.writeInt(0);

                        pw.writeInt(0);
                        pw.write(1);
                        pw.writeInt(0);
                        break;
                }
            } catch (Exception ez) {
                ez.printStackTrace();
            }
            return pw.getPacket();
        }

        public static byte[] TrifleWind(int cid, int skillid, int ga, int oid, int gu) {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
            pw.write(0);
            pw.writeInt(cid);
            pw.writeInt(7);
            pw.write(1);
            pw.writeInt(gu);
            pw.writeInt(oid);
            pw.writeInt(skillid);
            for (int i = 1; i < ga; i++) {
                pw.write(1);
                pw.writeInt(2 + i);
                pw.writeInt(1);
                pw.writeInt(Randomizer.rand(0x2A, 0x2F));
                pw.writeInt(7 + i);
                pw.writeInt(Randomizer.rand(5, 0xAB));
                pw.writeInt(Randomizer.rand(0, 0x37));
                pw.writeLong(0);
                pw.writeInt(Randomizer.nextInt());
                pw.writeInt(0);
            }
            pw.write(0);

            pw.write(new byte[69]); // for no dc goodluck charm! >:D xD

            return pw.getPacket();
        }
    }

    public static class PhantomPacket {

        public static byte[] addStolenSkill(int jobNum, int index, int skill, int level) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.CHANGE_STEAL_MEMORY_RESULT.getValue());
            pw.write(1);
            pw.write(0);
            pw.writeInt(jobNum);
            pw.writeInt(index);
            pw.writeInt(skill);
            pw.writeInt(level);
            pw.writeInt(0);
            pw.write(0);

            return pw.getPacket();
        }

        public static byte[] removeStolenSkill(int jobNum, int index) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.CHANGE_STEAL_MEMORY_RESULT.getValue());
            pw.write(1);
            pw.write(3);
            pw.writeInt(jobNum);
            pw.writeInt(index);
            pw.write(0);

            return pw.getPacket();
        }

        public static byte[] replaceStolenSkill(int base, int skill) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.REPLACE_SKILLS.getValue());
            pw.write(1);
            pw.write(skill > 0 ? 1 : 0);
            pw.writeInt(base);
            pw.writeInt(skill);

            return pw.getPacket();
        }

        public static byte[] gainCardStack(int oid, int runningId, int color, int skillid, int damage, int times) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
            pw.write(0);
            pw.writeInt(oid);
            pw.writeInt(1);
            pw.writeInt(damage);
            pw.writeInt(skillid);
            for (int i = 0; i < times; i++) {
                pw.write(1);
                pw.writeInt(damage == 0 ? runningId + i : runningId);
                pw.writeInt(color);
                pw.writeInt(Randomizer.rand(15, 29));
                pw.writeInt(Randomizer.rand(7, 11));
                pw.writeInt(Randomizer.rand(0, 9));
            }
            pw.write(0);

            pw.write(new byte[69]); // for no DC it requires this do not remove
            return pw.getPacket();
        }

        public static byte[] updateCardStack(final int total) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INC_JUDGEMENT_STACK_RESPONSE.getValue());
            pw.write(total);

            return pw.getPacket();
        }

        public static byte[] getCarteAnimation(int cid, int oid, int job, int total, int numDisplay) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
            pw.write(0);
            pw.writeInt(cid);
            pw.writeInt(1);

            pw.writeInt(oid);
            pw.writeInt(job == 2412 ? 24120002 : 24100003);
            pw.write(1);
            for (int i = 1; i <= numDisplay; i++) {
                pw.writeInt(total - (numDisplay - i));
                pw.writeInt(job == 2412 ? 2 : 0);

                pw.writeInt(15 + Randomizer.nextInt(15));
                pw.writeInt(7 + Randomizer.nextInt(5));
                pw.writeInt(Randomizer.nextInt(4));

                pw.write(i == numDisplay ? 0 : 1);
            }

            return pw.getPacket();
        }

        public static byte[] giveAriaBuff(int bufflevel, int buffid, int bufflength) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.DAMAGE_RATE, 0);
            statups.put(CharacterTemporaryStat.IndieDamR, 0);
            PacketHelper.writeBuffMask(pw, statups);
            for (int i = 0; i < 2; i++) {
                pw.writeShort(bufflevel);
                pw.writeInt(buffid);
                pw.writeInt(bufflength);
            }
            pw.write(new byte[3]);
            pw.writeShort(0);
            pw.write(0);
            return pw.getPacket();
        }
    }

    public static class AngelicPacket {

        public static byte[] showRechargeEffect() {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.EFFECT.getValue());
            pw.write(EffectType.ANGELIC_RECHARGE.getValue());
            return pw.getPacket();
        }

        public static byte[] RechargeEffect() {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.EFFECT.getValue());
            pw.write(EffectType.ANGELIC_RECHARGE.getValue());
            return pw.getPacket();
        }

        public static byte[] DressUpTime(byte type) {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.MESSAGE.getValue());
            pw.write(type);
            pw.writeShort(7707);
            pw.write(2);
            pw.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
            return pw.getPacket();
        }

        public static byte[] updateDress(int transform, MapleCharacter chr) {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.SET_KAISER_TRANSFORM_ITEM.getValue());
            pw.writeInt(chr.getId());
            pw.writeInt(transform);
            return pw.getPacket();
        }

        public static byte[] lockSkill(int skillid) {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.LOCK_CHARGE_SKILL.getValue());
            pw.writeInt(skillid);
            return pw.getPacket();
        }

        public static byte[] unlockSkill() {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.UNLOCK_CHARGE_SKILL.getValue());
            pw.writeInt(0);
            return pw.getPacket();
        }

        public static byte[] absorbingSoulSeeker(int characterid, int size, Point essence1, Point essence2, int skillid,
                boolean creation) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
            pw.write(!creation ? 0 : 1);
            pw.writeInt(characterid);
            if (!creation) {
                // false
                pw.writeInt(3);
                pw.write(1);
                pw.write(size);
                pw.write(new byte[3]);
                pw.writeShort(essence1.x);
                pw.writeShort(essence1.y);
                pw.writeShort(essence2.y);
                pw.writeShort(essence2.x);
            } else {
                // true
                pw.writeShort(essence1.x);
                pw.writeShort(essence1.y);
                pw.writeInt(4);
                pw.write(1);
                pw.writeShort(essence1.y);
                pw.writeShort(essence1.x);
            }
            pw.writeInt(skillid);
            if (!creation) {
                for (int i = 0; i < 2; i++) {
                    pw.write(1);
                    pw.writeInt(Randomizer.rand(19, 20));
                    pw.writeInt(1);
                    pw.writeInt(Randomizer.rand(18, 19));
                    pw.writeInt(Randomizer.rand(20, 23));
                    pw.writeInt(Randomizer.rand(36, 55));
                    pw.writeInt(540);
                    pw.writeShort(0);// new 142
                    pw.write(new byte[6]);// new 143
                }
            } else {
                pw.write(1);
                pw.writeInt(Randomizer.rand(6, 21));
                pw.writeInt(1);
                pw.writeInt(Randomizer.rand(42, 45));
                pw.writeInt(Randomizer.rand(4, 7));
                pw.writeInt(Randomizer.rand(267, 100));
                pw.writeInt(0);// 540
                pw.writeInt(0);
                pw.writeInt(0);
            }
            pw.write(0);
            return pw.getPacket();
        }

        public static byte[] SoulSeekerRegen(MapleCharacter chr, int sn) {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
            pw.write(1);
            pw.writeInt(chr.getId());
            pw.writeInt(sn);
            pw.writeInt(4);
            pw.write(1);
            pw.writeInt(sn);
            pw.writeInt(65111007); // hide skills
            pw.write(1);
            pw.writeInt(Randomizer.rand(0x06, 0x10));
            pw.writeInt(1);
            pw.writeInt(Randomizer.rand(0x28, 0x2B));
            pw.writeInt(Randomizer.rand(0x03, 0x04));
            pw.writeInt(Randomizer.rand(0xFA, 0x49));
            pw.writeInt(0);
            pw.writeLong(0);
            pw.write(0);
            return pw.getPacket();
        }

        public static byte[] SoulSeeker(MapleCharacter chr, int skillid, int sn, int sc1, int sc2) {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
            pw.write(0);
            pw.writeInt(chr.getId());
            pw.writeInt(3);
            pw.write(1);
            pw.writeInt(sn);
            if (sn >= 1) {
                pw.writeInt(sc1);// SHOW_ITEM_GAIN_INCHAT
                if (sn == 2) {
                    pw.writeInt(sc2);
                }
            }
            pw.writeInt(65111007); // hide skills
            for (int i = 0; i < 2; i++) {
                pw.write(1);
                pw.writeInt(i + 2);
                pw.writeInt(1);
                pw.writeInt(Randomizer.rand(0x0F, 0x10));
                pw.writeInt(Randomizer.rand(0x1B, 0x22));
                pw.writeInt(Randomizer.rand(0x1F, 0x24));
                pw.writeInt(540);
                pw.writeInt(0);// wasshort new143
                pw.writeInt(0);// new143
            }
            pw.write(0);
            return pw.getPacket();
        }
    }

    public static class LuminousPacket {

        public static byte[] updateLuminousGauge(int darktotal, int lighttotal, int darktype, int lighttype) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.INC_LARKNESS_RESPONSE.getValue());
            pw.writeInt(darktotal);
            pw.writeInt(lighttotal);
            pw.writeInt(darktype);
            pw.writeInt(lighttype);
            pw.writeInt(281874974);// 1210382225

            pw.write(new byte[69]); // for no dc

            return pw.getPacket();
        }

        public static byte[] giveLuminousState(int skill, int light, int dark, int duration) {
            PacketWriter pw = new PacketWriter();
            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());

            PacketHelper.writeSingleMask(pw, CharacterTemporaryStat.LUMINOUS_GAUGE);

            pw.writeShort(1);
            pw.writeInt(skill); // 20040217
            pw.writeInt(duration);
            pw.write(new byte[5]);
            pw.writeInt(skill); // 20040217
            pw.writeInt(483195070);
            pw.write(new byte[8]);
            pw.writeInt(Math.max(light, -1)); // light gauge
            pw.writeInt(Math.max(dark, -1)); // dark gauge
            pw.writeInt(1);
            pw.writeInt(1);// was2
            pw.writeInt(283183599);
            pw.writeInt(0);
            pw.writeInt(0);// new143
            pw.writeInt(1);
            pw.write(0);

            pw.write(new byte[69]); // for no dc
            return pw.getPacket();
        }
    }

    public static class XenonPacket {

        public static byte[] giveXenonSupply(short amount) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            PacketHelper.writeSingleMask(pw, CharacterTemporaryStat.SurplusSupply);

            pw.writeShort(amount);
            pw.writeInt(30020232); // skill id
            pw.writeInt(-1); // duration
            pw.write(new byte[18]);

            return pw.getPacket();
        }

        public static byte[] giveAmaranthGenerator() {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.SurplusSupply, 0);
            statups.put(CharacterTemporaryStat.AmaranthGenerator, 0);
            PacketHelper.writeBuffMask(pw, statups);

            pw.writeShort(20); // gauge fill
            pw.writeInt(30020232); // skill id
            pw.writeInt(-1); // duration

            pw.writeShort(1);
            pw.writeInt(36121054); // skill id
            pw.writeInt(10000); // duration

            pw.write(new byte[5]);
            pw.writeInt(1000);
            pw.writeInt(1);
            pw.write(0);

            pw.write(new byte[69]); // for no dc

            return pw.getPacket();
        }

        public static byte[] PinPointRocket(int cid, List<Integer> moblist) {
            PacketWriter packet = new PacketWriter();
            packet.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
            packet.write(0);
            packet.writeInt(cid);
            packet.writeInt(6);
            packet.write(1);
            packet.writeInt(moblist.size());
            for (int i = 0; i < moblist.size(); i++) {
                packet.writeInt(moblist.get(i));
            }
            packet.writeInt(36001005);
            for (int i = 1; i <= moblist.size(); i++) {
                packet.write(1);
                packet.writeInt(i + 7);
                packet.writeInt(0);
                packet.writeInt(Randomizer.rand(10, 20));
                packet.writeInt(Randomizer.rand(20, 40));
                packet.writeInt(Randomizer.rand(40, 200));
                packet.writeInt(Randomizer.rand(500, 2000));
                packet.writeLong(0); // v196
                packet.writeInt(Randomizer.nextInt());
                packet.writeInt(0);
            }
            packet.write(0);

            packet.write(new byte[69]); // for no dc
            return packet.getPacket();
        }

        public static byte[] MegidoFlameRe(int cid, int oid) {
            PacketWriter packet = new PacketWriter();
            packet.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
            packet.write(0);
            packet.writeInt(cid);
            packet.writeInt(3);
            packet.write(1);
            packet.writeInt(1);
            packet.writeInt(oid);
            packet.writeInt(2121055);
            packet.write(1);
            packet.writeInt(2);
            packet.writeInt(2);
            packet.writeInt(Randomizer.rand(10, 17));
            packet.writeInt(Randomizer.rand(10, 16));
            packet.writeInt(Randomizer.rand(40, 52));
            packet.writeInt(20);
            packet.writeLong(0);
            packet.writeLong(0);
            packet.write(0);
            packet.write(new byte[69]); // for no dc
            return packet.getPacket();
        }

        public static byte[] ShieldChacingRe(int cid, int unkwoun, int oid, int unkwoun2, int unkwoun3) {
            PacketWriter packet = new PacketWriter();
            packet.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
            packet.write(1);
            packet.writeInt(cid);
            packet.writeInt(unkwoun);
            packet.writeInt(4);
            packet.write(1);
            packet.writeInt(oid);
            packet.writeInt(31221014);

            packet.write(1);
            packet.writeInt(unkwoun2 + 1);
            packet.writeInt(3);
            packet.writeInt(unkwoun3);
            packet.writeInt(3);
            packet.writeInt(Randomizer.rand(36, 205));
            packet.writeInt(0);
            packet.writeLong(0);
            packet.writeInt(Randomizer.nextInt());
            packet.writeInt(0);
            packet.write(0);
            packet.write(new byte[69]); // for no dc
            return packet.getPacket();
        }

        public static byte[] ShieldChacing(int cid, List<Integer> moblist, int skillid) {
            PacketWriter packet = new PacketWriter();
            packet.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
            packet.write(0);
            packet.writeInt(cid);
            packet.writeInt(3);
            packet.write(1);
            packet.writeInt(moblist.size());
            for (int i = 0; i < moblist.size(); i++) {
                packet.writeInt(((Integer) moblist.get(i)).intValue());
            }
            packet.writeInt(skillid);
            for (int i = 1; i <= moblist.size(); i++) {
                packet.write(1);
                packet.writeInt(1 + i);
                packet.writeInt(3);
                packet.writeInt(Randomizer.rand(1, 20));
                packet.writeInt(Randomizer.rand(20, 50));
                packet.writeInt(Randomizer.rand(50, 200));
                packet.writeInt(skillid == 2121055 ? 720 : 660);
                packet.writeLong(0);
                packet.writeInt(Randomizer.nextInt());
                packet.writeInt(0);
            }
            packet.write(0);
            packet.write(new byte[69]); // for no dc
            return packet.getPacket();
        }

        public static byte[] EazisSystem(int cid, int oid) {
            PacketWriter packet = new PacketWriter();
            packet.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
            packet.write(0);
            packet.writeInt(cid);
            packet.writeInt(5);
            packet.write(1);
            packet.writeInt(oid);
            packet.writeInt(36110004);
            for (int i = 0; i < 3; i++) {
                packet.write(1);
                packet.writeInt(i + 2);
                packet.writeInt(0);
                packet.writeInt(0x23);
                packet.writeInt(5);
                packet.writeInt(Randomizer.rand(80, 100));
                packet.writeInt(Randomizer.rand(200, 300));
                packet.writeLong(0); // v196
                packet.writeInt(Randomizer.nextInt());
                packet.writeInt(0);
            }
            packet.write(0);
            packet.write(new byte[69]); // for no dc
            return packet.getPacket();
        }
    }

    public static class AvengerPacket {

        public static byte[] giveAvengerHpBuff(int hp) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());

            PacketHelper.writeSingleMask(pw, CharacterTemporaryStat.Larkness);
            pw.writeShort(3);
            pw.writeInt(0);
            pw.writeInt(2100000000);
            pw.write(new byte[5]);
            pw.writeInt(hp);
            pw.write(new byte[9]);

            pw.write(new byte[69]); // for no dc
            return pw.getPacket();
        }

        public static byte[] giveExceed(short amount) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            PacketHelper.writeSingleMask(pw, CharacterTemporaryStat.EXCEED);

            pw.writeShort(amount);
            pw.writeInt(30010230); // skill id
            pw.writeInt(-1); // duration
            pw.write(new byte[14]);

            pw.write(new byte[69]); // for no dc

            return pw.getPacket();
        }

        public static byte[] giveExceedAttack(int skill, short amount) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            PacketHelper.writeSingleMask(pw, CharacterTemporaryStat.EXCEED_ATTACK);

            pw.writeShort(amount);
            pw.writeInt(skill); // skill id
            pw.writeInt(15000); // duration
            pw.write(new byte[14]);

            pw.write(new byte[69]); // for no dc
            return pw.getPacket();
        }

        public static byte[] cancelExceed() {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_RESET.getValue());

            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.EXCEED, 0);
            statups.put(CharacterTemporaryStat.EXCEED_ATTACK, 0);
            PacketHelper.writeBuffMask(pw, statups);

            return pw.getPacket();
        }
    }

    public static class DawnWarriorPacket {

        public static byte[] giveMoonfallStance(int level) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.IndieCr, 0);
            statups.put(CharacterTemporaryStat.MOON_STANCE2, 0);
            statups.put(CharacterTemporaryStat.WARRIOR_STANCE, 0);
            PacketHelper.writeBuffMask(pw, statups);

            pw.writeShort(level);
            pw.writeInt(11101022);
            pw.writeInt(Integer.MAX_VALUE);
            pw.writeShort(1);
            pw.writeInt(11101022);
            pw.writeInt(Integer.MAX_VALUE);
            pw.writeInt(0);
            pw.write(5);
            pw.write(1);
            pw.writeInt(1);
            pw.writeInt(11101022);
            pw.writeInt(level);
            pw.writeInt(Integer.MAX_VALUE);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.write(1);
            pw.writeInt(0);

            pw.write(new byte[69]); // for no dc
            return pw.getPacket();
        }

        public static byte[] giveSunriseStance(int level) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.Booster, 0);
            statups.put(CharacterTemporaryStat.IndieDamR, 0);
            statups.put(CharacterTemporaryStat.WARRIOR_STANCE, 0);
            PacketHelper.writeBuffMask(pw, statups);

            pw.writeShort(level);
            pw.writeInt(11111022);
            pw.writeInt(Integer.MAX_VALUE);
            pw.writeInt(0);
            pw.write(5);
            pw.write(1);
            pw.writeInt(1);
            pw.writeInt(11111022);
            pw.writeInt(-1);
            pw.writeInt(Integer.MAX_VALUE);
            pw.writeInt(0);
            pw.writeInt(1);
            pw.writeInt(11111022);
            pw.writeInt(0x19);
            pw.writeInt(Integer.MAX_VALUE);
            pw.writeInt(0);
            pw.writeInt(0);
            pw.write(1);
            pw.writeInt(0);

            pw.write(new byte[69]); // for no dc
            return pw.getPacket();
        }

        public static byte[] giveEquinox_Moon(int level, int duration) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.IndieCr, 0);
            statups.put(CharacterTemporaryStat.MOON_STANCE2, 0);
            statups.put(CharacterTemporaryStat.EQUINOX_STANCE, 0);
            PacketHelper.writeBuffMask(pw, statups);

            pw.writeShort(level);
            pw.writeInt(11121005);
            pw.writeInt(duration);
            pw.writeShort(1);
            pw.writeInt(11121005);
            pw.writeInt(duration);
            pw.writeInt(0);
            pw.write(5);
            pw.writeInt(1);
            pw.writeInt(11121005);
            pw.writeInt(level);
            pw.writeInt(duration);
            pw.writeInt(duration);
            pw.writeInt(0);
            pw.write(1);
            pw.writeInt(0);

            pw.write(new byte[69]); // for no dc
            return pw.getPacket();
        }

        public static byte[] giveEquinox_Sun(int level, int duration) {
            PacketWriter pw = new PacketWriter();

            pw.writeShort(SendPacketOpcode.TEMPORARY_STAT_SET.getValue());
            Map<CharacterTemporaryStat, Integer> statups = new EnumMap<>(CharacterTemporaryStat.class);
            statups.put(CharacterTemporaryStat.Booster, 0);
            statups.put(CharacterTemporaryStat.IndieDamR, 0);
            statups.put(CharacterTemporaryStat.EQUINOX_STANCE, 0);
            PacketHelper.writeBuffMask(pw, statups);

            pw.writeShort(level);
            pw.writeInt(11121005);
            pw.writeInt(duration);
            pw.writeInt(0);
            pw.write(5);
            pw.writeInt(1);
            pw.writeInt(11121005);
            pw.writeInt(-1);
            pw.writeInt(duration);
            pw.writeInt(duration);
            pw.writeInt(1);
            pw.writeInt(11121005);
            pw.writeInt(0x19);
            pw.writeInt(duration);
            pw.writeInt(duration);
            pw.writeInt(0);
            pw.write(1);
            pw.writeInt(0);

            pw.write(new byte[69]); // for no dc

            return pw.getPacket();
        }
    }
}
