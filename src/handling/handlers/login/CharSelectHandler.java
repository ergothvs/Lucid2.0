package handling.handlers.login;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;

public class CharSelectHandler {

    @PacketHandler(opcode = RecvPacketOpcode.CHAR_SELECT)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        //onCheckCharacterResult
        int charId = lea.readInt();
        //String charName = lea.readMapleAsciiString(); // You can use it for logging or something.
        final String s = c.getSessionIPAddress();
        LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP(), c.getChannel());
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
        //c.getSession().write(CField.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));// is c.getChannel() not used anymore?
    }

}