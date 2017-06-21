var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    cm.dispose();
	}
	status--;
    }
    if (status == 0) {
		cm.sendPlayerToNpc("BOOYA! The future hero of Maple World walks through Wolf Forest, alone and unafraid, humming the strains of their very own theme song. Dunnn dun dun do daaaa.");
    } else if (status == 1) {
		cm.sendPlayerToNpc("The hero spies a trap! Is something caught inside? Could it be... DINNER?!");
    } else if (status == 2) {
	    cm.EnableUI(0);
		cm.forceCompleteQuest(59002);
		cm.dispose();
	}
}