var status = 0;
var mapid = 90000004; // map id to warp to
var item = 4001101; // item required to trade
var amount = 15; // amount of the required item

function start() {
	if (cm.haveItem(item, amount)) {
		cm.sendOk("Is that what I think it is? Well done! I just love eating these Rice Cakes. Shame you only can get them from killing these poor bunnies... Oh well. Lets move on!");
	} else {
		cm.sendOk("Looks like its time to test your strength! You see see those monstrous bunnies over there? I need you to slaughter them for I require their delicious Rice Cakes.\r\n\r\nI need " + amount + " #v" + item + "# #t" + item + "#.\r\n\r\nTalk to me as soon as you're done...I'm really hungry...");
		cm.dispose();
	}
}

function action(mode, type, selection) {
	   if (mode > 0) {
			if (cm.haveItem(item, amount)) { // already checking but just in case
				cm.gainItem(item, -amount);
				cm.warp(mapid, 0);
			}
	   }
	   cm.dispose();
}