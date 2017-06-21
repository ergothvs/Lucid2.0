
function init() {
    em.setProperty("leader", "true");
    em.setProperty("state", "0");
}

function setup(eim) {
	em.setProperty("leader", "true");
	em.setProperty("state", "1");
    var eim = em.newInstance("AswanOffSeason");
	eim.setInstanceMap(955000100);
    eim.startEventTimer(1800000); // 30 min
    return eim;
}

function playerEntry(eim, player) {
	var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
    eim.broadcastPacket(CField.showEffect("aswan/stageEff/stage"));
	eim.broadcastPacket(CField.showEffect("aswan/stageEff/number/1"));
	player.getMap().startMapEffect("Destroy the Guardian Tower and it's minions throughout the map to continue.", 5120059);
}



function changedMap(eim, player, mapid) { // TODO because this instance is never ending..
	if (mapid != 955000100 && mapid != 955000200 && mapid != 955000300) {
		eim.unregisterPlayer(player);
		var map = em.getChannelServer().getMapFactory().getMap(262010000);
		player.changeMap(map, map.getPortal(0));

		if (eim.disposeIfPlayerBelow(0, 0)) {
			em.setProperty("state", "0");
			em.setProperty("leader", "true");
		}
    }
}

function scheduledTimeout(eim) {
		eim.unregisterPlayer(player);
		var map = em.getChannelServer().getMapFactory().getMap(262010000);
		player.changeMap(map, map.getPortal(0));
		player.dropMessage(5, "[Azwan] You have failed to defeat her gang in the time limit.");
	if (eim.disposeIfPlayerBelow(0, 0)) {
		em.setProperty("state", "0");
		em.setProperty("leader", "true");
	}
}

function allMonstersDead(eim) {
}

function playerDead(eim, player) {
    return 0;
}

function playerRevive(eim, player) {
	player.dropMessage(5, "[Hilla] You are weak! You will never defeat me!!");
    player.addHP(50);
	var mapToSpawnId = player.getMapId();
    var map = eim.getMapFactory().getMap(mapToSpawnId);
    player.changeMap(map, map.getPortal(0));
    return true;
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobid) {
    return 1;
}

function leftParty (eim, player) {
	end(eim);
}
function disbandParty (eim) {
	end(eim);
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 921160000);
	em.setProperty("state", "0");
	em.setProperty("leader", "true");
}

function clearPQ(eim) {
    end(eim);
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
	if (eim.disposeIfPlayerBelow(0, 0)) {
		em.setProperty("state", "0");
		em.setProperty("leader", "true");
	}
}

function onMapLoad(eim, player) {}
function cancelSchedule() {}