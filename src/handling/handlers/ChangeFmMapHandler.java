package handling.handlers;

import client.MapleCharacter;
import client.MapleClient;
import constants.MapConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.ChannelServer;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class ChangeFmMapHandler {

	@PacketHandler(opcode = RecvPacketOpcode.CHANGE_FM_MAP)
	public static void handle(MapleClient c, LittleEndianAccessor lea){
		MapleCharacter chr = c.getPlayer();

		if (chr == null || chr.hasBlockedInventory() || chr.getEventInstance() != null || chr.getMap() == null || chr.isInBlockedMap() || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())) {
			c.getSession().write(CWvsContext.enableActions());
			return;
		}

		byte toChannel = (byte) (lea.readByte() + 1); // nChannelID
		int toMap = lea.readInt(); // dwFieldID
		int localTime = lea.readInt(); // update tick

		if(!MapConstants.isFmMap(chr.getMapId()) || !MapConstants.isFmMap(toMap)) {
			//not in the fm or trying to teleport to a non-fm map
			c.getSession().write(CWvsContext.enableActions());
			return;
		}

		if (c.getChannel() != toChannel) {	
			//cc them if they asked for it
			chr.changeChannel(toChannel);		
		}

		if (chr.getMapId() != toMap){
			//change map if they asked for it
			MapleMap map = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(toMap);		
			chr.changeMap(map, map.getPortal("out00"));
		}
	}
}
