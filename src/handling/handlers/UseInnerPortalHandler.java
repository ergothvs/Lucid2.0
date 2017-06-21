package handling.handlers;

import java.awt.Point;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import server.MaplePortal;
import tools.data.LittleEndianAccessor;

public class UseInnerPortalHandler {

	@PacketHandler(opcode = RecvPacketOpcode.USE_INNER_PORTAL)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		lea.skip(1);
		if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
			return;
		}
		String portalName = lea.readMapleAsciiString();
		MaplePortal portal = c.getPlayer().getMap().getPortal(portalName);

		if (portal == null) {
			return;
		}
		//That "22500" should not be hard coded in this manner
		if (portal.getPosition().distanceSq(c.getPlayer().getTruePosition()) > 22500.0D && !c.getPlayer().isGM()) {
			return;
		}
		
		int toX = lea.readShort();
		int toY = lea.readShort();
		
		//Are there not suppose to be checks here? Can players not just PE any x and y value they want?
		
		c.getPlayer().getMap().movePlayer(c.getPlayer(), new Point(toX, toY));
		c.getPlayer().checkFollow();
	}
}
