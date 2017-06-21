var fm18 = 910000018;
var fm19 = 910000019;
var fm20 = 910000020;
var fm21 = 910000021;
var fm22 = 910000022;
var status;

function start() { 
    status = -1; 
    action(1, 0, 0);
} 

function action(mode, type, selection) { 
    if (mode == 1) { 
        status++; 
    }else{ 
        status--; 
    }
    
    if (status == 0 && cm.getMap().getId() == fm18) { 
        cm.sendSimple("Hi there, welcome to Free Market room 18. You can spawn monsters here for mesos. Would you like to spawn one of the following monsters?:#b\r\nBlue Wyvern (1mil/ 5 for 3mil)\r\nChief Memory Guardian(1mil/ 5 for 3mil)\r\nDark Wyvern(1mil/ 5 for 3mil)");
    } else if (status == 0 && cm.getMap().getId() == fm19) {
		cm.sendSimple("Hi there, welcome to Free Market room 19. You can spawn monsters here for mesos. Would you like to spawn one of the following monsters?:#b\r\nBlue Thanatos (1mil/ 5 for 3mil)\r\nDark Cornian(1mil/ 5 for 3mil)\r\nQualm Monk(1mil/ 5 for 3mil)");
	} else if (status == 0 && cm.getMap().getId() == fm20) {
		cm.sendSimple("Hi there, welcome to Free Market room 20. You can spawn bosses here for mesos. Would you like to spawn one of the following bosses?:#b\r\nHeadless Horseman (5mil/ 5 for 20mil)\r\nGriffey(5mil/ 5 for 20mil)\r\nManon(5mil/ 5 for 20mil)");
	} else if (status == 0 && cm.getMap().getId() == fm21) {
		cm.sendSimple("Hi there, welcome to Free Market room 21. You can spawn bosses here for mesos. Would you like to spawn one of the following bosses?:#b\r\nBlack Crow (10mil/ 5 for 40mil)\r\nErgoth(10mil/ 5 for 40mil)\r\nFemale Boss(10mil/ 5 for 40mil)");
	} else if (status == 0 && cm.getMap().getId() == fm22) {
		cm.sendSimple("Hi there, welcome to Free Market room 22. You can spawn bosses here for mesos. Would you like to spawn one of the following bosses?:#b\r\nRellik (10mil/ 5 for 40mil)\r\nBlack Witch(10mil/ 5 for 40mil)\r\nThe Boss(50mil/ 5 for 200mil)\r\nCat Boss (1bil/ 1exp)");
	} else if (status == 1 && cm.getMap().getId() == fm18) { 
        cm.PlayerToNpc("#L0#One Blue Wyvern please.#l\r\n#L1#5 Blue Wyvern please.#l\r\n#L2#One Chief Memory Guardian please.#l\r\n#L3#5 Chief Memory Guardian please.#l\r\n#L4#One Dark Wyvern please.#l\r\n#L5#5 Dark Wyvern please.#l\r\n");
	} else if (status == 2 && cm.getMap().getId() == fm18) { 
        if (selection == 0 && cm.getPlayer().getMeso() >= 1000000){
			cm.gainMeso(-1000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8810020);
			cm.dispose();
		} else if (selection == 1  && cm.getPlayer().getMeso() >= 3000000){
			cm.gainMeso(-3000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8810020);
			cm.spawnmonster(8810020);
			cm.spawnmonster(8810020);
			cm.spawnmonster(8810020);
			cm.spawnmonster(8810020);
			cm.dispose();
		} else if (selection == 2  && cm.getPlayer().getMeso() >= 1000000){
			cm.gainMeso(-1000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8200004);
			cm.dispose();
		} else if (selection == 3  && cm.getPlayer().getMeso() >= 3000000){
			cm.gainMeso(-3000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8200004);
			cm.spawnmonster(8200004);
			cm.spawnmonster(8200004);
			cm.spawnmonster(8200004);
			cm.spawnmonster(8200004);
			cm.dispose();
		} else if (selection == 4  && cm.getPlayer().getMeso() >= 1000000){
			cm.gainMeso(-1000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8810021);
			cm.dispose();
		} else if (selection == 5  && cm.getPlayer().getMeso() >= 3000000){
			cm.gainMeso(-3000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8810021);
			cm.spawnmonster(8810021);
			cm.spawnmonster(8810021);
			cm.spawnmonster(8810021);
			cm.spawnmonster(8810021);
			cm.dispose();
		}
	} else if (status == 1 && cm.getMap().getId() == fm19) { 
        cm.PlayerToNpc("#L0#One Thanatos please.#l\r\n#L1#5 Thanatos please.#l\r\n#L2#One  Dark Cornian please.#l\r\n#L3#5  Dark Cornian please.#l\r\n#L4#One Qualm Monk please.#l\r\n#L5#5 Qualm Monk please.#l\r\n");
	} else if (status == 2 && cm.getMap().getId() == fm19) { 
        if (selection == 0 && cm.getPlayer().getMeso() >= 1000000){
			cm.gainMeso(-1000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8170000);
			cm.dispose();
		} else if (selection == 1  && cm.getPlayer().getMeso() >= 3000000){
			cm.gainMeso(-3000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8170000);
			cm.spawnmonster(8170000);
			cm.spawnmonster(8170000);
			cm.spawnmonster(8170000);
			cm.spawnmonster(8170000);
			cm.dispose();
		} else if (selection == 2  && cm.getPlayer().getMeso() >= 1000000){
			cm.gainMeso(-1000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9300068);
			cm.dispose();
		} else if (selection == 3  && cm.getPlayer().getMeso() >= 3000000){
			cm.gainMeso(-3000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9300068);
			cm.spawnmonster(9300068);
			cm.spawnmonster(9300068);
			cm.spawnmonster(9300068);
			cm.spawnmonster(9300068);
			cm.dispose();
		} else if (selection == 4  && cm.getPlayer().getMeso() >= 1000000){
			cm.gainMeso(-1000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8200005);
			cm.dispose();
		} else if (selection == 5  && cm.getPlayer().getMeso() >= 3000000){
			cm.gainMeso(-3000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8200005);
			cm.spawnmonster(8200005);
			cm.spawnmonster(8200005);
			cm.spawnmonster(8200005);
			cm.spawnmonster(8200005);
			cm.dispose();
		}
	} else if (status == 1 && cm.getMap().getId() == fm20) { 
        cm.PlayerToNpc("#L0#One Headless Horseman please.#l\r\n#L1#5 Headless Horseman please.#l\r\n#L2#One Griffey please.#l\r\n#L3#5 Griffey please.#l\r\n#L4#One Manon please.#l\r\n#L5#5 Manon please.#l\r\n");
	} else if (status == 2 && cm.getMap().getId() == fm20) { 
        if (selection == 0 && cm.getPlayer().getMeso() >= 5000000){
			cm.gainMeso(-5000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9400549);
			cm.dispose();
		} else if (selection == 1  && cm.getPlayer().getMeso() >= 20000000){
			cm.gainMeso(-20000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9400549);
			cm.spawnmonster(9400549);
			cm.spawnmonster(9400549);
			cm.spawnmonster(9400549);
			cm.spawnmonster(9400549);
			cm.dispose();
		} else if (selection == 2  && cm.getPlayer().getMeso() >= 5000000){
			cm.gainMeso(-5000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8180001);
			cm.dispose();
		} else if (selection == 3  && cm.getPlayer().getMeso() >= 20000000){
			cm.gainMeso(-20000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8180001);
			cm.spawnmonster(8180001);
			cm.spawnmonster(8180001);
			cm.spawnmonster(8180001);
			cm.spawnmonster(8180001);
			cm.dispose();
		} else if (selection == 4  && cm.getPlayer().getMeso() >= 5000000){
			cm.gainMeso(-5000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8180000);
			cm.dispose();
		} else if (selection == 5  && cm.getPlayer().getMeso() >= 20000000){
			cm.gainMeso(-20000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(8180000);
			cm.spawnmonster(8180000);
			cm.spawnmonster(8180000);
			cm.spawnmonster(8180000);
			cm.spawnmonster(8180000);
			cm.dispose();
		}
	} else if (status == 1 && cm.getMap().getId() == fm21) { 
        cm.PlayerToNpc("#L0#One Black Crow please.#l\r\n#L1#5 Black Crow please.#l\r\n#L2#One Ergoth please.#l\r\n#L3#5 Ergoth please.#l\r\n#L4#One Female Boss please.#l\r\n#L5#5 Female Boss please.#l\r\n");
	} else if (status == 2 && cm.getMap().getId() == fm21) { 
        if (selection == 0 && cm.getPlayer().getMeso() >= 10000000){
			cm.gainMeso(-10000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9400014);
			cm.dispose();
		} else if (selection == 1  && cm.getPlayer().getMeso() >= 40000000){
			cm.gainMeso(-40000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9400014);
			cm.spawnmonster(9400014);
			cm.spawnmonster(9400014);
			cm.spawnmonster(9400014);
			cm.spawnmonster(9400014);
			cm.dispose();
		} else if (selection == 2  && cm.getPlayer().getMeso() >= 10000000){
			cm.gainMeso(-10000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9300028);
			cm.dispose();
		} else if (selection == 3  && cm.getPlayer().getMeso() >= 40000000){
			cm.gainMeso(-40000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9300028);
			cm.spawnmonster(9300028);
			cm.spawnmonster(9300028);
			cm.spawnmonster(9300028);
			cm.spawnmonster(9300028);
			cm.dispose();
		} else if (selection == 4  && cm.getPlayer().getMeso() >= 10000000){
			cm.gainMeso(-10000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9400121);
			cm.dispose();
		} else if (selection == 5  && cm.getPlayer().getMeso() >= 40000000){
			cm.gainMeso(-40000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9400121);
			cm.spawnmonster(9400121);
			cm.spawnmonster(9400121);
			cm.spawnmonster(9400121);
			cm.spawnmonster(9400121);
			cm.dispose();
		}
	} else if (status == 1 && cm.getMap().getId() == fm22) { 
        cm.PlayerToNpc("#L0#One Rellik please.#l\r\n#L1#5 Rellik please.#l\r\n#L2#One Black Witch please.#l\r\n#L3#5 Black Witch please.#l\r\n#L4#One The Boss please.#l\r\n#L5#5 The Boss please.#l\r\n#L6#Cat Boss please.#l");
	} else if (status == 2 && cm.getMap().getId() == fm22) { 
        if (selection == 0 && cm.getPlayer().getMeso() >= 10000000){
			cm.gainMeso(-10000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9400592);
			cm.dispose();
		} else if (selection == 1  && cm.getPlayer().getMeso() >= 40000000){
			cm.gainMeso(-40000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9400592);
			cm.spawnmonster(9400592);
			cm.spawnmonster(9400592);
			cm.spawnmonster(9400592);
			cm.spawnmonster(9400592);
			cm.dispose();
		} else if (selection == 2  && cm.getPlayer().getMeso() >= 10000000){
			cm.gainMeso(-10000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9001010);
			cm.dispose();
		} else if (selection == 3  && cm.getPlayer().getMeso() >= 40000000){
			cm.gainMeso(-40000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9001010);
			cm.spawnmonster(9001010);
			cm.spawnmonster(9001010);
			cm.spawnmonster(9001010);
			cm.spawnmonster(9001010);
			cm.dispose();
		} else if (selection == 4  && cm.getPlayer().getMeso() >= 50000000){
			cm.gainMeso(-50000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9400300);
			cm.dispose();
		} else if (selection == 5  && cm.getPlayer().getMeso() >= 200000000){
			cm.gainMeso(-200000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9400300);
			cm.spawnmonster(9400300);
			cm.spawnmonster(9400300);
			cm.spawnmonster(9400300);
			cm.spawnmonster(9400300);
			cm.dispose();
		} else if (selection == 6  && cm.getPlayer().getMeso() >= 1000000000){
			cm.gainMeso(-1000000000);
			cm.sendOk("There you go!");
			cm.spawnmonster(9300325);
			cm.dispose();
		}
    } else if (cm.getMap().getId() == fm18||fm19||fm20||fm21||fm22) {
		cm.sendOk("Feel free to come back anytime!");
		cm.dispose();
	} else {
		cm.sendOk("Well hello traveller.");
}
}