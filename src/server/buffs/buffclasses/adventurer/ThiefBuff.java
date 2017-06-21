/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.buffs.buffclasses.adventurer;

import client.CharacterTemporaryStat;
import constants.GameConstants;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Itzik
 */
public class ThiefBuff extends AbstractBuffClass {

    public ThiefBuff() {
        buffs = new int[]{
            4001003, //Dark Sight
            4001005, //Haste
            4101003, //Claw Booster
            4201002, //Dagger Booster
            4201009, //Channel Karma
            4201011, //Meso Guard
            4111002, //Shadow Partner
            4111009, //Shadow Star
            4211003, //Pick Pocket
            4211008, //Shadow Partner
            4121000, //Maple Warrior     
            4121014, //Dark Harmony
            4221000, //Maple Warrior     
            4221013, //Shadow Instinct
            4301003, // Self haste
            4311005, // Channel Karma
            4311009, // Katara Booster
            4331002, // Mirror Image
            4341000, // Maple Warrior
            4341007, // Thorns
            4341006, // Mirrored Target
            4341054, // Blade Clone
            4341053, // Epic Adventure
            4121053, //Epic Adventure
            4121054, //Bleed Dart
            4220015, //prime critical
            4221053, //Epic Adventure
            4221054, //Flip of the Coin
        };
    }
    
    @Override
    public boolean containsJob(int job) {
        return GameConstants.isAdventurer(job) && job / 100 == 4;
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 4001005: //Haste
                break;
            case 4301003: // Self haste
                eff.statups.put(CharacterTemporaryStat.Jump, eff.info.get(MapleStatInfo.jump));
                eff.statups.put(CharacterTemporaryStat.Speed, eff.info.get(MapleStatInfo.speed));
                break;
            case 4001003: //Dark Sight
                eff.statups.put(CharacterTemporaryStat.DarkSight, eff.info.get(MapleStatInfo.x));
                break;
            case 4101003: //Claw Booster
            case 4201002: //Dagger Booster
            case 4311009: // Katara Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case 4201011: //Meso Guard
                eff.statups.put(CharacterTemporaryStat.MesoGuard, eff.info.get(MapleStatInfo.x));
                break;
            case 4201009: //Channel Karma
            case 4311005: // Channel Karma
                 eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.pad));
                break;
            case 4211003: //Pick Pocket
                eff.info.put(MapleStatInfo.time, 2100000000);
            //    eff.statups.put(CharacterTemporaryStat.PickPocket, eff.info.get(MapleStatInfo.x));
                break;
            case 4111002: //Shadow Partner
            case 4211008: //Shadow Partner
            case 4331002: // Mirror Image
                eff.statups.put(CharacterTemporaryStat.ShadowPartner, eff.info.get(MapleStatInfo.x));
                break;
            case 4111009: //Shadow Star
                eff.statups.put(CharacterTemporaryStat.NoBulletConsume, 0);
                break;
            case 4121014: //Dark Harmony
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));//test - works without
                break;
            case 4341007: // Thorns
                eff.statups.put(CharacterTemporaryStat.Stance, (int) eff.info.get(MapleStatInfo.prop));
                eff.statups.put(CharacterTemporaryStat.EPAD, (int) eff.info.get(MapleStatInfo.epad));
                break;
            case 4221013: //Shadow Instinct
                break;
            case 4121000: //Maple Warrior 
            case 4221000: //Maple Warrior 
            case 4341000: // MW
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case 4121054: //Bleed Dart
                break;
            case 4220015: //prime critical
            	eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;    
            case 4341054: // Blade Clone
                eff.statups.put(CharacterTemporaryStat.IndiePADR, eff.info.get(MapleStatInfo.damage));
                eff.statups.put(CharacterTemporaryStat.FinalAttackProp, Integer.valueOf(1));
                break;
            case 4221054: // flip of the coin
            	eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
            	eff.statups.put(CharacterTemporaryStat.IndieCr, eff.info.get(MapleStatInfo.x));
                break;
            case 4121053: //Epic Adventure
            case 4221053: //Epic Adventure
            case 4341053: //Epic Adventure
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            default:
               // System.out.println("Thief skill not coded: " + skill);
                break;
        }
    }
}
