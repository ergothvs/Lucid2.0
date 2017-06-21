
importPackage(Packages.world);
importPackage(Packages.server.life);
importPackage(Packages.server);
importPackage(Packages.server.maps);
importPackage(Packages.tools);
importPackage(Packages.client);

var exitMap;
var allowMapChange = true;
var minPlayers = 3;

var stage = 0;
var wave = 0;

var stages = [[
[6130101, 6300005], //wave1
[6130101, 6300005], //wave2
[6130101, 6300005], //wave3
[6130101, 6300005], //wave4
[6130101, 6300005]], //wave5

[[6130101, 6300005], //wave1
[6130101, 6300005], //wave2
[6130101, 6300005], //wave3
[6130101, 6300005], //wave4
[6130101, 6300005]]]; //wave5        //3-dimensional arrays, get me!

var numToSpawn = 15; //number of each monster per wave

var stage3bosses = Array(37, 399, -337, -368, 110, 453, -178, -244, -102, -102, -216, -202, 273, -178, -32);   //can just rip the code from OPQ for this
var bossnames = Array(-1, -1, -1, 200000, 400000, 550000, 800000, 3500000, -1, -1, 6000000, 6000000, 7500000, -1, 20000000);  //-1 means original hp
var bossStage = 0;


var finalboss = 8800002; //zakum 3rd body

var mapIds = Array(270040000, 270040100, 270050000, 270050100);
var mapBoundX1 = Array(-1499, -1905, -1005, -3);
var mapBoundX2 = Array(1642, -185, -329, -3);

var mapBoundY = Array(-41, -41, -41, -42);


function init() { // Initial loading.
    exitMap = em.getChannelServer().getMapFactory().getMap(270030411);
    em.setProperty("TTPQOpen", "true"); // allows entrance.
    em.setProperty("shuffleReactors", "true");
    instanceId = 1;
}

function monsterValue(eim, mobId) { // Killed monster.
    var map = getMap(eim);
    
    if(map.getSpawnedMonstersOnMap() == 0)
    {
       if(stage < 2)
       {
          map.broadcastMessage(MaplePacketCreator.serverNotice(6, "[PQ] " + "Congratulations on defeating wave " + (wave + 1) + " of stage " + (stage + 1) + "!"));
          waveEnd(eim);
       } else if (stage == 2) {
     //    spawnNextBoss(eim);
       } else if (stage == 3)  {
       }
       //  finishEvent(eim);
    }
    return 1;
}

function setup() {
    var eim = em.newInstance("TTPQ");
    var eventTime = 30 * (1000 * 60);
    var p;
    var map = getMap(eim);
    map.toggleDrops();

    stage = 0;
    wave = 0;
    bossStage = 0;


    em.schedule("timeOut", eim, eventTime); // invokes "timeOut" in how ever many seconds.
    eim.startEventTimer(eventTime); // Sends a clock packet and tags a timer to the players.
    eim.setProperty("pqFinished", "false");
    return eim;
}

function playerEntry(eim, player) {

    player.changeMap(getMap(eim, stage), getMap(eim, stage).getPortal(0));
    player.setallowedMapChange(false);
    spawnWave(eim); // this is now started by NPC
}

function playerDead(eim, player) {
}

function playerRevive(eim, player) { // player presses ok on the death pop up.
    player.setallowedMapChange(true);
    if (eim.isLeader(player) || party.size() <= minPlayers) { // Check for party leader
        var party = eim.getPlayers();
        for (var i = 0; i < party.size(); i++)
            playerExit(eim, party.get(i));
        eim.dispose();
    } else
        playerExit(eim, player);
}

function playerDisconnected(eim, player) {
    var party = eim.getPlayers();
    if (eim.isLeader(player) || party.size() < minPlayers) {
        var party = eim.getPlayers();
        for (var i = 0; i < party.size(); i++)
            if (party.get(i).equals(player))
                removePlayer(eim, player);
            else
                playerExit(eim, party.get(i));
        eim.dispose();
    } else
        removePlayer(eim, player);
}

function leftParty(eim, player) {
    var party = eim.getPlayers();
    if (party.size() < minPlayers) {
        for (var i = 0; i < party.size(); i++)
            playerExit(eim,party.get(i));
        eim.dispose();
    } else
        playerExit(eim, player);
}

function disbandParty(eim) {
    var party = eim.getPlayers();
    for (var i = 0; i < party.size(); i++) {
        playerExit(eim, party.get(i));
    }
    eim.dispose();
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    player.setallowedMapChange(true);
    player.changeMap(exitMap, exitMap.getPortal(0));
}

function removePlayer(eim, player) {  //for disconnected peeps / peeps who have left
    eim.unregisterPlayer(player);
    player.getMap(eim).removePlayer(player);
    player.setallowedMapChange(true);
    player.setMap(exitMap);
}

function clearPQ(eim) {
    var party = eim.getPlayers();
    for (var i = 0; i < party.size(); i++)
        playerExit(eim, party.get(i));
    eim.dispose();
}

function allMonstersDead(eim) {
}

function cancelSchedule() {
}

function playerMapChange(eim, player) {
         return player.allowedMapChange();
}

function dispose() {
    em.schedule("OpenTTPQ", 5000); // 5 seconds ?
}

function OpenTTPQ() {
    em.setProperty("TTPQOpen", "true");
}

function timeOut(eim) {
    if (eim != null) {
        if (eim.getPlayerCount() > 0) {
            var pIter = eim.getPlayers().iterator();
            while (pIter.hasNext())
                playerExit(eim, pIter.next());
        }
        stage = 0;
        wave = 0;
        eim.dispose();
    }
}

function spawnNextBoss(eim) {
       bossStage++;
       if (stage > bossid.length - 1)   //arrays start at 0, length starts at 1
       finishEvent(eim);
         var mob = MapleLifeFactory.getMonster(bossid[stage]);
        /*& var overrideStats = new MapleMonsterStats();
         if (bosshp[stage] == -1) {
            bosshp[stage] = mob.getHp();
         }
         overrideStats.setExp(mob.getExp() * (bosshp[stage] / mob.getHp()));  // exp directly proportional to ratio of orig & nerfed HPs
	 overrideStats.setHp(bosshp[stage]);
	 overrideStats.setMp(mob.getMaxMp());
         mob.setOverrideStats(overrideStats);   */
	 eim.registerMonster(mob);
         var map = getMap(eim, stage);
         map.spawnMonsterOnGroudBelow(mob, new java.awt.Point(randX(), mapBoundY[stage]));
    }

  function finishEvent(eim) {
    eim.setProperty("pqFinished", "true");
  }
  
  function contains(a, obj) {
  var i = a.length;
  while (i--) {
    if (a[i] === obj) {
      return true;
    }
  }
  return false;
}

function getMap(eim, stage)
{
      return eim.getMapInstance(mapIds[stage], true);
}

function getMapFromID(eim, id)
{
      return eim.getMapInstance(id, true);
}


function spawnNPC(eim, npcId, x, y, map)
{
   var point = new java.awt.Point(x, y);
  // var map = getMap(eim);
   var npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null) {
                npc.setPosition(point);
                npc.setCy(y);
                npc.setRx0(x);
                npc.setRx1(x);
                npc.setFh(map.getFootholds().findBelow(point).getId());
                map.addMapObject(npc);
                map.broadcastMessage(MaplePacketCreator.spawnNPC(npc));
            }
}

function waveEnd(eim)
{
   wave++;
   if(wave > stages[stage].length) //completed a stage
   {
     map.broadcastMessage(MaplePacketCreator.serverNotice(6, "[PQ] " + "Congratulations on completing stage" + stage + "! You will be warped to the next stage in 5 seconds."));
     wave = 0;
     stage++;
     eim.schedule("warpToNextStage", 5000);
   } else {
     map.broadcastMessage(MaplePacketCreator.serverNotice(6, "[PQ] " + "The next wave will be spawned in 5 seconds."));
     eim.schedule("spawnWave", 5000);
   }
}


function spawnWave(eim)
{
  for (var x = 0; x < stages[stage][wave].length; x++)
  {
         for (var y = 0; y < numToSpawn; y++) {
        	    var mob = net.sf.odinms.server.life.MapleLifeFactory.getMonster(stages[stage][wave][x]);
        		/*var overrideStats = new net.sf.odinms.server.life.MapleMonsterStats();
        		overrideStats.setHp(mob.getHp() * 3);
        		overrideStats.setExp(mob.getExp() / 4);
        		overrideStats.setMp(mob.getMaxMp());
        		mob.setOverrideStats(overrideStats);
        		mob.setHp(mob.getHp() * 3);          */
       		    eim.registerMonster(mob);
                    map.spawnMonsterOnGroudBelow(mob, new java.awt.Point(randX(), mapBoundY[stage]));
                }
  }
}

function warpToNextStage(eim)
{
    map = getMap(eim, stage);

    spawnNPC(eim, 2043000, -155, 1779, map);
    spawnNPC(eim, 1052015, 344, -4717, map);

    var iter = map.getPortals().iterator();    //kills the portals
    	while (iter.hasNext()) {
    		var p = iter.next();
                p.setScriptName("omPQ");
	}
	
    var pIter = eim.getPlayers().iterator();
        while (pIter.hasNext()) {
                player = pIter.next();
                player.changeMap(map, map.getPortal(0));
        }
}

function randX() {
	return mapBoundX1[stage] + Math.floor(Math.random() * mapBoundX2[stage]);
}
/*
mapbounds

270040000
-1499 / -41
1642 / -41

270040100
-1905 / -41
-185 / -41

270050000
-1005 / -41
-329 / -41

270050100
-3/-42

*/
