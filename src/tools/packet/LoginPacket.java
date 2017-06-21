package tools.packet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import client.MapleCharacter;
import client.MapleClient;
import client.PartTimeJob;
import constants.GameConstants;
import constants.JobConstants;
import constants.JobConstants.LoginJob;
import constants.ServerConstants;
import constants.WorldConstants.WorldOption;
import handling.SendPacketOpcode;
import handling.login.LoginServer;
import tools.Randomizer;
import tools.data.PacketWriter;

public class LoginPacket {

    public static byte[] getHello(short mapleVersion, byte[] sendIv, byte[] recvIv) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.CLIENT_HELLO.getValue());
        pw.writeShort(mapleVersion);
        pw.writeMapleAsciiString(ServerConstants.MAPLE_PATCH);
        pw.write(recvIv);
        pw.write(sendIv);
        pw.write(8);
        pw.write(0);
        return pw.getPacket();
    }

    public static final byte[] getPing() {
        PacketWriter pw = new PacketWriter(2);
        pw.writeShort(SendPacketOpcode.PING.getValue());
        return pw.getPacket();
    }
    
    public static final byte[] getStart() {
    	PacketWriter pw = new PacketWriter();
    	pw.writeShort(SendPacketOpcode.CLIENT_START.getValue());
    	pw.write(1);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.writeInt(0);
    	return pw.getPacket();
    }
    
    public static byte[] useAuthSever() {
    	PacketWriter pw = new PacketWriter();
    	pw.writeShort(SendPacketOpcode.AUTHSERVER.getValue());
    	pw.write(0); // disable the auth server check
    	return pw.getPacket();
    }

    public static byte[] getAuthSuccessRequest(MapleClient client) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        pw.write(0);
        pw.write(0);
        pw.writeInt(0);
        pw.writeMapleAsciiString(client.getAccountName()); // 174.1
        pw.writeInt(client.getAccID());
        pw.write(0);
        pw.write(0);
        pw.writeInt(2);
        pw.writeInt(0);
        pw.write(1); // if 0, writeMapleAsciiString(CensoredNxLoginID)
        // pw.write0(7); //gm stuff and new int
        pw.writeMapleAsciiString(client.getAccountName());
        pw.write(3);
        pw.write(0);
        pw.writeLong(0);
        pw.writeLong(0);

        pw.writeInt(0x1A); // The amount of characters available in total - 3?

        getAvailableJobs(pw);
        
        pw.write(0); // 174.1
        pw.writeInt(-1); // 174.1
        pw.write(1);
        pw.write(1);

        // The date the account was created.
        pw.writeLong(0);

        return pw.getPacket();
    }
    
    public static final byte[] sendAllowedCreation() {
    	PacketWriter pw = new PacketWriter();
    	pw.writeShort(SendPacketOpcode.ALLOWED_CREATION.getValue());
    	getAvailableJobs(pw);
    	return pw.getPacket();
    }
    
    private static void getAvailableJobs(PacketWriter pw) {
    	pw.write(JobConstants.enableJobs ? 1 : 0); //toggle
        pw.write(JobConstants.jobOrder); // Job Order (orders are located in lib)
        for (LoginJob j : LoginJob.values()) {
            pw.write(j.getFlag());
            pw.writeShort(j.getFlag());
        }
    }
    
    /**
     * Send a packet to confirm authentication was successful.
     * @param c
     * @return
     */
    public static final byte[] getSecondAuthSuccess(MapleClient c) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.LOGIN_SECOND.getValue());
        pw.write(0); // request
        pw.writeInt(c.getAccID());
        pw.write(0);
        pw.write(0);
        pw.writeInt(0);
        pw.writeInt(0);
        pw.write(0);
        pw.writeMapleAsciiString(c.getAccountName());
        pw.write(0);
        pw.write(0);
        pw.writeLong(0);
        pw.writeMapleAsciiString(c.getAccountName());
        pw.writeLong(Randomizer.nextLong());
        pw.writeInt(12);
        pw.writeLong(Randomizer.nextLong());
        /*for(byte b = 0; b < 3; b++) {
        	if(b == 1)
        		pw.writeInt(28);
        	pw.writeLong(Randomizer.nextLong());
        }*/
        pw.writeMapleAsciiString("");
        getAvailableJobs(pw); 
        pw.write(0);
        pw.writeInt(-1);
        return pw.getPacket();
    }

    public static final byte[] getLoginFailed(int reason) {
        PacketWriter pw = new PacketWriter(16);

        pw.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        pw.write(reason);
        pw.write(0);
        pw.writeInt(0);

        return pw.getPacket();
    }
    /*
     * location: UI.wz/Login.img/Notice/text
     * reasons:
     * useful:
     * 32 - server under maintenance check site for updates
     * 35 - your computer is running thirdy part programs close them and play again
     * 36 - due to high population char creation has been disabled
     * 43 - revision needed your ip is temporary blocked
     * 75-78 are cool for auto register
     
     */

    public static byte[] getTempBan(long timestampTill, byte reason) {
        PacketWriter pw = new PacketWriter(17);

        pw.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        pw.write(2);
        pw.write(0);
        pw.writeInt(0);
        pw.write(reason);
        pw.writeLong(timestampTill);

        return pw.getPacket();
    }

    public static final byte[] deleteCharResponse(int cid, int state) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.DELETE_CHAR_RESPONSE.getValue());
        pw.writeInt(cid);
        pw.write(state);

        return pw.getPacket();
    }

    public static byte[] secondPwError(byte mode) {
        PacketWriter pw = new PacketWriter(3);

        pw.writeShort(SendPacketOpcode.SECONDPW_ERROR.getValue());
        pw.write(0);

        return pw.getPacket();
    }

    public static byte[] sendAuthResponse(int response) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.AUTH_RESPONSE.getValue());
        pw.writeInt(response);
        return pw.getPacket();
    }

    public static byte[] enableRecommended(int world) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.ENABLE_RECOMMENDED.getValue());
        pw.writeInt(world);
        return pw.getPacket();
    }

    public static byte[] sendRecommended(int world, String message) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.SEND_RECOMMENDED.getValue());
        pw.write(message != null ? 1 : 0);
        if (message != null) {
            pw.writeInt(world);
            pw.writeMapleAsciiString(message);
        }
        return pw.getPacket();
    }

    public static byte[] getServerList(int serverId, Map<Integer, Integer> channelLoad) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        pw.write(serverId);
        String worldName = LoginServer.getTrueServerName();
        pw.writeMapleAsciiString(worldName);
        pw.write(WorldOption.getById(serverId).getFlag());
        pw.writeMapleAsciiString(LoginServer.getEventMessage());
        pw.writeShort(100);
        pw.writeShort(100);
        pw.write(0);
        int lastChannel = 1;
        Set<Integer> channels = channelLoad.keySet();
        for (int i = 30; i > 0; i--) {
            if (channels.contains(Integer.valueOf(i))) {
                lastChannel = i;
                break;
            }
        }
        pw.write(lastChannel);

        for (int i = 1; i <= lastChannel; i++) {
            int load;

            if (channels.contains(i)) {
                load = channelLoad.get(i);
            } else {
                load = 1200;
            }
            pw.writeMapleAsciiString(worldName + "-" + i);
            
            load = (int) Math.round(((load/12) + 10) * 0.60);
            pw.writeInt(load); // load -60 = 100%
            pw.write(serverId);
            pw.writeShort(i - 1);
        }
        pw.writeShort(0);
        pw.writeInt(0);
        pw.write(0);
        return pw.getPacket();
    }

    public static byte[] getEndOfServerList() {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        pw.write(0xFF);
        pw.write(0); // boolean disable cash shop and trade msg
        pw.write(0); // 174.1
        pw.write(0); // 174.1

        return pw.getPacket();
    }

    public static byte[] getServerStatus(int status) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.SERVERSTATUS.getValue());
        pw.write(status);
        pw.write(0);
        return pw.getPacket();
    }

    public static byte[] getCharList(MapleClient client, List<MapleCharacter> characters) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.CHARLIST.getValue());
        pw.write(0); // nDay
        pw.writeMapleAsciiString("normal"); // 174.1
        pw.writeInt(0); // 174.1 ref count?

        pw.write(1); // burning event block?
        
        // character locations
        pw.writeInt(0); // ?
        
        // timestamp?
        pw.writeLong(0); // 174.1
        
        // has the list of characters been edited
        pw.write(0);
        
        //pw.writeInt(0); // ? (nSecond)

        pw.writeInt(characters.size());
        for (MapleCharacter character : characters) {
            if (!characters.isEmpty()) {
                pw.writeInt(character.getId());
            }
        }
        pw.write(characters.size());
        for (MapleCharacter character : characters) {
            addCharEntry(pw, character);
            pw.write(0);
            
            boolean ranking = !GameConstants.isGM(character.getJob()) && character.getLevel() >= 30;
            pw.write(ranking);
            if (ranking) {
                pw.writeInt(character.getRank());
                pw.writeInt(character.getRankMove());
                pw.writeInt(character.getJobRank());
                pw.writeInt(character.getJobRankMove());
            }
        }
        pw.write(client.getPicStatus());
        pw.write(0);
        pw.writeInt(client.getCharacterSlots());

        pw.writeInt(0); // buy character count?
        pw.writeInt(-1); // event new char job
        
        /*
        pw.writeInt(6111);
        
        // Something to do with time.
        pw.writeInt(0); // hidword
        pw.writeInt(0); // lodword
        
        pw.write(0); // rename count?
        
        pw.write(0); // ?
        */
        
        pw.writeReversedLong(PacketHelper.getTime(System.currentTimeMillis()));
        pw.write(0); // the amount of allowed name changes
        pw.write(new byte[5]);
        return pw.getPacket();
    }
    
    public static byte[] getWorld(int world) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(SendPacketOpcode.SELECT_WORLD.getValue());
        pw.writeInt(world);
        
        return pw.getPacket();
    }
    
    public static byte[] getAccountName(String name) {
        PacketWriter pw = new PacketWriter();
        pw.writeShort(0x111);
        pw.writeLong(0);
        pw.writeMapleAsciiString(name);
        return pw.getPacket();
    }

    public static byte[] addNewCharEntry(MapleCharacter chr, boolean worked) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.ADD_NEW_CHAR_ENTRY.getValue());
        pw.write(worked ? 0 : 1);
        addCharEntry(pw, chr);

        return pw.getPacket();
    }

    public static byte[] charNameResponse(String charname, boolean nameUsed) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.CHAR_NAME_RESPONSE.getValue());
        pw.writeMapleAsciiString(charname);
        pw.write(nameUsed ? 1 : 0);

        return pw.getPacket();
    }

    private static void addCharEntry(PacketWriter pw, MapleCharacter chr) {
        PacketHelper.addCharStats(pw, chr);
        PacketHelper.addCharLook(pw, chr, true, false);
        if (GameConstants.isZero(chr.getJob())) {
            PacketHelper.addCharLook(pw, chr, true, true);
        }
    }

    public static byte[] enableSpecialCreation(int accid, boolean enable) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.SPECIAL_CREATION.getValue());
        pw.writeInt(accid);
        pw.write(enable ? 0 : 1);
        pw.write(0);

        return pw.getPacket();
    }

    public static byte[] partTimeJob(int cid, short type, long time) {
        PacketWriter pw = new PacketWriter();

        pw.writeShort(SendPacketOpcode.PART_TIME.getValue());
        pw.writeInt(cid);
        pw.write(0);
        pw.write(type);
        //1) 0A D2 CD 01 70 59 9F EA
        //2) 0B D2 CD 01 B0 6B 9C 18
        pw.writeReversedLong(PacketHelper.getTime(time));
        pw.writeInt(0);
        pw.write(0);

        return pw.getPacket();
    }

    public static byte[] updatePartTimeJob(PartTimeJob partTime) {
        PacketWriter pw = new PacketWriter(21);
        pw.writeShort(SendPacketOpcode.PART_TIME.getValue());
        pw.writeInt(partTime.getCharacterId());
        pw.write(0);
        PacketHelper.addPartTimeJob(pw, partTime);
        return pw.getPacket();
    }
}
