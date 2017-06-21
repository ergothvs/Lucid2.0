var status = 0;
var optionA = 1004042; // Hat #1
var optionB = 1004043; // Hat #2
var mapid = 90000009;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	   if (mode == 1 && status >= 0)
			status++;
	   else if (mode == 0 && status >= 0)
			cm.dispose();
	   else
			status--;
		if (status == 0) {
			cm.sendNext("You've manage to complete my tutorial, congratulation, now it's time for you to go on your own and have an adventure. However it's dangerous to go without protection, allow me to give you a little something.");
		} else if (status == 1) {
			cm.sendSimple("Select your starter hat\r\n\r\n#L0##i" + optionA + "# - Blue Starter Hat (Defense)#l\r\n#L1##i" + optionB + "# - Red Starter Hat (Attack)#l");
		} else if (status == 2) {
			if (selection == 0) {
				cm.sendOk("Here you go, now take the space elevator behind me, it will take you to a place called Galaxy Free Market, I'll contact you again when trouble happen");
				cm.gainItem(optionA, 1); 
				cm.warp(mapid, 0);
				cm.dispose();
			} else if (selection == 1) {
				cm.sendOk("Here you go, now take the space elevator behind me, it will take you to a place called Galaxy Free Market, I'll contact you again when trouble happen");
				cm.gainItem(optionB, 1);
				cm.warp(mapid, 0);
				cm.dispose(); 
			}
		}
}