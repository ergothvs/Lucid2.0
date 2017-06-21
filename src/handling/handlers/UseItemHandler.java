package handling.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.FieldLimitType;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class UseItemHandler {

    @PacketHandler(opcode = RecvPacketOpcode.USE_ITEM)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        MapleCharacter chr = c.getPlayer();
        if(chr == null || !chr.canUsePotion()){
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        long time = System.currentTimeMillis();
        if(chr.getNextConsume() > time){
            chr.dropMessage(5, "You may not use this item yet.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        int tick = lea.readInt();
        final byte slot = (byte) lea.readShort();
        final int itemId = lea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            //drop error perhaps?
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit()) && //cwk quick hack
            MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
            if (chr.getMap().getConsumeItemCoolTime() > 0) {
                chr.setNextConsume(time + (chr.getMap().getConsumeItemCoolTime() * 1000));
            }
        } else {
            c.getSession().write(CWvsContext.enableActions());
        }
    }
}
