/* Return to Masteria
    BeastTamer Tutorial
    Made by Daenerys
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else 
        if (status == 0) {
	    cm.forceStartQuest(59000);
	    cm.forceCompleteQuest(59000);
	    cm.forceStartQuest(59001);
	    cm.forceCompleteQuest(59001);
		cm.forceStartQuest(59002);
	    cm.forceCompleteQuest(59002);
		cm.forceStartQuest(59003);
	    cm.forceCompleteQuest(59003);
		cm.forceStartQuest(59004);
	    cm.forceCompleteQuest(59004);
		cm.forceStartQuest(59005);
	    cm.forceCompleteQuest(59005);
		cm.forceStartQuest(59006);
	    cm.forceCompleteQuest(59006);
		cm.forceStartQuest(59007);
	    cm.forceCompleteQuest(59007);
		cm.forceStartQuest(59008);
	    cm.forceCompleteQuest(59008);
		cm.forceStartQuest(59009);
	    cm.forceCompleteQuest(59009);
		cm.forceStartQuest(59011);
	    cm.forceCompleteQuest(59011);
		cm.forceStartQuest(59013);
	    cm.forceCompleteQuest(59013);
		cm.forceStartQuest(59015);
	    cm.forceCompleteQuest(59015);
		cm.forceStartQuest(59016);
	    cm.forceCompleteQuest(59016);
		cm.forceStartQuest(59017);
	    cm.forceCompleteQuest(59017);
		cm.forceStartQuest(59018);
	    cm.forceCompleteQuest(59018);
		cm.forceStartQuest(59018);
	    cm.forceCompleteQuest(59018);
		cm.forceStartQuest(59019);
	    cm.forceCompleteQuest(59019);
		cm.forceStartQuest(59020);
	    cm.forceCompleteQuest(59020);
		cm.gainItem(2000001, 50);//Orange Potion
        cm.gainItem(2000006, 50);//Mana Elixir
		cm.gainItem(1142673, 1);//Sprout Guardian
        cm.gainItem(1352810, 1);//Tiny Whisper
		cm.warp(100000000,0);
		cm.EnableUI(0);
		cm.dispose();
      status--;
    }
    if (status == 0) {
	    cm.sendYesNoS("Would you like to have a short introduction to Beast Tamer?",5,9010000);
	} else if (status == 1) {	
	    cm.sendNextS("Dun, dun, dun. Hero theme song! I'm #b#h0##k, from a town hidden deep within Arboren forest!",15);
	} else if (status == 2) {	
	    cm.sendDirectionStatus(1,500);
		cm.sendDirectionFacialExpression(1,2000);
		cm.sendDirectionStatus(1,500);
	    cm.sendNextS("I've got the coolest ears and tail, dun dun dun. They're super heroic, dun dun dun.",15);
	} else if (status == 3) {	
	    cm.sendDirectionStatus(1,500);
		cm.sendDirectionFacialExpression(0,5000);
		cm.sendDirectionStatus(1,500);
	    cm.sendNextS("And I'm gonna be a hero somedaaaaay. A hero somedaaaay! Drumroll!",15);
	} else if (status == 4) {	
	    cm.sendNextPrevS("For reals. Granny Rosanna tells me bedtime stories every night...",15);
	} else if (status == 5) {	
	    cm.sendNextPrevS("Stories about the #bfive brave heroes#k who sealed away the terrifying #bBlack Mage#k!\r\nPew, pew, kaboom! I'm gonna be a hero just like 'em someday soon!",15);
	} else if (status == 6) {
	    cm.dispose();
	    cm.forceStartQuest(59000);
	    cm.forceCompleteQuest(59000);
	    cm.forceStartQuest(59001);
	    cm.forceCompleteQuest(59001);
		cm.forceStartQuest(59002);
	    cm.forceCompleteQuest(59002);
		cm.forceStartQuest(59003);
	    cm.forceCompleteQuest(59003);
		cm.forceStartQuest(59004);
	    cm.forceCompleteQuest(59004);
		cm.forceStartQuest(59005);
	    cm.forceCompleteQuest(59005);
		cm.forceStartQuest(59006);
	    cm.forceCompleteQuest(59006);
		cm.forceStartQuest(59007);
	    cm.forceCompleteQuest(59007);
		cm.forceStartQuest(59008);
	    cm.forceCompleteQuest(59008);
		cm.forceStartQuest(59009);
	    cm.forceCompleteQuest(59009);
		cm.forceStartQuest(59011);
	    cm.forceCompleteQuest(59011);
		cm.forceStartQuest(59013);
	    cm.forceCompleteQuest(59013);
		cm.forceStartQuest(59015);
	    cm.forceCompleteQuest(59015);
		cm.forceStartQuest(59016);
	    cm.forceCompleteQuest(59016);
		cm.forceStartQuest(59017);
	    cm.forceCompleteQuest(59017);
		cm.forceStartQuest(59326);
		cm.forceCompleteQuest(59326);
		cm.forceStartQuest(28862);
		cm.forceCompleteQuest(28862);
        cm.gainItem(2000001, 50);//Orange Potion
        cm.gainItem(2000006, 50);//Mana Elixir
		cm.gainItem(1142434,1);
		cm.gainItem(1142673, 1);//Sprout Guardian
        cm.gainItem(1352810, 1);//Tiny Whisper
		cm.warp(866137000,0);
		cm.EnableUI(0);
    }
}