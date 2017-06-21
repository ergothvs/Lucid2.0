package server;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants;
import handling.world.World;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import server.commands.CommandProcessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.PlayerShopPacket;

public class MapleTrade {

    private MapleTrade partner = null;
    private final List<Item> items = new LinkedList<Item>();
    private List<Item> exchangeItems;
    private int meso = 0;
    private int exchangeMeso = 0;
    private boolean locked = false;
    private boolean inTrade = false;
    private final WeakReference<MapleCharacter> chr;
    private final byte tradingslot;

    public MapleTrade(byte tradingslot, MapleCharacter chr) {
        this.tradingslot = tradingslot;
        this.chr = new WeakReference(chr);
    }

    public final void CompleteTrade() {
        if (this.exchangeItems != null) {
            List<Item> itemz = new LinkedList<Item>(this.exchangeItems);
            for (Item item : itemz) {
                short flag = item.getFlag();

                if (ItemFlag.KARMA_EQ.check(flag)) {
                    item.setFlag((short) (flag - ItemFlag.KARMA_EQ.getValue()));
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    item.setFlag((short) (flag - ItemFlag.KARMA_USE.getValue()));
                }
                MapleInventoryManipulator.addFromDrop(((MapleCharacter) this.chr.get()).getClient(), item, false);
            }
            this.exchangeItems.clear();
        }
        if (this.exchangeMeso > 0) {
            ((MapleCharacter) this.chr.get()).gainMeso(this.exchangeMeso - GameConstants.getTaxAmount(this.exchangeMeso), false, false);
        }
        this.exchangeMeso = 0;

        ((MapleCharacter) this.chr.get()).getClient().getSession().write(CField.InteractionPacket.TradeMessage(this.tradingslot, (byte) 7));
    }

    public final void cancel(MapleClient c, MapleCharacter chr) {
        cancel(c, chr, 0);
    }

    public final void cancel(MapleClient c, MapleCharacter chr, int unsuccessful) {
        if (this.items != null) {
            List<Item> itemz = new LinkedList(this.items);
            for (Item item : itemz) {
                MapleInventoryManipulator.addFromDrop(c, item, false);
            }
            this.items.clear();
        }
        if (this.meso > 0) {
            chr.gainMeso(this.meso, false, false);
        }
        this.meso = 0;

        c.getSession().write(CField.InteractionPacket.getTradeCancel(this.tradingslot, unsuccessful));
    }

    public final boolean isLocked() {
        return this.locked;
    }

    public final void setMeso(int meso) {
        if ((this.locked) || (this.partner == null) || (meso <= 0) || (this.meso + meso <= 0)) {
            return;
        }
        if (((MapleCharacter) this.chr.get()).getMeso() >= meso) {
            ((MapleCharacter) this.chr.get()).gainMeso(-meso, false, false);
            this.meso += meso;
            ((MapleCharacter) this.chr.get()).getClient().getSession().write(CField.InteractionPacket.getTradeMesoSet((byte) 0, this.meso));
            if (this.partner != null) {
                this.partner.getChr().getClient().getSession().write(CField.InteractionPacket.getTradeMesoSet((byte) 1, this.meso));
            }
        }
    }

    public final void addItem(Item item) {
        if ((this.locked) || (this.partner == null)) {
            return;
        }
        this.items.add(item);
        ((MapleCharacter) this.chr.get()).getClient().getSession().write(CField.InteractionPacket.getTradeItemAdd((byte) 0, item));
        if (this.partner != null) {
            this.partner.getChr().getClient().getSession().write(CField.InteractionPacket.getTradeItemAdd((byte) 1, item));
        }
    }

    public final void chat(String message) throws Exception {
        if (!CommandProcessor.processCommand(chr.get().getClient(), message, ServerConstants.CommandType.TRADE)) {
            ((MapleCharacter) this.chr.get()).dropMessage(-2, ((MapleCharacter) this.chr.get()).getName() + " : " + message);
            if (this.partner != null) {
                this.partner.getChr().getClient().getSession().write(PlayerShopPacket.shopChat(((MapleCharacter) this.chr.get()).getName() + " : " + message, 1));
            }
        }
        if (((MapleCharacter) this.chr.get()).getClient().isMonitored()) {
            World.Broadcast.broadcastGMMessage(CWvsContext.broadcastMsg(6, ((MapleCharacter) this.chr.get()).getName() + " said in trade with " + this.partner.getChr().getName() + ": " + message));
        } else if ((this.partner != null) && (this.partner.getChr() != null) && (this.partner.getChr().getClient().isMonitored())) {
            World.Broadcast.broadcastGMMessage(CWvsContext.broadcastMsg(6, ((MapleCharacter) this.chr.get()).getName() + " said in trade with " + this.partner.getChr().getName() + ": " + message));
        }
    }

    public final void chatAuto(String message) {
        ((MapleCharacter) this.chr.get()).dropMessage(-2, message);
        if (this.partner != null) {
            this.partner.getChr().getClient().getSession().write(PlayerShopPacket.shopChat(message, 1));
        }
        if (((MapleCharacter) this.chr.get()).getClient().isMonitored()) {
            World.Broadcast.broadcastGMMessage(CWvsContext.broadcastMsg(6, ((MapleCharacter) this.chr.get()).getName() + " said in trade [Automated] with " + this.partner.getChr().getName() + ": " + message));
        } else if ((this.partner != null) && (this.partner.getChr() != null) && (this.partner.getChr().getClient().isMonitored())) {
            World.Broadcast.broadcastGMMessage(CWvsContext.broadcastMsg(6, ((MapleCharacter) this.chr.get()).getName() + " said in trade [Automated] with " + this.partner.getChr().getName() + ": " + message));
        }
    }

    public final MapleTrade getPartner() {
        return this.partner;
    }

    public final void setPartner(MapleTrade partner) {
        if (this.locked) {
            return;
        }
        this.partner = partner;
    }

    public final MapleCharacter getChr() {
        return (MapleCharacter) this.chr.get();
    }

    public final int getNextTargetSlot() {
        if (this.items.size() >= 9) {
            return -1;
        }
        int ret = 1;
        for (Item item : this.items) {
            if (item.getPosition() == ret) {
                ret++;
            }
        }
        return ret;
    }

    public boolean inTrade() {
        return this.inTrade;
    }

    public final boolean setItems(MapleClient c, Item item, byte targetSlot, int quantity) {
        int target = getNextTargetSlot();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((this.partner == null) || (target == -1) || (GameConstants.isPet(item.getItemId())) || (isLocked()) || ((GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.EQUIP) && (quantity != 1))) {
            return false;
        }
        short flag = item.getFlag();
        if ((ItemFlag.UNTRADABLE.check(flag)) || (ItemFlag.LOCK.check(flag))) {
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if (((ii.isDropRestricted(item.getItemId())) || (ii.isAccountShared(item.getItemId())))
                && (!ItemFlag.KARMA_EQ.check(flag)) && (!ItemFlag.KARMA_USE.check(flag))) {
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }

        Item tradeItem = item.copy();
        if ((GameConstants.isThrowingStar(item.getItemId())) || (GameConstants.isBullet(item.getItemId()))) {
            tradeItem.setQuantity(item.getQuantity());
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), item.getQuantity(), true);
        } else {
            tradeItem.setQuantity((short) quantity);
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), (short) quantity, true);
        }
        if (targetSlot < 0) {
            targetSlot = (byte) target;
        } else {
            for (Item itemz : this.items) {
                if (itemz.getPosition() == targetSlot) {
                    targetSlot = (byte) target;
                    break;
                }
            }
        }
        tradeItem.setPosition((short) targetSlot);
        addItem(tradeItem);
        return true;
    }

    private int check() {
        if (((MapleCharacter) this.chr.get()).getMeso() + this.exchangeMeso < 0L) {
            return 1;
        }

        if (this.exchangeItems != null) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            byte eq = 0;
            byte use = 0;
            byte setup = 0;
            byte etc = 0;
            byte cash = 0;
            for (Item item : this.exchangeItems) {
                switch (GameConstants.getInventoryType(item.getItemId())) {
                    case EQUIP:
                        eq = (byte) (eq + 1);
                        break;
                    case USE:
                        use = (byte) (use + 1);
                        break;
                    case SETUP:
                        setup = (byte) (setup + 1);
                        break;
                    case ETC:
                        etc = (byte) (etc + 1);
                        break;
                    case CASH:
                        cash = (byte) (cash + 1);
                }

                if ((ii.isPickupRestricted(item.getItemId())) && (((MapleCharacter) this.chr.get()).haveItem(item.getItemId(), 1, true, true))) {
                    return 2;
                }
            }
            if ((((MapleCharacter) this.chr.get()).getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq) || (((MapleCharacter) this.chr.get()).getInventory(MapleInventoryType.USE).getNumFreeSlot() < use) || (((MapleCharacter) this.chr.get()).getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup) || (((MapleCharacter) this.chr.get()).getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc) || (((MapleCharacter) this.chr.get()).getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash)) {
                return 1;
            }
        }

        return 0;
    }

    public static final void completeTrade(MapleCharacter c) {
        MapleTrade local = c.getTrade();
        MapleTrade partner = local.getPartner();

        if ((partner == null) || (local.locked)) {
            return;
        }
        local.locked = true;
        partner.getChr().getClient().getSession().write(CField.InteractionPacket.getTradeConfirmation());

        partner.exchangeItems = new LinkedList<Item>(local.items);
        partner.exchangeMeso = local.meso;

        if (partner.isLocked()) {
            int lz = local.check();
            int lz2 = partner.check();
            if ((lz == 0) && (lz2 == 0)) {
                local.CompleteTrade();
                partner.CompleteTrade();
            } else {
                partner.cancel(partner.getChr().getClient(), partner.getChr(), lz == 0 ? lz2 : lz);
                local.cancel(c.getClient(), c, lz == 0 ? lz2 : lz);
            }
            partner.getChr().setTrade(null);
            c.setTrade(null);
        }
    }

    public static final void cancelTrade(MapleTrade Localtrade, MapleClient c, MapleCharacter chr) {
        Localtrade.cancel(c, chr);

        MapleTrade partner = Localtrade.getPartner();
        if ((partner != null) && (partner.getChr() != null)) {
            partner.cancel(partner.getChr().getClient(), partner.getChr());
            partner.getChr().setTrade(null);
        }
        chr.setTrade(null);
    }

    public static final void startTrade(MapleCharacter c) {
        if (c.getTrade() == null) {
            c.setTrade(new MapleTrade((byte) 0, c));
            c.getClient().getSession().write(CField.InteractionPacket.getTradeStart(c.getClient(), c.getTrade(), (byte) 0));
        } else {
            c.getClient().getSession().write(CWvsContext.broadcastMsg(5, "You are already in a trade"));
        }
    }

    public static final void inviteTrade(MapleCharacter c1, MapleCharacter c2) {
        if ((c1 == null) || (c1.getTrade() == null)) {
            return;
        }
        if ((c2 != null) && (c2.getTrade() == null)) {
            c2.setTrade(new MapleTrade((byte) 1, c2));
            c2.getTrade().setPartner(c1.getTrade());
            c1.getTrade().setPartner(c2.getTrade());
            c2.getClient().getSession().write(CField.InteractionPacket.getTradeInvite(c1));
        } else {
            c1.getClient().getSession().write(CWvsContext.broadcastMsg(5, "The other player is already trading with someone else."));
            cancelTrade(c1.getTrade(), c1.getClient(), c1);
        }
    }

    public static final void visitTrade(MapleCharacter c1, MapleCharacter c2) {
        if ((c2 != null) && (c1.getTrade() != null) && (c1.getTrade().getPartner() == c2.getTrade()) && (c2.getTrade() != null) && (c2.getTrade().getPartner() == c1.getTrade())) {
            c1.getTrade().inTrade = true;
            c2.getClient().getSession().write(PlayerShopPacket.shopVisitorAdd(c1, 1));
            c1.getClient().getSession().write(CField.InteractionPacket.getTradeStart(c1.getClient(), c1.getTrade(), (byte) 1));
            c1.dropMessage(-2, "System : Use @tradehelp to see the list of trading commands");
            c2.dropMessage(-2, "System : Use @tradehelp to see the list of trading commands");
        } else {
            c1.getClient().getSession().write(CWvsContext.broadcastMsg(5, "The other player has already closed the trade"));
        }
    }

    public static final void declineTrade(MapleCharacter c) {
        MapleTrade trade = c.getTrade();
        if (trade != null) {
            if (trade.getPartner() != null) {
                MapleCharacter other = trade.getPartner().getChr();
                if ((other != null) && (other.getTrade() != null)) {
                    other.getTrade().cancel(other.getClient(), other);
                    other.setTrade(null);
                    other.dropMessage(5, c.getName() + " has declined your trade request");
                }
            }
            trade.cancel(c.getClient(), c);
            c.setTrade(null);
        }
    }
}
