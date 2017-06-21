//Xenon Hot Time 6

function start() {
    if (im.getInventory(1).getNumFreeSlot() < 1 || im.getInventory(2).getNumFreeSlot() < 5
        || im.getInventory(3).getNumFreeSlot() < 1 || im.getInventory(4).getNumFreeSlot() < 1) {
        im.sendOk("Please make more inventory space.");
        im.dispose();
        return;
    }
    im.gainItem(4310066, 10);
    im.gainPotentialItem(1072777, 1, 18); //Epic
    im.gainItem(3080004, 1);
    im.gainItem(2028174, 1);
    im.gainItem(2430442, 1);
    im.gainItem(2431405, 1);
    im.gainItem(2290285, 1);
    var scrollRandom = im.nextInt(3);
    im.gainItem(scrollRandom == 0 ? 2046964 : scrollRandom == 1 ? 2046965 : 2047801, 1);
    im.removeItem(2431811);
    im.dispose();
}