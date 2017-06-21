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
		cm.sendSimple ("#r#eHey, I'm the Vote Points NPC of MapleBlade. What would you like to do?#e#d" + 
                "\r\n#L1##kHow do I get Vote Points?" +
		"\r\n#L2##kCheck how many Vote Points I have!" +
                "\r\n#L3##kTrade Vote Points for cool, rare, and awesome stuff! :O");
				 	} else if (selection == 1) {
					  cm.sendOk("#eHow to get Vote Points is easy! Just go to our website: #rhttp://maple-blade.zapto.org#k or #bhttp://mapleblade.tk/#k and click 'Vote' tab on the top navigation of the website, put your MapleBlade Account ID/Username #e#r(Not character name)#k, and then submit the Vote! \r\n#r#eWarning: YOU MUST BE LOGGED OFF WHILE VOTING!!!#k");
					  cm.dispose();
				 	} else if (selection == 2) {
					  cm.sendOk("#eYou currently have(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
					  cm.dispose();
					} else if (selection == 3) {
                cm.sendSimple ("#eChoose what you want:"+
                 "#k\r\n#L4##rTrade Vote Points for NX Cash" +
                 "#k\r\n#L5##bTrade Vote Points for Gold Gachapon Tickets" +
                 "#k\r\n#L6##rTrade Vote Points for GM Scrolls" +
                 "#k\r\n#L50##rTrade Vote Points for Maple Leaves"+
                 "#k\r\n#L60##rTrade Vote Points for 2 Chaos Scrolls"+
                 "#k\r\n#L61##rTrade Vote Points for 1 White Scroll");
				 	} else if (selection == 4) {
		cm.sendSimple ("You currently have(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.#e"+
		 "#k\r\nHow much would you like?" +
                 "#k\r\n#L8##r6k NX Cash for 1 Vote Points" +
                 "#k\r\n#L9##b12k NX Cash for 2 Vote Points" +
                 "#k\r\n#L10##r18k NX Cash for 3 Vote Points" +
                 "#k\r\n#L11##b24k NX Cash for 4 Vote Points" +
                 "#k\r\n#L12##r30k NX Cash for 5 Vote Points");
        } else if (selection == 7) {
               cm.sendSimple ("Alright, here are the options \r\n#bYou currently have:#k (#r" + cm.getPlayer().getVote Points() + "#k) #bVote Points.#e#d" + 
            "\r\n#L13##r#kTrade 1 #rVote Point#k for 2 #i4031545#" + 
            "\r\n#L14##b#kTrade 2 #rVote Points#k for 4 #i4031545#" + 
            "\r\n#L15##r#kTrade 3 #rVote Points#k for 6 #i4031545#" + 
            "\r\n#L16##b#kTrade 4 #rVote Points#k for 8 #i4031545#" + 
            "\r\n#L17##r#kTrade 5 #rVote Points#k for 10 #i4031545#");
        } else if (selection == 5) {
               cm.sendSimple ("Alright, here are the options \r\n#bYou currently have:#k (#r" + cm.getPlayer().getVote Points() + "#k) #bVote Points.#e#d" + 
            "\r\n#L18##r#kTrade 1 #rVote Points#k for 2 #i5220020#" + 
            "\r\n#L19##b#kTrade 2 #rVote Points#k for 5 #i5220020#" + 
            "\r\n#L20##r#kTrade 3 #rVote Points#k for 8 #i5220020#" + 
            "\r\n#L21##b#kTrade 4 #rVote Points#k for 11 #i5220020#" + 
            "\r\n#L22##r#kTrade 5 #rVote Points#k for 14 #i5220020#");
	  } else if (selection == 6) {
               cm.sendSimple ("Pick a #rGM Scroll#k! You currently have: (#r" + cm.getPlayer().getVote Points() + "#k) Vote Points. #e#d" + 
            "\r\n#L23##r#kTrade 2 #rVote Points#k for Bottomwear for DEF" + 
            "\r\n#L24##b#kTrade 2 #rVote Points#k for Bow for ATT" + 
            "\r\n#L25##r#kTrade 2 #rVote Points#k for Cape for Magic DEF" + 
            "\r\n#L26##b#kTrade 2 #rVote Points#k for Cape for Weapon DEF" + 
            "\r\n#L27##r#kTrade 2 #rVote Points#k for Claw for ATT" + 
            "\r\n#L28##b#kTrade 2 #rVote Points#k for Crossbow for ATT" + 
            "\r\n#L29##r#kTrade 2 #rVote Points#k for Dagger for ATT" + 
            "\r\n#L31##r#kTrade 2 #rVote Points#k for Gloves for DEX" + 
            "\r\n#L32##b#kTrade 2 #rVote Points#k for Helmet for DEF" + 
            "\r\n#L33##r#kTrade 2 #rVote Points#k for Helmet for HP" + 
            "\r\n#L34##b#kTrade 2 #rVote Points#k for One-Handed Axe for ATT" + 
            "\r\n#L35##r#kTrade 2 #rVote Points#k for One-Handed BW for ATT" + 
            "\r\n#L36##b#kTrade 2 #rVote Points#k for One-Handed Sword for ATT" + 
            "\r\n#L37##r#kTrade 2 #rVote Points#k for Overall Armor for DEX" + 
            "\r\n#L38##b#kTrade 2 #rVote Points#k for Pole Arm for ATT" + 
            "\r\n#L39##r#kTrade 2 #rVote Points#k for Shield for DEF" + 
            "\r\n#L40##b#kTrade 2 #rVote Points#k for Shoes for DEX" + 
            "\r\n#L41##r#kTrade 2 #rVote Points#k for Shoes for Jump" + 
            "\r\n#L42##b#kTrade 2 #rVote Points#k for Shoes for Speed" + 
            "\r\n#L43##r#kTrade 2 #rVote Points#k for Spear for ATT" + 
            "\r\n#L44##b#kTrade 2 #rVote Points#k for Staff for Magic ATT" + 
            "\r\n#L45##r#kTrade 2 #rVote Points#k for Topwear for DEF" + 
            "\r\n#L46##b#kTrade 2 #rVote Points#k for Two-Handed Aex for ATT" + 
            "\r\n#L47##r#kTrade 2 #rVote Points#k for Two-Handed BW for ATT" + 
            "\r\n#L48##b#kTrade 2 #rVote Points#k for Two-Handed Sword for ATT" + 
            "\r\n#L49##r#kTrade 2 #rVote Points#k for Wand for Magic ATT");
				    } else if (selection == 8) {
                var price = 5000000;
                if (cm.getPlayer().getVote Points() > 0) {      
                    cm.getPlayer().gainVote Points(-1);                    
                   cm.modifyNX(6000, 4);
                   cm.dispose();
                     } else {
                   cm.sendOk ("You don't have enough #rVote Points#k!");
                   cm.dispose();
                   }
                } else if (selection == 9) {
                var price = 10000000;
                if (cm.getPlayer().getVote Points() > 1) {      
                    cm.getPlayer().gainVote Points(-2);                    
                   cm.modifyNX(12000, 4);
                   cm.dispose();
                     } else {
                   cm.sendOk ("You don't have enough #rVote Points#k!");
                   cm.dispose();
                   }
                } else if (selection == 10) {
                var price = 15000000;
                if (cm.getPlayer().getVote Points() > 2) {      
                    cm.getPlayer().gainVote Points(-3);                    
                   cm.modifyNX(18000, 4);
                   cm.dispose();
                     } else {
                   cm.sendOk ("You don't have enough #rVote Points#k!");
                   cm.dispose();
                   }
                } else if (selection == 11) {
                var price = 20000000;
                if (cm.getPlayer().getVote Points() > 3) {      
                    cm.getPlayer().gainVote Points(-4);                    
                   cm.modifyNX(24000, 4);
                   cm.dispose();
                     } else {
                   cm.sendOk ("You don't have enough #rVote Points#k!");
                   cm.dispose();
                   }
                } else if (selection == 12) {
                if (cm.getPlayer().getVote Points() > 4) {      
                    cm.getPlayer().gainVote Points(-5);                    
                   cm.modifyNX(30000, 4);
                   cm.dispose();
                     } else {
                   cm.sendOk ("You don't have enough #rVote Points#k!");
                   cm.dispose();
}
}
else if (selection == 13) {
                if (cm.getPlayer().getVote Points() > 0) {   
                    cm.getPlayer().gainVote Points(-1); 
		cm.gainItem(4031545, 2);
		cm.sendOk("Nice! Here are your 2 #i4031545#! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("You don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 14) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(4031545, 4);
		cm.sendOk("Nice! Here are your 4 #i4031545#! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("You don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 15) {
                if (cm.getPlayer().getVote Points() > 2) {   
                    cm.getPlayer().gainVote Points(-3); 
		cm.gainItem(4031545, 6);
		cm.sendOk("Nice! Here are your 6 #i4031545#! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("You don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 16) {
                if (cm.getPlayer().getVote Points() > 3) {   
                    cm.getPlayer().gainVote Points(-4); 
		cm.gainItem(4031545, 8);
		cm.sendOk("Nice! Here are your 8 #i4031545#! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("You don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 17) {
                if (cm.getPlayer().getVote Points() > 4) {   
                    cm.getPlayer().gainVote Points(-5); 
		cm.gainItem(4031545, 10);
		cm.sendOk("Nice! Here are your 10 #i4031545#! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("You don't have enough #rVote Points#k!")
		cm.dispose();
		}
                   }
else if (selection == 18) {
                if (cm.getPlayer().getVote Points() > 0) {   
                    cm.getPlayer().gainVote Points(-1); 
		cm.gainItem(5220020, 2);
		cm.sendOk("Nice! Here are your 2 #i5220020#! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("You don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 19) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(5220020, 5);
		cm.sendOk("Nice! Here are your 5 #i5220020#! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("You don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 20) {
                if (cm.getPlayer().getVote Points() > 2) {   
                    cm.getPlayer().gainVote Points(-3); 
		cm.gainItem(5220020, 8);
		cm.sendOk("Nice! Here are your 8 #i5220020#! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("You don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 21) {
                if (cm.getPlayer().getVote Points() > 3) {   
                    cm.getPlayer().gainVote Points(-4); 
		cm.gainItem(5220020, 11);
		cm.sendOk("Nice! Here are your 11 #i5220020#! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("You don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 22) {
                if (cm.getPlayer().getVote Points() > 4) {   
                    cm.getPlayer().gainVote Points(-5); 
		cm.gainItem(5220020, 14);
		cm.sendOk("Nice! Here are your 14 #i5220020#! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("You don't have enough #rVote Points#k!")
		cm.dispose();
		}
                   }
else if (selection == 23) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2040603, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 24) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2044503, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 25) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2041024, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 26) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2041025, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 27) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2044703, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 28) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2044603, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 29) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2043303, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 30) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2040807, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 31) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2040806, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 32) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2040006, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 33) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2040007, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 34) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2043103, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 35) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2043203, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 36) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2043003, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 37) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2040506, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 38) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2044403, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 39) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2040903, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 40) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2040709, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 41) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2040710, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 42) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2040711, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 43) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2044303, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 44) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2043803, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 45) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2040403, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 46) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2044103, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 47) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2044203, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 48) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2044003, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 49) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(2043703, 1);
		cm.sendOk("Nice job, here is your GM Scroll! You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
else if (selection == 50) {
               cm.sendSimple ("Alright, here are the options \r\n#bYou currently have:#k (#r" + cm.getPlayer().getVote Points() + "#k) #bVote Points.#e#d" + 
            "\r\n#L51##r#kTrade 1 #rVote Point#k for 350 #i4001126#" + 
            "\r\n#L52##b#kTrade 2 #rVote Points#k for 800 #i4001126#" + 
            "\r\n#L53##r#kTrade 3 #rVote Points#k for 1350 #i4001126#" + 
            "\r\n#L54##b#kTrade 4 #rVote Points#k for 2000 #i4001126#" + 
            "\r\n#L55##r#kTrade 5 #rVote Points#k for 2750 #i4001126#");
			}
else if (selection == 51) {
                if (cm.getPlayer().getVote Points() > 0) {   
                    cm.getPlayer().gainVote Points(-1); 
		cm.gainItem(4001126, 350);
		cm.sendOk("Nice job, here are your Maple Leaves. You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
	else if (selection == 52) {
                if (cm.getPlayer().getVote Points() > 1) {   
                    cm.getPlayer().gainVote Points(-2); 
		cm.gainItem(4001126, 800);
		cm.sendOk("Nice job, here are your Maple Leaves. You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
	else if (selection == 53) {
                if (cm.getPlayer().getVote Points() > 2) {   
                    cm.getPlayer().gainVote Points(-3); 
		cm.gainItem(4001126, 1350);
		cm.sendOk("Nice job, here are your Maple Leaves. You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
	else if (selection == 54) {
                if (cm.getPlayer().getVote Points() > 3) {   
                    cm.getPlayer().gainVote Points(-4); 
		cm.gainItem(4001126, 2000);
		cm.sendOk("Nice job, here are your Maple Leaves. You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
	else if (selection == 55) {
                if (cm.getPlayer().getVote Points() > 4) {   
                    cm.getPlayer().gainVote Points(-5); 
		cm.gainItem(4001126, 2750);
		cm.sendOk("Nice job, here are your Maple Leaves. You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
	else if (selection == 60) {
                if (cm.getPlayer().getVote Points() > 0) {   
                    cm.getPlayer().gainVote Points(-1); 
		cm.gainItem(2049100, 2);
		cm.sendOk("Nice job, here are your 2 #i2049100#. You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
	else if (selection == 61) {
                if (cm.getPlayer().getVote Points() > 0) {   
                    cm.getPlayer().gainVote Points(-1); 
		cm.gainItem(2340000, 1);
		cm.sendOk("Nice job, here are your 3 #2340000#. You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
	else if (selection == 62) {
                if (cm.getPlayer().getVote Points() > 0) {   
                    cm.getPlayer().gainVote Points(-1); 
		cm.gainItem(4032013, 3);
		cm.sendOk("Nice job, here is your #i4032013#. You now have:(#r" + cm.getPlayer().getVote Points() + "#k) Vote Points.");
		cm.dispose();
	  } else {
		cm.sendOk("#rYou don't have enough #rVote Points#k!")
		cm.dispose();
		}
	}
			}
				 