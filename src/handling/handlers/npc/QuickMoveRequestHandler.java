package handling.handlers.npc;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import script.npc.NPCScriptManager;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import tools.data.LittleEndianAccessor;

public class QuickMoveRequestHandler {

    @PacketHandler(opcode = RecvPacketOpcode.QUICK_MOVE_REQUEST)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        int npcid = lea.readInt();
        MapleNPC npc = MapleLifeFactory.getNPC(npcid);

        if (NPCScriptManager.getInstance().hasScript(c, npc.getId(), null)) { //I want it to come before shop
            NPCScriptManager.getInstance().start(c, npc.getId(), null);
        } else if (npc.hasShop()) {
            c.getPlayer().setConversation(1);
            npc.sendShop(c);
        } else {
            NPCScriptManager.getInstance().start(c, npc.getId(), null);
        }
    }

}
