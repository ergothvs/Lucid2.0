/* Grand Athenaeum
    Skylark Rita
    Made by Daenerys
*/
//todo: handle the selections
load("nashorn:mozilla_compat.js");
importPackage(Packages.tools.packet);

var status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
	} else {
		status++;
    if (status == 0) {
	    cm.sendSimple("Hello! Which donation reward would you like? You have #r0#k Units\r\n#b#L0#Max Stat Item - 5 USD\r\n#b#L1#5 Max Stat Item - 15 USD\r\n#b#L2#Donator commands - 10 USD#l\r\n#b#L3#Super Donator commands - 30 USD#l\r\n#b#L5#200000 NX - 2 USD#l\r\n#b#L6#Name change - 10 USD#l");
    } else if (status == 1) {
		if (mode != 0) {
		cm.dispose();
		}
	if (selection == 0) {		
	    cm.sendNext("You need #r5#k Units to get this donation reward!");
		cm.dispose();
	} else if (selection == 1) {	
		//cm.sendNext("You need #r15#k Units to get this donation reward!");
                for(i=0;i<1;i++) {
                cm.getPlayer().getClient().getSession().write(CWvsContext.sendWelcomeBack(cm.getPlayer()));
                }
		cm.dispose();
	} else if (selection == 2) {	
		cm.sendNext("You need #r10#k Units to get this donation reward!");
		cm.dispose();
	} else if (selection == 3) {	
		cm.sendNext("You need #r30#k Units to get this donation reward!");
		cm.dispose();
	} else if (selection == 5) {	
		cm.sendNext("You need #r2#k Units to get this donation reward!");
		cm.dispose();
	} else if (selection == 6) {	
		cm.sendNext("You need #r10#k Units to get this donation reward!");
		cm.dispose();
   }
  }
 }
}