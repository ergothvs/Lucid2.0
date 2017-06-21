function start() {
    if (im.getInventory(2).getNumFreeSlot() < 1) {
        im.sendOk("Please make some inventory space.");
        im.dispose();
        return;
    }
    im.sendSimple("What scroll would you like?\r\n#L0##i2046070:##t2046070##l\r\n#L1##i2046071:##t2046071##l\r\n#L2##i2046146:##t2046146##l\r\n#L3#End Chat#l")
}

function action(mode, type, selection) {
    if (mode != 1 || selection == 3) {
        im.dispose();
        return;
    }
    if (selection == 0) {
        im.gainItem(2046070, 1);
    } else if (selection == 1) {
        im.gainItem(2046071, 1);
    } else if (selection == 2) {
        im.gainItem(2046146, 1);
    }
    im.removeItem(2430669);
    im.dispose();
}