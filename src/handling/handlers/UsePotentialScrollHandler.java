package handling.handlers;

import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import server.MapleItemInformationProvider;
import tools.Randomizer;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class UsePotentialScrollHandler {

    @PacketHandler(opcode = RecvPacketOpcode.USE_POTENTIAL_SCROLL)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        MapleCharacter chr = c.getPlayer();
        if(chr == null){
            return;
        }
        lea.skip(4); //update tick
        short scrollSlot = lea.readShort();
        short equipSlot = lea.readShort();
        // the rest is whatever
        Item scroll = chr.getInventory(MapleInventoryType.USE).getItem(scrollSlot);
        if (scroll == null) {
            scroll = chr.getInventory(MapleInventoryType.CASH).getItem(scrollSlot);
        }
        MapleInventoryType mit = equipSlot < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
        Equip equip;
        equip = (Equip) chr.getInventory(mit).getItem(equipSlot);
        if(scroll == null || equip == null){
            c.getSession().write(CWvsContext.InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        Equip.ScrollResult scrollSuccess;
        if(equip.getState() == Equip.HIDDEN){
            chr.dropMessage(5, "Please unveil the potential before using a potential scroll on this item.");
            c.getSession().write(CWvsContext.InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return;
        }else{
            Map<String, Integer> scrollInfo = MapleItemInformationProvider.getInstance().getEquipStats(scroll.getItemId());
            final int curseChance = (scrollInfo != null && !scrollInfo.containsKey("cursed")) ? 0 : scrollInfo.get("cursed");
            if(equip.usePotentialScroll(scroll.getItemId())){ //check for boom
                boolean boom = Randomizer.nextInt(100) > curseChance;
                if(!boom || (boom && ItemFlag.SHIELD_WARD.check(equip.getFlag()))){
                    equip.setFlag((short) (equip.getFlag() - ItemFlag.SHIELD_WARD.getValue()));
                    scrollSuccess = Equip.ScrollResult.FAIL;
                }else {
                    scrollSuccess = Equip.ScrollResult.CURSE;
                }
            }else{
                scrollSuccess = Equip.ScrollResult.SUCCESS;
            }

            // Update

            if (scrollSuccess == Equip.ScrollResult.CURSE) {
                c.getSession().write(CWvsContext.InventoryPacket.scrolledItem(scroll, MapleInventoryType.EQUIP, equip, true, false, false));
                chr.getInventory(mit).removeItem(equip.getPosition());
            }

            chr.updateItemsFromScrolling(scroll, equip, mit);

            chr.getMap().broadcastMessage(chr, CField.getScrollEffect(c.getPlayer().getId(), scrollSuccess, false, equip.getItemId(), scroll.getItemId()), false);
            c.getSession().write(CField.enchantResult(scrollSuccess == Equip.ScrollResult.SUCCESS ? 1 : scrollSuccess == Equip.ScrollResult.CURSE ? 2 : 0));
            //addToScrollLog(chr.getAccountID(), chr.getId(), scroll.getItemId(), itemID, oldSlots, (byte)(scrolled == null ? -1 : scrolled.getUpgradeSlots()), oldVH, scrollSuccess.name(), whiteScroll, legendarySpirit, vegas);
            // equipped item was scrolled and changed
        }
        c.getSession().write(CWvsContext.enableActions());
        chr.forceReAddItem(equip, equipSlot < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
    }
}
