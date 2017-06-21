/* Grand Athenaeum
    Thales the Librarian
    Made by Daenerys
*/

var status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
	} else {
		status++;
    if (status == 0) {
	    cm.sendSimple("#bWhere would you like to go?#k#e\r\n#L0#Amherst#l\r\n#L1#Amoria#l\r\n#L2#Arboren#l\r\n#L3#Ardentmill#l\r\n#L4#Ariant#l\r\n#L5#Aquarium#l\r\n#L6#Azwan#l\r\n#L7#Boat Quay Town#l\r\n#L8#Commerci Republic#l\r\n#L9#Crimsonheart Castle#l\r\n#L10#Crimsonwood Keep#l\r\n#L11#Chryse#l\r\n#L12#Edelstein#l\r\n#L13#Ellinel Fairy Academy#l\r\n#L14#Ellin Forest#l\r\n#L15#Ellinia#l\r\n#L16#El Nath#l\r\n#L17#Elluel#l\r\n#L18#Ereve#l\r\n#L19#Fantasy Theme World#l\r\n#L20#Florina Beach#l\r\n#L21#Free Market#l\r\n#L22#Gold Beach#l\r\n#L23#Golden Temple#l\r\n#L24#Grand Athenaeum#l\r\n#L25#Haunted House#l\r\n#L26#Herb Town#l\r\n#L27#Henesys#l\r\n#L28#Henesys Ruins#l\r\n#L29#Kerning City#l\r\n#L30#Kerning Square#l\r\n#L31#Korean Folk Town#l\r\n#L32#Leafre#l\r\n#L33#Lion King's Castle#l\r\n#L34#Lith Harbor#l\r\n#L35#Ludibrium#l\r\n#L36#Lumiere#l\r\n#L37#Magatia#l\r\n#L38#Maple Tree Hill#l\r\n#L39#Mushroom Castle#l\r\n#L40#Mu Lung#l\r\n#L41#Nautilus#l\r\n#L42#Neo City#l\r\n#L43#New Leaf City#l\r\n#L44#Omega Sector#l\r\n#L45#Orbis#l\r\n#L46#Pantheon#l\r\n#L47#Perion#l\r\n#L48#Rien#l\r\n#L49#Riena Strait#l\r\n#L50#Sleepywood#l\r\n#L51#Southperry#l\r\n#L52#Temple of Time#l\r\n#L53#Tynerum#l\r\n#L54#Twilight Perion#l\r\n#L55#Twisted Aquarium#l\r\n#L56#Zipangu#l");
	} else if (status == 1) {
	if (selection == 0) {		
	    cm.warp(1000000,0);
		cm.dispose();
	} else if (selection == 1) {	
		cm.warp(680000000,0);
		cm.dispose();
	} else if (selection == 2) {	
		cm.warp(866000220,0);
		cm.dispose();
	} else if (selection == 3) {	
		cm.warp(910001000,0);
		cm.dispose();
	} else if (selection == 4) {	
		cm.warp(260000000,0);
		cm.dispose();
	} else if (selection == 5) {	
		cm.warp(230000000,0);
		cm.dispose();
	} else if (selection == 6) {	
		cm.warp(262000000,0);
		cm.dispose();
	} else if (selection == 7) {	
		cm.warp(541000000,0);
		cm.dispose();
	} else if (selection == 8) {	
		cm.warp(865000000,0);
		cm.dispose();
	} else if (selection == 9) {	
		cm.warp(301060000,0);
		cm.dispose();
	} else if (selection == 10) {	
		cm.warp(610020006,0);
		cm.dispose();
	} else if (selection == 11) {	
		cm.warp(200100000,0);
		cm.dispose();
	} else if (selection == 12) {	
		cm.warp(310000000,0);
		cm.dispose();
	} else if (selection == 13) {	
		cm.warp(101071300,0);
		cm.dispose();
	} else if (selection == 14) {	
		cm.warp(300000000,0);
		cm.dispose();
	} else if (selection == 15) {	
		cm.warp(101000000,0);
		cm.dispose();
	} else if (selection == 16) {	
		cm.warp(211000000,0);
		cm.dispose();
	} else if (selection == 17) {	
		cm.warp(101050000,0);
		cm.dispose();
	} else if (selection == 18) {	
		cm.warp(130000000,0);
		cm.dispose();
	} else if (selection == 19) {	
		cm.warp(223000000,0);
		cm.dispose();
	} else if (selection == 20) {	
		cm.warp(120030000,0);
		cm.dispose();
	} else if (selection == 21) {	
		cm.warp(910000000,0);
		cm.dispose();
	} else if (selection == 22) {	
		cm.warp(120040000,0);
		cm.dispose();
	} else if (selection == 23) {	
		cm.warp(809060000,0);
		cm.dispose();
	} else if (selection == 24) {	
		cm.warp(302000000,0);
		cm.dispose();
	} else if (selection == 25) {	
		cm.warp(682000000,0);
		cm.dispose();
	} else if (selection == 26) {	
		cm.warp(251000000,0);
		cm.dispose();
	} else if (selection == 27) {	
		cm.warp(100000000,0);
		cm.dispose();
	} else if (selection == 28) {	
		cm.warp(271010000,0);
		cm.dispose();
	} else if (selection == 29) {	
		cm.warp(103000000,0);
		cm.dispose();
	} else if (selection == 30) {	
		cm.warp(103040000,0);
		cm.dispose();
	} else if (selection == 31) {	
		cm.warp(222000000,0);
		cm.dispose();
	} else if (selection == 32) {	
		cm.warp(240000000,0);
		cm.dispose();
	} else if (selection == 33) {	
		cm.warp(211060010,0);
		cm.dispose();
	} else if (selection == 34) {	
		cm.warp(104000000,0);
		cm.dispose();
	} else if (selection == 35) {	
		cm.warp(220000000,0);
		cm.dispose();
	} else if (selection == 36) {	
		cm.warp(150000000,0);
		cm.dispose();
	} else if (selection == 37) {	
		cm.warp(261000000,0);
		cm.dispose();
	} else if (selection == 38) {	
		cm.warp(10000,0);
		cm.dispose();
	} else if (selection == 39) {	
		cm.warp(106020000,0);
		cm.dispose();
	} else if (selection == 40) {	
		cm.warp(250000000,0);
		cm.dispose();
	} else if (selection == 41) {	
		cm.warp(120000000,0);
		cm.dispose();
	} else if (selection == 42) {	
		cm.warp(240070000,0);
		cm.dispose();
	} else if (selection == 43) {	
		cm.warp(600000000,0);
		cm.dispose();
	} else if (selection == 44) {	
		cm.warp(221000000,0);
		cm.dispose();
	} else if (selection == 45) {	
		cm.warp(200000000,0);
		cm.dispose();
	} else if (selection == 46) {	
		cm.warp(400000000,0);
		cm.dispose();
	} else if (selection == 47) {	
		cm.warp(102000000,0);
		cm.dispose();
    } else if (selection == 48) {	
		cm.warp(140000000,0);
		cm.dispose();
	} else if (selection == 49) {	
		cm.warp(141010000,0);
		cm.dispose();
	} else if (selection == 50) {	
		cm.warp(105000000,0);
		cm.dispose();
	} else if (selection == 51) {	
		cm.warp(60000,0);
		cm.dispose();
	} else if (selection == 52) {	
		cm.warp(270000100,0);
		cm.dispose();
	} else if (selection == 53) {	
		cm.warp(863000017,0);
		cm.dispose();
	} else if (selection == 54) {	
		cm.warp(273000000,0);
		cm.dispose();
	} else if (selection == 55) {	
		cm.warp(860000000,0);
		cm.dispose();
	} else if (selection == 56) {	
		cm.warp(800000000,0);
		cm.dispose();
   }
  }
 }
}