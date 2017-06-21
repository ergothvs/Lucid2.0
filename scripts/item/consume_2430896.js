//Angelic Buster Hot Time 2
var extra = 
[1212044, 1222044, 1302248, 1312135, 1322181, 1332205, 1342075, 1362074, 1372161,
1382192, 1402172, 1412122, 1422124, 1432150, 1442202, 1452189, 1462177, 1472197,
1482151, 1492162, 1522078, 1532081];

function start() {
    if (im.getInventory(1).getNumFreeSlot() < 2 || im.getInventory(2).getNumFreeSlot() < 3) {
        im.sendOk("Please make more inventory space.");
        im.dispose();
        return;
    }
    im.gainItem(2290724, 1);
    im.gainItem(2500000, 1);
    im.gainPotentialItem(1222006, 1, 19); //Unique
    im.gainItem(2430909, 1);
    if (im.nextInt(100) < 10) {
        im.gainItem(extra[im.nextInt(extra.length)], 1);
    }
    im.removeItem(2430896);
    im.dispose();
}