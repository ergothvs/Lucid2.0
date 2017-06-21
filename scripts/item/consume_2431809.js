//Xenon Hot Time 4

function start() {
    if (im.getInventory(1).getNumFreeSlot() < 1 || im.getInventory(2).getNumFreeSlot() < 2
        || im.getInventory(4).getNumFreeSlot() < 1) {
        im.sendOk("Please make more inventory space.");
        im.dispose();
        return;
    }
    im.gainItem(4310066, 5);
    im.gainPotentialItem(1242053, 1, 18); //Epic
    im.gainItem(2430442, 1);
    im.gainItem(2290285, 1);
    im.removeItem(2431809);
    im.dispose();
}