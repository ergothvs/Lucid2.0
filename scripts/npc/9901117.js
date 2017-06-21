var status;
var chanceOfGettingAnything = 0.75; //75%
var stuff = [
//Format: [ItemId, Chance]
[1072344,0.3],
[1072344, 0.3],
[1072344, 0.2],
[1072344, 0.2],
[1072344, 0.2],
];
//Chances must sum to 1

function start() {
    var tot = 0;
    for (var i = 0;i < stuff.length;i++) {
        tot += stuff[i][1];
    }
    if (tot != 1) {
        cm.sendOk("Okay.");
        cm.dispose();
    } else {
        status = -1;
        action(1, 0, 0);
    }
}

function action(mode, type, selection) {
    if (mode < 1) {
        cm.dispose();
    } else {
        status++;
        if (status == 0) {
            cm.sendYesNo("Gamble? Meso.");
        } else if (status == 1) {
            if (cm.haveItem(5220010, 5)) {
                if (Math.random() < 0.75) {
                    var a = Math.random();
                    var b = 0;
                    for (var i = 0;i < stuff.length;i++) {
                        b += stuff[i][1];
                        if (a < b) {
                            cm.giveRandItem(stuff[i][0]);
                            cm.gainItem(5220010, 5);
                            cm.sendOk("Enjoy your item.");
                            cm.dispose();
                            break;
                        }
                    }
                } else {
                    cm.sendOk("Oh no! You got nothing!");
                    cm.dispose();
                }
            } else {
                cm.sendOk("You don't have enough #i5220010#.");
                cm.dispose();
            }
        } else {
            cm.dispose();
        }
    }
}