//Mercedes Hot Time
var extra = 
[5064000, 5520000, 5150053, 5152057, 5062000, 5230000, 5072000, 5030004, 5130003, 5510000, 1112908, 1182006,
1122149, 1112662, 1132104, 1032110, 1012283, 1112663, 1012284, 1012285, 1112597, 1112593];

function start() {
    if (im.getInventory(1).getNumFreeSlot() < 2 || im.getInventory(2).getNumFreeSlot() < 6
        || im.getInventory(4).getNumFreeSlot() < 1 || im.getInventory(5).getNumFreeSlot() < 1) {
        im.sendOk("Please make more inventory space.");
        im.dispose();
        return;
    }
    im.gainItem(2501000, 1);
    im.gainItem(2430473, 1);
    im.gainItem(2430441, 1);
    im.gainPotentialItem(1003359, 1, 18); //Epic
    im.gainItem(4310027, 20);
    im.gainItem(2500000, 1);
    im.gainItem(2022740 + im.nextInt(6), 1);
    if (im.nextInt(100) < 10) {
        im.gainItem(extra[im.nextInt(extra.length)], 1);
    }
    im.removeItem(2430471);
    im.dispose();
}