//script by Alcandon

var status;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.sendOk("#e#kOkay, see you next time!");
        cm.dispose();
        return;
    }
		if (status == 0) {
	cm.sendSimple ("#eHello there!#r#h#k I'm the Maple Leaves *2* System of MapleBlade! #rThe Godly Maple Leaves System is in the Free Market :)#k. This is just a simple trader. You will be required to have this item: #i4001126##e#d" +
                 "\r\n#L83##kTrade my Maple Leaves for awesome and rare items!");
	  } else if (selection == 80) {
               cm.sendSimple ("It's not available yet, sorry. #e#d" +
			"\r\n#L0##gTrade 10 Blue Wish Tickets for 10 Silver Slimes!");
	  } else if (selection == 83) {
               cm.sendSimple ("Pick the prizes you want to trade!#e#d" + 
            "\r\n#L13##r#kTrade 100 Maple Leaves for 1,000,000 Mesos" + 
            "\r\n#L1##r#kTrade 200 Maple Leaves for 150 Power Elixirs" + 
            "\r\n#L2##b#kTrade 600 Maple Leaves for 3 Onyx Apples" + 
            "\r\n#L3##r#kTrade 1200 Maple Leaves for 1 Blue Wish Ticket" + 
            "\r\n#L5##b#kTrade 1500 Maple Leaves for 1 Chaos Scroll" + 
            "\r\n#L6##b#kTrade 2000 Maple Leaves for 1 White Scroll"+
            "\r\n#L50##b#kMore options coming soon! :)");
} else if (selection == 1) {
		if (cm.haveItem(4001126, 200)){
		cm.gainItem(2000005, 150);
		cm.gainItem(4001126, -200);
		cm.sendOk("Nice job, here's your 150 Power Elixirs!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 2) {
        if (cm.haveItem(4001126, 600)){ 
        cm.gainItem(2022179, 3); 
        cm.gainItem(4001126, -600); 
        cm.sendOk("Nice job, here are your 3 Onyx Apples!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 3) { 
        if (cm.haveItem(4001126, 1200)){ 
        cm.gainItem(4031545, 1); 
        cm.gainItem(4001126, -1200); 
        cm.sendOk("Nice job, here is your #i4031545#!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 4) { 
        if (cm.haveItem(4001126, 1500)){ 
        cm.gainItem(1122014, 1); 
        cm.gainItem(4001126, -1500); 
        cm.sendOk("Nice job, here is your Silver Deputy Star!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 13) { 
        if (cm.haveItem(4001126, 100)){ 
        cm.gainMeso(1000000); 
        cm.gainItem(4001126, -100); 
        cm.sendOk("Nice job, here is your 1,000,000 mesos!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 5) { 
        if (cm.haveItem(4001126, 1500)){ 
        cm.gainItem(2049100, 1); 
        cm.gainItem(4001126, -1500); 
        cm.sendOk("Nice job, here is your Chaos Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 6) { 
        if (cm.haveItem(4001126, 2000)){ 
        cm.gainItem(2340000, 1); 
        cm.gainItem(4001126, -2000); 
        cm.sendOk("Nice job, here is your White Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    }  else if (selection == 7) { 
               cm.sendSimple ("Pick any GM Scroll you want if you got enough Maple Leaves!#e#d" + 
            "\r\n#L100##r#kTrade 2500 #i4001126# for Bottomwear for DEF" + 
            "\r\n#L101##b#kTrade 2500 #i4001126# for Bow for ATT" + 
            "\r\n#L102##r#kTrade 2500 #i4001126# for Cape for Magic DEF" + 
            "\r\n#L103##b#kTrade 2500 #i4001126# for Cape for Weapon DEF" + 
            "\r\n#L104##r#kTrade 2500 #i4001126# for Claw for ATT" + 
            "\r\n#L105##b#kTrade 2500 #i4001126# for Crossbow for ATT" + 
            "\r\n#L106##r#kTrade 2500 #i4001126# for Dagger for ATT" + 
            "\r\n#L107##b#kTrade 2500 #i4001126# for Gloves for ATT" + 
            "\r\n#L108##r#kTrade 2500 #i4001126# for Gloves for DEX" + 
            "\r\n#L109##b#kTrade 2500 #i4001126# for Helmet for DEF" + 
            "\r\n#L110##r#kTrade 2500 #i4001126# for Helmet for HP" + 
            "\r\n#L111##b#kTrade 2500 #i4001126# for One-Handed Axe for ATT" + 
            "\r\n#L112##r#kTrade 2500 #i4001126# for One-Handed BW for ATT" + 
            "\r\n#L113##b#kTrade 2500 #i4001126# for One-Handed Sword for ATT" + 
            "\r\n#L114##r#kTrade 2500 #i4001126# for Overall Armor for DEX" + 
            "\r\n#L115##b#kTrade 2500 #i4001126# for Pole Arm for ATT" + 
            "\r\n#L116##r#kTrade 2500 #i4001126# for Shield for DEF" + 
            "\r\n#L117##b#kTrade 2500 #i4001126# for Shoes for DEX" + 
            "\r\n#L118##r#kTrade 2500 #i4001126# for Shoes for Jump" + 
            "\r\n#L119##b#kTrade 2500 #i4001126# for Shoes for Speed" + 
            "\r\n#L120##r#kTrade 2500 #i4001126# for Spear for ATT" + 
            "\r\n#L121##b#kTrade 2500 #i4001126# for Staff for Magic ATT" + 
            "\r\n#L122##r#kTrade 2500 #i4001126# for Topwear for DEF" + 
            "\r\n#L123##b#kTrade 2500 #i4001126# for Two-Handed Aex for ATT" + 
            "\r\n#L124##r#kTrade 2500 #i4001126# for Two-Handed BW for ATT" + 
            "\r\n#L125##b#kTrade 2500 #i4001126# for Two-Handed Sword for ATT" + 
            "\r\n#L126##r#kTrade 2500 #i4001126# for Wand for Magic ATT");
  } else if (selection == 8) { 
        if (cm.haveItem(4001126, 2500)){ 
        cm.gainItem(2082149, 1); 
        cm.gainItem(4001126, -2500); 
        cm.sendOk("Nice job, here is your Brown Work Gloves!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 9) { 
        if (cm.haveItem(4001126, 2500)){ 
        cm.gainItem(2070016, 1); 
        cm.gainItem(4001126, -2500); 
        cm.sendOk("Nice job, here is Your Crystal Illbi's"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 10) { 
        if (cm.haveItem(4001126, 3000)){ 
        cm.gainItem(1082223, 1); 
        cm.gainItem(4001126, -3000); 
        cm.sendOk("Nice job, here is Your Stormcaster Gloves!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 11) { 
        if (cm.haveItem(4001126, 4000)){ 
        cm.gainItem(2070018, 1); 
        cm.gainItem(4001126, -4000); 
        cm.sendOk("Nice job, here is your Balanced Fury!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 12) { 
        if (cm.haveItem(4001126, 7500)){ 
        cm.gainItem(1002357, 1); 
        cm.gainItem(4001126, -7500); 
        cm.sendOk("Nice job, here is your Zakum Helmet!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 100) { 
        if (cm.haveItem(4001126, 400)){ 
        cm.gainItem(2040603, 1); 
        cm.gainItem(4001126, -400); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 101) { 
        if (cm.haveItem(4001126, 400)){ 
        cm.gainItem(2044503, 1); 
        cm.gainItem(4001126, -400); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 102) { 
        if (cm.haveItem(4001126, 400)){ 
        cm.gainItem(2041024, 1); 
        cm.gainItem(4001126, -400); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 103) { 
        if (cm.haveItem(4001126, 400)){ 
        cm.gainItem(2041050, 1); 
        cm.gainItem(4001126, -400); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 104) { 
        if (cm.haveItem(4001126, 400)){ 
        cm.gainItem(2044703, 1); 
        cm.gainItem(4001126, -400); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 105) { 
        if (cm.haveItem(4001126, 400)){ 
        cm.gainItem(2044603, 1); 
        cm.gainItem(4001126, -400); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 106) { 
        if (cm.haveItem(4001126, 400)){ 
        cm.gainItem(2043303, 1); 
        cm.gainItem(4001126, -400); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!")
        cm.dispose(); 
        } 
    } 
else if (selection == 107) { 
        if (cm.haveItem(4001126, 400)){ 
        cm.gainItem(2040807, 1); 
        cm.gainItem(4001126, -400); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 108) {
        if (cm.haveItem(4001126, 400)){ 
        cm.gainItem(2040806, 1); 
        cm.gainItem(4001126, -400); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    }  
	else if (selection == 109) {
        if (cm.haveItem(4001126, 400)){ 
		cm.gainItem(2040006, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 110) {
        if (cm.haveItem(4001126, 400)){ 
		cm.gainItem(2040007, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 111) {
        if (cm.haveItem(4001126, 400)){ 
		cm.gainItem(2043103, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 112) {
        if (cm.haveItem(4001126, 400)){  
		cm.gainItem(2043203, 1);
		cm.gainItem(4001126, -400); 		
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 113) {
        if (cm.haveItem(4001126, 400)){ 
		cm.gainItem(2043003, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 114) {
        if (cm.haveItem(4001126, 400)){ 
		cm.gainItem(2040506, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 115) {
        if (cm.haveItem(4001126, 400)){ 
		cm.gainItem(2044403, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 116) {
        if (cm.haveItem(4001126, 400)){ 
		cm.gainItem(2040903, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 117) {
        if (cm.haveItem(4001126, 400)){ 
		cm.gainItem(2040709, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 118) {
        if (cm.haveItem(4001126, 400)){ 
		cm.gainItem(2040710, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 119) {
        if (cm.haveItem(4001126, 400)){  
		cm.gainItem(2040711, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 120) {
        if (cm.haveItem(4001126, 400)){ 
		cm.gainItem(2044303, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 121) {
        if (cm.haveItem(4001126, 400)){  
		cm.gainItem(2043803, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 122) {
        if (cm.haveItem(4001126, 400)){  
		cm.gainItem(2040403, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 123) {
        if (cm.haveItem(4001126, 400)){ 
		cm.gainItem(2044103, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 124) {
        if (cm.haveItem(4001126, 400)){ 
		cm.gainItem(2044203, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 125) {
        if (cm.haveItem(4001126, 400)){ 
		cm.gainItem(2044003, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 126) {
        if (cm.haveItem(4001126, 400)){  
		cm.gainItem(2043703, 1);
		cm.gainItem(4001126, -400); 
		cm.sendOk("Nice job, here is your GM Scroll!");
		cm.dispose();
	  } else {
		cm.sendOk("#e#rYou do not have enough #i4001126#!!")
		cm.dispose();
		}
	}
else if (selection == 10) { 
        if (cm.haveItem(4001126, 500)){ 
        cm.gainItem(1082223, 1); 
        cm.gainItem(4001126, -500); 
        cm.sendOk("Nice job, here is your Stormcaster Gloves!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    }  
else if (selection == 11) { 
        if (cm.haveItem(4001126, 600)){ 
        cm.gainItem(2070018, 1); 
        cm.gainItem(4001126, -600); 
        cm.sendOk("Nice job, here is your Balanced Fury!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 12) { 
        if (cm.haveItem(4001126, 700)){ 
        cm.gainItem(1002357, 1); 
        cm.gainItem(4001126, -700); 
        cm.sendOk("Nice job, here is your Zakum Helmet!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 30) { 
        if (cm.haveItem(4001126, 30)){ 
        cm.gainItem(2041024, 1); 
        cm.gainItem(4001126, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 31) { 
        if (cm.haveItem(4001126, 30)){ 
        cm.gainItem(2041025, 1); 
        cm.gainItem(4001126, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 32) { 
        if (cm.haveItem(4001126, 30)){ 
        cm.gainItem(2044703, 1); 
        cm.gainItem(4001126, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 33) { 
        if (cm.haveItem(4001126, 30)){ 
        cm.gainItem(2044603, 1); 
        cm.gainItem(4001126, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4001126#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 34) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2043303, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 35) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2040807, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 36) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2040806, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 37) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2040006, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 38) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2040007, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 39) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2043103, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 40) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2043203, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 41) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2043003, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 42) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2040506, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 43) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2044403, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 44) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2040903, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 45) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2040709, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 46) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2040710, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 47) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2040711, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 48) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2044303, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 49) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2043803, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 50) { 
cm.sendOk("More Coming Soon :)");
cm.dispose();
    } 
else if (selection == 51) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2044103, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 52) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2044203, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 53) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2044003, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("You do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    }
else if (selection == 54) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2043703, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 55) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2043103, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 56) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2043203, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 129) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2043003, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 130) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2040506, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 131) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2044403, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 132) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2040903, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 133) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2040709, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
else if (selection == 134) { 
        if (cm.haveItem(4031545, 30)){ 
        cm.gainItem(2040710, 1); 
        cm.gainItem(4031545, -30); 
        cm.sendOk("Nice job, here is your GM Scroll!"); 
        cm.dispose(); 
      } else { 
        cm.sendOk("#e#rYou do not have enough #i4031545#!!") 
        cm.dispose(); 
        } 
    } 
}
