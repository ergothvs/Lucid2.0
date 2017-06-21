importPackage(java.lang);

var status = -1;
var oldWepName;
var oldWepId;
var newWepId;
var newWepName;
var leaves;
var stimulator;
var cost;
var getNewWep;
var sel;
var reward_msg = "Here is your new, #ecool#n, stylish Maple Weapon.\r\n\r\nTreat it with care.";

function start() {
    cm.sendNext("Welcome to #bWizStory#k!!\r\nI'm #bWizStory's#k Head Coder, #rEric#k.\r\nChat me if you need anything!\r\nAlso, report any bugs to us so we can fix them.\r\n\r\n#gThanks!#k");
}

function action(mode, type, selection) {
    if (mode == 0) {
	cm.dispose();
	return;
    } else {
	status++;
    }
    if (status == 0) {
	   cm.sendGetText("Want to see something cool?\r\nEnter the secret message #eEric is cool#n to get a weapon!");
	   } else if (status == 1) {
	    if (cm.getText().equalsIgnoreCase("Eric is cool")) {
		  status = 1; 
		  cm.sendSimple("Oh, Im cool huh? Why, thank you! <3\r\nChoose the #elevel#n of the weapon you want!\r\n\r\n#b#L0#Level 35 Weapon#l\r\n#L1#Level 43 Weapon#l\r\n#L2#Level 64 Weapon#l\r\n#L4#Level 77 Weapon#l");
		 // cm.sendNext("Oh, I'm cool huh? Why, thank you! <3\r\nClick next for a weapon. :)");
		  } else {
		  if (cm.getPlayer().gmLevel() < 3) { // oh i'm such a troll. ;P
		    cm.getPlayer().dropMessage(1, "Okay, have it your way. :(");
		    cm.getPlayer().unequipEverything();
			cm.dispose();
			} else {
			cm.sendOk("#rWAAAAAAAAAAAAAAAAAH?!#k");
			cm.dispose();
			}
		  }
		  } else if (status == 2) {
		sel = selection;
	if (sel == 0) {
	    cm.sendSimple("\r\nChoose a #bLevel 35 Weapon :\r\n#L0#Maple Sword#l \r\n#b#L2#Maple Staff#l \r\n#b#L3#Maple Bow#l \r\n#b#L4#Maple Crow#l \r\n#b#L5#Maple Claw#l \r\n#b#L6#Maple Gun#l \r\n#b#L7#Maple Knuckle#l \r\n#b#L8#Maple Shield#l \r\n#b#L9#Maple Katara#l");
	} else if (sel == 2) {
	    cm.sendSimple("\r\nChoose a #bLevel 43 Weapon :\r\n#L0#Maple Glory Sword (One-Handed Sword)#l\r\n#L1#Maple Soul Rohen (Two-Handed Sword)#l\r\n#L2#Maple Steel Axe (One-Handed Axe)#l\r\n#L3#Maple Demon Axe (Two-Handed Axe)#l\r\n#L4#Maple Havoc Hammer (One-Handed Mace)#l\r\n#L5#Maple Belzet (Two-Handed Mace)#l\r\n#L6#Maple Kandiva Bow (Bow)#l\r\n#L7#Maple Nishada (Crossbow)#l\r\n#L8#Maple Skanda (Claw)#l\r\n#L9#Maple Asura Dagger (Dagger)#l\r\n#L10#Maple Dark Mate (Dagger)#l\r\n#L11#Maple Soul Spear (Spear)#l\r\n#L12#Maple Karstan (Polearm)#l\r\n#L13#Maple Shine Wand (Wand)#l\r\n#L14#Maple Wisdom Staff (Staff)#l\r\n#L15#Maple Golden Claw (Knuckler)#l\r\n#L16#Maple Cannon Shooter (Gun)#l\r\n#L17#Maple Warrior Shield (Warrior Shield)#l\r\n#L18#Maple Magician Shield (Magician Shield)#l\r\n#L19#Maple Thief Shield (Thief Shield)#l\r\n#L20#Maple Cleat Katara#l");
	} else if (sel == 1) {
	    cm.sendSimple("\r\nChoose a#bLevel 64 Weapon :\r\n#L0#Maple Soul Singer#l \r\n#b#L1#Maple Lama Staff#l \r\n#b#L2#Maple Dragon Axe#l \r\n#b#L3#Maple Doom Singer#l \r\n#b#L4#Maple Impaler#l \r\n#b#L5#Maple Scorpio#l \r\n#b#L6#Maple Soul Searcher#l \r\n#b#L7#Maple Crossbow#l \r\n#b#L8#Maple Kanduyo#l \r\n#b#L9#Maple Storm Pistol#l \r\n#b#L10#Maple Storm Finger#l \r\n#b#L11#Maple Duke Katara#l  \r\n#b#L12#Maple Wagner#l");
	} else if (sel == 4) {
	    cm.sendSimple("\r\nChoose a #bLevel 77 Weapon :\r\n#L0#Maple Pyrope Sword#l \r\n#b#L1#Maple Pyrope Axe#l \r\n#b#L2#Maple Pyrope Hammer#l \r\n#b#L3#Maple Pyrope Halfmoon#l \r\n#b#L4#Maple Pyrope Wand#l \r\n#b#L5#Maple Pyrope Staff#l \r\n#b#L6#Maple Pyrope Rohen#l \r\n#b#L7#Maple Pyrope Battle Axe#l \r\n#b#L8#Maple Pyrope Maul#l \r\n#b#L9#Maple Pyrope Spear#l \r\n#b#L10#Maple Pyrope Hellslayer#l \r\n#b#L11#Maple Pyrope Bow#l \r\n#b#L12#Maple Pyrope Crow#l \r\n#b#L13#Maple Pyrope Skanda#l \r\n#b#L14#Maple Pyrope Knuckle#l \r\n#b#L15#Maple Pyrope Shooter#l \r\n#b#L16#Maple Pyrope Katara#l");
	}
    } else if (status == 3) {
	if (sel == 0) {
	    if (selection == 0) {
		newWepName = "Maple Sword";
		newWepId = 1302020;
		leaves = 100;
		cost = 50000;
	    } else if (selection == 2) {
		newWepName = "Maple Staff";
		newWepId = 1382009;
		leaves = 100;
		cost = 50000;
	    } else if (selection == 3) {
		newWepName = "Maple Bow";
		newWepId = 1452016;
		leaves = 100;
		cost = 50000;
	    } else if (selection == 4) {
		newWepName = "Maple Crow";
		newWepId = 1462014;
		leaves = 100;
		cost = 50000;
	    } else if (selection == 5) {
		newWepName = "Maple Claw";
		newWepId = 1472030;
		leaves = 100;
		cost = 50000;
	    } else if (selection == 6) {
		newWepName = "Maple Gun";
		newWepId = 1492020;
		leaves = 100;
		cost = 50000;
	    } else if (selection == 7) {
		newWepName = "Maple Knuckle";
		newWepId = 1482020;
		leaves = 100;
		cost = 50000;
	    } else if (selection == 8) {
		newWepName = "Maple Shield";
		newWepId = 1092030;
		leaves = 100;
		cost = 50000;
	    } else if (selection == 9) {
		newWepName = "Maple Katara";
		newWepId = 1342025;
		leaves = 100;
		cost = 50000;
	    }
	    cm.sendYesNo("Are you sure you want a #b" + newWepName + "#k?");
	// 1482020
	} else if (sel == 2) {
	    if (selection == 0) {
		oldWepName = "Maple Soul Singer";
		oldWepId = 1302030;
		newWepName = "Maple Glory Sword";
		newWepId = 1302064;
		leaves = 100;
		cost = 300000;
		stimulator = 4130002;
	    } else if (selection == 1) {
		oldWepName = "Maple Soul Singer";
		oldWepId = 1302030;
		newWepName = "Maple Soul Rohen";
		newWepId = 1402039;
		leaves = 200;
		cost = 500000;
		stimulator = 4130005;
	    } else if (selection == 2) {
		oldWepName = "Maple Dragon Axe";
		oldWepId = 1412011;
		newWepName = "Maple Steel Axe";
		newWepId = 1312032;
		leaves = 100;
		cost = 300000;
		stimulator = 4130003;
	    } else if (selection == 3) {
		oldWepName = "Maple Dragon Axe";
		oldWepId = 1412011;
		newWepName = "Maple Demon Axe";
		newWepId = 1412027;
		leaves = 200;
		cost = 500000;
		stimulator = 4130006;
	    } else if (selection == 4) {
		oldWepName = "Maple Doom Singer";
		oldWepId = 1422014;
		newWepName = "Maple Havoc Hammer";
		newWepId = 1322054;
		leaves = 100;
		cost = 300000;
		stimulator = 4130004;
	    } else if (selection == 5) {
		oldWepName = "Maple Doom Singer";
		oldWepId = 1422014;
		newWepName = "Maple Belzet";
		newWepId = 1422029;
		leaves = 200;
		cost = 500000;
		stimulator = 4130007;
	    } else if (selection == 6) {
		oldWepName = "Maple Soul Searcher";
		oldWepId = 1452022;
		newWepName = "Maple Kandiva Bow";
		newWepId = 1452045;
		leaves = 200;
		cost = 500000;
		stimulator = 4130012;
	    } else if (selection == 7) {
		oldWepName = "Maple Crossbow";
		oldWepId = 1462019;
		newWepName = "Maple Nishada";
		newWepId = 1462040;
		leaves = 200;
		cost = 500000;
		stimulator = 4130013;
	    } else if (selection == 8) {
		oldWepName = "Maple Kandayo";
		oldWepId = 1472032;
		newWepName = "Maple Skanda";
		newWepId = 1472055;
		leaves = 200;
		cost = 500000;
		stimulator = 4130015;
	    } else if (selection == 9 || selection == 10) {
		oldWepName = "Maple Wagner";
		oldWepId = 1332025;
		if (selection == 9) {
		    newWepName = "Maple Asura Dagger";
		    newWepId = 1332056;
		} else {
		    newWepName = "Maple Dark Mate";
		    newWepId = 1332055;
		}
		leaves = 200;
		cost = 500000;
		stimulator = 4130014;
	    } else if (selection == 11) {
		oldWepName = "Maple Impaler";
		oldWepId = 1432012;
		newWepName = "Maple Soul Spear";
		newWepId = 1432040;
		leaves = 200;
		cost = 500000;
		stimulator = 4130008;
	    } else if (selection == 12) {
		oldWepName = "Maple Scorpio";
		oldWepId = 1442024;
		newWepName = "Maple Karstan";
		newWepId = 1442051;
		leaves = 200;
		cost = 500000;
		stimulator = 4130009;
	    } else if (selection == 13) {
		oldWepName = "Maple Lama Staff";
		oldWepId = 1382012;
		newWepName = "Maple Shine Wand";
		newWepId = 1372034;
		leaves = 200;
		cost = 500000;
		stimulator = 4130010;
	    } else if (selection == 14) {
		oldWepName = "Maple Lama Staff";
		oldWepId = 1382012;
		newWepName = "Maple Wisdom Staff";
		newWepId = 1382039;
		leaves = 200;
		cost = 500000;
		stimulator = 4130011;
	    } else if (selection == 15){
		oldWepName = "Maple Storm Finger";
		oldWepId = 1482021;
		newWepName = "Maple Golden Claw";
		newWepId = 1482022;
		leaves = 200;
		cost = 500000;
		stimulator = 4130016;
	    } else if (selection == 16) {
		oldWepName = "Maple Storm Pistol";
		oldWepId = 1492021;
		newWepName = "Maple Cannon Shooter";
		newWepId = 1492022;
		leaves = 200;
		cost = 500000;
		stimulator = 4130017;
	    } else if (selection == 17) {
		oldWepName = "Maple Shield";
		oldWepId = 1092030;
		newWepName = "Maple Warrior Shield";
		newWepId = 1092046;
		leaves = 200;
		cost = 500000;
	    } else if (selection == 18) {
		oldWepName = "Maple Shield";
		oldWepId = 1092030;
		newWepName = "Maple Magician Shield";
		newWepId = 1092045;
		leaves = 200;
		cost = 500000;
	    } else if (selection == 19) {
		oldWepName = "Maple Shield";
		oldWepId = 1092030;
		newWepName = "Maple Thief Shield";
		newWepId = 1092047;
		leaves = 200;
		cost = 500000;
	    } else if (selection == 20) {
		oldWepName = "Maple Duke Katara";
		oldWepId = 1342026;
		newWepName = "Maple Cleat Katara";
		newWepId = 1342027;
		leaves = 200;
		cost = 500000;
	    }
	    cm.sendYesNo("Are you sure you want a #b" + newWepName + "#k?");
	} else if (sel == 1) {
	    if (selection == 0) {
		newWepName = "Maple Soul Singer";
		newWepId = 1302030;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 1) {
		newWepName = "Maple Lama Staff";
		newWepId = 1382012;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 2) {
		newWepName = "Maple Dragon Axe";
		newWepId = 1412011;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 3) {
		newWepName = "Maple Doom Singer";
		newWepId = 1422014;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 4) {
		newWepName = "Maple Impaler";
		newWepId = 1432012;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 5) {
		newWepName = "Maple Scorpio";
		newWepId = 1442024;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 6) {
		newWepName = "Maple Soul Searcher";
		newWepId = 1452022;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 7) {
		newWepName = "Maple Crossbow";
		newWepId = 1462019;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 8) {
		newWepName = "Maple Kandayo";
		newWepId = 1472032;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 9) {
		newWepName = "Maple Storm Pistol";
		newWepId = 1492021;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 10) {
		newWepName = "Maple Storm Finger";
		newWepId = 1482021;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 11) {
		newWepName = "Maple Duke Katara";
		newWepId = 1342026;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 12) {
		newWepName = "Maple Wagner";
		newWepId = 1332025;
		leaves = 200;
		cost = 50000;
	    }
	    cm.sendYesNo("Are you sure you want a #b" + newWepName + "#k?");
	} else if (sel == 4) {
	    if (selection == 0) {
		oldWepName = "Maple Glory Sword";
		oldWepId = 1302064;
		newWepName = "Maple Pyrope Sword";
		newWepId = 1302142;
		leaves = 250;
		cost = 3000000;
		stimulator = 4130002;
	    } else if (selection == 6) {
		oldWepName = "Maple Soul Rohen";
		oldWepId = 1402039;
		newWepName = "Maple Pyrope Rohen";
		newWepId = 1402085;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130005;
	    } else if (selection == 1) {
		oldWepName = "Maple Steel Axe";
		oldWepId = 1312032;
		newWepName = "Maple Pyrope Axe";
		newWepId = 1312056;
		leaves = 250;
		cost = 3000000;
		stimulator = 4130003;
	    } else if (selection == 7) {
		oldWepName = "Maple Demon Axe";
		oldWepId = 1412027;
		newWepName = "Maple Pyrope Battle Axe";
		newWepId = 1412055;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130006;
	    } else if (selection == 2) {
		oldWepName = "Maple Havoc Hammer";
		oldWepId = 1322054;
		newWepName = "Maple Pyrope Hammer";
		newWepId = 1322084;
		leaves = 250;
		cost = 3000000;
		stimulator = 4130004;
	    } else if (selection == 8) {
		oldWepName = "Maple Belzet";
		oldWepId = 1422029;
		newWepName = "Maple Pyrope Maul";
		newWepId = 1422057;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130007;
	    } else if (selection == 11) {
		oldWepName = "Maple Kandiva Bow";
		oldWepId = 1452045;
		newWepName = "Maple Pyrope Bow";
		newWepId = 1452100;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130012;
	    } else if (selection == 12) {
		oldWepName = "Maple Nishada";
		oldWepId = 1462040;
		newWepName = "Maple Pyrope Crow";
		newWepId = 1462085;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130013;
	    } else if (selection == 13) {
		oldWepName = "Maple Skanda";
		oldWepId = 1472055;
		newWepName = "Maple PyropeSkanda";
		newWepId = 1472111;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130015;
	    } else if (selection == 3) {
		oldWepName = "Maple Dark Mate";
		oldWepId = 1332055;
		newWepName = "Maple Pyrope Halfmoon";
		newWepId = 1332114;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130014;
	    } else if (selection == 9) {
		oldWepName = "Maple Soul Spear";
		oldWepId = 1432040;
		newWepName = "Maple Pyrope Spear";
		newWepId = 1432075;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130008;
	    } else if (selection == 10) {
		oldWepName = "Maple Karstan";
		oldWepId = 1442051;
		newWepName = "Maple Pyrope Hellslayer";
		newWepId = 1442104;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130009;
	    } else if (selection == 4) {
		oldWepName = "Maple Shine Wand";
		oldWepId = 1372034;
		newWepName = "Maple Pyrope Wand";
		newWepId = 1372071;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130010;
	    } else if (selection == 5) {
		oldWepName = "Maple Wisdom Staff";
		oldWepId = 1382039;
		newWepName = "Maple Pyrope Staff";
		newWepId = 1382093;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130011;
	    } else if (selection == 14){
		oldWepName = "Maple Golden Claw";
		oldWepId = 1482022;
		newWepName = "Maple Pyrope Knuckle";
		newWepId = 1482073;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130016;
	    } else if (selection == 15) {
		oldWepName = "Maple Cannon Shooter";
		oldWepId = 1492022;
		newWepName = "Maple Pyrope Shooter";
		newWepId = 1492073;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130017;
	    } else if (selection == 16) {
		oldWepName = "Maple Cleat Katara";
		oldWepId = 1342027;
		newWepName = "Maple Pyrope Katara";
		newWepId = 1342028;
		leaves = 500;
		cost = 5000000;
	    }
	    cm.sendYesNo("Are you sure you want a #b" + newWepName + "#k?");
	}
    } else if (status == 4) {
	if (sel == 2 || sel == 4) {
	    if (mode != 1) {
		cm.sendOk("No? Maybe you should make up your mind. I'll be here, waiting.");
		cm.dispose();
	    } else {
		    if (cm.canHold(newWepId)) {
			//cm.gainItem(oldWepId, -1);
			//cm.gainItem(4001126, -leaves);
			//cm.gainMeso(-cost);
			cm.gainItem(newWepId,1);
			cm.sendOk(reward_msg);
		    } else {
			cm.sendOk("It appears that you are currently in full inventory, please check.");
		    }
		    cm.dispose();
	    }
	} else if (sel == 0 || sel == 1) {
		if (cm.canHold(newWepId)) {
		   // cm.gainItem(4001126, -leaves);
		   // cm.gainMeso(-cost);
		    cm.gainItem(newWepId, 1);
		    cm.sendOk(reward_msg);
		} else {
		    cm.sendOk("It appears that you are currently in full inventory, please check.");
		}
	    cm.dispose();
	}
    } else if (status == 5) {
	if (sel == 2 || sel == 4) {
	    if (cm.canHold(newWepId)) {
		if (selection == 21) {
		    //cm.gainItem(oldWepId,-1);
		    //cm.gainItem(4001126,-leaves);
		    //cm.gainMeso(-cost);
		    cm.gainItem(newWepId, 1);
		    cm.sendOk(reward_msg);
		} else {
		    //cm.gainItem(oldWepId,-1);
		    //cm.gainItem(4001126,-leaves);
		    //cm.gainItem(stimulator,-1);
		    //cm.gainMeso(-cost);
		    cm.gainItem(newWepId,1,true);
		    cm.sendOk(reward_msg);
		}
	    } else {
		cm.sendOk("It appears that you are currently in full inventory, please check.");
	    }
	    cm.dispose();
	}
	} else if (status == 10) {
		if (selection == 0) {
			cm.sendOk("If you don't want to trade any leaves, then you won't get exp.");
			cm.dispose();
			return;
		}
		if (!cm.haveItem(4001126, selection)) {
			cm.sendOk("You do not have that many leaves.");
			cm.dispose();
			return;
		}
		if (cm.getPlayerStat("EXP") >= (Integer.MAX_VALUE - 200 * selection)) {
			cm.sendOk("You are trying to trade me too many leaves!");
			cm.dispose();
			return;
		}
		cm.gainItem(4001126, -selection);
		cm.gainExp(200 * selection);
		cm.sendOk("There you go!");
		cm.dispose();
    }
}