/**
 *
 * @author: Eric
 * @func: 2nd Job Advancement
 * @rev: 2 - Added Job Selection and added Beginner
 * @rev: 3 - Added level check, made text clearer, added starterpacks. (this npc was a mess...) #Kaz
 * @rev: 4 - Added appropriate checks so that there is no meso exploit. #kaz (mah bad, just in time tho)
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
	  if (cm.getPlayer().getJob() == 530 || cm.getPlayer().getJob() == 100 || cm.getPlayer().getJob() == 200 || cm.getPlayer().getJob() == 300 || cm.getPlayer().getJob() == 400 || cm.getPlayer().getJob() == 500 || cm.getPlayer().getJob() == 1100 || cm.getPlayer().getJob() == 1200 || cm.getPlayer().getJob() == 1300 || cm.getPlayer().getJob() == 1400 || cm.getPlayer().getJob() == 1500 || cm.getPlayer().getJob() == 2000 || cm.getPlayer().getJob() == 2100 || cm.getPlayer().getJob() == 430 || cm.getPlayer().getJob() == 431 || cm.getPlayer().getJob() == 2200 || cm.getPlayer().getJob() == 2201 || cm.getPlayer().getJob() == 3200 || cm.getPlayer().getJob() == 3300 || cm.getPlayer().getJob() == 3500 || cm.getPlayer().getJob() == 507 || cm.getPlayer().getJob() == 508 || cm.getPlayer().getJob() == 2300 || cm.getPlayer().getJob() == 2400 || cm.getPlayer().getJob() == 3100 || cm.getPlayer().getJob() == 5000) {
		cm.sendNext("Oh hi! Looks you are nearly ready for the next step! If you are still below level 30 you are Obliged to receive a starter Pack. If you are level 30 or higher, you can receive a 2nd job advancement!\r\n\r\nNote that only people with 0 rebirths, and who have not received the package before can receive the package.");
	  } else if (cm.getPlayer().getJob() == 0 || cm.getPlayer().getJob() == 1000 || cm.getPlayer().getJob () == 2000 || cm.getPlayer().getJob() == 3000) {
	    status = 3; // ++ing here
		cm.sendNext("Hi there, looks like your all set for getting a new Job! What would you like to become?"); 
	  } else {
		cm.sendOk("Oh hello. It looks like you allready have a job. And are also #eNOT#n in need of a #e2nd Job Advance#n.\r\n\r\n#rKeep in mind most advances occur automatically after reaching a certain level.");
		cm.dispose();
	  }
	  } else if (status == 1) {
	     if (cm.getPlayer().getJob() == 100 && cm.getPlayer().getLevel() >= 30) {
	     cm.sendSimple("Cool! A true #bWarrior#k! So you like to stab? What do you want to be:\r\n#L120#Page\r\n#L130#Spearman\r\n#L110#Fighter");
		} else if (cm.getPlayer().getJob() == 200 && cm.getPlayer().getLevel() >= 30) {
	     cm.sendSimple("Woah! A #bMagician!#k Can you do a magic trick?\r\nHaha, What job do you want to become? :\r\n#L220#Ice / Lightning\r\n#L210#Fire / Poison\r\n#L230# Cleric");
		 } else if (cm.getPlayer().getJob() == 300 && cm.getPlayer().getLevel() >= 30) {
	     cm.sendSimple("Damn! A #bBowman!#k Can you hit a bullseye every time?\r\nWell, if not maybe upgrading your job will help :\r\n#L310#Bowman\r\n#L320#Crossbowman");
		 } else if (cm.getPlayer().getJob() == 400 && cm.getPlayer().getLevel() >= 30) {
	     cm.sendSimple("Uh-oh! A #bThief!#k Don't rob me please! Haha.\r\nHere, take your job! :\r\n#L410#Assassin\r\n#L420#Bandit");
		 } else if (cm.getPlayer().getJob() == 500 && cm.getPlayer().getLevel() >= 30) {
	     cm.sendSimple("Don't shoot, all-mighty #bPirate#k!\r\nJust take your job! :\r\n#L510#Brawler\r\n#L520#Gunsligner");
		} else if (cm.getPlayer().getJob() == 530 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 100 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 200 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 300 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 400 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 500 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 1100 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 1200 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 1300 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 1400 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 1500 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 2000 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 2100 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 430 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 431 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 2200 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 2201 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 3200 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 3300 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 3500 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 507 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 508 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 2300 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 2400 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 3100 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1 || cm.getPlayer().getJob() == 5000 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1) {
	     cm.sendSimple("Oh phew, you are just a low level. I almost got scared. But hey! Don't worry! You are obliged to receive a starter pack!");
		} else {
		 cm.sendOk("Looks like you are unable to receive a starter package. You either have too many rebirths, more than 10 million mesos or received the package before.");
		 cm.dispose();
	}
    } else if (status == 2 && cm.getPlayer().getLevel() >= 30) {
	  status = 999;
	  cm.getPlayer().changeJob(selection);
	  cm.dispose();
	  // job selection (not advance) 
	} else if (status == 2) {
	  status = 999;
		if (cm.getPlayer().getJob() == 100 || cm.getPlayer().getJob() == 1100 || cm.getPlayer().getJob() == 2000 || cm.getPlayer().getJob() == 2100 || cm.getPlayer().getJob() == 3100 || cm.getPlayer().getJob() == 5000 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1) {
		cm.sendOk("You are obliged to receive the warrior package!");  
    cm.gainItem(1102041, 1); // Pink Advent Cape 
    cm.gainItem(1302067, 1); // Maple Ani Weapon 
    cm.gainItem(1442071, 1); // Seraphim Polearm 
    cm.gainItem(1442050, 1); // Seraphim Spear 
    cm.gainItem(1402053, 1); // Seraphim 2-H Sword 
    cm.gainItem(1412035, 1); // Seraphim 2-H Axe 
    cm.gainItem(3010000, 1); // Beginner Chair 
    cm.gainItem(1422039, 1); // Seraphim 2-H Blunt Weapon (Mace) 
    cm.gainItem(1002357, 1); // Zakum Hat 
    cm.gainItem(2022179, 5); // 5 Onyx Apple 
    cm.gainItem(2000005, 50); // 50 Power Elixir 
    cm.gainMeso(10000000); // 10 Million Meso 
    cm.gainItem(1082146, 1); // Yellow WG
	cm.addDojoPoints(1);
	cm.dispose();
	} else if (cm.getPlayer().getJob() == 200 || cm.getPlayer().getJob() == 1200 || cm.getPlayer().getJob() == 2200 || cm.getPlayer().getJob() == 2201 || cm.getPlayer().getJob() == 3200 || cm.getPlayer().getJob() == 2400 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1) {
	cm.sendOk("You are obliged to receive the Magic package!");  
    cm.gainItem(1102042, 1); // Purple Advent Cape 
    cm.gainItem(1302067, 1); // Maple Ani Weapon 
    cm.gainItem(1372046, 1); // Seraphim Wand 
    cm.gainItem(1382062, 1); // Seraphim Staff 
    cm.gainItem(1002357, 1); // Zakum Hat 
    cm.gainItem(2022179, 5); // 5 Onyx Apple 
    cm.gainItem(2000005, 50); // 50 Power Elixir 
    cm.gainMeso(10000000); // 10 Million Meso 
    cm.gainItem(1082145, 1); // Yellow WG 
    cm.gainItem(3010000, 1); // Beginner Chair
    cm.gainItem(1362000, 1); // cane
	cm.addDojoPoints(1);
	cm.dispose();
	} else if (cm.getPlayer().getJob == 300 || cm.getPlayer().getJob() == 1300 || cm.getPlayer().getJob() == 3300 || cm.getPlayer().getJob() == 2300 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1){
	cm.sendOk("You are obliged to receive the Archer package!");  
    cm.gainItem(1452062, 1); // Seraphim Bow 
    cm.gainItem(1462056, 1); // Seraphim Cross bow 
    cm.gainItem(2060001, 5000); // Arrows   
    cm.gainItem(2061003, 5000); // X-Box arrows.  
    cm.gainItem(1102041, 1); // Pink Advent Cape 
    cm.gainItem(1302067, 1); // Maple Ani Weapon 
    cm.gainItem(1002357, 1); // Zakum Hat 
    cm.gainItem(2022179, 5); // 5 Onyx Apple 
    cm.gainItem(2000005, 50); // 50 Power Elixir 
    cm.gainMeso(10000000); // 10 Million Meso 
    cm.gainItem(1082147, 1); // Blue WG 
    cm.gainItem(3010000, 1); // Beginner Chair
    cm.gainItem(1522000, 1); // dual bowgun
	cm.addDojoPoints(1);
	cm.dispose();
	}else if (cm.getPlayer().getJob() == 400 || cm.getPlayer().getJob() == 1400 || cm.getPlayer().getJob() == 430 || cm.getPlayer().getJob() == 431 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1) {
	cm.sendOk("You are obliged to receive the Thief package!");  
    cm.gainItem(1102041, 1); // Pink Advent Cape 
    cm.gainItem(2070001, 5000); // Stars   
    cm.gainItem(1302067, 1); // Maple Ani Weapon 
    cm.gainItem(1332081, 1); // Seraphim Dagger 
    cm.gainItem(1472077, 1); // Seraphim Claw 
    cm.gainItem(1002357, 1); // Zakum Hat 
    cm.gainItem(2022179, 5); // 5 Onyx Apple 
    cm.gainItem(2000005, 50); // 50 Power Elixir 
    cm.gainItem(1082148, 1); // Purple WG 
    cm.gainItem(3010000, 1); // Beginner Chair 
    cm.gainMeso(10000000); // 10 Million Meso
    cm.gainItem(1342047, 1); // katara
	cm.addDojoPoints(1);
	cm.dispose();
	}else if (cm.getPlayer().getJob() == 500 || cm.getPlayer().getJob() == 1500 || cm.getPlayer().getJob() == 3500 || cm.getPlayer().getJob() == 530 || cm.getPlayer().getJob() == 507 || cm.getPlayer().getJob() == 508 && cm.getPlayer().getReborns() < 1 && cm.getPlayer().getMeso() < 10000000 && cm.getDojoPoints() < 1) {
	cm.sendOk("You are obliged to receive the Pirate package!");  
    cm.gainItem(1102041, 1);  // Pink Advent Cape 
    cm.gainItem(1482029, 1); // Seraphim Knuckles 
	cm.gainItem(1302067, 1); // Maple Ani Weapon 
    cm.gainItem(1492000, 1); // pistol
    cm.gainItem(1002357, 1); // Zakum Hat 
    cm.gainItem(2022179, 5); // 5 Onyx Apple 
    cm.gainItem(2000005, 50); // 50 Power Elixir 
    cm.gainItem(1082147, 1); // Blue WG 
    cm.gainItem(3010000, 1); // Beginner Chair 
    cm.gainMeso(10000000); // 10 Million Meso
    cm.gainItem(1532000, 1); // cannon
	cm.addDojoPoints(1);
	cm.dispose();
	} else {
	cm.sendOk("How the hell did you even get here? This npc chat box should be inaccessible!");
	cm.dispose()
	}
	} else if (status == 4) {
	  var joblist = "What Job do you want to become?\r\n #L0#Beginner#l \r\n #L100#Warrior#l \r\n #L200#Magician#l \r\n #L300#Bowman#l \r\n #L400#Thief#l \r\n #L430#Dual Blade#l \r\n #L500#Pirate#l \r\n #L501#Cannoneer#l \r\n #L508#Jett#l \r\n #L1100#Dawn Warrior#l \r\n #L1200#Blaze Wizard#l \r\n #L1300#Wind Archer#l \r\n #L1400#Night Walker#l \r\n #L1500#Thunder Breaker#l \r\n #L2100#Aran#l \r\n #L2200#Evan#l \r\n #L2300#Mercedes#l \r\n #L2400#Phantom#l \r\n #L3100#Demon Slayer#l \r\n #L3200#Battle Mage#l \r\n #L3300#Wild Hunter#l \r\n #L3500#Mechanic#l \r\n #L5100#Mihile#l";
	  cm.sendSimple(joblist);
	} else if (status == 5) {
	   cm.getPlayer().changeJob(selection);
	   //for (var i = 0; i < 14; i++)
	   //cm.getPlayer().levelUp(); // for ap due to force setting, should we forloop? 
	   cm.dispose();
    }
}