var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 || mode == -1 && status == 0) {
        cm.sendNextS("This has gotta be the box...", 2);
        cm.dispose();
        return;
    }
    mode == 1 ? status++ : status--;
    if (status == 0) {
        if (cm.itemQuantity(4033194) || cm.itemQuantity(4033195) >= 1) {
            cm.sendOk("I'd better get back downstairs with the potion box before old-man Limber'ts heart finally explodes with rage.");
            cm.dispose();
        }
        if (cm.isQuestActive(20031))
            cm.sendYesNo("All these potions are disgusting! Should we even be selling them?\r\nTake the Potion Box?");
        else {
            cm.sendOk("It doesn't look like you need my potions!");
            cm.dispose();
        }
    } else if (status == 1) {
        cm.gainItem(4033194,1);
        cm.gainItem(4033195,1);
        cm.sendPlayerToNpc("Is this a letter? Must be held together by all the dust...\r\nFrom 'Chromile'... It doesn't say who it's for... Maybe Limbert will want it.");
        cm.dispose();
    }
}