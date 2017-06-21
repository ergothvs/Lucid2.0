var status = 0;
var optionA = 1004042; // Hat #1
var optionB = 1004043; // Hat #2
var mapid = 90000009;

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
			if (cm.haveItem(optionA, 1, true, true)) {
				cm.getPlayer().unequipStarter();
				cm.removeAll(optionA);
			} else if (cm.haveItem(optionB, 1, true, true)) {
				cm.getPlayer().unequipStarter();
				cm.removeAll(optionB);
			}
			cm.sendNext("Congratulations! You've completed my tutorial. Now, it's time for you to embark on your epic journey. A whole world of adventure awaits you! It will dangerous to go unprotected so, as a parting gift, let me give you a little something to aid you on your journey.");
		} else if (status == 1) {
			cm.sendSimple("Select your starter hat\r\n\r\n#L0##i" + optionA + "# - Blue Starter Hat (Defense)#l\r\n#L1##i" + optionB + "# - Red Starter Hat (Attack)#l");
		} else if (status == 2) {
			if (selection == 0) {
				cm.sendOk("Here you go. I will be sure to contact you again when the need arises. Now, see the elevator behind me? Take it and it will bring you to the Galaxy Free Market. Today marks a special day, a day of new beginnings. Will you embrace the comfort of light or be swallowed by the temptation of darkness? Your journey begins now!");
				cm.gainItem(optionA, 1); 
			} else if (selection == 1) {
				cm.sendOk("Here you go. I will be sure to contact you again when the need arises. Now, see the elevator behind me? Take it and it will bring you to the Galaxy Free Market. Today marks a special day, a day of new beginnings. Will you embrace the comfort of light or be swallowed by the temptation of darkness? Your journey begins now!");
				cm.gainItem(optionB, 1);
			}
		} else if (status == 3) {
			cm.warp(mapid, 0);
			cm.dispose();
		}
}