package handling.handlers;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import server.MaplePortal;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class ChangeMapSpecialHandler {

	@PacketHandler(opcode = RecvPacketOpcode.CHANGE_MAP_SPECIAL)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
        lea.skip(1);
        String portalName = lea.readMapleAsciiString();
        
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
			return;
		}
		
        MaplePortal portal = c.getPlayer().getMap().getPortal(portalName);
        
		if (portal == null && c.getPlayer().hasBlockedInventory()) {
			c.getSession().write(CWvsContext.enableActions());
			return;
		}
		
		portal.enterPortal(c);
	}
}
