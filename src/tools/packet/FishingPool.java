package tools.packet;

import handling.SendPacketOpcode;
import tools.data.PacketWriter;

public class FishingPool {

	/**
	 * <0> close the gui.
	 * <1> open the gui.
	 * <2> show an explanation mark above the user's head.
	 * <3> show a <gotcha> message above the user's head.
	 * <4> show a <got away...> message above the user's head.
	 * <5> does nothing?
	 * @param mode
	 * @return
	 */
	public static byte[] getFishingInformation(int mode) {
		PacketWriter pw = new PacketWriter();
		pw.writeShort(SendPacketOpcode.FISHING_INFO.getValue());
		pw.writeInt(mode);
		switch(mode) {
			case 2:
				pw.writeDouble(50); // distance
				pw.write(1); // state (1 draw, 2 update)?
				pw.writeDouble(10); // fDamageMin
				pw.writeDouble(10); // fDamageMax
				pw.writeInt(0); // GaugeMove
				
				// FishTemplate::Decode
				pw.writeDouble(100); // distance max
				pw.writeInt(0); // move count
				
				// pw.writeInt(0); // ?
				// pw.writeInt(0); // ?
				// pw.writeInt(0); // ?
				// pw.writeDouble(10.0);
				// pw.writeDouble(50.0);
				break;
			default: break;
		}
		return pw.getPacket();
	}
	
	/**
	 * Show the item that was caught, above the player's head.
	 * @param cid
	 * @param itemid 
	 * @return
	 */
	public static byte[] getFishingReward(int cid, int itemid) {
		PacketWriter pw = new PacketWriter();
		pw.writeShort(SendPacketOpcode.FISHING_REWARD.getValue());
		pw.writeInt(cid);
		pw.writeInt(itemid);
		return pw.getPacket();
	}
	
	/**
	 * Sends an error <message> to user.
	 * <message> You can't do that. Someone is already 
	 * fishing there. Max: <code>amount</code> people.
	 * @param amount
	 * @return
	 */
	public static byte[] getFishingZoneInfo(int amount) {
		PacketWriter pw = new PacketWriter();
		pw.writeShort(SendPacketOpcode.FISHING_ZONE_INFO.getValue());
		pw.writeInt(amount);
		return pw.getPacket();
	}

}
