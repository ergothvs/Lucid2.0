/* Grand Athanaeum
    Tot Guide
    Made by Daenerys
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	    cm.sendNextS("Did you notice the red-haired boy icon on the left side of your screen?\r\n\r\n#v3800475# This is Tot's Know How, a helper that will guide you through #e#rlevels 1 to 60#n#k. He will even #e#rhelp you level up instantly#n#k when you complete quests!\r\nPress the button on the left or hotkey #e#r' - '#n#k to check it out!",5,9010000);
        cm.dispose();
    }
}
