importPackage(Packages.tools.packet);

function start() {
	if (cm.getPlayer().getItemEffect() > 0) {
		cm.getPlayer().getMap().broadcastMessage(cm.getPlayer(), CField.itemEffect(cm.getPlayer().getId(), cm.getPlayer().getItemEffect()), false);
		cm.sendOk("You have a Cash Item Effect with an ID of #e" + cm.getPlayer().getItemEffect() + "#n.");
		cm.dispose();
	} else {
		cm.sendOk("You don't have an Item Effect.");
		cm.dispose();
	}
}