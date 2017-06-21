package server.buffs.buffclasses.sengoku;

import client.CharacterTemporaryStat;
import constants.GameConstants;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Charmander
 */
public class KannaBuff extends AbstractBuffClass  {

    public KannaBuff() {
        buffs = new int[]{
            42101023, // Breath of the Unseen - HiddenBuff
            42101021, // Foxfire - HiddenBuff
            42121021, // Foxfire - HiddenBuff
            42101001, // Shikigami Charm - HiddenBuff
            42101022, // Haku's Blessing - HiddenBuff
            
            42101002, // Haku Reborn
            42101003, // Radiant Peacock
            42111004, // Blossom Barrier - mist
            42121005, // Bellflower Barrier - mist
            42121006, // Maple Warrior
            42121054, // Blackhearted Curse
            42121053, // Princess's Vow
            
            42101004, // Burning Shikigami Haunting
            42111006, // Frozen Shikigami Haunting
            42121008, // Mighty Shikigami Haunting
            42111003, // Kishin Shoukan
            
        };
    }
    
    @Override
    public boolean containsJob(int job) {
        return GameConstants.isKanna(job);
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 42101023: // Breath of the Unseen - HiddenBuff?
                eff.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.prop));
                break;
            case 42101021: // Foxfire -HiddenBuff
                eff.statups.put(CharacterTemporaryStat.FOX_FIRE, eff.info.get(MapleStatInfo.x));
             case 42121021:// Foxfire
             case 42101001:// Shikigami Charm
             case 42100010:
                 eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                 break;
             case 42101022: // Haku's Blessing
                 eff.statups.put(CharacterTemporaryStat.HAKU_BLESS, eff.info.get(MapleStatInfo.x));
                 eff.statups.put(CharacterTemporaryStat.WDEF_BOOST, eff.info.get(MapleStatInfo.indiePdd));
                 eff.statups.put(CharacterTemporaryStat.MDEF_BOOST, eff.info.get(MapleStatInfo.indieMdd));
                 break;
            case 42101002: // Haku Reborn
                eff.statups.put(CharacterTemporaryStat.HAKU_REBORN, Integer.valueOf(1));
                break;
            case 42101003: // Radiant Peacock
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            
            case 42121006: // Maple Warrior
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case 42121054: // Blackhearted Curse
                //TODO
                break;
            case 42121053: // Princess's Vow
                eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                break;
            case 42101004: // Burning Shikigami Haunting
            case 42111006: // Frozen Shikigami Haunting
            case 42121008: // Mighty Shikigami Haunting
                    eff.statups.put(CharacterTemporaryStat.SHIKIGAMI, Integer.valueOf(-eff.info.get(MapleStatInfo.x).intValue()));
                    break;
            case 42111003: // Kishin Shoukan
                eff.info.put(MapleStatInfo.time, Integer.valueOf(60000));
                eff.statups.put(CharacterTemporaryStat.SUMMON, Integer.valueOf(1));
                break;
            default:
                //System.out.println("Kanna skill not coded: " + skill);
                break;
        }
    }
}
