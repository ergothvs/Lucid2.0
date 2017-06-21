package server.movement;

import java.awt.Point;

import tools.data.LittleEndianAccessor;
import tools.data.PacketWriter;

public class Movement7 extends AbstractLifeMovement {

	public Movement7(LittleEndianAccessor lea, byte command) {
		super();
		this.command = command;
		
		short x = lea.readShort();
		short y = lea.readShort();
		position = new Point(x, y);
		
		short vx = lea.readShort();
		short vy = lea.readShort();
		vposition = new Point(vx, vy);
		
		bMoveAction = lea.readByte();
		tElapse = lea.readShort();
		bForcedStop = lea.readByte();
	}

	@Override
	public void serialize(PacketWriter lew) {
		lew.write(getCommand());
		lew.writePos(getPosition());
		lew.writePos(getVPosition());
		lew.write(getMoveAction());
		lew.writeShort(getDuration());
		lew.write(getForcedStop());
	}

}
