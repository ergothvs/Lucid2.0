package handling.handlers;

import java.lang.ref.WeakReference;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import constants.MapConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.ChannelServer;
import handling.channel.handler.AttackInfo;
import handling.channel.handler.AttackType;
import handling.channel.handler.DamageParse;
import server.MapleStatEffect;
import server.Timer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.JobPacket;

public class MagicAttackHandler {

	@PacketHandler(opcode = RecvPacketOpcode.MAGIC_ATTACK)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {

		if (c.getPlayer() == null || c.getPlayer().hasBlockedInventory() || c.getPlayer().getMap() == null) {
			return;
		}
		
		AttackInfo attack = DamageParse.parseMagicAttack(lea);
		if (attack == null) {
			c.getSession().write(CWvsContext.enableActions());
			return;
		}
		
		Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(attack.skillid));
		
		if (skill == null || (GameConstants.isAngel(attack.skillid) && c.getPlayer().getStat().equippedSummon % 10000 != attack.skillid % 10000)) {
			c.getSession().write(CWvsContext.enableActions());
			return;
		}
		
		int skillLevel = c.getPlayer().getTotalSkillLevel(skill);
		
		MapleStatEffect effect = attack.getAttackEffect(c.getPlayer(), skillLevel, skill);
		if (effect == null) {
			return;
		}
		
		if (skill.getId() >= 27100000 && skill.getId() < 27120400 && attack.getTargets() > 0 && c.getPlayer().getLuminousState() < 20040000) {
			//chr.changeSkillLevel(SkillFactory.getSkill(20040216), (byte) 1, (byte) 1);
			//chr.changeSkillLevel(SkillFactory.getSkill(20040217), (byte) 1, (byte) 1);
			//chr.changeSkillLevel(SkillFactory.getSkill(20040220), (byte) 1, (byte) 1);
			//chr.changeSkillLevel(SkillFactory.getSkill(20041239), (byte) 1, (byte) 1);
			c.getPlayer().setLuminousState(GameConstants.getLuminousSkillMode(skill.getId()));
			c.getSession().write(JobPacket.LuminousPacket.giveLuminousState(GameConstants.getLuminousSkillMode(skill.getId()), c.getPlayer().getLightGauge(), c.getPlayer().getDarkGauge(), 10000));
			SkillFactory.getSkill(GameConstants.getLuminousSkillMode(skill.getId())).getEffect(1).applyTo(c.getPlayer());
		}
		
		attack = DamageParse.ModifyAttackCrit(attack, c.getPlayer(), 3, effect);
		
		if (MapConstants.isEventMap(c.getPlayer().getMapId())) {
			for (MapleEventType t : MapleEventType.values()) {
				MapleEvent e = ChannelServer.getInstance(c.getPlayer().getClient().getChannel()).getEvent(t);
				if ((e.isRunning()) && (!c.getPlayer().isGM())) {
					for (int i : e.getType().mapids) {
						if (c.getPlayer().getMapId() == i) {
							c.getPlayer().dropMessage(5, "You may not use that here.");
							return;
						}
					}
				}
			}
		}
		
		double maxdamage = c.getPlayer().getStat().getCurrentMaxBaseDamage() * (effect.getDamage() + c.getPlayer().getStat().getDamageIncrease(attack.skillid)) / 100.0D;
		
		if (GameConstants.isPyramidSkill(attack.skillid)) {
			maxdamage = 1.0D;
		} else if ((GameConstants.isBeginnerJob(skill.getId() / 10000)) && (skill.getId() % 10000 == 1000)) {
			maxdamage = 40.0D;
		}
		
		if (effect.getCooldown(c.getPlayer()) > 0 && !c.getPlayer().isGM()) {
			if (c.getPlayer().skillisCooling(attack.skillid)) {
				c.getSession().write(CWvsContext.enableActions());
				return;
			}
			c.getSession().write(CField.skillCooldown(attack.skillid, effect.getCooldown(c.getPlayer())));
			c.getPlayer().addCooldown(attack.skillid, System.currentTimeMillis(), effect.getCooldown(c.getPlayer()) * 1000);
		}
		
		c.getPlayer().checkFollow();
		
		byte[] packet = CField.magicAttack(c.getPlayer().getId(), attack.nMobCount, attack.skillid, skillLevel, attack.display, attack.speed, attack.allDamage, attack.charge, c.getPlayer().getLevel(), attack.unk);
		if (!c.getPlayer().isHidden()) {
			c.getPlayer().getMap().broadcastMessage(c.getPlayer(), packet, c.getPlayer().getTruePosition());
		} else {
			c.getPlayer().getMap().broadcastGMMessage(c.getPlayer(), packet, false);
		}
		
		DamageParse.applyAttackMagic(attack, skill, c.getPlayer(), effect, maxdamage);
		
		WeakReference<MapleCharacter>[] clones = c.getPlayer().getClones();
		for (int i = 0; i < clones.length; i++) {
			if (clones[i].get() != null) {
				final MapleCharacter clone = clones[i].get();
				final Skill skil2 = skill;
				final MapleStatEffect eff2 = effect;
				final double maxd = maxdamage;
				final int skillLevel2 = skillLevel;
				final AttackInfo attack2 = DamageParse.DivideAttack(attack, c.getPlayer().isGM() ? 1 : 4);
				Timer.CloneTimer.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						byte [] packet = CField.magicAttack(clone.getId(), attack2.nMobCount, attack2.skillid, skillLevel2, attack2.display, attack2.speed, attack2.allDamage, attack2.charge, clone.getLevel(), attack2.unk);
						if (!clone.isHidden()) {
							clone.getMap().broadcastMessage(packet);
						} else {
							clone.getMap().broadcastGMMessage(clone, packet, false);
						}
						DamageParse.applyAttackMagic(attack2, skil2, c.getPlayer(), eff2, maxd);
					}
				}, 500 * i + 500);
			}
		}
		
		int bulletCount = 1; //This isn't even being used anywhere
		switch (attack.skillid) {
		case 27101100: // Sylvan Lance
		case 27101202: // Pressure Void
		case 27111100: // Spectral Light
		case 27111202: // Moonlight Spear
		case 27121100: // Reflection
		case 27001100:
		case 27121202: // Apocalypse
		case 2121006: // Paralyze
		case 2221003: //
		case 2221006: // Chain Lightning
		case 2221007: // Blizzard
		case 2221012: // Frozen Orb
		case 2111003: // Poison Mist
		case 2121003: // Myst Eruption
		case 22181002: // Dark Fog
		case 2321054:
		case 27121303:
		case 27111303:
		case 36121013:
		//case 36101009:
		//case 36111010:
			bulletCount = effect.getAttackCount();
			DamageParse.applyAttack(attack, skill, c.getPlayer(), skillLevel, maxdamage, effect, AttackType.RANGED);
			break;
		default:
			DamageParse.applyAttackMagic(attack, skill, c.getPlayer(), effect, maxdamage);
			break;
		}
	}
}
