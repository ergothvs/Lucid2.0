package handling.handlers;

import client.MapleClient;
import constants.GameConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import script.npc.NPCScriptManager;
import server.quest.MapleQuest;
import tools.data.LittleEndianAccessor;
import tools.packet.CField.EffectPacket;
import tools.packet.CWvsContext;

public class QuestActionHandler {

	@PacketHandler(opcode = RecvPacketOpcode.QUEST_ACTION)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		byte action = lea.readByte();
        int quest = lea.readInt();
        
        if (quest == 20734) {
            c.getSession().write(CWvsContext.ultimateExplorer());
            return;
        }
        
        if (c.getPlayer() == null){
            return;
        }
        
        MapleQuest q = MapleQuest.getInstance(quest);
        switch (action) {
            case 0: { // restore lost item
                lea.readInt(); // update tick
                final int itemid = lea.readInt();
                q.RestoreLostItem(c.getPlayer(), itemid);
                break;
            }
            case 1: { // start quest
                int npc = lea.readInt();
                
                if (npc == 0 && quest > 0) {
                    q.forceStart(c.getPlayer(), npc, null);
                } else if (!q.hasStartScript()) {
                    q.start(c.getPlayer(), npc);
                }
                break;
            }
            case 2: { // complete quest
                int npc = lea.readInt();
                lea.readInt(); // update tick
                
                if (q.hasEndScript())
                    return;
                
                if (lea.available() >= 4) {
                	int selection = lea.readInt();
                    q.complete(c.getPlayer(), npc, selection);
                } else {
                    q.complete(c.getPlayer(), npc);
                }
                break;
            }
            case 3: { // forfeit quest
                if (GameConstants.canForfeit(q.getId())) {
                    q.forfeit(c.getPlayer());
                } else {
                    c.getPlayer().dropMessage(1, "You may not forfeit this quest.");
                }
                break;
            }
            case 4: { // scripted start quest
                int npc = lea.readInt();
                
                if (c.getPlayer().hasBlockedInventory()){
                    return;
                }
                
                NPCScriptManager.getInstance().startQuest(c, npc, quest);
                break;
            }
            case 5: { // scripted end quest
                int npc = lea.readInt();
                
                if (c.getPlayer().hasBlockedInventory())
                    return;
                
                NPCScriptManager.getInstance().endQuest(c, npc, quest, false);
                c.getSession().write(EffectPacket.showForeignEffect(12));
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), EffectPacket.showForeignEffect(c.getPlayer().getId(), 12), false);
                break;
            }
        } 
	}
}
