package handling.handlers;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.cashshop.handler.CashShopOperation;
import handling.channel.handler.PlayerHandler;
import tools.data.LittleEndianAccessor;

public class ChangeMapHandler {

	@PacketHandler(opcode = RecvPacketOpcode.CHANGE_MAP)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
            if (c.getPlayer().getMap() == null) {
                CashShopOperation.LeaveCS(lea, c, c.getPlayer());
            } else {
                PlayerHandler.ChangeMap(lea, c, c.getPlayer());
            }
	}
}
