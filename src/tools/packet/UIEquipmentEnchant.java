package tools.packet;

import client.inventory.*;
import handling.SendPacketOpcode;
import tools.data.PacketWriter;

import java.util.Map;

public class UIEquipmentEnchant {

    public enum EnhanceTypes {

        SHOW_SCROLL(0x32),
        SHOW_STAR_FORCE(0x34),
        SHOW_STAR_FORCE_MINIGAME(0x35),

        SHOW_SCROLL_RESULT(0x64),
        SHOW_STAR_FORCE_RESULT(0x65),
        SHOW_SCROLL_VESTIGE_RESULT(0x66), // wtf is this
        SHOW_TRANSMISSION_RESULT(0x67),
        SHOW_UNKNOWN_FAIL_RESULT(0x68);

        private int value;

        EnhanceTypes(int value){
            this.value = value;
        }

        public byte getValue(){
            return (byte) value;
        }
    }

    /**
     * Returns the packet to show an equip in the enhancement UI.
     * @param equip Equip to be shown.
     * @param price Current price.
     * @param oldPrice Old price, if the current price is a discount.
     * @param sucChance The success chance of the enhancement.
     * @param canDropStar Whether this upgrade will drop a star if it fails.
     * @param boomChance The chance that this equipment will explode if it fails.
     * @param chanceTime Whether this enhancement will have chance time (100% chance to succeed).
     * @return
     */
    public static byte[] showNewEquip(Equip equip, long price, long oldPrice, int sucChance, int boomChance,
                                      boolean canDropStar, boolean chanceTime){
        // Failure (keep/drop a star, depending on canDropStar) = 100 - sucChance - boomChance
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.EQUIPMENT_ENCHANT.getValue());
        pw.write(EnhanceTypes.SHOW_STAR_FORCE.getValue()); // nType
        pw.write(canDropStar);
        pw.writeLong(price);
        pw.writeInt(0); // 0 writing, I know, but it's some info we probably don't need.
        pw.writeLong(oldPrice);
        pw.writeInt(0); // I'm sure I'll figure this one out soontm
        pw.writeInt(sucChance);
        pw.writeInt(boomChance);
        pw.write(chanceTime);
        pw.writeInt(0);// old chance?
        pw.writeInt(0);
        Map<EnhanceStat, Short> addStats = equip.getEnhanceStats();
        int mask = 0;
        for(EnhanceStat key : addStats.keySet()){
            mask |= key.getValue();
        }
        pw.writeInt(mask);
        if(addStats.containsKey(EnhanceStat.WATK)){
            pw.writeInt(addStats.get(EnhanceStat.WATK));
        }
        if(addStats.containsKey(EnhanceStat.MATK)){
            pw.writeInt(addStats.get(EnhanceStat.MATK));
        }
        if(addStats.containsKey(EnhanceStat.STR)){
            pw.writeInt(addStats.get(EnhanceStat.STR));
        }
        if(addStats.containsKey(EnhanceStat.DEX)){
            pw.writeInt(addStats.get(EnhanceStat.DEX));
        }
        if(addStats.containsKey(EnhanceStat.INT)){
            pw.writeInt(addStats.get(EnhanceStat.INT));
        }
        if(addStats.containsKey(EnhanceStat.LUK)){
            pw.writeInt(addStats.get(EnhanceStat.LUK));
        }
        if(addStats.containsKey(EnhanceStat.WDEF)){
            pw.writeInt(addStats.get(EnhanceStat.WDEF));
        }
//        if(addStats.containsKey(EnhanceStat.MDEF)){
//            pw.writeInt(addStats.get(EnhanceStat.MDEF));
//        }
        if(addStats.containsKey(EnhanceStat.MHP)){
            pw.writeInt(addStats.get(EnhanceStat.MHP));
        }
        if(addStats.containsKey(EnhanceStat.MMP)){
            pw.writeInt(addStats.get(EnhanceStat.MMP));
        }
//        if(addStats.containsKey(EnhanceStat.ACC)){
//            pw.writeInt(addStats.get(EnhanceStat.ACC));
//        }
//        if(addStats.containsKey(EnhanceStat.AVOID)){
//            pw.writeInt(addStats.get(EnhanceStat.AVOID));
//        }
        if(addStats.containsKey(EnhanceStat.JUMP)){
            pw.writeInt(addStats.get(EnhanceStat.JUMP));
        }
        if(addStats.containsKey(EnhanceStat.SPEED)){
            pw.writeInt(addStats.get(EnhanceStat.SPEED));
        }

        return pw.getPacket();
    }

    public static byte[] showStarForceMinigame(){
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.EQUIPMENT_ENCHANT.getValue());
        pw.writeShort(EnhanceTypes.SHOW_STAR_FORCE_MINIGAME.getValue());
        pw.write(0); // ???
        pw.writeInt(0); // ???

        return pw.getPacket();
    }

    public static byte[] showStarForceResult(int result, boolean boom, Equip oldEquip, Equip newEquip){
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.EQUIPMENT_ENCHANT.getValue());
        pw.write(EnhanceTypes.SHOW_STAR_FORCE_RESULT.getValue()); // nType
        pw.writeInt(result);
        pw.writeBoolean(boom);
        PacketHelper.addItemInfo(pw, oldEquip);
        if(!boom) {
            PacketHelper.addItemInfo(pw, newEquip);
        }

        return pw.getPacket();
    }
}
