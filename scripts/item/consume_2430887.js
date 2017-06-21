//Luminous Hot Time 1
var extra = 
[1212017, 1222017, 1302173, 1312072, 1322107, 1332148, 1332149, 1342040, 1362022,
1372100, 1382124, 1402111, 1412071, 1422073, 1432099, 1442136, 1452129, 1462118,
1472141, 1482102, 1492101, 1522020, 1532037];

function start() {
    if (im.getInventory(1).getNumFreeSlot() < 2 || im.getInventory(2).getNumFreeSlot() < 1
        || im.getInventory(3).getNumFreeSlot() < 1) {
        im.sendOk("Please make more inventory space.");
        im.dispose();
        return;
    }
    im.gainPotentialItem(1003409, 1, 19); //Unique
    im.gainItem(2501000, 1);
    if (im.nextInt(100) < 10) {
        im.gainItem(extra[im.nextInt(extra.length)], 1);
    }
    if (im.nextInt(100) < 10) {
        im.gainItem(3700049, 1);
    }
    im.removeItem(2430887);
    im.dispose();
}