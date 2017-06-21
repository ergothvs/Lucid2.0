/**
 * @author: Jvlaple | Eric
 * @func: Ariant Coliseum one
 */
importPackage(java.lang);
importPackage(Packages.tools.packet);

var exitMap;
var instanceId;
var minPlayers = 2;

function init() {
	instanceId = 1;
}

function monsterValue(eim, mobId) {
	return 1;
}

function setup() {
	instanceId = em.getChannelServer().getInstanceId();
	exitMap = em.getChannelServer().getMapFactory().getMap(980010020);
	doneMap = em.getChannelServer().getMapFactory().getMap(980010010);
	var instanceName = "AriantPQ1" + instanceId;
	var eim = em.newInstance(instanceName);
	var mf = eim.getMapFactory();
	em.getChannelServer().addInstanceId();
	var map = mf.getMap(980010101);
	em.schedule("timeOut", 60000 * 20);
	em.schedule("broadcastClock", 1500);
	eim.setProperty("entryTimestamp",System.currentTimeMillis() + (20 * 60000));
	var tehwat = Math.random() * 3;
	if (tehwat > 1) {
		eim.setProperty("theWay", "darkness");
	} else {
		eim.setProperty("theWay", "light");
	}
	
	return eim;
}

function playerEntry(eim, player) {
	var map = eim.getMapInstance(980010101);
	player.changeMap(map, map.getPortal(0));
	player.getClient().getSession().write(CField.getClock((Long.parseLong(eim.getProperty("entryTimestamp")) - System.currentTimeMillis()) / 1000));
	player.getClient().getSession().write(CField.updateAriantScore(eim.getPlayers()));
}

function playerDead(eim, player) {} // you die you die..

function playerRevive(eim, player) {} // you die you die..

function playerDisconnected(eim, player) { // TODO: update to lithium
	if (eim.isSquadLeader(player, MapleSquadType.ARIANT1)) { //check for party leader
		var squad = player.getClient().getChannelServer().getMapleSquad(MapleSquadType.ARIANT1);
		player.getClient().getChannelServer().removeMapleSquad(squad, MapleSquadType.ARIANT1);
		var party = eim.getPlayers();
		for (var i = 0; i < party.size(); i++) {
			if (party.get(i).equals(player)) {
				removePlayer(eim, player);
			}			
			else {
				playerExit(eim, party.get(i));
			}
		}
		eim.dispose();
	} else {
		// If only 5 players are left, uncompletable:
		var party = eim.getPlayers();
		if (party.size() < minPlayers) {
			for (var i = 0; i < party.size(); i++) {
				playerExit(eim,party.get(i));
			}
			eim.dispose();
		}
		else
			playerExit(eim, player);
	}
}

function leftParty(eim, player) {}

function disbandParty(eim) {}

function playerExit(eim, player) {
	eim.unregisterPlayer(player);
	player.changeMap(exitMap, exitMap.getPortal(0));
}

function removePlayer(eim, player) {
	eim.unregisterPlayer(player);
	player.getMap().removePlayer(player);
	player.setMap(exitMap);
}

function clearPQ(eim) {
	//HTPQ does nothing special with winners
	var party = eim.getPlayers();
	for (var i = 0; i < party.size(); i++) {
		playerExit(eim, party.get(i));
	}
	eim.dispose();
}

function allMonstersDead(eim) {
    //Open Portal? o.O
}

function cancelSchedule() {
}

function timeOut() {
	var iter = em.getInstances().iterator();
	while (iter.hasNext()) {
		var eim = iter.next();
		if (eim.getPlayerCount() > 0) {
			var pIter = eim.getPlayers().iterator();
			while (pIter.hasNext()) {
				playerDone(eim, pIter.next());
			}
		}
		eim.dispose();
	}
}

function playerClocks(eim, player) {
  if (player.getMap().hasTimer() == false){
		player.getClient().getSession().write(CField.getClock((Long.parseLong(eim.getProperty("entryTimestamp")) - System.currentTimeMillis()) / 1000));
		//player.getMap().setTimer(true);
	}
}

function playerTimer(eim, player) {
	if (player.getMap().hasTimer() == false) {
		player.getMap().setTimer(true);
	}
}

function broadcastClock(eim, player) {
	var iter = em.getInstances().iterator();
	while (iter.hasNext()) {
		var eim = iter.next();
		if (eim.getPlayerCount() > 0) {
			var pIter = eim.getPlayers().iterator();
			while (pIter.hasNext()) {
				playerClocks(eim, pIter.next());
			}
		}
	}
	var iterr = em.getInstances().iterator();
	while (iterr.hasNext()) {
		var eim = iterr.next();
		if (eim.getPlayerCount() > 0) {
			var pIterr = eim.getPlayers().iterator();
			while (pIterr.hasNext()) {
				playerTimer(eim, pIterr.next());
			}
		}
	}
	em.schedule("broadcastClock", 1600);
}