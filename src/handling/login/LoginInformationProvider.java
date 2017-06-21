/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constants.GameConstants;
import lib.data.MapleData;
import lib.data.MapleDataProvider;
import lib.data.MapleDataProviderFactory;
import lib.data.MapleDataTool;
import tools.Triple;

public class LoginInformationProvider {

    public enum JobType {

        UltimateAdventurer(-1, 0, 450004600, true, true, false, false, true, false, true, true),
        Resistance(0, 3000, 100000003, true, false, false, false, false, false, true, true), // works
        Adventurer(1, 0, 100000003, false, true, false, false, false, false, true, true), // works
        Cygnus(2, 1000, 450004600, false, true, false, false, false, true, true, true), // works
        Aran(3, 2000, 450004600, true, true, false, false, true, false, true, true), // cant create char
        Evan(4, 0, 450004600, true, true, false, false, true, false, true, false),//works
        Mercedes(5, 2002, 450004600, false, false, false, false, false, false, true, true),//works
        Demon(6, 3001, 450004600, false, false, true, false, false, false, true, true),//works
        Phantom(7, 2003, 450004600, false, true, false, false, false, true, true, true),//works
        DualBlade(8, 0, 450004600, false, true, false, false, false, false, true, true),//works
        Mihile(9, 5000, 450004600, true, true, false, false, true, false, true, true),//works
        Luminous(10, 2004, 450004600, false, true, false, false, false, true, true, true),//works
        Kaiser(11, 6000, 450004600, false, true, false, false, false, false, true, true),//works
        AngelicBuster(12, 6001, 450004600, false, true, false, false, false, false, true, true),//works
        Cannoneer(13, 0, 450004600, true, true, false, false, true, false, true, true),//doesn't warp to the right map warps to 3000000
        Xenon(14, 3002, 450004600, true, true, true, false, false, false, true, true),//works
        Zero(15, 10112, 450004600, false, true, false, false, false, true, true, true),//works
        Shade(16, 2500, 450004600, false, true, false, false, false, true, true, true),//needs testing
        Jett(17, 508, 450004600, false, false, false, false, false, true, true, true),//works
        Hayato(18, 4001, 450004600, true, true, false, true, false, false, true, true),//works
        Kanna(19, 4002, 450004600, true, true, false, true, false, false, true, true),//works
        BEAST_TAMER(20, 11212, 450004600, true, true, false, true, false, false, true, true), //needs testing
        KINESIS(22, 14000, 450004600, true, true, false, true, false, false, true, false); //works
        
    	//name(job selection, jobid, startermap, ?,?,?,?,?,?);
    	//todo add chase/bt(works on acernis146) and shade and kinesis
    	
    	
        // actual tutorial map values
//        UltimateAdventurer(-1, 0, 450004600, true, true, false, false, true, false),
//        Resistance(0, 3000, 450004600, true, false, false, false, false, false), // tutorial works 100%
//        Adventurer(1, 0, 450004600, false, true, false, false, false, false),
//        Cygnus(2, 1000, 450004600, false, true, false, false, false, true),
//        Aran(3, 2000, 450004600, true, true, false, false, true, false),
//        Evan(4, 2001, 450004600, true, true, false, false, true, false),//evan starter map - need to test tutorial
//        Mercedes(5, 2002, 450004600, false, false, false, false, false, false),//101050000 - 910150000
//        Demon(6, 3001, 450004600, false, false, true, false, false, false),
//        Phantom(7, 2003, 450004600, false, true, false, false, false, true),
//        DualBlade(8, 0, 450004600, false, true, false, false, false, false),//tutorial fixed
//        Mihile(9, 5000, 450004600, true, true, false, false, true, false),
//        Luminous(10, 2004, 450004600, false, true, false, false, false, true),//Ellinia atm TODO tutorial
//        Kaiser(11, 6000, 450004600, false, true, false, false, false, false),
//        AngelicBuster(12, 6001, 450004600, false, true, false, false, false, false),//400000000 - 940011000 - town now TODO tutorial
//        Cannoneer(13, 0, 450004600, true, true, false, false, true, false),//portal problem
//        Xenon(14, 3002, 450004600, true, true, true, false, false, false),
//        Zero(15, 10112, 450004600, false, true, false, false, false, true),//create tutorial plox
//        Jett(16, 0, 450004600, false, false, false, false, false, true),//End map for tutorial
//        Hayato(17, 4001, 450004600, true, true, false, true, false, false),//half stater map TODO real tutorial
//        Kanna(18, 4002, 450004600, true, true, false, true, false, false);
        public int type, id, map;
        public boolean hairColor, skinColor, faceMark, hat, bottom, cape, shoes, weapon;

        private JobType(int type, int id, int map, boolean hairColor, boolean skinColor, boolean faceMark, boolean hat, boolean bottom, boolean cape, boolean shoes, boolean weapon) {
            this.type = type;
            this.id = id;
            this.map = map;
            this.hairColor = hairColor;
            this.skinColor = skinColor;
            this.faceMark = faceMark;
            this.hat = hat;
            this.bottom = bottom;
            this.cape = cape;
            this.shoes = shoes;
            this.weapon = weapon;
        }

        public static JobType getByType(int g) {
            if (g == JobType.Cannoneer.type) {
                return JobType.Adventurer;
            }
            for (JobType e : JobType.values()) {
                if (e.type == g) {
                    return e;
                }
            }
            return null;
        }

        public static JobType getById(int g) {
            if (g == JobType.Adventurer.id) {
                return JobType.Adventurer;
            }
            if (g == 508) {
                return JobType.Jett;
            }
            for (JobType e : JobType.values()) {
                if (e.id == g) {
                    return e;
                }
            }
            return null;
        }
    }
    
    private final static LoginInformationProvider instance = new LoginInformationProvider();
    protected final List<String> ForbiddenName = new ArrayList<>();
    //gender, val, job
    
    protected final Map<Triple<Integer, Integer, Integer>, List<Integer>> makeCharInfo = new HashMap<>();
    //0 = eyes 1 = hair 2 = haircolor 3 = skin 4 = top 5 = bottom 6 = shoes 7 = weapon
    
    /*
     * Resistance
     * 0 - Eyes
     * 1 - Hair
     * 2 - Hair Color
     * 3 - Longcoat?
     * 4 - Shoes
     * 5 - Weapon
     */

    public static LoginInformationProvider getInstance() {
        return instance;
    }

    protected LoginInformationProvider() {
        final MapleDataProvider prov = MapleDataProviderFactory.getDataProvider("Etc.wz");
        	
        MapleData nameData = prov.getData("ForbiddenName.img");
        for (final MapleData data : nameData.getChildren()) {
            ForbiddenName.add(MapleDataTool.getString(data));
        }
        
        final MapleData infoData = prov.getData("MakeCharInfo.img");
        final MapleData uA = infoData.getChildByPath("UltimateAdventurer");
        
    }

    public static boolean isExtendedSpJob(int jobId) {
        return GameConstants.isSeparatedSp(jobId);
    }

    public final boolean isForbiddenName(final String in) {
        for (final String name : ForbiddenName) {

            if (in.toLowerCase().contains(name.toLowerCase())) {
                return true;
            }

        }
        return false;
    }

    public final boolean isEligibleItem(final int gender, final int val, final int job, final int item) {
        if (item < 0) {
            return false;
        }
        final Triple<Integer, Integer, Integer> key = new Triple<>(gender, val, job);
        final List<Integer> our = makeCharInfo.get(key);
        if (our == null) {
            return false;
        }
        return our.contains(item);
    }
}
