importPackage(Packages.client);

var status = 0;
var selected = 1;
var wui = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
    selected = selection;
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 0 && mode == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
 			cm.sendAcceptDecline("Hey there! And welcome to #rMaple Blade#k Max-Stat-Item NPC!#k\r\n#r Please Meet these Requirements: \r\n\r\n#b32,767 Stats in all#k\r\n#b1 #i4001085#,1 #i4001084#,1 #i4001083#,1 #i4032013#,5 #i4000138#\r\n#b200 #i4004004#, #b200 #i4004002#, #b200 #i4004000#, #b200 #i4004003#, #b200 #i4004001#,\r\n#b2 #i2049100#, #b1 #i2340000# ");
		} else if (status == 1) {
				if (cm.getPlayer().getStr() > 32766 && cm.getPlayer().getDex() > 32766 && cm.getPlayer().getInt() > 32766 && cm.getPlayer().getLuk() > 32766 && cm.haveItem(4001085, 1) && cm.haveItem(4001084, 1) && cm.haveItem(4001083, 1) && cm.haveItem(4032013, 2) && cm.haveItem(4000138, 5) && cm.haveItem(4004004, 200) && cm.haveItem(4004002, 200) && cm.haveItem(4004000, 200) && cm.haveItem(4004003, 200) && cm.haveItem(4004001, 200) && cm.haveItem(2049100, 2) && cm.haveItem(2340000, 1)){
				            var String = "Please choose your desired item or NX item you want as your new Max-Stat-Item. Please check your inventory to make sure you have enough room, because we don't do refunds. Enjoy!\r\n\r\n";
                            cm.sendSimple(String+cm.EquipList(cm.getClient()));
				} else  {
					cm.sendOk ("Sorry but you don't meet the requirements to do this.");
					cm.dispose(); 
				}
		} else if (status == 2) { 
		     cm.MakeGMItem(selected, cm.getP());
			  cm.getPlayer().setStr(4); cm.getPlayer().setDex(4); cm.getPlayer().setLuk(4); cm.getPlayer().setInt(4);
              cm.gainItem(4001085, -1);
              cm.gainItem(4001084, -1); 
              cm.gainItem(4001083, -1);
              cm.gainItem(4032013, -2);
              cm.gainItem(4000138, -5);
              cm.gainItem(4004000, -200);
              cm.gainItem(4004001, -200);
              cm.gainItem(4004002, -200);
              cm.gainItem(4004003, -200);
              cm.gainItem(4004004, -200);
              cm.gainItem(4004004, -200);
              cm.gainItem(2049100, -2);
              cm.gainItem(2340000, -1);
              cm.reloadChar();
			  cm.dispose();	
         }			
        if (selection == 1) {
				cm.sendOk("See you next time!");
				cm.dispose();
			}
		}
	}
