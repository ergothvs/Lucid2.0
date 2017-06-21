/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.movement;

import java.awt.Point;

import tools.data.LittleEndianAccessor;
import tools.data.PacketWriter;

public class Movement1 extends AbstractLifeMovement {

    public Movement1(LittleEndianAccessor lea, byte command) {
        super();
        this.command = command;
        
        short x = lea.readShort();
        short y = lea.readShort();
        position = new Point(x, y);
        
        short xv = lea.readShort();
        short yv = lea.readShort();
        vposition = new Point(xv, yv);
        
        fh = lea.readShort();
        
        if (command == 15 || command == 17)
        	fhFootStart = lea.readShort();
        
        short xoffset = lea.readShort();
        short yoffset = lea.readShort();
        offset = new Point(xoffset, yoffset);
        
        bMoveAction = lea.readByte();
        tElapse = lea.readShort();
        bForcedStop = lea.readByte();
    }

    @Override
    public void serialize(PacketWriter lew) {
        lew.write(getCommand());
        lew.writePos(getPosition());
        lew.writePos(getVPosition());
        lew.writeShort(getFh());
        if (getCommand() == 15 || getCommand() == 17) {
        	lew.writeShort(getFhFootStart());
        }
        lew.writePos(getOffset());
        lew.write(getMoveAction());
        lew.writeShort(getDuration());
        lew.write(getForcedStop());
    }
    
}
