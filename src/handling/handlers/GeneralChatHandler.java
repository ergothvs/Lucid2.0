package handling.handlers;

import client.MapleClient;
import constants.ServerConstants.CommandType;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import server.commands.CommandProcessor;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class GeneralChatHandler {

	@PacketHandler(opcode = RecvPacketOpcode.GENERAL_CHAT)
	public static void handle(MapleClient c, LittleEndianAccessor slea){
		if (c.getPlayer() == null || c.getPlayer().getMap() == null){
			return;
		}
		slea.skip(4); // update tick
		String text = slea.readMapleAsciiString();
		byte balloon = slea.readByte();

		if (CommandProcessor.processCommand(c, text, CommandType.NORMAL)){
			return;
		}
		if (text.length() < 1 || (!c.getPlayer().isIntern() && text.length() >= 80)) {
			return;
		}
		if (!c.getPlayer().getCanTalk()){
			c.getSession().write(CWvsContext.broadcastMsg(6, "You have been muted and are therefore unable to talk."));
			return;
		}
		if (!c.getPlayer().isHidden()){
			c.getPlayer().getMap().broadcastMessage(CField.getChatText(c.getPlayer().getId(), text, false, balloon));
			c.getPlayer().getMap().broadcastMessage(CField.getChatText(c.getPlayer().getId(), text, c.getPlayer().isSuperGM(), 1));
			return;
		}
		// Note: This patch is needed to prevent chat packet from being broadcast to people who might be packet sniffing.
		if (c.getPlayer().isIntern() && !c.getPlayer().isSuperGM() && balloon == 0) {
			c.getPlayer().getMap().broadcastGMMessage(c.getPlayer(), CField.getChatText(c.getPlayer().getId(), text, c.getPlayer().isSuperGM(), (byte) 1), true);
			c.getPlayer().getMap().broadcastGMMessage(c.getPlayer(), CWvsContext.broadcastMsg(2, c.getPlayer().getName() + " : " + text), true);
		} else {
			c.getPlayer().getMap().broadcastGMMessage(c.getPlayer(), CField.getChatText(c.getPlayer().getId(), text, c.getPlayer().isSuperGM(), balloon), true);
		}
	}
}
