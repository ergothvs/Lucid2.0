package server.commands;

import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import client.BuddylistEntry;
import client.inventory.*;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import constants.EventConstants;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.SendPacketOpcode;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.world.CharacterTransfer;
import handling.world.MapleMessengerCharacter;
import handling.world.PlayerBuffStorage;
import handling.world.World;
import net.DatabaseConnection;
import script.npc.NPCTalk;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.ShutdownServer;
import server.Timer.EventTimer;
import server.Timer.WorldTimer;
import tools.HexTool;
import tools.Randomizer;
import tools.StringUtil;
import tools.data.PacketWriter;
import tools.packet.CField;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;
import tools.packet.PetPacket;
import tools.packet.enums.BuddyType;

public class AdminCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.ADMIN;
    }

    public static class UpdatePet extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MaplePet pet = c.getPlayer().getPet(0);
            if (pet == null) {
                return 0;
            }
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PetPacket.petColor(c.getPlayer().getId(), (byte) 0, Color.yellow.getAlpha()), true);
            return 1;
        }
    }

    public static class DamageBuff extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            SkillFactory.getSkill(9101003).getEffect(1).applyTo(c.getPlayer());
            return 1;
        }
    }

    public static class MagicWheel extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            List<Integer> items = new LinkedList<Integer>();
            for (int i = 1; i <= 10; i++) {
                try {
                    items.add(Integer.parseInt(splitted[i]));
                } catch (NumberFormatException ex) {
                    items.add(GameConstants.eventRareReward[GameConstants.eventRareReward.length]);
                }
            }
            int end = Randomizer.nextInt(10);
            String data = "Magic Wheel";
            c.getPlayer().setWheelItem(items.get(end));
            c.getSession().write(CWvsContext.magicWheel((byte) 3, items, data, end));
            return 1;
        }
    }

    public static class UnsealItem extends CommandExecute {

        @Override
        public int execute(final MapleClient c, String[] splitted) {
            short slot = Short.parseShort(splitted[1]);
            Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
            if (item == null) {
                return 0;
            }
            final int itemId = item.getItemId();
            Integer[] itemArray = {1002140, 1302000, 1302001,
                1302002, 1302003, 1302004, 1302005, 1302006,
                1302007};
            final List<Integer> items = Arrays.asList(itemArray);
            c.getSession().write(CField.sendSealedBox(slot, 2028162, items)); //sealed box
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            WorldTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);
                    Item item = ii.getEquipById(items.get(Randomizer.nextInt(items.size())));
                    MapleInventoryManipulator.addbyItem(c, item);
                    c.getSession().write(CField.unsealBox(item.getItemId()));
                    c.getSession().write(CField.EffectPacket.showRewardItemAnimation(2028162, "")); //sealed box
                }
            }, 10000);
            return 1;
        }
    }

    public static class TestBuddy extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            PacketWriter pw = new PacketWriter();

//            pw.write(HexTool.getByteArrayFromHexString(splitted[1]));
            MapleCharacter chr = c.getPlayer();
            BuddylistEntry ble = new BuddylistEntry(chr.getName(), chr.getId(), "Testing yo", 1,
                    true, 0, chr.getAccountID(), "God", "Baas", false);
//            c.getSession().write(CWvsContext.BuddylistPacket.requestBuddylistAddTest(chr.getId(), chr.getName(), chr.getLevel(), chr.getJob(), Byte.valueOf(splitted[1])));
            c.getSession().write(CWvsContext.BuddylistPacket.requestBuddyAdd(false, chr.getId(),
                    chr.getAccountID(), chr.getName(), chr.getLevel(), chr.getJob(), chr.getSubcategory(), ble));

            return 1;
        }
    }

    public static class TestUpdate extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getSession().write(CWvsContext.BuddylistPacket.updateBuddylist(c.getPlayer().getBuddylist().getBuddies()));
            return 1;
        }
    }

    public static class CutScene extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getSession().write(NPCPacket.getCutSceneSkip());
            return 1;
        }
    }

    public static class DemonJob extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getSession().write(NPCPacket.getDemonSelection());
            return 1;
        }
    }

    public static class NearestPortal extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MaplePortal portal = c.getPlayer().getMap().findClosestPortal(c.getPlayer().getTruePosition());
            c.getPlayer().dropMessage(6, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());

            return 1;
        }
    }

    public static class Uptime extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, "Server has been up for " + StringUtil.getReadableMillis(ChannelServer.serverStartTime, System.currentTimeMillis()));
            return 1;
        }
    }

    public static class Reward extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            chr.addReward(Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5]), Integer.parseInt(splitted[6]), StringUtil.joinStringFrom(splitted, 7));
            chr.updateReward();
            return 1;
        }
    }

    public static class GMPerson extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]).setGM(Byte.parseByte(splitted[2]));
            return 1;
        }
    }
    
    public static class DoubleTime extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            EventConstants.DoubleTime = !EventConstants.DoubleTime;
            //if (EventConstants.DoubleMiracleTime) {
            World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(4, "It's Miracle Time!  Between 2:00 PM and 4:00 PM (Pacific) today, Miracle, Premium, Revolutionary Miracle, Super Miracle, Enlightening Miracle and Carved Slot Miracle Cubes have increased chances to raise your item to the next potential tier!"));
            //}
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.dropMessage(0, "Double Time Event has " + (EventConstants.DoubleTime ? "began!" : "ended"));
                }
            }
            return 1;
        }
    }

    public static class DoubleMiracleTime extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            EventConstants.DoubleMiracleTime = !EventConstants.DoubleMiracleTime;
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.dropMessage(0, "Double Miracle Time Event has " + (EventConstants.DoubleMiracleTime ? "began!" : "ended"));
                }
            }
            return 1;
        }
    }

    public static class WarpCashShop extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            MapleClient client = chr.getClient();
            final ChannelServer ch = ChannelServer.getInstance(client.getChannel());

            chr.changeRemoval();

            if (chr.getMessenger() != null) {
                MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
                World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
            }
            PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
            PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
            PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), -10);
            ch.removePlayer(chr);
            client.updateLoginState(MapleClient.CHANGE_CHANNEL, client.getSessionIPAddress());
            chr.saveToDB(false, false);
            chr.getMap().removePlayer(chr);
            client.getSession().write(CField.getChannelChange(client, Integer.parseInt(CashShopServer.getIP().split(":")[1])));
            client.setPlayer(null);
            client.setReceiving(false);
            return 1;
        }
    }

    public static class TestDirection extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getSession().write(CField.UIPacket.getDirectionInfo(StringUtil.joinStringFrom(splitted, 5), Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5])));
            return 1;
        }
    }

    public static class Packet extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getSession().write(HexTool.getByteArrayFromHexString(StringUtil.joinStringFrom(splitted, 1)));
            return 1;
        }
    }

    public static class StripEveryone extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            ChannelServer cs = c.getChannelServer();
            for (MapleCharacter mchr : cs.getPlayerStorage().getAllCharacters()) {
                if (c.getPlayer().isGM()) {
                    continue;
                }
                MapleInventory equipped = mchr.getInventory(MapleInventoryType.EQUIPPED);
                MapleInventory equip = mchr.getInventory(MapleInventoryType.EQUIP);
                List<Short> ids = new ArrayList<>();
                for (Item item : equipped.newList()) {
                    ids.add(item.getPosition());
                }
                for (short id : ids) {
                    MapleInventoryManipulator.unequip(mchr.getClient(), id, equip.getNextFreeSlot());
                }
            }
            return 1;
        }
    }

    public static class Strip extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            MapleInventory equipped = victim.getInventory(MapleInventoryType.EQUIPPED);
            MapleInventory equip = victim.getInventory(MapleInventoryType.EQUIP);
            List<Short> ids = new ArrayList<>();
            for (Item item : equipped.newList()) {
                ids.add(item.getPosition());
            }
            for (short id : ids) {
                MapleInventoryManipulator.unequip(victim.getClient(), id, equip.getNextFreeSlot());
            }
            boolean notice = false;
            if (splitted.length > 1) {
                notice = true;
            }
            if (notice) {
                World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(0, victim.getName() + " has been stripped by " + c.getPlayer().getName()));
            }
            return 1;
        }
    }

    public static class MesoEveryone extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.gainMeso(Integer.parseInt(splitted[1]), true);
                }
            }
            return 1;
        }
    }

    public static class ScheduleHotTime extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 1) {
                c.getPlayer().dropMessage(0, "!ScheduleHotTime <Item Id>");
                return 0;
            }
            if (!MapleItemInformationProvider.getInstance().itemExists(Integer.parseInt(splitted[1]))) {
                c.getPlayer().dropMessage(0, "Item does not exists.");
                return 0;
            }
            NPCTalk talk = new NPCTalk((byte) 4, 9010010, (byte) 0);
            talk.setParam((byte) 1);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    if (c.canClickNPC()) {
                    	talk.setText("You got the #t" + Integer.parseInt(splitted[1]) + "#, right? Click it to see what's inside. Go ahead and check your item inventory now, if you're curious.");
                        
                        mch.gainItem(Integer.parseInt(splitted[1]), 1);
                        mch.getClient().getSession().write(CField.NPCPacket.getNPCTalk(talk));
                    }
                }
            }
            System.out.println("Hot Time had been scheduled successfully.");
            return 1;
        }
    }

    public static class WarpAllHere extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (mch.getMapId() != c.getPlayer().getMapId()) {
                    mch.changeMap(c.getPlayer().getMap(), c.getPlayer().getPosition());
                }
            }
            return 1;
        }
    }

    public static class DCAll extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int range = -1;
            switch (splitted[1]) {
                case "m":
                    range = 0;
                    break;
                case "c":
                    range = 1;
                    break;
                case "w":
                    range = 2;
                    break;
            }
            if (range == -1) {
                range = 1;
            }
            if (range == 0) {
                c.getPlayer().getMap().disconnectAll();
            } else if (range == 1) {
                c.getChannelServer().getPlayerStorage().disconnectAll(true);
            } else if (range == 2) {
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    cserv.getPlayerStorage().disconnectAll(true);
                }
            }
            return 1;
        }
    }

    public static class Shutdown extends CommandExecute {

        protected static Thread t = null;

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, "Shutting down...");
            if (t == null || !t.isAlive()) {
                t = new Thread(ShutdownServer.getInstance());
                ShutdownServer.getInstance().shutdown();
                t.start();
            } else {
                c.getPlayer().dropMessage(6, "A shutdown thread is already in progress or shutdown has not been done. Please wait.");
            }
            return 1;
        }
    }

    public static class ShutdownTime extends Shutdown {

        private static ScheduledFuture<?> ts = null;
        private int minutesLeft = 0;

        @Override
        public int execute(MapleClient c, String[] splitted) {
            minutesLeft = Integer.parseInt(splitted[1]);
            c.getPlayer().dropMessage(6, "Shutting down... in " + minutesLeft + " minutes");
            if (ts == null && (t == null || !t.isAlive())) {
                t = new Thread(ShutdownServer.getInstance());
                ts = EventTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        if (minutesLeft == 0) {
                            ShutdownServer.getInstance().shutdown();
                            t.start();
                            ts.cancel(false);
                            return;
                        }
                        World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(0, "The server will shutdown in " + minutesLeft + " minutes. Please log off safely."));
                        minutesLeft--;
                    }
                }, 60000);
            } else {
                c.getPlayer().dropMessage(6, "A shutdown thread is already in progress or shutdown has not been done. Please wait.");
            }
            return 1;
        }
    }

    public static class Sql extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            try {
                Connection con = (Connection) DatabaseConnection.getConnection();
                PreparedStatement ps = (PreparedStatement) con.prepareStatement(StringUtil.joinStringFrom(splitted, 1));
                ps.executeUpdate();
            } catch (SQLException e) {
                c.getPlayer().dropMessage(0, "Failed to execute SQL command.");
                return 0;
            }
            return 1;
        }
    }
}
