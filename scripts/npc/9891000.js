var status = 0;
var mapid = 90000001; // map id to warp to

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
	     cm.sendNext("Hello and Welcome to #e#bWiz World#k#n, #h #, an alternate version of #rMaple World#k. It is a #eparallel universe#n of the one you grew in, many things are similar yet different at the same time. Currently we're on one of the #b5 Moons of Wiz#k, natural satellites of the center of this universe, better known as #bWiz#k.\r\n\r\nAnyway, I know what is on your mind. Why am I here?");
	   } else if (status == 1) {
	     cm.sendNext("I'm the one that summoned you here, our future is bleak.\r\n#dThe Black Magician#k reigns over multiple universes and only one of them survived the onslaught of darkness, the #rMaple World#k. I have chosen you because you've slain #dThe Black Magician#k and saved your world from tyranny. And since you are not native to this world, the passage of time will be distorted.");
	   } else if (status == 2) {
	     cm.sendOk("Wait you didn't defeat #dThe Black Magician#k? Hmmm, I must've picked you at the wrong time... Well it doesn't really matter now, you will have plenty of time to grow stronger, I will contact you when the time is at hand. But before that, let me guide you, I will show you the marvels of #e#bWiz World#k#n.");		 
	   } else if (status == 3) {
	     cm.warp(mapid, 0);
		 cm.dispose();
	   }
}