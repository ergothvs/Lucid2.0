/* Return to Masteria
    BeastTamer Tutorial
    Made by Daenerys
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    cm.dispose();
	}
	status--;
    }
    if (status == 0) {
		cm.sendNextS("Anything else an aspiring hero can do for you, kitty cat?",15);
    } else if (status == 1) {
		cm.sendNextPrevS("Yup! Just turn around for a second. Real quick. I gotta, uh, do something.",1);
    } else if (status == 2) {
	    cm.sendNextPrevS("Done yet?",15);
	} else if (status == 3) {
	    cm.sendNextPrevS("NO PEEKING. Don't turn around yet! Give me another second",1);
	} else if (status == 4) {
		cm.dispose();
	}
}