package handling.handlers.login;

import java.util.ArrayList;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConfig;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.cashshop.CashShopServer;
import handling.cashshop.handler.CashShopOperation;
import handling.channel.ChannelServer;
import handling.channel.handler.PlayersHandler;
import handling.login.LoginServer;
import handling.world.CharacterTransfer;
import handling.world.World;
import handling.world.guild.MapleGuild;
import server.MapleInventoryManipulator;
import server.quest.MapleQuest;
import server.quest.MapleQuestStatus;
import tools.Triple;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.GuildPacket;
import tools.packet.JobPacket.AvengerPacket;

public class PlayerLoggedInHandler {

	@PacketHandler(opcode = RecvPacketOpcode.PLAYER_LOGGEDIN)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		lea.readInt(); // this could be the world or account
		final int playerid = lea.readInt();

		MapleCharacter player;
		
		CharacterTransfer transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
		
		if (transfer != null) {
			// c.getSession().write(CWvsContext.BuffPacket.cancelBuff());
			CashShopOperation.EnterCS(transfer, c);
			return;
		}
		
		for (ChannelServer cserv : ChannelServer.getAllInstances()) {
			transfer = cserv.getPlayerStorage().getPendingCharacter(playerid);
			if (transfer != null) {
				c.setChannel(cserv.getChannel());
				break;
			}
		}

		if (transfer == null) { // player couldn't be found in the storage
			Triple<String, String, Integer> ip = LoginServer.getLoginAuth(playerid);
			
			String s = c.getSessionIPAddress();
			String ss = s.substring(s.indexOf('/') + 1, s.length());
			
			if (ip == null|| !s.substring(s.indexOf('/') + 1, s.length()).equals(ip.left)) {
				System.out.println("Player wasn't found in the storage.");
				c.getSession().close();
				return;
			}
			LoginServer.putLoginAuth(playerid, ip.left, ip.mid, ip.right);

			c.setTempIP(ip.mid);
			c.setChannel(ip.right);
			player = MapleCharacter.loadCharFromDB(playerid, c, true);
		} else {
			System.out.println("Reconstructing Character!");
			player = MapleCharacter.ReconstructChr(transfer, c, true);
		}
		
		final ChannelServer channelServer = c.getChannelServer();
		c.setPlayer(player);
		c.setAccID(player.getAccountID());

		// remote ip hack
		if (!c.CheckIPAddress()) {
			System.out.println("Remote hack detected (close session).");
			c.getSession().close();
			return;
		}
		
		final int state = c.getLoginState();
		
		boolean allowLogin = false;
		
		if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL || state == MapleClient.LOGIN_NOTLOGGEDIN) { 
			allowLogin = !World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()));
		}
		
		if (!allowLogin) {
			System.out.println("Error! (close session).");
			c.setPlayer(null);
			c.getSession().close();
			return;
		}
		
		c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
		channelServer.addPlayer(player);

		// player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()));
		// player.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(player.getId()));
		// player.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(player.getId()));

		// c.getSession().write(HexTool.getByteArrayFromHexString("18 01 00 00 FF 00 00 FF 00 00 FF 00 00 FF 00 00 FF"));
		
		c.getSession().write(CWvsContext.updateCrowns(new int[] { -1, -1, -1, -1, -1 }));
		c.getSession().write(CField.setField(player, null, 0, true));
		
		PlayersHandler.calcHyperSkillPointCount(c);
		// c.getSession().write(CSPacket.enableCSUse());
		//c.getSession().write(CWvsContext.updateLinkSkill(c.getPlayer().getSkills(), true, false, false));
		// player.getStolenSkills();
		// c.getSession().write(JobPacket.addStolenSkill());

		player.getMap().addPlayer(player);

		try {
			/*// Start of buddylist
			final int buddyIds[] = player.getBuddylist().getBuddyIds();
			World.Buddy.loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds);
			if (player.getParty() != null) {
				final MapleParty party = player.getParty();
				World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));

				if (party != null && party.getExpeditionId() > 0) {
					final MapleExpedition me = World.Party.getExped(party.getExpeditionId());
					if (me != null) {
						c.getSession().write(CWvsContext.ExpeditionPacket.expeditionStatus(me, false, true));
					}
				}
			}
			final CharacterIdChannelPair[] onlineBuddies = World.Find.multiBuddyFind(player.getId(), buddyIds);
			for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
				player.getBuddylist().get(onlineBuddy.getCharacterId()).setChannel(onlineBuddy.getChannel());
			}
			// c.getSession().write(BuddylistPacket.updateBuddylist(player.getBuddylist().getBuddies()));

			// Start of Messenger
			final MapleMessenger messenger = player.getMessenger();
			if (messenger != null) {
				World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
				World.Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
			}*/

			// Start of Guild and alliance
			if (player.getGuildId() > 0) {
				World.Guild.setGuildMemberOnline(player.getMGC(), true, c.getChannel());
				c.getSession().write(GuildPacket.showGuildInfo(player));
				final MapleGuild gs = World.Guild.getGuild(player.getGuildId());
				if (gs != null) {
					final List<byte[]> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
					if (packetList != null) {
						for (byte[] pack : packetList) {
							if (pack != null) {
								c.getSession().write(pack);
							}
						}
					}
				} else { // guild not found, change guild id
					player.setGuildId(0);
					player.setGuildRank((byte) 5);
					player.setAllianceRank((byte) 5);
					player.saveGuildStatus();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//player.getClient().getSession().write(CWvsContext.broadcastMsg(channelServer.getServerMessage()));
		player.sendMacros();
		// player.showNote();
		// player.sendImp();
		// player.updatePartyMemberHP();
		// player.startFairySchedule(false);
		// player.baseSkills(); // fix people who've lost skills.
		if (GameConstants.isZero(player.getJob())) {
			c.getSession().write(CWvsContext.updateLinkSkill(c.getPlayer().getSkills(), true, false, false));
		}
		c.getSession().write(CField.getKeymap(player.getKeyLayout()));
		// player.updatePetAuto();
		// player.expirationTask(true, transfer == null);
		// c.getSession().write(CWvsContext.updateMaplePoint(player.getCSPoints(2)));
		
		if (player.getJob() == 132) { // Dark Knight
			player.checkBerserk();
		}
		if (GameConstants.isXenon(player.getJob())) {
			player.startXenonSupply();
		}
		if (GameConstants.isDemonAvenger(player.getJob())) {
			c.getSession().write(AvengerPacket.giveAvengerHpBuff(player.getStat().getHp()));
		}
		// player.spawnClones();
		// player.spawnSavedPets();
		if (player.getStat().equippedSummon > 0) {
			// SkillFactory.getSkill(player.getStat().equippedSummon + (GameConstants.getBeginnerJob(player.getJob()) * 1000)).getEffect(1).applyTo(player);
		}
		MapleQuestStatus stat = player.getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
		// c.getSession().write(CWvsContext.pendantSlot(stat != null && stat.getCustomData() != null && Long.parseLong(stat.getCustomData()) > System.currentTimeMillis()));
		stat = player.getQuestNoAdd(MapleQuest.getInstance(GameConstants.QUICK_SLOT));
		/// c.getSession().write(CField.quickSlot(stat != null && stat.getCustomData() != null ? stat.getCustomData() : null));
		// c.getSession().write(CWvsContext.getFamiliarInfo(player));
		MapleInventory equipped = player.getInventory(MapleInventoryType.EQUIPPED);
		MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
		List<Short> slots = new ArrayList<>();
		for (Item item : equipped.newList()) {
			slots.add(item.getPosition());
		}
		for (short slot : slots) {
			if (GameConstants.isIllegalItem(equipped.getItem(slot).getItemId())) {
				MapleInventoryManipulator.removeFromSlot(player.getClient(), MapleInventoryType.EQUIPPED, slot, (short) 1, false);
			}
		}
		// hack to make skills update
		c.getSession().write(CWvsContext.updateLinkSkill(c.getPlayer().getSkills(), true, false, false));
		// c.getSession().write(CWvsContext.shopDiscount(ServerConstants.SET_POTION_DISCOUNT_RATE));
		// List<Pair<Integer, String>> npcs = new ArrayList<>();
		// npcs.add(new Pair<>(9070006, "Why...why has this happened to me?
		// My knightly honor... My knightly pride..."));
		// npcs.add(new Pair<>(9000021, "Are you enjoying the event?"));
		// c.getSession().write(NPCPacket.setNpcScriptable(npcs));
		// c.getSession().write(NPCPacket.setNPCScriptable());
		// player.updateReward();
		// player.setDeathCount(99);
		// c.getSession().write(CField.EffectPacket.updateDeathCount(99));
		// //for fun
		// player.getClient().getSession().write(CWvsContext.broadcastMsg(channelServer.getServerMessage()));
		if (c.getPlayer().getLevel() < 11 && ServerConfig.RED_EVENT_10) {
			// NPCScriptManager.getInstance().start(c, 9000108, "LoginTot");
		} else if (c.getPlayer().getLevel() > 10 && ServerConfig.RED_EVENT) {
			// NPCScriptManager.getInstance().start(c, 9000108, "LoginRed");
		}
	}
}
