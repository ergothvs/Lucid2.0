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
        if (status == 0) { // first interaction with NPC
            cm.sendNext("Special Job Items. Would you like to know more?");
        } else if (status == 1) {
            cm.sendSimple("#dSpecial Job Items#k, do I really have to explain that? Want to buy some?\r\n#b#L0#Why not?#l#k");
        } else if (status == 2) {
            if (selection == 0) {
                cm.sendNext("Terrific! Remember that they all cost #b1 million mesos#k each.");
            }
        } else {
            var items = new Array (5010069, 1492074, 1452099, 5010068, 1099000, 1099002, 1099003, 1099004); 
            if (status == 3) {
                var selStr = "Which #dSpecial Job Item#k to you want to buy?";
                for (var i = 0; i < items.length; i++){
                    selStr += "\r\n#b#L" + i + "# #v" + items[i] + "# #l#k";
                }
                cm.sendSimple(selStr);
            }
            if (status == 4) {
                if (cm.getMeso() < 1000000) {
                    cm.sendOk("You do not have enough mesos.");
                    cm.dispose();
                } else {
                    cm.gainMeso(-1000000);
                    cm.gainItem(items[selection], 1);
                    cm.dispose();
		}
            }
        }
    }
}