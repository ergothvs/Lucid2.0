function start() {
	cm.sendNext("Hello, I'm #bSerge#k!\r\nI sell #rCanes#k. Do you want one?");
}

function action (m, t, s) {
  if (m > 0 ) {
	cm.openShop(313);
  }
  cm.dispose();
}