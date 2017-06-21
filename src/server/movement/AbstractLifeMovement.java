/*
 * This file is part of the OdinMS MapleStory Private Server
 * Copyright (C) 2012 Patrick Huy and Matthias Butz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.movement;

import java.awt.Point;

/**
 * 
 * @author Maxcloud
 */
public abstract class AbstractLifeMovement implements LifeMovement {

	protected byte command;
	protected byte bMoveAction;
	protected byte bForcedStop;
	protected byte bStat;
	
	protected short fh;
	protected short fhFootStart;
	protected short tElapse;   
    
	protected Point position;
	protected Point vposition;
	protected Point offset;
    
    @Override
    public byte getCommand() {
    	return command;
    }
    
    public void setCommand(byte command) {
    	this.command = command;
    }
    
    @Override
    public byte getMoveAction() {
    	return bMoveAction;
    }

    @Override
    public byte getForcedStop() {
    	return bForcedStop;
    }

    @Override
    public byte getBStat() {
    	return bStat;
    }
    
    @Override
    public short getFh() {
    	return fh;
    }
    
    @Override
    public short getFhFootStart() {
    	return fhFootStart;
    }

    @Override
    public short getDuration() {
    	return tElapse;
    }

    @Override
    public Point getPosition() {
    	return position;
    }
    
    @Override
    public Point getVPosition() {
    	return vposition;
    }

    @Override
    public Point getOffset() {
    	return offset;
    }
    
}
