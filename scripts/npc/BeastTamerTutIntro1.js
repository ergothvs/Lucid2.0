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
	//    cm.sendNextS("Uhhhhhh. You can use my power if you need it.",5,9390301);
	//} else if (status == 1) {	
	//    cm.sendNextPrevS("Same goes for me, kiddo.",5,9390302);
	//} else if (status == 2) {	
    //    cm.sendNextPrevS("Eka and Arby gave you new powers! Check out your skill window if you want more info about us, kiddo.",5,9390302);
	//} else if (status == 3) {
	    cm.topMsg("Your animal friends gave you access to Guardian Leap and Homeward Bound.");
		cm.OpenUI(192);
	    cm.dispose();
	}
}