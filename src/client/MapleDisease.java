package client;

import java.io.Serializable;

import handling.Buffstat;
import tools.Randomizer;

public enum MapleDisease implements Serializable, Buffstat {

    SEAL(CharacterTemporaryStat.Seal, 120),
    DARKNESS(CharacterTemporaryStat.Darkness, 121),
    WEAKEN(CharacterTemporaryStat.Weakness, 122),
    STUN(CharacterTemporaryStat.Stun, 123),
    CURSE(CharacterTemporaryStat.Curse, 124),
    POISON(CharacterTemporaryStat.Poison, 125),
    SLOW(CharacterTemporaryStat.SLOWNESS, 126),
    SEDUCE(CharacterTemporaryStat.Attract, 128),
    REVERSE_DIRECTION(CharacterTemporaryStat.ReverseInput, 132),
    ZOMBIFY(CharacterTemporaryStat.Undead, 133),
    POTION(CharacterTemporaryStat.POTION_CURSE, 134),
    SHADOW(CharacterTemporaryStat.SHADOW, 135), //receiving damage/moving
    BLIND(CharacterTemporaryStat.BLINDNESS, 136),
    FREEZE(CharacterTemporaryStat.Frozen, 137),
    DISABLE_POTENTIAL(CharacterTemporaryStat.DispelItemOption, 138),
    MORPH(CharacterTemporaryStat.Morph, 172),
    TORNADO(CharacterTemporaryStat.TORNADO_CURSE, 173),
    FLAG(CharacterTemporaryStat.PVP_FLAG, 799); // PVP - Capture the Flag
    // 127 = 1 snow?
    // 129 = turn?
    // 131 = poison also, without msg
    // 133, become undead?..50% recovery?
    // 0x100 is disable skill except buff
    private static final long serialVersionUID = 0L;
    private final int buffstat;
    private final int first;
    private final int disease;

    private MapleDisease(CharacterTemporaryStat buffstat, int disease) {
        this.buffstat = buffstat.getValue();
        this.first = buffstat.getPosition();
        this.disease = disease;
    }

    @Override
    public int getPosition() {
        return first;
    }

    @Override
    public int getValue() {
        return buffstat;
    }

    public int getDisease() {
        return disease;
    }

    public static MapleDisease getRandom() {
        while (true) {
            for (MapleDisease dis : MapleDisease.values()) {
                if (Randomizer.nextInt(MapleDisease.values().length) == 0) {
                    return dis;
                }
            }
        }
    }

    public static MapleDisease getBySkill(final int skill) {
        for (MapleDisease d : MapleDisease.values()) {
            if (d.getDisease() == skill) {
                return d;
            }
        }
        return null;
    }
}
