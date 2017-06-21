// Phantom Warper : To Henesys

function start() {
	cm.sendYesNo("Do you want to go to #bHenesys#k?");
}

function action (m, t, s) {
  if (m > 0) {
	cm.warp(100000000, 0);
  }
  cm.dispose();
}