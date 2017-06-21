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

import client.MapleCharacter;
import handling.SendPacketOpcode;
import server.carnival.MapleCarnivalParty;

import java.util.List;

import tools.data.PacketWriter;

public class MonsterCarnivalPacket {

    public static byte[] startMonsterCarnival(final MapleCharacter chr, final int enemyavailable, final int enemytotal) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_START.getValue());
        final MapleCarnivalParty friendly = chr.getCarnivalParty();
        pw.write(friendly.getTeam());
        pw.writeInt(chr.getAvailableCP());
        pw.writeInt(chr.getTotalCP());
        pw.writeInt(friendly.getAvailableCP()); // ??
        pw.writeInt(friendly.getTotalCP()); // ??
        pw.write(0); // ??

        return pw.getPacket();
    }

    public static byte[] playerDiedMessage(String name, int lostCP, int team) { //CPQ
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_DIED.getValue());
        pw.write(team); //team
        pw.writeMapleAsciiString(name);
        pw.write(lostCP);

        return pw.getPacket();
    }

    public static byte[] playerLeaveMessage(boolean leader, String name, int team) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_LEAVE.getValue());
        pw.write(leader ? 7 : 0);
        pw.write(team); // 0: red, 1: blue
        pw.writeMapleAsciiString(name);

        return pw.getPacket();
    }

    public static byte[] CPUpdate(boolean party, int curCP, int totalCP, int team) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_OBTAINED_CP.getValue());
        pw.writeInt(curCP);
        pw.writeInt(totalCP);

        return pw.getPacket();
    }

    public static byte[] showMCStats(int left, int right) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_STATS.getValue());
        pw.writeInt(left);
        pw.writeInt(right);

        return pw.getPacket();
    }

    public static byte[] playerSummoned(String name, int tab, int number) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_SUMMON.getValue());
        pw.write(tab);
        pw.write(number);
        pw.writeMapleAsciiString(name);

        return pw.getPacket();
    }

    public static byte[] showMCResult(int mode) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_RESULT.getValue());
        pw.write(mode);

        return pw.getPacket();
    }

    public static byte[] showMCRanking(List<MapleCharacter> players) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_RANKING.getValue());
        pw.writeShort(players.size());
        for (MapleCharacter i : players) {
            pw.writeInt(i.getId());
            pw.writeMapleAsciiString(i.getName());
            pw.writeInt(10); // points
            pw.write(0); // team
        }

        return pw.getPacket();
    }

    public static byte[] startCPQ(byte team, int usedcp, int totalcp) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_START.getValue());
        pw.write(0); //team
        pw.writeShort(0); //Obtained CP - Used CP
        pw.writeShort(0); //Total Obtained CP
        pw.writeShort(0); //Obtained CP - Used CP of the team
        pw.writeShort(0); //Total Obtained CP of the team
        pw.writeShort(0); //Obtained CP - Used CP of the team
        pw.writeShort(0); //Total Obtained CP of the team
        pw.writeShort(0); //Probably useless nexon shit
        pw.writeLong(0); //Probably useless nexon shit
        return pw.getPacket();
    }

    public static byte[] obtainCP() {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_OBTAINED_CP.getValue());
        pw.writeShort(0); //Obtained CP - Used CP
        pw.writeShort(0); //Total Obtained CP
        return pw.getPacket();
    }

    public static byte[] obtainPartyCP() {
        PacketWriter pw = new PacketWriter();
        //pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_PARTY_CP.getValue());
        pw.write(0); //Team where the points are given to.
        pw.writeShort(0); //Obtained CP - Used CP
        pw.writeShort(0); //Total Obtained CP
        return pw.getPacket();
    }

    public static byte[] CPQSummon() {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_SUMMON.getValue());
        pw.write(0); //Tab
        pw.write(0); //Number of summon inside the tab
        pw.writeMapleAsciiString(""); //Name of the player that summons
        return pw.getPacket();
    }

    public static byte[] CPQDied() {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_SUMMON.getValue());
        pw.write(0); //Team
        pw.writeMapleAsciiString(""); //Name of the player that died
        pw.write(0); //Lost CP
        return pw.getPacket();
    }

    /**
     * Sends a CPQ Message
     *
     * Possible values for <code>message</code>:<br>
     * 1: You don't have enough CP to continue. 2: You can no longer summon the
     * Monster. 3: You can no longer summon the being. 4: This being is already
     * summoned. 5: This request has failed due to an unknown error.
     *
     * @param message Displays a message inside Carnival PQ
     * @return 
     *
     */
    public static byte[] CPQMessage(byte message) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_MESSAGE.getValue());
        pw.write(message); //Message
        return pw.getPacket();
    }

    public static byte[] leaveCPQ() {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.MONSTER_CARNIVAL_LEAVE.getValue());
        pw.write(0); // Something?
        pw.write(0); // Team
        pw.writeMapleAsciiString(""); //Player name
        return pw.getPacket();
    }
}
