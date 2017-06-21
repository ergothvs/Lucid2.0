package handling.handlers.login;

import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import constants.WorldConstants.WorldOption;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.login.LoginServer;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class ServerListRequest 	{

	@PacketHandler(opcode = RecvPacketOpcode.SERVERLIST_REQUEST)	
	public static void getServerListRequest(MapleClient c, LittleEndianAccessor slea) {
		ServerListRequest(c);
	}
	
	@PacketHandler(opcode = RecvPacketOpcode.REDISPLAY_SERVERLIST)
	public static void getRedisplayServerList(MapleClient c, LittleEndianAccessor lea) {
		ServerListRequest(c);
	}
	
	private static void ServerListRequest(MapleClient c) {

        for (WorldOption servers : WorldOption.values()) {
            if (WorldOption.getById(servers.getWorld()).show() && servers != null) {
                c.getSession().write(LoginPacket.getServerList(servers.getWorld(), LoginServer.getLoad()));
            }
        }
        
        c.getSession().write(LoginPacket.getEndOfServerList());
        boolean hasCharacters = false;
        for (int world = 0; world < WorldOption.values().length; world++) {
            final List<MapleCharacter> chars = c.loadCharacters(world);
            if (chars != null) {
                hasCharacters = true;
                break;
            }
        }

        if (!hasCharacters) {
            c.getSession().write(LoginPacket.enableRecommended(WorldOption.recommended));
        }
        if (WorldOption.recommended >= 0) {
            c.getSession().write(LoginPacket.sendRecommended(WorldOption.recommended, WorldOption.recommendedmsg));
        }
	}

}
