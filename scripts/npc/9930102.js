/**
 * @author: Eric
 * @npc: Kyun
 * @func: Custom Weapon Seller
*/

var currency = 4000999;
var items = [[1402998, 1100]];

function start() {
    text = "Hello there cutie, I'm Kyun, the #rCustom Weapon Vender!#k#b";
    for (var i = 0; i < items.length; text+= "\r\n#L"+i+"#" + "#z" + items[i][0] + "# - " + items[i][1] + " Munny", i++);
		cm.sendSimple(text);
}

function action(m, t, s) {
    if (m > 0) {
		if (cm.haveItem(currency, items[s][1])) {
			if (cm.canHold(items[s][0])) {
				cm.gainItem(currency, -items[s][1]);
				cm.gainItem(items[s][0], 1);
				cm.sendOk("Have fun with that~ teehee");
			} else
				cm.sendOk("Please have one space available cutie~");
		} else
			cm.sendOk("Hey you little shit, you don't have enough!");
		}
    cm.dispose();
}