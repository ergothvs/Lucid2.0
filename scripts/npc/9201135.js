var status = -1;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
    }
    if (status == 0) {
		if (cm.getPlayer().getMapId() == 551000000) {
			cm.sendYesNo("Would you like to head back to #rCBD#k?\r\nIt will cost you another #e20,000#n mesos.");
		} else {
			cm.sendYesNo("Would you like to take a tour to #rUlu City#k?\r\nIt will only cost you #e20,000#n mesos!");
		}
	} else if (status == 1) {
		if (mode > 0) {
		  if (cm.getPlayer().getMeso() > 19999) {
			if (cm.getPlayer().getMapId() == 551000000) {
				cm.warp(540000000, 0);
			} else {
				cm.warp(551000000, 0);
			}
				cm.gainMeso(-20000);
				cm.dispose();
		  } else {
			cm.sendOk("You don't have #e20,000#n mesos!");
			cm.dispose();
		  }
		}
	}
}