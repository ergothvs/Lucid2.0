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
public class AranBuff extends AbstractBuffClass {

    public AranBuff() {
        buffs = new int[]{
            21001003, // Polearm Booster
            21101006, // Snow Charge
            21101005, // Combo Drain
            21111001, // Might
            21111009, // Combo Recharge
            21111012, // Maha Blessing
            21121007, // Combo Barrier
            21121000, // Maple Warrior
            21121054, // Unlimited Combo
            21121053, // Heroic Memories
        };
    }

    @Override
    public boolean containsJob(int job) {
        return GameConstants.isAran(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 21001003: // Polearm Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case 21101006: // Snow Charge
                eff.statups.put(CharacterTemporaryStat.WeaponCharge, eff.info.get(MapleStatInfo.x));
                break;
            case 21101005: // Combo Drain
                eff.statups.put(CharacterTemporaryStat.ComboDrain, eff.info.get(MapleStatInfo.x));
                break;
            case 21111001: // Might
                eff.statups.put(CharacterTemporaryStat.KnockBack, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.EPAD, eff.info.get(MapleStatInfo.epad));
                eff.statups.put(CharacterTemporaryStat.EPDD, eff.info.get(MapleStatInfo.epdd));
                break;
            case 21111009: // Combo Recharge
                eff.statups.put(CharacterTemporaryStat.ComboAbilityBuff, eff.info.get(MapleStatInfo.x));
                break;
            case 21111012: // Maha Blessing
                eff.statups.put(CharacterTemporaryStat.MAD, eff.info.get(MapleStatInfo.mad));
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.pad));
                break;
            case 21121007: // Combo Barrier
                eff.statups.put(CharacterTemporaryStat.ComboBarrier, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.MDD, eff.info.get(MapleStatInfo.mdd));
                eff.statups.put(CharacterTemporaryStat.PDD, eff.info.get(MapleStatInfo.pdd));
                break;
            case 21121000: // Maple Warrior
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case 21121054: // Unlimited Combo
                eff.statups.put(CharacterTemporaryStat.DEFAULT_BUFFSTAT, eff.info.get(MapleStatInfo.indieDamR));
                break;
            case 21121053: // Heroic Memories
                eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                break;
            default:
                //System.out.println("Aran skill not coded: " + skill);
                break;
        }
    }
}
