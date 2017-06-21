// This is a multi-dimensional array, seperate as: ItemID, Name, Cost, Amount.
rewards = [[1902201, "Amaterasu (Okami) Mount", 2000, 1], [1022199, "Blue Neon Shades", 500, 1], [1004001, "Hollow Ichigo mask", 1000, 1], [1082500, "Monster Energy Gloves", 200, 1], [1022196, "Red Neon Shades", 500, 1], [1442299, "Solar Scythe", 1000, 1], [1382199, "Solar Staff", 1000, 1], [1302999, "Tensa Zangetsu", 1500, 1], [1442202, "Thunder Edge", 1500, 1], [1102299, "Water Cape", 500, 1]];

function start() { 
    text = "Oh hey there! I'm in charge of the #eWizStory#n #rDonator Shop#k!\r\nYou have #r" + cm.getPlayer().getPoints() + "#k Donator Points.\r\nWhat would you like to buy?\r\n#b"; 
    for (var i = 0; i < rewards.length; text += "\r\n#L" + i + "# " + rewards[i][1] + " (" + rewards[i][2] + " Donator Points)#l", i++); 
    cm.sendSimple(text); 
} 

function action(m,t,s) { 
    if (m > 0) { 
		if (cm.getPlayer().getPoints() >= rewards[s][2]) { 
            if (cm.canHold(rewards[s][0])) { 
                cm.getPlayer().setPoints2(-rewards[s][2]); 
			 if (s == 0) {
                cm.gainItem(1902201, 1); //okami mount
				cm.gainItem(1912200, 1);  // okami saddle
		        cm.sendOk("Here's your #bAmaterasu (Okami) Mount#k. Enjoy~");
			} else {
				cm.gainItem(rewards[s][0], rewards[s][3]); 
		        cm.sendOk("Here's your #b" + rewards[s][1] + "#k.");
			}
            } else 
                cm.sendOk("It seems your #einventory#n is #rfull#k. Please make space."); 
        } else 
            cm.sendOk("Uh-oh! You must have at least #b" + rewards[s][2] + " Donator Points#k to purchase this item!"); 
    } 
    cm.dispose(); 
}  