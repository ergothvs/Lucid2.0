//script by Alcandon

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
			cm.sendOk ("Newbies Guide ~~~ \r\n@blade for All in one NPCs\r\nDon't forget occupation, @occupation\r\nRates 500x/250x/4x\r\n@commands / @help / @commands to view all commands\r\nTrade button warps you to the FM\r\nVote for Points!!\r\n\r\nHehehehe Have fun noobs <3\r\nPot Seller, @shop");
cm.dispose();
			}
			}
			}
			