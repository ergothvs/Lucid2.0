package server.movement;

import java.awt.Point;

import tools.data.LittleEndianAccessor;
import tools.data.PacketWriter;

public class Movement6 extends AbstractLifeMovement {

	public Movement6(LittleEndianAccessor lea, byte command) {
		super();
		this.command = command;
		this.position = new Point(0, 0);
		
		short xv = lea.readShort();
		short xy = lea.readShort();
		vposition = new Point(xv, xy);
		
		// I'm not sure about this, it' needs testing.
		fhFootStart = lea.readShort();
		
		bMoveAction = lea.readByte();
		tElapse = lea.readShort();
		bForcedStop = lea.readByte();
	}
	
	@Override
	public void serialize(PacketWriter lew) {
		lew.write(getCommand());
		lew.writePos(getVPosition());
		lew.writeShort(getFhFootStart());
		lew.write(getMoveAction());
		lew.writeShort(getDuration());
		lew.write(getForcedStop());
	}

}
