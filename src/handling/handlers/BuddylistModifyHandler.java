package handling.handlers;


import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.handler.BuddyListHandler;
import tools.data.LittleEndianAccessor;

public class BuddylistModifyHandler {

    @PacketHandler(opcode = RecvPacketOpcode.BUDDYLIST_MODIFY)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        BuddyListHandler.BuddyOperation(lea, c);
    }
}
