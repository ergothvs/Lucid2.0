var status = 0;
var item = 4007098; // item required to trade
var amount = 1; // amount of the required item
var item_gain = 4000115; // item they gain if they click yes
var item_amount = 1; // amount of this ^^^

function start() {
   cm.sendYesNo("Oh? Ho ho What do we have here? Do you perhaps have #bWiz Coins#k? How about we do a little trade? One #bWiz Coin#k for my #dCog#k. What do you say?");
}

function action(mode, type, selection) {
	   if (mode > 0) { // assuming they actually have the required item to trade for new one..
			if (cm.haveItem(item, amount)) {
				cm.removeAll(item);
				cm.gainItem(item_gain, item_amount); 
				cm.sendOk("Ho ho ho! A wise choice indeed! Enjoy your new #dCog#k! Come again soon heh heh heh...");
				cm.dispose();
			} else {
				cm.sendOk("Huh? I don't see any #bWiz Coins#k on you.\r\n\r\nDon't bother me if you have nothing to trade.");
				cm.dispose();
			}
	   } else {
	     cm.sendOk("Huh? You're missing out on a great deal here! You might wanna reconsider...");
		 cm.dispose();
	   }
}