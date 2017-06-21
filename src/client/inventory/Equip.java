package client.inventory;

import java.io.Serializable;
import java.util.*;

import constants.EventConstants;
import constants.GameConstants;
import constants.ItemConstants;
import server.MapleItemInformationProvider;
import server.StructItemOption;
import tools.ArrayUtil;
import tools.Randomizer;

public class Equip extends Item implements Serializable {

    public static enum ScrollResult {
        SUCCESS,
        FAIL,
        CURSE
    }
    public static final int HIDDEN = 1, RARE = 17, EPIC = 18, UNIQUE = 19, LEGENDARY = 20;
    public static final long ARMOR_RATIO = 350000L;
    public static final long WEAPON_RATIO = 700000L;
    //charm: -1 = has not been initialized yet, 0 = already been worn, >0 = has teh charm exp
    private byte upgradeSlots = 0, level = 0, vicioushammer = 0, enhance = 0, enhanctBuff = 0, reqLevel = 0, yggdrasilWisdom = 0, bossDamage = 0, ignorePDR = 0, totalDamage = 0, allStat = 0, karmaCount = -1;
    private short str = 0, dex = 0, _int = 0, luk = 0, hp = 0, mp = 0, watk = 0, matk = 0, wdef = 0, mdef = 0, acc = 0, avoid = 0, hands = 0, speed = 0, jump = 0, charmExp = 0, pvpDamage = 0;
    private int durability = -1, incSkill = -1, fusionAnvil = 0, successiveEnhanceFails = 0;
    private long itemEXP = 0;
    private boolean finalStrike = false;
    private int[] mainPotential = new int[3];
    private int[] bonusPotential = new int[3];
    private int[] socket = new int[3];
    private int[] oldPotential = new int[3]; // main potential
    private MapleRing ring = null;
    private MapleAndroid android = null;
    private List<EquipStat> stats = new LinkedList<EquipStat>();
    private List<EquipSpecialStat> specialStats = new LinkedList<EquipSpecialStat>();
    private Map<EquipStat, Long> statsTest = new LinkedHashMap<>();
    private Map<Byte, Map<EquipStat, Short>> addedStatsPerStar = new HashMap<>();

    public Equip(int id, short position, byte flag) {
        super(id, position, (short) 1, flag);
    }

    public Equip(int id, short position, int uniqueid, short flag) {
        super(id, position, (short) 1, flag, uniqueid);
    }

    @Override
    public Item copy() {
        Equip ret = new Equip(getItemId(), getPosition(), getUniqueId(), getFlag());
        ret.str = str;
        ret.dex = dex;
        ret._int = _int;
        ret.luk = luk;
        ret.hp = hp;
        ret.mp = mp;
        ret.matk = matk;
        ret.mdef = mdef;
        ret.watk = watk;
        ret.wdef = wdef;
        ret.acc = acc;
        ret.avoid = avoid;
        ret.hands = hands;
        ret.speed = speed;
        ret.jump = jump;
        ret.enhance = enhance;
        ret.upgradeSlots = upgradeSlots;
        ret.level = level;
        ret.itemEXP = itemEXP;
        ret.durability = durability;
        ret.vicioushammer = vicioushammer;
        for(int i = 0; i < mainPotential.length; i++){
            ret.mainPotential[i] = mainPotential[i];
        }
        for(int i = 0; i < bonusPotential.length; i++){
            ret.bonusPotential[i] = bonusPotential[i];
        }
        for(int i = 0; i < socket.length; i++){
            ret.socket[i] = socket[i];
        }
        ret.fusionAnvil = fusionAnvil;
        ret.charmExp = charmExp;
        ret.pvpDamage = pvpDamage;
        ret.incSkill = incSkill;
        ret.enhanctBuff = enhanctBuff;
        ret.reqLevel = reqLevel;
        ret.yggdrasilWisdom = yggdrasilWisdom;
        ret.finalStrike = finalStrike;
        ret.bossDamage = bossDamage;
        ret.ignorePDR = ignorePDR;
        ret.totalDamage = totalDamage;
        ret.allStat = allStat;
        ret.karmaCount = karmaCount;
        ret.setGiftFrom(getGiftFrom());
        ret.setOwner(getOwner());
        ret.setQuantity(getQuantity());
        ret.setExpiration(getExpiration());
        ret.stats = stats;
        ret.specialStats = specialStats;
        ret.statsTest = statsTest;
        return ret;
    }

    @Override
    public byte getType() {
        return 1;
    }

    public byte getUpgradeSlots() {
        return upgradeSlots;
    }

    public short getStr() {
        return str;
    }

    public short getDex() {
        return dex;
    }

    public short getInt() {
        return _int;
    }

    public short getLuk() {
        return luk;
    }

    public short getHp() {
        return hp;
    }

    public short getMp() {
        return mp;
    }

    public short getWatk() {
        return watk;
    }

    public short getMatk() {
        return matk;
    }

    public short getWdef() {
        return wdef;
    }

    public short getMdef() {
        return mdef;
    }

    public short getAcc() {
        return acc;
    }

    public short getAvoid() {
        return avoid;
    }

    public short getHands() {
        return hands;
    }

    public short getSpeed() {
        return speed;
    }

    public short getJump() {
        return jump;
    }

    public void setStr(short str) {
        if (str < 0) {
            str = 0;
        }
        this.str = str;
    }

    public void setDex(short dex) {
        if (dex < 0) {
            dex = 0;
        }
        this.dex = dex;
    }

    public void setInt(short _int) {
        if (_int < 0) {
            _int = 0;
        }
        this._int = _int;
    }

    public void setLuk(short luk) {
        if (luk < 0) {
            luk = 0;
        }
        this.luk = luk;
    }

    public void setHp(short hp) {
        if (hp < 0) {
            hp = 0;
        }
        this.hp = hp;
    }

    public void setMp(short mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public void setWatk(short watk) {
        if (watk < 0) {
            watk = 0;
        }
        this.watk = watk;
    }

    public void setMatk(short matk) {
        if (matk < 0) {
            matk = 0;
        }
        this.matk = matk;
    }

    public void setWdef(short wdef) {
        if (wdef < 0) {
            wdef = 0;
        }
        this.wdef = wdef;
    }

    public void setMdef(short mdef) {
        if (mdef < 0) {
            mdef = 0;
        }
        this.mdef = mdef;
    }

    public void setAcc(short acc) {
        if (acc < 0) {
            acc = 0;
        }
        this.acc = acc;
    }

    public void setAvoid(short avoid) {
        if (avoid < 0) {
            avoid = 0;
        }
        this.avoid = avoid;
    }

    public void setHands(short hands) {
        if (hands < 0) {
            hands = 0;
        }
        this.hands = hands;
    }

    public void setSpeed(short speed) {
        if (speed < 0) {
            speed = 0;
        }
        this.speed = speed;
    }

    public void setJump(short jump) {
        if (jump < 0) {
            jump = 0;
        }
        this.jump = jump;
    }

    public void setUpgradeSlots(byte upgradeSlots) {
        this.upgradeSlots = upgradeSlots;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getViciousHammer() {
        return vicioushammer;
    }

    public void setViciousHammer(byte ham) {
        vicioushammer = ham;
    }

    public long getItemEXP() {
        return itemEXP;
    }

    public void setItemEXP(long itemEXP) {
        if (itemEXP < 0) {
            itemEXP = 0;
        }
        this.itemEXP = itemEXP;
    }

    public long getEquipExp() {
        if (itemEXP <= 0) {
            return 0;
        }
        //aproximate value
        if (GameConstants.isWeapon(getItemId())) {
            return itemEXP / WEAPON_RATIO;
        } else {
            return itemEXP / ARMOR_RATIO;
        }
    }

    public long getEquipExpForLevel() {
        if (getEquipExp() <= 0) {
            return 0;
        }
        long expz = getEquipExp();
        for (int i = getBaseLevel(); i <= GameConstants.getMaxLevel(getItemId()); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return expz;
    }

    public long getExpPercentage() {
        if (getEquipLevel() < getBaseLevel() || getEquipLevel() > GameConstants.getMaxLevel(getItemId()) || GameConstants.getExpForLevel(getEquipLevel(), getItemId()) <= 0) {
            return 0;
        }
        return getEquipExpForLevel() * 100 / GameConstants.getExpForLevel(getEquipLevel(), getItemId());
    }

    public int getEquipLevel() {
        if (GameConstants.getMaxLevel(getItemId()) <= 0) {
            return 0;
        } else if (getEquipExp() <= 0) {
            return getBaseLevel();
        }
        int levelz = getBaseLevel();
        long expz = getEquipExp();
        for (int i = levelz; (GameConstants.getStatFromWeapon(getItemId()) == null ? (i <= GameConstants.getMaxLevel(getItemId())) : (i < GameConstants.getMaxLevel(getItemId()))); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                levelz++;
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return levelz;
    }

    public int getBaseLevel() {
        return (GameConstants.getStatFromWeapon(getItemId()) == null ? 1 : 0);
    }

    @Override
    public void setQuantity(short quantity) {
        if (quantity < 0 || quantity > 1) {
            throw new RuntimeException("Setting the quantity to " + quantity + " on an equip (itemid: " + getItemId() + ")");
        }
        super.setQuantity(quantity);
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(final int dur) {
        durability = dur;
    }

    public byte getEnhanctBuff() {
        return enhanctBuff;
    }

    public void setEnhanctBuff(byte enhanctBuff) {
        this.enhanctBuff = enhanctBuff;
    }

    public byte getReqLevel() {
        return reqLevel;
    }

    public void setReqLevel(byte reqLevel) {
        this.reqLevel = reqLevel;
    }

    public byte getYggdrasilWisdom() {
        return yggdrasilWisdom;
    }

    public void setYggdrasilWisdom(byte yggdrasilWisdom) {
        this.yggdrasilWisdom = yggdrasilWisdom;
    }

    public boolean getFinalStrike() {
        return finalStrike;
    }

    public void setFinalStrike(boolean finalStrike) {
        this.finalStrike = finalStrike;
    }

    public byte getBossDamage() {
        return bossDamage;
    }

    public void setBossDamage(byte bossDamage) {
        this.bossDamage = bossDamage;
    }

    public byte getIgnorePDR() {
        return ignorePDR;
    }

    public void setIgnorePDR(byte ignorePDR) {
        this.ignorePDR = ignorePDR;
    }

    public byte getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(byte totalDamage) {
        this.totalDamage = totalDamage;
    }

    public byte getAllStat() {
        return allStat;
    }

    public void setAllStat(byte allStat) {
        this.allStat = allStat;
    }

    public byte getKarmaCount() {
        return karmaCount;
    }

    public void setKarmaCount(byte karmaCount) {
        this.karmaCount = karmaCount;
    }

    public byte getEnhance() {
        return enhance;
    }

    public void setEnhance(final byte en) {
        enhance = en;
    }

    public int getPotentialByLine(int line){
        return mainPotential[line];
    }

    public void setPotentialByLine(int line, int potential){
        mainPotential[line] = potential;
    }

    public int[] getPotential(){
        return mainPotential;
    }

    public void setPotential(int[] newMainPotential){
        mainPotential = newMainPotential;
    }

    public int getBonusPotentialByLine(int line){
        return bonusPotential[line];
    }

    public void setBonusPotentialByLine(int line, int potential){
        bonusPotential[line] = potential;
    }

    public int[] getBonusPotential(){
        return bonusPotential;
    }

    public void setBonusPotential(int[] newBonusPotential){
        bonusPotential = newBonusPotential;
    }

    public int getFusionAnvil() {
        return fusionAnvil;
    }

    public void setFusionAnvil(final int en) {
        fusionAnvil = en;
    }

    /**
     * Uses a potential scroll on this item.
     * @param scrollId
     * @return Whether it failed or not.
     */
    public boolean usePotentialScroll(int scrollId){
        Map<String, Integer> scrollInfo = MapleItemInformationProvider.getInstance().getEquipStats(scrollId);
        final int chance = scrollInfo.containsKey("success") ? scrollInfo.get("success") : 0;
        if (Randomizer.nextInt(100) > chance) {
            return true; //fail
        }
        resetPotentialWithRank(GameConstants.getStateOfPotScroll(scrollId), GameConstants.CHANCE_ON_3RD_LINE_WITH_POT_SCROLL);
        return false;
    }

    public byte getState(){
        int maxPot = ArrayUtil.absoluteMax(ArrayUtil.concat(getPotential(), getBonusPotential()));
        byte res = 0;
        if (maxPot < 0) {
            res = HIDDEN; //hidden
        }else if (maxPot >= 40000) {
            res = LEGENDARY; //legendary
        }else if (maxPot >= 30000) {
            res = UNIQUE; //unique
        }else if (maxPot >= 20000) {
            res = EPIC; //epic
        }else if (maxPot >= 1) {
            res = RARE; //rare
        }
        return res;
    }

    public byte getStateByPotential(int[] potential){
        int maxPot = ArrayUtil.absoluteMax(potential);
        byte res = 0;
        if (maxPot < 0) {
            res = HIDDEN; //hidden
        }else if (maxPot >= 40000) {
            res = LEGENDARY; //legendary
        }else if (maxPot >= 30000) {
            res = UNIQUE; //unique
        }else if (maxPot >= 20000) {
            res = EPIC; //epic
        }else if (maxPot >= 1) {
            res = RARE; //rare
        }
        return res;
    }

    public byte getItemGrade(){
        int pot = getPotentialByLine(0);
        return (byte) (pot == -LEGENDARY ? 4 : pot == -UNIQUE ? 3 : pot == -EPIC ? 2 : pot == -RARE ? 1 : 0);
    }

    public void resetPotential_Fuse(boolean half) { //maker skill - equip first receive
        //no legendary, 0.16% chance unique, 4% chance epic, else rare
        int potentialState = -RARE;
        if (Randomizer.nextInt(100) < 4) {
            potentialState -= Randomizer.nextInt(100) < 4 ? 2 : 1;
        }
        setPotentialByLine(0, potentialState);
        setPotentialByLine(1, potentialState);
        setPotentialByLine(2, (Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0));
//        setPotential1(potentialState); // leaving this here commented to maybe look at later
//        setPotential2((Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0)); //1/10 chance of 3 line
//        setPotential3((Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0)); //just set it theoretically
//        setPotential4((Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0));
//        setPotential5((Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0));
    }

    public void resetBonusPotential_Fuse(boolean half, int potentialState) { //maker skill - equip first receive
        //no legendary, 0.16% chance unique, 4% chance epic, else rare
        potentialState = -potentialState;
        if (Randomizer.nextInt(100) < 4) {
            potentialState -= Randomizer.nextInt(100) < 4 ? 2 : 1;
        }
        setBonusPotentialByLine(0, potentialState);
        setBonusPotentialByLine(1, (Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0));
        setBonusPotentialByLine(2, (Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0));
//        setBonusPotential2((Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0)); //1/10 chance of 3 line
//        setBonusPotential3(0); //just set it theoretically
//        setPotential4(0);
//        setPotential5(0);
    }

    /**
     * Resets the current potential. 10% chance on 3rd line if equip currently does not have a 3rd line.
     */
    public void resetPotential() {
        final int rank = Randomizer.nextInt(100) < 4 ? (Randomizer.nextInt(100) < 4 ? -UNIQUE : -EPIC) : -RARE;
        resetPotentialWithRank(rank, 10);
    }

    /**
     * Sets the current potential with a given rank and chance on a third line.
     * @param rank
     * @param chanceOnThirdLine
     */
    public void resetPotentialWithRank(int rank, int chanceOnThirdLine){
        setPotentialByLine(0, -rank);
        setPotentialByLine(1, -rank);
        if(getPotentialByLine(2) == 0) {
            setPotentialByLine(2, (Randomizer.nextInt(100) < chanceOnThirdLine) ? -rank : 0);
        }else{
            setPotentialByLine(2, -rank);
        }
    }

    /**
     * Resets the bonus potential. 0.16% unique, 0.4% epic, else rare. No chance on 2nd/3rd lines.
     */
    public void resetBonusPotential() {
        final int rank = Randomizer.nextInt(100) < 4 ? (Randomizer.nextInt(100) < 4 ? -UNIQUE : -EPIC) : -RARE;
        resetBonusPotentialWithRank(rank, false);
    }

    public void resetBonusPotentialWithRank(int rank, boolean threeLines){
        for(int i = 0; i < getBonusPotential().length; i++){
            if(getBonusPotentialByLine(i) != 0 || threeLines || i == 0){
                //first line is always set
                setBonusPotentialByLine(i, -rank);
            }else{
                setBonusPotentialByLine(i, 0);
            }
        }
    }

    public void renewPotential(GameConstants.Cubes cube){
        int miracleRate = 1;
        if(EventConstants.DoubleMiracleTime){
            miracleRate *= 2;
        }
        boolean bonus = cube == GameConstants.Cubes.BONUS;
        int[] pots = bonus ? getBonusPotential() : getPotential();
        int rank = getStateByPotential(pots);
        int maxState = GameConstants.getMaxAvailableState(cube);
        if(rank < RARE || rank > maxState){
            return;
        }else if(rank != maxState && Randomizer.nextInt(100) < GameConstants.getRankUpChanceByCube(cube) * miracleRate){
            rank += 1; //rank up
        }
        if(!bonus){
            resetPotentialWithRank(rank, GameConstants.get3rdLineUpChanceByCube(cube));
        }else{
            resetBonusPotentialWithRank(rank, false);
        }
    }

    public void renewPotential_OLD(int type, int line, int toLock, boolean bonus) { // 0 = normal miracle cube, 1 = premium, 2 = epic pot scroll, 3 = super, 5 = enlightening
        //OUTDATED
        int miracleRate = 1;
        if (EventConstants.DoubleMiracleTime) {
            miracleRate *= 2;
        }
        int rank;
        if (bonus) {
            if (type != 6) {
                return;
            }
            rank = (Randomizer.nextInt(100) < 4 * miracleRate && getStateByPotential(getBonusPotential()) != LEGENDARY ?
                    -(getStateByPotential(getBonusPotential()) + 1) : -(getStateByPotential(getBonusPotential()))); // 4 % chance to up 1 tier
            setBonusPotentialByLine(1, rank);
        } else {
            rank = type == 2 ? -EPIC : type == 5 ? (Randomizer.nextInt(100) < 3 * miracleRate && getState() != LEGENDARY ? -LEGENDARY : Randomizer.nextInt(100) < 10 * miracleRate && getState() != LEGENDARY ? -(getState() + 1) : -(getState())) : (Randomizer.nextInt(100) < 4 * miracleRate && getState() != (type == 3 ? LEGENDARY : UNIQUE) ? -(getState() + 1) : -(getState())); // 4 % chance to up 1 tier
            setPotentialByLine(1, rank);
        }
        if (getPotentialByLine(2) > 0 && !bonus) {
            setPotentialByLine(1, rank); // put back old 3rd line
            setPotentialByLine(2, 0);
        } else {
            switch (type) {
                case 1: // premium-> suppose to be 25%
                    setPotentialByLine(1, Randomizer.nextInt(10) == 0 ? rank : 0); //1/10 chance of 3 line
                    break;
                case 2: // epic pot
                    setPotentialByLine(1, Randomizer.nextInt(10) == 0 ? rank : 0); //2/10 chance of 3 line
                    break;
                case 3: // super
                    setPotentialByLine(1, Randomizer.nextInt(10) == 0 ? rank : 0); //3/10 chance of 3 line
                    break;
                case 4: // revolutionary
                    setPotentialByLine(1, Randomizer.nextInt(10) == 0 ? rank : 0); //4/10 chance of 3 line
                    break;
                case 5: // enlightening
                    setPotentialByLine(1, Randomizer.nextInt(10) == 0 ? rank : 0); //3/10 chance of 3 line
               //     setBonusPotential2(Randomizer.nextInt(10) <= 2 ? rank : 0); //3/10 chance of 3 line
                    break;
                case 6: // master
                    if (!bonus) {
                        return;
                    }
                    setBonusPotentialByLine(2, Randomizer.nextInt(10) <= 2 ? rank : 0); //3/10 chance of 3 line
                    setPotentialByLine(2, Randomizer.nextInt(10) <= 2 ? rank : 0); //3/10 chance of 3 line
                    break;
                default:
                    setPotentialByLine(2, 0);
                    break;
            }
        }

        //bunch of stuff that shouldn't be here
//        if (type == 3) {
//            setPotentialByLine(3, Randomizer.nextInt(100) <= 2 ? rank : 0);
//        } else {
//            setPotentialByLine(3, 0);
//        }
//        if (getPotentialByLine(5) > 0) {
//            setPotentialByLine(4, rank);
//        } else if (type == 3) {
//            setPotentialByLine(4, Randomizer.nextInt(100) <= 1 ? rank : 0);
//        } else {
//            setPotentialByLine(4, 0);
//        }
//        setPotentialByLine(5, 0);

        if (bonus) {
            if (getBonusPotentialByLine(2) > 0) {
                setBonusPotentialByLine(1, rank); // put back old 5th line
            } else if (type == 6) { // super, revolutionary and enlightening
                setBonusPotentialByLine(1, Randomizer.nextInt(100) <= 1 ? rank : 0); // 2/100 to get 5 lines
            } else {
                setBonusPotentialByLine(1, 0); //just set it theoretically
            }
            setBonusPotentialByLine(2, 0); //just set it theoretically
        }

//        switch (line) {
//            case 0:
//                //Don't lock
//                break;
//            case 1:
//                setPotentialByLine(1, -(toLock + line * 100000 + (rank > getState() ? 10000 : 0)));
//                break;
//            case 2:
//                setPotentialByLine(2, -(toLock + line * 100000));
//                break;
//            case 3:
//                setPotential3(-(toLock + line * 100000));
//                break;
//            default:
//                System.out.println("[Hacking Attempt] Try to lock potential line which does not exists.");
//                break;
//        }

        // potential locking exists?
        if(line > 0 && line <= 3){
            setPotentialByLine(line - 1, -(toLock + line * 100000));
        }
    }

    /**
     * Reveals hidden potential on items. Can be main, bonus or both.
     */
    public void revealHiddenPotential(){
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(getItemId()) / 10;
        final List<List<StructItemOption>> pots = new LinkedList<>(MapleItemInformationProvider.getInstance().getAllPotentialInfo().values());
        if(getPotentialByLine(0) < 0){ //hidden main
            int newState = -getPotentialByLine(0);
            if(newState > Equip.LEGENDARY){
                newState = Equip.LEGENDARY;
            }else if(newState < Equip.RARE){
                newState = Equip.RARE;
            }

            while (getStateByPotential(getPotential()) != newState) {
                //TODO:This is brute forcing, could potentially (haha) last forever. IDs would have to be hardcoded to do this.
                //31001 = haste, 31002 = door, 31003 = se, 31004 = hb, 41005 = combat orders, 41006 = advanced blessing, 41007 = speed infusion
                for (int i = 0; i < getPotential().length; i++) {
                    if (getPotentialByLine(i) == 0) {
                        break;
                    }
                    boolean rewarded = false;
                    while (!rewarded) {
                        StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                        if (pot != null && pot.reqLevel <= reqLevel && GameConstants.optionTypeFits(pot.optionType, getItemId())
                                && GameConstants.potentialIDFits(pot.opID, newState, i) && !GameConstants.isBonusPot(pot.opID)) { //optionType
                            /*only if the potential is correct for it's type (weapon, acc, etc)
                            and if the potential fits the rank (legendary). In potentialIDFits, the chance to
                            get the same rank on the 2nd/3rd line as on the 1st line is taken into account.*/
                            setPotentialByLine(i, pot.opID);
                            rewarded = true;
                        }
                    }
                }
            }
        }
        if(getBonusPotentialByLine(0) < 0) { //hidden bonus
            //TODO make this not as copy-pasty (not high prio)
            int newState = -getBonusPotentialByLine(0);
            if (newState > Equip.LEGENDARY) {
                newState = Equip.LEGENDARY;
            } else if (newState < Equip.RARE) {
                newState = Equip.RARE;
            }
            while (getStateByPotential(getBonusPotential()) != newState) {
                for (int i = 0; i < getBonusPotential().length; i++) {
                    if (getBonusPotentialByLine(i) == 0) {
                        break;
                    }
                    boolean rewarded = false;
                    while (!rewarded) {
                        StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                        if (pot != null && pot.reqLevel <= reqLevel && GameConstants.optionTypeFits(pot.optionType, getItemId())
                                && GameConstants.potentialIDFits(pot.opID, newState, i) && GameConstants.isBonusPot(pot.opID)) { //optionType
                            /*only if the potential is correct for it's type (weapon, acc, etc)
                            and if the potential fits the rank (legendary). In potentialIDFits, the chance to
                            get the same rank on the 2nd/3rd line as on the 1st line is taken into account.*/
                            setBonusPotentialByLine(i, pot.opID);
                            rewarded = true;
                        }
                    }
                }
            }
        }
    }

    public int getIncSkill() {
        return incSkill;
    }

    public void setIncSkill(int inc) {
        incSkill = inc;
    }

    public short getCharmEXP() {
        return charmExp;
    }

    public short getPVPDamage() {
        return pvpDamage;
    }

    public void setCharmEXP(short s) {
        charmExp = s;
    }

    public void setPVPDamage(short p) {
        pvpDamage = p;
    }

    public MapleRing getRing() {
        if (!GameConstants.isEffectRing(getItemId()) || getUniqueId() <= 0) {
            return null;
        }
        if (ring == null) {
            ring = MapleRing.loadFromDb(getUniqueId(), getPosition() < 0);
        }
        return ring;
    }

    public void setRing(MapleRing ring) {
        this.ring = ring;
    }

    public MapleAndroid getAndroid() {
        if (getItemId() / 10000 != 166 || getUniqueId() <= 0) {
            return null;
        }
        if (android == null) {
            android = MapleAndroid.loadFromDb(getItemId(), getUniqueId());
        }
        return android;
    }

    public void setAndroid(MapleAndroid ring) {
        android = ring;
    }

    public short getSocketState() {
        int flag = 0;
        if (socket[0] > 0 || socket[0] > 0 || socket[0] > 0) { // Got empty sockets show msg
            flag |= SocketFlag.DEFAULT.getValue();
        }
        if (socket[0] > 0) {
            flag |= SocketFlag.SOCKET_BOX_1.getValue();
        }
        if (socket[0] > 1) {
            flag |= SocketFlag.USED_SOCKET_1.getValue();
        }
        if (socket[1] > 0) {
            flag |= SocketFlag.SOCKET_BOX_2.getValue();
        }
        if (socket[1] > 1) {
            flag |= SocketFlag.USED_SOCKET_2.getValue();
        }
        if (socket[2] > 0) {
            flag |= SocketFlag.SOCKET_BOX_3.getValue();
        }
        if (socket[2] > 1) {
            flag |= SocketFlag.USED_SOCKET_3.getValue();
        }
        return (short) flag;
    }

    public int getSocketByNmb(int nmb){
        return socket[nmb];
    }

    public void setSocketByNmb(int nmb, int newSocket){
        socket[nmb] = newSocket;
    }

    public int[] getSockets(){
        return socket;
    }

    public void setSocket(int[] newSocket){
        socket = newSocket;
    }

    public List<EquipStat> getStats() {
        return stats;
    }

    public List<EquipSpecialStat> getSpecialStats() {
        return specialStats;
    }

    public Map<EquipStat, Long> getStatsTest() {
        return statsTest;
    }

    public static Equip calculateEquipStatsTest(Equip eq) {
        eq.getStatsTest().clear();
        if (eq.getUpgradeSlots() > 0) {
            eq.getStatsTest().put(EquipStat.SLOTS, Long.valueOf(eq.getUpgradeSlots()));
        }
        if (eq.getLevel() > 0) {
            eq.getStatsTest().put(EquipStat.LEVEL, Long.valueOf(eq.getLevel()));
        }
        if (eq.getStr() > 0) {
            eq.getStatsTest().put(EquipStat.STR, Long.valueOf(eq.getStr()));
        }
        if (eq.getDex() > 0) {
            eq.getStatsTest().put(EquipStat.DEX, Long.valueOf(eq.getDex()));
        }
        if (eq.getInt() > 0) {
            eq.getStatsTest().put(EquipStat.INT, Long.valueOf(eq.getInt()));
        }
        if (eq.getLuk() > 0) {
            eq.getStatsTest().put(EquipStat.LUK, Long.valueOf(eq.getLuk()));
        }
        if (eq.getHp() > 0) {
            eq.getStatsTest().put(EquipStat.MHP, Long.valueOf(eq.getHp()));
        }
        if (eq.getMp() > 0) {
            eq.getStatsTest().put(EquipStat.MMP, Long.valueOf(eq.getMp()));
        }
        if (eq.getWatk() > 0) {
            eq.getStatsTest().put(EquipStat.WATK, Long.valueOf(eq.getWatk()));
        }
        if (eq.getMatk() > 0) {
            eq.getStatsTest().put(EquipStat.MATK, Long.valueOf(eq.getMatk()));
        }
        if (eq.getWdef() > 0) {
            eq.getStatsTest().put(EquipStat.WDEF, Long.valueOf(eq.getWdef()));
        }
        if (eq.getMdef() > 0) {
//            eq.getStatsTest().put(EquipStat.MDEF, Long.valueOf(eq.getMdef()));
        }
        if (eq.getAcc() > 0) {
//            eq.getStatsTest().put(EquipStat.ACC, Long.valueOf(eq.getAcc()));
        }
        if (eq.getAvoid() > 0) {
//            eq.getStatsTest().put(EquipStat.AVOID, Long.valueOf(eq.getAvoid()));
        }
        if (eq.getHands() > 0) {
            eq.getStatsTest().put(EquipStat.HANDS, Long.valueOf(eq.getHands()));
        }
        if (eq.getSpeed() > 0) {
            eq.getStatsTest().put(EquipStat.SPEED, Long.valueOf(eq.getSpeed()));
        }
        if (eq.getJump() > 0) {
            eq.getStatsTest().put(EquipStat.JUMP, Long.valueOf(eq.getJump()));
        }
        if (eq.getFlag() > 0) {
            eq.getStatsTest().put(EquipStat.FLAG, Long.valueOf(eq.getFlag()));
        }
        if (eq.getIncSkill() > 0) {
            eq.getStatsTest().put(EquipStat.INC_SKILL, Long.valueOf(eq.getIncSkill()));
        }
        if (eq.getEquipLevel() > 0) {
            eq.getStatsTest().put(EquipStat.ITEM_LEVEL, Long.valueOf(eq.getEquipLevel()));
        }
        if (eq.getItemEXP() > 0) {
            eq.getStatsTest().put(EquipStat.ITEM_EXP, Long.valueOf(eq.getItemEXP()));
        }
        if (eq.getDurability() > -1) {
            eq.getStatsTest().put(EquipStat.DURABILITY, Long.valueOf(eq.getDurability()));
        }
        if (eq.getViciousHammer() > 0) {
            eq.getStatsTest().put(EquipStat.VICIOUS_HAMMER, Long.valueOf(eq.getViciousHammer()));
        }
        if (eq.getPVPDamage() > 0) {
            eq.getStatsTest().put(EquipStat.PVP_DAMAGE, Long.valueOf(eq.getPVPDamage()));
        }
        if (eq.getEnhanctBuff() > 0) {
            eq.getStatsTest().put(EquipStat.ENCHANT_BUFF, Long.valueOf(eq.getEnhanctBuff()));
        }
        if (eq.getReqLevel() > 0) {
            eq.getStatsTest().put(EquipStat.REQUIRED_LEVEL, Long.valueOf(eq.getReqLevel()));
        }
        if (eq.getYggdrasilWisdom() > 0) {
            eq.getStatsTest().put(EquipStat.YGGDRASIL_WISDOM, Long.valueOf(eq.getYggdrasilWisdom()));
        }
        if (eq.getFinalStrike()) {
            eq.getStatsTest().put(EquipStat.FINAL_STRIKE, Long.valueOf(eq.getFinalStrike() ? 1 : 0));
        }
        if (eq.getBossDamage() > 0) {
            eq.getStatsTest().put(EquipStat.BOSS_DAMAGE, Long.valueOf(eq.getBossDamage()));
        }
        if (eq.getIgnorePDR() > 0) {
            eq.getStatsTest().put(EquipStat.IGNORE_PDR, Long.valueOf(eq.getIgnorePDR()));
        }
        //SPECIAL STATS:
        if (eq.getTotalDamage() > 0) {
            eq.getStatsTest().put(EquipStat.TOTAL_DAMAGE, Long.valueOf(eq.getTotalDamage()));
        }
        if (eq.getAllStat() > 0) {
            eq.getStatsTest().put(EquipStat.ALL_STAT, Long.valueOf(eq.getAllStat()));
        }
        eq.getStatsTest().put(EquipStat.KARMA_COUNT, Long.valueOf(eq.getKarmaCount())); //no count = -1
        //eq.getStatsTest().put(EquipStat.UNK8, Long.valueOf(-1)); // test
        //eq.getStatsTest().put(EquipStat.CAN_ENHANCE, Long.valueOf(0)); // test
        return (Equip) eq.copy();
    }

    public static Equip calculateEquipStats(Equip eq) {
        eq.getStats().clear();
        eq.getSpecialStats().clear();
        if (eq.getUpgradeSlots() > 0) {
            eq.getStats().add(EquipStat.SLOTS);
        }
        if (eq.getLevel() > 0) {
            eq.getStats().add(EquipStat.LEVEL);
        }
        if (eq.getStr() > 0) {
            eq.getStats().add(EquipStat.STR);
        }
        if (eq.getDex() > 0) {
            eq.getStats().add(EquipStat.DEX);
        }
        if (eq.getInt() > 0) {
            eq.getStats().add(EquipStat.INT);
        }
        if (eq.getLuk() > 0) {
            eq.getStats().add(EquipStat.LUK);
        }
        if (eq.getHp() > 0) {
            eq.getStats().add(EquipStat.MHP);
        }
        if (eq.getMp() > 0) {
            eq.getStats().add(EquipStat.MMP);
        }
        if (eq.getWatk() > 0) {
            eq.getStats().add(EquipStat.WATK);
        }
        if (eq.getMatk() > 0) {
            eq.getStats().add(EquipStat.MATK);
        }
        if (eq.getWdef() > 0) {
            eq.getStats().add(EquipStat.WDEF);
        }
        if (eq.getMdef() > 0) {
//            eq.getStats().add(EquipStat.MDEF);
        }
        if (eq.getAcc() > 0) {
//            eq.getStats().add(EquipStat.ACC);
        }
        if (eq.getAvoid() > 0) {
//            eq.getStats().add(EquipStat.AVOID);
        }
        if (eq.getHands() > 0) {
            eq.getStats().add(EquipStat.HANDS);
        }
        if (eq.getSpeed() > 0) {
            eq.getStats().add(EquipStat.SPEED);
        }
        if (eq.getJump() > 0) {
            eq.getStats().add(EquipStat.JUMP);
        }
        if (eq.getFlag() > 0) {
            eq.getStats().add(EquipStat.FLAG);
        }
        if (eq.getIncSkill() > 0) {
            eq.getStats().add(EquipStat.INC_SKILL);
        }
        if (eq.getEquipLevel() > 0) {
            eq.getStats().add(EquipStat.ITEM_LEVEL);
        }
        if (eq.getItemEXP() > 0) {
            eq.getStats().add(EquipStat.ITEM_EXP);
        }
        if (eq.getDurability() > -1) {
            eq.getStats().add(EquipStat.DURABILITY);
        }
        if (eq.getViciousHammer() > 0) {
            eq.getStats().add(EquipStat.VICIOUS_HAMMER);
        }
        if (eq.getPVPDamage() > 0) {
            eq.getStats().add(EquipStat.PVP_DAMAGE);
        }
        if (eq.getEnhanctBuff() > 0) {
            eq.getStats().add(EquipStat.ENCHANT_BUFF);
        }
        if (eq.getReqLevel() > 0) {
            eq.getStats().add(EquipStat.REQUIRED_LEVEL);
        }
        if (eq.getYggdrasilWisdom() > 0) {
            eq.getStats().add(EquipStat.YGGDRASIL_WISDOM);
        }
        if (eq.getFinalStrike()) {
            eq.getStats().add(EquipStat.FINAL_STRIKE);
        }
        if (eq.getBossDamage() > 0) {
            eq.getStats().add(EquipStat.BOSS_DAMAGE);
        }
        if (eq.getIgnorePDR() > 0) {
            eq.getStats().add(EquipStat.IGNORE_PDR);
        }
        //SPECIAL STATS:
        if (eq.getTotalDamage() > 0) {
            eq.getSpecialStats().add(EquipSpecialStat.TOTAL_DAMAGE);
        }
        if (eq.getAllStat() > 0) {
            eq.getSpecialStats().add(EquipSpecialStat.ALL_STAT);
        }
        eq.getSpecialStats().add(EquipSpecialStat.KARMA_COUNT); //no count = -1
        //if (0 != 0) {
        //    eq.getSpecialStats().add(EquipSpecialStat.CAN_ENHANCE);
        //}
        eq.getSpecialStats().add(EquipSpecialStat.UNK8); // test
        eq.getSpecialStats().add(EquipSpecialStat.CAN_ENHANCE); // test
        return (Equip) eq.copy();
    }

    public int[] getOldPotential(){
        return oldPotential;
    }

    public void setOldPotential(int[] potential){
        this.oldPotential = potential;
    }

    public void enhance(){
        Map<EquipStat, Short> addedStats = new HashMap<>();
        Map<EnhanceStat, Short> enhanceInfo = getEnhanceStats();
        for(EnhanceStat enhStat : enhanceInfo.keySet()){
            EquipStat equipStat = ItemConstants.getEquipStatByEnhanceStat(enhStat);
            setEquipStat(equipStat, (short) (getEquipStat(equipStat) + enhanceInfo.get(enhStat)));
            addedStats.put(equipStat, enhanceInfo.get(enhStat));
        }
        getAddedStatsPerStar().put(getEnhance(), addedStats);
        setEnhance((byte) (getEnhance() + 1));
    }

    public void removeStar() {
        // could probably be done using a stack, but whatever, this works.
        // also, this has to be stored in the DB.
        // Now if the equip class is gone (like after a restart) you won't get your stats removed.
        setEnhance((byte) (getEnhance() - 1));
        Map<EquipStat, Short> lastAddedStats = getAddedStatsPerStar().get(getEnhance());
        if(lastAddedStats != null) {
            for (EquipStat es : lastAddedStats.keySet()) {
                setEquipStat(es, (short) (getEquipStat(es)-lastAddedStats.get(es)));
            }
        }
    }

    public void setEquipStat(EquipStat es, short amount){
        switch(es){
            case STR:
                setStr(amount);
                break;
            case DEX:
                setDex(amount);
                break;
            case LUK:
                setLuk(amount);
                break;
            case INT:
                setInt(amount);
                break;
            case WATK:
                setWatk(amount);
                break;
            case MATK:
                setMatk(amount);
                break;
            case WDEF:
                setWdef(amount);
                break;
//            case MDEF:
//                setMdef(amount);
//                break;
//            case ACC:
//                setAcc(amount);
//                break;
            default:
                break;
        }
    }

    public short getEquipStat(EquipStat es){
        short res = 0;
        switch(es){
            case STR:
                res = getStr();
                break;
            case DEX:
                res = getDex();
                break;
            case LUK:
                res = getLuk();
                break;
            case INT:
                res = getInt();
                break;
            case WATK:
                res = getWatk();
                break;
            case MATK:
                res =  getMatk();
                break;
            case WDEF:
                res = getWdef();
                break;
//            case MDEF:
//                res = getMdef();
//                break;
//            case ACC:
//                res =  getAcc();
//                break;
            default:
                break;
        }
        return res;
    }

    public Map<EnhanceStat, Short> getEnhanceStats() {
        Map<EnhanceStat, Short> res = new HashMap<>();
        EnhanceStat[] upgradeStats = EnhanceStat.values();
        // these values are thought up on the spot.
        int base = 2;
        for(EnhanceStat es : upgradeStats){
            short currentStat = getEquipStat(ItemConstants.getEquipStatByEnhanceStat(es));
            if(currentStat > 0) {
                if(es == EnhanceStat.WDEF){
                    base = 10;
                }
                res.put(es, (short) (base + getEnhance()/5 + currentStat/50)); // base + more per 5 stars + more per 50 stat
            }
        }
        return res;
    }

    public int getSuccessiveEnhanceFails(){
        return successiveEnhanceFails;
    }

    public void setSuccessiveEnhanceFails(int successiveEnhanceFails) {
        this.successiveEnhanceFails = successiveEnhanceFails;
    }

    public Map<Byte, Map<EquipStat, Short>> getAddedStatsPerStar(){
        return addedStatsPerStar;
    }

    public void setStarStats(String starStats){
        /*TODO save+load added stats from stars from DB (to ensure nothing goes wrong if server
          restarts/crashes/etc...)
        */
    }
}
