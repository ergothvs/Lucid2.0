package constants;

public class SkillConstants {

    public final static int ROCKET_RUSH = 37111005;

    public static boolean isSoulShear(int skillId) {
        return skillId == 42111002 || skillId == 800001835;
    }

    public static boolean isKinesisPsychicLockSkill(int skillId) {
        // just copying ida
        return skillId >= 142120000 || skillId <= 142120002 || skillId == 142120014
                || skillId == 142100010 || skillId == 142110003 || skillId == 142110015;
    }

    public static boolean isSuperNovaSkill(int skillId) {
        return skillId == 65121052 || skillId == 4221052;
    }

    public static boolean isScreenCenterAttackSkill(int skillId) {
        return skillId == 80001431 || skillId == 100001283 || skillId == 21121057 ||
                skillId == 13121052 || skillId == 14121052 || skillId == 15121052;
    }

    public static boolean isKeydownSkillRectMoveXY(int skillId){
        return skillId == 13111020;
    }

    public static boolean isShikigamiHaunting(int skillId) {
        // shikigami haunting, kinetic step,
        return skillId == 80001850 || skillId == 42001000 || (skillId > 42001004 && skillId <= 42001006)
                || skillId == 80011067;
    }
}
