var status = 0;
var occupationName = ["Leprechaun", "NX Addict", "Hacker #b(Does nothing)#k", "The Transformers AutoBots #b(Does nothing)#k", "Smega Whore"];
var occupationId = [200, 300, 400, 600, 700];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) { // allow GMs unlimited changes if they don't know about !occ :o
	  if (cm.getPlayer().getOccId() == 1 || cm.getPlayer().getOccId() == 0 || cm.getPlayer().isGM()) { // 0 = null (None) | 1 = Noob
		cm.sendNext("#rHaha, Occupations you say?#k\r\nThey are only for the #eskilled#n.\r\nIf you want one, you'll need to meet my #rrequirements#k.\r\nI'd like to see you reach #e10 Rebirths#n and earn #e100,000,000 mesos#n.\r\n\r\n#bIf you have my requirements, lets talk business.#k");
	  } else {
		cm.sendOk("You already have an #eOccupation#n.");
		cm.dispose();
	  }
	  } else if (status == 1) {
	 if (cm.getPlayer().getReborns() >= 10 && cm.getMeso() >= 100000000) {
	     cm.sendNext("#b*talks business*#k\r\n\r\n#eAre you sure you want to trade up 100,000,000 mesos#n?");
	   } else if (cm.getPlayer().getReborns() < 10 && cm.getMeso() < 100000000) {
	    cm.sendOk("Ha! You don't even have #e10 Rebirths#n and #e100,000,000 mesos#n.");
		cm.dispose();
	   } else if (cm.getPlayer().getReborns() < 10) {
		cm.sendOk("Ha! You don't even have #e10 Rebirths#n.");
		cm.dispose();
		} else {
		cm.sendOk("Ha! You don't even have #e100,000,000 mesos#n.");
		cm.dispose();
	}
    } else if (status == 2) {
     if (cm.getPlayer().getReborns() >= 10 && cm.getMeso() >= 100000000) {
	// lol @ packet editing, doubt this is going to happen and not necessary 
		if (cm.getPlayer().isDonator()) { // Donator
			occupationName = ["Sniper", "Leprechaun", "NX Addict", "Hacker #b(Does nothing)#k", "The Transformers AutoBots #b(Does nothing)#k", "Smega Whore", "Terrorist"];
			occupationId = [100, 200, 300, 400, 600, 700, 800];
		}
		var occscript = "Nice job! You have matched my #rrequirements#k.\r\n\r\nChoose a(n) #gOccupation#k:"; 
		for (var i = 0; i < occupationId.length; occscript += "\r\n #L" + i + "# " + occupationName[i] + "#l", i++); 
			cm.sendSimple(occscript); 
	 } else {
	  status = 1337; // unexistant status so they don't status++
	  cm.sendOk("y u packet edit la . zzzz ..");
	  cm.dispose();
	 }
	 } else if (status == 3) {
	   //var selection = occupationId[selection] + 1; // 101++
	   if (cm.getPlayer().isDonator()) {
	   if (selection == 0) {
	     cm.sendSimple("#rOccupation : #eSniper#n#k\r\n\r\n#bDescription: A #eSniper#n Occupation allows players to snipe victims, killing them instantly with a BOOM HEADSHOT! effect.#k\r\n#L1#I want to become a Sniper\r\n#L99#Go back");
	   } else if (selection == 1) {
	     cm.sendSimple("#rOccupation : #eLeprechaun#n#k\r\n\r\n#bDescription: A #eLeprechaun#n Occupation allows players to clone themselves! You can clone up to #e3#n times.#k\r\n#L2#I want to become a Leprechaun\r\n#L99#Go back");
	   } else if (selection == 2) {
	     cm.sendSimple("#rOccupation : #eNX Addict#n#k\r\n\r\n#bDescription: A #eNX Addict#n Occupation allows players to gain random amounts of NX after killing monsters! \r\n- Higher Occupation Level means more NX gained at random!#k\r\n#L3#I want to become a NX Addict\r\n#L99#Go back");
	   } else if (selection == 3) {
	     cm.sendSimple("#rOccupation : #eHacker#n#k\r\n\r\n#bDescription: A #eHacker#n Occupation allows players to ...#k\r\n#L4#I want to become a Hacker\r\n#L99#Go back");
	   } else if (selection == 4) {
	     cm.sendSimple("#rOccupation : #eThe Transformers AutoBots#n#k\r\n\r\n#bDescription: A #eTransformers AutoBot#n Occupation allows players to ...#k\r\n#L5#I want to become a Transformers AutoBot\r\n#L99#Go back");
	   } else if (selection == 5) {
	     cm.sendSimple("#rOccupation : #eSmega Whore#n#k\r\n\r\n#bDescription: A #eSmega Whore#n Occupation allows players to Smega without using smegas via @smega! \r\n- Higher Occupation Level will allow you to use @avi and others!#k\r\n#L6#I want to become a Smega Whore\r\n#L99#Go back");
	   } else if (selection == 6) {
		 cm.sendSimple("#rOccupation : #eTerrorist#n#k\r\n\r\n#bDescription: A #eTerrorist#n Occupation allows players to spawn bombs that will detonate and possibly bounce players away!#k\r\n#L7#I want to become a Terrorist\r\n#L99#Go back");
	     }
	   } else {
	   if (selection == 0) {
	     cm.sendSimple("#rOccupation : #eLeprechaun#n#k\r\n\r\n#bDescription: A #eLeprechaun#n Occupation allows players to clone themselves! You can clone up to #e3#n times.#k\r\n#L2#I want to become a Leprechaun\r\n#L99#Go back");
	   } else if (selection == 1) {
	     cm.sendSimple("#rOccupation : #eNX Addict#n#k\r\n\r\n#bDescription: A #eNX Addict#n Occupation allows players to gain random amounts of NX after killing monsters! \r\n- Higher Occupation Level means more NX gained at random!#k\r\n#L3#I want to become a NX Addict\r\n#L99#Go back");
	   } else if (selection == 2) {
	     cm.sendSimple("#rOccupation : #eHacker#n#k\r\n\r\n#bDescription: A #eHacker#n Occupation allows players to ...#k\r\n#L4#I want to become a Hacker\r\n#L99#Go back");
	   } else if (selection == 3) {
	     cm.sendSimple("#rOccupation : #eThe Transformers AutoBots#n#k\r\n\r\n#bDescription: A #eTransformers AutoBot#n Occupation allows players to ...#k\r\n#L5#I want to become a Transformers AutoBot\r\n#L99#Go back");
	   } else if (selection == 4) {
	     cm.sendSimple("#rOccupation : #eSmega Whore#n#k\r\n\r\n#bDescription: A #eSmega Whore#n Occupation allows players to Smega without using smegas via @smega! \r\n- Higher Occupation Level will allow you to use @avi and others!#k\r\n#L6#I want to become a Smega Whore\r\n#L99#Go back");
	     } 
	   }
	 } else if (status == 4) {
		if (selection < 99) { // "Choose occ"
			cm.sendOk(" You're occupation is now a(n) " + occupationName[selection - (cm.getPlayer().isDonator() ? 1 : 2)] + ".\r\n\r\n#dYou can type @occinfo for more information about your #eOccupation#n.#k");
			cm.getPlayer().setOccupation(occupationId[selection - (cm.getPlayer().isDonator() ? 1 : 2)]);
			cm.gainMeso(-100000000);
			cm.dispose();
		} else if (selection == 99) {
			status = 1;
			cm.sendNext("Let's go back, shall we?");
		} else { 
			cm.sendOk("Report this problem to #rforums#k.");
			cm.dispose();
	  }
    }
}