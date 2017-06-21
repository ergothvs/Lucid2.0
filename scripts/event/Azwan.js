
function init() {
    
}

function setup(eim) {
    var a = Randomizer.nextInt();
    while (em.getInstance("Azwan") != null) {
        a = Randomizer.nextInt();
    }
    var eim = em.newInstance("Azwan");
    return eim;
}

function playerEntry(eim, player) {
    var map = Integer.parseInt(eim.getProperty("Global_StartMap"));
    player.changeMap(eim.getMapFactory().getMap(map), eim.getMapFactory().getMap(map).getPortal("sp"));
}



function changedMap(eim, player, mapid) {
    if (mapid < 262020000 || mapid >= 262023000) {
        eim.unregisterPlayerAzwan(player);
    }
}

function scheduledTimeout(eim) {
}

function allMonstersDead(eim) {
    var startmap = Integer.parseInt(eim.getProperty("Global_StartMap"));
    //eim.broadcastPacket(CPacket.showEffect("aswan/clear"));
    //eim.broadcastPacket(CPacket.UIPacket.showAzwanClear());
    var exit = em.getChannelServer().getMapFactory().getMap(Integer.parseInt(eim.getProperty("Global_ExitMap")));
    var it = eim.getPlayers().iterator();
    while (it.hasNext()) {
        var chr = it.next();
        //chr.changeMap(exit, exit.getPortal(0));
        chr.makeNewAzwanShop();
        chr.azwanReward(exit, exit.getPortal(0));
    }
    eim.unregisterAll();
    if (eim != null) {
        eim.dispose();
    }
}

function playerDead(eim, player) {
    return 0;
}

function playerRevive(eim, player) {
}

function playerDisconnected(eim, player) {
    if (eim.getProperty("Global_MinPerson") == null) {
        return -1;
    }
    return -Integer.parseInt(eim.getProperty("Global_MinPerson"));
}

function monsterValue(eim, mobid) {
    return 1;
}

function leftParty(eim, player) {
/*
    if (eim.getPlayerCount() < Integer.parseInt(eim.getProperty("Global_MinPerson"))) {
        var exit = em.getChannelServer().getMapFactory().getMap(Integer.parseInt(eim.getProperty("Global_ExitMap")));
        var it = eim.getPlayers().iterator();
        while (it.hasNext()) {
            var chr = it.next();
            chr.changeMap(exit, exit.getPortal(0));
        }
        eim.unregisterAll();
        if (eim != null) {
            eim.dispose();
        }
    }
    */
}

function disbandParty(eim) {
    var exit = eim.getPlayers().get(0).getClient().getChannelServer().getMapFactory().getMap(Integer.parseInt(eim.getProperty("Global_ExitMap")));
    var it = eim.getPlayers().iterator();
    while (it.hasNext()) {
        var chr = it.next();
        chr.changeMap(exit, exit.getPortal(0));
    }
    eim.unregisterAll();
    if (eim != null) {
        eim.dispose();
    }
}

function clearPQ(eim) {
}

function playerExit(eim, player) {
    var exit = eim.getPlayers().get(0).getClient().getChannelServer().getMapFactory().getMap(Integer.parseInt(eim.getProperty("Global_ExitMap")));
    var it = eim.getPlayers().iterator();
    while (it.hasNext()) {
        var chr = it.next();
        chr.changeMap(exit, exit.getPortal(0));
    }
    eim.unregisterAll();
    if (eim != null) {
        eim.dispose();
    }
}

function onMapLoad(eim, player) {
}

function cancelSchedule(a) {
}