package handling.handlers;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.BattleRecordManager;

public class BattleAnalysisRequestHandler {

    @PacketHandler(opcode = RecvPacketOpcode.BATTLE_RECORD_CALC_REQUEST)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        boolean isActive = lea.readByte() != 0;
        boolean idk = lea.readByte() != 0;
        boolean idk2 = lea.readByte() != 0;
        if(isActive) {
            BattleRecordManager.serverCalcRequestResult(isActive);
        }
    }
}
