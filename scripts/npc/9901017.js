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
			cm.sendSimple ("Hey, I'm the #rOccupation Level System#k of #rMaple Blade#k. Your current occupation level is: (#b" + cm.getPlayer().getOccupation() + "#k) #rAlso, please read the Occupation Level chart :). #k\r\n#L0#I would like to level up an Occupation Level!\r\n#L1#Uhh.. I'm not sure yet..\r\n#L3#View the Occupation Level chart!");
				 } else if (selection == 0) {
          cm.sendSimple ("Your current Occupation Level is:(#b" + cm.getPlayer().getOccupation() + "#k). For more info, read the Occupation Level chart. I would like to advance to...#e#d" +
                 "#k\r\n#L45#Lv.1 #bNoob Blader#k #r[Req Level 70]" +
                 "#k\r\n#L46#Lv.2 #bStorm Blader#k #r[Req RB 1]" +
                 "#k\r\n#L47#Lv.3 #bSoul Blader#k #r[Req RBs 5]" +
                 "#k\r\n#L48#Lv.4 #bBlade Reaper#k #r[Req RBs 15]" +
                 "#k\r\n#L49#Lv.5 #bDevil Blader#k #r[Req RBs 30]#r (Different Quest)" +
                 "#k\r\n#L50#Lv.6 #bNight Blader#k #r[Req RBs 70]#r (Different Quest)" +
		         "#k\r\n#L51#Lv.7 #bBankai Blader#k #r[Req RBs 150]#r (Different Quest)" +
                 "#k\r\n#L52#Lv.8 #bHoly Blader#k #r #r[Req RBs 450] (Different Quest)" +
				 "#k\r\n#L53#Lv.9 #bLieutenant Blader#k #r[Req RBs 700] (Different Quest)" +
                 "#k\r\n#L54#Lv.10 #bBlade Master#k #r[Req RBs 2000] (Different Quest)#k");
				 } else if (selection == 45) {
				  if (cm.getLevel() > 69 && cm.HasOccupation0())  {
				  cm.changeOccupationById(100);
				  cm.gainItem(1142109, 1);
				  cm.sendOk("Congratulations, you are now a #bNoob Blader#k :) Your exp rate is now 300x!");
				  cm.dispose();
				  } else {
				  cm.sendOk("You're not a high enough level to be a #bNoob Blader#k, or you're already a #bNoob Blader#k or higher than #bNoob Blader#k. Please read the Occupation Level Chart.")
				  cm.dispose();
				  }
				} else if (selection == 46) {
				if(cm.getPlayer().getrebirths() > 0 && cm.HasOccupation1()) {
				  cm.changeOccupationById(110);
				  cm.sendOk("Congratulations, you are now a #bStorm Blader#k :) Your Exp rate is now 350x, and Meso rate 100x.");
				  cm.dispose();
				  } else {
				  cm.sendOk("You don't have enough rebirths to be a #bStorm Blader#k, or you're already a #bStorm Blader#k or higher than #bStorm Blader#k, or your Occupation Level is way behind. Please read the Occupation Level Chart.");
				  cm.dispose();
				  }
				} else if (selection == 47) {
				if(cm.getPlayer().getrebirths() > 4 && cm.HasOccupation2()) {
				  cm.changeOccupationById(120);
				  cm.sendOk("Congratulations, you are now a #bSoul Blader#k :) Your Exp rate is now 400x, and Meso rate 200x.");
				  cm.dispose();
				  } else {
				  cm.sendOk("You don't have enough rebirths to be a #bSoul Blader#k, or you're already a #bSoul Blader#k or higher than #bSoul Blader#k, or your Occupation Level is way behind. Please read the Occupation Level Chart.");
				  cm.dispose();
				  }
				} else if (selection == 48) {
				if(cm.getPlayer().getrebirths() > 14 && cm.HasOccupation3()) {
				  cm.changeOccupationById(130);
				  cm.sendOk("Congratulations, you are now a #bBlade Reaper#k :) Your Exp rate is now 450x, and Meso rate 300x.");
				  cm.dispose();
				  } else {
				  cm.sendOk("You don't have enough rebirths to be a #bBlade Reaper#k, or you're already a #bBlade Reaper#k or higher than #bBlade Reaper#k, or your Occupation Level is way behind. Please read the Occupation Level Chart.");
				  cm.dispose();
				  }
				} else if (selection == 49) {
				if(cm.getPlayer().getrebirths() > 29 && cm.HasOccupation4()) {
				  cm.changeOccupationById(140);
				  cm.sendOk("Congratulations, you are now a #bDevil Blader#k :) Your Exp rate is now 500x, and Meso rate 400x.");
				  cm.dispose();
				  } else {
				  cm.sendOk("You don't have enough rebirths to be a #bDevil Blader#k, or you're already a #bDevil Blader#k or higher than #bDevil Blader#k, or your Occupation Level is way behind. Please read the Occupation Level Chart.");
				  cm.dispose();
				  }
				} else if (selection == 50) {
				if(cm.getPlayer().getrebirths() > 69 && cm.HasOccupation5()) {
				  cm.warp(300000012);
				  cm.sendOk("If you want to advance to next Occupation Level, please try talking to the 6th Occupation :)");
				  cm.dispose();
				  } else {
				  cm.sendOk("You don't have enough rebirths to be a #bNight Blader#k, or you're already a #bNight Blader#k or higher than #bNight Blader#k, or your Occupation Level is way behind. Please read the Occupation Level Chart.");
				  cm.dispose();
				  }
				  } else if (selection == 51) {
				if(cm.getPlayer().getrebirths() > 149 && cm.HasOccupation6()) {
				  cm.warp(541010100);
				  cm.sendOk("If you want to advance to next Occupation Level, please try talking to the 7th Occupation :)");
				  cm.dispose();
				  } else {
				  cm.sendOk("You don't have enough rebirths to be a #bBankai Blader#k, or you're already a #bBankai Blader#k or higher than #bBankai Blader#k, or your Occupation Level is way behind. Please read the Occupation Level Chart.");
				  cm.dispose();
				  }
				  } else if (selection == 52) {
				if(cm.getPlayer().getrebirths() > 449 && cm.HasOccupation7()) {
				  cm.warp(970010000);
				  cm.sendOk("If you want to advance to next Occupation Level, please try talking to the 8th Occupation :)");
				  cm.dispose();
				  } else {
				  cm.sendOk("You don't have enough rebirths to be a #bHoly Blader#k, or you're already a #bHoly Blader#k or higher than #bHoly Blader#k, or your Occupation Level is way behind. Please read the Occupation Level Chart.");
				  cm.dispose();
				  }
				  } else if (selection == 53) {
				if(cm.getPlayer().getrebirths() > 699 && cm.HasOccupation8()) {
				  cm.warp(261030000);
				  cm.sendOk("If you want to advance to next Occupation Level, please try talking to the 9th Occupation :)");
				  cm.dispose();
				  } else {
				  cm.sendOk("You don't have enough rebirths to be a #bLieutenant Blader#k, or you're already a #bLieutenant Blader#k or higher than #bLieutenant Blader#k, or your Occupation Level is way behind. Please read the Occupation Level Chart.");
				  cm.dispose();
				  }
				  } else if (selection == 54) {
				if(cm.getPlayer().getrebirths() > 1999 && cm.HasOccupation9()) {
				  cm.warp(240050310);
				  cm.sendOk("If you want to advance to next Occupation Level, please try talking to the Blade Master :)");
				  cm.dispose();
				  } else {
				  cm.sendOk("You don't have enough rebirths to be a #bBlade Master#k, or you're already a #bBlade Master#k or higher than #bBlade Master#k, or your Occupation Level is way behind. Please read the Occupation Level Chart.");
				  cm.dispose();
				  }
				} else if (selection == 3) {
				  cm.sendNext("#eAs your Occupation Level increases, your Exp and Meso rate will increase at the same time. Take a look at the Occupation Level chart. (RBs = rebirths) When you have the following requirements for next occupation, please talk to me again.\r\n \r\nLevel 0 - #rNoob#k\r\nLevel 1 - #rNoob Blader#k #b[Req Level 70]#k (Exp rate 300x)\r\nLevel 2 - #rStorm Blader#k #b[Req RBs 1]#k (Exp rate 350x, Meso rate 100x)\r\nLevel 3 - #rSoul Blader#k #b[Req RBs 5]#k (Exp rate 400x, Meso rate 200x)\r\nLevel 4 - #rBlade Reaper#k #b[Req RBs 15]#k (Exp rate 450x, Meso rate 300x)\r\nLevel 5 - #rDevil Blader#k #b[Req RBs 30]#k (Exp rate 500x, Meso rate 400x)\r\nLevel 6 - #rNight Blader#k #b[Req RBs 70]#k (Exp rate 550x, Meso rate 500x)\r\nLevel 7 - #rBankai Blader#k #b[Req RBs 150]#k (Exp rate 750x, Meso rate 600x)\r\nLevel 8 - #rHoly Blader#k #b[Req RBs 450]#k (Exp rate 1000x, Meso rate 700x)\r\nLevel 9 - #rLieutenant Blader#k #b[Req RBs 700]#k (Exp rate 1250x, Meso rate 800x) \r\nLevel 10 - #rBlade Master#k #b[Req RBs 2000]#k (Exp rate 1750x, Meso rate 1000x)");
				  cm.dispose();
				} else if (selection == 4) {
				if(!cm.HasOccupationM(0) && cm.HasOccupationM(1) && cm.HasOccupationM(200) && cm.HasOccupationM(210) && cm.HasOccupationM(220) && cm.HasOccupationM(230) && cm.HasOccupationM(220)) {
                    cm.sendSimple ("#eAlright, If you have enough rebirths. #e#d" +
                 "#k\r\n#L10#Advance to Corporal #rExp rate x3#k #bMeso rate x2#k (5 rebirths Required)#k" +
                 "#k\r\n#L11#Advance to Sergeant #rExp rate x4#k #bMeso rate x2#k (15 rebirths Required)#k" +
                 "#k\r\n#L12#Advance to Lieutenant #rExp rate x5#k #bMeso rate x3#k (50 rebirths Required)#k " +
				 "#k\r\n#L13#Advance to Commander #rExp rate x7#k #bMeso rate x5#k (100 rebirths Required)#k" +
                 "#k\r\n#L14#More options coming soon! :)" );
				} else {
          cm.sendOk ("Nope.");
		  cm.dispose();
		  			}
				} else if (selection == 10) {
				if(cm.getPlayer().getrebirths() > 4 && !cm.HasOccupationM(100)) {
				  cm.changeOccupationById(110);
				  cm.sendNext("Congratulations, you are now a Corporal! Your exp rates has been set to 450x, and Meso 100x!");
				  cm.dispose();
				  } else {
				  cm.sendOk("You don't have enough rebirths!");
				  cm.dispose();
				} 
		    }
		}	
	}