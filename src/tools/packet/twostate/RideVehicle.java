package tools.packet.twostate;

import client.CharacterTemporaryStat;
import client.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import tools.data.PacketWriter;

public class RideVehicle extends TemporaryStat {

	@Override
	public void encode(PacketWriter pw, MapleCharacter chr) {
		
		int mount = 0;
		int nReason = chr.getBuffSource(CharacterTemporaryStat.RideVehicle);
		
		Item b_mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -118);
		Item c_mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18);
		
		if ((GameConstants.getMountItem(nReason, chr) == 0) && (b_mount != null)
				&& (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -119) != null)) {
			mount = b_mount.getItemId();
		} else if ((GameConstants.getMountItem(nReason, chr) == 0) && (c_mount != null)
				&& (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -19) != null)) {
			mount = c_mount.getItemId();
		} else {
			mount = GameConstants.getMountItem(nReason, chr);
		}
		
		if (nReason < 0)
			nReason = 0;
		
		encode(pw, chr, mount, nReason);
		time(pw, true, Integer.MAX_VALUE);

	}
}
