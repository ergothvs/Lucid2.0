/*Coded By Tim (Vote)*/

var status = 0;
var Error = "#rHmmm ... You don't have enough #eVotePoints!#n";

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
    cm.sendSimple ("Hello #r#h ##k, Welcome to the #rLucid VotePoint Exchanger. \r\n You have #r[" + cm.getVPoints() +"]#k Votepoints#k. \r\n #e#rTHERE ARE NO REFUNDS." +
                "\r\n#L1##e#bNexon Cash#l" + 
                "\r\n#L2##rMiracle Cubes#l");
        } else if (selection == 1) {
                cm.sendSimple ("You Currently have#r [" + cm.getVPoints() + "]#k Vote Points."+
                "#k\r\nChoose Carefully!" +
                "#k\r\n#L3##i5200009# #e#b5k #rNX #gCash #d~ #r(2) #bVotePoint(s) #i5200009#" +
                "#k\r\n#L4##i5200009# #e#b10k #rNX #gCash #d~ #r(3) #bVotePoint(s) #i5200009#" +
                "#k\r\n#L5##i5200009# #e#b15k #rNX #gCash #d~ #r(4) #bVotePoint(s) #i5200009#" +
                "#k\r\n#L6##i5200009# #e#b20k #rNX #gCash #d~ #r(5) #bVotePoint(s) #i5200009#" +
                "#k\r\n#L7##i5200009# #e#b25k #rNX #gCash #d~ #r(6) #bVotePoint(s) #i5200009#");
        } else if (selection == 2) {
                cm.sendSimple ("You Currently have#r [" + cm.getVPoints() + "]#k Vote Points."+
                "#k\r\nChoose Carefully!" +
                "#k\r\n#L8##b#i5062002# #e#bMiracle #rCube #g10 #d~ #b(2) #rVotePoint(s)" +
		"#k\r\n#L9##b#i5062000# #eMiracle #rCube #gx15 #d~ #b(3) #rVotePoint(s)" +
		"#k\r\n#L10##b#i5062009# #e#bMiracle #rCube #gx5 #d~ #b(4) #rVotePoint(s)");
	
    } else if (selection == 3) {

                var price = 5000000;
                if (cm.getVPoints() > 1) {      
                   cm.setVPoints(-2);                   
                   cm.gainNX(10000);
                   cm.dispose();
                } else {
                   cm.sendOk(Error);
                   cm.dispose();
                   }

    } else if (selection == 4) {

                var price = 10000000;
                if (cm.getVPoints() > 2) {      
                    cm.setVPoints(-3);                  
                    cm.gainNX(30000);
                    cm.dispose();
                 } else {
                   cm.sendOk(Error);
                   cm.dispose();
                   }

    } else if (selection == 5) {

                var price = 15000000;
                if (cm.getVPoints() > 3) {      
                   cm.setVPoints(-4);                    
                   cm.gainNX(40000);
                   cm.dispose();
                } else {
                   cm.sendOk(Error);
                   cm.dispose();
                   }

    } else if (selection == 6) {

                var price = 20000000;
                if (cm.getVPoints() > 4) {      
                   cm.setVPoints(-5);                    
                   cm.gainNX(50000);
                   cm.dispose();
                } else {
                   cm.sendOk(Error);
                   cm.dispose();
                   }
                   
    } else if (selection == 7) {

                if (cm.getVPoints() > 5) {      
                   cm.setVPoints(-6);                    
                   cm.gainNX(100000);
                   cm.dispose();
                } else {
                   cm.sendOk(Error);
                   cm.dispose();
                   }
            
    } else if (selection == 8) {
                
                if (cm.getVPoints() > 1) {      
                    cm.setVPoints(-2);                   
                    cm.gainItem(5062000, 15);
                    cm.dispose();
                 } else {
                    cm.sendOk(Error);
                    cm.dispose();
                    }
                   
    } else if (selection == 9) {
                
                if (cm.getVPoints() > 2) {      
                    cm.setVPoints(-3);                   
                    cm.gainItem(5062002, 10);
                    cm.dispose();
                 } else {
                    cm.sendOk(Error);
                    cm.dispose();
                   }

    } else if (selection == 10) {
                
                if (cm.getVPoints() > 3) {      
                    cm.setVPoints(-4);                   
                    cm.gainItem(5062009, 5);
                    cm.dispose();
                 } else {
                    cm.sendOk(Error);
                    cm.dispose();
                   }
                }
                }
		}