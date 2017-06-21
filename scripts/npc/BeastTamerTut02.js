/* Return to Masteria
    BeastTamer Tutorial
    Made by Daenerys
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
         cm.sendZeroTalk("Uh oh, what's this? I smell... the need for a hero!");
     } else if (status == 1) {
	    cm.warp(866101000,0);
            cm.dispose();
    }
}