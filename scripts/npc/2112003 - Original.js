/**
 * @author: Eric
 * @npc: Juliet
 * @func: Romeo and Juliet GMS-like PQ
*/
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	var em = cm.getEventManager("Juliet");
	if (status == 0) {
		if (cm.getPlayer().getMapId() == 261000021) {
			cm.sendSimple("#eParty Quest: Romeo and Juliet>#n\r\nMagatia faces a grave threat. We need brave adventurers to answer our call.#b\r\n#L0#Listen to Juliet's story.\r\n#L1#Start the quest.\r\n#L2#Find a party.\r\n#L3#Make a necklace with Alcando Marbles.\r\n#L4#Combine two necklaces into one.#k"); //#L5#Check the number of tries left for today.
		} else {
		switch(cm.getPlayer().getMapId()) {
			case 926110000:
			case 926110001:
			case 926110100:
			case 926110300:
			case 926110400:
				status = 16;
				cm.sendSimple("How can I help you?#b\r\n#L0#Where am I?\r\n#L1#I want to get out of here!");
				break;
			case 926110200:
				if (cm.haveItem(4001131,1)) {
					cm.sendOk("Oh, the Letter I wrote! Thank you!"); // TODO.. :(
					cm.gainItem(4001131,-1);
					em.setProperty("stage", "1");
					cm.dispose();
				} else if (cm.haveItem(4001134, 1)) {
					status = 19;
					cm.sendSimple("Hey, isn't that #bAlcando's Experiment Files?#k This should prove that the Zenumists are not responsible for stealing Alcando's source of energy! Please give me that right now!\r\n\r\n#b#L0#Give the Alcando's Experiment Files to Juliet.#l");
				} else if (cm.haveItem(4001135, 1) && em.getProperty("stage4").equals("1")) {
					status = 19;
					cm.sendSimple("Hey, isn't that #bAlcando's Experiment Files?#k This should prove that the Zenumists are not responsible for stealing Alcando's source of energy! Please give me that right now!\r\n\r\n#b#L1#Give the Alcando's Experiment Files to Juliet.#l");
				} else {
					status = 16;
					cm.sendSimple("How can I help you?#b\r\n#L0#Where am I?\r\n#L1#I want to get out of here!");
				}
				break;
			case 926110401:
				status = 24;
				cm.sendNext("Thank you so much for your help in saving Romeo. Thank you so, so much.");
				break;
			case 926110600:
				status = 29;
				cm.sendNext("Again, thank you so much for helping us out. Magatia may still be on the threat of danger, but I think this is enough to snuff out the big fire for now.");
				// cm.openNpc(2112018);
				break;
		}
	}
	} else if (status == 1) {
		if (selection == 0) {
			status = 10;
			cm.sendNext("I, Juliet, am deeply in love with Romeo, and I know he loves me too. The problem is, I am in Alcando Society, and Romeo is under Zenumist Society, so we are not meant to be together...");
		} else if (selection == 1) {
			var items = [4001130, 4001131, 4001132, 4001133, 4001134, 4001135];
			for (var i = 0; i < items.length; i++) {
				cm.removeAll(items[i]);
			}
			if (em == null || !cm.getPlayer().isGM()) {
				cm.sendOk("Please try again later.");
				cm.dispose();
				return;
			}
			if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
				cm.sendOk("The leader of the party must be here.");
			} else {
				var party = cm.getPlayer().getParty().getMembers();
				var mapId = cm.getPlayer().getMapId();
				var next = true;
				var size = 0;
				var it = party.iterator();
			while (it.hasNext()) {
				var cPlayer = it.next();
				var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
				if (ccPlayer == null || ccPlayer.getLevel() < 70 || ccPlayer.getLevel() > 255) {
					next = false;
					cm.dispose();
				}
				size += (ccPlayer.isGM() ? 4 : 1);
			}	
			if (next && (cm.getPlayer().isGM() || size == 4)) {
				var prop = em.getProperty("state");
				if (prop.equals("0") || prop == null) {
					em.startInstance(cm.getPlayer().getParty(), cm.getPlayer().getMap(), 200);
					cm.sendOk("This is the lab where rumors are abound that a suspicious noise can be heard from here every night. If there's anything hidden in here, it has to be in this place. Please look thoroughly into this lab.");
				} else {
					cm.sendOk("Another party quest has already entered on this channel.");
				}
			} else {
				cm.sendOk("All 4 members of your party must be here and above level 70.");
			}
			}
		} else if (selection == 2) {
			cm.findParty();
			cm.dispose();
		} else if (selection == 3) {
			cm.sendOk("Eric is working on scripting the #eexact#n #bRomeo and Juliet PQ#k from #rGlobal MapleStory#k.\r\nBecause this script has not been translated to GMS-like, it is unfunctional.\r\n\r\nIf you have a screenshot or text that this window uses, please report this to our forums.");
			cm.dispose();
		} else if (selection == 4) {
			cm.sendOk("Eric is working on scripting the #eexact#n #bRomeo and Juliet PQ#k from #rGlobal MapleStory#k.\r\nBecause this script has not been translated to GMS-like, it is unfunctional.\r\n\r\nIf you have a screenshot or text that this window uses, please report this to our forums.");
			cm.dispose();
		}
	} else if (status == 11) {
		cm.sendNextPrev("What you should know is that is wasn't always like this. That is why we would like nothing more than to serve as a bridge between Zenumist and Alcando and contribute towards peace between these two societies.");
	} else if (status == 12) {
		cm.sendNextPrev("We have tried our best, but unfortunately, Magatia is currently #bon the verge of a full-fledged war.#k That is because a while ago, the #bpower source of both Zenumist and Alcando went missing.#k Both societies are now blaming one another for this incidient, and it is getting worse by the day.");
	} else if (status == 13) {
		cm.sendNextPrev("I recently received a tip from an anonymous source that it is actually a deed of a #b3rd personal,#k totally unrelated to this. In order to prevent this civil war of Magatia and have my love for Romeo fully blossom, we must find that #b3rd person#k and stop that person from destroying this great town.");
	} else if (status == 14) {
		cm.sendNextPrev("Show your bravery, and help defend the peace in Magatia!\r\n#e - Level:#n 70 or higher #r(Recommended Level: 70-119)#k\r\n#e - Time Limit:#n 20 min\r\n#e - Number of Players:#n 4\r\n#e - Reward:#n\r\n#i1122117# Juliet's Pendant\r\n(Can be obtained from #bJuliet#k once you collect #r20#k #bAlcando Marbles.#k)\r\n#i1122118# Symbol of Eternal Love\r\n(Can be traded for 1 #bRomeo's Pendant#k and 1 #bJuliet's Pendant#k)");
	} else if (status == 15) {
		cm.dispose();
	} else if (status == 17) {
		if (selection == 0) {
			switch(cm.getPlayer().getMapId()) { // TODO: Script ALL of these.. :/
				case 926110000:
					cm.sendOk("This is the lab where rumors are abound that a suspicious noise can be heard from here every night. If there's anything hidden in here, it has to be in this place. Please look thoroughly into this lab.");
					break;
				case 926110001:
					cm.sendOk("Please, eliminate all the monsters! I'll be right behind you.");
					break;
				case 926110100:
					cm.sendOk("These beakers have leaks in them. We must pour the Suspicious Liquid to the beakers' brims so we can continue.");
					break;
				case 926110200:
					cm.sendOk("We must stop the conflict between Alcadno and Zenumist! Find me Alcadno files first, then Zenumist!");
					break;
				case 926110300:
					cm.sendOk("We must get to the top of the Lab, each of your members.");
					break;
				case 926110400:
					cm.sendOk("Whenever you are ready, we shall go and save my love.");
					break;
			}
			cm.dispose();
		} else if (selection == 1) {
			cm.warp(261000021, 0);
			cm.dispose();
		}
	} else if (status == 20) {
		if (selection == 0) {
			cm.sendOk("In order to stop the war, we still need to find a hard evidence that convinces the Zenumists that it's not Alcando's fault. I'll leave the door open so please find a concrete evidence for us!");
			cm.gainItem(4001134, -1);
			em.setProperty("stage4", "1");
			cm.dispose();
		} else if (selection == 1) { // TODO: broadcast cm.sendOk to the party/map!
			cm.showEffect(true, "quest/party/clear"); // map
			cm.showEffect(false, "quest/party/clear"); // client
			cm.playSound(true, "Party1/Clear"); // map
			cm.playSound(false, "Party1/Clear"); // client
			cm.sendOk("Now that it's proven that neither Zenumist nor Alcando are responsible for stealing each other's source of energy, the war can finally be prevented. Thank you so much for your hard work. I have opened the door that'll lead you to the next stage, so please find out who is responsible for this mess in the first place!!");
			cm.gainItem(4001135, -1);
			em.setProperty("stage4", "2");
			cm.getMap().getReactorByName("jnr3_out3").hitReactor(cm.getClient());
			cm.dispose();
		}
	} else if (status == 25) {
		cm.sendNextPrev("Unfortunately, Yulete got away from us, so this is not over yet. I doubt he is too far from here, so please find him right now!!");
	} else if (status == 26) {
		cm.warpParty(926110500);
		cm.dispose();
	} else if (status == 30) {
		cm.sendNextPrev("Eventhough our love is still littered with obstacles, I can promise you that I will not give up in my quest to be with Romeo until the end.");
	} else if (status == 31) {
		cm.sendNextPrev("Here's the Alcando Marble that I have had for the longest time. Please take it. I have also given you some rewards for the job well done. I will now lead your way out of here.");
	} else if (status == 32) {
		var items = [4001130, 4001131, 4001132, 4001133, 4001134, 4001135];
		for (var i = 0; i < items.length; i++) {
			cm.removeAll(items[i]);
		}
		var em = cm.getEventManager("Juliet");
		if (em != null) {
			var itemid = cm.getMapId() == 926100600 ? 4001160 : 4001159;
			if (!cm.canHold(itemid, 1)) {
				cm.sendOk("Please make some space in your ETC inventory.");
				cm.dispose();
				return;
			}
			cm.gainItem(itemid, 1);
			if (em.getProperty("stage").equals("2")) {
				cm.gainExpR(140000); // TODO: calculate the exp gains after boss kill, not here.
			} else {
				cm.gainExpR(105000);
			}
		}
		cm.getPlayer().endPartyQuest(1205);
		cm.warp(926110700, 0);
		cm.addTrait("will", 1); // todo: randomize
		cm.addTrait("sense", 1); // todo: randomize
		cm.dispose();
	}
}