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
	    cm.sendNextS("Dun, dun, dun. Hero theme song! I'm #b#h0##k, from a town hidden deep within Arboren forest!",15);
	} else if (status == 1) {	
	    cm.sendNextS("I've got the coolest ears and tail, dun dun dun. They're super heroic, dun dun dun.",15);
	} else if (status == 2) {
	    cm.sendNextS("And I'm gonna be a hero somedaaaaay. A hero somedaaaay! Drumroll!",15);
    } else if (status == 3) {	
	    cm.sendNextPrevS("For reals. Granny Rosanna tells me bedtime stories every night...",15);
	} else if (status == 4) {	
	    cm.sendNextPrevS("Stories about the #bfive brave heroes#k who sealed away the terrifying #bBlack Mage#k!\r\nPew, pew, kaboom! I'm gonna be a hero just like 'em someday soon!",15);	
	} else if (status == 5) {	
        cm.showBeastTamerTutScene1();
		cm.dispose();
    }
}