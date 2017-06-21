package handling.handlers;

import java.lang.ref.WeakReference;

import client.CharacterTemporaryStat;
import client.MapleCharacter;
import client.MapleClient;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.MapConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.ChannelServer;
import handling.channel.handler.AttackInfo;
import handling.channel.handler.AttackType;
import handling.channel.handler.DamageParse;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Timer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleMonster;
import tools.AttackPair;
import tools.Randomizer;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.JobPacket;
import tools.packet.JobPacket.AngelicPacket;

public class RangedAttackHandler {

	@PacketHandler(opcode = RecvPacketOpcode.RANGED_ATTACK)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		if (c.getPlayer() == null || c.getPlayer().hasBlockedInventory() || c.getPlayer().getMap() == null) {
			return;
		}
		AttackInfo attack = DamageParse.parseRangedAttack(lea, c);
		if(attack.position == null) {
			attack.position = c.getPlayer().getTruePosition();
		}
		if (attack == null) {
			c.getSession().write(CWvsContext.enableActions());
			return;
		}
		int bulletCount = 1;
		int skillLevel = 0;
		MapleStatEffect effect = null;
		Skill skill = null;
		boolean AOE = attack.skillid == 4111004;
		boolean noBullet = (c.getPlayer().getJob() >= 300 && c.getPlayer().getJob() <= 322)
				|| (c.getPlayer().getJob() >= 3500 && c.getPlayer().getJob() <= 3512) || GameConstants.isCannon(c.getPlayer().getJob())
				|| GameConstants.isXenon(c.getPlayer().getJob()) || GameConstants.isJett(c.getPlayer().getJob())
				|| GameConstants.isPhantom(c.getPlayer().getJob()) || GameConstants.isMercedes(c.getPlayer().getJob())
				|| GameConstants.isZero(c.getPlayer().getJob());
		if (attack.skillid != 0) {
			skill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(attack.skillid));
			if ((skill == null) || ((GameConstants.isAngel(attack.skillid))
					&& (c.getPlayer().getStat().equippedSummon % 10000 != attack.skillid % 10000))) {
				c.getSession().write(CWvsContext.enableActions());
				return;
			}
			skillLevel = c.getPlayer().getTotalSkillLevel(skill);
			effect = attack.getAttackEffect(c.getPlayer(), skillLevel, skill);
			if (effect == null) {
				return;
			}
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
			if (GameConstants.isAngelicBuster(c.getPlayer().getJob())) {
				int Recharge = effect.getOnActive();
				if (Recharge > -1) {
					if (Randomizer.isSuccess(Recharge)) {
						c.getSession().write(AngelicPacket.unlockSkill());
						c.getSession().write(AngelicPacket.showRechargeEffect());
					} else {
						if (c.getPlayer().isGM()) {
							c.getSession().write(AngelicPacket.unlockSkill());
							// c.getSession().write(AngelicPacket.showRechargeEffect());
						} else {
							c.getSession().write(AngelicPacket.lockSkill(attack.skillid));
						}
					}
				} else {
					if (c.getPlayer().isGM()) {
						c.getSession().write(AngelicPacket.unlockSkill());
						// c.getSession().write(AngelicPacket.showRechargeEffect());
					} else {
						c.getSession().write(AngelicPacket.lockSkill(attack.skillid));
					}
				}
			}
			if (GameConstants.isWindArcher(c.getPlayer().getJob())) {
				int percent = 0, count = 0, skillid = 0, type = 0;
				if (c.getPlayer().getSkillLevel(SkillFactory.getSkill(13120003)) > 0) {
					if (Randomizer.nextInt(100) < 85) {
						skillid = 13120003;
						type = 1;
					} else {
						skillid = 13120010;
						type = 1;
					}
					count = Randomizer.rand(1, 5);
					percent = 20;
				} else if (c.getPlayer().getSkillLevel(SkillFactory.getSkill(13110022)) > 0) {
					if (Randomizer.nextInt(100) < 90) {
						skillid = 13110022;
						type = 1;
					} else {
						skillid = 13110027;
						type = 1;
					}
					count = Randomizer.rand(1, 4);
					percent = 10;
				} else if (c.getPlayer().getSkillLevel(SkillFactory.getSkill(13100022)) > 0) {
					if (Randomizer.nextInt(100) < 95) {
						skillid = 13100022;
						type = 1;
					} else {
						skillid = 13100027;
						type = 1;
					}
					count = Randomizer.rand(1, 3);
					percent = 5;
				}
				for (AttackPair at : attack.allDamage) {

					MapleMonster mob = c.getPlayer().getMap().getMonsterByOid(at.objectid);
					if (Randomizer.nextInt(100) < percent) {
						if (mob != null) {
							c.getPlayer().getMap().broadcastMessage(c.getPlayer(), JobPacket.WindArcherPacket
									.TrifleWind(c.getPlayer().getId(), skillid, count, mob.getObjectId(), type), false);
							c.getSession().write(JobPacket.WindArcherPacket.TrifleWind(c.getPlayer().getId(), skillid,
									count, mob.getObjectId(), type));
						}
					}
				}
			}
			switch (attack.skillid) {
			case 13101005:
			case 21110004: // Ranged but uses attackcount instead
			case 14101006: // Vampure
			case 21120006:
			case 11101004:
				// MIHILE
			case 51001004: // Soul Blade
			case 51111007:
			case 51121008:
				// END MIHILE
			case 1077:
			case 1078:
			case 1079:
			case 11077:
			case 11078:
			case 11079:
			case 15111007:
			case 13111007: // Wind Shot
			case 33101007:
			case 13101020:// Fary Spiral
			case 33101002:
			case 33121002:
			case 33121001:
			case 21100004:
			case 21110011:
			case 21100007:
			case 21000004:
			case 5121002:
			case 5921002:
			case 4121003:
			case 4221003:
			case 5221017:
			case 5721007:
			case 5221016:
			case 5721006:
			case 5211008:
			case 5201001:
			case 5721003:
			case 5711000:
			case 4111013:
			case 5121016:
			case 5121013:
			case 5221013:
			case 5721004:
			case 5721001:
			case 5321001:
			case 14111008:
			case 60011216:// Soul Buster
			case 65001100:// Star Bubble
				// case 2321054:
				AOE = true;
				bulletCount = effect.getAttackCount();
				break;

			case 35121005:
			case 35111004:
			case 35121013:
				AOE = true;
				bulletCount = 6;
				break;
			default:
				bulletCount = effect.getBulletCount();
				break;
			}
			if (noBullet && effect.getBulletCount() < effect.getAttackCount()) {
				bulletCount = effect.getAttackCount();
			}
			if ((noBullet) && (effect.getBulletCount() < effect.getAttackCount())) {
				bulletCount = effect.getAttackCount();
			}
			if ((effect.getCooldown(c.getPlayer()) > 0) && (!c.getPlayer().isGM())
					&& (((attack.skillid != 35111004) && (attack.skillid != 35121013))
							|| (c.getPlayer().getBuffSource(CharacterTemporaryStat.Mechanic) != attack.skillid))) {
				if (c.getPlayer().skillisCooling(attack.skillid)) {
					c.getSession().write(CWvsContext.enableActions());
					return;
				}
				c.getSession().write(CField.skillCooldown(attack.skillid, effect.getCooldown(c.getPlayer())));
				c.getPlayer().addCooldown(attack.skillid, System.currentTimeMillis(), effect.getCooldown(c.getPlayer()) * 1000);
			}
		}
		attack = DamageParse.ModifyAttackCrit(attack, c.getPlayer(), 2, effect);
		Integer ShadowPartner = c.getPlayer().getBuffedValue(CharacterTemporaryStat.ShadowPartner);
		if (ShadowPartner != null) {
			bulletCount *= 2;
		}
		int projectile = 0;
		int visProjectile = 0;
		if ((!AOE) && (c.getPlayer().getBuffedValue(CharacterTemporaryStat.SoulArrow) == null) && (!noBullet)) {
			Item ipp = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((short) attack.slot);
			if (ipp == null) {
				return;
			}
			projectile = ipp.getItemId();

			if (attack.csstar > 0) {
				if (c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((short) attack.csstar) == null) {
					return;
				}
				visProjectile = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((short) attack.csstar).getItemId();
			} else {
				visProjectile = projectile;
			}

			if (c.getPlayer().getBuffedValue(CharacterTemporaryStat.NoBulletConsume) == null) {
				int bulletConsume = bulletCount;
				if ((effect != null) && (effect.getBulletConsume() != 0)) {
					bulletConsume = effect.getBulletConsume() * (ShadowPartner != null ? 2 : 1);
				}
				if ((c.getPlayer().getJob() == 412) && (bulletConsume > 0)
						&& (ipp.getQuantity() < MapleItemInformationProvider.getInstance().getSlotMax(projectile))) {
					Skill expert = SkillFactory.getSkill(4120010);
					if (c.getPlayer().getTotalSkillLevel(expert) > 0) {
						MapleStatEffect eff = expert.getEffect(c.getPlayer().getTotalSkillLevel(expert));
						if (eff.makeChanceResult()) {
							ipp.setQuantity((short) (ipp.getQuantity() + 1));
							c.getSession().write(CWvsContext.InventoryPacket.updateInventorySlot(MapleInventoryType.USE,
									ipp, false));
							bulletConsume = 0;
							c.getSession().write(CWvsContext.InventoryPacket.getInventoryStatus());
						}
					}
				}
				if ((bulletConsume > 0) && (!MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, projectile,
						bulletConsume, false, true))) {
					c.getPlayer().dropMessage(5, "You do not have enough arrows/bullets/stars.");
					return;
				}
			}
		} else if ((c.getPlayer().getJob() >= 3500) && (c.getPlayer().getJob() <= 3512)) {
			visProjectile = 2333000;
		} else if (GameConstants.isCannon(c.getPlayer().getJob())) {
			visProjectile = 2333001;
		}

		int projectileWatk = 0;
		if (projectile != 0) {
			projectileWatk = MapleItemInformationProvider.getInstance().getWatkForProjectile(projectile);
		}
		PlayerStats statst = c.getPlayer().getStat();
		double basedamage;
		switch (attack.skillid) {
		case 4001344:
		case 4121007:
		case 14001004:
		case 14111005:
			basedamage = Math.max(statst.getCurrentMaxBaseDamage(),
					statst.getTotalLuk() * 5.0F * (statst.getTotalWatk() + projectileWatk) / 100.0F);
			break;
		case 4111004:
			basedamage = 53000.0D;
			break;
		default:
			basedamage = statst.getCurrentMaxBaseDamage();
			switch (attack.skillid) {
			case 3101005:
				basedamage *= effect.getX() / 100.0D;
				break;
			}
		}

		if (effect != null) {
			basedamage *= (effect.getDamage() + statst.getDamageIncrease(attack.skillid)) / 100.0D;

			long money = effect.getMoneyCon();
			if (money != 0) {
				if (money > c.getPlayer().getMeso()) {
					money = c.getPlayer().getMeso();
				}
				c.getPlayer().gainMeso(-money, false);
			}
		}
		c.getPlayer().checkFollow();
		if (!c.getPlayer().isHidden()) {
			if (attack.skillid == 3211006) {
				c.getPlayer().getMap().broadcastMessage(c.getPlayer(),
						CField.strafeAttack(c.getPlayer().getId(), attack.nMobCount, attack.skillid, skillLevel, attack.display,
								attack.speed, visProjectile, attack.allDamage, attack.position, c.getPlayer().getLevel(),
								c.getPlayer().getStat().passive_mastery(), attack.unk, c.getPlayer().getTotalSkillLevel(3220010)),
						c.getPlayer().getTruePosition());
			} else {
				c.getPlayer().getMap().broadcastMessage(c.getPlayer(),
						CField.rangedAttack(c.getPlayer().getId(), attack.nMobCount, attack.skillid, skillLevel, attack.display,
								attack.speed, visProjectile, attack.allDamage, attack.position, c.getPlayer().getLevel(),
								c.getPlayer().getStat().passive_mastery(), attack.unk),
						c.getPlayer().getTruePosition());
			}
		} else if (attack.skillid == 3211006) {
			c.getPlayer().getMap().broadcastGMMessage(c.getPlayer(),
					CField.strafeAttack(c.getPlayer().getId(), attack.nMobCount, attack.skillid, skillLevel, attack.display,
							attack.speed, visProjectile, attack.allDamage, attack.position, c.getPlayer().getLevel(),
							c.getPlayer().getStat().passive_mastery(), attack.unk, c.getPlayer().getTotalSkillLevel(3220010)),
					false);
		} else {
			c.getPlayer().getMap().broadcastGMMessage(c.getPlayer(),
					CField.rangedAttack(c.getPlayer().getId(), attack.nMobCount, attack.skillid, skillLevel, attack.display,
							attack.speed, visProjectile, attack.allDamage, attack.position, c.getPlayer().getLevel(),
							c.getPlayer().getStat().passive_mastery(), attack.unk),
					false);
		}

		DamageParse.applyAttack(attack, skill, c.getPlayer(), bulletCount, basedamage, effect,
				ShadowPartner != null ? AttackType.RANGED_WITH_SHADOWPARTNER : AttackType.RANGED);
		WeakReference<MapleCharacter>[] clones = c.getPlayer().getClones();
		for (int i = 0; i < clones.length; i++) {
			if (clones[i].get() != null) {
				final MapleCharacter clone = clones[i].get();
				final Skill skil2 = skill;
				final MapleStatEffect eff2 = effect;
				final double basedamage2 = basedamage;
				final int bulletCount2 = bulletCount;
				final int visProjectile2 = visProjectile;
				final int skillLevel2 = skillLevel;
				final AttackInfo attack2 = DamageParse.DivideAttack(attack, c.getPlayer().isGM() ? 1 : 4);
				Timer.CloneTimer.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						if (!clone.isHidden()) {
							if (attack2.skillid == 3211006) {
								clone.getMap()
										.broadcastMessage(CField.strafeAttack(clone.getId(), attack2.nMobCount,
												attack2.skillid, skillLevel2, attack2.display, attack2.speed,
												visProjectile2, attack2.allDamage, attack2.position, clone.getLevel(),
												clone.getStat().passive_mastery(), attack2.unk,
												c.getPlayer().getTotalSkillLevel(3220010)));
							} else {
								clone.getMap()
										.broadcastMessage(CField.rangedAttack(clone.getId(), attack2.nMobCount,
												attack2.skillid, skillLevel2, attack2.display, attack2.speed,
												visProjectile2, attack2.allDamage, attack2.position, clone.getLevel(),
												clone.getStat().passive_mastery(), attack2.unk));
							}
						} else {
							if (attack2.skillid == 3211006) {
								clone.getMap().broadcastGMMessage(clone,
										CField.strafeAttack(clone.getId(), attack2.nMobCount, attack2.skillid, skillLevel2,
												attack2.display, attack2.speed, visProjectile2, attack2.allDamage,
												attack2.position, clone.getLevel(), clone.getStat().passive_mastery(),
												attack2.unk, c.getPlayer().getTotalSkillLevel(3220010)),
										false);
							} else {
								clone.getMap().broadcastGMMessage(clone,
										CField.rangedAttack(clone.getId(), attack2.nMobCount, attack2.skillid, skillLevel2,
												attack2.display, attack2.speed, visProjectile2, attack2.allDamage,
												attack2.position, clone.getLevel(), clone.getStat().passive_mastery(),
												attack2.unk),
										false);
							}
						}
						DamageParse.applyAttack(attack2, skil2, c.getPlayer(), bulletCount2, basedamage2, eff2,
								AttackType.RANGED);
					}
				}, 500 * i + 500);
			}
		}
	}
}
