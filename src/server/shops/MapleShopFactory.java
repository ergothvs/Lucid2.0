package server.shops;

import java.util.HashMap;
import java.util.Map;

public class MapleShopFactory {

    private final Map<Integer, MapleShop> shops = new HashMap<Integer, MapleShop>();
    private final Map<Integer, MapleShop> npcShops = new HashMap<Integer, MapleShop>();
    private static final MapleShopFactory instance = new MapleShopFactory();

    public static MapleShopFactory getInstance() {
        return instance;
    }

    public void clear() {
        this.shops.clear();
        this.npcShops.clear();
    }

    public MapleShop getShop(int shopId) {
        if (this.shops.containsKey(Integer.valueOf(shopId))) {
            return (MapleShop) this.shops.get(Integer.valueOf(shopId));
        }
        return loadShop(shopId, true);
    }

    public MapleShop getShopForNPC(int npcId) {
        if (this.npcShops.containsKey(Integer.valueOf(npcId))) {
            return (MapleShop) this.npcShops.get(Integer.valueOf(npcId));
        }
        return loadShop(npcId, false);
    }

    private MapleShop loadShop(int id, boolean isShopId) {
        MapleShop ret = MapleShop.createFromDB(id, isShopId);
        if (ret != null) {
            this.shops.put(Integer.valueOf(ret.getId()), ret);
            this.npcShops.put(Integer.valueOf(ret.getNpcId()), ret);
        } else if (isShopId) {
            this.shops.put(Integer.valueOf(id), null);
        } else {
            this.npcShops.put(Integer.valueOf(id), null);
        }
        return ret;
    }
}
