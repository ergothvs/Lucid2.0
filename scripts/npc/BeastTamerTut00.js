/* Return to Masteria
    BeastTamer Tutorial
    Made by Daenerys
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else 
        if (status == 0) {
		    cm.showBeastTamerTutScene();
			cm.dispose();
        status--;
    }
    if (status == 0) {
	    cm.sendYesNoS("Would you like to skip the tutorial cutscenes?",5,9010000);
	} else if (status == 1) {	
	    cm.warp(866191000,0);
        cm.dispose();
    }
}