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
 * @author Maple
 */
public class XenonBuff extends AbstractBuffClass {
    
    public XenonBuff() {
        buffs = new int[]{
            36001002, //Circuit Surge
            36101002, //Perspective Shift
            36101003, //Efficiency Streamline
            36101004, //Xenon Booster
            36111006, //Manifest Projector
            36111008, // Emergency Resupply
            36111003, // Hybird Defenses
            36121003, // OOPArts Code
            36121004, // Offensive Matrix
            36121008, // Maple Warrior
            36121054,
        };
    }
    
    @Override
    public boolean containsJob(int job) {
        return GameConstants.isXenon(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 30021237:
               eff.statups.put(CharacterTemporaryStat.NaviFlying, Integer.valueOf(1));
                eff.info.put(MapleStatInfo.time, Integer.valueOf(180000));
                break;
            case 36001002: // Circuit Surge
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
            case 36101002: // Perspective Shift - dc temp to remove dc
                eff.info.put(MapleStatInfo.powerCon, Integer.valueOf(6));
                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.x));
                break;
            case 36101003: // Efficiency Streamline
               eff.statups.put(CharacterTemporaryStat.IndieMMPR, eff.info.get(MapleStatInfo.indieMmpR));
               eff.statups.put(CharacterTemporaryStat.IndieMHPR, eff.info.get(MapleStatInfo.indieMhpR));
               eff.info.put(MapleStatInfo.time, Integer.valueOf(180000));
                break; 
            case 36101004: // Xenon Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case 36111006: // Manifest Projector
                eff.statups.put(CharacterTemporaryStat.ShadowPartner, eff.info.get(MapleStatInfo.x));
                break;
            case 36111008: // Emergency Resupply
                eff.statups.put(CharacterTemporaryStat.SurplusSupply, eff.info.get(MapleStatInfo.x));
                break;
            case 36111003: // Hybrid Defenses
                eff.statups.put(CharacterTemporaryStat.HYBRID_DEFENSES, eff.info.get(MapleStatInfo.prop));
                eff.statups.put(CharacterTemporaryStat.DamAbsorbShield, eff.info.get(MapleStatInfo.z));
                break;
            case 36121003: // OOPArts Code IndieDamR
                eff.statups.put(CharacterTemporaryStat.BDR, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                break; 
            case 36121004: // Offensive Matrix
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, eff.info.get(MapleStatInfo.y));
                break;
            case 36121008: // Maple Warrior
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            default:
                // System.out.println("Unhandled Xenon Buff: " + skill);
                break;
        }
    }
}
