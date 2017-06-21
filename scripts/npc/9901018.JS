//script by Alcandon

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendSimple ("Hey wassup?!#r#h#k I am the Wish Tickets System of MapleBlade! I can be used as bank too! What do you wanna do? \r\n#L0#Trade 500MIL for a #i4031545#\r\n#L1#Trade 1BIL for a #i4031544# \r\n#L2#Trade 1.5BIL for a #i4031543#\r\n#L3#Trade #i4031545# for 500MIL\r\n#L4#Trade #i4031544# for 1BIL\r\n#L5#Trade #i4031543# for 1.5BIL");
				  } else if (selection == 0) {
                                  if(cm.getMeso() >= 500000000) {
                                  cm.gainMeso(-500000000);
				  cm.gainItem(4031545, 1);
                                  cm.sendOk("Here's your Blue Wish Ticket!");
				  cm.dispose();
        }
        else {
        cm.sendOk("You don't have enough mesos.");
        cm.dispose();
        }
 				  } else if (selection == 1) {
                                  if(cm.getMeso() >= 1000000000) {
                                  cm.gainMeso(-1000000000);
				  cm.gainItem(4031544, 1);
                                  cm.sendOk("Here's your Green Wish Ticket!");
				  cm.dispose();
        }
        else {
        cm.sendOk("You don't have enough mesos.");
        cm.dispose();
        }
				  } else if (selection == 2) {
                                  if(cm.getMeso() >= 1500000000) {
                                  cm.gainMeso(-1500000000);
				  cm.gainItem(4031543, 1);
                                  cm.sendOk("Here's your Yellow Wish Ticket!");
				  cm.dispose();
        }
        else {
        cm.sendOk("You don't have enough mesos.");
        cm.dispose();
        }
				  } else if (selection == 3) {
                                  if(cm.getMeso() < 1600000000 && cm.haveItem(4031545, 1)) {
                                  cm.gainMeso(500000000);
				  cm.gainItem(4031545, -1);
                                  cm.sendOk("Here's your 500,000,000 (500MIL) mesos!");
				  cm.dispose();
        }
        else {
        cm.sendOk("You don't have any Blue wish Tickets or you have too much mesos in your inventory (if we gave the mesos to you anyway, it would make your mesos go negative!).");
        cm.dispose();
        }
				  } else if (selection == 4) {
                                  if(cm.getMeso() < 1100000000 && cm.haveItem(4031544, 1)) {
                                  cm.gainMeso(1000000000);
				  cm.gainItem(4031544, -1);
                                  cm.sendOk("Here's your 1,000,000,000 (1BIL) mesos!");
				  cm.dispose();
        }
        else {
        cm.sendOk("You don't have any Blue wish Tickets or you have too much mesos in your inventory (if we gave the mesos to you anyway, it would make your mesos go negative!).");
        cm.dispose();
        }
				  } else if (selection == 5) {
                                  if(cm.getMeso() < 600000000 && cm.haveItem(4031543, 1)) {
                                  cm.gainMeso(1500000000);
				  cm.gainItem(4031543, -1);
                                  cm.sendOk("Here's your 1,500,000,000 (1.5BIL) mesos!");
				  cm.dispose();
        }
        else {
        cm.sendOk("You don't have any Blue wish Tickets or you have too much mesos in your inventory (if we gave the mesos to you anyway, it would make your mesos go negative!).");
        cm.dispose();
        }
				} else if (selection == 6) {
				  cm.warp(610030012);
				  cm.dispose();
				} else if (selection == 7) {
				  cm.warp(610030015);
				  cm.dispose();
				} else if (selection == 8) {
				  cm.warp(610030013);
				  cm.dispose();
				} else if (selection == 9) {
				  cm.warp(683000000);
				  cm.dispose();
			    }
		    }
}