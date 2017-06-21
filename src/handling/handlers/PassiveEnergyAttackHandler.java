package handling.handlers;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.handler.PlayerHandler;
import tools.data.LittleEndianAccessor;

public class PassiveEnergyAttackHandler {
	
	@PacketHandler(opcode = RecvPacketOpcode.PASSIVE_ENERGY)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		PlayerHandler.closeRangeAttack(lea, c, true);
	}

}
