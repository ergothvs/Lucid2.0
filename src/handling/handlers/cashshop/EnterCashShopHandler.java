package handling.handlers.cashshop;

import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.world.CharacterTransfer;
import handling.world.MapleMessengerCharacter;
import handling.world.PlayerBuffStorage;
import handling.world.World;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class EnterCashShopHandler {

	@PacketHandler(opcode = RecvPacketOpcode.ENTER_CASH_SHOP)
	public static void handle(MapleClient c , LittleEndianAccessor lea) {
		if (c.getPlayer().hasBlockedInventory() || c.getPlayer().getMap() == null || c.getPlayer().getEventInstance() != null || c.getChannelServer() == null) {
            c.getSession().write(CField.serverBlocked(2));
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 10) {
            c.getPlayer().dropMessage(1, "The server is busy at the moment. Please try again in a minute or less.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        ChannelServer ch = ChannelServer.getInstance(c.getChannel());
        c.getPlayer().changeRemoval();
        if (c.getPlayer().getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
            World.Messenger.leaveMessenger(c.getPlayer().getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(c.getPlayer().getId(), c.getPlayer().getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(c.getPlayer().getId(), c.getPlayer().getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(c.getPlayer().getId(), c.getPlayer().getAllDiseases());
        World.ChannelChange_Data(new CharacterTransfer(c.getPlayer()), c.getPlayer().getId(), -10);
        ch.removePlayer(c.getPlayer());
        c.updateLoginState(3, c.getSessionIPAddress());
        c.getPlayer().saveToDB(false, false);
        c.getPlayer().getMap().removePlayer(c.getPlayer());
        c.getSession().write(CField.getChannelChange(c, Integer.parseInt(CashShopServer.getIP().split(":")[1])));
        c.setPlayer(null);
        c.setReceiving(false);
	}
}
