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
	    cm.sendSelfTalk("Ouch! What is this place??? It looks like I am near the Golem Temple.. let's go to that portal over there on the left.");
        cm.spawnMonster(9390916,1,679,95);
		cm.dispose();
    }
}