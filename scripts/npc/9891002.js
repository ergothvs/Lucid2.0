var status = 0;
var item = 4007098; // chicken, change for the item to gain
var amount = 1; // amount of the required item
var item_gain = 4000115; // item they gain if they meet required item
var item_amount = 1; // amount of this ^^^
var coins = 4007098;				
var mapid = 90000003;

function start() {
     if (cm.haveItem(item, amount)) {
      cm.sendOk("Do you need another explanation? Click on the #rMerchant NPC#d while having the #bWiz Coin#k in your inventory. The #rMerchant#d will request a #bWiz Coin#k from you. He will then give you two options to pick from, Yes or No. Pick Yes to complete the trade then, talk to me again.");
	  cm.dispose();
   } else if (cm.haveItem(item_gain, item_amount)) {
      cm.sendOk("You've got the item? Good job! Now if you were wondering how much value 1 #bWiz Coin#k is worth, it's exactly 1 Billion Mesos. #bWiz Coins#k are our main currency here. With it you can buy all sorts of rare items and perhaps even really strong equips. You are also able to get tokens from participating in various events in this world. Alright with that done, you have just learnt the basics on how some shops work in this game. Now, let's see how well you fare against ferocious monsters! Lets go!");
   } else {
     cm.sendOk("Now, I will show you how currency works in this world.\r\n\r\nSee that #rMerchant#d over there? Click on him to begin the trade. You can purchase the item he has on sale using this #bWiz Coin#k.");
	 cm.gainItem(item, amount);
	 cm.dispose();
   }
}

function action(mode, type, selection) {
    if (mode > 0) {
	  cm.removeAll(item_gain);
	  cm.removeAll(coins);	  
	  cm.warp(mapid, 0);
	  cm.dispose();
	} else {
	  cm.dispose(); 
	}
}