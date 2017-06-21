/* Return to Masteria
    BeastTamer Quest line
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
		cm.sendNextNoESC("No, don't go this way!");
    } else if (status == 1) {
		cm.sendDirectionInfo("Effect/Direction14.img/effect/ShamanBT/BalloonMsg1/27");
		cm.sendDirectionStatus(1, 2500);
		cm.sendNextNoESC("(Tom, that's enough horsing around.");
    } else if (status == 2) {
		cm.sendNextNoESC("Enough is enough!");
	} else if (status == 3) {
		cm.sendNextNoESC("No one believes me anymore... Sigh. I miss my mommy!");
	} else if (status == 4) {
		cm.introEnableUI(0);
        cm.introDisableUI(false);
		cm.warp(866000000,0);
		cm.topMsg("Earned Adventurebound title!");
		cm.dispose();
	}
}