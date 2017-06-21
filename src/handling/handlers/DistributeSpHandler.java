package handling.handlers;

import client.*;
import constants.GameConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class DistributeSpHandler {

	@PacketHandler(opcode = RecvPacketOpcode.DISTRIBUTE_SP)
	public static void handle(MapleClient c, LittleEndianAccessor lea){
		MapleCharacter chr = c.getPlayer();
		lea.skip(4); //update tick
		int skillId = lea.readInt();
		int amount = lea.readInt();
		int remainingSp;
		boolean isBeginnerSkill = false;
		if(GameConstants.isBeginnerSkill(skillId)){
			remainingSp = getSpLeftByBeginnerSkill(chr, skillId);
			isBeginnerSkill = true;
		} else {
			remainingSp = chr.getRemainingSp(GameConstants.getSkillBookForSkill(skillId));
		}
		Skill skill = SkillFactory.getSkill(skillId);
		for (Pair<String, Integer> ski : skill.getRequiredSkills()) {
			if (ski.left.equals("level")) {
				if (chr.getLevel() < ski.right) {
					return;
				}
			} else {
				int left = Integer.parseInt(ski.left);
				if (chr.getSkillLevel(SkillFactory.getSkill(left)) < ski.right) {
					//AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to learn a skill without the required skill (" + skillid + ")");
					return;
				}
			}
		}
		final int maxlevel = skill.isFourthJob() ? chr.getMasterLevel(skill) : skill.getMaxLevel();
		final int curLevel = chr.getSkillLevel(skill);
		// Legacy code
		if (skill.isInvisible() && chr.getSkillLevel(skill) == 0) {
			if ((skill.isFourthJob() && chr.getMasterLevel(skill) == 0) || (!skill.isFourthJob() && maxlevel < 10 && !GameConstants.isDualBlade(chr.getJob()) && !isBeginnerSkill && chr.getMasterLevel(skill) <= 0)) {
				c.getSession().write(CWvsContext.enableActions());
				//AutobanManager.getInstance().addPoints(c, 1000, 0, "Illegal distribution of SP to invisible skills (" + skillid + ")");
				return;
			}
		}
		// End legacy code
		for (int i : GameConstants.blockedSkills) {
			if (skill.getId() == i) {
				c.getSession().write(CWvsContext.enableActions());
				chr.dropMessage(1, "This skill has been blocked and may not be added.");
				return;
			}
		}
		if ((remainingSp >= amount && curLevel + amount <= maxlevel) && skill.canBeLearnedBy(chr.getJob())) {
			if (!isBeginnerSkill) {
				final int skillbook = GameConstants.getSkillBookForSkill(skillId);
				chr.setRemainingSp(chr.getRemainingSp(skillbook) - amount, skillbook);
			}
			chr.updateSingleStat(MapleStat.AVAILABLESP, 0); // we don't care about the value here
			chr.changeSingleSkillLevel(skill, (byte) (curLevel + amount), chr.getMasterLevel(skill));
		} else {
			System.out.println("Skill errors!!");
			System.out.println("isbeginner " + isBeginnerSkill);
			System.out.println("canlearn " + skill.canBeLearnedBy(chr.getJob()));
			System.out.println("remainingsp " + remainingSp);
			System.out.println("amount " + amount);
			System.out.println("curlvl " + curLevel);
			System.out.println("maxlvl " + maxlevel);
			c.getSession().write(CWvsContext.enableActions());
		}
	}

	/**
	 * Magic surrounding the beginner skills' remaining sp. Handled seperately.
	 * @param chr
	 * @param skillId
	 * @return
	 */
	public static int getSpLeftByBeginnerSkill(MapleCharacter chr, int skillId){
		final boolean resistance = skillId / 10000 == 3000 || skillId / 10000 == 3001;
		final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(((skillId / 10000) * 10000) + 1000));
		final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(((skillId / 10000) * 10000) + 1001));
		final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(((skillId / 10000) * 10000) + (resistance ? 2 : 1002)));
		return Math.min((chr.getLevel() - 1), resistance ? 9 : 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
	}
}
