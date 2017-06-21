importPackage(Packages.tools); 
var player;

function start(ms) { 
	player = ms.getPlayer();
    player.resetEnteredScript(); 
    ms.getClient().getSession().write(MaplePacketCreator.showEffect("event/space/start")); 
    player.startMapEffect("Please rescue Gaga within the time limit.", 5120027); 
	var map = player.getMap();
	if (map.getTimeLeft() > 0) {
		ms.getClient().getSession().write(MaplePacketCreator.getClock(map.getTimeLeft()));
	} else {
		map.addMapTimer(180);
	}
	ms.useItem(2360002);
}  