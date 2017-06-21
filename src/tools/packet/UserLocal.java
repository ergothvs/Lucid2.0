package tools.packet;

import handling.SendPacketOpcode;
import tools.data.PacketWriter;
import tools.packet.enums.ReviveType;

public class UserLocal {

    /**
     * Opens the ui if a character has died.
     * @param onDeadRevive Whether or not the character can revive here.
     * @param onDeadProtectForBuff Whether or not the character has a buff protector.
     * @param onDeadProtectExp Whether or not the character has an exp protector.
     * @param reviveType No idea, but it returns instantly if this is 10.
     * @return The packet with the given arguments.
     */
    public static byte[] openUIOnDead(boolean onDeadRevive, boolean onDeadProtectForBuff,
                                      boolean onDeadProtectExp, int reviveType){
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.OPEN_UI_ON_DEAD.getValue());
        int mask = 0;
        if(onDeadRevive){
            mask |= 1;
        }
        if(onDeadProtectExp){
            mask |= 1 << 2;
        }
        if(onDeadProtectForBuff){
            mask |= 1 << 3;
        }
        pw.writeInt(mask);
        pw.write(reviveType == ReviveType.ANNIVERSARY_SURPRISE_CHANCE.getValue() ? 1 : 0);
        pw.writeInt(reviveType);

        return pw.getPacket();
    }
}
