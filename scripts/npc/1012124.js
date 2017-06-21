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
    if(cm.getQuestStatus(2013) != 2){
	cm.sendYesNo("Would you like to get out?");
    }
    else if(cm.getQuestStatus(2013) == 2 || cm.getQuestStatus(2014) == 2){
    if(cm.getJob() == 100){
    cm.sendSimple("What class would you like to advance to? \r\n #L0# #dFighter #l \r\n #L1# #dPage #l \r\n #L2# #dSpearman #l");
    }
    else if(cm.getJob() == 200){
		cm.sendSimple("What class would you like to advance to? \r\n #L3# #dWizard f/p #l \r\n #L4# #dWizard i/l #l \r\n #L5# #dCleric #l");
		}
    
	else if(cm.getJob() == 300){
		cm.sendSimple("What class would you like to advance to? \r\n #L6# #dHunter #l \r\n #L7# #dCrossbowman #l");

    }
	else if(cm.getJob() == 400){
		cm.sendSimple("What class would you like to advance to? \r\n #L8# #dAssassin #l \r\n #L9# #dBandit #l");
    }
	else if(cm.getJob() == 500){
		cm.sendSimple("What class would you like to advance to? \r\n #L10# #dBrawler #l \r\n #L11# #dGunslinger #l");
    } else{
		cm.sendNext("Would you like to advance? \r\n #L12# #dYes, please #l \r\n #L13# #dNo, not yet #l");
	}
    }
	}
    else if (status == 1) {
		if(cm.getQuestStatus(2013) != 2){
    	cm.warp(980010000,0);
	cm.dispose();
		}
		else if(cm.getQuestStatus(2013) == 2){
		switch (selection){
		case 0: // fighter
		case 3: // i/l mage
		case 6: // hunter
		case 8: // assassin
		case 10: // brawler
		cm.getPlayer().changeJob(cm.getPlayer().getJob() + 10);
		if(!cm.haveItem(2430447)){
		cm.gainItem(2430447,1);	
		}
		cm.dispose;
		break;
		case 1: // page
		case 4: // f/p mage
		case 7: // crossbowman
		case 9: // bandit
		case 11: // gunslinger
		cm.getPlayer().changeJob(cm.getPlayer().getJob() + 20);
		if(!cm.haveItem(2430447)){
		 cm.gainItem(2430447,1);	
		}
		cm.dispose;
		break;
		case 2: //spearman
		case 5: // cleric
		cm.getPlayer().changeJob(cm.getPlayer().getJob() + 30);
		if(!cm.haveItem(2430447)){
		cm.gainItem(2430447,1);	
		}
		cm.dispose;
		case 12: //jobs other than 2nd explorer
		switch(cm.getPlayer().getJob()){
		case 1100: // dawn warrior 
		case 1200: // blaze wizard 
		case 1300: // wind archer 
		case 1400: // night walker 
		case 1500: //thunder breaker 
		case 2100: //aran 
		case 2300: // mercedes
		case 2400: // phantom
		case 2500: // shade
		case 2700: // luminous
		case 3100: // demon slayer
		case 3200: // battle mage
		case 3300: // wild hunter
		case 3500: // mechanic
		case 3600: // xenon
		case 3700: // blaster
		case 4100: // hayato
		case 4200: // kanna
		case 5100: // mihile
		case 6100: // kaiser
		case 6500: // angelic buster
		case 14200: // kinesis
		cm.getPlayer().changeJob(cm.getPlayer().getJob() + 10);
		if(!cm.haveItem(2430447)){
		cm.gainItem(2430447,1);	
		}
		cm.dispose;
		break;
		case 508: // jett
		cm.getPlayer().changeJob(cm.getPlayer().getJob() + 62);
		if(!cm.haveItem(2430447)){
		cm.gainItem(2430447,1);	
		}
		break;
		case 501: // cannoneer
		cm.getPlayer().changeJob(cm.getPlayer().getJob() + 29);
		if(!cm.haveItem(2430447)){
		cm.gainItem(2430447,1);	
		}
		break;
		case 2210: // evan
		cm.getPlayer().changeJob(cm.getPlayer().getJob() + 2);
		if(!cm.haveItem(2430447)){
		cm.gainItem(2430447,1);	
		}
		break;
		case 3101: // demon avenger
		cm.getPlayer().changeJob(cm.getPlayer().getJob() + 19);
		if(!cm.haveItem(2430447)){
		cm.gainItem(2430447,1);	
		}
		break;
		case 10112: // zero
		case 11212: // beast tamer
		cm.sendOk("You can't advance");
		break;
		case 110: // crusader
		case 120: // white knight
		case 130: // dragon knight
		case 210: // f/p mage
		case 220: // i/l mage
		case 230: // priest
		case 310: // ranger
		case 320: // sniper
		case 410: // hermit
		case 420: // chief bandit
		case 510: // marauder
		case 520: // outlaw
		case 530: // cannon blaster
		case 570: // jett
		case 1110: // dawn warrior
		case 1210: // blaze wizard
		case 1310: // wind archer
		case 1410: // night walker
		case 1510: // thunder breaker
		case 2110: // aran
		case 2310: // mercedes
		case 2410: // phantom
		case 2510: // shade
		case 2710: // luminous
		case 3110: // demon slayer
		case 3120: // demon avenger
		case 3210: // battle mage
		case 3310: // wild hunter
		case 3510: // mechanic
		case 3610: // xenon
		case 3710: // blaster
		case 4110: // hayato
		case 4210: // kanna
		case 5110: // mihile
		case 6110: // kaiser
		case 6510: // angelic buster
		case 14210: // kinesis
		cm.getPlayer().changeJob(cm.getPlayer().getJob() + 1);
		if(!cm.haveItem(2430451)){
		cm.gainItem(2430451,1);	
		}
		break;
		case 2212: // evan
		cm.getPlayer().changeJob(cm.getPlayer().getJob() + 2);
		if(!cm.haveItem(2430451)){
		cm.gainItem(2430451,1);	
		}
		break;
		case 111: // hero
		case 121: // paladin
		case 131: // darkknight
		case 211: // f/p archmage
		case 221: // i/l archmage
		case 231: // bishop
		case 311: // bowmaster
		case 321: // marksman
		case 411: // night lord
		case 421: // shadower
		case 511: // buccaneer
		case 521: // corsair
		case 531: // cannon master
		case 571: // jett
		case 1111: // dawn warrior
		case 1211: // blaze wizard
		case 1311: // wind archer
		case 1411: // night walker
		case 1511: // thunder breaker
		case 2111: // aran
		case 2311: // mercedes
		case 2411: // phantom
		case 2511: // shade
		case 2711: // luminous
		case 3111: // demon slayer
		case 3121: // demon avenger
		case 3211: // battle mage
		case 3311: // wild hunter
		case 3511: // mechanic
		case 3611: // xenon
		case 3711: // blaster
		case 4111: // hayato
		case 4211: // kanna
		case 5111: // mihile
		case 6111: // kaiser
		case 6511: // angelic buster
		case 14211: // kinesis
		cm.getPlayer().changeJob(cm.getPlayer().getJob() + 1);
		if(!cm.haveItem(2430451)){
		cm.gainItem(2430451,1);	
		}
		}
		case 13: //if doesnt wanna advance
		cm.dispose;
		break;
		default:
		cm.sendOk("Something went wrong");
		cm.dispose
		}	
		}

    }

}