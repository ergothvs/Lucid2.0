//Xenon Hot Time 3

function start() {
    if (im.getInventory(1).getNumFreeSlot() < 1 || im.getInventory(2).getNumFreeSlot() < 2
        || im.getInventory(4).getNumFreeSlot() < 1) {
        im.sendOk("Please make more inventory space.");
        im.dispose();
        return;
    }
    im.gainItem(4310066, 5);
    im.gainItem(1672028, 1);
    im.gainItem(2431405, 1);
    var scrollRandom = im.nextInt(3);
    im.gainItem(scrollRandom == 0 ? 2046964 : scrollRandom == 1 ? 2046965 : 2047801, 1);
    im.removeItem(2431808);
    im.dispose();
}