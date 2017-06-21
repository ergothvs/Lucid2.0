/* RED Zero
    Kiru
    Made by Daenerys
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else 
        if (status == 0) {
		    cm.sendNext("Not interested? Oh well...");
            cm.dispose();
        status--;
    }
    if (status == 0) {
	    cm.sendYesNo("This ship will head towards #b#m130000000##k, an island where you'll find crimson leaves soaking up the sun, the gentle breeze that glides past the stream, and the Empress of Maple, Cygnus. If you're interested in joining the Cygnus Knights, then you should definitely pay a visit there. Are you interested in visiting #m130000000#?\r\n\r\nThe trip will cost you #b1000#k Mesos.");
	} else if (status == 1) {	
        cm.warp(130000210,0);	
	    cm.dispose(); 
    }
}