package handling.handlers.login;

import client.MapleClient;
import constants.WorldConstants.WorldOption;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.LoginPacket;

public class CharacterWithSecondPassword {

	@PacketHandler(opcode = RecvPacketOpcode.AUTH_SECOND_PASSWORD)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		
		final String password = lea.readMapleAsciiString();
        final int charId = lea.readInt();
        lea.readByte();
        /*if (view) {
            c.setChannel(1);
            c.setWorld(slea.readInt());
        }*/
        
        // || loginFailCount(c)
        if (!c.isLoggedIn() || c.getSecondPassword() == null || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null || !WorldOption.isExists(c.getWorld())) {
            c.getSession().close();
            return;
        }
        c.updateMacs(lea.readMapleAsciiString());
        if (c.CheckSecondPassword(password) && password.length() >= 6 && password.length() <= 16 || c.isGm()) {
            if (c.getIdleTask() != null) {
                c.getIdleTask().cancel(true);
            }

            final String s = c.getSessionIPAddress();
            LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP(), c.getChannel());
            c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
            c.getSession().write(CField.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
        } else {
            c.getSession().write(LoginPacket.secondPwError((byte) 0x14));
        }
        
	}
}
