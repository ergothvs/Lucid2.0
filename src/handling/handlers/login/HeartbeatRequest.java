package handling.handlers.login;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class HeartbeatRequest {

	@PacketHandler(opcode = RecvPacketOpcode.AUTH_REQUEST)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
        c.getSession().write(LoginPacket.sendAuthResponse(SendPacketOpcode.AUTH_RESPONSE.getValue() ^ lea.readInt()));
	}

}
