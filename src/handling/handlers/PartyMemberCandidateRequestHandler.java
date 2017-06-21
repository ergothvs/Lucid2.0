package handling.handlers;


import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class PartyMemberCandidateRequestHandler {

    @PacketHandler(opcode = RecvPacketOpcode.PARTY_MEMBER_CANDIDATE_REQUEST)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        if(c.getPlayer() == null){
            return;
        }
        c.getSession().write(CWvsContext.PartyPacket.partyMemberCandidateResult(c.getPlayer()));
    }
}
