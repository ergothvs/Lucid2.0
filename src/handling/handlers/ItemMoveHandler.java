package handling.handlers;

import client.MapleClient;
import client.inventory.MapleInventoryType;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import server.MapleInventoryManipulator;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class ItemMoveHandler {

    @PacketHandler(opcode = RecvPacketOpcode.ITEM_MOVE)
    public static void handle(MapleClient c, LittleEndianAccessor lea) {
        if(c.getPlayer().hasBlockedInventory()){ 
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
        int tick = lea.readInt();
        MapleInventoryType type = MapleInventoryType.getByType(lea.readByte());
        short src = lea.readShort();
        short dst = lea.readShort();
        if(type == MapleInventoryType.EQUIP && src < 0){
            type = MapleInventoryType.EQUIPPED;
        }
        short quantity = lea.readShort();
        if(c.getPlayer().getInventory(type).getItem(src) == null){
            c.getSession().write(CWvsContext.InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (src < 0 && dst > 0) {
            MapleInventoryManipulator.unequip(c, src, dst);
        } else if (dst < 0) {
            MapleInventoryManipulator.equip(c, src, dst);
        } else if (dst == 0) {
            MapleInventoryManipulator.drop(c, type, src, quantity);
        } else {
            MapleInventoryManipulator.move(c, type, src, dst);
        }
    }
}