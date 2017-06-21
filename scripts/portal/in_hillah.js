function enter(pi) {
	try {
		pi.openNpc(2184000);
	} catch (e) {
		pi.playerMessage(5, "Error: " + e);
	}
}