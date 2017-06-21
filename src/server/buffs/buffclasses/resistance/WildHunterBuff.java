/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.buffs.buffclasses.resistance;

import client.CharacterTemporaryStat;
import constants.GameConstants;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Sunny
 */
public class WildHunterBuff extends AbstractBuffClass {
    
    public WildHunterBuff() {
        buffs = new int[]{
           33001001, // Jaguar Rider
           33101003, // Soul Arrow: Crossbow
           33101005, // Call of the Wild
           33101012, // Crossbow Booster
           33111007, // Feline Berserk
           33111009, // Concentrate
           33121004, // Sharp Eyes
           33121007, // Maple Warrior
           33121013, // Extended Magazine
           33121054, // Silent Rampage
           33121053, // For Liberty

        };
    }
    
    @Override
    public boolean containsJob(int job) {
        return GameConstants.isWildHunter(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 33101003:
                 eff.statups.put(CharacterTemporaryStat.SoulArrow, eff.info.get(MapleStatInfo.x));
                 break;
            case 33101005:// Call of the Wild
                eff.statups.put(CharacterTemporaryStat.HowlingMaxMP, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.HowlingCritical, eff.info.get(MapleStatInfo.y));
                eff.statups.put(CharacterTemporaryStat.TORNADO, eff.info.get(MapleStatInfo.z));
//                eff.statups.put(CharacterTemporaryStat.SATELLITESAFE_ABSORB, eff.info.get(MapleStatInfo.lt));
//                eff.statups.put(CharacterTemporaryStat.SoulArrow, eff.info.get(MapleStatInfo.rb));                
                break;
            case 33101012:// Crossbow Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x) * 2);
                break;
            case 33111007: // Feline Berserk
                eff.statups.put(CharacterTemporaryStat.Speed, eff.info.get(MapleStatInfo.z));
                eff.statups.put(CharacterTemporaryStat.BeastFormDamageUp, eff.info.get(MapleStatInfo.y));
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.indieBooster));
                break;
            case 33111009: // Concentrate
                eff.statups.put(CharacterTemporaryStat.EPAD, eff.info.get(MapleStatInfo.epad));
                eff.statups.put(CharacterTemporaryStat.Concentration, eff.info.get(MapleStatInfo.x));
                break;
            case 33121004: // Sharp Eyes
                 eff.statups.put(CharacterTemporaryStat.SharpEyes, (eff.info.get(MapleStatInfo.x) << 8) + eff.info.get(MapleStatInfo.criticaldamageMax));
                break;
            case 33121007: // Maple Warrior
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case 33121013: // Extended Magazine
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.IndieAllStat, eff.info.get(MapleStatInfo.indieAllStat));
                break;
            case 33121054:// Silent Rampage
                break;
            case 33121053:// For Liberty
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            default:
                // System.out.println("Unhandled WildHunter Buff: " + skill);
                break;
        }
    }
}
