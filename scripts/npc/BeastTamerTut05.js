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
		cm.sendNextS("A stranger saved me? The world is a wonderful place!",1);
    } else if (status == 1) {
		cm.sendNextPrevS("Thank you, sweet stranger! My name's Arby, and I'm a proud member of the Critter Champs!",1);
    } else if (status == 2) {
	    cm.sendNextPrevS("The Critter Champs? Never heard of 'em.",15);
	} else if (status == 3) {
	    cm.sendNextPrevS("We're just starting out, but soon we'll be big! All of Maple World will be chanting our names! 'Arby! Arby! Arby!' Just like that",1);
	} else if (status == 4) {
	    cm.warp(866107000,0);
		cm.dispose();
	}
}
