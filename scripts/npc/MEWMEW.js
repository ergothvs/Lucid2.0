/**
 * @author: Eric
 * @func: Starter Quest
 * @npc: MEWMEW
*/

var status = 0;
var jobs = [["Warrior", 100], ["Magician", 200], ["Bowman", 300], ["Thief", 400], ["Pirate", 500], ["Dawn Warrior", 1100], ["Blaze Wizard", 1200], ["Wind Archer", 1300], ["Night Walker", 1400], ["Thunder Breaker", 1500], ["Aran", 2000], ["Evan", 2200], ["Mercedes", 2300], ["Phantom", 2400], ["Dual Blade", 430], ["Cannoneer", 501], ["Jett", 508], ["Demon Slayer", 3100], ["Battle Mage", 3200], ["Wild Hunter", 3300], ["Mechanic", 3500], ["Mihile", 5100]];

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	if (cm.getMapId() == 2) {
		switch (status) {
			case 0:
				cm.sendNextNoESC("..Well, I'm MEWMEW.. Say, you look beaten up.. Can you still walk?");
				break;
			case 1:
				cm.sendNextPrevS("..Ugh, I think so..");
				break;
			case 2:
				cm.sendNextPrevS("Well, let's get your body warmed up before we exchange more information.", 1);
				break;
			case 3:
				cm.sendNextPrevS("You see that cliff area on the right? Go through that portal. See you there.", 1);
				cm.hideNpc(cm.getNpc());
				break;
			case 4:
				cm.sendNextPrevS("Sigh...I should go back to sleep.");
				cm.warp(cm.getMapId() + 1);
				cm.dispose();
				break;
		}
	} else if (cm.getMapId() == 3) {
		switch(status) {
			case 0:
				if (cm.haveItem(2000000, 1) || cm.haveItem(2000003, 1) || cm.haveItem(4000253, 1)) { // Get ID for Milk Bottles
					if (cm.haveItem(4000252, 10)) {
						cm.sendOk("Nya! Good work there, small fry. Alright, a promise is a promise, come into the castle and I'll tell you what you need to know.");
						cm.hideNpc(cm.getNpc());
					} else
						cm.sendOk("Come back when you finish what I asked for.");
					cm.dispose();
				} else
					cm.sendNextS("So, can you tell me where I am now?", 2);
				break;
			case 1:
				cm.sendNextPrev("Hang on, not now... Do you see those mice..? Those fucks are responsible for making such a mess here.");
				break;
			case 2:
				cm.sendNextPrev("I'm sure you still have strength to attack, right? You are pretty weak, but I'm sure you can take them out.");
				break;
			case 3:
				if (mode > 0) {
					cm.sendNextPrev("Here, take these potions.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#b#i2000000:# Red Potion\r\n#i2000003:# Blue Potion#k");
					if (!cm.haveItem(2000000, 1) && !cm.haveItem(2000003, 1)) {
						cm.gainItem(2000000, 20); // Red Potion
						cm.gainItem(2000003, 20); // Blue Potion
					}
				}
				break;
			case 4:
				cm.sendNextPrev("If you run low on HP or MP, just open up your inventory (the default key is \"I\") and drag it to the key you wish to use.\r\nDouble-clicking on the selected item in your inventory will also consume the item.");
				break;
			case 5:
				cm.sendNextPrevS("Yeah..Pretty sure I know all this already.", 2);
				break;
			case 6:
				cm.sendAcceptDecline("Whatever..I'm hungry. Steal the milk they drop once you clap one of those mother fuckers and I'll tell you what you want to know afterwards.");
				break;
			case 7:
				if (mode > 0) {
					cm.getPlayer().dropMessage(5, "[MEWMEW] Kill 10 Mice and bring me back 10 Milk Bottles.");
				}
				cm.dispose();
				break;
		}
	} else if (cm.getMapId() == 4 && cm.getPlayer().getJobId() == 0 && !cm.haveItem(4000999, 1)) {
		switch(status) {
			case 0:
				cm.sendNext("..Alright, I'm sure you are pretty curious on what is happening.. Just.. sit tight and let me tell you what's going on.");
				break;
			case 1:
				cm.sendNextPrevS("Okay, first of all, before you say anything.. TELL ME WHERE I AM!", 2);
				break;
			case 2:
				cm.sendNextPrev("..You're in #eDevelopment#n, #h #.. ");
				break;
			case 3:
				cm.sendNextPrevS("#eDevelopment#n.. What kind of world name is that?", 2);
				break;
			case 4:
				cm.sendNextPrev("..Our world is called #eDevelopment#n because just as yourself and this world in general.. It's incomplete, and as far as we know, it will never be completed.. Our three creators are always bringing us new life to this world.");
				break;
			case 5:
				cm.sendNextPrevS("..3 creators? Is that them behind us?! The three statues? Who are they, and and who are you?", 2);
				break;
			case 6:
				var rando = cm.getPlayer().rand(1, 10);
				cm.sendNextPrev("Our creators " + ((rando >= 1 && rando <= 5) ? "Paul and Eric" : "Eric and Paul") + " created this world and are continuing to make it bigger. You might come across the creators in the future.");
				break;
			case 7:
				cm.sendNextPrev("As for me.. I may look cute and fluffy, but I am the guardian of everyone. When someone enters this world, I guide them through the beginning and when they need help.");
				break;
			case 8:
				cm.sendNextPrev("Anyways #h #, I have spoken enough. Here, take this as a reward.\r\n\r\n#fUI/UIWindow.img/Quest/reward#\r\n\r\n#i1002000:# MEWMEW Ears\r\n#i4000999:# Munny\r\n\r\n#fUI/UIWindow.img/QuestIcon/7/0# 1,000,000 mesos");
				if (!cm.haveItem(4000999, 1) && cm.getMeso() < 1000000) {
					cm.gainItem(1002000, 1); // MEWMEW Ears
					cm.gainItem(4000999, 300); // Munny
					cm.gainMeso(1000000); // 1,000,000 Mesos
				}
			case 9:
				cm.sendNextPrevS("Uhm.. Thanks, I guess..", 2);
				break;
			case 10:
				var text = "Now, which path you would like to take? You can't take back your decision, so choose wisely.";
				for (var i = 0; i < jobs.length; text += "\r\n#L" + i + "##b" + jobs[i][0] + "#k", i++);
				cm.sendSimple(text);
				break;
			case 11:
				if (mode > 0) {
					cm.getPlayer().changeJob(jobs[selection][1]);
				}
				cm.sendNextS("Good choice.", 1);
				break;
			case 12:
				cm.sendNextPrevS("Before you go, you should click on Kio or Kurry to change your hairstyle. You can always find them in the Free Market.", 1);
				break;
			case 13:
				cm.sendOkS("When you are done here, go enter that portal and you'll transported to our main destination.", 1);
				cm.dispose();
		}
	} else if (cm.getMapId() == 4 && cm.getPlayer().getJobId() > 0) {
		switch(status) {
			case 0:
				cm.sendNextNoESC("OH, I almost forgot! Type @commands to view this worlds commands. Plus, each time you vote for #eDevelopment#n you receive 150 #b#z4000999##k!");
				break;
			case 1:
				cm.sendNextPrevS("Thank you for everything.");
				break;
			case 2:
				cm.sendNextPrevS("Where do I go now?");
				break;
			case 3:
				cm.sendNextPrevS("Just go where the wind blows you.", 1);
				break;
			case 4:
				cm.warp(cm.getMapId() + 1);
				cm.dispose();
				break;
		}
	} else {
		cm.sendOk("Nya! I'm MEWMEW!");
		cm.dispose();
	}
}