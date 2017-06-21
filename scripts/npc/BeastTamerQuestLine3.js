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
	    cm.sendSelfTalk("Where am I? I better walk a bit to the right to find out where I am.");
        cm.dispose();
    }
}