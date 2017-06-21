package handling.handlers;

import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import server.quest.MapleQuest;
import tools.data.LittleEndianAccessor;

import java.sql.SQLException;

public class ChangeKeymapHandler {

	@PacketHandler(opcode = RecvPacketOpcode.CHANGE_KEYMAP)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		if (c.getPlayer() == null){
			return;
		}
		if (lea.available() > 8L) {
			handleKeyBindingChanges(c, lea);
		} else {
			handleQuests(c, lea);
		}
	}

	private static void handleKeyBindingChanges(MapleClient c, LittleEndianAccessor lea){
		lea.skip(4);
		int changes = lea.readInt();

		for (int i = 0; i < changes; i++) {
			int key = lea.readInt();
			byte type = lea.readByte();
			int action = lea.readInt();
//			if (type != 1 || action < 1000 || GameConstants.isLinkedAttackSkill(action) || action % 10000 < 1000){
//				continue;
//			}
			Skill skill = SkillFactory.getSkill(action);
			if (type == 1 && skill != null && !skill.isFourthJob() && !skill.isBeginnerSkill() && skill.isInvisible() && c.getPlayer().getSkillLevel(skill) <= 0) {
				continue;
			}
			c.getPlayer().changeKeybinding(key, type, action);
		}
		try {
			c.getPlayer().getKeyLayout().saveKeys(c.getPlayer().getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void handleQuests(MapleClient c, LittleEndianAccessor lea){
		int type = lea.readInt();
		int data = lea.readInt();
		switch (type) {
		case 1:
			if (data <= 0) {
				c.getPlayer().getQuestRemove(MapleQuest.getInstance(GameConstants.HP_ITEM));
			} else {
				c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.HP_ITEM)).setCustomData(String.valueOf(data));
			}
			break;
		case 2:
			if (data <= 0) {
				c.getPlayer().getQuestRemove(MapleQuest.getInstance(GameConstants.MP_ITEM));
			} else {
				c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.MP_ITEM)).setCustomData(String.valueOf(data));
			}
			break;
		}
	}
}
