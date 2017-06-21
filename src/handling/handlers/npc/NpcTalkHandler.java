package handling.handlers.npc;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.handler.NPCHandler;
import tools.data.LittleEndianAccessor;

public class NpcTalkHandler {

	@PacketHandler(opcode = RecvPacketOpcode.NPC_TALK)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		NPCHandler.NPCTalk(lea, c, c.getPlayer());
	}
}
