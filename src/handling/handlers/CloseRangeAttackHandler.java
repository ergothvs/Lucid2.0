package handling.handlers;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.handler.PlayerHandler;
import tools.data.LittleEndianAccessor;

public class CloseRangeAttackHandler {

	@PacketHandler(opcode = RecvPacketOpcode.CLOSE_RANGE_ATTACK)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		PlayerHandler.closeRangeAttack(lea, c, false);
	}
}
