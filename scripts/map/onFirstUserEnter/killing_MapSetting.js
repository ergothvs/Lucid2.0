importPackage(Packages.tools);

function start(ms) { 
    var pq = ms.getPyramid();
    ms.getPlayer().resetEnteredScript();
    ms.getClient().getSession().write(MaplePacketCreator.getClock(pq.timer()));
}