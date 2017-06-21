package tools.packet.twostate;

import client.MapleCharacter;
import tools.Randomizer;
import tools.data.PacketWriter;

public abstract class TemporaryStat {

	private static final int rand = Randomizer.nextInt();
	
	public void encode(PacketWriter pw, MapleCharacter chr) {
		
	}
	
	public void encode(PacketWriter pw, MapleCharacter chr, int nValue, int nReason) {
		pw.writeInt(nValue);
		pw.writeInt(nReason);
	}
	
	public void time(PacketWriter pw, boolean flag, int tDuration) {
		pw.write(flag);
		pw.writeInt(tDuration);
	}
}
