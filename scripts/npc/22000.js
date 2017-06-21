/* 
	NPC Name: 		Shanks
	Map(s): 		Maple Road : Southperry (60000)
	Description: 	Select job and warp to Victoria Island
*/
var job = [ 
[[100, "Warrior"], [200, "Magician"], [300, "Bowman"], [400, "Thief"], [500, "Pirate"]], 
[[1100, "Dawn Warrior"], [1200, "Blaze Wizard"], [1300, "Wind Archer"], [1400, "Night Walker"], [1500, "Thunder Breaker"]], 
[[3200, "Battle Mage"], [3300, "Wild Hunter"], [3500, "Mechanic"]], 
[[110, "Fighter"], [120, "Page"], [130, "Spearman"]], 
[[210, "Wizard (F/P)"], [220, "Wizard (I/L)"], [230, "Cleric"]], 
[[310, "Hunter"], [320, "Crossbow Man"]], 
[[410, "Assassin"], [420, "Bandit"]], 
[[510, "Brawler"], [520, "Gunslinger"]],
[[3100, "Demon Slayer"], [3101, "Demon Avenger"]]]; 
var status = 0; 
var select;
var destination = 2010000; //Lith Harbor

function jobSelection(index) { 
    var choose = "Please, select your job:" 
    for (var i = 0; i < job[index].length; i++) 
        choose += "\r\n#L" + job[index][i][0] + "#" + job[index][i][1] + "#l"; 
    cm.sendSimple(choose); 
}  

function starterpack() { 
	cm.gainItem(1102041, 1); // Pink Advent Cape 
    cm.gainItem(1102042, 1); // Purple Advent Cape 
	cm.gainItem(3010000, 1); // Beginner Chair 
    cm.gainItem(2000005, 50); // 50 Power Elixir
    cm.gainItem(1082146, 1); // Yellow WG 
	cm.warp(destination, 0);
	cm.sendOk("Welcome to Victoria Island! Enjoy your stay!~");
	cm.dispose();
}

function proccespackage() {
	 switch (cm.getPlayer().getJob()) { 
            case 100:
			case 1100:
			case 2000:
			case 2100:
			case 3100:
			case 5100:
			case 6000: //warriors (aran/ demons/ Kaiser/ mihile included)
                cm.gainItem(1442071, 1); // Seraphim Polearm 
				cm.gainItem(1442050, 1); // Seraphim Spear 
				cm.gainItem(1402053, 1); // Seraphim 2-H Sword 
				cm.gainItem(1412035, 1); // Seraphim 2-H Axe 
				cm.gainItem(1422039, 1); // Seraphim 2-H Blunt Weapon (Mace) 1302033
				cm.gainItem(1302033, 1); // 1-H sword 
				starterpack();
                break; 
			case 200:
			case 1200:
			case 2200:
			case 2201:
			case 3200: //mages (evan/ BM included)
				cm.gainItem(1372046, 1); // Seraphim Wand 
				cm.gainItem(1382062, 1); // Seraphim Staff
				starterpack();
				break;
			case 300:
			case 1300:
			case 3300:
			case 2300: //archers (mercedes, WH included)
				cm.gainItem(1452062, 1); // Seraphim Bow 
				cm.gainItem(1462056, 1); // Seraphim Cross bow 
				cm.gainItem(2060001, 5000); // Arrows   
				cm.gainItem(2061003, 5000); // X-Box arrows.
				cm.gainItem(1352000, 1); // magic arrows. 
				cm.gainItem(1522000, 1); // dual bowgun
				starterpack();
				break;
			case 400:
			case 1400:
			case 430:
			case 431: //thieves (dual blade included)
				cm.gainItem(2070001, 5000); // Stars   
				cm.gainItem(1332081, 1); // Seraphim Dagger 
				cm.gainItem(1472077, 1); // Seraphim Claw 
				cm.gainItem(1342047, 1); // katara
				starterpack();
				break;
			case 500:
			case 1500:
			case 3500: 
			case 530:
			case 507: 
			case 508: //pirates (mech/ cannoneer included)
				cm.sendOk("You are obliged to receive the Pirate package!");
				cm.gainItem(1482029, 1); // Seraphim Knuckles 
				cm.gainItem(1492000, 1); // pistol
				cm.gainItem(2300000, 100); // bullets
				cm.gainItem(2300000, 100); // bullets
				cm.gainItem(2300000, 100); // bullets
				cm.gainItem(1532000, 1); // cannon
				starterpack();
				break;
			case 2400: //phantom
				cm.gainItem(1362000, 1); // cane
				cm.starterpack();	
				break;
			case 6500: //angelic buster
				cm.gainItem(1222062, 1); // Soul Shooter
				cm.gainItem(1352601, 1); // Soul ring
				starterpack();
			case 2710: //luminous
				cm.gainItem(1212086, 1); // Lumi Stick thingy (expires after relog)
				cm.gainItem(1212001, 1); // Lumi Stick thingy
				cm.gainItem(1352400, 1); // Light orb
				starterpack();
				break;
			case 3600: //Xenon
				cm.gainItem(1242001, 1); // xenon blade thingy
				starterpack();
				break;
			default:
				cm.sendOk("Hi there! Something went wrong in giving you a starter package! This might be because you are a job that is currently unknown to us, if so, report it at the forums!")
				cm.dispose();
	 }
}

function start() { 
    status = -1; 
    action(1, 0, 0); 
}  


function action(mode, type, selection) { 
    if (mode == 1) 
        status++; 
    else if (mode == -1) 
        status--; 
    else { 
        cm.dispose(); 
        return; 
    } 
    if (status == 0) { 
        if (cm.getPlayer().getLevel() >= 10 && cm.getPlayer().getLevel() < 50 ) 
            cm.sendYesNo("Oh hello there! Here comes a though decision:\r\n\r\nEither stay on your current path, or change it by selecting a different job."); 
        else { 
            cm.sendOk("You may not advance yet. You must be at least lvl 10 and below lvl 50 to proceed."); 
            cm.dispose(); 
        } 
    } else if (status == 1) { 
        cm.sendSimple("Please, pick a job:\r\n\r\n" + 
		"#L0#Adventurer#l\r\n" +
		"#L1000#Knight of Cygnus#l\r\n" +
		"#L3000#Resistance#l\r\n" +
		"#L3001#Demon#l\r\n" +
		"#L501#Cannoneer#l\r\n" +
		"#L2000#Aran#l\r\n" +
		"#L2001#Evan#l\r\n" +
		"#L2002#Mercedes#l\r\n" +
		"#L2003#Phantom#l\r\n" +
		"#L2004#Luminous#l\r\n" +
		"#L3002#Xenon#l\r\n" +
	/*	"#L6000#Kaiser#l\r\n" + */
		"#L6001#Angelic Buster#l\r\n" +
		"#L2000#Aran#l\r\n" +
		"#L4001#Hayato#l\r\n" +
		"#L4002#Kanna#l\r\n" +
		"#L5000#Mihile#l\r\n" +
		"#L2000#Aran#l\r\n" +
		"#L10000#Zero#l\r\n" +
		"#L2000#Dual Blade#l\r\n");
	}else if (status == 2) {
        switch (selection) { 
            case 0: // Adventurer
                jobSelection(0); 
                break; 
            case 1000: // Cygnus knight
                jobSelection(1); 
                break; 
            case 3000: // Resistance
                jobSelection(2); 
                break; 
            case 3001: // Demon 
                jobSelection(8); 
                break; 
            //Special Jobs 
            case 501: // Pirate(Cannoneer) 
                cm.getPlayer().changeJob(530); 
                proccespackage();
                break; 
            case 2000: // Legend(Aran) 
                cm.getPlayer().changeJob(2100); 
                proccespackage(); 
                break; 
            case 2001: // Farmer(Evan) 
                cm.getPlayer().changeJob(2200); 
                proccespackage(); 
                break; 
            case 2002: // Mercedes 
                cm.getPlayer().changeJob(2300); 
                proccespackage(); 
                break; 
            case 2003: // Phantom Jr. 
                cm.getPlayer().changeJob(2400); 
                proccespackage();
                break;
            case 2004: // Luminous
                cm.getPlayer().changeJob(2710); 
                proccespackage();
                break; 
            case 3002: // Xenon 
                cm.getPlayer().changeJob(3600); 
                proccespackage(); 
                break;
            case 6000: // Kaiser 
                cm.getPlayer().changeJob(6100); 
                proccespackage(); 
                break; 
            case 6001: // Angelic Buster 
                cm.getPlayer().changeJob(6500); 
                proccespackage();
                break;
           /* case 11000: // Beast Tamer  
                cm.getPlayer().changeJob(11200); 
                proccespackage();
                break; */
            case 4001: // Hayato  
                cm.getPlayer().changeJob(4100); 
                proccespackage();
                break;
            case 4002: // Kanna
                cm.getPlayer().changeJob(4200); 
                proccespackage();
                break;
            case 5000: // Mihile
                cm.getPlayer().changeJob(5100); 
                proccespackage();
                break; 
			case 10000: // Zero
				cm.getPlayer().changeJob(10000);
				proccespackage();
				break;
            case 430: // Dual Blade
				cm.getPlayer().changeJob(430);
				proccespackage();
				break;
		}
    } else if (status == 3) { 
        select = selection; 
        cm.sendYesNo("Are you sure you want to Job Advance?"); 
    } else if (status == 4) { 
        cm.getPlayer().changeJob(select); 
        proccespackage();
    } 
} 