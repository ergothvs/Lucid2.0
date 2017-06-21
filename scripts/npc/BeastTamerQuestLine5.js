/* Return to Masteria
    BeastTamer Quest line
    Made by Daenerys
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
		cm.sendNextS("I'll let you go this time, but just you wait. Those animals will be mine!",5);
    } else if (status == 1) {
	    cm.forceCompleteQuest(59070);
		cm.levelUp();
        cm.warp(100040400,0);
		cm.dispose();
	}
}