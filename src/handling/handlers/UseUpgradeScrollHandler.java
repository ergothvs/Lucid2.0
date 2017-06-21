package handling.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.handler.InventoryHandler;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class UseUpgradeScrollHandler {

    @PacketHandler(opcode = RecvPacketOpcode.USE_UPGRADE_SCROLL)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        MapleCharacter chr = c.getPlayer();
        if(chr == null){
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        int tick = lea.readInt(); // unused for now
        short src = lea.readShort();
        short dst = lea.readShort();
        short ws = lea.readShort();
//        boolean whiteScroll = false;
        boolean legendarySpirit = lea.readByte() == 1; // does this even exist anymore? Probably not.

        // old inventory handler seemed to do stuff just fine, so we'll simply transfer it to that.
        InventoryHandler.UseUpgradeScroll(src, dst, ws, c, chr, 0, legendarySpirit);

//        MapleInventoryType mit = dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
//
//        Item item = chr.getInventory(MapleInventoryType.USE).getItem(src);
//        if (item == null) {
//            item = chr.getInventory(MapleInventoryType.CASH).getItem(src); //just in case
//        }
//        Equip equip;
//        equip = (Equip) chr.getInventory(mit).getItem(dst);
//        if (item == null || equip == null) {
//            c.getSession().write(CWvsContext.InventoryPacket.getInventoryFull());
//            c.getSession().write(CWvsContext.enableActions());
//            return;
//        }
//        if ((ws & 2) == 2) {
//            whiteScroll = true;
//        }


    }
}
