/**
 * @author: Eric
 * @npc: ????
 * @func: Development's Custom PvP Clan System
 * @todo: Implement "Leaving"
*/

var status = 0;
var menu = 0;
var kickedPlayer = 0;

function start() {
	if (cm.getPlayer().warning[12] == true && cm.getPlayer().master > 0) {
		status = 9;
		menu = -1;
		action(1, 0, 0);
	} else {
		status = -1;
		menu = -1;
		action(1, 0, 0);
	}
}

function action(mode, type, selection) {
	if (mode == -1 || mode == 0) {
		if ((status == 10) && cm.getPlayer().warning[12] == true) {
			var clanLeader = cm.getPlayer().getClient().getChannelServer().getPlayerStorage().getCharacterById(cm.getPlayer().master);
			cleanLeader.dropMessage(5, cm.getPlayer().getName() + " has denied your request.");
			cm.getPlayer().warning[12] = false;
			cm.getPlayer().master = 0;
			cm.sendOk("You have declined the invitation.");
		}
        cm.dispose();
        return;
    } else if (mode == 1) {
        status++;
    }
	if (status == 0) {
		if (cm.getPlayer().getClanId() < 1) {
			cm.sendSimple("#r< #eWelcome to the Clan System!#n >#k\r\nIt seems you're not in a Clan. What would you like to do?#b\r\n#L0#Create a Clan#l\r\n#L1#Clan Leaderboards#k#l"); 
		} else if (cm.getPlayer().getClanId() > 0 && cm.getPlayer().getId() == cm.getClanLeader()) {
			cm.sendSimple("#r" + ((cm.getClanMessage() == "" || cm.getClanMessage() == "null") ? "                  < #eWelcome to the Clan System!#n >" : "< #e" + cm.getClanMessage() + "#n >") + "#k\r\nHello, #h #, and welcome to #e" + cm.getClanName() + "#n's Clan Leader Menu!#b\r\n#L100#Change Clan Message#l\r\n#L101#Invite a player to #e" + cm.getClanName() + "#n\r\n#L102#Kick a player from #e" + cm.getClanName() + "#n\r\n#L103#View #e" + cm.getClanName() + "#n's Roster\r\n#L104#Change #e" + cm.getClanName() + "#n's Clantag\r\n#L1#Clan Leaderboards#k#l");
		} else {
			cm.sendSimple("#r" + ((cm.getClanMessage() == "" || cm.getClanMessage() == "null") ? "                  < #eWelcome to the Clan System!#n >" : "< #e" + cm.getClanMessage() + "#n >") + "#k\r\n");
		}
	} else if (status == 1) {
		if (selection == 0) {
			if (cm.haveItem(4000999, 750) && cm.getPlayer().getLevel() >= 150) {
				cm.sendGetText("Enter the name of the clan you wish to create!\r\n");
				menu = 1;
			} else {
				cm.sendOk("Requirements to Create a Clan have #e#rNOT#k#n been met!\r\n\r\n"
				+ (cm.getPlayer().getLevel() >= 150 ? "#g" : "#r") + "* Reach level 150 or higher#k\r\n"
				+ (cm.haveItem(4000999, 750) ? "#g" : "#r") + "* Acquired 750 #i4000999:##k\r\n");
				cm.dispose();
			}
		} else if (selection == 1) {
			cm.sendOk("#eOfficial Clan Leaderboard Rankings#n : \r\n" + cm.getClanRanks());
			//"#e1.#n #rFaZe#k\r\nClan Level : 1 | Clan Members : 1 | Wins : 14,397,012\r\n#e2.#n #rOpTic#k\r\nClan Level : 1 | Clan Members : 0 | Wins : 13,337\r\n#e3.#n #rSoaR#k\r\nClan Level : 1 | Clan Members : 0 | Wins : 666\r\n#e4.#n #rObey#k\r\nClan Level : 1 | Clan Members : 0 | Wins : 9,999\r\n#e5.#n #rSynergy#k\r\nClan Level : 1 | Clan Members : 0 | Wins : 1,234");
			cm.dispose();
		} else if (selection == 100) {
			menu = 100;
			cm.sendGetText("Enter the desired Clan Message below.\r\n#rNote: You may type ' #e/none#n ' for no changes.#k");
		} else if (selection == 101) {
			menu = 101;
			cm.sendGetText("Enter the #eIGN#n of the user you wish to invite to #e" + cm.getClanName() + "#n.\r\n");
		} else if (selection == 102) {
			menu = 102;
			cm.sendSimple(cm.getClanKickMenu()); 
			//"#e" + cm.getClanName() + "#n's Memberlist :\r\nWho do you wish to kick from #e" + cm.getClanName() + "#n?#b\r\n#L0#<3\r\n#L1#Kevin\r\n#L2#Paul\r\n#L3#.pulse\r\n#L4#Eric\r\n#L5#Republic");
		} else if (selection == 103) {
			cm.sendNext("#e" + cm.getClanName() + "#n's Roster :#b\r\n" + cm.getClanRoster());
			cm.dispose();
		} else if (selection == 104) {
			menu = 104;
			cm.sendGetText("#r< Current Clantag : #e" + cm.getClantag() + "#n >#k\r\nChoose a #eClan Tag#n (a prefix before your name) for your clan to use!\r\n");
		} else {
			cm.dispose();
		}
	} else if (status == 2) {
		var textTransfer = cm.getText();
		if (menu == 1) {
			cm.gainItem(4000999, -750);
			cm.sendOk("Your clan, #e" + textTransfer + "#n, is now in effect.\r\nInvite other players to join you and battle against other clans for justice and rank!");
			cm.createClan(textTransfer);
			cm.getPlayer().setClanId(cm.getClanIdByName(textTransfer));
			cm.dispose();
		} else if (menu == 100) {
			if (textTransfer.length() < 40) {
				if (textTransfer.equalsIgnoreCase("/none")) {
					cm.sendOk("No changes have been made to your #eClan Message#n.");
					cm.dispose();
					return;
				}
				cm.sendOk("You've changed your #eClan Message#n to the following: \r\n#r< #e" + textTransfer + "#n >#k");
				cm.setClanMessage(textTransfer);
				cm.dispose();
			} else {
				cm.sendOk("Your #eClan Message#n may only reach 20 characters.");
				cm.dispose();
			}
		} else if (menu == 101) {
			if (textTransfer.length() <= 12) {
				var player = cm.getPlayer().getClient().getChannelServer().getPlayerStorage().getCharacterByName(textTransfer);
				if (player != null && player.warning[12] == false && player.master == 0 && player.getClanId() <= 0 && cm.getPlayer() != player) {
					player.warning[12] = true;
					player.master = cm.getPlayer().getId(); // master is useless variable, might as well make use of it
					cm.openNpc(player.getClient(), 2180001);
					cm.sendOk("You've successfully sent a #bClan Invitation#k to #e" + player.getName() + "#n!");
				} else {
					cm.sendOk("We're unable to locate #e" + textTransfer + "#n. Please try again.");
					cm.dispose();
				}
			} else {
				cm.sendOk("We're unable to locate #e" + textTransfer + "#n. Please try again.");
				cm.dispose();
			}
		} else if (menu == 102) {
			status = 4;
			kickedPlayer = selection;
			cm.sendYesNo("Are you sure you want to kick #e" + Packages.client.MapleCharacter.getNameById(kickedPlayer) + "#n from #e" + cm.getClanName() + "#n?");
		} else if (menu == 104) {
			if (textTransfer.length() <= 4) {
				cm.sendOk("You've changed your #eClantag#n to the following: #r#e" + textTransfer + "#n#k.");
				cm.setClantag(textTransfer);
				cm.dispose();
			} else {
				cm.sendOk("A #eClan Tag#n can have no more than 5 characters.");
				cm.dispose();
			}
		}
	} else if (status == 5) {
		cm.kickPlayerFromClan(kickedPlayer);
		cm.sendOk("You've successfully kicked #e" + Packages.client.MapleCharacter.getNameById(kickedPlayer) + "#n from #e" + cm.getClanName() + "#n.");
		cm.dispose();
	} else if (status == 10) {
		cm.sendAcceptDecline(cm.getClanRequest(cm.getPlayer().getClient().getChannelServer().getPlayerStorage().getCharacterById(cm.getPlayer().master)));
	} else if (status == 11) {
		if (mode > 0) {
			var newLeader = cm.getPlayer().getClient().getChannelServer().getPlayerStorage().getCharacterById(cm.getPlayer().master);
			cm.getPlayer().joinClan(newLeader.getClanId());
			cm.sendOk("You have joined #e" + cm.getClanNameNonStatic(newLeader.getClanId()) + "#n. Congrats!");
			cm.getPlayer().warning[12] = false;
			cm.getPlayer().master = 0;
			cm.dispose();
		}
	}
}