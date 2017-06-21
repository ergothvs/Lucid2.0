var status = 0;
var m;

function start() {
    if (cm.getMapId() == 951000000) {
        cm.sendYesNo("Would you like to go back?");
        m = 1;
        return;
    }
    cm.sendYesNo("Would you like to go to the Monster Park?");
}

function action(mode, type, selection) {
    if (mode == 1) {
        if (m == 1) {
            cm.warp(100000000);
        } else {
            cm.warp(951000000);
        }
    }
    cm.dispose();
}