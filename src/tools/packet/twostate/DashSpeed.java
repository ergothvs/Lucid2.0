package tools.packet.twostate;

import client.CharacterTemporaryStat;
import client.MapleCharacter;
import tools.data.PacketWriter;

public class DashSpeed extends TemporaryStat {
	
	private final CharacterTemporaryStat nBuff = CharacterTemporaryStat.Dash_Speed;

	@Override
	public void encode(PacketWriter pw, MapleCharacter chr) {
		
		int nValue = 0;
		int nReason = 0;
		
		if (chr.getBuffedValue(nBuff) != null)
			nValue = chr.getBuffedValue(nBuff);
		
		if (chr.getBuffSource(nBuff) > -1)
			nReason = chr.getBuffSource(nBuff);
		
		encode(pw, chr, nValue, nReason);
		time(pw, true, Integer.MAX_VALUE);
		
		pw.writeShort(0); // usExpireTerm
	}
}
