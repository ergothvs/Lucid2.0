
package server.buffs.buffclasses.cygnus;

import client.CharacterTemporaryStat;
import client.MonsterStatus;
import constants.GameConstants;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.AbstractBuffClass;


public class DawnWarriorBuff extends AbstractBuffClass {

    public DawnWarriorBuff() {
        buffs = new int[]{
            11001021, // Hand of Light
            11001022, // Soul Element
            11101023, // Divine Hand
            11101022, // Falling Moon
            11101003, // Rage
            11101024, // Soul Speed
            11101024, // Booster
            11111007, // Radiant Charge
            11111022, // Rising Sun
            11121012, // Equinox Cycle (Rising Sun)
            11121011, // Equinox Cycle (Falling Moon)
            11121005, // Equinox Cycle
            11111024, // Soul of the Guardian
            11121000, // Call of Cygnus
            11121053, //Glory of guardians
        };
    }
    
    @Override
    public boolean containsJob(int job) {
        return GameConstants.isDawnWarrior(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
               case 11101024: // booster
                      eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x) * 2);
                      break;
               case 11001021: //Hand of Light
                       eff.statups.put(CharacterTemporaryStat.ADD_AVOIDABILITY, 20);
                       eff.statups.put(CharacterTemporaryStat.ACCURACY_PERCENT, eff.info.get(MapleStatInfo.x));
                       eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                       break;
                case 11001022: // Soul Element
                       eff.statups.put(CharacterTemporaryStat.ElementSoul, eff.info.get(MapleStatInfo.prop));
                       eff.monsterStatus.put(MonsterStatus.STUN, 1);
                       break;
                case 11101023: // Divine Hand
                       eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                       break;
                case 11101022: //Falling Moon
                       eff.statups.put(CharacterTemporaryStat.SOLUNA_EFFECT, 0);
                       eff.statups.put(CharacterTemporaryStat.AttackCountX, eff.info.get(MapleStatInfo.x));
                       eff.statups.put(CharacterTemporaryStat.IndieCr, eff.info.get(MapleStatInfo.indieCr));
                       eff.info.put(MapleStatInfo.time, Integer.MAX_VALUE);
                       break;
                case 11111022: // Rising Sun
                       eff.statups.put(CharacterTemporaryStat.SOLUNA_EFFECT, 1);
                       eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                       eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.indieBooster));
                       eff.info.put(MapleStatInfo.time, Integer.MAX_VALUE);
                       break;
                case 11111007: // rad charge
                       eff.statups.put(CharacterTemporaryStat.WeaponCharge, eff.info.get(MapleStatInfo.x));
                       eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.z));
                       break;
               case 11111024: // soul of guardian
                       eff.statups.put(CharacterTemporaryStat.IncMaxHP, eff.info.get(MapleStatInfo.indieMhp));
                       eff.statups.put(CharacterTemporaryStat.IndieMAD, eff.info.get(MapleStatInfo.indiePdd));
                       eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePdd));
                       break;
                case 11121005: // Equinox Cycle
                      eff.statups.put(CharacterTemporaryStat.SOLUNA_EFFECT, 1); // should be level but smd
                      break;
                case 11121011: //Equinox Cycle (Falling Moon)
                       eff.statups.put(CharacterTemporaryStat.EQUINOX_STANCE, 11121012);
                       eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                       eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.indieBooster));
                       break;
                case 11121012: //Equinox Cycle (Rising Sun)
                       eff.statups.put(CharacterTemporaryStat.EQUINOX_STANCE, 11121011);
                       eff.statups.put(CharacterTemporaryStat.IndieCr, eff.info.get(MapleStatInfo.indieCr));
                       eff.statups.put(CharacterTemporaryStat.AttackCountX, eff.info.get(MapleStatInfo.x));
                       break;
                case 11121006: // soul pledge
                       eff.statups.put(CharacterTemporaryStat.IndieCr, eff.info.get(MapleStatInfo.indieCr));
                       eff.statups.put(CharacterTemporaryStat.STACK_ALLSTATS, eff.info.get(MapleStatInfo.indieAllStat));
                       eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.prop));
                       break;
                case 11121053: //Glory of the guardians
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            default:
                //System.out.println("Unhandled Buff: " + skill);
                break;
        }
    }
}
