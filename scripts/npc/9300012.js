/**
 * Created by : Shawn of RaGEZONE
 **/
var name = "#b#eTesting#k#n";
var talk = "What would you like to browse?\r\n\r\n";
var menu = ["Canes"];
var options = [
/*Common*/	["Hats","Earrings","Necklaces and Face Accessories","Capes","Overalls","Gloves","Shields","Shoes","Maple Weapons","Level 0 Weapons"], 
/*Warrior*/	["Hats","Tops","Bottoms","Overalls","Gloves","Shields","Shoes","1H Axes","2H Axes","1H BWs","2H BWs","1H Swords","2H Swords","Spears","Pole Arms"], 
/*Mage*/	["Hats","Overalls","Gloves","Shields","Shoes","Wands","Staffs"], 
/*Archer*/	["Hats","Overalls","Gloves","Shoes","Bows","CrossBows","Arrows"], 
/*Thief*/	["Hats","Tops","Bottoms","Overalls","Gloves","Shields","Shoes","Daggers","Claws","Throwing Stars"], 
/*Pirate*/	["Hats","Overalls","Gloves","Shoes","Weapons","Bullets and Capsules"], 
/*NX*/		["Hats","Earrings","Face","Tops","Bottoms","Capes","Overalls","Rings","Gloves","Shields","Shoes","Weapons","Throwing Stars","Pets","Pet Misc","Emotion","Effects","Accessories","Player FM Stores"], 
/*ETC*/		["Messengers","Super Megaphones, Gachapon Tickets, Rocks, and Morphs","Boss Pieces","Buffs and Potions","Scrolls","Summoning Sacks","Chairs","Mounts"]];
var colors = ["#g","#r","#d","#b"];
var rand = Math.floor(Math.random()*4);
var rand2 = Math.ceil(Math.floor(Math.random()*4));
var c;
npc = 0;
function start(){
	var text = "Hello #e#d#h ##k#n. I am the All in One seller of "+name+". "+talk+"";
	for (var z = 0; z < menu.length; z++)
		text+= "#L"+z+"##e"+colors[rand]+""+menu[z]+"#l\r\n";
	cm.sendSimple(text);
}
function action(m,t,s){
	if (m != 1){
		cm.dispose();
		return;
	}else{
		npc++;
	}
	if (npc == 1){
		c = s;
		for (var i = 0; i < options[c].length; i++)
			talk+="#L"+i+"##e"+colors[rand2]+""+options[c][i]+"#k#l\r\n";
		cm.sendSimple(talk);
	} else if (npc == 2){
		//cm.openShop(999999+((c*100)+s));
		cm.sendOk("It works now.");
		cm.dispose();
	}
}