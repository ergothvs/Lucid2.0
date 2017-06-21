function enter(pi) {
	switch(pi.getMapId()) {
		case 262031100:
			if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
				pi.warpParty(262031200, 0);
			 } else {
				pi.playerMessage(5, "The portal is locked! Eliminate all of Hilla's guardians blocking the portal!");
			}
			break;
		case 262031200:
			if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
				pi.warpParty(262031300, 0);
			} else {
				pi.playerMessage(5, "The portal is locked! Eliminate all of Hilla's guardians blocking the portal!");
			}
			break;
	}
}