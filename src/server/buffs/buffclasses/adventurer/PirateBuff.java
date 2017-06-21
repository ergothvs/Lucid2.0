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
 * @author Maple
 */
public class PirateBuff extends AbstractBuffClass {

    public PirateBuff() {
        buffs = new int[]{
            5101011, // Dark Clarity
            5701006, // Dark Clarity
            5101006, // Knuckle Booster
            5111007, // Roll Of The Dice
            5211007, // Roll Of The Dice
            5111010, // Admiral's Wings
            5121015, // Crossbones
            5121010, // Time Leap
            5121009, // Speed Infusion
            5121000, // Maple Warrior
            5221000, // Maple Warrior
            5321005, // Maple Warrior
            5721000, // Maple Warrior
            5121054, // Stimulating Conversation
            5121053, // Epic Adventure
            5221053, // Epic Adventure
            5321053, // Epic Adventure
            5721053, // Epic Adventure
            5201012, // Scurvy Summons
            5201003, // Gun Booster
            5201008, // Infinity Blast
            5211009, // Cross Cut Blast
            5221018, // Jolly Roger
            5221021, // Quickdraw
            5221054, // Whaler's Potion
            5301002, // Cannon Booster
            5301003, // Monkey Magic
            5311004, // Barrel Roulette
            5311005, // Luck of the Die
            5321010, // Pirate's Spirit
            5321054, // Buckshot
            5701005, // Gun Booster
            5711011, // Roll of the Dice
            5721009, // Relentless
            5721054, // Bionic Maximizer
            5721052,
            
            
        };
    }
    
    @Override
    public boolean containsJob(int job) {
        return GameConstants.isAdventurer(job) && job / 100 == 5;
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 5101011: // Dark Clarity
            case 5701006: // Dark Clarity
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
            case 5101006: // Knuckle Booster
            case 5201003: // Gun Booster
            case 5301002: // Cannon Booster
            case 5701005: // Gun Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case 5111007: // Roll Of The Dice
            case 5211007: // Roll Of The Dice
            case 5311005: // Luck of the Die
            case 5711011: // Roll of the Dice
                eff.statups.put(CharacterTemporaryStat.Dice, 1);
                break;
            case 5111010: // Admiral's Wings
                //TODO
                break;
            case 5121015: // Crossbones
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.x));
                break;
            case 5121009: // Speed Infusion
                eff.statups.put(CharacterTemporaryStat.Speed, 0);
                break;
            case 5121054: // Stimulating Conversation
                //TODO
                break;
            case 5221018: // Jolly Roger
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.y));//or x?
                eff.statups.put(CharacterTemporaryStat.DAMAGE_RATE, eff.info.get(MapleStatInfo.damR));
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.z));
                eff.statups.put(CharacterTemporaryStat.EVA, eff.info.get(MapleStatInfo.eva));
                break;
            case 5201008: // Infinity Blast
                eff.statups.put(CharacterTemporaryStat.NoBulletConsume, eff.info.get(MapleStatInfo.x));
                break;
            case 5211009: // Cross Cut Blast
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
            case 5221054: // Whaler's Potion
		        eff.statups.put(CharacterTemporaryStat.MaxHP, eff.info.get(MapleStatInfo.x)); //Max HP: +40%                  
		        eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.y));//Status Ailment and Elemental Resistance: +15%
		        eff.statups.put(CharacterTemporaryStat.Invincible, eff.info.get(MapleStatInfo.w)); //Damage Intake: -15%  
            break;
            case 5311004: // barrel roulette
                eff.statups.put(CharacterTemporaryStat.RepeatEffect, 0);
                break;
            case 5321010: // Pirate's Spirit
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.prop));
                break;
            case 5321054: // Buckshot
                //TODO
                break;
            case 5721009: // Relentless
                //TODO
                break;
            case 5721054: // Bionic Maximizer
                eff.statups.put(CharacterTemporaryStat.IndieMHPR, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.STATUS_RESIST_TWO, eff.info.get(MapleStatInfo.v));
                eff.statups.put(CharacterTemporaryStat.ELEMENT_RESIST_TWO, eff.info.get(MapleStatInfo.w));
                eff.statups.put(CharacterTemporaryStat.DAMAGE_RESIST, eff.info.get(MapleStatInfo.y));
                break;
            case 5721052:
                eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.x));
                break;
            case 5121000: // Maple Warrior
            case 5221000: // Maple Warrior
            case 5321005: // Maple Warrior
            case 5721000: // Maple Warrior
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case 5121053: // Epic Adventure
            case 5221053: // Epic Adventure
            case 5321053: // Epic Adventure
            case 5721053: // Epic Adventure
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
                case 5221021: // quickdraw
                    break;
            default:
                //System.out.println("Pirate skill not coded: " + skill);
                break;
        }
    }
}
