var status = 0;

function start() {
    cm.sendYesNo("Would you like to go to Pantheon?");
}

function action(mode, type, selection) {
	if (mode == 1) {
		cm.warp(400000001);
	}
    cm.dispose();
}