package server.shops;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import client.MapleClient;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import net.DatabaseConnection;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.FileoutputUtil;
import tools.Pair;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class MapleShop {

    private static final Set<Integer> rechargeableItems = new LinkedHashSet<Integer>();
    private final int id;
    private final int npcId;
    private final List<MapleShopItem> items = new LinkedList<MapleShopItem>();
    private final List<Pair<Integer, String>> ranks = new ArrayList<Pair<Integer, String>>();

    static {
        rechargeableItems.add(Integer.valueOf(2070000));
        rechargeableItems.add(Integer.valueOf(2070001));
        rechargeableItems.add(Integer.valueOf(2070002));
        rechargeableItems.add(Integer.valueOf(2070003));
        rechargeableItems.add(Integer.valueOf(2070004));
        rechargeableItems.add(Integer.valueOf(2070005));
        rechargeableItems.add(Integer.valueOf(2070006));
        rechargeableItems.add(Integer.valueOf(2070007));
        rechargeableItems.add(Integer.valueOf(2070008));
        rechargeableItems.add(Integer.valueOf(2070009));
        rechargeableItems.add(Integer.valueOf(2070010));
        rechargeableItems.add(Integer.valueOf(2070011));
        rechargeableItems.add(Integer.valueOf(2070023));
        rechargeableItems.add(Integer.valueOf(2070024));
        rechargeableItems.add(Integer.valueOf(2330000));
        rechargeableItems.add(Integer.valueOf(2330001));
        rechargeableItems.add(Integer.valueOf(2330002));
        rechargeableItems.add(Integer.valueOf(2330003));
        rechargeableItems.add(Integer.valueOf(2330004));
        rechargeableItems.add(Integer.valueOf(2330005));
        rechargeableItems.add(Integer.valueOf(2330008));
        rechargeableItems.add(Integer.valueOf(2331000));
        rechargeableItems.add(Integer.valueOf(2332000));
    }

    public MapleShop(int id, int npcId) {
        this.id = id;
        this.npcId = npcId;
    }

    public void addItem(MapleShopItem item) {
        this.items.add(item);
    }

    public List<MapleShopItem> getItems() {
        return this.items;
    }

    public void sendShop(MapleClient c) {
        c.getPlayer().setShop(this);
        c.getSession().write(CField.NPCPacket.getNPCShop(getNpcId(), this, c));
    }

    public void sendShop(MapleClient c, int customNpc) {
        c.getPlayer().setShop(this);
        c.getSession().write(CField.NPCPacket.getNPCShop(customNpc, this, c));
    }

    public void buy(MapleClient c, short slot, int itemId, short quantity) {
        if ((itemId / 10000 == 190) && (!GameConstants.isMountItemAvailable(itemId, c.getPlayer().getJob()))) {
            c.getPlayer().dropMessage(1, "You may not buy this item.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int x = 0;
        int index = -1;
        for (Item i : c.getPlayer().getRebuy()) {
            if (i.getItemId() == itemId) {
                index = x;
                break;
            }
            x++;
        }
        if (index >= 0) {
            Item i = (Item) c.getPlayer().getRebuy().get(index);
            int price = (int) Math.max(Math.ceil(ii.getPrice(itemId) * (GameConstants.isRechargable(itemId) ? 1 : i.getQuantity())), 0.0D);
            if ((price >= 0) && (c.getPlayer().getMeso() >= price)) {
                if (MapleInventoryManipulator.checkSpace(c, itemId, i.getQuantity(), i.getOwner())) {
                    c.getPlayer().gainMeso(-price, false);
                    MapleInventoryManipulator.addbyItem(c, i);
                    c.getPlayer().getRebuy().remove(index);
                    c.getSession().write(CField.NPCPacket.confirmShopTransaction((byte) 0, this, c, x));
                } else {
                    c.getPlayer().dropMessage(1, "Your inventory is full.");
                    c.getSession().write(CField.NPCPacket.confirmShopTransaction((byte) 0, this, c, -1));
                }
            } else {
                c.getSession().write(CField.NPCPacket.confirmShopTransaction((byte) 0, this, c, -1));
            }

            return;
        }
        //MapleShopItem item = findById(itemId);
        MapleShopItem item = findBySlot(slot);
        if (item == null) {
            System.out.println("Error with shop " + id);
            return;
        }
        quantity *= item.getQuantity();
        if ((item != null) && (item.getPrice() > 0) && (item.getReqItem() == 0)) {
            if (item.getRank() >= 0) {
                boolean passed = true;
                int y = 0;
                for (Pair<Integer, String> i : getRanks()) {
                    if ((c.getPlayer().haveItem(((Integer) i.left).intValue(), 1, true, true)) && (item.getRank() >= y)) {
                        passed = true;
                        break;
                    }
                    y++;
                }
                if (!passed) {
                    c.getPlayer().dropMessage(1, "You need a higher rank.");
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
            }
            int price = GameConstants.isRechargable(itemId) ? item.getPrice() : item.getPrice() * quantity;
            if ((price >= 0) && (c.getPlayer().getMeso() >= price)) {
                if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                    c.getPlayer().gainMeso(-price, false);
                    if (GameConstants.isPet(itemId)) {
                        MapleInventoryManipulator.addById(c, itemId, (short) (quantity * item.getQuantity()), "", MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance()), -1, false, "Bought from shop " + id + ", " + npcId + " on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        if (GameConstants.isRechargable(itemId)) {
                            quantity = ii.getSlotMax(item.getItemId());
                        }

                        MapleInventoryManipulator.addById(c, itemId, quantity, "Bought from shop " + this.id + ", " + this.npcId + " on " + FileoutputUtil.CurrentReadable_Date());
                    }
                } else {
                    c.getPlayer().dropMessage(1, "Your Inventory is full");
                }
                c.getSession().write(CField.NPCPacket.confirmShopTransaction((byte) 0, this, c, -1));
            }
        } else if ((item != null) && (item.getReqItem() > 0) && (quantity == 1) && (c.getPlayer().haveItem(item.getReqItem(), item.getReqItemQ(), false, true))) {
            if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(item.getReqItem()), item.getReqItem(), item.getReqItemQ(), false, false);
                if (GameConstants.isPet(itemId)) {
                    MapleInventoryManipulator.addById(c, itemId, (short) (quantity * item.getQuantity()), "", MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance()), -1, false, "Bought from shop " + id + ", " + npcId + " on " + FileoutputUtil.CurrentReadable_Date());
                } else {
                    if (GameConstants.isRechargable(itemId)) {
                        quantity = ii.getSlotMax(item.getItemId());
                    }
                    MapleInventoryManipulator.addById(c, itemId, quantity, "Bought from shop " + this.id + ", " + this.npcId + " on " + FileoutputUtil.CurrentReadable_Date());
                }
            } else {
                c.getPlayer().dropMessage(1, "Your Inventory is full");
            }
            c.getSession().write(CField.NPCPacket.confirmShopTransaction((byte) 0, this, c, -1));
        }
    }

    public void sell(MapleClient c, MapleInventoryType type, byte slot, short quantity) {
        if ((quantity == 65535) || (quantity == 0)) {
            quantity = 1;
        }
        Item item = c.getPlayer().getInventory(type).getItem((short) slot);
        if (item == null) {
            return;
        }

        if ((GameConstants.isThrowingStar(item.getItemId())) || (GameConstants.isBullet(item.getItemId()))) {
            quantity = item.getQuantity();
        }

        short iQuant = item.getQuantity();
        if (iQuant == 65535) {
            iQuant = 1;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((ii.cantSell(item.getItemId())) || (GameConstants.isPet(item.getItemId()))) {
            return;
        }

        List<Item> listRebuy = new ArrayList<>();
        if ((quantity <= iQuant) && (iQuant > 0)) {
            if (item.getQuantity() == quantity) {
                if (c.getPlayer().getRebuy().size() < 10) {
                    c.getPlayer().getRebuy().add(item.copy());
                } else if (c.getPlayer().getRebuy().size() == 10) {
                    for (int i = 1; i < 10; i++) {
                        listRebuy.add(c.getPlayer().getRebuy().get(i));
                    }
                    listRebuy.add(item.copy());
                    c.getPlayer().setRebuy(listRebuy);
                } else {
                    int x = c.getPlayer().getRebuy().size();
                    for (int i = x - 10; i < x; i++) {
                        listRebuy.add(c.getPlayer().getRebuy().get(i));
                    }
                    c.getPlayer().setRebuy(listRebuy);
                }
            } else {
                c.getPlayer().getRebuy().add(item.copyWithQuantity(quantity));
            }
            MapleInventoryManipulator.removeFromSlot(c, type, (short) slot, quantity, false);
            double price;
            
            if ((GameConstants.isThrowingStar(item.getItemId())) || (GameConstants.isBullet(item.getItemId()))) {
                    price = ii.getWholePrice(item.getItemId()) / ii.getSlotMax(item.getItemId());
                } else {
                    price = ii.getPrice(item.getItemId());
                }

            int recvMesos = (int) Math.max(Math.ceil(price * quantity), 0.0D);
            if ((price != -1.0D) && (recvMesos > 0)) {
                c.getPlayer().gainMeso(recvMesos, false);
            }
            c.getSession().write(CField.NPCPacket.confirmShopTransaction((byte) 0, this, c, -1));
        }
    }

    public void recharge(MapleClient c, byte slot) {
        Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((short) slot);

        if ((item == null) || ((!GameConstants.isThrowingStar(item.getItemId())) && (!GameConstants.isBullet(item.getItemId())))) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        short slotMax = ii.getSlotMax(item.getItemId());
        int skill = GameConstants.getMasterySkill(c.getPlayer().getJob());

        if (skill != 0) {
            slotMax = (short) (slotMax + c.getPlayer().getTotalSkillLevel(SkillFactory.getSkill(skill)) * 10);
        }
        if (item.getQuantity() < slotMax) {
            int price = (int) Math.round(ii.getPrice(item.getItemId()) * (slotMax - item.getQuantity()));
            if (c.getPlayer().getMeso() >= price) {
                item.setQuantity(slotMax);
                c.getSession().write(CWvsContext.InventoryPacket.updateInventorySlot(MapleInventoryType.USE, item, false));
                c.getPlayer().gainMeso(-price, false, false);
                c.getSession().write(CField.NPCPacket.confirmShopTransaction((byte) 0, this, c, -1));
            }
        }
    }

    protected MapleShopItem findById(int itemId) {
        for (MapleShopItem item : this.items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }
    
    protected MapleShopItem findBySlot(short slot) {
        for (MapleShopItem item : this.items) {
            if (item.getSlot() == slot) {
                return item;
            }
        }
        return null;
    }

    public static MapleShop createFromDB(int id, boolean isShopId) {
        MapleShop ret = null;

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(isShopId ? "SELECT * FROM shops WHERE shopid = ?" : "SELECT * FROM shops WHERE npcid = ?");
            int shopId;
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                shopId = rs.getInt("shopid");
                ret = new MapleShop(shopId, rs.getInt("npcid"));
                rs.close();
                ps.close();
            } else {
                rs.close();
                ps.close();
                return null;
            }
            ps = con.prepareStatement("SELECT * FROM shopitems WHERE shopid = ? ORDER BY position ASC");
            ps.setInt(1, shopId);
            rs = ps.executeQuery();
            List<Integer> recharges = new ArrayList<Integer>(rechargeableItems);
            while (rs.next()) {
                if (ii.itemExists(rs.getInt("itemid"))) {
                    if ((GameConstants.isThrowingStar(rs.getInt("itemid"))) || (GameConstants.isBullet(rs.getInt("itemid")))) {
                        MapleShopItem starItem = new MapleShopItem((short) rs.getShort("buyable"), ii.getSlotMax(rs.getInt("itemid")), rs.getInt("itemid"), rs.getInt("price"), (short) rs.getInt("position"), rs.getInt("reqitem"), rs.getInt("reqitemq"), rs.getByte("rank"), rs.getInt("category"), rs.getInt("minLevel"), rs.getInt("expiration"), false);
                        ret.addItem(starItem);
                        if (rechargeableItems.contains(Integer.valueOf(starItem.getItemId()))) {
                            recharges.remove(Integer.valueOf(starItem.getItemId()));
                        }
                    } else {
                        ret.addItem(new MapleShopItem((short) rs.getShort("buyable"), rs.getShort("quantity"), rs.getInt("itemid"), rs.getInt("price"), (short) rs.getInt("position"), rs.getInt("reqitem"), rs.getInt("reqitemq"), rs.getByte("rank"), rs.getInt("category"), rs.getInt("minLevel"), rs.getInt("expiration"), false)); //todo potential
                    }
                }
            }
            for (Integer recharge : recharges) {
                ret.addItem(new MapleShopItem((short) 1, ii.getSlotMax(recharge.intValue()), recharge.intValue(), 0, (short) 0, 0, 0, (byte) 0, 0, 0, 0, false));
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT * FROM shopranks WHERE shopid = ? ORDER BY rank ASC");
            ps.setInt(1, shopId);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (ii.itemExists(rs.getInt("itemid"))) {
                    ret.ranks.add(new Pair<Integer, String>(Integer.valueOf(rs.getInt("itemid")), rs.getString("name")));
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Could not load shop");
        }
        return ret;
    }

    public int getNpcId() {
        return this.npcId;
    }

    public int getId() {
        return this.id;
    }

    public List<Pair<Integer, String>> getRanks() {
        return this.ranks;
    }
}
