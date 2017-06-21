package server.commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.MapleStat;
import client.Skill;
import client.SkillFactory;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import handling.world.World;
import lib.data.MapleData;
import lib.data.MapleDataProvider;
import lib.data.MapleDataProviderFactory;
import lib.data.MapleDataTool;
import script.event.EventManager;
import script.npc.NPCScriptManager;
import script.npc.NPCTalk;
import server.ItemInformation;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleSquad.MapleSquadType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.quest.MapleQuest;
import server.shops.MapleShopFactory;
import tools.HexTool;
import tools.Pair;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;

/**
 *
 * @author Emilyx3
 */
public class InternCommand {

	public static PlayerGMRank getPlayerLevelRequired() {
		return PlayerGMRank.INTERN;
	}

	public static class Hide extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			if (c.getPlayer().isHidden()) {
				c.getPlayer().dispelBuff(9101004);
				// MapleItemInformationProvider.getInstance().getItemEffect(2100069).applyTo(c.getPlayer());
				// c.getSession().write(CWvsContext.InfoPacket.getStatusMsg(2100069));
			} else {
				SkillFactory.getSkill(9101004).getEffect(1).applyTo(c.getPlayer());
			}
			return 0;
		}
	}

	public static class Heal extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.getPlayer().getStat().heal(c.getPlayer());
			c.getPlayer().dispelDebuffs();
			return 0;
		}
	}

	public static class HealMap extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			MapleCharacter player = c.getPlayer();
			for (MapleCharacter mch : player.getMap().getCharacters()) {
				if (mch != null) {
					mch.getStat().setHp(mch.getStat().getMaxHp(), mch);
					mch.updateSingleStat(MapleStat.HP, mch.getStat().getMaxHp());
					mch.getStat().setMp(mch.getStat().getMaxMp(), mch);
					mch.updateSingleStat(MapleStat.MP, mch.getStat().getMaxMp());
					mch.dispelDebuffs();
				}
			}
			return 1;
		}
	}

	public static class WhereAmI extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.getPlayer().dropMessage(5, "You are on map " + c.getPlayer().getMap().getId());
			return 1;
		}
	}

	public static class Online extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			String online = "";
			for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
				online += ChannelServer.getInstance(i).getPlayerStorage().getOnlinePlayers(true);
			}
			c.getPlayer().dropMessage(6, online);
			return 1;
		}
	}

	public static class CharInfo extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			final StringBuilder builder = new StringBuilder();
			final MapleCharacter other = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
			if (other == null) {
				builder.append("...does not exist");
				c.getPlayer().dropMessage(6, builder.toString());
				return 0;
			}
			// if (other.getClient().getLastPing() <= 0) {
			// other.getClient().sendPing();
			// }
			builder.append(MapleClient.getLogMessage(other, ""));
			builder.append(" at (").append(other.getPosition().x);
			builder.append(", ").append(other.getPosition().y);
			builder.append(")");

			builder.append("\r\nHP : ");
			builder.append(other.getStat().getHp());
			builder.append(" /");
			builder.append(other.getStat().getCurrentMaxHp());

			builder.append(" || MP : ");
			builder.append(other.getStat().getMp());
			builder.append(" /");
			builder.append(other.getStat().getCurrentMaxMp(other.getJob()));

			builder.append(" || BattleshipHP : ");
			builder.append(other.currentBattleshipHP());

			builder.append("\r\nWATK : ");
			builder.append(other.getStat().getTotalWatk());
			builder.append(" || MAD : ");
			builder.append(other.getStat().getTotalMagic());
			builder.append(" || MAXDAMAGE : ");
			builder.append(other.getStat().getCurrentMaxBaseDamage());
			builder.append(" || DAMAGE% : ");
			builder.append(other.getStat().dam_r);
			builder.append(" || BOSSDAMAGE% : ");
			builder.append(other.getStat().bossdam_r);
			builder.append(" || CRIT CHANCE : ");
			builder.append(other.getStat().passive_sharpeye_rate());
			builder.append(" || CRIT DAMAGE : ");
			builder.append(other.getStat().passive_sharpeye_percent());

			builder.append("\r\nSTR : ");
			builder.append(other.getStat().getStr()).append(" + (")
					.append(other.getStat().getTotalStr() - other.getStat().getStr()).append(")");
			builder.append(" || DEX : ");
			builder.append(other.getStat().getDex()).append(" + (")
					.append(other.getStat().getTotalDex() - other.getStat().getDex()).append(")");
			builder.append(" || INT : ");
			builder.append(other.getStat().getInt()).append(" + (")
					.append(other.getStat().getTotalInt() - other.getStat().getInt()).append(")");
			builder.append(" || LUK : ");
			builder.append(other.getStat().getLuk()).append(" + (")
					.append(other.getStat().getTotalLuk() - other.getStat().getLuk()).append(")");

			builder.append("\r\nEXP : ");
			builder.append(other.getExp());
			builder.append(" || MESO : ");
			builder.append(other.getMeso());

			builder.append("\r\nVote Points : ");
			builder.append(other.getVPoints());
			builder.append(" || Event Points : ");
			builder.append(other.getPoints());
			builder.append(" || NX Prepaid : ");
			builder.append(other.getCSPoints(1));

			builder.append("\r\nParty : ");
			builder.append(other.getParty() == null ? -1 : other.getParty().getId());

			builder.append(" || hasTrade: ");
			builder.append(other.getTrade() != null);


			builder.append("\r\nJob : ");
			builder.append(other.getJob());
			builder.append(" || Map : ");
			builder.append(other.getMapId());
			builder.append(" || Channel : ");
			builder.append(other.getClient().getChannel());
			// builder.append(" || Latency: ");
			// builder.append(other.getClient().getLatency());
			// builder.append(" || PING: ");
			// builder.append(other.getClient().getLastPing());
			// builder.append(" || PONG: ");
			// builder.append(other.getClient().getLastPong());
			// builder.append(" || remoteAddress: ");
			// other.getClient().DebugMessage(builder);

			c.getPlayer().dropMessage(6, builder.toString());
			return 1;
		}
	}

	public static class GoTo extends CommandExecute {

		private static final HashMap<String, Integer> gotomaps = new HashMap<>();

		static {
			gotomaps.put("ardent", 910001000);
			gotomaps.put("ariant", 260000100);
			gotomaps.put("amherst", 1010000);
			gotomaps.put("amoria", 680000000);
			gotomaps.put("aqua", 860000000);
			gotomaps.put("aquaroad", 230000000);
			gotomaps.put("boatquay", 541000000);
			gotomaps.put("cwk", 610030000);
			gotomaps.put("edelstein", 310000000);
			gotomaps.put("ellin", 300000000);
			gotomaps.put("ellinia", 101000000);
			gotomaps.put("ellinel", 101071300);
			gotomaps.put("elluel", 101050000);
			gotomaps.put("elnath", 211000000);
			gotomaps.put("erev", 130000000);
			gotomaps.put("florina", 120000300);
			gotomaps.put("fm", 910000000);
			gotomaps.put("future", 271000000);
			gotomaps.put("gmmap", 180000000);
			gotomaps.put("happy", 209000000);
			gotomaps.put("harbor", 104000000);
			gotomaps.put("henesys", 100000000);
			gotomaps.put("herbtown", 251000000);
			gotomaps.put("kampung", 551000000);
			gotomaps.put("kerning", 103000000);
			gotomaps.put("korean", 222000000);
			gotomaps.put("leafre", 240000000);
			gotomaps.put("ludi", 220000000);
			gotomaps.put("malaysia", 550000000);
			gotomaps.put("mulung", 250000000);
			gotomaps.put("nautilus", 120000000);
			gotomaps.put("nlc", 600000000);
			gotomaps.put("omega", 221000000);
			gotomaps.put("orbis", 200000000);
			gotomaps.put("pantheon", 400000000);
			gotomaps.put("pinkbean", 270050100);
			gotomaps.put("phantom", 610010000);
			gotomaps.put("perion", 102000000);
			gotomaps.put("rien", 140000000);
			gotomaps.put("showatown", 801000000);
			gotomaps.put("singapore", 540000000);
			gotomaps.put("sixpath", 104020000);
			gotomaps.put("sleepywood", 105000000);
			gotomaps.put("southperry", 2000000);
			gotomaps.put("tot", 270000000);
			gotomaps.put("twilight", 273000000);
			gotomaps.put("tynerum", 301000000);
			gotomaps.put("zipangu", 800000000);
			gotomaps.put("pianus", 230040420);
			gotomaps.put("horntail", 240060200);
			gotomaps.put("chorntail", 240060201);
			gotomaps.put("griffey", 240020101);
			gotomaps.put("manon", 240020401);
			gotomaps.put("zakum", 280030000);
			gotomaps.put("czakum", 280030001);
			gotomaps.put("pap", 220080001);
			gotomaps.put("oxquiz", 109020001);
			gotomaps.put("ola", 109030101);
			gotomaps.put("fitness", 109040000);
			gotomaps.put("snowball", 109060000);
		}

		@Override
		public int execute(MapleClient c, String[] splitted) {
			if (splitted.length < 2) {
				c.getPlayer().dropMessage(6, "Syntax: !goto <mapname>");
			} else {
				if (gotomaps.containsKey(splitted[1])) {
					MapleMap target = c.getChannelServer().getMapFactory().getMap(gotomaps.get(splitted[1]));
					if (target == null) {
						c.getPlayer().dropMessage(6, "Map does not exist");
						return 0;
					}
					MaplePortal targetPortal = target.getPortal(0);
					c.getPlayer().changeMap(target, targetPortal);
				} else {
					if (splitted[1].equals("locations")) {
						c.getPlayer().dropMessage(6, "Use !goto <location>. Locations are as follows:");
						StringBuilder sb = new StringBuilder();
						for (String s : gotomaps.keySet()) {
							sb.append(s).append(", ");
						}
						c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));
					} else {
						c.getPlayer().dropMessage(6,
								"Invalid command syntax - Use !goto <location>. For a list of locations, use !goto locations.");
					}
				}
			}
			return 1;
		}
	}

	public static class Clock extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.getPlayer().getMap()
					.broadcastMessage(CField.getClock(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 60)));
			return 1;
		}
	}

	public static class Map extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			try {
				MapleCharacter victim;
				int ch = World.Find.findChannel(splitted[1]);
				if (ch < 0) {
					MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
					if (target == null) {
						c.getPlayer().dropMessage(6, "Map does not exist.");
						return 0;
					}
					MaplePortal targetPortal = null;
					if (splitted.length > 2) {
						try {
							targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
						} catch (IndexOutOfBoundsException e) {
							// noop, assume the gm didn't know how many portals
							// there are
							c.getPlayer().dropMessage(5, "Invalid portal selected.");
						} catch (NumberFormatException a) {
							// noop, assume that the gm is drunk
						}
					}
					if (targetPortal == null) {
						targetPortal = target.getPortal(0);
					}
					c.getPlayer().changeMap(target, targetPortal);
				} else {
					victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
					c.getPlayer().dropMessage(6, "Cross changing channel. Please wait.");
					if (victim.getMapId() != c.getPlayer().getMapId()) {
						final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
						c.getPlayer().changeMap(mapp, mapp.findClosestPortal(victim.getTruePosition()));
					}
					c.getPlayer().changeChannel(ch);
				}
			} catch (NumberFormatException e) {
				c.getPlayer().dropMessage(6, "Something went wrong " + e.getMessage());
				return 0;
			}
			return 1;
		}
	}

	public static class Say extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			if (splitted.length > 1) {
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				if (!c.getPlayer().isGM()) {
					sb.append("Intern ");
				}
				sb.append(c.getPlayer().getName());
				sb.append("] ");
				sb.append(StringUtil.joinStringFrom(splitted, 1));
				World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(c.getPlayer().isGM() ? 6 : 5, sb.toString()));
			} else {
				c.getPlayer().dropMessage(6, "Syntax: say <message>");
				return 0;
			}
			return 1;
		}
	}

	public static class Find extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			if (splitted.length == 1) {
				c.getPlayer().dropMessage(6,
						splitted[0] + ": <NPC> <MOB> <ITEM> <MAP> <SKILL> <QUEST> <HEADER/OPCODE>");
			} else if (splitted.length == 2) {
				c.getPlayer().dropMessage(6, "Provide something to search.");
			} else {
				String type = splitted[1];
				String search = StringUtil.joinStringFrom(splitted, 2);
				MapleData data;
				MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider("String.wz");
				StringBuilder sb = new StringBuilder();
				sb.append("<<" + "Type: ").append(type).append(" | " + "Search: ").append(search).append(">>");

				if (type.equalsIgnoreCase("NPC")) {
					List<String> retNpcs = new ArrayList<>();
					data = dataProvider.getData("Npc.img");
					List<Pair<Integer, String>> npcPairList = new LinkedList<>();
					for (MapleData npcIdData : data.getChildren()) {
						npcPairList.add(new Pair<>(Integer.parseInt(npcIdData.getName()),
								MapleDataTool.getString(npcIdData.getChildByPath("name"), "NO-NAME")));
					}
					for (Pair<Integer, String> npcPair : npcPairList) {
						if (npcPair.getRight().toLowerCase().contains(search.toLowerCase())) {
							retNpcs.add("\r\n" + npcPair.getLeft() + " - " + npcPair.getRight());
						}
					}
					if (retNpcs != null && retNpcs.size() > 0) {
						for (String singleRetNpc : retNpcs) {
							if (sb.length() > 10000) {
								sb.append("\r\nThere were too many results, and could not display all of them.");
								break;
							}
//							sb.append(singleRetNpc.toString());
							// c.getSession().write(NPCPacket.getNPCTalk(9010000,
							// (byte) 0, retNpcs.toString(), "00 00", (byte) 0,
							// 9010000));
							 c.getPlayer().dropMessage(6, singleRetNpc);
						}
					} else {
						c.getPlayer().dropMessage(6, "No NPC's Found");
					}

				} else if (type.equalsIgnoreCase("MAP")) {
					List<String> retMaps = new ArrayList<>();
					data = dataProvider.getData("Map.img");
					List<Pair<Integer, String>> mapPairList = new LinkedList<>();
					for (MapleData mapAreaData : data.getChildren()) {
						for (MapleData mapIdData : mapAreaData.getChildren()) {
							mapPairList.add(new Pair<>(Integer.parseInt(mapIdData.getName()),
									MapleDataTool.getString(mapIdData.getChildByPath("streetName"), "NO-NAME") + " - "
											+ MapleDataTool.getString(mapIdData.getChildByPath("mapName"), "NO-NAME")));
						}
					}
					for (Pair<Integer, String> mapPair : mapPairList) {
						if (mapPair.getRight().toLowerCase().contains(search.toLowerCase())) {
							retMaps.add("\r\n" + mapPair.getLeft() + " - " + mapPair.getRight());
						}
					}
					if (retMaps != null && retMaps.size() > 0) {
						for (String singleRetMap : retMaps) {
							if (sb.length() > 10000) {
								sb.append("\r\nThere were too many results, and could not display all of them.");
								break;
							}
//							sb.append(singleRetMap);
							// c.getSession().write(NPCPacket.getNPCTalk(9010000,
							// (byte) 0, retMaps.toString(), "00 00", (byte) 0,
							// 9010000));
							 c.getPlayer().dropMessage(6, singleRetMap);
						}
					} else {
						c.getPlayer().dropMessage(6, "No Maps Found");
					}
				} else if (type.equalsIgnoreCase("MOB")) {
					List<String> retMobs = new ArrayList<>();
					data = dataProvider.getData("Mob.img");
					List<Pair<Integer, String>> mobPairList = new LinkedList<>();
					for (MapleData mobIdData : data.getChildren()) {
						mobPairList.add(new Pair<>(Integer.parseInt(mobIdData.getName()),
								MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME")));
					}
					for (Pair<Integer, String> mobPair : mobPairList) {
						if (mobPair.getRight().toLowerCase().contains(search.toLowerCase())) {
							retMobs.add("\r\n" + mobPair.getLeft() + " - " + mobPair.getRight());
						}
					}
					if (retMobs != null && retMobs.size() > 0) {
						for (String singleRetMob : retMobs) {
							if (sb.length() > 10000) {
								sb.append("\r\nThere were too many results, and could not display all of them.");
								break;
							}
//							sb.append(singleRetMob);
							// c.getSession().write(NPCPacket.getNPCTalk(9010000,
							// (byte) 0, retMobs.toString(), "00 00", (byte) 0,
							// 9010000));
							 c.getPlayer().dropMessage(6, singleRetMob);
						}
					} else {
						c.getPlayer().dropMessage(6, "No Mobs Found");
					}

				} else if (type.equalsIgnoreCase("ITEM")) {
					List<String> retItems = new ArrayList<>();
					for (ItemInformation itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
						if (itemPair != null && itemPair.name != null
								&& itemPair.name.toLowerCase().contains(search.toLowerCase())) {
							retItems.add("\r\n" + itemPair.itemId + " - " + itemPair.name);
						}
					}
					if (retItems != null && retItems.size() > 0) {
						for (String singleRetItem : retItems) {
							if (sb.length() > 10000) {
								sb.append("\r\nThere were too many results, and could not display all of them.");
								break;
							}
//							sb.append(singleRetItem);
							// c.getSession().write(NPCPacket.getNPCTalk(9010000,
							// (byte) 0, retItems.toString(), "00 00", (byte) 0,
							// 9010000));
							 c.getPlayer().dropMessage(6, singleRetItem);
						}
					} else {
						c.getPlayer().dropMessage(6, "No Items Found");
					}
				} else if (type.equalsIgnoreCase("QUEST")) {
					List<String> retQuests = new ArrayList<>();
					for (MapleQuest questPair : MapleQuest.getAllInstances()) {
						if (questPair.getName().length() > 0
								&& questPair.getName().toLowerCase().contains(search.toLowerCase())) {
							retQuests.add("\r\n" + questPair.getId() + " - " + questPair.getName());
						}
					}
					if (retQuests != null && retQuests.size() > 0) {
						for (String singleRetQuest : retQuests) {
							if (sb.length() > 10000) {
								sb.append("\r\nThere were too many results, and could not display all of them.");
								break;
							}
//							sb.append(singleRetQuest);
							// c.getSession().write(NPCPacket.getNPCTalk(9010000,
							// (byte) 0, retQuests.toString(), "00 00", (byte)
							// 0, 9010000));
							 c.getPlayer().dropMessage(6, singleRetQuest);
						}
					} else {
						c.getPlayer().dropMessage(6, "No Quests Found");
					}
				} else if (type.equalsIgnoreCase("SKILL")) {
					List<String> retSkills = new ArrayList<>();
					for (Skill skill : SkillFactory.getAllSkills()) {
						if (skill.getName() != null && skill.getName().toLowerCase().contains(search.toLowerCase())) {
							retSkills.add("\r\n" + skill.getId() + " - " + skill.getName());
						}
					}
					if (retSkills != null && retSkills.size() > 0) {
						for (String singleRetSkill : retSkills) {
							if (sb.length() > 10000) {
								sb.append("\r\nThere were too many results, and could not display all of them.");
								break;
							}
//							sb.append(singleRetSkill);
							// c.getSession().write(NPCPacket.getNPCTalk(9010000,
							// (byte) 0, retSkills.toString(), "00 00", (byte)
							// 0, 9010000));
							 c.getPlayer().dropMessage(6, singleRetSkill);
						}
					} else {
						c.getPlayer().dropMessage(6, "No Skills Found");
					}
				} else if (type.equalsIgnoreCase("HEADER") || type.equalsIgnoreCase("OPCODE")) {
					List<String> headers = new ArrayList<>();
					headers.add("\r\nSend Opcodes:");
					for (SendPacketOpcode send : SendPacketOpcode.values()) {
						if (send.name() != null && send.name().toLowerCase().contains(search.toLowerCase())) {
							headers.add("\r\n" + send.name() + " Value: " + send.getValue() + " Hex: "
									+ HexTool.getOpcodeToString(send.getValue()));
						}
					}
					headers.add("\r\nRecv Opcodes:");
					for (RecvPacketOpcode recv : RecvPacketOpcode.values()) {
						if (recv.name() != null && recv.name().toLowerCase().contains(search.toLowerCase())) {
							headers.add("\r\n" + recv.name() + " Value: " + recv.getValue() + " Hex: "
									+ HexTool.getOpcodeToString(recv.getValue()));
						}
					}
					for (String header : headers) {
						if (sb.length() > 10000) {
							sb.append("\r\nThere were too many results, and could not display all of them.");
							break;
						}
//						sb.append(header);
						// c.getSession().write(NPCPacket.getNPCTalk(9010000,
						// (byte) 0, headers.toString(), "00 00", (byte) 0,
						// 9010000));
						 c.getPlayer().dropMessage(6, header);
					}
				} else {
					c.getPlayer().dropMessage(6, "Sorry, that search call is unavailable");
				}
				
				NPCTalk talk = new NPCTalk((byte) 4, 9010000, (byte) 0);
				talk.setText(sb.toString());
				
//				c.getSession().write(NPCPacket.getNPCTalk(talk));
			}
			return 0;
		}
	}

	public static class ID extends Find {
	}

	public static class LookUp extends Find {
	}

	public static class Search extends Find {
	}

	public static class WhosFirst extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			List<Pair<String, Long>> players = new ArrayList<>();
			for (MapleCharacter chr : c.getPlayer().getMap().getCharacters()) {
				if (!chr.isIntern()) {
					players.add(new Pair<>(MapleCharacterUtil.makeMapleReadable(chr.getName()), chr.getChangeTime()));
				}
			}
			Collections.sort(players, new WhoComparator());
			StringBuilder sb = new StringBuilder("List of people in this map in order, counting AFK (10 minutes): ");
			for (Pair<String, Long> z : players) {
				sb.append(z.left).append(", ");
			}
			c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
			return 0;
		}

		public static class WhoComparator implements Comparator<Pair<String, Long>>, Serializable {

			@Override
			public int compare(Pair<String, Long> o1, Pair<String, Long> o2) {
				if (o1.right > o2.right) {
					return 1;
				} else if (Objects.equals(o1.right, o2.right)) {
					return 0;
				} else {
					return -1;
				}
			}
		}
	}

	public static class WhosLast extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			if (splitted.length < 2) {
				StringBuilder sb = new StringBuilder("whoslast [type] where type can be:  ");
				for (MapleSquadType t : MapleSquadType.values()) {
					sb.append(t.name()).append(", ");
				}
				c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
				return 0;
			}
			final MapleSquadType t = MapleSquadType.valueOf(splitted[1].toLowerCase());
			if (t == null) {
				StringBuilder sb = new StringBuilder("whoslast [type] where type can be:  ");
				for (MapleSquadType z : MapleSquadType.values()) {
					sb.append(z.name()).append(", ");
				}
				c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
				return 0;
			}
			if (t.queuedPlayers.get(c.getChannel()) == null) {
				c.getPlayer().dropMessage(6, "The queue has not been initialized in this channel yet.");
				return 0;
			}
			c.getPlayer().dropMessage(6, "Queued players: " + t.queuedPlayers.get(c.getChannel()).size());
			StringBuilder sb = new StringBuilder("List of participants:  ");
			for (Pair<String, String> z : t.queuedPlayers.get(c.getChannel())) {
				sb.append(z.left).append('(').append(z.right).append(')').append(", ");
			}
			c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
			return 0;
		}
	}

	public static class WhosNext extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			if (splitted.length < 2) {
				StringBuilder sb = new StringBuilder("whosnext [type] where type can be:  ");
				for (MapleSquadType t : MapleSquadType.values()) {
					sb.append(t.name()).append(", ");
				}
				c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
				return 0;
			}
			final MapleSquadType t = MapleSquadType.valueOf(splitted[1].toLowerCase());
			if (t == null) {
				StringBuilder sb = new StringBuilder("whosnext [type] where type can be:  ");
				for (MapleSquadType z : MapleSquadType.values()) {
					sb.append(z.name()).append(", ");
				}
				c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
				return 0;
			}
			if (t.queue.get(c.getChannel()) == null) {
				c.getPlayer().dropMessage(6, "The queue has not been initialized in this channel yet.");
				return 0;
			}
			c.getPlayer().dropMessage(6, "Queued players: " + t.queue.get(c.getChannel()).size());
			StringBuilder sb = new StringBuilder("List of participants:  ");
			final long now = System.currentTimeMillis();
			for (Pair<String, Long> z : t.queue.get(c.getChannel())) {
				sb.append(z.left).append('(').append(StringUtil.getReadableMillis(z.right, now)).append(" ago),");
			}
			c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
			return 0;
		}
	}

	public static class ItemVac extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			final List<MapleMapObject> items = c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(),
					GameConstants.maxViewRangeSq(), Arrays.asList(MapleMapObjectType.ITEM));
			MapleMapItem mapitem;
			for (MapleMapObject item : items) {
				mapitem = (MapleMapItem) item;
				if (mapitem.getMeso() > 0) {
					c.getPlayer().gainMeso(mapitem.getMeso(), true);
				} else if (mapitem.getItem() == null
						|| !MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true)) {
					continue;
				}
				mapitem.setPickedUp(true);
				c.getPlayer().getMap().broadcastMessage(
						CField.removeItemFromMap(mapitem.getObjectId(), 2, c.getPlayer().getId()),
						mapitem.getPosition());
				c.getPlayer().getMap().removeMapObject(item);

			}
			return 1;
		}
	}

	public static class CancelBuffs extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.getPlayer().cancelAllBuffs();
			return 1;
		}
	}

	public static class CC extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.getPlayer().changeChannel(Integer.parseInt(splitted[1]));
			return 1;
		}
	}

	public static class FakeRelog extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.getPlayer().fakeRelog();
			return 1;
		}
	}

	public static class Fly extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			SkillFactory.getSkill(1146).getEffect(1).applyTo(c.getPlayer());
			SkillFactory.getSkill(1142).getEffect(1).applyTo(c.getPlayer());
			c.getPlayer().dispelBuff(1146);
			return 1;
		}
	}

	public static class OpenNpc extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			NPCScriptManager.getInstance().start(c, Integer.parseInt(splitted[1]),
					splitted.length > 2 ? StringUtil.joinStringFrom(splitted, 2) : splitted[1]);
			return 1;
		}
	}

	public static class OpenShop extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			MapleShopFactory.getInstance().getShop(Integer.parseInt(splitted[1]));
			return 1;
		}
	}

	public static class Shop extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			MapleShopFactory shop = MapleShopFactory.getInstance();
			int shopId = Integer.parseInt(splitted[1]);
			if (shop.getShop(shopId) != null) {
				shop.getShop(shopId).sendShop(c);
			}
			return 1;
		}
	}

	public static class Song extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.getPlayer().getMap().broadcastMessage(CField.musicChange(splitted[1]));
			return 1;
		}
	}

	public static class ClearInv extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			MapleCharacter player = c.getPlayer();
			if (splitted.length < 2 || player.hasBlockedInventory()) {
				c.getPlayer().dropMessage(5, "!clearinv <eq / use / setup / etc / cash / all >");
				return 0;
			} else {
				MapleInventoryType type;
				if (splitted[1].equalsIgnoreCase("eq")) {
					type = MapleInventoryType.EQUIP;
				} else if (splitted[1].equalsIgnoreCase("use")) {
					type = MapleInventoryType.USE;
				} else if (splitted[1].equalsIgnoreCase("setup")) {
					type = MapleInventoryType.SETUP;
				} else if (splitted[1].equalsIgnoreCase("etc")) {
					type = MapleInventoryType.ETC;
				} else if (splitted[1].equalsIgnoreCase("cash")) {
					type = MapleInventoryType.CASH;
				} else if (splitted[1].equalsIgnoreCase("all")) {
					type = null;
				} else {
					c.getPlayer().dropMessage(5, "Invalid. @clearslot <eq / use / setup / etc / cash / all >");
					return 0;
				}
				if (type == null) { // All, a bit hacky, but it's okay
					MapleInventoryType[] invs = { MapleInventoryType.EQUIP, MapleInventoryType.USE,
							MapleInventoryType.SETUP, MapleInventoryType.ETC, MapleInventoryType.CASH };
					for (MapleInventoryType t : invs) {
						type = t;
						MapleInventory inv = c.getPlayer().getInventory(type);
						byte start = -1;
						for (byte i = 0; i < inv.getSlotLimit(); i++) {
							if (inv.getItem(i) != null) {
								start = i;
								break;
							}
						}
						if (start == -1) {
							c.getPlayer().dropMessage(5, "There are no items in that inventory.");
							return 0;
						}
						int end = 0;
						for (byte i = start; i < inv.getSlotLimit(); i++) {
							if (inv.getItem(i) != null) {
								MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(),
										true);
							} else {
								end = i;
								break;// Break at first empty space.
							}
						}
						c.getPlayer().dropMessage(5, "Cleared slots " + start + " to " + end + ".");
					}
				} else {
					MapleInventory inv = c.getPlayer().getInventory(type);
					byte start = -1;
					for (byte i = 0; i < inv.getSlotLimit(); i++) {
						if (inv.getItem(i) != null) {
							start = i;
							break;
						}
					}
					if (start == -1) {
						c.getPlayer().dropMessage(5, "There are no items in that inventory.");
						return 0;
					}
					byte end = 0;
					for (byte i = start; i < inv.getSlotLimit(); i++) {
						if (inv.getItem(i) != null) {
							MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
						} else {
							end = i;
							break;// Break at first empty space.
						}
					}
					c.getPlayer().dropMessage(5, "Cleared slots " + start + " to " + end + ".");
				}
				return 1;
			}
		}
	}

	public static class Bob extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			MapleMonster mob = MapleLifeFactory.getMonster(9400551);
			for (int i = 0; i < 10; i++) {
				c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
			}
			return 1;
		}
	}

	public static class StartAutoEvent extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			final EventManager em = c.getChannelServer().getEventSM().getEventManager("AutomatedEvent");
			if (em != null) {
				em.setWorldEvent();
				em.scheduleRandomEvent();
				System.out.println("Scheduling Random Automated Event.");
			} else {
				System.out.println("Could not locate Automated Event script.");
			}
			return 1;
		}
	}

	public static class CloneMe extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.getPlayer().cloneLook();
			return 1;
		}
	}

	public static class DisposeClones extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			c.getPlayer().dropMessage(6, c.getPlayer().getCloneSize() + " clones disposed.");
			c.getPlayer().disposeClones();
			return 1;
		}
	}

	public static class ChatType extends CommandExecute {

		@Override
		public int execute(MapleClient c, String[] splitted) {
			try {
				c.getPlayer().setChatType(c.getPlayer().getChatType() == 0 ? (short) 11 : 0);
				c.getPlayer().dropMessage(0, "Text colour has successfully changed.");
			} catch (Exception e) {
				c.getPlayer().dropMessage(0, "An error has occured.");
			}
			return 1;
		}
	}
}
