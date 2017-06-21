package tools.packet;

import handling.SendPacketOpcode;
import tools.data.PacketWriter;

public class BattleRecordManager {

    public static byte[] serverCalcRequestResult(boolean isActive){
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.SERVER_ON_CALC_REQUEST_RESULT.getValue());
        pw.write(isActive);

        return pw.getPacket();
    }
}
