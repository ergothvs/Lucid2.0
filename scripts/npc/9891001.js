var status = 0;
var mapid = 90000002; // map id to warp to

function start() {
 status = -1;
 action(1,0,0);
}

function action(mode, type, selection) {
    if (mode == 1)
	 status++;
	  else 
	 status--;
	   if (status == 0) {
	     cm.sendNext("I'm sure you noticed the #e4 statues#n on your way here... #eAlpha#n, #eToon#n, #eTenshi#n and #eAkuma#n. These are the so called #rChampions#k of their civilisations, born and raised on each of the respective #bWiz moons#k. Some to be revered, Some to be feared.");
	   } else if (status == 1) {
	     cm.sendOk("If you somehow managed to catch a glimpse of them... You should show your respect and not your disdain towards them.\r\n\r\nAnyway legends aside, it's time to show you the mechanics of this world. Come now, let's continue...");
	   } else if (status == 2) {
	     cm.warp(mapid, 0);
		 cm.dispose();
	   }
}