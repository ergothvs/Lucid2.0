package handling.handlers.login;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.login.handler.CharLoginHandler;
import tools.data.LittleEndianAccessor;

public class DeleteCharHandler {
	
	@PacketHandler(opcode = RecvPacketOpcode.DELETE_CHAR)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		CharLoginHandler.DeleteChar(lea, c);
	}

}
