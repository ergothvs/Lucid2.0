importPackage(Packages.constants);
/**
 * Dimensional warper
 * Warps you to towns
 * By Novak
 */

function start() {
    cm.sendSlideMenu(0, cm.getSlideMenuSelection(5));
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
	/* order:
	TOWN_0(0, "Six Path Crossway", 0, 0),
            TOWN_1(1, "Henesys", 0, 0),
            TOWN_2(2, "Ellinia", 0, 0),
            TOWN_3(3, "Perion", 0, 0),
            TOWN_4(4, "Kerning City", 0, 0),
            TOWN_5(5, "Lith Harbor", 0, 0),
            TOWN_6(6, "Sleepywood", 0, 0),
            TOWN_7(7, "Nautilus", 0, 0),
            TOWN_8(8, "Ereve", 0, 0),
            TOWN_9(9, "Rien", 0, 0),
            TOWN_10(10, "Orbis", 0, 0),
            TOWN_11(11, "El Nath", 0, 0),
            TOWN_12(12, "Ludibrium", 0, 0),
            TOWN_13(13, "Omega Sector", 0, 0),
            TOWN_14(14, "Korean Folk Town", 0, 0),
            TOWN_15(15, "Aquarium", 0, 0),
            TOWN_16(16, "Leafre", 0, 0),
            TOWN_17(17, "Mu Lung", 0, 0),
            TOWN_18(18, "Herb Town", 0, 0),
            TOWN_19(19, "Ariant", 0, 0),
            TOWN_20(20, "Magatia", 0, 0),
            TOWN_21(21, "Edelstein", 0, 0),
            TOWN_22(22, "Elluel", 0, 0);
	*/var mapid = 230050000;
    var portal = 0;
    switch (selection) {
        case 0: // Six path Crossway
            mapid = 104020000, portal = 1;
            break;
        case 1: // Henesys
            mapid = 100000000, portal = 1;
            break;
        case 2: // Ellenia
            mapid = 101000000, portal = 1;
            break;
        case 3: // Perion
            mapid = 102000000, portal = 1;
            break;
        case 4: // Kerning City
            mapid = 103000000, portal = 1;
            break;
        case 5: // lith harbor
            mapid = 104000000, portal = 1;
            break;
        case 6: // sleepywood
            mapid = 105000000, portal = 1;
            break;
        case 7: // Nautulis
            mapid = 120000000, portal = 1;
            break;
        case 8: // Ereve
            mapid = 915000400, portal = 1;
            break;
        case 9: // Rien
            mapid = 914040000, portal = 1;
            break;
        case 10: //Orbis
            mapid = 200000000, portal = 1;
            break;
        case 11: //El nath
            mapid = 211000000, portal = 1;
            break;
        case 12: //  Ludibrium
            mapid = 200090100, portal = 1;
            break;
        case 13: // omega sector
            mapid = 221000000, portal = 1;
            break;
        case 14: // Korean folk town
            mapid = 240000000, portal = 1;
            break;
		case 15: // aquarium (real)
            mapid = 230000000, portal = 1;
            break;
		case 16: // Leafre
            mapid = 240000000, portal = 1;
            break;
		case 17: // Mu Lung
            mapid = 250000000, portal = 1;
            break;
		case 18: // Herb Town
            mapid = 251000000, portal = 1;
            break;
		case 19: // ariant
            mapid = 260000000, portal = 1;
            break;
		case 20: // Magatia
            mapid = 326090010, portal = 1;
            break;
		case 21: // Edelstein
            mapid = 310000000, portal = 1;
            break;
		case 22: // Elluel
            mapid = 101050000, portal = 1;
            break;
        default:
            cm.dispose();
            return;
    }
    cm.saveReturnLocation("MULUNG_TC");
    cm.warp(mapid, portal);
    //cm.warp(cm.getSlideMenuDataInteger(0), 0);
    cm.dispose();
}