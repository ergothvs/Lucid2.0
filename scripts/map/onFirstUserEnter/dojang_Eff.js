/*
 *@Author:     Moogra, Traitor
 *@Map(s):     All Dojo fighting maps
 *@Function:   Spawns dojo monsters and handles time
*/
importPackage(Packages.server.life);
importPackage(Packages.tools);

function start(ms) {
    try {
        ms.getPlayer().resetEnteredScript();
        var stage = (ms.getPlayer().getMap().getId() / 100) % 100;
        if (stage % 6 == 1)
            ms.getPlayer().setDojoStart();
        if (ms.getPlayer().getMap().getCharacters().size() == 1)
            ms.getPlayer().showDojoClock();
        if (stage % 6 > 0) {
            var realstage = stage - ((stage / 6) | 0);
            ms.getClient().getSession().write(MaplePacketCreator.getEnergy("energy", ms.getPlayer().getDojoEnergy()));
            var mob = MapleLifeFactory.getMonster(9300183 + realstage);
            if (mob != null && ms.getPlayer().getMap().getMonsterById(9300183 + realstage) == null && ms.getPlayer().getMap().getMonsterById(9300216) == null) {
                mob.setBoss(false);
                ms.getPlayer().getMap().spawnDojoMonster(mob);
                ms.getClient().getSession().write(MaplePacketCreator.playSound("Dojang/start"));
                ms.getClient().getSession().write(MaplePacketCreator.showEffect("dojang/start/stage"));
                ms.getClient().getSession().write(MaplePacketCreator.showEffect("dojang/start/number/" + realstage));
            }
        }
    } catch(err) {
        ms.getPlayer().dropMessage(err);
    }
}