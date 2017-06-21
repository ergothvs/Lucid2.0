package handling.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class BlackCubeResultHandler {

    @PacketHandler(opcode = RecvPacketOpcode.BLACK_CUBE_RESULT)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        MapleCharacter chr = c.getPlayer();
        if (chr == null){
            c.getSession().write(CWvsContext.enableActions());
        	return;
        }
        lea.skip(4); // update tick
        short choice = lea.readShort();
        long uniqueId = lea.readLong(); // currently not used, may be implemented in the future.
        boolean choseOld = choice == 7; // old choice = 7, new choice = 6
        if(choseOld){
        	Equip equip = chr.getLastBlackCubedItem();
        	if (equip == null){
        		//should not happen under normal circumstances
                c.getSession().write(CWvsContext.enableActions());
                return;
        	}
            equip.setPotential(equip.getOldPotential());
            MapleInventoryType mit = equip.getPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
            chr.forceReAddItem(equip, mit);
        }
        c.getSession().write(CWvsContext.enableActions());
        chr.setLastBlackCubedItem(null); // to indicate user has finished cubing
    }
}
