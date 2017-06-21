/**
 * @author: Eric
 * @npc: Missi
 * @func: Custom Equipment Seller
*/

var status = -1;
var currency = 4000999;
var hats = [[1005000, 200], [1005001, 200], [1005002, 200], [1005003, 200], [1005004, 200], [1005005, 200], [1005006, 200], [1005007, 350], [1005008, 350], [1005009, 350], [1005010, 350], [1005011, 440], [1005012, 200], [1005013, 320], [1005014, 320], [1005015, 320], [1005016, 320], [1005017, 700], [1005018, 650], [1005019, 300], [1005020, 300], [1005021, 300], [1005022, 300], [1005023, 520], [1005024, 500], [1005025, 500], [1005026, 500], [1005027, 500], [1005028, 400], [1005029, 400], [1005030, 400], [1005031, 560], [1005032, 440], [1005033, 350], [1005034, 350], [1005035, 700], [1005036, 520]];
var eyeAccs = [[1022000, 1], [1022000, 1]];
var faceAccs = [[1012996, 400], [1012997, 400], [1012998, 400], [1012999, 400]];
var capes = [[1102000, 1], [1102200, 1]];
var specials = [[1112100, 1], [1902000, 1]];
var type = 0;

function start() {
	action(1, 0, 0);
}

function action(mode, t, s) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	if (status == 0) {
		cm.sendNext("H-Hello, I'm Missi, the #rC-Custom Equipment Vender#k....nya..");
	} else if (status == 1) {
		cm.sendSimple("C-Choose what you would like..#b\r\n#L0#Hats\r\n#L1#Face Accessories\r\n#L2#Eye Accessories\r\n#L3#Capes\r\n#L4#Special");
	} else if (status == 2) {
		text = "W-What would you like to buy?#b";
		switch(s) {
			case 0:
				type = 1;
				for (var iH = 0; iH < hats.length; text+= "\r\n#L"+iH+"#" + "#z" + hats[iH][0] + "# - " + hats[iH][1] + " Munny", iH++);
				break;
			case 1:
				type = 2;
				for (var iFA = 0; iFA < faceAccs.length; text+= "\r\n#L"+iFA+"# " + "#z" + faceAccs[iFA][0] + "# - " + faceAccs[iFA][1] + " Munny", iFA++);
				break;
			case 2:
				type = 3;
				cm.sendOk("I'm all sold out for now...s-sorry..");
				cm.dispose();
				return;
				//for (var iEA = 0; iEA < eyeAccs.length; text+= "\r\n#L"+iEA+"# " + "#z" + eyeAccs[iEA][0] + "#"; iEA++)
				//break;
			case 3:
				type = 4;
				cm.sendOk("I'm all sold out for now...s-sorry..");
				cm.dispose();
				return;
				for (var iC = 0; iC < capes.length; text+= "\r\n#L"+iC+"# " + "#z" + capes[iC][0] + "#", iC++);
				//break;
			case 4:
				type = 5;
				cm.sendOk("I'm all sold out for now...s-sorry..");
				cm.dispose();
				return;
				//for (var iS = 0; iS < specials.length; text+= "\r\n#L"+iS+"# " + "#z" + specials[iS][0] + "#"; iS++)
				//break;
		}
		cm.sendSimple(text);
	} else if (status == 3) {
		var toBuy = s;
		var purchase = type - 1;
		switch(purchase) {
			case 0:
				if (cm.haveItem(currency, hats[toBuy][1])) {
					if (cm.canHold(hats[toBuy][0])) {
						cm.gainItem(currency, -hats[toBuy][1]);
						cm.gainItem(hats[toBuy][0], 1);
						cm.sendOk("P-please come a-again..");
					} else
						cm.sendOk("P-please have at least one free slot!");
				} else
					cm.sendOk("Kyaa, you don't have enough!");
				break;
			case 1:
				if (cm.haveItem(currency, faceAccs[toBuy][1])) {
					if (cm.canHold(faceAccs[toBuy][0])) {
						cm.gainItem(currency, -faceAccs[toBuy][1]);
						cm.gainItem(faceAccs[toBuy][0], 1);
						cm.sendOk("P-please come a-again..");
					} else
						cm.sendOk("P-please have at least one free slot!");
				} else
					cm.sendOk("Kyaa, you don't have enough!");
				break;
			case 2:
				if (cm.haveItem(currency, eyeAccs[toBuy][1])) {
					if (cm.canHold(eyeAccs[toBuy][0])) {
						cm.gainItem(currency, -eyeAccs[toBuy][1]);
						cm.gainItem(eyeAccs[toBuy][0], 1);
						cm.sendOk("P-please come a-again..");
					} else
						cm.sendOk("P-please have at least one free slot!");
				} else
					cm.sendOk("Kyaa, you don't have enough!");
				break;
			case 3:
				if (cm.haveItem(currency, capes[toBuy][1])) {
					if (cm.canHold(capes[toBuy][0])) {
						cm.gainItem(currency, -capes[toBuy][1]);
						cm.gainItem(capes[toBuy][0], 1);
						cm.sendOk("P-please come a-again..");
					} else
						cm.sendOk("P-please have at least one free slot!");
				} else
					cm.sendOk("Kyaa, you don't have enough!");
				break;
			case 4:
				if (cm.haveItem(currency, specials[toBuy][1])) {
					if (cm.canHold(specias[toBuy][0])) {
						cm.gainItem(currency, -specials[toBuy][1]);
						cm.gainItem(specials[toBuy][0], 1);
						cm.sendOk("P-please come a-again..");
					} else
						cm.sendOk("P-please have at least one free slot!");
				} else
					cm.sendOk("Kyaa, you don't have enough!");
				break;
		}
		cm.dispose();
	}
}