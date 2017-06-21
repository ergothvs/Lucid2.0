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

import static client.BuddyList.BuddyOperation.ADDED;
import static client.BuddyList.BuddyOperation.DELETED;

import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import client.BuddyList;
import client.BuddylistEntry;
import client.CharacterNameAndId;
import client.MapleCharacter;
import client.MapleClient;
import client.BuddyList.BuddyAddResult;
import client.BuddyList.BuddyOperation;
import handling.channel.ChannelServer;
import handling.world.World;
import net.DatabaseConnection;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext.BuddylistPacket;
import tools.packet.enums.BuddyType;

public class BuddyListHandler {

    private static final class CharacterIdNameBuddyCapacity extends CharacterNameAndId {

        private final int buddyCapacity;

        public CharacterIdNameBuddyCapacity(int id, String name, String group, int buddyCapacity) {
            super(id, name, group);
            this.buddyCapacity = buddyCapacity;
        }

        public int getBuddyCapacity() {
            return buddyCapacity;
        }
    }

    private static CharacterIdNameBuddyCapacity getCharacterIdAndNameFromDatabase(final String name, final String group) throws SQLException {
        Connection con = DatabaseConnection.getConnection();

        CharacterIdNameBuddyCapacity ret;
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name LIKE ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                ret = null;
                if (rs.next()) {
                    if (rs.getInt("gm") < 3) {
                        ret = new CharacterIdNameBuddyCapacity(rs.getInt("id"), rs.getString("name"), group, rs.getInt("buddyCapacity"));
                    }
                }
            }
        }

        return ret;
    }

    public static final void BuddyOperation(final LittleEndianAccessor lea, final MapleClient c) {
        final int mode = lea.readByte();
        final BuddyList buddylist = c.getPlayer().getBuddylist();

        if (mode == 1) { // add
            final String addName = lea.readMapleAsciiString();
            final String groupName = lea.readMapleAsciiString();
            final BuddylistEntry ble = buddylist.get(addName);

            if (addName.length() > 13 || groupName.length() > 16) {
                return;
            }
            if (ble != null && (ble.getGroup().equals(groupName) || !ble.isVisible())) {
                c.getSession().write(BuddylistPacket.buddylistMessage((byte) BuddyType.FULL_BUDDYLIST.getValue()));//11
            } else if (ble != null && ble.isVisible()) {
                ble.setGroup(groupName);
                c.getSession().write(BuddylistPacket.updateBuddylist(buddylist.getBuddies(), false, true));
            } else if (buddylist.isFull()) {
                c.getSession().write(BuddylistPacket.buddylistMessage((byte) BuddyType.FULL_BUDDYLIST.getValue()));//11
            } else {
                try {
                    CharacterIdNameBuddyCapacity charWithId = null;
                    int channel = World.Find.findChannel(addName);
                    MapleCharacter otherChar = null;
                    if (channel > 0) {
                        otherChar = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(addName);
                        if (otherChar == null) {
                            charWithId = getCharacterIdAndNameFromDatabase(addName, groupName);
                        } else if (!otherChar.isIntern() || c.getPlayer().isIntern()) {
                            charWithId = new CharacterIdNameBuddyCapacity(otherChar.getId(), otherChar.getName(), groupName, otherChar.getBuddylist().getCapacity());
                        }
                    } else {
                        charWithId = getCharacterIdAndNameFromDatabase(addName, groupName);
                    }

                    if (charWithId != null) {
                        BuddyAddResult buddyAddResult = null;
                        if (channel > 0) {
                            buddyAddResult = World.Buddy.requestBuddyAdd(addName, c.getChannel(), c.getPlayer().getId(),
                                    c.getPlayer().getName(), c.getPlayer().getLevel(), c.getPlayer().getJob(), groupName);
                        } else {
                            Connection con = DatabaseConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as buddyCount FROM buddies WHERE characterid = ? AND pending = 0");
                            ps.setInt(1, charWithId.getId());
                            ResultSet rs = ps.executeQuery();

                            if (!rs.next()) {
                                ps.close();
                                rs.close();
                                throw new RuntimeException("Result set expected");
                            } else {
                                int count = rs.getInt("buddyCount");
                                if (count >= charWithId.getBuddyCapacity()) {
                                    buddyAddResult = BuddyAddResult.BUDDYLIST_FULL;
                                }
                            }
                            rs.close();
                            ps.close();

                            ps = con.prepareStatement("SELECT pending FROM buddies WHERE characterid = ? AND buddyid = ?");
                            ps.setInt(1, charWithId.getId());
                            ps.setInt(2, c.getPlayer().getId());
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                buddyAddResult = BuddyAddResult.ALREADY_ON_LIST;
                            }
                            rs.close();
                            ps.close();
                        }
                        if (buddyAddResult == BuddyAddResult.BUDDYLIST_FULL) {
                            c.getSession().write(BuddylistPacket.buddylistMessage((byte) BuddyType.FULL_BUDDYLIST.getValue()));
                        } else {
                            int displayChannel = -1;
                            int otherCid = charWithId.getId();
                            if (buddyAddResult == BuddyAddResult.ALREADY_ON_LIST && channel > 0) {
                                displayChannel = channel;
                                notifyRemoteChannel(c, channel, otherCid, groupName, ADDED);
                            } else if (buddyAddResult != BuddyAddResult.ALREADY_ON_LIST) {
                                Connection con = DatabaseConnection.getConnection();
                                try (PreparedStatement ps = con.prepareStatement("INSERT INTO buddies (`characterid`, `buddyid`, `groupname`, `pending`) VALUES (?, ?, ?, 1)")) {
                                    ps.setInt(1, charWithId.getId());
                                    ps.setInt(2, c.getPlayer().getId());
                                    ps.setString(3, groupName);
                                    ps.executeUpdate();
                                }
                            }
                            buddylist.put(new BuddylistEntry(charWithId.getName(), otherCid, groupName, displayChannel, true));
                            c.getSession().write(BuddylistPacket.requestMessage(charWithId.getName()));
                            c.getSession().write(BuddylistPacket.updateBuddylist(buddylist.getBuddies(), false, true));
                        }
                    } else {
                        c.getSession().write(BuddylistPacket.buddylistMessage((byte) BuddyType.CHAR_NOT_REGISTERED.getValue()));//was15
                    }
                } catch (SQLException e) {
                    System.err.println("SQL THROW" + e);
                }
            }
        } else if (mode == 2) { // accept buddy
            int otherCid = lea.readInt();
            final BuddylistEntry ble = buddylist.get(otherCid);
            if (!buddylist.isFull() && ble != null && !ble.isVisible()) {
                final int channel = World.Find.findChannel(otherCid);
                buddylist.put(new BuddylistEntry(ble.getName(), otherCid, ble.getGroup(), channel, true));
                c.getSession().write(BuddylistPacket.updateBuddylist(buddylist.getBuddies(), false, true));
                notifyRemoteChannel(c, channel, otherCid, ble.getGroup(), ADDED);
            } else {
                c.getSession().write(BuddylistPacket.buddylistMessage((byte) BuddyType.FULL_BUDDYLIST.getValue()));
            }
        } else if (mode == 5) { // delete
            final int otherCid = lea.readInt();
            if(otherCid < 0) {
                c.getSession().write(BuddylistPacket.buddylistMessage((byte) BuddyType.FRIEND_NOT_FOUND.getValue()));
            }

            final BuddylistEntry blz = buddylist.get(otherCid);
            if (blz != null && blz.isVisible()) {
                notifyRemoteChannel(c, World.Find.findChannel(otherCid), otherCid, blz.getGroup(), DELETED);
            }
            buddylist.remove(otherCid);
            c.getSession().write(BuddylistPacket.updateBuddylist(buddylist.getBuddies(), true, false));
        } else if (mode == 6) { // deny request
            int otherId = lea.readInt();
            int channel = World.Find.findChannel(otherId);
            MapleCharacter mc = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterById(otherId);
            mc.getClient().getSession().write(BuddylistPacket.declinedRequest(c.getPlayer().getName()));
        } else if(mode == 11) { // convert to account buddy

        } else if (mode == 12) { // edit
            // TODO add new info
        }
    }

    private static void notifyRemoteChannel(final MapleClient c, final int remoteChannel, final int otherCid, final String group, final BuddyOperation operation) {
        final MapleCharacter player = c.getPlayer();

        if (remoteChannel > 0) {
            World.Buddy.buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation, group);
        }
    }
}
