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
package tools.packet;

import java.awt.Point;
import java.util.List;

import client.MapleCharacter;
import client.MapleStat;
import client.inventory.Item;
import client.inventory.MaplePet;
import handling.SendPacketOpcode;
import server.movement.LifeMovementFragment;
import tools.data.PacketWriter;

public class PetPacket {

    public static final byte[] updatePet(MaplePet pet, Item item, boolean active) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
        pw.writeInt(0);
        pw.write(2);
        pw.write(0);//new141
        pw.write(3);
        pw.write(5);
        pw.writeShort(pet.getInventoryPosition());
        pw.write(0);
        pw.write(5);
        pw.writeShort(pet.getInventoryPosition());
        pw.write(3);
        pw.writeInt(pet.getPetItemId());
        pw.write(1);
        pw.writeLong(pet.getUniqueId());
        PacketHelper.addPetItemInfo(pw, item, pet, active);
        return pw.getPacket();
    }
    public static final byte[] addPetData(MapleCharacter chr, MaplePet pet) {
        PacketWriter pw = new PacketWriter();
        
        pw.writeLong(pet.getPetItemId());
        pw.writeMapleAsciiString(pet.getName());
        pw.writeLong(pet.getUniqueId());
        pw.writeShort(pet.getPos().x);
        pw.writeShort(pet.getPos().y - 20);
        pw.write(pet.getStance());
//            pw.writeShort(1);//new 141
        pw.writeShort(pet.getFh());
        pw.writeInt(-1); // nPetHue
//        pw.writeInt(100);//new 141
        pw.writeShort(0); // nGiantRate
        pw.write(0); // bTransform
        pw.write(0); // bReinforced
        
        return pw.getPacket();
    }
    public static final byte[] showPet(MapleCharacter chr, MaplePet pet, boolean remove, boolean hunger) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SPAWN_PET.getValue());
        pw.writeInt(chr.getId());
        pw.write(chr.getPetIndex(pet));
        if (remove) {
            pw.writeInt(0);
            pw.write(hunger ? 1 : 0); // int now?
        } else {
        	pw.writeShort(0);
        	pw.write(0);
            pw.write(1);
            pw.write(1);//was 0
            pw.writeInt(pet.getPetItemId());
            pw.writeMapleAsciiString(pet.getName());
            pw.writeLong(pet.getUniqueId());
            pw.writeShort(pet.getPos().x);
            pw.writeShort(pet.getPos().y - 20);
            pw.write(pet.getStance());
//            pw.writeShort(1);//new 141
            pw.writeShort(pet.getFh());
            pw.writeInt(-1);
            pw.writeInt(100);//new 141
        }

        return pw.getPacket();
    }

    public static final byte[] removePet(int cid, int index) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SPAWN_PET.getValue());
        pw.writeInt(cid);
        pw.write(index);
        pw.writeShort(0);

        return pw.getPacket();
    }

    public static byte[] movePet(int cid, int pid, byte slot, Point pos, List<LifeMovementFragment> moves) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MOVE_PET.getValue());
        pw.writeInt(cid);
        pw.write(slot);
        pw.writeInt(0);//new 141
        pw.writePos(pos);
        pw.writeInt(pid);
        PacketHelper.serializeMovementList(pw, moves);

        return pw.getPacket();
    }

    public static byte[] petChat(int cid, int un, String text, byte slot) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PET_CHAT.getValue());
        pw.writeInt(cid);
        
        pw.write(slot);
        pw.write(un);
        pw.write(0);
        pw.writeMapleAsciiString(text);
        pw.write(0);

        return pw.getPacket();
    }
    
    public static byte[] petColor(int cid,byte slot, int color) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PET_COLOR.getValue());
        pw.writeInt(cid);
        pw.write(slot);
        pw.writeInt(color);

        return pw.getPacket();
    }

    public static final byte[] commandResponse(int cid, byte command, byte slot, boolean success, boolean food) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PET_COMMAND.getValue());
        pw.writeInt(cid);
        pw.write(slot);
        pw.write(command == 1 ? 1 : 0);
        pw.write(command);
        pw.write(success ? 1 : command == 1 ? 0 : 0);
        pw.writeInt(0);
        pw.write(0);//new142
        pw.write(0);//new142

        return pw.getPacket();
    }

    public static final byte[] showPetLevelUp(MapleCharacter chr, byte index) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.EFFECT_REMOTE.getValue());
        pw.writeInt(chr.getId());
        pw.write(6);
        pw.write(0);
        pw.writeInt(index);

        return pw.getPacket();
    }
    
    public static final byte[] petSize(int cid, byte slot,short size) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PET_SIZE.getValue());
        pw.writeInt(cid);
        pw.write(slot);
        pw.writeShort(size);

        return pw.getPacket();
    }

    public static final byte[] showPetUpdate(MapleCharacter chr, int uniqueId, byte index) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PET_EXCEPTION_LIST.getValue());
        pw.writeInt(chr.getId());
        
        pw.write(index);
        pw.writeLong(uniqueId);
        pw.write(0);
        pw.writeInt(0);
        //pw.writeZeroBytes(50);

        return pw.getPacket();
    }

    public static byte[] petStatUpdate(MapleCharacter chr) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.STAT_CHANGED.getValue());
        pw.write(0);
        pw.writeLong(MapleStat.PET.getValue());

        byte count = 0;
        for (MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                pw.writeLong(pet.getUniqueId());
                count = (byte) (count + 1);
            }
        }
        while (count < 3) {
            pw.write(new byte[8]);
            count = (byte) (count + 1);
        }
        pw.write(0);
        pw.writeShort(0);
        pw.write(new byte[100]);

        return pw.getPacket();
    }
}
