package handling.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleTrait;
import client.inventory.Equip;
import client.inventory.Equip.ScrollResult;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ItemConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import server.MapleItemInformationProvider;
import tools.Randomizer;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class UseBonusPotentialScrollHandler {

    @PacketHandler(opcode = RecvPacketOpcode.USE_BONUS_POTENTIAL)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        MapleCharacter chr = c.getPlayer();
        if (chr == null){
        	return;
        }
        lea.skip(4); //update tick
        short src = lea.readShort();
        short dst = lea.readShort();
        MapleInventoryType mit = dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
        Item item = chr.getInventory(MapleInventoryType.USE).getItem(src);
        Equip equip = (Equip) chr.getInventory(mit).getItem(dst);
        if (item == null || equip == null) {
            c.getSession().write(CWvsContext.InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        final int reqLevel = ItemConstants.getLevelByEquip(equip) / 10;
        boolean hasEnoughInsight = chr.getTrait(MapleTrait.MapleTraitType.sense).getLevel() >= GameConstants.getRequiredSense(reqLevel);
        long price = hasEnoughInsight ? 0 : GameConstants.getMagnifyPrice(equip); // free if above required insight

        if(!chr.checkAndAddMeso(-price, false)){
            chr.dropMessage(5, "You do not have enough mesos for this operation (needs " + price + ").");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        int itemId = item.getItemId();
        boolean threeLines = itemId == 2048306;
        // ghetto way of getting the scroll chance, as it's not currently stored in the data cache.
        // last word of the scroll is always xx% if it has a <100% chance, else it's "Scroll"
        String scrollName = MapleItemInformationProvider.getInstance().getName(itemId);
        String[] split = scrollName.split(" ");
        String lastSplit = split[split.length - 1];
        int successChance;
        if(lastSplit.equalsIgnoreCase("scroll")){
            successChance = 100;
        }else {
            successChance = Integer.parseInt(lastSplit.replace("%", ""));
        }
        ScrollResult success = ScrollResult.FAIL;
        if(Randomizer.nextInt(100) < successChance){
            success = Equip.ScrollResult.SUCCESS;
            equip.resetBonusPotentialWithRank(Equip.RARE, threeLines);
            equip.revealHiddenPotential();
        }

        // Update
        chr.updateItemsFromScrolling(item, equip, mit);
        c.getSession().write(CWvsContext.enableActions());
        c.getSession().write(CField.getScrollEffect(chr.getId(), success, false, equip.getItemId(), itemId));
    }
}
