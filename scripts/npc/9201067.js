/*
  * NPC: Claw Machine
  * Desc: 100 Wiz Coins - > Medal(s)
*/
items = [1142000, 1142001, 1142002, 1142003, 1142004, 1142005, 1142006, 1142007, 1142008, 1142009, 1142010, 1142011, 1142012, 1142013, 1142014, 1142015, 1142016, 1142017, 1142018, 1142019, 1142020, 1142021, 1142022, 1142023, 1142024, 1142025, 1142026, 1142027, 1142028, 1142029, 1142030, 1142031, 1142032, 1142033, 1142034, 1142035, 1142036, 1142037, 1142038, 1142039, 1142040, 1142041, 1142042, 1142043, 1142044, 1142045, 1142046, 1142047, 1142048, 1142049, 1142050, 1142051, 1142052, 1142053, 1142054, 1142055, 1142056, 1142057, 1142058, 1142059, 1142060, 1142061, 1142062, 1142063, 1142064, 1142070, 1142071, 1142072, 1142073, 1142074, 1142075, 1142076]; 

function start() { 
    text = "Medals! Medals! Medals!\r\nYou also may #brollover the item#k to view more #binformation#k about that particular item. They are each #r100 Wiz Coins.#b"; 
    for (var i = 0; i < items.length; text += "\r\n #L"+i+"# #z"+items[i]+"##l", i++); 
    cm.sendSimple(text); 
} 

function action(m,t,s) { 
    if (m > 0) { 
		if (cm.haveItem(4007099, 100)) { 
            if (cm.canHold(items[s])) { 
                cm.gainItem(4007099, -100);
                cm.gainItem(items[s], 1); 
				cm.sendOk("Well, here is your #b#t " + items[s] + " ##k that you wanted!");
            } else 
                cm.sendOk("Your inventory is too full, care to make some space?"); 
        } else 
            cm.sendOk("You must have #b100 Wiz Coins#k in order to buy this item. Please check if you have enough Wiz Coins."); 
    } 
    cm.dispose(); 
}  