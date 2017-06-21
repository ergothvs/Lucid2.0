package client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import constants.GameConstants;
import lib.data.MapleData;
import lib.data.MapleDataTool;
import server.MapleStatEffect;
import server.life.Element;
import tools.Pair;
import tools.Randomizer;

public class Skill implements Comparator<Skill> {

    private String name = "", psdDamR = "", targetPlus = "";
    private final List<MapleStatEffect> effects = new ArrayList<>();
    private List<MapleStatEffect> pvpEffects = null;
    private List<Integer> animation = null;
    private final List<Pair<String, Integer>> requiredSkill = new ArrayList<>();
    private Element element = Element.NEUTRAL;
    private int id, animationTime = 0, masterLevel = 0, maxLevel = 0, delay = 0, trueMax = 0, eventTamingMob = 0, skillTamingMob = 0, skillType = 0, psd = 0, psdSkill = 0; //4 is alert
    private boolean invisible = false, chargeskill = false, timeLimited = false, combatOrders = false, pvpDisabled = false, magic = false, casterMove = false, pushTarget = false, pullTarget = false;
    private int hyper = 0;
    int vskill = 0; // 0 = ?, 1 = skill, 2 = boost
    private int boostjob = 0, vSkillIconID = 0;

    public Skill(final int id) {
        super();
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Skill loadFromData(final int id, final MapleData data, final MapleData delayData) {
        Skill ret = new Skill(id);

        boolean isBuff;
        final int skillType = MapleDataTool.getInt("skillType", data, -1);
        final String elem = MapleDataTool.getString("elemAttr", data, null);
        if (elem != null) {
            ret.element = Element.getFromChar(elem.charAt(0));
        }
        ret.skillType = skillType;
        ret.invisible = MapleDataTool.getInt("invisible", data, 0) > 0;
        ret.timeLimited = MapleDataTool.getInt("timeLimited", data, 0) > 0;
        ret.combatOrders = MapleDataTool.getInt("combatOrders", data, 0) > 0;
        ret.hyper = MapleDataTool.getInt("hyper", data, 0);
        ret.masterLevel = MapleDataTool.getInt("masterLevel", data, 0);

        ret.psd = MapleDataTool.getInt("psd", data, 0);
        if(ret.psd == 1){
            final MapleData psdskill = data.getChildByPath("psdSkill");
            if (psdskill != null)
            {
                ret.psdSkill = Integer.parseInt(data.getChildByPath("psdSkill").getChildren().get(0).getName());
            }
        }

        if ((id == 22111001 || id == 22140000 || id == 22141002)) {
            ret.masterLevel = 5; //hack
        }
        ret.eventTamingMob = MapleDataTool.getInt("eventTamingMob", data, 0);
        ret.skillTamingMob = MapleDataTool.getInt("skillTamingMob", data, 0);

        boolean hasVskillMod = data.getChildByPath("vSkill") != null;
        if(hasVskillMod) {
            ret.vskill = MapleDataTool.getInt("vSkill", data); // returns 0 if not found
            if (ret.vskill == 2) {
                // TODO WZ this shit
//                int job = ret.psdSkill / 10000;
//                if(job == MapleJob.WARRIOR.getId()) { //
//                    ret.boostjob.add(MapleJob.HERO.getId());
//                    ret.boostjob.add(MapleJob.PALADIN.getId());
//                    ret.boostjob.add(MapleJob.DARKKNIGHT.getId());
//                }else if(job == MapleJob.MAGICIAN.getId()){
//                    ret.boostjob.add(MapleJob.FP_ARCHMAGE.getId());
//                    ret.boostjob.add(MapleJob.IL_ARCHMAGE.getId());
//                    ret.boostjob.add(MapleJob.BISHOP.getId());
//                }else if(job == MapleJob.BOWMAN.getId()){
//                    ret.boostjob.add(MapleJob.MARKSMAN.getId());
//                    ret.boostjob.add(MapleJob.BOWMASTER.getId());
//                }else if(job == MapleJob.THIEF.getId()){
//                    ret.boostjob.add(MapleJob.SHADOWER.getId());
//                    ret.boostjob.add(MapleJob.NIGHTLORD.getId());
//                }else if(job == MapleJob.PIRATE.getId()){
//                    ret.boostjob.add(MapleJob.BUCCANEER.getId());
//                    ret.boostjob.add(MapleJob.CORSAIR.getId());
//                }else if(job == MapleJob.JETT1.getId()){
//                    ret.boostjob.add(MapleJob.JETT4.getId());
//                }else if(job == MapleJob.EVAN1.getId() || job == MapleJob.EVAN2.getId() || job == MapleJob.EVAN3.getId()
//                        || job == 2200 || job == 2211 || job == 2217){
//                    ret.boostjob.add(MapleJob.EVAN4.getId());
//                }else if(job == MapleJob.DEMON_AVENGER1.getId()){
//                    ret.boostjob.add(MapleJob.DEMON_AVENGER4.getId());
//                }else if(job == MapleJob.PIRATE_CANNONNEER.getId()){
//                    ret.boostjob.add(MapleJob.CANNON_MASTER.getId());
//                }else if(job % 10 == 0) {
//                    if(job <= 530){
//                        ret.boostjob.add(job + 2);
//                        if (!MapleJob.isExist(job + 2)) {
//                            System.out.println("Oh no-");
//                        }
//                    }else {
//                        ret.boostjob.add(job + 12);
//                        if (!MapleJob.isExist(job + 12)) {
//                            System.out.println("Oh no");
//                        }
//                    }
////                }else if(job % 100 == 10){
////                    ret.boostjob.add(job + 2);
////                    if(!MapleJob.isExist(job + 12)){
////                        System.out.println("Oh no");
////                    }
//                }else if(job % 10 == 1){
//                    ret.boostjob.add(job + 1);
//                    if(!MapleJob.isExist(job + 1)){
//                        System.out.println("Oh no");
//                    }
//                }else{
//                    ret.boostjob.add(job);
//                    if(!MapleJob.isExist(job)){
//                        System.out.println("Oh no");
//                    }
//                }
            }
        }

        final MapleData inf = data.getChildByPath("info");
        if (inf != null) {
            ret.pvpDisabled = MapleDataTool.getInt("pvp", inf, 1) <= 0;
            ret.magic = MapleDataTool.getInt("magicDamage", inf, 0) > 0;
            ret.casterMove = MapleDataTool.getInt("casterMove", inf, 0) > 0;
            ret.pushTarget = MapleDataTool.getInt("pushTarget", inf, 0) > 0;
            ret.pullTarget = MapleDataTool.getInt("pullTarget", inf, 0) > 0;
        }
        final MapleData effect = data.getChildByPath("effect");
        if (skillType == 2) {
            isBuff = true;
        } else if (skillType == 3) { //final attack
            ret.animation = new ArrayList<>();
            ret.animation.add(0);
            isBuff = effect != null;
        } else {
            MapleData action_ = data.getChildByPath("action");
            final MapleData hit = data.getChildByPath("hit");
            final MapleData ball = data.getChildByPath("ball");

            boolean action = false;
            if (action_ == null) {
                if (data.getChildByPath("prepare/action") != null) {
                    action_ = data.getChildByPath("prepare/action");
                    action = true;
                }
            }
            isBuff = ((effect != null) && (hit == null) && (ball == null));
            if (action_ != null) {
                String d;
                if (action) {
                    d = MapleDataTool.getString(action_, null);
                } else {
                    d = MapleDataTool.getString("0", action_, null);
                }
                if (d != null) {
                    isBuff |= d.equals("alert2");
                    final MapleData dd = delayData.getChildByPath(d);
                    if (dd != null) {
                        for (MapleData del : dd) {
                            ret.delay += Math.abs(MapleDataTool.getInt("delay", del, 0));
                        }
                        if (ret.delay > 30) {
                            ret.delay = (int) Math.round(ret.delay * 11.0 / 16.0);
                            ret.delay -= (ret.delay % 30);
                        }
                    }
                    if (SkillFactory.getDelay(d) != null) { //this should return true always
                        ret.animation = new ArrayList<>();
                        ret.animation.add(SkillFactory.getDelay(d));
                        if (!action) {
                            for (MapleData ddc : action_) {
                                if (!MapleDataTool.getString(ddc, d).equals(d)) {
                                    String c = MapleDataTool.getString(ddc);
                                    if (SkillFactory.getDelay(c) != null) {
                                        ret.animation.add(SkillFactory.getDelay(c));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(ret.vskill == 2){
                isBuff = false;
            }else {
                switch (id) {
                    case 1076:
                    case 11076:
                    case 2111002:
                    case 2111003:
                    case 2121001:
                    case 2221001:
                    case 2301002:
                    case 2321001:
                    case 4211001:
                    case 12111005:
                    case 22161003:
                        isBuff = false;
                        break;
                    case 93:
                    case 1004:
                    case 4121054:
                    case 1026:
                    case 1111002:
                    case 65111100:
                    case 1111007:
                    case 1211009:
                    case 4341054:
                    case 1220013:
                    case 5321054:
                    case 1311007:
                    case 1320009:
                    case 2120010:
                    case 2121009:
                    case 4341052:
                    case 31201002:
                    case 31001001:
                    case 2220010:
                    case 2221009:
                    case 2311006:
                    case 2320011:
                    case 2321010:
                    case 3120006:
                    case 3121002:
                    case 3220005:
                    case 3221002:
                    case 4111001:
                    case 4111009:
                    case 4211003:
                    case 4221013:
                    case 4321000:
                    case 4331003:
                    case 4341002:
                    case 5110001:
                    case 5111005:
                    case 5111007:
                    case 5120011:
                    case 5120012:
                    case 5121003:
                    case 5121009:
                    case 5121015:
                    case 5211001:
                    case 5211002:
                    case 5211006:
                    case 5211007:
                    case 5211009:
                    case 5220002:
                    case 5220011:
                    case 5220012:
                    case 5311004:
                    case 5311005:
                    case 5320007:
                    case 5321003:
                    case 5321004:
                    case 5701005:
                    case 5711001:
                    case 5711011:
                    case 5720005:
                    case 5721002:
                    case 9001004:
                    case 9101004:
                    case 10000093:
                    case 10001004:
                    case 10001026:
                    case 13111005:
                    case 14111007:
                    case 15100004:
                    case 15101006:
                    case 15111002:
                    case 15111005:
                    case 15111006:
                    case 15111011:
                    case 20000093:
                    case 20001004:
                    case 20001026:
                    case 20010093:
                    case 20011004:
                    case 20011026:
                    case 20020093:
                    case 20021026:
                    case 20031209:
                    case 20031210:
                    case 21000000:
                    case 21101003:
                    case 22121001:
                    case 22131001:
                    case 22131002:
                    case 22141002:
                    case 22151002:
                    case 22151003:
                    case 22161002:
                    case 22161004:
                    case 22171000:
                    case 22171004:
                    case 22181000:
                    case 22181003:
                    case 22181004:
                    case 24101005:
                    case 24111002:
                    case 24121008:
                    case 24121009:
                    case 27101202:
                    case 27110007:
                    case 30000093:
                    case 30001026:
                    case 30010093:
                    case 30011026:
                    case 31121005:
                    case 32001003:
                    case 32101003:
                    case 32110000:
                    case 32110007:
                    case 32110008:
                    case 32110009:
                    case 32111005:
                    case 32111006:
                    case 32111012:
                    case 32120000:
                    case 32120001:
                    case 32121003:
                    case 33101006:
                    case 33111003:
                    case 35001001:
                    case 35001002:
                    case 35111003:
                    case 35101005:
                    case 35101007:
                    case 35101009:
                    case 35111001:
                    case 35111002:
                    case 35111004:
                    case 35111005:
                    case 35111009:
                    case 35111010:
                    case 35111011:
                    case 35111013:
                    case 35120000:
                    case 35120014:
                    case 35121003:
                    case 35121005:
                    case 35121006:
                    case 35121009:
                    case 35121010:
                    case 35121013:
                    case 36111006:
                    case 41001001:
                    case 41121003:
                    case 42100010:
                    case 42101002:
                    case 42101004:
                    case 42111006:
                    case 42121008:
                    case 50001214:
                    case 51101003:
                    case 51111003:
                    case 51111004:
                    case 51121004:
                    case 51121005:
                    case 60001216:
                    case 61100005:
                    case 60001217:
                    case 61101002:
                    case 61111008://Kaiser mode
                    case 61120007:
                    case 61120008://Kaiser mode
                    case 61121053://Kaiser Hyper Mode
                    case 61121054://Kaiser's Majesty
                    case 61120011:
                    case 80001000:
                    case 80001089:
                    case 31211003:
                    case 31221004:
                    case 32121054:
                    case 2121054:
                    case 11101021:
                    case 13111024:
                    case 2321054:
                    case 11101022: //polling moon
                    case 11111022:
                    case 36121054:
                    case 31011001:
                    case 31211004:
                    case 32121006:
                    case 3101004:
                    case 3201004:
                    case 5001005:
                    case 5220014://dice2 cosair
                    case 15001003:
                    case 5111010:
                    case 80001079:
                    case 80001080:
                    case 80001081:
                        //    case 33001001:
                    case 1101013:
                    case 36121003:
                    case 33111006:
                    case 35111016:
                    case 33111007:
                    case 61111004:
                    case 24121054:
                    case 80001155:
                    case 31221054:
                    case 5721054:
                    case 5721052:
                        //    case 27121005:
                    case 27001004: // Ã¬ï¿½ÂµÃ¬Å Â¤Ã­â€¦ï¿½Ã«â€œÅ“ Ã«Â§Ë†Ã«â€šËœ
                    case 27100003: // Ã«Â¸â€�Ã«Â Ë†Ã¬Å Â¤ Ã¬ËœÂ¤Ã«Â¸Å’ Ã«â€¹Â¤Ã­ï¿½Â¬Ã«â€¹Ë†Ã¬Å Â¤
                        //       case 27101202: // Ã«Â³Â´Ã¬ï¿½Â´Ã«â€œÅ“ Ã­â€�â€žÃ«Â Ë†Ã¬â€¦â€�
                    case 27111004: // Ã¬â€¢Ë†Ã­â€¹Â° Ã«Â§Â¤Ã¬Â§ï¿½Ã¬â€°Ëœ
                    case 27111005: // Ã«ï¿½Â¼Ã¬ï¿½Â´Ã­Å Â¸Ã¬â€°ï¿½Ã«ï¿½â€žÃ¬Å¡Â° ÃªÂ°â‚¬Ã«â€œÅ“
                    case 27111006: // Ã­ï¿½Â¬Ã­â€¹Â± Ã«Â©â€�Ã«â€�â€�Ã­â€¦Å’Ã¬ï¿½Â´Ã¬â€¦Ëœ
                        //      case 27110007: // Ã«ï¿½Â¼Ã¬ï¿½Â´Ã­â€�â€ž Ã­Æ’â‚¬Ã¬ï¿½Â´Ã«â€¹Â¬
                    case 27121006: // Ã«â€¹Â¤Ã­ï¿½Â¬Ã«â€¹Ë†Ã¬Å Â¤ Ã¬â€ Å’Ã¬â€žÅ“Ã«Â¦Â¬
                    case 27111202: // Ã«â€¦Â¹Ã¬Å Â¤Ã­â€�Â¼Ã¬â€“Â´
                    case 27121202: // Ã¬â€¢â€žÃ­ï¿½Â¬Ã¬Â¹Â¼Ã«Â¦Â½Ã¬Å Â¤
                    case 27101100: // Ã¬â€¹Â¤Ã­â€�Â¼Ã«â€œÅ“ Ã«Å¾Å“Ã¬â€žÅ“
                    case 27111100: // Ã¬Å Â¤Ã­Å½â„¢Ã­Å Â¸Ã«Å¸Â´ Ã«ï¿½Â¼Ã¬ï¿½Â´Ã­Å Â¸
                    case 27121100: // Ã«ï¿½Â¼Ã¬ï¿½Â´Ã­Å Â¸ Ã«Â¦Â¬Ã­â€�Å’Ã«Â â€°Ã¬â€¦Ëœ
                    case 27111303: // Ã«ï¿½Â°Ã¬Å Â¤ Ã¬â€šÂ¬Ã¬ï¿½Â´Ã«â€œÅ“
                    case 27121303: // Ã¬â€¢Â±Ã¬â€ â€�Ã«Â£Â¨Ã­Å Â¸ Ã­â€šÂ¬
                    case 27121054: // Ã«Â©â€�Ã«ÂªÂ¨Ã«ï¿½Â¼Ã¬ï¿½Â´Ã¬Â¦Ë†
                    case 42101020:
                    case 42101021:
                    case 42101022:
                    case 42101023:
                    case 36121013:
                    case 36121014:
                    case 36121002:
                        //       case 15001022:
                        //   case 1301013:
                    case 1321015:
                    case 20041239:

                        isBuff = true;
                }

                if (GameConstants.isAngel(id)/* || GameConstants.isSummon(id)*/) {
                    isBuff = false;
                }
            }
        }
        ret.chargeskill = data.getChildByPath("keydown") != null;

        final MapleData level = data.getChildByPath("common");

        if (level != null) {
            ret.maxLevel = MapleDataTool.getInt("maxLevel", level, 1); //10 just a failsafe, shouldn't actually happens
            ret.psdDamR = MapleDataTool.getString("damR", level, ""); //for the psdSkill tag
            ret.targetPlus = MapleDataTool.getString("targetPlus", level, "");
            ret.trueMax = ret.maxLevel + (ret.combatOrders ? 2 : 0);
            for (int i = 1; i <= ret.trueMax; i++) {
                ret.effects.add(MapleStatEffect.loadSkillEffectFromData(level, id, isBuff, i, "x"));
            }

        } else {
            for (final MapleData leve : data.getChildByPath("level")) {
                ret.effects.add(MapleStatEffect.loadSkillEffectFromData(leve, id, isBuff, Byte.parseByte(leve.getName()), null));
            }
            ret.maxLevel = ret.effects.size();
            ret.trueMax = ret.effects.size();
        }

        final MapleData level2 = data.getChildByPath("PVPcommon");
        if (level2 != null) {
            ret.pvpEffects = new ArrayList<>();
            for (int i = 1; i <= ret.trueMax; i++) {
                ret.pvpEffects.add(MapleStatEffect.loadSkillEffectFromData(level2, id, isBuff, i, "x"));
            }
        }

        final MapleData reqDataRoot = data.getChildByPath("req");
        if (reqDataRoot != null) {
            for (final MapleData reqData : reqDataRoot.getChildren()) {
                ret.requiredSkill.add(new Pair<>(reqData.getName(), MapleDataTool.getInt(reqData, 1)));
            }
        }

        ret.animationTime = 0;
        if (effect != null) {
            for (final MapleData effectEntry : effect) {
                ret.animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
            }
        }
        return ret;
    }

    public MapleStatEffect getEffect(final int level) {
        if (effects.size() < level) {
            if (effects.size() > 0) { //incAllskill
                return effects.get(effects.size() - 1);
            }
            return null;
        } else if (level <= 0) {
            return effects.get(0);
        }
        return effects.get(level - 1);
    }

    public MapleStatEffect getPVPEffect(final int level) {
        if (pvpEffects == null) {
            return getEffect(level);
        }
        if (pvpEffects.size() < level) {
            if (pvpEffects.size() > 0) { //incAllskill
                return pvpEffects.get(pvpEffects.size() - 1);
            }
            return null;
        } else if (level <= 0) {
            return pvpEffects.get(0);
        }
        return pvpEffects.get(level - 1);
    }

    public int getSkillType() {
        return skillType;
    }

    public List<Integer> getAllAnimation() {
        return animation;
    }

    public int getAnimation() {
        if (animation == null) {
            return -1;
        }
        return (animation.get(Randomizer.nextInt(animation.size()))).intValue();
    }

    public boolean isPVPDisabled() {
        return pvpDisabled;
    }

    public boolean isChargeSkill() {
        return chargeskill;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public boolean hasRequiredSkill() {
        return requiredSkill.size() > 0;
    }

    public int getPsdSkill(){
        return psdSkill;
    }
    public int getPsd(){
        return psd;
    }
    public String getPsdDamR(){
        return psdDamR;
    }

    public String getPsdtarget() {
        return targetPlus;
    }

    public List<Pair<String, Integer>> getRequiredSkills() {
        return requiredSkill;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getTrueMax() {
        return trueMax;
    }

    public boolean combatOrders() {
        return combatOrders;
    }

    public boolean canBeLearnedBy(int job) { //test
//        if (GameConstants.getBeginnerJob((short) (id / 10000)) == GameConstants.getBeginnerJob((short) job))
//            return true;
        int jid = job;
        int skillForJob = id / 10000;
        if (skillForJob == 2001) {
            return GameConstants.isEvan(job); //special exception for beginner -.-
        } else if (skillForJob == 0) {
            return GameConstants.isAdventurer(job); //special exception for beginner
        } else if (skillForJob == 1000) {
            return GameConstants.isKOC(job); //special exception for beginner
        } else if (skillForJob == 2000) {
            return GameConstants.isAran(job); //special exception for beginner
        } else if (skillForJob == 3000) {
            return GameConstants.isResistance(job); //special exception for beginner
        } else if (skillForJob == 1) {
            return GameConstants.isCannon(job); //special exception for beginner
        } else if (skillForJob == 3001) {
            return GameConstants.isDemonSlayer(job) || GameConstants.isDemonAvenger(job); //special exception for beginner
        } else if (skillForJob == 2002) {
            return GameConstants.isMercedes(job); //special exception for beginner
        } else if (skillForJob == 508) {
            return GameConstants.isJett(job); //special exception for beginner
        } else if (skillForJob == 2003) {
            return GameConstants.isPhantom(job); //special exception for beginner
        } else if (skillForJob == 5000) {
            return GameConstants.isMihile(job); //special exception for beginner
        } else if (skillForJob == 2004) {
            return GameConstants.isLuminous(job); //special exception for beginner
        } else if (skillForJob == 6000) {
            return GameConstants.isKaiser(job); //special exception for beginner
        } else if (skillForJob == 6001) {
            return GameConstants.isAngelicBuster(job); //special exception for beginner
        } else if (skillForJob == 3002) {
            return GameConstants.isXenon(job); //special exception for beginner
        }else if (skillForJob == 10000) {
            return GameConstants.isZero(job); //special exception for beginner
        } else if (jid / 100 != skillForJob / 100) { // wrong job
            return false;
        } else if (jid / 1000 != skillForJob / 1000) { // wrong job
            return false;
        } else if (GameConstants.isDemonAvenger(skillForJob) && !GameConstants.isDemonAvenger(job)) {
            return false;
        } else if (GameConstants.isXenon(skillForJob) && !GameConstants.isXenon(job)) {
            return false;
        } else if (GameConstants.isZero(skillForJob) && !GameConstants.isZero(job)) {
            return false;
            //} else if (GameConstants.isBeastTamer(skillForJob) && !GameConstants.isBeastTamer(job)) {
            //   return false;
        } else if (GameConstants.isAngelicBuster(skillForJob) && !GameConstants.isAngelicBuster(job)) {
            return false;
        } else if (GameConstants.isKaiser(skillForJob) && !GameConstants.isKaiser(job)) {
            return false;
        } else if (GameConstants.isMihile(skillForJob) && !GameConstants.isMihile(job)) {
            return false;
        } else if (GameConstants.isLuminous(skillForJob) && !GameConstants.isLuminous(job)) {
            return false;
        } else if (GameConstants.isPhantom(skillForJob) && !GameConstants.isPhantom(job)) {
            return false;
        } else if (GameConstants.isJett(skillForJob) && !GameConstants.isJett(job)) {
            return false;
        } else if (GameConstants.isCannon(skillForJob) && !GameConstants.isCannon(job)) {
            return false;
        } else if (GameConstants.isDemonSlayer(skillForJob) && !GameConstants.isDemonSlayer(job)) {
            return false;
        } else if (GameConstants.isAdventurer(skillForJob) && !GameConstants.isAdventurer(job)) {
            return false;
        } else if (GameConstants.isKOC(skillForJob) && !GameConstants.isKOC(job)) {
            return false;
        } else if (GameConstants.isAran(skillForJob) && !GameConstants.isAran(job)) {
            return false;
        } else if (GameConstants.isEvan(skillForJob) && !GameConstants.isEvan(job)) {
            return false;
        } else if (GameConstants.isMercedes(skillForJob) && !GameConstants.isMercedes(job)) {
            return false;
        } else if (GameConstants.isResistance(skillForJob) && !GameConstants.isResistance(job)) {
            return false;
        } else if ((job / 10) % 10 == 0 && (skillForJob / 10) % 10 > (job / 10) % 10) { // wrong 2nd job
            return false;
        } else if ((skillForJob / 10) % 10 != 0 && (skillForJob / 10) % 10 != (job / 10) % 10) { //wrong 2nd job
            return false;
        } else if (skillForJob % 10 > job % 10) { // wrong 3rd/4th job
            return false;
        }
        return true;
    }

    public boolean isTimeLimited() {
        return timeLimited;
    }

    public boolean isFourthJobSkill(int skillid) {
        switch (skillid / 10000) {
            case 112:
            case 122:
            case 132:
            case 212:
            case 222:
            case 232:
            case 312:
            case 322:
            case 412:
            case 422:
            case 512:
            case 522:
                return true;
        }
        return false;
    }

    public boolean isThirdJobSkill(int skillid) {
        switch (skillid / 10000) {
            case 111:
            case 121:
            case 131:
            case 211:
            case 221:
            case 231:
            case 311:
            case 321:
            case 411:
            case 421:
            case 511:
            case 521:
                return true;
        }
        return false;
    }

    public boolean isSecondJobSkill(int skillid) {
        switch (skillid / 10000) {
            case 110:
            case 120:
            case 130:
            case 210:
            case 220:
            case 230:
            case 310:
            case 320:
            case 410:
            case 420:
            case 510:
            case 520:
                return true;
        }
        return false;
    }

    public boolean isFourthJob() {
        switch (id) { // I guess imma make an sql table to store these, so that we could max them all out.
            case 3120011:
            case 3220010:
            case 4320005:
            case 4340010:
            case 5120012:
            case 5211009:
            case 5220014:
            case 5320007:
            case 5321006:
            case 5720008:
            case 21120011:
            case 22181004:
            case 23120011:
            case 23121008:
            case 33120010:
            case 33121005:
            case 1001008:
                return false;
        }
        if (isHyper()) {
            return true;
        }

        if ((this.id / 10000 == 2312) || (this.id / 10000 == 2712) || (this.id / 10000 == 6112) || (this.id / 10000 == 6512)) {
            return true;
        }
        if ((this.id == 24121009) || (this.id == 24121010)) {
            return true;
        }
        if ((this.id / 10000 == 3612) && (getMasterLevel() >= 10)) {
            return true;
        }
        if ((getMaxLevel() <= 15 && !invisible && getMasterLevel() <= 0)) {
            return false;
        }
        if (id / 10000 >= 2210 && id / 10000 < 3000) { //evan skill
            return ((id / 10000) % 10) >= 7 || getMasterLevel() > 0;
        }
        if (id / 10000 >= 430 && id / 10000 <= 434) { //db skill
            return ((id / 10000) % 10) == 4 || getMasterLevel() > 0;
        }
        if (id / 10000 >= 400 && id / 10000 <= 412) { //nl skill
            return ((id / 10000) % 10) == 4 || getMasterLevel() > 0;
        }

        return ((id / 10000) % 10) == 2 && id < 90000000 && !isBeginnerSkill();
    }

    public Element getElement() {
        return element;
    }

    public int getAnimationTime() {
        return animationTime;
    }

    public int getMasterLevel() {
        return masterLevel;
    }

    public int getDelay() {
        return delay;
    }

    public int getTamingMob() {
        return eventTamingMob;
    }

    public int getSkillTamingMob() {
        return eventTamingMob;
    }

    public boolean isBeginnerSkill() {
        int jobId = id / 10000;
        return GameConstants.isBeginnerJob(jobId);
    }

    public boolean isMagic() {
        return magic;
    }

    public boolean isHyper() {
        return hyper > 0;
    }

    public int getHyper() {
        return hyper;
    }

    public boolean isMovement() {
        return casterMove;
    }

    public boolean isPush() {
        return pushTarget;
    }

    public boolean isPull() {
        return pullTarget;
    }

    public static boolean isKeyDownSkill(int skillid) {
        switch(skillid) {
            case 1311011: // la mancha spear
            case 2221011: // freezing breath
            case 2221052: // lightning orb
            case 2321001: // big bang
            case 3101008: // covering fire
            case 3111013: // arrow blaster
            case 3121020: // hurricane
            case 4341002: // final cut
            case 5221004: // rapid fire
            case 5221022: // broadside
            case 5311002: // monkey wave
            case 60011216: // soul buster
            case 65121003: // soul resonance
            case 80001587: // airship lv. 1
            case 80001389: // ?
            case 80001390: // ?
            case 80001391: // ?
            case 80001392: // ?
            case 80001629: // ?
            case 80001836: // vanquisher's charm
            case 80001880: // liberate the rune of barrage
            case 80001887: // mille aiguilles
            case 95001001: // flying battle chair mount
            case 101110100: // wheel wind
            case 131001004: // let's roll!
            case 131001008: // sky jump
            case 142111010: // kinetic jaunt
                return true;
            default:
                return false;
        }
    }

    public static boolean isSuperNovaSkill(int skillid) {
        switch(skillid) {
            case 4221052: // shadow veil
            case 65121052: // supreme supernova
                return true;
            default:
                return false;
        }
    }

    public static boolean isRushBombSkill(int skillid) {
        switch(skillid) {
            case 2221012: // frozen orb
            case 5101012: // tornado uppercut
            case 12121001: // blazing extinction
            case 61111218: // wing beat
            case 101120200: // wind cutter
            case 101120203: // storm break
            case 101120205: // severe storm break
                return true;
            default:
                return false;
        }
    }

    public static boolean isScreenCenterAttackSkill(int skillid) {
        switch(skillid) {
            case 13121052: // monsoon
            case 80001431: // liberate the destructive rune
            case 100001283: // shadow rain
                return true;
            default:
                return false;
        }
    }

    public static boolean isAranFallingStopSkill(int skillid) {
        switch(skillid) {
            case 80001925: // smash swing
            case 80001926: // bomb punch
            case 80001927: // bomb punch
            case 80001936: // ?
            case 80001937: // ?
            case 80001938: // ?
                return true;
            default:
                return false;
        }
    }

    public static boolean isUseBulletMeleeAttack(int skillid) {
        return (skillid == 14121052 || (skillid >= 14111022 && skillid <= 14121003) || (skillid <= 14001027 && skillid >= 14000029));
    }

    public static boolean isNonConsumingBulletMeleeAttack(int skillid) {
        switch(skillid) {
            case 14000028: // shadow bat
            case 14000029: // shadow bat
            case 14121003: // dark omen
            case 14121052: // dominion
                return true;
            default:
                return false;
        }
    }

    public static boolean isEvanForceSkill(int skillid) {
        switch(skillid) {
            case 22110022: // dragon flash
            case 22110023: // dragon flash
            case 22141011: // thunder circle
            case 22171062: // earth circle
            case 22140022: // dragon dive
            case 80001894: // dragon flash
                return true;
            default:
                return false;
        }
    }

    public boolean isSpecialSkill() {
        int jobId = id / 10000;
        return jobId == 900 || jobId == 800 || jobId == 9000 || jobId == 9200 || jobId == 9201 || jobId == 9202 || jobId == 9203 || jobId == 9204;
    }

    public static boolean isUserCloneSummonableSkill(int nSkillID) {
        switch (nSkillID) {
            case 14121002:
            case 14121001:
            case 14111020:
            case 14111021:
            case 14101021:
            case 14101020:
            case 14001020:
            case 14111023:
            case 14111022:
                return true;
            default:
                return false;

        }
    }

    public static boolean isFlipAffectedAreaSkill(int nSkillID) {
        switch (nSkillID) {
            case 131001207:
            case 131001107:
            case 4121015:
            case 51120057:
            case 33111013:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int compare(Skill o1, Skill o2) {
        return (Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId())));
    }

    public int getVskill() {
        return vskill;
    }

    public int getBoostjob() {
        return boostjob;
    }

    public void setBoostjob(int boostjob) {
        this.boostjob = boostjob;
    }

    public int getvSkillIconID() {
        return vSkillIconID;
    }

    public void setvSkillIconID(int vSkillIconID) {
        this.vSkillIconID = vSkillIconID;
    }
}
