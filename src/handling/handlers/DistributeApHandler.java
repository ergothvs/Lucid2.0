package handling.handlers;

import java.util.EnumMap;
import java.util.Map;

import client.MapleClient;
import client.MapleStat;
import client.PlayerStats;
import constants.GameConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import tools.Randomizer;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class DistributeApHandler {

	@PacketHandler(opcode = RecvPacketOpcode.DISTRIBUTE_AP)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		Map<MapleStat, Long> statupdate = new EnumMap<>(MapleStat.class);
        c.getSession().write(CWvsContext.updatePlayerStats(statupdate, true, c.getPlayer()));
        lea.skip(4); // update tick
        final int statmask = lea.readInt();
        final PlayerStats stat = c.getPlayer().getStat();
        final int job = c.getPlayer().getJob();
        if (c.getPlayer().getRemainingAp() > 0) {
            switch (statmask) {
                case 0x40: // Str
                    if (stat.getStr() >= 999) {
                        return;
                    }
                    stat.setStr((short) (stat.getStr() + 1), c.getPlayer());
                    statupdate.put(MapleStat.STR, (long) stat.getStr());
                    break;
                case 0x80: // Dex
                    if (stat.getDex() >= 999) {
                        return;
                    }
                    stat.setDex((short) (stat.getDex() + 1), c.getPlayer());
                    statupdate.put(MapleStat.DEX, (long) stat.getDex());
                    break;
                case 0x100: // Int
                    if (stat.getInt() >= 999) {
                        return;
                    }
                    stat.setInt((short) (stat.getInt() + 1), c.getPlayer());
                    statupdate.put(MapleStat.INT, (long) stat.getInt());
                    break;
                case 0x200: // Luk
                    if (stat.getLuk() >= 999) {
                        return;
                    }
                    stat.setLuk((short) (stat.getLuk() + 1), c.getPlayer());
                    statupdate.put(MapleStat.LUK, (long) stat.getLuk());
                    break;
                case 0x400: // HP
                    int maxhp = stat.getMaxHp();
                    if (c.getPlayer().getHpApUsed() >= 10000 || maxhp >= 500000) {
                        return;
                    }
                    if (GameConstants.isBeginnerJob(job)) { // Beginner
                        maxhp += Randomizer.rand(8, 12);
                    } else if ((job >= 100 && job <= 132) || (job >= 3200 && job <= 3212) || (job >= 1100 && job <= 1112) || (job >= 3100 && job <= 3112)) { // Warrior
                        maxhp += Randomizer.rand(36, 42);
                    } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job))) { // Magician
                        maxhp += Randomizer.rand(10, 20);
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 3300 && job <= 3312) || (job >= 2300 && job <= 2312)) { // Bowman
                        maxhp += Randomizer.rand(16, 20);
                    } else if ((job >= 510 && job <= 512) || (job >= 1510 && job <= 1512)) {
                        maxhp += Randomizer.rand(28, 32);
                    } else if ((job >= 500 && job <= 532) || (job >= 3500 && job <= 3512) || job == 1500) { // Pirate
                        maxhp += Randomizer.rand(18, 22);
                    } else if (job >= 1200 && job <= 1212) { // Flame Wizard
                        maxhp += Randomizer.rand(15, 21);
                    } else if (job >= 2000 && job <= 2112) { // Aran
                        maxhp += Randomizer.rand(38, 42);
                    } else { // GameMaster
                        maxhp += Randomizer.rand(50, 100);
                    }
                    maxhp = Math.min(500000, Math.abs(maxhp));
                    c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + 1));
                    stat.setMaxHp(maxhp, c.getPlayer());
                    statupdate.put(MapleStat.MAXHP, (long) maxhp);
                    break;
                case 0x800: // MP
                    int maxmp = stat.getMaxMp();
                    if (c.getPlayer().getHpApUsed() >= 10000 || stat.getMaxMp() >= 500000) {
                        return;
                    }
                    if (GameConstants.isBeginnerJob(job)) { // Beginner
                        maxmp += Randomizer.rand(6, 8);
                    } else if (GameConstants.isDemonSlayer(job) || GameConstants.isAngelicBuster(job) || GameConstants.isDemonAvenger(job)) { // Demon, Angelic Buster
                        return;
                    } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job)) || (job >= 3200 && job <= 3212) || (job >= 1200 && job <= 1212)) { // Magician
                        maxmp += Randomizer.rand(38, 40);
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 500 && job <= 532) || (job >= 3200 && job <= 3212) || (job >= 3500 && job <= 3512) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 1500 && job <= 1512) || (job >= 2300 && job <= 2312)) { // Bowman
                        maxmp += Randomizer.rand(10, 12);
                    } else if ((job >= 100 && job <= 132) || (job >= 1100 && job <= 1112) || (job >= 2000 && job <= 2112)) { // Soul Master
                        maxmp += Randomizer.rand(6, 9);
                    } else { // GameMaster
                        maxmp += Randomizer.rand(50, 100);
                    }
                    maxmp = Math.min(500000, Math.abs(maxmp));
                    c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + 1));
                    stat.setMaxMp(maxmp, c.getPlayer());
                    statupdate.put(MapleStat.MAXMP, (long) maxmp);
                    break;
                default:
                    c.getSession().write(CWvsContext.enableActions());
                    return;
            }
            c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - 1));
            statupdate.put(MapleStat.AVAILABLEAP, (long) c.getPlayer().getRemainingAp());
            c.getSession().write(CWvsContext.updatePlayerStats(statupdate, true, c.getPlayer()));
        }
	}
}
