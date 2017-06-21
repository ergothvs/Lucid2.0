/* guild creation npc */
var status = -1;
var sel;
var sel2;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 0) {
    	cm.dispose();
    	return;
    }
    
    if (mode == 1) {
    	status++;
    } else {
    	status--;
    }
    
    if (status == 0) {
    	if (cm.getPlayerStat("GID") > 0 && cm.getPlayerStat("GRANK") == 1) {
    		cm.sendSimple("Now, how can I help you?\r\n#b#L0#I want to expand my guild#l\r\n#L1#I want to break up my guild#l")
    	} else {
    		cm.sendNext("Hey...would you happen to be interested in guilds?");
    	}
    } else if (status == 1) {
    	sel = selection;
    	if (selection == 0) {
    		cm.sendNext("Are you here because you want to expand your guild? To increase the number of people you can accept into your guild, you'll have to re-register. You'll also have to pay a fee. Just so you know, the absolute maximum size of a guild is 200 members.");
    	} else if (selection == 1) {
    		cm.sendYesNo("Are you sure you want to break up your guild? Remember, once you break up your guild, it will be gone forever. Are you sure you still want to do it?");
    	} else {
    		cm.sendSimple("#b#L2#What's a guild?#l\r\n#L3#What do I do to form a guild?#l\r\n#L4#I want to start a guild#l");
    	}
    } else if (status == 2) {
    	sel2 = selection;
    	if (sel == 0) {
    		cm.sendYesNo("Current Max Guild Members: #bNaN#k characters. To increase that amount by #b10#k, you need #b10000 GP#k. Your guild has #bNaN GP#k right now. Do you want to expand your guild?");
    	} else if (sel == 1) {
    		cm.sendYesNo("I'll ask one more time. Would you like to give up all guild privileges and disband the guild?");
    	} else if (selection == 2) {
    		cm.sendNext("You can think of a guild as a small crew full of people with similar interests and goals, except it will be officially registered in our Guild Headquarters and be accepted as a valid GUILD.");
    	} else if (selection == 3) {
    		cm.sendNext("You must be at least Lv. 100 to create a guild.");
    	} else if (selection == 4) {
    		cm.sendYesNo("Oh! So you're here to register a guild... You need 5,000,000 mesos to register a guild. I trust that you are ready. Would you like to create a guild?");
    	}
    } else if (status == 3) {
    	if (sel == 0) {
    		cm.increaseGuildCapacity(true);
		    cm.dispose();
    	} else if (sel == 1) {
    		cm.disbandGuild();
		    cm.dispose();
    	} else if (sel2 == 2) {
    		cm.sendNextPrev("There are a variety of benefits that you can get through guild activities. For example, you can obtain a guild skill or an item that is exclusive to guilds.");
    		cm.dispose();
    	} else if (sel2 == 3) {
    		cm.sendNextPrev("You also need 5,000,000 mesos. This is the registration fee.");
    	} else if (sel2 == 4) {
    		cm.genericGuildMessage(3);
		    cm.dispose();
    	}
     } else if (status == 4) {
    	 if (sel2 == 3) {
    		 cm.sendPrevNext("So, come see me if you would like to register a guild! Oh, and of course you can't be already registered to another guild!");
    		 cm.dispose();
    	 }
     }
}