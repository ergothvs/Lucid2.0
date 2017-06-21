package handling.handlers.npc;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.handler.NPCHandler;
import tools.data.LittleEndianAccessor;

public class NpcActionHandler {

	@PacketHandler(opcode = RecvPacketOpcode.NPC_ACTION)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		NPCHandler.NPCAnimation(lea, c);
	}
}
