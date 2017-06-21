package server.movement;

import java.awt.Point;

import tools.data.LittleEndianAccessor;
import tools.data.PacketWriter;

public class Movement3 extends AbstractLifeMovement  {

	public Movement3(LittleEndianAccessor lea, byte command) {
		super();
		this.command = command;
		this.position = new Point(0, 0);
		
		short xv = lea.readShort();
		short xy = lea.readShort();
		vposition = new Point(xv, xy);
		
		if (command == 21 || command == 22)
			fhFootStart = lea.readShort();
		
		bMoveAction = lea.readByte();
		tElapse = lea.readShort();
		bForcedStop = lea.readByte();
	}
	
	@Override
	public void serialize(PacketWriter lew) {
		lew.write(getCommand());
		lew.writePos(getVPosition());
		if (getCommand() == 21 || getCommand() == 22) {
			lew.writeShort(getFhFootStart());
		}
		lew.write(getMoveAction());
		lew.writeShort(getDuration());
		lew.write(getForcedStop());
	}

}
