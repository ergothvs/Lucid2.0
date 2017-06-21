var eventmapid = 706041720;
var returnmap = 100000000;

var monster = new Array(
    9400609, 	// andras
	9400623, 	// amdusias
    9400611, 	// crocell
    9400612, 	// marbas
    9400613, 	// valefor
    8220007		// blue mushmom
    );

function init() {
// After loading, ChannelServer
}

function setup(partyid) {
    var instanceName = "JobAdvance2" + partyid;

    var eim = em.newInstance(instanceName);
    // If there are more than 1 map for this, you'll need to do mapid + instancename
    var map = eim.createInstanceMapS(eventmapid);
    map.spawnNpc(1012124, new java.awt.Point(37, -855));

    eim.setProperty("points", 0);
    eim.setProperty("monster_number", 0);

    beginQuest(eim);
    return eim;
}

function beginQuest(eim) { // Custom function
    if (eim != null) {
    	eim.startEventTimer(5000); // After 5 seconds -> scheduledTimeout()
    }
}

function monsterSpawn(eim) { // Custom function
    var monsterid = monster[parseInt(eim.getProperty("monster_number"))];
    var mob = em.getMonster(monsterid);
    switch (monsterid) {
	case 9400609:
	case 9400623:
	case 9400610:
	case 9400611:
	case 9400612:
	case 9400613:
	    var modified = em.newMonsterStats();
	    modified.setOExp(mob.getMobExp() * 10);
	    modified.setOHp(mob.getMobMaxHp() * 4);
	    modified.setOMp(mob.getMobMaxMp() * 1.5);

	    mob.setOverrideStats(modified);
	    break;
	    }
    
    eim.registerMonster(mob);

    var map = eim.getMapInstance(0);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(0, -435));
}
function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
}

function changedMap(eim, player, mapid) {
    if (mapid != eventmapid) {
	eim.unregisterPlayer(player);

	eim.disposeIfPlayerBelow(0, 0);
    }
}

function scheduledTimeout(eim) {
	var party = eim.getPlayers();
	var num = parseInt(eim.getProperty("monster_number"));
    if (num < monster.length) {
	monsterSpawn(eim);
	eim.setProperty("monster_number", num + 1);
    } else {
	//eim.disposeIfPlayerBelow(100, returnmap);
    	for (var i = 0; i < party.size(); i++) {
    		party.get(i).forceCompleteQuest(2013);
    	}
    }
// When event timeout..

// restartEventTimer(long time)
// stopEventTimer()
// startEventTimer(long time)
// isTimerStarted()
}

function allMonstersDead(eim) {
    eim.restartEventTimer(30);


  eim.saveBossQuest(num);

    if (mobnum < monster.length) {
	eim.broadcastPlayerMsg(6, "Prepare! The next boss will appear in a glimpse of an eye!");
} else {
	eim.broadcastPlayerMsg(5, "Talk to .... Npc to Choose your job");
    }
// When invoking unregisterMonster(MapleMonster mob) OR killed
// Happens only when size = 0
}

function playerDead(eim, player) {
// Happens when player dies
}

function playerRevive(eim, player) {
    return true;
// Happens when player's revived.
// @Param : returns true/false
}

function playerDisconnected(eim, player) {
    return 0;
// return 0 - Deregister player normally + Dispose instance if there are zero player left
// return x that is > 0 - Deregister player normally + Dispose instance if there x player or below
// return x that is < 0 - Deregister player normally + Dispose instance if there x player or below, if it's leader = boot all
}

function monsterValue(eim, mobid) {
    return 0;
// Invoked when a monster that's registered has been killed
// return x amount for this player - "Saved Points"
}

function leftParty(eim, player) {
    // Happens when a player left the party
    eim.unregisterPlayer(player);

    var map = em.getMapFactory().getMap(returnmap);
    player.changeMap(map, map.getPortal(0));

    eim.disposeIfPlayerBelow(0, 0);
}

function disbandParty(eim, player) {
    // Boot whole party and end
    eim.disposeIfPlayerBelow(100, returnmap);
}

function clearPQ(eim) {
// Happens when the function EventInstanceManager.finishPQ() is invoked by NPC/Reactor script
}

function removePlayer(eim, player) {
    eim.dispose();
// Happens when the funtion NPCConversationalManager.removePlayerFromInstance() is invoked
}

function registerCarnivalParty(eim, carnivalparty) {
// Happens when carnival PQ is started. - Unused for now.
}

function onMapLoad(eim, player) {
// Happens when player change map - Unused for now.
}

function cancelSchedule() {
}