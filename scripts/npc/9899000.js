itemSasuke = [1302000, 1302000]; //items for sasuke or cursed sasuke(rewards)
itemKirito = [5000241]; // Kirito Pet
itemAsuna = [5000240]; // Asuna Pet
itemKyuubiNaruto = [1302000, 1302000]; // kyuubi naruto items
itemRequired = [4007097, 4007096]; //, 4007100, 4007101]; // Anime Boxes
var status = 0; // Because of the fact we'll be using selections :o
var itemType = -1; // This is declaring the "selection" because selection isn't final for some reason

function start() { 
	status = -1;
	action(1, 0, 0);
} 

function action(mode, type, selection) { 
	if (mode == 1)
		status++;
	else if (status >= 1 && mode == 0) {
		cm.sendOk("No? Talk to me when you're willing to #eredeem#n your #ranime box#k.");
		cm.dispose();
		return;
	} else
		status--;
	if (status == 0) {
		menu = "Oh hey there, I can trade #rAnime boxes#k for their items inside!\r\n#rAnime boxes#k look like this:\r\n\r\n";
		for (var i = 0; i < itemRequired.length; menu += "#v" + itemRequired[i] + "#", i++); 
		menu += "\r\n\r\n#L0#Redeem #bCursed Sasuke Box#k\r\n#L1#Redeem #bKyuubi Naruto Box#k"; //\r\n#L2#Redeem #bKirito Box#k\r\n#L3#Redeem #bAsuna Box#k";
		cm.sendSimple(menu); 
	} else if (status == 1) {
		if (selection == 0) {
			if (cm.haveItem(itemRequired[selection], 1)) {
				cm.sendAcceptDecline("Nice job! It seems you have the #bCursed Sasuke Box#k!\r\n\r\nAre you ready to redeem your #eprize#n?");
			} else {
				cm.sendOk("You don't have a #bCursed Sasuke Box#k.");
				cm.dispose();
			}
		} else if (selection == 1) {
			if (cm.haveItem(itemRequired[selection], 1)) {
				itemType = 1;
				cm.sendAcceptDecline("Nice job! It seems you have the #bKyuubi Naruto Box#k!\r\n\r\nAre you ready to redeem your #eprize#n?");
			} else {
				cm.sendOk("You don't have a #bKyuubi Naruto Box#k");
				cm.dispose();
			}
		} else if (selection == 2) {
			if (cm.haveItem(itemRequired[selection], 1)) {
				cm.sendAcceptDecline("Nice job! It seems you have the #bKirito Box#k!\r\n\r\nAre you ready to redeem your #eprize#n?");
			} else {
				cm.sendOk("You don't have a #bKirito Box#k.");
				cm.dispose();
			}
		} else if (selection == 3) {
			if (cm.haveItem(itemRequired[selection], 1)) {
				cm.sendAcceptDecline("Nice job! It seems you have the #bAsuna Box#k!\r\n\r\nAre you ready to redeem your #eprize#n?");
			} else {
				cm.sendOk("You don't have a #bAsuna Box#k.");
				cm.dispose();
			}
		}
	} else if (status == 2) {
		if (mode > 0) {
			// cm.gainItem(itemRequired[selection], -1);
			// cm.gainItem((itemType == 0 ? itemSasuke[0] : itemType == 1 ? itemKyuubiNaruto[0] : itemType == 2 ? itemKirito[0] : itemType == 3 ? itemAsuna[0] : 0), 1);
			cm.sendOk("Everything looks good! Your #ranime box#k has been #eredeemed#n.\r\n\r\nHere's your very own #b#t" + (itemType == 0 ? itemSasuke[0] : itemType == 1 ? itemKyuubiNaruto[0] : itemType == 2 ? itemKirito[0] : itemType == 3 ? itemAsuna[0] : 0) + "##k!");
			cm.dispose();
		}
	}
}  