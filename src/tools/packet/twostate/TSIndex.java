package tools.packet.twostate;

import java.util.Arrays;

import client.MapleCharacter;
import tools.data.PacketWriter;

public abstract class TSIndex {

	private static final TemporaryStat[] encoders = {
			new EnergyCharged(),
			new DashSpeed(),
			new DashJump(),
			new RideVehicle(),
			new PartyBooster(),
			new GuidedBullet(),
			new Undead(),
			new RideVehicleExpire()
		};
	
	public static void encodeAll(PacketWriter pw, MapleCharacter chr) {
		Arrays.stream(encoders).forEachOrdered(stat -> stat.encode(pw, chr));
	}

}
