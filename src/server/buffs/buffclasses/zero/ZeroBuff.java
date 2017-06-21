/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.buffs.buffclasses.zero;

import client.CharacterTemporaryStat;
import constants.GameConstants;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Itzik
 */
public class ZeroBuff extends AbstractBuffClass {

    public ZeroBuff() { //since only beginner job has buffs we put them in first job buffs
        buffs = new int[]{
            100001005, // Temple Recall
            100001263, // Divine Force
            100001264, // Divine Speed
            100001268, // Rhinne's Protection
            100001269,
            100001270,
            100001272};
    }
    
    @Override
    public boolean containsJob(int job) {
        return GameConstants.isZero(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        // If this initial check and the corresponding arrays are removed, 
        // there should not be any impact (i.e., it will keep its functionality). 
        if (!containsSkill(skill)) {
            return;
        }

        switch (skill) {
            case 100001005: //Focused Time
                eff.statups.put(CharacterTemporaryStat.ATTACK, eff.info.get(MapleStatInfo.x));
                break;
            case 100001268: // Rhinne's Protection
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case 100001263: // Divine Force
                eff.statups.put(CharacterTemporaryStat.DIVINE_FORCE_AURA, 1);
                eff.statups.put(CharacterTemporaryStat.STATUS_RESIST_TWO, eff.info.get(MapleStatInfo.indieTerR));
                eff.statups.put(CharacterTemporaryStat.PARTY_STANCE, eff.info.get(MapleStatInfo.indieAsrR));
                eff.statups.put(CharacterTemporaryStat.MDEF_BOOST, eff.info.get(MapleStatInfo.indieMdd));
                eff.statups.put(CharacterTemporaryStat.WDEF_BOOST, eff.info.get(MapleStatInfo.indiePdd));
                eff.statups.put(CharacterTemporaryStat.IndieMAD, eff.info.get(MapleStatInfo.indiePad));
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indieMad));
                break;
            case 100001264: // Divine Speed
                eff.statups.put(CharacterTemporaryStat.DIVINE_SPEED_AURA, 1);
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.indieBooster));
                eff.statups.put(CharacterTemporaryStat.IndieACC, eff.info.get(MapleStatInfo.indieAcc));
                eff.statups.put(CharacterTemporaryStat.IndieEVA, eff.info.get(MapleStatInfo.indieEva));
                eff.statups.put(CharacterTemporaryStat.IndieJump, eff.info.get(MapleStatInfo.indieJump));
                eff.statups.put(CharacterTemporaryStat.IndieSpeed, eff.info.get(MapleStatInfo.indieSpeed));
                break;
            default:
                //System.out.println("Unhandled Buff: " + skill);
                break;
        }
    }
}
