package tools.packet.twostate;

import client.CharacterTemporaryStat;
import client.MapleCharacter;
import tools.data.PacketWriter;

public class PartyBooster extends TemporaryStat {
	
	private final CharacterTemporaryStat nBuff = CharacterTemporaryStat.PartyBooster;
	
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
		time(pw, false, 1977283546);
		
		pw.writeShort(0); // usExpireTerm
	}

}
