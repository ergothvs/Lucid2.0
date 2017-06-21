package handling.handlers.login;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class ClientStartHandler {

	@PacketHandler(opcode = RecvPacketOpcode.CLIENT_START)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		c.getSession().write(LoginPacket.getStart());
	}

}
