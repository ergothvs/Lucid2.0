/* RED 1st impact
    Vasily
    Made by Daenerys
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	    cm.sendNext("The ship isn't ready to set sail yet.");
		cm.dispose();
    }
}