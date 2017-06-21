var normal = Array(2340000, 2049100);
var common = Array(1003172, 1102275, 1082295, 1052314, 1072485, 1302152, 1402095, 1432086, 1442116, 1003177, 1102280, 1082300, 1052319, 1072490, 1302153, 1312066, 1322097, 1402096, 1412066, 1422067, 1432087, 1442117, 1003175, 1102278, 1082298, 1052317, 1072488, 1332130, 1472122, 1003180, 1102283, 1082303, 1052322, 1072493, 1332131, 1472123, 1003173, 1102276, 1082296, 1052315, 1072486, 1372084, 1382104, 1003174, 1102277, 1082297, 1052316, 1072487, 1452111, 1462099, 1003179, 1102282, 1082302, 1052321, 1072492, 1452112, 1462100, 1003176, 1102279, 1082299, 1052318, 1072489, 1482084, 1492085, 1003181, 1102284, 1082304, 1052323, 1072494, 1482085);
//var rare = Array(1012196, 1012197, 1012198, 1012199, 1012200, 1012201, 1032198, 1012298, 1004004, 1004006, 1004007);
var rare = Array(1004004, 1004005, 1004006, 1004007, 1012196, 1012197, 1012198, 1012199, 1012200, 1012298, 1032198);

function getRandom(min, max) {
	if (min > max) {
		return(-1);
	}
	if (min == max) {
		return(min);
	}
	return(min + parseInt(Math.random() * (max - min + 1)));
}

var icommon = common[getRandom(0, common.length - 1)];
var inormal = normal[getRandom(0, normal.length - 1)];
var irare = rare[getRandom(0, rare.length - 1)];
var chance = getRandom(0, 5);

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendSimple("Tons and tons of treasures! Straight from the back of store all the way to your hands!\r\n#b#L0#Uh.. what?#l\r\n#L2#What can I win?#l\r\n#L1#Let's give it a run!#l");
		} else if (status == 1) {
			if (selection == 0) {
				cm.sendOk("I stole some things from a back of a shopping mall alley and.. woops.. probably shouldn't have said that.\r\nWell, I'm exchanging #rVote Points#k in which you can obtain via #evoting#n, for a random item!");
				cm.dispose();
			} else if (selection == 1) {
			if (!cm.getPlayer().getVPoints() >= 1) { 
				cm.sendOk("Sorry, but it does not appear that you have more than 1 Vote Point.");
				cm.dispose();
			} else {
				cm.sendYesNo("Thank you for voting!\r\nWould you like a chance to try out for some amazing items?");
			}
			} else if (selection == 2) {
				text = "Tons and tons of items are available, because I stole some from the back of an alley last night.\r\n"; 
				text+= "#rCommon:#k\r\n";
				for (var i = 0; i < common.length; text += " #k#b#v"+common[i]+"##l", i++); 
				text+= "\r\n#rNormal:#k\r\n";
				for (var i = 0; i < normal.length; text += " #k#b#v"+normal[i]+"##l", i++); 
				text+= "\r\n#rRare:#k\r\n";
				for (var i = 0; i < rare.length; text += " #k#b#v"+rare[i]+"##l", i++); 
				cm.sendOk(text);
				cm.dispose();
			}
		} else if (status == 2) {
			cm.getPlayer().gainVotePoints(-1); 
			if (chance > 0 && chance <= 2) {
				cm.gainItem(icommon, 1);
			} else if (chance >= 3 && chance <= 4) {
				cm.gainItem(inormal, 1);
			} else {
				cm.gainItem(irare, 1);
			}
			cm.sendOk("Don't forget you can vote once every 6 hours!");
			cm.dispose();
		}
	}
}