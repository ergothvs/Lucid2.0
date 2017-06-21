package handling.handlers;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import constants.ItemConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import tools.Randomizer;
import tools.Triple;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.UIEquipmentEnchant;

public class UseEnhancementHandler {

    @PacketHandler(opcode = RecvPacketOpcode.ITEM_UPGRADE_UI)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        byte type = lea.readByte();

        Equip equip;
        switch(type){
            case 0x1: {
                lea.skip(4); // tick
                short src = lea.readShort();
                int extraChanceFromMinigame = lea.readByte() != 0 ? 50 : 0;
                // the rest we don't use: tick on which the minigame is won, (int 1) and (int -1 (FF FF FF FF))
                equip = (Equip) c.getPlayer().getInventory(src < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP).getItem(src);
                int result = 1;
                boolean hasBoomed = false;
                Equip oldEquip = (Equip) equip.copy();

                Triple<Integer, Integer, Boolean> starInfo = ItemConstants.getStarChanceInfo().get(equip.getEnhance());
                boolean hasChanceTime = equip.getSuccessiveEnhanceFails() >= 2;
                long price = ItemConstants.getEnhancementCost(equip);

                if (!c.getPlayer().checkAndAddMeso(-price, false)) {
                    // should never happen, as client stops this, but just in case.
                    c.getPlayer().dropMessage(5, "You do not have enough mesos for this action.");
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }

                int successChance;
                if (hasChanceTime) {
                    equip.setSuccessiveEnhanceFails(0);
                    successChance = 1000;
                } else {
                    successChance = starInfo.getLeft() + extraChanceFromMinigame;
                }
                int boomChance = starInfo.getMid();
                boolean canDropStar = starInfo.getRight();

                if (Randomizer.nextInt(1000) < successChance) {
                    equip.enhance();
                } else {
                    result = 0;
                    if (Randomizer.nextInt(1000) < boomChance) {
                        c.getPlayer().dropMessage(5, "The powerful force has made your item into an equipment trace.");
                        hasBoomed = true;
                        // TODO: Figure out how to make an equip trace
                        // probably setting a flag and removing stars
                        equip.setEnhance((byte) (equip.getEnhance() - 1));
                        int currentStars = equip.getEnhance();
                        for(int i = 0; i < currentStars; i++){
                            // not i < equip.getEnhance(), as this changes as we remove stars. (Would probably
                            // still get the correct result, though.
                            equip.removeStar();
                        }
                    } else if (canDropStar) {
                        equip.removeStar();
                    }
                    equip.setSuccessiveEnhanceFails(equip.getSuccessiveEnhanceFails() + 1);
                }

                c.getPlayer().updateItemsFromScrolling(null, equip, src < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);

                c.getSession().write(UIEquipmentEnchant.showStarForceResult(result, hasBoomed, oldEquip, equip));
                c.getSession().write(CWvsContext.enableActions());
                break;
            }
            case 0x32:
                //scrolling
                break;
            case 0x34: {
                int itemPos = lea.readInt();
                equip = (Equip) c.getPlayer().getInventory(itemPos < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP).getItem((short) itemPos);
                Triple<Integer, Integer, Boolean> starInfo = ItemConstants.getStarChanceInfo().get(equip.getEnhance());
                boolean hasChanceTime = equip.getSuccessiveEnhanceFails() >= 2;
                long price = ItemConstants.getEnhancementCost(equip);
                if (hasChanceTime) {
                    // 100% chance to succeed
                    c.getSession().write(UIEquipmentEnchant.showNewEquip(equip, price, 0, 1000, 0, false, true));
                } else {
                    c.getSession().write(UIEquipmentEnchant.showNewEquip(equip, price,
                            0, starInfo.getLeft(), starInfo.getMid(), starInfo.getRight(), false));
                }
                break;
            }
            case 0x35:
                c.getSession().write(UIEquipmentEnchant.showStarForceMinigame());
                break;
//            case 100:
//            case 101:
//                int result = lea.readInt(); // 0 = fail, 1 = succeed
//                boolean boom = lea.readByte() != 0;
//                break;
            default:
                c.getPlayer().dropMessage(5, String.format("Unknown upgrade UI type \"", type + "\"."));
                break;
        }
    }
}
