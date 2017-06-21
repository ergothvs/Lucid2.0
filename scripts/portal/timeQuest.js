/*
All Time Temple portal
*/

var tomap;

function enter(pi) {
    switch (pi.getMapId()) {
        // Green area
        case 270010100:
            tomap = 270010110;
            break;
        case 270010200:
            tomap = 270010210;
            break;
        case 270010300:
            tomap = 270010310;
            break;
        case 270010400:
            tomap = 270010410;
            break;
        case 270010500:
            tomap = 270020000;
            break;
        // Blue area
        case 270020100:
            tomap = 270020110;
            break;
        case 270020200:
            tomap = 270020210;
            break;
        case 270020300:
            tomap = 270020310;
            break;
        case 270020400:
            tomap = 270020410;
            break;
        case 270020500:
            tomap = 270030000;
            break;
        // Red zone
        case 270030100:
            tomap = 270030110;
            break;
        case 270030200:
            tomap = 270030210;
            break;
        case 270030300:
            tomap = 270030310;
            break;
        case 270030400:
            tomap = 270030410;
            break;
        case 270030500:
            tomap = 270040000;
            break;
        case 270040000:
            //if (pi.haveItem(4032002)) { // hahaha fuck quests.
				pi.playPortalSE();
                pi.warp(270040100, "out00");
                pi.playerMessage("Now moving to a deep part of the temple.");
                return true;
            break;
        default:
            return false;
    }
	pi.playPortalSE();
    pi.warp(tomap, "out00");
    return true;
}