package handling.handlers.login;

import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import constants.WorldConstants.WorldOption;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.ChannelServer;
import handling.world.World;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class CharacterListRequest {

	@PacketHandler(opcode = RecvPacketOpcode.CHARLIST_REQUEST)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		if (!c.isLoggedIn()) {
			c.getSession().close();
			return;
		}
		lea.readByte();
		final int server = lea.readByte();
		final int channel = lea.readByte() + 1;
		
		if (!World.isChannelAvailable(channel, server) || !WorldOption.isExists(server)) {
			c.getSession().write(LoginPacket.getLoginFailed(10)); // cannot process so many
			return;
		}

		List<MapleCharacter> chars = c.loadCharacters(server);
		
		if (chars == null || ChannelServer.getInstance(channel) == null){
			c.getSession().close();
			return;
		}

		c.setWorld(server);
		c.setChannel(channel);
		c.getSession().write(LoginPacket.getSecondAuthSuccess(c));
                c.getSession().write(LoginPacket.getWorld(server));
		c.getSession().write(LoginPacket.getAccountName(c.getAccountName()));
		c.getSession().write(LoginPacket.getCharList(c, chars));
		c.getSession().write(LoginPacket.sendAllowedCreation());
	}
}
