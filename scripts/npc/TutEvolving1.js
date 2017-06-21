/* Dawnveil
    Evolving Tutorial 1
	Orchid + Gelimer
    Made by Daenerys
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	    cm.sendNextS("Gelimer! Why did you move Lotus here without my authorization?",1);
	} else if (status == 1) {
	    cm.sendNextPrevS("M-madame Orchid. You are... early...", 1,0,9075004);
	} else if (status == 2) {
	    cm.sendNextPrevS("Shut your trap, you greasy old nerd! You don't move my brother unless I tell you to move my brother! My little Lotus needs to be near me or he'll get scared!",1);
	} else if (status == 3) {
	    cm.sendNextPrevS("Please lower your voice, dear. There have been some developments...", 1,0,9075004);
	} else if (status == 4) {
	    cm.sendNextPrevS("I'm developing a need to set your mustache on fire, Gelimer. How long do you think you can keep delaying these experiments? Lotus should have been awake months ago. You know what I'm going to do to you if you don't succeed, don't you?",1);
	} else if (status == 5) {
	    cm.sendNextPrevS("Lotus will awaken soon, I assure you. He will wake up, very soon...", 1,0,9075004);
	} else if (status == 6) {
	    cm.sendNextPrevS("You want more time? Then buy a new watch! I want my brother awake now!",1);
	} else if (status == 7) {
	     cm.sendNextPrevS("Perhaps he only needs to hear your voice... Come, take a look.", 1,0,9075004);
   } else if (status == 8) {
	    cm.warp(957020002);
        cm.dispose();
    }
  } 
