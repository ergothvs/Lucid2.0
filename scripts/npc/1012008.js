/* RED 1st impact
    Casey
    Made by Daenerys
*/
var status = -1;
var sel = 0;

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
	    cm.sendSimple("You want to learn more about the minigames? Awesome! Ask me anything. Which minigame do you want to know more about?\r\n#b#L0#Omok#l\r\n#b#L1#Match Cards#l");		
    } else if (status == 1) {
        sel = selection;
	  if (selection == 0) {		
	    cm.sendNext("Here are the rules for Omok, so listen carefully. Omok is a game in which you and your opponent take turns laying a piece on the table until someone finds a way to lay 5 consecutive pieces in a line, be it horizontal, diagonal, or vertical. For starters, only the ones with an #bOmok Set#k can open a game room.");	
     } else if (selection == 1) {
		cm.sendNext("You want #b#t4080100##k? Hmm...to make #t4080100#, you'll need some #b#t4030012#s#k. #t4030012# can be obtained by taking out the monsters all around the island. Collect 15 #t4030012#s and you can make a set of #t4080100#.");
        cm.dispose();	   
	   }
	} else if (status == 2) {
	  if (sel == 0) {		
	    cm.sendNextPrev("Every game of Omok will cost you #r100 mesos#k. Even if you don't have an #bOmok Set#k, you can enter the room and play. However, if you don't possess 100 mesos, then you won't be allowed in the room at all. The person opening the game room also needs 100 mesos to open the room (or else there's no game). If you run out of mesos during the game, then you're automatically kicked out of the room!");	
	    }
    } else if (status == 3) {
	  if (sel == 0) {		
	    cm.sendNextPrev("Enter the room, and when you're ready to play, click on #bReady#k. Once the visitor clicks on #bReady#k, the room owner can press #bStart#k to begin the game. If an unwanted visitor walks in, and you don't want to play with that person, the room owner has the right to kick the visitor out of the room. There will be a square box with x written on the right of that person. Click on that for a cold goodbye, okay?");	
        }
    } else if (status == 4) {
	  if (sel== 0) {		
	    cm.sendNextPrev("When the first game starts, #bthe room owner goes first#k. Beware that you'll be given a time limit, and you may lose your turn if you don't make your move on time. Normally, 3 x 3 is not allowed, but if there comes a point that it's absolutely necessary to put your piece there or face ending the game, then you can put it there. 3 x 3 is allowed as the last line of defense! Oh, and it won't count if it's #r6 or 7 straight#k. Only 5!");	
   	    }
	} else if (status == 5) {
	  if (sel == 0) {		
	    cm.sendNextPrev("If you know your back is against the wall, you can request a #bRedo#k. If the opponent accepts your request, then you and your opponent's last moves will cancel out. If you ever feel the need to go to the bathroom, or take an extended break, you can request a #btie#k. The game will end in a tie if the opponent accepts the request. Tip: this may be a good way to keep your friendships in tact.");	
   	    }
    } else if (status == 6) {
	   if (sel == 0) {		
	    cm.sendNextPrev("When the next game starts, the loser will go first. Also, no one is allowed to leave in the middle of a game. If you do, you may need to request either a #bforfeit or tie#k. (Of course, if you request a forfeit, you'll lose the game.) And if you click on 'Leave' in the middle of the game and call to leave after the game, you'll leave the room right after the game is over. This will be a much more useful way to leave.");		
		cm.dispose();
	   }
	    cm.dispose();
    }
}
