package handling.handlers;

import java.util.LinkedList;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleTrait;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ItemConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import server.MapleItemInformationProvider;
import server.StructItemOption;
import tools.Randomizer;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class UseMagnifyGlassHandler {

    @PacketHandler(opcode = RecvPacketOpcode.USE_MAGNIFY_GLASS)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        MapleCharacter chr = c.getPlayer();
        if(chr == null){
            return;
        }
        lea.skip(4); //update tick
        short src = lea.readShort(); // Assumed 0x7F, inventory glass. Don't think you can get the other ones anymore.
        short dst = lea.readShort();
        MapleInventoryType mit = dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
        Item item = chr.getInventory(mit).getItem(dst);
        if (item == null || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.InventoryPacket.getInventoryFull());
            return;
        }
        Equip equip = (Equip) item;
        final int reqLevel = ItemConstants.getLevelByEquip(equip) / 10;
        boolean hasEnoughInsight = chr.getTrait(MapleTrait.MapleTraitType.sense).getLevel() >= GameConstants.getRequiredSense(reqLevel);
        long price = hasEnoughInsight ? 0 : GameConstants.getMagnifyPrice(equip); // free if above required insight



        if(equip.getState() == 1 && chr.checkAndAddMeso(-price, false)){
            equip.revealHiddenPotential();
            c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(chr.getId(), true, equip.getItemId()));
            c.getSession().write(CWvsContext.enableActions());
            c.getPlayer().forceReAddItem(equip, mit);
        }else{
            //client blocks revealing >0 state equips, no error messag needed for this.
            chr.dropMessage(5, "You do not have enough mesos to do this.");
            c.getSession().write(CWvsContext.enableActions());
        }

    }
}
