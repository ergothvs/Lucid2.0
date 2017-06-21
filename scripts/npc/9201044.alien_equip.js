importPackage(net.sf.odinms.client);

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) { 
            cm.sendSimple("#dAlien Equips#k, they come from outerspace. Duh, where else? Want to buy some?\r\n#b#L0#Why not?#l#k");
        } else if (status == 1) {
            if (selection == 0) {
                cm.sendNext("Great! Remember that they all cost #b1 billion mesos#k each.");
            }
        } else {
            var items = new Array (1003115, 1003116, 1003117, 1003118, 1003119, 1052277, 1052278, 1052279, 1052280, 1052281, 1072449, 1072450, 1072451, 1072452, 1072453, 1082277, 1082278, 1082279, 1082280, 1082281, 1032080, 1032081, 1032082, 1032083, 1032084, 1092070, 1092071, 1092072, 1092073, 1092074, 1092075, 1092076, 1092077, 1092078, 1092079, 1092080, 1092081, 1092082, 1092083, 1092084, 1302143, 1302144, 1302145, 1302146, 1302147, 1312058, 1312059, 1312060, 1312061, 1312062, 1322086, 1322087, 1322088, 1322089, 1322090, 1332116, 1332117, 1332118, 1332119, 1332120, 1372074, 1372075, 1372076, 1372077, 1372078, 1382095, 1382096, 1382097, 1382098, 1382099, 1402086, 1402087, 1402088, 1402089, 1402090, 1412058, 1412059, 1412060, 1412061, 1412062, 1422059, 1422060, 1422061, 1422062, 1422063, 1432077, 1432078, 1432079, 1432080, 1432081, 1442107, 1442108, 1442109, 1442110, 1442111, 1452102, 1452103, 1452104, 1452105, 1452106, 1462087, 1462088, 1462089, 1462090, 1462091, 1472113, 1472114, 1472115, 1472116, 1472117, 1482075, 1482076, 1482077, 1482078, 1482079, 1492075, 1492076, 1492077, 1492078, 1492079); 
            if (status == 2) {
                var selStr = "Which #dAlien Equip#k to you want to buy?\r\nScroll on the #bitem#k to learn its #estats#n.";
                for (var i = 0; i < items.length; i++){
                    selStr += "\r\n#b#L" + i + "# #z" + items[i] + "##l#k";
                }
                cm.sendSimple(selStr);
            }
            if (status == 3) {
                if (cm.getMeso() < 1000000000) {
                    cm.sendOk("You do not have enough mesos.");
                    cm.dispose();
                } else {
                    cm.gainMeso(-1000000000);
                    cm.gainItem(items[selection], 1);
                    cm.dispose();
		}
            }
        }
    }
}