var status3  = 0;
var mapform = 0;
var status = 0;
var maps = Array(300000000, 680000000, 230000000, 910001000, 260000000, 541000000, 540000000, 211060010, 450002000, 863100000, 105300000, 310000000, 211000000, 101072000, 101000000, 101050000, 130000000, 820000000, 223000000, 410000000, 141000000, 120040000, 209000000, 682000000, 310070000, 401000000, 100000000, 271010000, 251000000, 744000000, 551000000, 103000000, 222000000, 450003000, 240000000, 104000000, 220000000, 150000000, 261000000, 807000000, 250000000, 800000000, 600000000, 450001000, 120000000, 200000000, 800040000, 400000000, 102000000, 914040000, 200100000, 865000000, 801000000, 105000000, 866000000, 693000020, 270000000, 860000000, 273000000, 320000000);
var bossmaps = Array(211070000, 262000000, 105100100, 240050000, 240040700, 105100100, 350060300, 271040000, 211041700, 240050400);
var cost = Array(1000, 1000, 1000, 1000, 1000, 1000);
function start() {
status = -1;
action(1, 0, 0);
}

function action(mode, type, selection) 
{

if (mode == -1) 
{
cm.dispose();
}
else {
if (status == 0 && mode == 0) 
{
cm.dispose();
return;
}
if (mode == 1) 
{
status++;
}
else 
{
status--;
}
if (status == 0)
{
	cm.sendSimple(" Hello #h # \r\n What would you like to do? \r\n  #L0# #dI want to go somewhere #l \r\n  #L1# #dI want to buy something #l \r\n  #L2# #dTrade Gold Maple Leaf #l \r\n  #L3# #dVote in-game #l\r\n  #L4# #dView achievements #l");
	}
else if(status == 1)
{
	if(selection == 0)
	{
	cm.sendSimple("\r\n  #L5# #dTown maps #l \r\n  #L6# #dMonster maps #l \r\n  #L7# #dBoss maps #l"); // add all maps in a var
	cm.dispose	
	}
	else if(selection == 1)
	{
		cm.openShop(); // puddin npcid 1511000 shops broken?
		cm.dispose // supposed to be here?
	}
	else if (selection == 2)
	{
			cm.sendOk("Would you like to convert \r\n #L8# #dGold Maple Leaf to mesos #l \r\n #L9# #dMesos to Gold Maple Leaf #l \r\n #L10# #dMaple Leaf Gold to Nx #l \r\n #L11# #dNx to Maple Leaf Gold ");
		}	
	}
else if(status == 2)
{
	switch (selection)
	{
	case 5:
		var job = cm.getJob();
		
		    var selStr = "Choose your destination.#b";
		    for (var i = 0; i < maps.length; i++) 
		    {
			if (maps[i] != cm.getMapId()) 
			{
			selStr += "\r\n#L" + i + "##m" + maps[i] + "# #l";
			}
		    }
		
		cm.sendNext(selStr);
		status3 = 1;
	break;	
	case 6:
		cm.sendSimple("to be finished")
		status3 = 2;
		break;
	case 7:
		    var selStr = "Choose your destination.#b";
		    for (var i = 0; i < bossmaps.length; i++) 
		    {
			if (bossmaps[i] != cm.getMapId()) 
			{
			selStr += "\r\n#L" + i + "##m" + bossmaps[i] + "# #l";
			}
		    }
		    cm.sendNext(selStr);
		    status3 = 3;
	break;
	case 8:
		if(cm.haveItem(4001619))
		{
			cm.sendYesNo("Are you sure you want to trade 1 Gold Maple Leaf for \r\n1.000.000.000 mesos?" );
	}	
		else
	{
		cm.sendOk("You don't have the needed items.");
	}
		status3 = 4;
		break;
	case 9:
		if(cm.getMeso() >= 1100000000)
		{
			cm.sendYesNo("Are you sure you want to trade 1.100.000.000 mesos for 1 Gold Maple Leaf?" );
		}
		else
		{
			cm.sendOk("You don't have the needed amount of mesos.");
		}
		status3 = 5;
		break;
	case 10:
		 if(cm.haveItem(4430000))
		 {
			cm.sendYesNo("Are you sure you want to trade 1 Maple Leaf Gold for 1.000.000 Nx?" );
		 }
		 else
		 {
			cm.sendOk("You don't have the needed items.");
		 }
		 status3 = 6;
		 break;
	case 11:
		 if(cm.getMeso() >= 1200000)
		 {
			cm.sendYesNo("Are you sure you want to trade 1.200.000 Nx for 1 Maple Leaf Gold?" );
		 }
		 else
		 {
			cm.sendOk("You don't have the needed amount of NxCash.");
		 }
		 status3 = 7;
		 break;
	}
}
else if(status == 3){
	switch (status3){
	case 1:
		cm.sendYesNo("You don't have anything else to do here, huh? Do you really want to go to #b#m" + maps[selection]);
		selectedMap = selection;
		mapform = 1;
		break;
	case 2:
		//placeholder for mobmaps
		mapform = 2;
		break;
	case 3:
		cm.sendYesNo("You don't have anything else to do here, huh? Do you really want to go to #b#m" + bossmaps[selection]);
		selectedMap = selection;
		mapform = 3;
		break;
	case 4:
		cm.gainItem(4001619, -1);
		cm.gainMeso(1000000000);
		cm.sendOk("Thank you for this Golden Maple Leaf!");
		cm.dispose;
		break;
	case 5:
		cm.gainItem(4001619, 1);
		cm.gainMeso(-1100000000);
		cm.sendOk("Have fun with your Golden Maple Leaf!");
		cm.dispose;
		break;
	case 6:
		cm.gainItem(4430000, -1);
		cm.gainNX(1000000);
		cm.sendOk("Thank you for this Maple Leaf Gold!");
		cm.dispose;
		break;
	case 7:
		cm.gainItem(4430000, 1);
		cm.gainNX(-1200000);
		cm.sendOk("Have fun with your Maple Leaf Gold!");
		cm.dispose;
		break;
	}
}
else if (status == 4) {
	switch (mapform){
	case 1:
		cm.warp(maps[selectedMap]);
		cm.dispose;
		break;
	case 2:
		cm.warp(mobmaps[selectedMap]);
		cm.dispose;
		break;
	case 3:
		cm.warp(bossmaps[selectedMap]);
		cm.dispose;
		break;
	}
}
}
}
