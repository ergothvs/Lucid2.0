function action(mode, type, selection) {
	if (cm.getNpc() >= 9901000) {
		cm.sendNext("Hello #h0#, I am in the Hall of Fame for reaching LEVEL 200.");
	} else {
		cm.sendNext("The following script ("+cm.getNpc()+") has not been coded.");
	}
	cm.safeDispose();
}