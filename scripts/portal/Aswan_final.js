importPackage(Packages.client);

function enter(pi) {
	switch(pi.getMapId()) {
		case 955000300:
			if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
				pi.getPlayer().gainExp(30000, true, true, true);
				pi.getPlayer().addHonourExp(100 * pi.getPlayer().getHonourLevel());
				pi.getPlayer().dropMessage(5, "You've gained " + 100 * pi.getPlayer().getHonourLevel()+ " Honor EXP!");
				pi.warp(262010000, 0);
			} else {
				pi.playerMessage(5, "You shall not pass! Do not dare continue until you've eliminated all the monsters!");
			}
			break;
	}
}