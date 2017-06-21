var status = 0; 
function start() { 
status = -1; 
action(1, 0, 0); 
} 

function action(mode, type, selection) { 

if (mode == -1) { 
cm.dispose(); 
} 
else { 
if (status == 0 && mode == 0) { 
cm.dispose(); 
return; 
} 
if (mode == 1) { 
status++; 
} 
else { 
status--; 
} 
if (status == 0) { 
	cm.sendNext("Hello, welcome to LucidMaple. i hope you have lots of fun Here!"); //welcome
}
else if (status == 1) { 
	cm.sendSimple("Do you agree to follow LucidMaple's TOS as stated in the forums? \r\n #L0# I agree #l \r\n #L1# I dont agree #l "); //rule agreement
}
else if (status == 2) { 
	if(selection == 0){
	cm.sendSimple("Here you can choose your job, what job category would you like to coose? \r\n #L0# Warrior #l \r\n #L1# Magician #l \r\n #L2# Archer #l \r\n #L3# Thief #l \r\n #L4# Pirate #l"); // Opens a window with class category 
	cm.dispose
	}
	else if (selection == 1){
		cm.sendOk("I'm sorry, you aren't able to play LucidMaple untill you accept the TOS.");
	}
	} else if (status == 3) { // class category chosen
    if (selection == 0) { // warrior
        if (cm.getLevel() >= 1) {
        	cm.sendSimple("The strong warriors! \r\n #L5# Hero #l \r\n #L6# Paladin #l \r\n #L7# Dark knight #l \r\n #L8# Dawn warrior #l \r\n #L9# Aran #l \r\n #L10# Demon avenger #l \r\n #L11# Demon slayer #l \r\n #L12# Mihile #l \r\n #L13# Kaiser #l \r\n #L14# Zero #l \r\n #L15# Blaster #l \r\n #L16# Hayato #l");
        } 
    } else if (selection == 1) { // magician
        if (cm.getLevel() >= 1) {
        	cm.sendSimple("The smart mages! \r\n #L17# F/P archmage #l \r\n #L18# I/L archmage #l \r\n #L19# Bishop #l \r\n #L20# Blaze wizard #l \r\n #L21# Battle mage #l \r\n #L22# Evan #l \r\n #L23# Luminous #l \r\n #L24# Kanna #l \r\n #L25# Beast tamer #l \r\n #L26# Kinesis #l");
        } 
    }
    else if (selection == 2) { // Archer
        if (cm.getLevel() >= 1) {
        	cm.sendSimple("The nimble archers! \r\n #L27# Bowmaster #l \r\n #L28# Marksman #l \r\n #L29# Wind archer #l \r\n #L30# Wild hunter #l \r\n #L31# Mercedes #l ");
        } 
    } 
    else if (selection == 3) { // Thief
        if (cm.getLevel() >= 1) {
        	cm.sendSimple("The sneaky thieves! \r\n #L32# Night lord #l \r\n #L33# Shadower #l \r\n #L34# Night walker #l \r\n #L35# Xenon#l \r\n #L36# Phantom #l \r\n #L37# Dual blade #l");
        } 
    } 
    else if (selection == 4) { // pirate
        if (cm.getLevel() >= 1) {
        	cm.sendSimple("The cool pirates! \r\n #L38# Buccaneer #l \r\n #L39# Corsair #l \r\n #L40# Thunder breaker #l \r\n #L41# Cannon master #l \r\n #L42# Jett #l \r\n #L43# Mechanic #l\r\n #L44# Angelic buster #l \r\n #L45# Shade #l");
        } 
    }
     
} else if (status == 4){
	if (selection == 5){ //hero
		cm.changeJob(100);
		cm.gainMeso(100000);
		cm.gainItem(1302007,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 6){ //paladin
		cm.changeJob(100);
		cm.gainMeso(100000);
		cm.gainItem(1322032,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 7){ //dk
		cm.changeJob(100);
		cm.gainMeso(100000);
		cm.gainItem(1432000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 8){ //dw
		cm.changeJob(1100);
		cm.gainMeso(100000);
		cm.gainItem(1302007,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 9){ //aran
		cm.changeJob(2100);
		cm.gainMeso(100000);
		cm.gainItem(1442000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 10){ //da
		cm.changeJob(3101);		
		cm.gainMeso(100000);
		cm.gainItem(1232000,1);
		cm.gainItem(11099006,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 11){ //ds
		cm.changeJob(3100);
		cm.gainMeso(100000);
		cm.gainItem(1312000,1);
		cm.gainItem(11099006,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 12){ //mihile
		cm.changeJob(5100);
		cm.gainMeso(100000);
		cm.gainItem(1302007,1);
		cm.gainItem(1098000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 13){ //kaiser
		cm.changeJob(6100);
		cm.gainMeso(100000);
		cm.gainItem(1402001,1);
		cm.gainItem(1352500,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 14){ //zero !moet exp adjust worden!
		cm.changeJob(10100);
		cm.gainMeso(100000);
		cm.gainItem(1562001,1);
		cm.gainItem(1572001,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 15){ //blaster !weet id nog niet, moet ook weapon en secondary nog toevoegen!
		cm.changeJob(3700);
		cm.gainMeso(100000);
		cm.gainItem(2023137, 100);//gingerbread
		cm.gainItem(2023138, 100);//gingerbread
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 16){ //hayato
		cm.changeJob(4100);		
		cm.gainMeso(100000);
		cm.gainItem(1542000,1);
		cm.gainItem(1352800,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 17){ //f/p
		cm.changeJob(200);
		cm.gainMeso(100000);
		cm.gainItem( 1382000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 18){ //i/l
		cm.changeJob(200);
		cm.gainMeso(100000);
		cm.gainItem( 1382000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 19){ //cleric
		cm.changeJob(200);
		cm.gainMeso(100000);
		cm.gainItem( 1382000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 20){ //bw
		cm.changeJob(1200);		
		cm.gainMeso(100000);
		cm.gainItem( 1382000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 21){ //bam
		cm.changeJob(3200);		
		cm.gainMeso(100000);
		cm.gainItem(1382000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 22){ //evan
		cm.changeJob(2200);		
		cm.gainMeso(100000);
		cm.gainItem(1382000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 23){ //lumi
		cm.changeJob(2700);
		cm.gainMeso(100000);
		cm.gainItem(1212001,1);
		cm.gainItem(1352400,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 24){ //kanna
		cm.changeJob(4200);		
		cm.gainMeso(100000);
		cm.gainItem(1552000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 25){ //beasttamer
		cm.changeJob(11200);		
		cm.gainMeso(100000);
		cm.gainItem(1252001,1);
		cm.gainItem(1352810,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 26){ //kinesis
		cm.changeJob(1420);		
		cm.gainMeso(100000);
		cm.gainItem(1262000,1);
		cm.gainItem(1353200,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 27){ //bmaster
		cm.changeJob(300);		
		cm.gainMeso(100000);
		cm.gainItem(1452002,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 28){ //mm
		cm.changeJob(300);		
		cm.gainMeso(100000);
		cm.gainItem(1452002,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 29){ //wa
		cm.changeJob(1300);		
		cm.gainMeso(100000);
		cm.gainItem(1452002,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);
		cm.dispose;	
	}	else if (selection == 30){ //wh
		cm.changeJob(3300);		
		cm.gainMeso(100000);
		cm.gainItem(1452002,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 31){ //mercedes
		cm.changeJob(2300);		
		cm.gainMeso(100000);
		cm.gainItem(1522000,1);
		cm.gainItem(1352000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 32){ //nl
		cm.changeJob(400);		
		cm.gainMeso(100000);
		cm.gainItem(1472000,1);
		cm.gainItem(2070000,500);//subi
		cm.gainItem(2070000,500);
		cm.gainItem(2070000,500);
		cm.gainItem(2070000,500);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 33){ //shadower
		cm.changeJob(400);	
		cm.gainMeso(100000);
		cm.gainItem(1332063,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 34){ //nw
		cm.changeJob(1400);		
		cm.gainMeso(100000);
		cm.gainItem(1472000,1);
		cm.gainItem(2070000,500);//subi
		cm.gainItem(2070000,500);
		cm.gainItem(2070000,500);
		cm.gainItem(2070000,500);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 35){ //xenon
		cm.changeJob(3600);		
		cm.gainMeso(100000);
		cm.gainItem(1242001,1);
		cm.gainItem(1353001,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 36){ //phantom
		cm.changeJob(2400);	
		cm.gainMeso(100000);
		cm.gainItem(1362000,1);
		cm.gainItem(1352100,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 37){ //db
		cm.changeJob(430);	
		cm.gainMeso(100000);
		cm.gainItem(1332063,1);
		cm.gainItem(1342000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;	
	}	else if (selection == 38){ //bucc
		cm.changeJob(500);		
		cm.gainMeso(100000);
		cm.gainItem(1482000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);
		cm.dispose;	
	}	else if (selection == 39){ //corsair
		cm.changeJob(500);		
		cm.gainMeso(100000);
		cm.gainItem(1492000,1);
		cm.gainItem(2330000,500);//bullet
		cm.gainItem(2330000,500);
		cm.gainItem(2330000,500);	
		cm.gainItem(2330000,500);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 40){ //tb
		cm.changeJob(1500);		
		cm.gainMeso(100000);
		cm.gainItem(1482000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);
		cm.dispose;	
	}	else if (selection == 41){ //cannoneer
		cm.changeJob(530);		
		cm.gainMeso(100000);
		cm.gainItem(1532000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 42){ //jett
		cm.changeJob(508);		
		cm.gainMeso(100000);
		cm.gainItem(1492000,1);
		cm.gainItem(1352820,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 43){ //mech
		cm.changeJob(3500);		
		cm.gainMeso(100000);
		cm.gainItem(1492000,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 44){ //ab
		cm.changeJob(6500);		
		cm.gainMeso(100000);
		cm.gainItem(1222001,1);
		cm.gainItem(1352601,1); // !pink soul ring gives error 38!
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}	else if (selection == 45){ //shade
		cm.changeJob(2500);		
		cm.gainMeso(100000);
		cm.gainItem(1482000,1);
		cm.gainItem(1353100,1);
		cm.gainItem(2023137, 100);
		cm.gainItem(2023138, 100);
		//cm.gainExp(4589);
		cm.warp(100000000);	
		cm.dispose;
	}
	
}
}
}





