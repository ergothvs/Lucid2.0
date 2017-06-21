//Script by Alcandon 

importPackage(Packages.server);

var status = 0;


function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

         
         if (mode == -1) {
        cm.dispose();
    
    }else if (mode == 0){
        cm.sendOk ("#eOkay, talk to me when you want to Super Scroll!");
        cm.dispose();

    }else{             
        if (mode == 1)
            status++;
        else
            status--;
        
        if (status == 0) {
        Rebirths = cm.getChar().getRebirths();
        cm.sendNext("#eHey sup, I'm the Belt System of #rMaple Blade#k.");
        }else if (status == 1) { 
          cm.sendSimple ("If you got the sufficient amount of Rebirths, pick one!#e#d" +
                 "#k\r\n#L0##r#v1132000# - Req. Level 120+, It gives all stats + 50, 5 W.att" +
                 "#k\r\n#L1##r#v1132001# - Req. 1+ Rebirths It gives all stats + 120, 10 W.att" +
                 "#k\r\n#L2##r#v1132002# - Req. 5+ Rebirths It gives all stats + 220, 15 W.att " +
                 "#k\r\n#L3##r#v1132003# - Req. 10+ Rebirths It gives all stats + 420 20 W.att" +
                 "#k\r\n#L4##r#v1132004# - Req. 20+ Rebirths It gives all stats + 777 30 W.att");

            } else if (selection == 0) {  
            if(cm.getPlayer().getLevel() > 119  && !cm.haveItem(1132000) && cm.canHold(1132000)) {
            cm.gainItem (1132000);            
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132000, "str", 50);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132000, "dex", 50);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132000, "int", 50);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132000, "luk", 50);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132000, "watk", 5);
            cm.clearDrops();
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must be #rLevel 120+#k to get this belt or #ryou already have the item#k or you don't have #renough space#k in your inventory!");
             cm.dispose();
             }
            } else if (selection == 1) {  
            if(cm.getPlayer().getRebirths() > 0 && !cm.haveItem(1132001) && cm.canHold(1132001)) {
            cm.gainItem (1132001);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132001, "str", 120);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132001, "dex", 120);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132001, "int", 120);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132001, "luk", 120);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132001, "watk", 10);
            cm.clearDrops();
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have #r1 Rebirths+#k to get this belt or #ryou already have the item#k or you don't have #renough space#k in your inventory!");
             cm.dispose();
             }
            } else if (selection == 2) {  
            if(cm.getPlayer().getRebirths() > 4 && !cm.haveItem(1132002) && cm.canHold(1132002)) {
            cm.gainItem (1132002);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132002, "str", 220);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132002, "dex", 220);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132002, "int", 220);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132002, "luk", 220);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132002, "watk", 15);
            cm.clearDrops();
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have #r5 Rebirths+#k to get this belt or #ryou already have the item#k or you don't have #renough space#k in your inventory!");
             cm.dispose();
             }
            } else if (selection == 3) {  
            if(cm.getPlayer().getRebirths() > 9 && !cm.haveItem(1132003) && cm.canHold(1132003)) {
            cm.gainItem (1132003);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132003, "str", 420);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132003, "dex", 420);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132003, "int", 420);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132003, "luk", 420);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132003, "watk", 20);
            cm.clearDrops();
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have #r10 Rebirths+#k to get this belt or #ryou already have the item#k or you don't have #renough space#k in your inventory!");
             cm.dispose();
             }
            } else if (selection == 4) {  
            if(cm.getPlayer().getRebirths() > 19 && !cm.haveItem(1132004) && cm.canHold(1132004)) {
            cm.gainItem (1132004);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132004, "str", 777);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132004, "dex", 777);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132004, "int", 777);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132004, "luk", 777);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1132004, "watk", 30);
            cm.clearDrops();
            cm.reloadChar();
            cm.gainItem(1132003, -1);
            cm.dispose();
               } else {
             cm.sendOk ("You must have #r20 Rebirths+#k to get this belt or #ryou already have the item#k or you don't have #renough space#k in your inventory!");
             cm.dispose();
             }
            } else if (selection == 5) {  
            if(cm.getPlayer().getRebirths() > 4 && !cm.haveItem(1302036) && cm.canHold(1302036)) {
            cm.gainRebirths (-50);
            cm.gainItem (1302036);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302036, "str", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302036, "dex", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302036, "int", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302036, "luk", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302036, "watk", 1000);
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have 50 Rebirths to use this item. Unless you already have the item or you don't have enough space in your inventory!");
             cm.dispose();
             }
            } else if (selection == 6) {  
            if(cm.getPlayer().getRebirths() > 4 && !cm.haveItem(1302057) && cm.canHold(1302057)) {
            cm.gainRebirths (-50);
            cm.gainItem (1302057);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302057, "str", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302057, "dex", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302057, "int", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302057, "luk", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302057, "watk", 1000);
            cm.clearDrops();
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have 50 Rebirths to use this item. Unless you already have the item or you don't have enough space in your inventory!");
             cm.dispose();
             }
            } else if (selection == 7) {  
            if(cm.getPlayer().getRebirths() > 4 && !cm.haveItem(1302065) && cm.canHold(1302065)) {
            cm.gainRebirths (-50);
            cm.gainItem (1302065);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302065, "str", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302065, "dex", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302065, "int", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302065, "luk", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302065, "watk", 1000);
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have 50 Rebirths to use this item. Unless you already have the item or you don't have enough space in your inventory!");
             cm.dispose();
             }
            } else if (selection == 8) {  
            if(cm.getPlayer().getRebirths() > 4 && !cm.haveItem(1302066) && cm.canHold(1302066)) {
            cm.gainRebirths (-50);
            cm.gainItem (1302066);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302066, "str", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302066, "dex", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302066, "int", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302066, "luk", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1302066, "watk", 1000);
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have 50 Rebirths to use this item. Unless you already have the item or you don't have enough space in your inventory!");
             cm.dispose();
             }
            } else if (selection == 9) {  
            if(cm.getPlayer().getRebirths() > 4 && !cm.haveItem(1442012) && cm.canHold(1442012)) {
            cm.gainRebirths (-50);
            cm.gainItem (1442012);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1442012, "str", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1442012, "dex", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1442012, "int", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1442012, "luk", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1442012, "watk", 1000);
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have 50 Rebirths to use this item. Unless you already have the item or you don't have enough space in your inventory!");
             cm.dispose();
             }
            } else if (selection == 10) {  
            if(cm.getPlayer().getRebirths() > 4 && !cm.haveItem(1402009) && cm.canHold(1402009)) {
            cm.gainRebirths (-50);
            cm.gainItem (1402009);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1402009, "str", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1402009, "dex", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1402009, "int", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1402009, "luk", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1402009, "watk", 1000);
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have 50 Rebirths to use this item. Unless you already have the item or you don't have enough space in your inventory!");
             cm.dispose();
             }
            } else if (selection == 11) {  
            if(cm.getPlayer().getRebirths() > 4 && !cm.haveItem(1412026) && cm.canHold(1472051)) {
            cm.gainRebirths (-50);
            cm.gainItem (1412026);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1412026, "str", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1412026, "dex", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1412026, "int", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1412026, "luk", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1412026, "watk", 1000);
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have 50 Rebirths to use this item. Unless you already have the item or you don't have enough space in your inventory!");
             cm.dispose();
             }
            } else if (selection == 12) {  
            if(cm.getPlayer().getRebirths() > 4 && !cm.haveItem(1432038) && cm.canHold(1472051)) {
            cm.gainRebirths (-50);
            cm.gainItem (1432038);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1432038, "str", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1432038, "dex", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1432038, "int", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1432038, "luk", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1432038, "watk", 1000);
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have 50 Rebirths to use this item. Unless you already have the item or you don't have enough space in your inventory!");
             cm.dispose();
             }
            } else if (selection == 13) {  
            if(cm.getPlayer().getRebirths() > 4 && !cm.haveItem(1442045) && cm.canHold(1472051)) {
            cm.gainRebirths (-50);
            cm.gainItem (1442045);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1442045, "str", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1442045, "dex", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1442045, "int", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1442045, "luk", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1442045, "watk", 1000);
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have 50 Rebirths to use this item. Unless you already have the item or you don't have enough space in your inventory!");
             cm.dispose();
             }
            } else if (selection == 14) {  
            if(cm.getPlayer().getRebirths() > 4 && !cm.haveItem(1492013) && cm.canHold(1472051)) {
            cm.gainRebirths (-50);
            cm.gainItem (1492013);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1492013, "str", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1492013, "dex", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1492013, "int", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1492013, "luk", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1492013, "watk", 1000);
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have 50 Rebirths to use this item. Unless you already have the item or you don't have enough space in your inventory!");
             cm.dispose();
             }
            } else if (selection == 15) {  
            if(cm.getPlayer().getRebirths() > 4 && !cm.haveItem(1482013) && cm.canHold(1472051)) {
            cm.gainRebirths (-50);
            cm.gainItem (1482013);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1482013, "str", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1482013, "dex", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1482013, "int", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1482013, "luk", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1482013, "watk", 1000);
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have 50 Rebirths to use this item. Unless you already have the item or you don't have enough space in your inventory!");
             cm.dispose();
             }
            } else if (selection == 16) {  
            if(cm.getPlayer().getRebirths() > 4 && !cm.haveItem(1482023) && cm.canHold(1472051)) {
            cm.gainRebirths (-50);
            cm.gainItem (1482023);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1482023, "str", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1482023, "dex", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1482023, "int", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1482023, "luk", 2000);
            Packages.server.Mapleinventory!Manipulator.editEquipById(cm.getPlayer(), 1, 1482023, "watk", 1000);
            cm.reloadChar();
            cm.dispose();
               } else {
             cm.sendOk ("You must have 50 Rebirths to use this item. Unless you already have the item or you don't have enough space in your inventory!");
             cm.dispose();
             }
}
}
}