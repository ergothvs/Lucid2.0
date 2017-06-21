package handling.handlers.npc;

import client.MapleClient;
import constants.GameConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import server.shops.MapleShop;
import tools.data.LittleEndianAccessor;

public class NpcShopHandler {

	@PacketHandler(opcode = RecvPacketOpcode.NPC_SHOP)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {

		if (c.getPlayer() == null) {
			return;
		}

		MapleShop shop = c.getPlayer().getShop();

		if (shop == null) {
			return;
		}

		byte bmode = lea.readByte();
		short slot = 0;
        if(bmode >= 0 && bmode <= 2) {
            slot = lea.readShort();
        }
		switch (bmode) {
		case 0: {
			slot++;
			int itemId = lea.readInt();
			short quantity = lea.readShort();
			lea.readInt(); //Nate help here
			int unitprice = lea.readInt();
			shop.buy(c, slot, itemId, quantity);
			break;
		}
		case 1: {
			int itemId = lea.readInt();
			short quantity = lea.readShort();
			shop.sell(c, GameConstants.getInventoryType(itemId), (byte) slot, quantity);
			break;
		}
		case 2: {
			shop.recharge(c, (byte) slot);
			break;
		}
		default:
			c.getPlayer().setConversation(0);
			break;
		}
	}
}
