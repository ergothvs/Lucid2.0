/**
 *
 * @author: Eric
 * @func: Server Starter
 *
*/

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
		if (cm.getPlayer().getLevel() > 15) {
		  status = 999;
		  cm.sendOk("You #rcan't#k trust the #bsystem#k.");
		  cm.dispose();
	  } else {
		var joblist = "#bWelcome to WizStory!#k\r\nWhat Job do you want to become?\r\n #L0#Beginner#l \r\n #L100#Warrior#l \r\n #L200#Magician#l \r\n #L300#Bowman#l \r\n #L400#Thief#l \r\n #L430#Dual Blade#l \r\n #L500#Pirate#l \r\n #L501#Cannoneer#l \r\n #L508#Jett#l \r\n #L1100#Dawn Warrior#l \r\n #L1200#Blaze Wizard#l \r\n #L1300#Wind Archer#l \r\n #L1400#Night Walker#l \r\n #L1500#Thunder Breaker#l \r\n #L2100#Aran#l \r\n #L2200#Evan#l \r\n #L2300#Mercedes#l \r\n #L2400#Phantom#l \r\n #L3100#Demon Slayer#l \r\n #L3200#Battle Mage#l \r\n #L3300#Wild Hunter#l \r\n #L3500#Mechanic#l \r\n #L5100#Mihile#l";
		cm.sendSimple(joblist);
	  }
	} else if (status == 1) {
	   cm.getPlayer().changeJob(selection);
	   cm.warp(100000000, 0);
	   for (var i = 0; i < 15; i++)
	   cm.getPlayer().levelUp(); // for ap due to force setting, should we forloop? 
	   cm.dispose();
    }
}