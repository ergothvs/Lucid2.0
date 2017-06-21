/* Return to Masteria
	BeastTamer Tutorial
    Made by Daenerys
*/
var status = -1;

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		cm.dispose();
		return;
	}
	if (status == 0) {
		cm.sendNextNoESC("Now, go ahead and play fetch with the friendly wolves using that tree branch! Or were you gonna smack 'em? I forget.");
		cm.forceStartQuest(59005);
		cm.dispose();
	//} else if (status == 2) {
	//	cm.sendNextNoESC("Wowzers, you're clever! Hey, what's that red bar at the bottom of your screen? It says 'HP'... Let me know if that bar gets low and I'll fill it back up, because red is my favorite color!\r\n#i03800626#");
	//} else if (status == 3) {
     //   cm.forceStartQuest(59005);
	//	cm.dispose();
	}
}