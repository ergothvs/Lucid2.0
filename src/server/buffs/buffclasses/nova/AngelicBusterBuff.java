/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.buffs.buffclasses.nova;

import client.CharacterTemporaryStat;
import constants.GameConstants;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Maple
 */
public class AngelicBusterBuff extends AbstractBuffClass {

    public AngelicBusterBuff() {
        buffs = new int[]{
            60011219, // Terms and Conditions
            65001002, //Melody Cross
            65101002, //Power Transfer
            65111003, //Dragon Whistle
            65111004, //Iron Blossom
            65121004, //Star Gazer
            65121009, //Nova Warrior
            65121053, //Final Contract
            65121054, //Pretty Exaltation
        };
    }
    
    @Override
    public boolean containsJob(int job) {
        return GameConstants.isAngelicBuster(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 80001155:
            case 60011219: // terms and conditions
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                break;
            case 65001002: // melody cross
                eff.statups.put(CharacterTemporaryStat.IndieACC, eff.info.get(MapleStatInfo.x));
                break;
            case 65101002: // power transfer
                eff.statups.put(CharacterTemporaryStat.DamAbsorbShield, eff.info.get(MapleStatInfo.x));
                break;
            case 65111004: // iron blossom
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.prop));
                break;
            case 65121004: // star gazer
                eff.statups.put(CharacterTemporaryStat.CRIT_DAMAGE, eff.info.get(MapleStatInfo.x));
                break;
            case 65121009: // nova warrior
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case 65121053: // Final Contract - TODO
//                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.x));
//                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.y));
//                eff.statups.put(CharacterTemporaryStat.ABNORMAL_STATUS_R, eff.info.get(MapleStatInfo.asrR));
//                eff.statups.put(CharacterTemporaryStat.ELEMENTAL_STATUS_R, eff.info.get(MapleStatInfo.terR));																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																							
                    break;
            case 65121054: // Pretty Exaltation - TODO
                //eff.statups.put(CharacterTemporaryStat.RECHARGE, eff.info.get(MapleStatInfo.x));
                break;
            default:
                //System.out.println("Unhandled Buff: " + skill);
                break;
        }
    }
}
