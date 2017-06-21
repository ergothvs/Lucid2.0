package constants;

import client.inventory.EnhanceStat;
import client.inventory.Equip;
import client.inventory.EquipStat;
import client.inventory.MapleInventory;
import server.MapleItemInformationProvider;
import tools.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemConstants {

    public static List<Triple<Integer, Integer, Boolean>> starChanceInfo = new ArrayList<>(); // success, boom, canDropStar
    public static Map<EquipStat, EnhanceStat> equipToEnhanceStats = new HashMap<>();
    public static final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

    public static void initStarChanceInfo(){
        /*
        index is used to indicate which star this info belongs to (0 = 0* => 1*)
        https://orangemushroom.files.wordpress.com/2014/07/star-force-enhancement-rates.png
        success chance, boom chance if it fails, canDropStar
        */
        starChanceInfo.add(new Triple<>(950, 0, Boolean.FALSE)); // 0
        starChanceInfo.add(new Triple<>(900, 0, Boolean.FALSE)); // 1
        starChanceInfo.add(new Triple<>(850, 0, Boolean.FALSE)); // 2
        starChanceInfo.add(new Triple<>(850, 0, Boolean.FALSE)); // 3
        starChanceInfo.add(new Triple<>(800, 0, Boolean.FALSE)); // 4
        starChanceInfo.add(new Triple<>(750, 0, Boolean.FALSE)); // 5
        starChanceInfo.add(new Triple<>(700, 0, Boolean.TRUE)); // 6
        starChanceInfo.add(new Triple<>(650, 0, Boolean.TRUE)); // 7
        starChanceInfo.add(new Triple<>(600, 0, Boolean.TRUE)); // 8
        starChanceInfo.add(new Triple<>(550, 0, Boolean.TRUE)); // 9
        starChanceInfo.add(new Triple<>(450, 0, Boolean.FALSE)); // 10
        starChanceInfo.add(new Triple<>(350, 0, Boolean.TRUE)); // 11
        starChanceInfo.add(new Triple<>(300, 10, Boolean.TRUE)); // 12
        starChanceInfo.add(new Triple<>(300, 10, Boolean.TRUE)); // 13
        starChanceInfo.add(new Triple<>(300, 10, Boolean.TRUE)); // 14
        starChanceInfo.add(new Triple<>(300, 20, Boolean.FALSE)); // 15
        starChanceInfo.add(new Triple<>(300, 20, Boolean.TRUE)); // 16
        starChanceInfo.add(new Triple<>(300, 20, Boolean.TRUE)); // 17
        starChanceInfo.add(new Triple<>(300, 30, Boolean.TRUE)); // 18
        starChanceInfo.add(new Triple<>(300, 30, Boolean.TRUE)); // 19
        starChanceInfo.add(new Triple<>(300, 70, Boolean.FALSE)); // 20
        starChanceInfo.add(new Triple<>(300, 70, Boolean.TRUE)); // 21
        starChanceInfo.add(new Triple<>(30, 190, Boolean.TRUE)); // 22
        starChanceInfo.add(new Triple<>(20, 290, Boolean.TRUE)); // 23
        starChanceInfo.add(new Triple<>(10, 400, Boolean.TRUE)); // 24
    }

    public static List<Triple<Integer, Integer, Boolean>> getStarChanceInfo(){
        if(starChanceInfo.size() == 0){
            initStarChanceInfo();
        }
        return starChanceInfo;
    }

    public static void initEquipToEnhanceStats(){
        equipToEnhanceStats.put(EquipStat.WATK, EnhanceStat.WATK);
        equipToEnhanceStats.put(EquipStat.MATK, EnhanceStat.MATK);
        equipToEnhanceStats.put(EquipStat.STR, EnhanceStat.STR);
        equipToEnhanceStats.put(EquipStat.DEX, EnhanceStat.DEX);
        equipToEnhanceStats.put(EquipStat.INT, EnhanceStat.INT);
        equipToEnhanceStats.put(EquipStat.LUK, EnhanceStat.LUK);
        equipToEnhanceStats.put(EquipStat.WDEF, EnhanceStat.WDEF);
//        equipToEnhanceStats.put(EquipStat.MDEF, EnhanceStat.MDEF);
        equipToEnhanceStats.put(EquipStat.MHP, EnhanceStat.MHP);
        equipToEnhanceStats.put(EquipStat.MMP, EnhanceStat.MMP);
//        equipToEnhanceStats.put(EquipStat.ACC, EnhanceStat.ACC);
//        equipToEnhanceStats.put(EquipStat.AVOID, EnhanceStat.AVOID);
        equipToEnhanceStats.put(EquipStat.JUMP, EnhanceStat.JUMP);
        equipToEnhanceStats.put(EquipStat.SPEED, EnhanceStat.SPEED);
    }

    public static EnhanceStat getEnhanceStatByEquipStat(EquipStat es){
        if(equipToEnhanceStats.size() == 0){
            initEquipToEnhanceStats();
        }
        return equipToEnhanceStats.get(es);
    }

    public static EquipStat getEquipStatByEnhanceStat(EnhanceStat es){
        if(equipToEnhanceStats.size() == 0){
            initEquipToEnhanceStats();
        }
        EquipStat res = null;
        for(EquipStat key : equipToEnhanceStats.keySet()){
            if(equipToEnhanceStats.get(key) == es){
                res = key;
            }
        }
        return res;
    }

    public static long getEnhancementCost(Equip equip){
        int star = equip.getEnhance();
        int level = getLevelByEquip(equip);
        level = level/10 > 14 ? 14 : (level / 10);
        int[] bases = {1000, 1300, 2100, 3600, 6000, 9600, 14700, 21500, 30200, 41000, 54200, 70100, 88900, 110800, 136000};
        int[] addedPerStar = {100, 300, 1100, 2500, 5000, 8700, 13700, 20500, 29100, 40000, 53300, 69100, 87900, 109700, 135000};
        return bases[level] + addedPerStar[level] * star;
    }

    public static long getMaxStarsByItemLevel(int level){
        return level < 95 ? 5 : level < 108 ? 8 : level < 118 ? 10 : level < 128 ? 12 : level < 138 ? 13 : 15;
    }

    public static int getLevelByEquip(Equip equip){
        return ii.getReqLevel(equip.getItemId());
    }

    public static boolean isHyperTeleportRock(int itemId) {
        return itemId == 5040004;
    }
}
