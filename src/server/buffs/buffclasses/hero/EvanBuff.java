/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.buffs.buffclasses.hero;

import client.CharacterTemporaryStat;
import constants.GameConstants;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Charmander
 */
public class EvanBuff extends AbstractBuffClass  {

    public EvanBuff() {
        buffs = new int[]{
            22111001, // Magic Guard
            22131001, // Magic Shield
            22131002, // Elemental Decrease
            22141002, // Magic Booster
            22151003, // Magic Resistance
            22161004, // Onyx Shroud
            22171000, // Maple Warrior
            22181003, // Soul Stone
            22181004, // Onyx Will
            22181000, // Blessing of the Onyx
            22171054, // Frenzied Soul
            22171053, // Heroic Memories
        };
    }
    
    @Override
    public boolean containsJob(int job) {
        return GameConstants.isEvan(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 22111001: // Magic Guard
                eff.statups.put(CharacterTemporaryStat.MagicGuard, eff.info.get(MapleStatInfo.x));
                break;
            case 22131001: // Magic Shield
                eff.statups.put(CharacterTemporaryStat.MagicShield, eff.info.get(MapleStatInfo.x));
                break;
            case 22131002: // Elemental Decrease
                eff.statups.put(CharacterTemporaryStat.Slow, eff.info.get(MapleStatInfo.x));
                break;
            case 22141002: // Magic Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x) * 2);
                break;
            case 22151003: // Magic Resistance
                eff.statups.put(CharacterTemporaryStat.MagicResistance, eff.info.get(MapleStatInfo.x));
                break;
            case 22161004: // Onyx Shroud
                eff.statups.put(CharacterTemporaryStat.OnyxDivineProtection, eff.info.get(MapleStatInfo.x));
                break;
            case 322171000: // Maple Warrior
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case 22181003: // Soul Stone
                eff.statups.put(CharacterTemporaryStat.SoulStone, 1);
                break;
            case 22181004: // Onyx Will
                eff.statups.put(CharacterTemporaryStat.ONYX_WILL, eff.info.get(MapleStatInfo.damage));
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.prop));
                break;
            case 22181000: // Blessing of the Onyx
                eff.statups.put(CharacterTemporaryStat.EMAD, eff.info.get(MapleStatInfo.emad));
                eff.statups.put(CharacterTemporaryStat.EPDD, eff.info.get(MapleStatInfo.epdd));
            //    eff.statups.put(CharacterTemporaryStat.EMDD, eff.info.get(MapleStatInfo.emdd));
                break;
            case 22171054: // Frenzied Soul
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.OnyxDivineProtection, eff.info.get(MapleStatInfo.x));//guessed???
                break;
            case 22171053: // Heroic Memories
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            default:
                //System.out.println("Evan skill not coded: " + skill);
                break;
        }
    }
}
