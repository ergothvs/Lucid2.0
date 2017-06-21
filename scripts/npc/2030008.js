/* Adobis
 * 
 * El Nath: The Door to Zakum (211042300)
 * 
 * Zakum Quest NPC 
 
 * Custom Quest 100200 = whether you can do Zakum
 * Custom Quest 100201 = Collecting Gold Teeth <- indicates it's been started
 * Custom Quest 100203 = Collecting Gold Teeth <- indicates it's finished
 * Quest 7000 - Indicates if you've cleared first stage / fail
 * 4031061 = Piece of Fire Ore - stage 1 reward
 * 4031062 = Breath of Fire    - stage 2 reward
 * 4001017 = Eye of Fire       - stage 3 reward
 * 4000082 = Zombie's Gold Tooth (stage 3 req)
*/

var status;
var mapId = 211042300;
var stage;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 0) {
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	if (cm.getPlayerStat("LVL") >= 50) {
		cm.sendOk("You want to be permitted to do the Zakum Dungeon Quest?  Well, I, #bAdobis#k... judge you to be suitable.  You should be safe roaming around the dungeon.  Just be careful... \r\n #L0# I'd like to challenge Easy Zakum #l \r\n #L1# I'd like to challenge Normal Zakum #l \r\n #L2# I'd like to challenge Chaos Zakum #l");
		return;
	}else if (cm.getPlayerStat("LVL") <= 49) {
	    cm.sendOk("Please come back to me when you've become stronger.  I've seen a few adventurers in my day, and you're far too weak to complete my tasks.");
	    cm.dispose();
	}
    }
    else if(status == 1){
    if(selection == 0){
    cm.warp(211042402, 1);
    cm.dispose;
    }
    else if(selection == 1){
    cm.warp(211042400, 1);
    cm.dispose;
    }
    else if(selection == 2){
    cm.warp(211042401, 1);
    cm.dispose;
    }
    }else {
	cm.dispose();
    }
}