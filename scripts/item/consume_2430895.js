//Angelic Buster Hot Time 1
var extra = 
[1132174, 1132175, 1132176, 1132177, 1132178, 1102481, 1102482, 1102483, 1102484,
1102485, 1072743, 1072744, 1072745, 1072746, 1072747];

function start() {
    if (im.getInventory(1).getNumFreeSlot() < 2 || im.getInventory(2).getNumFreeSlot() < 3
        || im.getInventory(3).getNumFreeSlot() < 1) {
        im.sendOk("Please make more inventory space.");
        im.dispose();
        return;
    }
    im.gainItem(2430441, 1);
    im.gainItem(2501000, 1);
    im.gainItem(2430768 + im.nextInt(5), 1);
    im.gainPotentialItem(1003635, 1, 19); //Unique
    if (im.nextInt(100) < 10) {
        im.gainItem(extra[im.nextInt(extra.length)], 1);
    }
    if (im.nextInt(100) < 10) {
        im.gainItem(3700049, 1);
    }
    im.removeItem(2430895);
    im.dispose();
}