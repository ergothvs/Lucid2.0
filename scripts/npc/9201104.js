/**
 * @author: Eric
 * @func: Map-wide PvP-state changer. 
 * @desc: Change the state of PvP. (e.g From Free-for-all to Team Deathmatch)
 * @npc: Sage
*/
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
		cm.sendSimple("Well? Do you wish to change the #ePvP Game Mode#n?\r\nYou can #rphysically#k modify it, for a #dprice#k..\r\n\r\n#L0##bGame Mode - #eFree-For-All#n | Price : 10 Wiz Coins#k\r\n#L1##rGame Mode - #eSurvival#n | Price : 10 Wiz Coins\r\n#L2##bGame Mode - #eGuild vs Guild#n | Price : 10 Wiz Coins#k\r\n#L3##bGame Mode - #e(Skin) Race vs. Race#n | Price : 20 Wiz Coins#k\r\n#L4##bGame Mode - #eocc#n | Price : 10 Wiz Coins#k\r\n#L5##rGame Mode - #ejob#n | Price : 10 Wiz Coins#k\r\n#L6##bGame Mode - #egender#n | Price : 10 Wiz Coins#k");
	} else if (status == 1) {
		if (selection == 0) {
			cm.sendAcceptDecline("#eGame Mode - Free-For-All#n.");
		} else if (selection == 1) {
			cm.sendAcceptDecline("#eGame Mode - Survival#n.");
		} else if (selection == 2) {
			cm.sendAcceptDecline("#eGame Mode - Guild vs. Guild#n.");
		} else if (selection == 3) {
			cm.sendAcceptDecline("#eGame Mode - (Skin) Race vs. Race#n.");
		} else if (selection == 4) {
			cm.sendAcceptDecline("#eGame Mode - Job vs. Job#n.");
		} else if (selection == 5) {
			cm.sendAcceptDecline("#eGame Mode - Occupation vs. Occupation#n.");
		} else if (selection == 6) {
			cm.sendAcceptDecline("#eGame Mode - Boys vs. Girls#n.");
		}
	} else if (status == 2) {
		if (mode > 0) {
			cm.sendOk("Well then, the #ePvP Game Mode#n has been #rupdated#k! #d#yolo#k");
			cm.dispose();
		} else
			cm.dispose();
	}
}