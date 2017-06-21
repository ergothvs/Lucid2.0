//Kaiser Hot Time 2
function start() {
    if (im.getInventory(1).getNumFreeSlot() < 1 || im.getInventory(2).getNumFreeSlot() < 3) {
        im.sendOk("Please make more inventory space.");
        im.dispose();
        return;
    }
    im.gainItem(2290723, 1);
    im.gainItem(2500000, 1);
    im.gainPotentialItem(1402015, 1, 19); //Unique
    im.gainItem(2430909, 1);
    if (im.nextInt(100) < 10) {
        im.gainItem(1052526, 1);
    }
    im.removeItem(2430892);
    im.dispose();
}