/*
 * Gachapon
 * by Flav
 */


var common = Array(3010000, 3010001, 3010002, 3010003, 3010004, 3010005, 3010006, 3010007, 3010008, 3010009, 3010010, 3010011, 3010012, 3010013, 3010014, 3010015, 3010016, 3010017, 3010018, 3010019, 3010021, 3010025, 3010035, 3010036, 3010038, 3010039, 3010040, 3010041, 3010043, 3010044, 3010045, 3010046, 3010047, 3010049, 3010052, 3010053, 3010054, 3010055, 3010057, 3010058);
var normal = Array(3010196, 3010253, 3010255, 3010060, 3010061, 3010062, 3010063, 3010064, 3010065, 3010066, 3010067, 3010068, 3010069, 3010071, 3010072, 3010073, 3010075, 3010077, 3010080, 3010085, 3010092, 3010093, 3010095, 3010096, 3010098, 3010099, 3010101, 3010106, 3010107, 3010108, 3010109, 3010110, 3010111, 3010112, 3010113, 3010114, 3010115, 3010116, 3010117, 3010118, 3010119, 301020);
var rare = Array(3010183, 3010189, 3010249, 3010124, 3010125, 3010126, 3010127, 3010128, 3010129, 3010130, 3010131, 3010132, 3010133, 3010134, 3010135, 3010136, 3010137, 3010138, 3010139, 3010140, 3010141, 3010142, 3010145, 3010149, 3010151, 3010152, 3010154, 3010155, 3010156, 3010157, 3010161, 3010163, 3010165, 3010167, 3010168, 3010169, 3010170, 3010171, 3010172, 3010173, 3010174, 3010175, 3010177);


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
			cm.sendOk("See you next time, when you try your luck here~!");
			cm.dispose();
			return;
		} else if (mode == 1) {
			status++;
		}

		if (status == 0) {
			cm.sendNext("I am LucidMaple's Gachapon Chair NPC.\r\nThe tickets look like this: #i5680021#");
		} else if (status == 1) {
			if (!cm.haveItem(5680021)) {
				cm.sendOk("You dont have any #bChair Gachapon Tickets#k.");
				cm.dispose();
			} else {
				cm.sendYesNo("I see you have a ticket of mine, do you wish to use it?");
			}
		} else if (status == 2) {
			cm.gainItem(5680021, -1);

			if (chance > 0 && chance <= 2) {
				cm.gainItem(icommon, 1);
			} else if (chance >= 3 && chance <= 4) {
				cm.gainItem(inormal, 1);
			} else {
				cm.gainItem(irare, 1);
			}

			cm.dispose();
		}
	}
}