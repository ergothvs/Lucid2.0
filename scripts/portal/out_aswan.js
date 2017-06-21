function enter(pi) {
    pi.playPortalSE();
    var map = pi.getSavedLocation("MULUNG_TC");
	pi.warp(map, 0);
	return true;
}