//Cannoneer Hot Time
var extra = 
[5064000, 5520000, 5150053, 5152057, 5062000, 5230000, 5072000, 5030004, 5130003, 5510000, 1112908, 1182006,
1302173, 1312072, 1322107, 1332148, 1332149, 1342040, 1362022, 1402111, 1412071, 1422073, 1432099, 1442136,
1452129, 1472141, 1482102, 1492101, 1522020];

function start() {
    if (im.getInventory(1).getNumFreeSlot() < 1 || im.getInventory(2).getNumFreeSlot() < 2
        || im.getInventory(5).getNumFreeSlot() < 1) {
        im.sendOk("Please make more inventory space.");
        im.dispose();
        return;
    }
    im.gainItem(2500000, 1);
    im.gainItem(2022740 + im.nextInt(6), 1);
    if (im.nextInt(100) < 10) {
        im.gainItem(extra[im.nextInt(extra.length)], 1);
    }
    im.removeItem(2430456);
    im.dispose();
}