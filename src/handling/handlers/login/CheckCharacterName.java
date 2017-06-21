package handling.handlers.login;

import client.MapleCharacterUtil;
import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.login.LoginInformationProvider;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class CheckCharacterName {

	@PacketHandler(opcode = RecvPacketOpcode.CHECK_CHAR_NAME)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		String name = lea.readMapleAsciiString();
		
		LoginInformationProvider li = LoginInformationProvider.getInstance();
        boolean nameUsed = true;
        if (MapleCharacterUtil.canCreateChar(name, c.isGm())) {
            nameUsed = false;
        }
        if (li.isForbiddenName(name) && !c.isGm()) {
            nameUsed = false;
        }
        c.getSession().write(LoginPacket.charNameResponse(name, nameUsed));
		
	}

}
