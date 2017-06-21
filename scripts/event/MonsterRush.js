/**
 * @author: Eric
 * @desc: Monster Rush System
 * @rev: 2.1 - Multi-spawn randomization
 *
*/
importPackage(Packages.server.life);
var MonsterRush;
var maps = Array(260000000, 680000000, 211000000, 600000000, 120000000);
var mobPosX = Array(108, 2037, -1463, 2031, 1180);
var mobPosY = Array(275, -56, 94, 501, 155);
var exitMap;
var minPlayers = 1;

function init() {
    setup();
}

function setup() {
    //var eim = em.newInstance("MonsterRush_");
    startMonsterRush();
}

function startMonsterRush() {
    em.getChannelServer().yellowWorldMessage("[Monster Rush] Monsters have been summoned in Ariant, Amoria, El Nath, New Leaf City, and Nautilus Harbor. Go rape!");
    var i = 0;
	for( m in maps ) {	
		var map = em.getChannelServer().getMapFactory().getMap(maps[m]);   
        map.killAllMonsters(true);
		var count = Math.floor(Math.random() * 40);
		for(var x = 0; x < count; x++) {			
			var isNx = Math.floor(Math.random() * 10);
			if(isNx <= 4) {	
				em.spawnMrushMob1(map.getId(), mobPosX[i], mobPosY[i]);
				//var mob = net.sf.odinms.server.life.MapleLifeFactory.getMonster(3110300);
				//map.spawnMonsterOnGroudBelow(mob, new java.awt.Point(mobPosX[i], mobPosY[i]));
			} else if(isNx == 10) {
				em.spawnMrushMob2(map.getId(), mobPosX[i], mobPosY[i]);
				//var mob = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9400505);
				//map.spawnMonsterOnGroudBelow(mob, new java.awt.Point(mobPosX[i], mobPosY[i]));
			} else {
				em.spawnMrushMob3(map.getId(), mobPosX[i], mobPosY[i]);
				//var mob = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9400519);
				//map.spawnMonsterOnGroudBelow(mob, new java.awt.Point(mobPosX[i], mobPosY[i]));
			}			
		}
		i++;
	}
}