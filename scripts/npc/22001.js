/* RED 1st impact
    Vasily (Maple Return skill)
    Made by Daenerys
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else 
        if (status == 0) {
		    cm.sendNext("We are just a few miles away from our destination. Just chat with the other passengers while we prepare for landing.");
            cm.dispose();
        status--;
    }
    if (status == 0) {
	    cm.sendYesNo("Are you getting off? The ship is going to depart soon. If you leave, you must wait for the next one to come.");
	} else if (status == 1) {
	    cm.warp(2000100,0);
		cm.dispose();
    }
  }