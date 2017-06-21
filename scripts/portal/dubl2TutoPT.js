function enter(pi) {
    if (pi.getQuestStatus(2600) == 1) {
	pi.warp(103050910, 0);
	return true;
    } else {
	pi.getPlayer().dropMessage(5, "On a rainy day...");
	return false;
    }
    
}