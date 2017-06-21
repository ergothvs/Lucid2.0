package handling.handlers.login;

import java.util.Calendar;

import client.MapleCharacter;
import client.MapleClient;
import constants.WorldConstants.WorldOption;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.ChannelServer;
import handling.login.LoginWorker;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.LoginPacket;
import tools.packet.PacketHelper;

public class LoginPasswordHandler {

    private static boolean loginAttempt(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 3;
    }
    
    @PacketHandler(opcode = RecvPacketOpcode.LOGIN_PASSWORD)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		lea.skip(1); // 174.1
    	
    	String pwd = lea.readMapleAsciiString();
        String login = lea.readMapleAsciiString();

        login = login.replace("NP12:auth06:5:0:", "");

        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.hasBannedMac();

        int loginok = 0;
        if (pwd.equalsIgnoreCase("disconnect")) {
            for (WorldOption servers : WorldOption.values()) {
                if (servers.show() && servers.isAvailable()) {
                    for (MapleCharacter chr : c.loadCharacters(servers.getWorld())) {
                        for (ChannelServer cs : ChannelServer.getAllInstances()) {
                            MapleCharacter victim = cs.getPlayerStorage().getCharacterById(chr.getId());
                            if (victim != null) {
                                victim.getClient().getSession().close();
                                victim.getClient().disconnect(true, false);
                            }
                        }
                    }
                }
            }
            c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
            c.getSession().write(CWvsContext.broadcastMsg(1, "Your characters have been disconnected successfully."));
            c.getSession().write(LoginPacket.getLoginFailed(1)); //Shows no message, used for unstuck the login button
            return;
        } else {
            loginok = c.login(login, pwd, ipBan || macBan);
        }

        final Calendar tempbannedTill = c.getTempBanCalendar();

        if (loginok == 0 && (ipBan || macBan) && !c.isGm()) {
            loginok = 3;
            if (macBan) {
                // this is only an ipban o.O" - maybe we should refactor this a bit so it's more readable
                MapleCharacter.ban(c.getSession().getRemoteAddress().toString().split(":")[0], "Enforcing account ban, account " + login, false, 4, false);
            }
        }
        if (loginok != 0) {
            if (!loginAttempt(c)) {
                c.clearInformation();
                if (loginok == 3) {
                    c.getSession().write(CWvsContext.broadcastMsg(1, c.showBanReason(login, true)));
                    c.getSession().write(LoginPacket.getLoginFailed(1)); //Shows no message, used for unstuck the login button
                } else {
                    c.getSession().write(LoginPacket.getLoginFailed(loginok));
                }
            } else {
            	System.out.println("Closing the session.");
                c.getSession().close();
            }
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            if (!loginAttempt(c)) {
                c.clearInformation();
                c.getSession().write(LoginPacket.getTempBan(PacketHelper.getTime(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            } else {
                c.getSession().close();
            }
        } else {
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
        }
	}

}
