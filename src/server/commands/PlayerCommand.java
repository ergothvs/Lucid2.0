package server.commands;

import java.util.Arrays;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.channel.ChannelServer;
import script.npc.NPCScriptManager;
import script.npc.NPCTalk;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.SavedLocationType;
import tools.FileoutputUtil;
import tools.StringUtil;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;

/**
 *
 * @author Emilyx3
 */
public class PlayerCommand {

	public static PlayerGMRank getPlayerLevelRequired() {
		return PlayerGMRank.NORMAL;
	}

	public static class Dispose extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.removeClickedNPC();
			NPCScriptManager.getInstance().dispose(c);
			c.getSession().write(CWvsContext.enableActions());
			return 1;
		}
	}

	public static class ExpFix extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.getPlayer().setExp(c.getPlayer().getExp() - GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) >= 0 ? GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) : 0);
			return 1;
		}
	}

	public static class ResetExp extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.getPlayer().setExp(0);
			return 1;
		}
	}

	public static class STR extends DistributeStatCommands {

		public STR() {
			stat = MapleStat.STR;
		}
	}

	public static class DEX extends DistributeStatCommands {

		public DEX() {
			stat = MapleStat.DEX;
		}
	}

	public static class INT extends DistributeStatCommands {

		public INT() {
			stat = MapleStat.INT;
		}
	}

	public static class LUK extends DistributeStatCommands {

		public LUK() {
			stat = MapleStat.LUK;
		}
	}

	public static class HP extends DistributeStatCommands {

		public HP() {
			stat = MapleStat.MAXHP;
		}
	}

	public static class MP extends DistributeStatCommands {

		public MP() {
			stat = MapleStat.MAXMP;
		}
	}

	public static class Hair extends DistributeStatCommands {

		public Hair() {
			stat = MapleStat.HAIR;
		}
	}

	public abstract static class DistributeStatCommands extends CommandExecute {

		protected MapleStat stat = null;
		private static final int statLim = 500000;
		private static final int hpMpLim = 500000;

		private void setStat(MapleCharacter player, int current, int amount) {
			switch (stat) {
			case STR:
				player.getStat().setStr((short) (current + amount), player);
				player.updateSingleStat(MapleStat.STR, player.getStat().getStr());
				break;
			case DEX:
				player.getStat().setDex((short) (current + amount), player);
				player.updateSingleStat(MapleStat.DEX, player.getStat().getDex());
				break;
			case INT:
				player.getStat().setInt((short) (current + amount), player);
				player.updateSingleStat(MapleStat.INT, player.getStat().getInt());
				break;
			case LUK:
				player.getStat().setLuk((short) (current + amount), player);
				player.updateSingleStat(MapleStat.LUK, player.getStat().getLuk());
				break;
			case MAXHP:
				long maxhp = Math.min(500000, Math.abs(current + amount * 30));
				//      player.getStat().setMaxHp((short) (current + amount * 30), player);
				player.getStat().setMaxHp((short) maxhp, player);
				player.updateSingleStat(MapleStat.HP, player.getStat().getHp());
				break;
			case MAXMP:
				long maxmp = Math.min(500000, Math.abs(current + amount));
				player.getStat().setMaxMp((short) maxmp, player);
				player.updateSingleStat(MapleStat.MP, player.getStat().getMp());
				break;     
			case HAIR:
				int hair = amount;
				player.setSecondHair(hair);
				player.updateSingleStat(MapleStat.HAIR, player.getSecondHair());
				break;
			}
		}

		private int getStat(MapleCharacter player) {
			switch (stat) {
			case STR:
				return player.getStat().getStr();
			case DEX:
				return player.getStat().getDex();
			case INT:
				return player.getStat().getInt();
			case LUK:
				return player.getStat().getLuk();
			case MAXHP:
				return player.getStat().getMaxHp();
			case MAXMP:
				return player.getStat().getMaxMp();
			default:
				throw new RuntimeException(); //Will never happen.
			}
		}

		@Override
		public int execute(MapleClient c, String[] splitted) {
			if (splitted.length < 2) {
				c.getPlayer().dropMessage(5, "Invalid number entered.");
				return 0;
			}
			int change;
			try {
				change = Integer.parseInt(splitted[1]);
			} catch (NumberFormatException nfe) {
				c.getPlayer().dropMessage(5, "Invalid number entered.");
				return 0;
			}
			int hpUsed = 0;
			int mpUsed = 0;
			//   if (stat == MapleStat.MAXHP) {
			//        hpUsed = change;
			//       short job = c.getPlayer().getJob();
			//       change *= GameConstants.getHpApByJob(job);
			//   }
			if (stat == MapleStat.MAXMP) {
				mpUsed = change;
				short job = c.getPlayer().getJob();
				if (GameConstants.isDemonSlayer(job) || GameConstants.isAngelicBuster(job) || GameConstants.isDemonAvenger(job)) {
					c.getPlayer().dropMessage(5, "You cannot raise MP.");
					return 0;
				}
				change *= GameConstants.getMpApByJob(job);
			}         

			if (change <= 0) {
				c.getPlayer().dropMessage(5, "You don't have enough AP Resets that.");
				return 0;
			}
			if (c.getPlayer().getRemainingAp() < change) {
				c.getPlayer().dropMessage(5, "You don't have enough AP for that.");
				return 0;
			}
			if (getStat(c.getPlayer()) + change > statLim && stat != MapleStat.MAXHP && stat != MapleStat.MAXMP) {
				c.getPlayer().dropMessage(5, "The stat limit is " + statLim + ".");
				return 0;
			}
			if (getStat(c.getPlayer()) + change > hpMpLim && (stat == MapleStat.MAXHP || stat == MapleStat.MAXMP)) {
				c.getPlayer().dropMessage(5, "The stat limit is " + hpMpLim + ".");
				return 0;
			}
			setStat(c.getPlayer(), getStat(c.getPlayer()), change);
			c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - change));
			c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + hpUsed));
			c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + mpUsed));
			c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
			if (stat == MapleStat.MAXHP) {
				c.getPlayer().dropMessage(5, StringUtil.makeEnumHumanReadable(stat.name()) + " has been raised by " + change * 30 + ".");
				c.getPlayer().fakeRelog();
			} else
				c.getPlayer().dropMessage(5, StringUtil.makeEnumHumanReadable(stat.name()) + " has been raised by " + change + ".");
			return 1;
		}
	}

	public static class Mob extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			MapleMonster mob = null;
			for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
				mob = (MapleMonster) monstermo;
				if (mob.isAlive()) {
					c.getPlayer().dropMessage(6, "Monster " + mob.toString());
					break; //only one
				}
			}
			if (mob == null) {
				c.getPlayer().dropMessage(6, "No monster was found.");
			}
			return 1;
		}
	}

	/*public abstract static class OpenNPCCommand extends CommandExecute {

     protected int npc = -1;
     private static int[] npcs = { //Ish yur job to make sure these are in order and correct ;(
     9270035,
     9900000,
     9000000,
     9000030,
     9010000,
     9000085,
     9000018,
     9900000, // yes, i know, again but for other reason.
     9010000 // again too, but used for opening a scriptname
     };

     @Override
     public int execute(MapleClient c, String[] splitted) {
     if (npc != 6 && npc != 5 && npc != 4 && npc != 3 && npc != 1 && c.getPlayer().getMapId() != 910000000) { //drpcash can use anywhere
     if (c.getPlayer().getLevel() < 10 && c.getPlayer().getJob() != 200) {
     c.getPlayer().dropMessage(5, "You must be over level 10 to use this command.");
     return 0;
     }
     if (c.getPlayer().isInBlockedMap()) {
     c.getPlayer().dropMessage(5, "You may not use this command here.");
     return 0;
     }
     } else if (npc == 1) {
     if (c.getPlayer().getLevel() < 70) {
     c.getPlayer().dropMessage(5, "You must be over level 70 to use this command.");
     return 0;
     }
     }
     if (c.getPlayer().hasBlockedInventory()) {
     c.getPlayer().dropMessage(5, "You may not use this command here.");
     return 0;
     }
     NPCScriptManager.getInstance().start(c, npcs[npc], npc == 1 ? "CashDrop" : npc == 8 ? "BossWarp" : null);
     return 1;
     }
     }

     public static class Style extends Stylist {
     }

     public static class Stylist extends OpenNPCCommand {

     public Stylist() {
     npc = 7;
     }
     }

     public static class Npc extends Maple {
     }

     public static class Maple extends OpenNPCCommand {

     public Crescent() {
     npc = 0;
     }
     }

     public static class BossWarp extends OpenNPCCommand {

     public BossWarp() {
     npc = 8;
     }
     }

     public static class Advance extends CommandExecute {

     @Override
     public int execute(MapleClient c, String[] splitted) {
     if (c.getPlayer().isInBlockedMap()) {
     c.getPlayer().dropMessage(5, "You may not use this command here.");
     return 0;
     }
     if (c.getPlayer().getLevel() < 10) {
     c.getPlayer().dropMessage(5, "You must be over level 10 to use this command.");
     return 0;
     }
     NPCScriptManager.getInstance().start(c, 9900002, null);
     return 1;
     }
     }*/

	public static class Save extends CommandExecute {

		@Override

		public int execute(MapleClient c, String[] splitted) {
			c.getPlayer().setExp(c.getPlayer().getExp() - GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) >= 0 ? GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) : 0);
			c.getPlayer().saveToDB(false, false);
			return 1;
		}
	}
	
	public static class Event extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			if (c.getPlayer().isInBlockedMap() || c.getPlayer().hasBlockedInventory()) {
				c.getPlayer().dropMessage(5, "You may not use this command here.");
				return 0;
			}
			NPCScriptManager.getInstance().start(c, 9000000, null);
			return 1;
		}
	}

	public static class fm extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			if (c.getPlayer().getLevel() < 10 && c.getPlayer().getJob() != 200) {
				c.getPlayer().dropMessage(5, "You must be over level 10 to use this command.");
				return 0;
			}
			if (c.getPlayer().hasBlockedInventory() || c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000/* || FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())*/) {
				c.getPlayer().dropMessage(5, "You may not use this command here.");
				return 0;
			}

			if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
				c.getPlayer().dropMessage(5, "You may not use this command here.");
				return 0;
			}

			c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET, c.getPlayer().getMap().getReturnMap().getId());
			MapleMap map = c.getChannelServer().getMapFactory().getMap(910000000);

			c.getPlayer().changeMap(map, map.getPortal(0));

			return 1;
		}
	}
	
	public static class Check extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getCSPoints(1) + " Cash, " + c.getPlayer().getEPoints() + " Event Points, " + c.getPlayer().getDPoints() + " Donation Points, " + c.getPlayer().getVPoints() + " voting points and " + c.getPlayer().getIntNoRecord(GameConstants.BOSS_PQ) + " Boss Party Quest points.");
			c.getPlayer().dropMessage(6, "The time is currently " + FileoutputUtil.CurrentReadable_TimeGMT() + " GMT. | EXP " + (Math.round(c.getPlayer().getEXPMod()) * 100) * Math.round(c.getPlayer().getStat().expBuff / 100.0) + "%, Drop " + (Math.round(c.getPlayer().getDropMod()) * 100) * Math.round(c.getPlayer().getStat().dropBuff / 100.0) + "%, Meso " + Math.round(c.getPlayer().getStat().mesoBuff / 100.0) * 100 + "%");
			c.getPlayer().dropMessage(6, "EXP: " + c.getPlayer().getExp() + " / " + c.getPlayer().getNeededExp());
			c.removeClickedNPC();
			NPCScriptManager.getInstance().dispose(c);
			c.getSession().write(CWvsContext.enableActions());
			return 1;
		}
	}

	public static class Help extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			StringBuilder sb = new StringBuilder();
			sb.append("\r\n@str, @dex, @int, @luk, @hp, @mp <amount to add or subtract>");
			sb.append("\r\n@mob < Information on the closest monster >");
			sb.append("\r\n@check < Displays various information; also use if you are stuck or unable to talk to NPC >");
			//sb.append("\r\n@npc < Universal NPC >");
			sb.append("\r\n@callgm < Send a message to all online GameMasters.");
			sb.append("\r\n@home < Warp to Lucid base >");
			sb.append("\r\n@fm < Warp to the FreeMarket instantly. >");
			sb.append("\r\n@job < Job advancements! >");
			sb.append("\r\n@save < Fixes your experience and saves your character >");
			/*sb.append("\r\n@joinevent < Join ongoing event >");
             sb.append("\r\n@crescent < Universal Town Warp / Event NPC>");
             sb.append("\r\n@dcash < Universal Cash Item Dropper >");
             sb.append("\r\n@tsmega < Toggle super megaphone on/off >");
             sb.append("\r\n@ea < If you are unable to attack or talk to NPC >");
             sb.append("\r\n@clearslot < Cleanup that trash in your inventory >");
             sb.append("\r\n@ranking < Use @ranking for more details >");
             sb.append("\r\n@checkdrop < Use @checkdrop for more details >");
             sb.append("\r\n@style < Styler >");
             sb.append("\r\n@advance < Job Advancer >");
             sb.append("\r\n@bosswarp < Boss Warper >");
             sb.append("\r\n@fly < Makes you fly if you're in the Free Market >");*/
			if (c.canClickNPC()) {
				NPCTalk talk = new NPCTalk((byte) 4, 9010000, (byte) 0);
				talk.setText(sb.toString());

				NPCPacket.getNPCTalk(talk);
			}
			for (String command : sb.toString().split("\r\n")) {
				c.getPlayer().dropMessage(5, command);
			}
			return 1;
		}
	}

	public static class job extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			if (c.getPlayer().isInBlockedMap() || c.getPlayer().hasBlockedInventory()) {
				c.getPlayer().dropMessage(5, "You may not use this command here.");
				return 0;
			}else if (c.getPlayer().getLevel() < 30) {
				c.getPlayer().dropMessage(5, "You need to be at least lvl 30 in order to advance.");
				return 0;
			} else {
				NPCScriptManager.getInstance().start(c, 2300001, null);
				return 1;
			}
		}
	}

	public static class JoinEvent extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.getChannelServer().warpToEvent(c.getPlayer());
			return 1;
		}
	}

	public static class CashDrop extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			NPCScriptManager.getInstance().start(c, 9010000, "CashDrop");
			return 1;
		}
	}

	public static class CallGM extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
				cserv.broadcastGMMessage(tools.packet.CField.multiChat("[GM Help] " + c.getPlayer().getName(), StringUtil.joinStringFrom(splitted, 1), 6));
			}
			c.getPlayer().dropMessage(5, "Your message had been sent successfully.");
			return 1;
		}
	}
}
