package handling.handlers;

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
import handling.channel.handler.InventoryHandler;
import server.MapleItemInformationProvider;
import tools.ArrayUtil;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class UseCashItemHandler {

    //[Recv] (0x00FE) Data: FA 28 65 00 04 00 79 3D 4D 00 01 00 00 00
    //[Recv] (0x00FE) Data: [0C 53 65 00] [04 00] [79 3D 4D 00] [01 00] [00 00]
    // 0xFE int:updateTick short:src(nUPos) int:itemId short:dst short:?

    @PacketHandler(opcode = RecvPacketOpcode.USE_CASH_ITEM)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        MapleCharacter chr = c.getPlayer();
        if(chr == null) {
            return;
        }
        lea.skip(4); //update tick
        short src = lea.readShort();
        int itemId = lea.readInt();

        if(GameConstants.getCashCubeByItemId(itemId) != null) {
            handleCashCubes(c, chr, lea, src, itemId);
        }else if(ItemConstants.isHyperTeleportRock(itemId)){
            InventoryHandler.UseTeleRock(lea, c, itemId);
        }

        else{
            chr.dropMessage(5, "You have used a cash item currently not known by the server.");
            chr.dropMessage(5, "Be sure to report this problem. ItemID = " + itemId);
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static void handleCashCubes(MapleClient c, MapleCharacter chr, LittleEndianAccessor lea, short src, int itemId){
        short dst = lea.readShort();
        MapleInventoryType mit = dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;

        Item item = chr.getInventory(MapleInventoryType.CASH).getItem(src);
        if (item == null) {
            item = chr.getInventory(MapleInventoryType.USE).getItem(src); //just in case
        }
        Equip equip;
        equip = (Equip) chr.getInventory(mit).getItem(dst);
        if (item == null || equip == null) {
            c.getSession().write(CWvsContext.InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        if(chr.getLastBlackCubedItem() == null && GameConstants.getCashCubeByItemId(itemId) == GameConstants.Cubes.BLACK){
            chr.setLastBlackCubedItem(equip);
            equip.setOldPotential(ArrayUtil.copy(equip.getPotential()));
        }

        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(equip.getItemId()) / 10;
        boolean hasEnoughInsight = chr.getTrait(MapleTrait.MapleTraitType.sense).getLevel() >= GameConstants.getRequiredSense(reqLevel);
        long price = hasEnoughInsight ? 0 : GameConstants.getMagnifyPrice(equip); // free if above required insight

        if(!chr.checkAndAddMeso(-price, false)){
            chr.dropMessage(5, "You do not have enough mesos for this action.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        int oldState = equip.getState();
        equip.renewPotential(GameConstants.getCashCubeByItemId(itemId));
        equip.revealHiddenPotential();
        boolean hasRankedUp = oldState != equip.getState();

        // Update

        chr.updateItemsFromScrolling(item, equip, mit);

        c.getSession().write(CWvsContext.enableActions());

        GameConstants.Cubes cube = GameConstants.getCashCubeByItemId(itemId);
        if(cube == GameConstants.Cubes.BLACK){
            c.getSession().write(CField.showBlackCubePotentialReset(chr.getId(), true, itemId));
            c.getSession().write(CWvsContext.onBlackCubeRequest(true, itemId, src, dst, equip));
        } else if(cube == GameConstants.Cubes.RED){
            c.getSession().write(CField.showPotentialReset(chr.getId(), true, itemId));
            c.getSession().write(CWvsContext.onRedCubeResult(chr.getId(), hasRankedUp, itemId, dst, equip));
        } else{
            c.getSession().write(CField.showPotentialReset(chr.getId(), true, itemId));
        }
    }
}
