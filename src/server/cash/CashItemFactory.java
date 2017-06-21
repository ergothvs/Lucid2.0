package server.cash;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lib.data.MapleData;
import lib.data.MapleDataProvider;
import lib.data.MapleDataProviderFactory;
import lib.data.MapleDataTool;
import net.DatabaseConnection;
import server.cash.CashItemInfo.CashModInfo;

public class CashItemFactory {

    private static CashItemFactory instance = new CashItemFactory();
    
    private MapleDataProvider data = MapleDataProviderFactory.getDataProvider("Etc.wz");
    
    private Map<Integer, CashItemInfo> itemStats = new HashMap<>();
    private Map<Integer, List<Integer>> itemPackage = new HashMap<>();
    private Map<Integer, CashModInfo> itemMods = new HashMap<>();
    private Map<Integer, CashItem> menuItems = new HashMap<>();
    private Map<Integer, CashItem> categoryItems = new HashMap<>();
    
    private List<CashCategory> categories = new LinkedList<>();
   
    public static CashItemFactory getInstance() {
        return instance;
    }

    public void initialize() {
        List<MapleData> commodity = data.getData("Commodity.img").getChildren();
        
        for (MapleData field : commodity) {
            int itemid = MapleDataTool.getIntConvert("ItemId", field, 0);
            int count = MapleDataTool.getIntConvert("Count", field, 1);
            int price = MapleDataTool.getIntConvert("Price", field, 0);
            int sn = MapleDataTool.getIntConvert("SN", field, 0);
            int period = MapleDataTool.getIntConvert("Period", field, 0);
            int gender = MapleDataTool.getIntConvert("Gender", field, 2);
            int sale = MapleDataTool.getIntConvert("OnSale", field, 0);
            
            CashItemInfo stats = new CashItemInfo(itemid, count, price, sn, period, gender, (sale > 0 && price > 0), 0);
           
            if (sn > 0) {
                itemStats.put(sn, stats);
            }
        }

        MapleData cPackage = data.getData("CashPackage.img");
        for (MapleData c : cPackage.getChildren()) {
            
        	if (c.getChildByPath("SN") == null)
                continue;
            
            List<Integer> packageItems = new ArrayList<Integer>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items"); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CashModInfo ret = new CashModInfo(rs.getInt("serial"), rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                    itemMods.put(ret.sn, ret);
                    if (ret.showUp) {
                        CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                        if (cc != null) {
                            ret.toCItem(cc);
                        }
                    }
                }
            }
        } catch (SQLException e) {
        	System.out.println("Failed to load cash shop modified items. " + e);
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_categories"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CashCategory cat = new CashCategory(rs.getInt("categoryid"), rs.getString("name"), 
                    		rs.getInt("parent"), rs.getInt("flag"), rs.getInt("sold"));
                    categories.add(cat);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to load cash shop categories. " + e);
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_menuitems"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CashItem item = new CashItem(rs.getInt("category"), rs.getInt("subcategory"), rs.getInt("parent"), rs.getString("image"), rs.getInt("sn"), rs.getInt("itemid"), rs.getInt("buyable"), rs.getInt("flag"), rs.getInt("price"), rs.getInt("discountPrice"), rs.getInt("quantity"), rs.getInt("expire"), rs.getInt("gender"), rs.getInt("likes"));
                    menuItems.put(item.getSN(), item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to load cash shop categories. " + e);
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_items"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CashItem item = new CashItem(rs.getInt("category"), rs.getInt("subcategory"), rs.getInt("parent"), rs.getString("image"), rs.getInt("sn"), rs.getInt("itemid"),  rs.getInt("buyable"), rs.getInt("flag"), rs.getInt("price"), rs.getInt("discountPrice"), rs.getInt("quantity"), rs.getInt("expire"), rs.getInt("gender"), rs.getInt("likes"));
                    categoryItems.put(item.getSN(), item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to load cash shop categories. " + e);
        }

    }

    public CashItemInfo getSimpleItem(int sn) {
        return itemStats.get(sn);
    }

    public CashItemInfo getItem(int sn) {
        CashItemInfo stats = itemStats.get(sn);
        CashModInfo z = getModInfo(sn);
        if (z != null && z.showUp) {
            return z.toCItem(stats); //null doesnt matter
        }
        if (stats == null || !stats.onSale()) {
            return null;
        }
        return stats;
    }

    public CashItem getMenuItem(int sn) {
        for (CashItem ci : getMenuItems()) {
            if (ci.getSN() == sn) {
                return ci;
            }
        }
        return null;
    }
    
    public CashItem getAllItem(int sn) {
        for (CashItem ci : getAllItems()) {
            if (ci.getSN() == sn) {
                return ci;
            }
        }
        return null;
    }

    public List<Integer> getPackageItems(int itemId) {
        return itemPackage.get(itemId);
    }

    public CashModInfo getModInfo(int sn) {
        return itemMods.get(sn);
    }

    public Collection<CashModInfo> getAllModInfo() {
        return itemMods.values();
    }

    public List<CashCategory> getCategories() {
        return categories;
    }

    public List<CashItem> getMenuItems(int type) {
        List<CashItem> items = new LinkedList<CashItem>();
        for (CashItem ci : menuItems.values()) {
            if (ci.getSubCategory() / 10000 == type) {
                items.add(ci);
            }
        }
        return items;
    }

    public Collection<CashItem> getMenuItems() {
        return menuItems.values();
    }
    
    public List<CashItem> getAllItems(int type) {
        List<CashItem> items = new LinkedList<CashItem>();
        for (CashItem ci : categoryItems.values()) {
            if (ci.getSubCategory() / 10000 == type) {
                items.add(ci);
            }
        }
        return items;
    }

    public Collection<CashItem> getAllItems() {
        return categoryItems.values();
    }

    public List<CashItem> getCategoryItems(int subcategory) {
        List<CashItem> items = new LinkedList<CashItem>();
        for (CashItem ci : categoryItems.values()) {
            if (ci.getSubCategory() == subcategory) {
                items.add(ci);
            }
        }
        return items;
    }
}
