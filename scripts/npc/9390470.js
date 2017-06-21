/* Return to Masteria
    Eka
    Made by Daenerys
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1) 
    status++;
    else 
	status--;
    if (status == 0) {
    	cm.sendNext("It's not time to leave yet. You should go find more things to do in the town.");
    	cm.dispose();
    }
}