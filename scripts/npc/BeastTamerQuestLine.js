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
		cm.sendNext("You used the Kobold Musk! Your powerful stench is quite remarkable!");
    } else if (status == 1) {
        cm.sendPrevS("Hurry! Talk to Woodrock about #o9390915#.",5);
		cm.gainItem(2432251, -1);
		cm.dispose();
	}
}