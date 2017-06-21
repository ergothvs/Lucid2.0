package handling.handlers;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;

public class CancelChairHandler {

	@PacketHandler(opcode = RecvPacketOpcode.CANCEL_CHAIR)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		short id = lea.readShort();

		if (c.getPlayer() == null || c.getPlayer().getMap() == null){
			c.disconnect(false, false);
			return;
		}
		
		c.getSession().write(CField.cancelChair(id, c.getPlayer().getId()));
		
		if (id == -1) {
			c.getPlayer().cancelFishingTask();
			c.getPlayer().setChair(0);
			c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.showChair(c.getPlayer().getId(), 0), false);
		} else {
			c.getPlayer().setChair(id);
		}
	}
}
