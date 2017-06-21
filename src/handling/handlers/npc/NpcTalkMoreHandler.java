package handling.handlers.npc;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.handler.NPCHandler;
import tools.data.LittleEndianAccessor;

public class NpcTalkMoreHandler {

	@PacketHandler(opcode = RecvPacketOpcode.NPC_TALK_MORE)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		NPCHandler.NPCMoreTalk(lea, c);
	}
}
