var status;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
   if (mode == 1) {
        status++;
    } else {
        status--;
  }
    if (status == 0) {
      if (cm.getLevel() > 199) {
	cm.sendSimple("#gCongratulations#k on your #brebirth!#k\r\nHere's how it works, I will reborn you into a beginner.\r\nYou keep your stats, items, etc.\r\nYou might lose #rSOME NON-COMPATIBLE#k skills.\r\nThis means no Shadow Partner on Bowmasters, stuff like that.\r\nSo, would you like to #bRebirth?#k\r\n\r\n#L1337#Yes\r\n#L1336#No");
    } else {
        cm.sendOk("You have not proven your strength.\r\nPlease become #rLEVEL 200#k before you talk to me.");
	cm.safeDispose();
    }
 }
  if (selection == 1337) {
	cm.getPlayer().doReborn();
	cm.getPlayer().levelUp();
        cm.sendOk("You have rebirthed! Talk to me when you want a #rJob Advancement#k.");
        cm.dispose();
   } else if (selection == 1336) {
	cm.sendOk("Please come back to me once you think you are strong enough to #bRebirth.#k");
        cm.dispose();
   }
}