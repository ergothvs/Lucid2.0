/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.channel.handler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import client.MapleCharacter;
import server.maps.AnimatedMapleMapObject;
import server.movement.*;
import tools.data.LittleEndianAccessor;

public class MovementParse {

    // 1 = player, 2 = mob, 3 = pet, 4 = summon, 5 = dragon (missing: android, familiar, haku)
    public static List<LifeMovementFragment> parseMovement(final LittleEndianAccessor lea, final int kind) {
        return parseMovement(lea, kind, null);
    }

    public static List<LifeMovementFragment> parseMovement(final LittleEndianAccessor lea, final int kind, MapleCharacter chr) {
        final List<LifeMovementFragment> res = new ArrayList<>();
        final byte numCommands = lea.readByte();
        for (byte i = 0; i < numCommands; i++) {
            byte command = lea.readByte();
            
            switch (command) {
                case 0:
                case 8:
                case 15:
                case 17:
                case 19:
                case 67:
                case 68:
                case 69: {
                    res.add(new Movement1(lea, command));
                    break;
                }
                case 56:
                case 66:
                case 85: {
                    res.add(new Movement2(lea, command));
                	break;
                }
                case 1:
                case 2:
                case 18:
                case 21:
                case 22:
                case 24:
                case 62:
                case 63:
                case 64:
                case 65: {
                    res.add(new Movement3(lea, command));
                    break;
                }
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 57:
                case 58: 
                case 59: 
                case 60: 
                case 70: 
                case 71:
                case 72:
                case 74: 
                case 79: 
                case 81:
                case 83: {
                    res.add(new Movement4(lea, command));
                    break;
                }
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 9:
                case 10:
                case 11:
                case 13:
                case 26:
                case 27:
                case 52:
                case 53:
                case 54:
                case 61:
                case 76:
                case 77:
                case 78:
                case 80:
                case 82: {
                    res.add(new Movement5(lea, command));
                    break;
                }
                case 14: 
                case 16: {
                    res.add(new Movement6(lea, command));
                    break;
                }
                case 23: {
                    res.add(new Movement7(lea, command));
                    break;
                }
                case 12: {
                    res.add(new Movement8(lea, command));
                    break;
                }
                default:
                	// System.out.printf("The command (%s) is unhandled. %n", command);
                	break;
            }
        }
        
        if (numCommands != res.size()) {
            return null;
        }
        return res;
    }

    public static void updatePosition(final List<LifeMovementFragment> movement, final AnimatedMapleMapObject target, final int yoffset) {
        if (movement == null) {
            return;
        }
        for (final LifeMovementFragment move : movement) {
            if (move instanceof LifeMovement) {
                final Point position = ((LifeMovement) move).getPosition();
                position.y += yoffset;
                target.setPosition(position);
                target.setStance(((LifeMovement) move).getMoveAction());
            }
        }
    }
}
