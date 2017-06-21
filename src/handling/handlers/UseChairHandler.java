package handling.handlers;

import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.MapConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class UseChairHandler {

	@PacketHandler(opcode = RecvPacketOpcode.USE_CHAIR)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		int itemid = lea.readInt();

		if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
			return;
		}
		final Item toUse = c.getPlayer().getInventory(MapleInventoryType.SETUP).findById(itemid);
		if (toUse == null) {
			return;
		}
		if (MapConstants.isFishingMap(c.getPlayer().getMapId()) && itemid == 3011000) {
			c.getPlayer().startFishingTask();
		}
		c.getPlayer().setChair(itemid);
		c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.showChair(c.getPlayer().getId(), itemid), false);
		c.getSession().write(CWvsContext.enableActions());
	}
}
