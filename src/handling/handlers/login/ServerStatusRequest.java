package handling.handlers.login;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.login.LoginServer;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class ServerStatusRequest {

	@PacketHandler(opcode = RecvPacketOpcode.SERVERSTATUS_REQUEST)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
        // 0 = Select world normally
        // 1 = "Since there are many users, you may encounter some..."
        // 2 = "The concurrent users in this world have reached the max"
		
        final int numPlayer = LoginServer.getUsersOn();
        final int userLimit = LoginServer.getUserLimit();
        if (numPlayer >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(1));
        } else {
            c.getSession().write(LoginPacket.getServerStatus(0));
        }
		
	}

}
