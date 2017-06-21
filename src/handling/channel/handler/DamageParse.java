package handling.channel.handler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import client.CharacterTemporaryStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MonsterStatus;
import client.MonsterStatusEffect;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import server.MapleStatEffect;
import server.life.Element;
import server.life.ElementalEffectiveness;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.AttackPair;
import tools.Pair;
import tools.Randomizer;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class DamageParse {

	@SuppressWarnings("empty-statement")
	public static void applyAttack(AttackInfo attack, Skill theSkill, MapleCharacter player, int attackCount,
			double maxDamagePerMonster, MapleStatEffect effect, AttackType attack_type) {
		if (attack.getSkillId() != 0) {
			if (effect == null) {
				player.getClient().getSession().write(CWvsContext.enableActions());
				return;
			}
			if (GameConstants.isMulungSkill(attack.getSkillId())) {
				if (player.getMapId() / 10000 != 92502) {
					return;
				}
				if (player.getMulungEnergy() < 10000) {
					return;
				}
				player.mulung_EnergyModify(false);
			} else if (GameConstants.isPyramidSkill(attack.getSkillId())) {
				if (player.getMapId() / 1000000 != 926) {
					return;
				}

				if ((player.getPyramidSubway() != null) && (player.getPyramidSubway().onSkillUse(player)))
					;
			} else if (GameConstants.isInflationSkill(attack.getSkillId())) {
				if (player.getBuffedValue(CharacterTemporaryStat.Inflation) != null)
					;
			}
		}
		/*
		 * if (player.getClient().getChannelServer().isAdminOnly()) {
		 * player.dropMessage(-1, new StringBuilder().append("Animation: "
		 * ).append(Integer.toHexString((attack.display & 0x8000) != 0 ?
		 * attack.display - 32768 : attack.display)).toString()); }
		 */
		boolean useAttackCount = (attack.getSkillId() != 4211006) && (attack.getSkillId() != 3221007) && (attack.getSkillId() != 23121003)
				&& ((attack.getSkillId() != 1311001) || (player.getJob() != 132)) && (attack.getSkillId() != 3211006);

		if ((attack.getHits() > 0) && (attack.getTargets() > 0)) {
			if (!player.getStat().checkEquipDurabilitys(player, -1)) {
				player.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
				return;
			}
		}
		long totDamage = 0;
		MapleMap map = player.getMap();
		if (attack.getSkillId() == 4211006) {
			for (AttackPair oned : attack.allDamage) {
				if (oned.attack == null) {
					MapleMapObject mapobject = map.getMapObject(oned.objectid, MapleMapObjectType.ITEM);

					if (mapobject != null) {
						MapleMapItem mapitem = (MapleMapItem) mapobject;
						mapitem.getLock().lock();
						try {
							if (mapitem.getMeso() > 0) {
								if (mapitem.isPickedUp()) {
									return;
								}
								map.removeMapObject(mapitem);
								map.broadcastMessage(CField.explodeDrop(mapitem.getObjectId()));
								mapitem.setPickedUp(true);
							}
						} finally {
							mapitem.getLock().unlock();
						}
					}
				}
			}
		}
		long totDamageToOneMonster = 0;
		long hpMob = 0L;
		PlayerStats stats = player.getStat();

		int CriticalDamage = stats.passive_sharpeye_percent();
		int ShdowPartnerAttackPercentage = 0;
		if ((attack_type == AttackType.RANGED_WITH_SHADOWPARTNER)
				|| (attack_type == AttackType.NON_RANGED_WITH_MIRROR)) {
			MapleStatEffect shadowPartnerEffect = player.getStatForBuff(CharacterTemporaryStat.ShadowPartner);
			if (shadowPartnerEffect != null) {
				ShdowPartnerAttackPercentage += shadowPartnerEffect.getX();
			}
			attackCount /= 2;
		}
		ShdowPartnerAttackPercentage *= (CriticalDamage + 100) / 100;
		if (attack.getSkillId() == 4221001) {
			ShdowPartnerAttackPercentage *= 10;
		}

		double maxDamagePerHit = 0.0D;
		
		for (AttackPair oned : attack.allDamage) {
			MapleMonster monster = map.getMonsterByOid(oned.objectid);

			if ((monster != null) && (monster.getLinkCID() <= 0)) {
				totDamageToOneMonster = 0;
				hpMob = monster.getMobMaxHp();
				MapleMonsterStats monsterstats = monster.getStats();
				int fixeddmg = monsterstats.getFixedDamage();
				boolean Tempest = (monster.getStatusSourceID(MonsterStatus.FREEZE) == 21120006)
						|| (attack.getSkillId() == 21120006) || (attack.getSkillId() == 1221011);

				if ((!Tempest) && (!player.isGM())) {
					if (((player.getJob() >= 3200) && (player.getJob() <= 3212)
							&& (!monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY))
							&& (!monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY))
							&& (!monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT))) || (attack.getSkillId() == 3221007)
							|| (attack.getSkillId() == 23121003)
							|| (((player.getJob() < 3200) || (player.getJob() > 3212))
									&& (!monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY))
									&& (!monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY))
									&& (!monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)))) {
						maxDamagePerHit = CalculateMaxWeaponDamagePerHit(player, monster, attack, theSkill, effect,
								maxDamagePerMonster, Integer.valueOf(CriticalDamage));
					} else {
						maxDamagePerHit = 1.0D;
					}
				}
				byte overallAttackCount = 0;

				int criticals = 0;
				for (Pair eachde : oned.attack) {
					Long eachd = (Long) eachde.left;
					overallAttackCount = (byte) (overallAttackCount + 1);
					if (((Boolean) eachde.right).booleanValue()) {
						criticals++;
					}
					if ((useAttackCount) && (overallAttackCount - 1 == attackCount)) {
						maxDamagePerHit = maxDamagePerHit / 100.0D * (ShdowPartnerAttackPercentage
								* (monsterstats.isBoss() ? stats.bossdam_r : stats.dam_r) / 100.0D);
					}

					if (fixeddmg != -1) {
						if (monsterstats.getOnlyNoramlAttack()) {
							eachd = Long.valueOf(attack.getSkillId() != 0 ? 0 : fixeddmg);
						} else {
							eachd = Long.valueOf(fixeddmg);
						}
					} else if (monsterstats.getOnlyNoramlAttack()) {
						eachd = Long
								.valueOf(attack.getSkillId() != 0 ? 0 : Math.min(eachd.longValue(), (int) maxDamagePerHit));
					} else if (!player.isGM()) {
						if (Tempest) {
							if (eachd.longValue() > monster.getMobMaxHp()) {
								eachd = Long.valueOf((int) Math.min(monster.getMobMaxHp(), Long.MAX_VALUE));
							}
						} else if (((player.getJob() >= 3200) && (player.getJob() <= 3212)
								&& (!monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY))
								&& (!monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY))
								&& (!monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT)))
								|| (attack.getSkillId() == 23121003)
								|| (((player.getJob() < 3200) || (player.getJob() > 3212))
										&& (!monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY))
										&& (!monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY))
										&& (!monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)))) {
							if (eachd.longValue() > maxDamagePerHit) {

								if (eachd.longValue() > maxDamagePerHit * 2.0D) {
									eachd = Long.valueOf((int) (maxDamagePerHit * 2.0D));
									if (eachd.longValue() >= 2499999) {
										player.getClient().getSession().close();
										System.err.println("Closed session with " + player.getName() + " for extreme damage.");
									}
								}
							}

						} else if (eachd.longValue() > maxDamagePerHit) {
							eachd = Long.valueOf((int) maxDamagePerHit);
						}

					}

					if (player == null) {
						return;
					}
					totDamageToOneMonster += eachd.longValue();

					if (((eachd.longValue() == 0) || (monster.getId() == 9700021))
							&& (player.getPyramidSubway() != null)) {
						player.getPyramidSubway().onMiss(player);
					}
				}
				totDamage += totDamageToOneMonster;
				player.checkMonsterAggro(monster);

				if ((GameConstants.getAttackDelay(attack.getSkillId(), theSkill) >= 100)
						&& (!GameConstants.isNoDelaySkill(attack.getSkillId())) && (attack.getSkillId() != 3101005)
						&& (!monster.getStats().isBoss())
						&& (player.getTruePosition().distanceSq(monster.getTruePosition()) > GameConstants
								.getAttackRange(effect, player.getStat().defRange))) {
				}

				if (player.getSkillLevel(36110005) > 0) {
					Skill skill = SkillFactory.getSkill(36110005);
					MapleStatEffect eff = skill.getEffect(player.getSkillLevel(skill));
					if (player.getLastCombo() + 5000 < System.currentTimeMillis()) {
						monster.setTriangulation(0);
						// player.clearDamageMeters();
					}
					if (eff.makeChanceResult()) {
						player.setLastCombo(System.currentTimeMillis());
						if (monster.getTriangulation() < 3) {
							monster.setTriangulation(monster.getTriangulation() + 1);
						}
						monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.DARKNESS, eff.getX(),
								eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
						monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.TRIANGULATION,
								monster.getTriangulation(), eff.getSourceId(), null, false), false, eff.getY() * 1000,
								true, eff);
					}
				}

				if (player.getBuffedValue(CharacterTemporaryStat.PickPocket) != null) {
					switch (attack.getSkillId()) {
					case 0:
					case 4001334:
					case 4201005:
					case 4211002:
					case 4211004:
					case 4221003:
					case 4221007:
						handlePickPocket(player, monster, oned);
					}

				}

				if ((totDamageToOneMonster > 0) || (attack.getSkillId() == 1221011) || (attack.getSkillId() == 21120006)) {
					if (GameConstants.isDemonSlayer(player.getJob())) {
						player.handleForceGain(monster.getObjectId(), attack.getSkillId());
					}
					if ((GameConstants.isPhantom(player.getJob())) && (attack.getSkillId() != 24120002)
							&& (attack.getSkillId() != 24100003)) {
						player.handleCardStack();
					}

					if (GameConstants.isKaiser(player.getJob())) {
						player.handleKaiserCombo();
					}
					if (attack.getSkillId() != 1221011) {
						monster.damage(player, totDamageToOneMonster, true, attack.getSkillId());
					} else {
						monster.damage(player, monster.getStats().isBoss() ? 500000L : monster.getHp() - 1L, true,
								attack.getSkillId());
					}

					if (monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)) {
						player.addHP(-(7000 + Randomizer.nextInt(8000)));
					}
					player.onAttack(monster.getMobMaxHp(), monster.getMobMaxMp(), attack.getSkillId(), monster.getObjectId(),
							totDamage, 0);
					switch (attack.getSkillId()) {
					case 4001002:
					case 4001334:
					case 4001344:
					case 4111005:
					case 4121007:
					case 4201005:
					case 4211002:
					case 4221001:
					case 4221007:
					case 4301001:
					case 4311002:
					case 4311003:
					case 4331000:
					case 4331004:
					case 4331005:
					case 4331006:
					case 4341002:
					case 4341004:
					case 4341005:
					case 4341009:
					case 14001004:
					case 14111002:
					case 14111005:
						int[] skills = { 4120005, 4220005, 4340001, 14110004 };
						for (int i : skills) {
							Skill skill = SkillFactory.getSkill(i);
							if (player.getTotalSkillLevel(skill) > 0) {
								MapleStatEffect venomEffect = skill.getEffect(player.getTotalSkillLevel(skill));
								if (!venomEffect.makeChanceResult()) {
									break;
								}
								monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.POISON,
										Integer.valueOf(1), i, null, false), true, venomEffect.getDuration(), true,
										venomEffect);
								break;
							}

						}

						break;
					case 4201004:
						monster.handleSteal(player);
						break;
					case 21000002:
					case 21100001:
					case 21100002:
					case 21100004:
					case 21110002:
					case 21110003:
					case 21110004:
					case 21110006:
					case 21110007:
					case 21110008:
					case 21120002:
					case 21120005:
					case 21120006:
					case 21120009:
					case 21120010:
						if ((player.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null)
								&& (!monster.getStats().isBoss())) {
							MapleStatEffect eff = player.getStatForBuff(CharacterTemporaryStat.WeaponCharge);
							if (eff != null) {
								monster.applyStatus(
										player, new MonsterStatusEffect(MonsterStatus.SPEED,
												Integer.valueOf(eff.getX()), eff.getSourceId(), null, false),
										false, eff.getY() * 1000, true, eff);
							}
						}
						if ((player.getBuffedValue(CharacterTemporaryStat.BodyPressure) != null)
								&& (!monster.getStats().isBoss())) {
							MapleStatEffect eff = player.getStatForBuff(CharacterTemporaryStat.BodyPressure);

							if ((eff != null) && (eff.makeChanceResult())
									&& (!monster.isBuffed(MonsterStatus.NEUTRALISE))) {
								monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.NEUTRALISE,
										Integer.valueOf(1), eff.getSourceId(), null, false), false, eff.getX() * 1000,
										true, eff);
							}
						}
						break;
					}
					// int randomDMG = Randomizer.nextInt(player.getDamage2() -
					// player.getReborns() + 1) + player.getReborns();
					// monster.damage(player, randomDMG, true, attack.skill);
					// if (player.getshowdamage() == 1)
					// player.dropMessage(5, new StringBuilder().append("You
					// have done ").append(randomDMG).append(" extra RB damage!
					// (disable/enable this with @dmgnotice)").toString());
					// }
					// else {
					// if (player.getDamage() > 2147483647L) {
					// long randomDMG = player.getDamage();
					// monster.damage(player, monster.getMobMaxHp(), true,
					// attack.skill);
					// if (player.getshowdamage() == 1) {
					// player.dropMessage(5, new StringBuilder().append("You
					// have done ").append(randomDMG).append(" extra RB damage!
					// (disable/enable this with @dmgnotice)").toString());
					// }
					// }
					if (totDamageToOneMonster > 0) {
						Item weapon_ = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
						if (weapon_ != null) {
							MonsterStatus stat = GameConstants.getStatFromWeapon(weapon_.getItemId());
							if ((stat != null) && (Randomizer.nextInt(100) < GameConstants.getStatChance())) {
								MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(stat,
										Integer.valueOf(GameConstants.getXForStat(stat)),
										GameConstants.getSkillForStat(stat), null, false);
								monster.applyStatus(player, monsterStatusEffect, false, 10000L, false, null);
							}
						}
						if (player.getBuffedValue(CharacterTemporaryStat.Blind) != null) {
							MapleStatEffect eff = player.getStatForBuff(CharacterTemporaryStat.Blind);

							if ((eff != null) && (eff.makeChanceResult())) {
								MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.ACC,
										Integer.valueOf(eff.getX()), eff.getSourceId(), null, false);
								monster.applyStatus(player, monsterStatusEffect, false, eff.getY() * 1000, true, eff);
							}
						}

						if (player.getBuffedValue(CharacterTemporaryStat.IllusionStep) != null) {
							MapleStatEffect eff = player.getStatForBuff(CharacterTemporaryStat.IllusionStep);

							if ((eff != null) && (eff.makeChanceResult())) {
								MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.SPEED,
										Integer.valueOf(eff.getX()), 3121007, null, false);
								monster.applyStatus(player, monsterStatusEffect, false, eff.getY() * 1000, true, eff);
							}
						}
						if ((player.getJob() == 121) || (player.getJob() == 122)) {
							Skill skill = SkillFactory.getSkill(1211006);
							if (player.isBuffFrom(CharacterTemporaryStat.WeaponCharge, skill)) {
								MapleStatEffect eff = skill.getEffect(player.getTotalSkillLevel(skill));
								MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.FREEZE,
										Integer.valueOf(1), skill.getId(), null, false);
								monster.applyStatus(player, monsterStatusEffect, false, eff.getY() * 2000, true, eff);
							}
						}
					}
					if ((effect != null) && (effect.getMonsterStati().size() > 0) && (effect.makeChanceResult())) {
						for (Map.Entry z : effect.getMonsterStati().entrySet()) {
							monster.applyStatus(player,
									new MonsterStatusEffect((MonsterStatus) z.getKey(), (Integer) z.getValue(),
											theSkill.getId(), null, false),
									effect.isPoison(), effect.getDuration(), true, effect);
						}
					}
				}
			}
		}

		if (GameConstants.isLuminous(player.getJob())) {
			if (player.getBuffedValue(CharacterTemporaryStat.StackBuff) != 1)
				;
			{
				MapleStatEffect crescendo = SkillFactory.getSkill(27121005).getEffect(player.getSkillLevel(27121005));
				if (crescendo != null) {

					if (crescendo.makeChanceResult()) {
						player.setLastCombo(System.currentTimeMillis());
						if (player.acaneAim <= 29) {
							player.acaneAim++;
							crescendo.applyTo(player);
						}
					}
				}
			}
		}

		if (player.getJob() >= 1500 && player.getJob() <= 1512) {
			MapleStatEffect crescendo = SkillFactory.getSkill(15001022).getEffect(player.getSkillLevel(15001022));
			if (crescendo != null) {

				if (crescendo.makeChanceResult()) {
					player.setLastCombo(System.currentTimeMillis());
					if (player.acaneAim <= 3) {
						player.acaneAim++;
						crescendo.applyTo(player);
					}
				}
			}
		}

		if (player.getJob() >= 420 && player.getJob() <= 422) {
			MapleStatEffect crescendo = SkillFactory.getSkill(4200013).getEffect(player.getSkillLevel(4200013));
			if (crescendo != null) {

				if (crescendo.makeChanceResult()) {
					player.setLastCombo(System.currentTimeMillis());
					if (player.acaneAim <= 30) {
						player.acaneAim++;
						crescendo.applyTo(player);
					}
				}
			}
		}

		if ((attack.getSkillId() == 4331003) && ((hpMob <= 0L) || (totDamageToOneMonster < hpMob))) {
			return;
		}
		if ((hpMob > 0L) && (totDamageToOneMonster > 0)) {
			player.afterAttack(attack.getTargets(), attack.getHits(), attack.getSkillId());
		}
		if ((attack.getSkillId() != 0) && ((attack.getTargets() > 0) || ((attack.getSkillId() != 4331003) && (attack.getSkillId() != 4341002)))
				&& (!GameConstants.isNoDelaySkill(attack.getSkillId()))) {
			boolean applyTo = effect.applyTo(player, attack.position);
		}
		if (player.getSkillLevel(4100011) > 0) {
			MapleStatEffect eff = SkillFactory.getSkill(4100011).getEffect(player.getSkillLevel(4100011));
			if (eff.makeChanceResult()) {
				for (Map.Entry z : effect.getMonsterStati().entrySet()) {
					for (AttackPair ap : attack.allDamage) {
						final MapleMonster monster = player.getMap().getMonsterByOid(ap.objectid);
						// monster.applyStatus(player, new
						// MonsterStatusEffect((MonsterStatus)z.getKey(),
						// (Integer)z.getValue(), theSkill.getId(), null,
						// false), effect.isPoison(), effect.getDuration(),
						// true, effect);
						// }

						// MonsterStatusEffect monsterStatusEffect = new
						// MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON,
						// eff.getSkillStats().getStats("dot")),
						// SkillFactory.getSkill(4100011), null, false);
						// monsterStatusEffect.setOwnerId(player.getId());
						// //cidê°€ ë§žì•„ì•¼ ë³´ì�´ë¯€ë¡œ
						// monster.applyStatus(player, new
						// MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON,
						// eff.getourceId().getStats("dot")),
						// SkillFactory.getSkill(4100011), null, false), true,
						// eff.getDuration(), false);
						// monster.applyStatus(player, new
						// MonsterStatusEffect(MonsterStatus.POISON,
						// Integer.valueOf(eff.getX()), eff.getSourceId(), null,
						// false), false, eff.getY() * 1000, true, eff);
						monster.applyStatus(player,
								new MonsterStatusEffect((MonsterStatus) z.getKey(), (Integer) z.getValue(),
										theSkill.getId(), null, false),
								effect.isPoison(), effect.getDuration(), true, effect);
					}
				}
			}

			int bulletCount = eff.getBulletCount();
			for (AttackPair ap : attack.allDamage) {
				final MapleMonster source = player.getMap().getMonsterByOid(ap.objectid);

				final MonsterStatusEffect check = source.getBuff(MonsterStatus.POISON);

				if (check != null && check.getSkill() == 4100011 && check.getOwnerId() == player.getId()) { // :3
					final List<MapleMapObject> objs = player.getMap().getMapObjectsInRange(player.getPosition(), 500000,
							Arrays.asList(MapleMapObjectType.MONSTER));
					final List<MapleMonster> monsters = new ArrayList<>();
					for (int i = 0; i < bulletCount; i++) {
						int rand = Randomizer.rand(0, objs.size() - 1);
						if (objs.size() < bulletCount) {
							if (i < objs.size()) {
								monsters.add((MapleMonster) objs.get(i));
							}
						} else {
							monsters.add((MapleMonster) objs.get(rand));
							objs.remove(rand);
						}
					}
					if (monsters.size() <= 0) {
						CWvsContext.enableActions();
						return;
					}
					final List<Point> points = new ArrayList<>();
					for (MapleMonster mob : monsters) {
						points.add(mob.getPosition());
					}
					// player.dropMessage(monsters.size());
					// player.dropMessage("ì‹œìž‘" + monsters.size());
					player.getMap().broadcastMessage(CWvsContext.giveMarkOfTheif(player.getId(), source.getObjectId(),
							4100012, monsters, player.getPosition(), monsters.get(0).getPosition(), 2070005));
					// player.message("ì¢…ë£Œ");
				}
			}
		}
	}

	@SuppressWarnings("empty-statement")
	public static final void applyAttackMagic(AttackInfo attack, Skill theSkill, MapleCharacter player,
			MapleStatEffect effect, double maxDamagePerHit) {

		if ((attack.getHits() > 0) && (attack.getTargets() > 0) && (!player.getStat().checkEquipDurabilitys(player, -1))) {
			player.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
			return;
		}

		if (GameConstants.isMulungSkill(attack.getSkillId())) {
			if (player.getMapId() / 10000 != 92502) {
				return;
			}
			if (player.getMulungEnergy() < 10000) {
				return;
			}
			player.mulung_EnergyModify(false);
		} else if (GameConstants.isPyramidSkill(attack.getSkillId())) {
			if (player.getMapId() / 1000000 != 926) {
				return;
			}

			if ((player.getPyramidSubway() != null) && (player.getPyramidSubway().onSkillUse(player)));
		} else if ((GameConstants.isInflationSkill(attack.getSkillId()))
				&& (player.getBuffedValue(CharacterTemporaryStat.Inflation) == null)) {
			return;
		}

		/*
		 * if (player.getClient().getChannelServer().isAdminOnly()) {
		 * player.dropMessage(-1, new StringBuilder().append("Animation: "
		 * ).append(Integer.toHexString((attack.display & 0x8000) != 0 ?
		 * attack.display - 32768 : attack.display)).toString()); }
		 */

		PlayerStats stats = player.getStat();
		// Element element = player.getBuffedValue(CharacterTemporaryStat.ElementalReset) != null ? Element.NEUTRAL : theSkill.getElement();

		double MaxDamagePerHit = 0.0D;
		long totDamage = 0;

		int CriticalDamage = stats.passive_sharpeye_percent();
		MapleMap map = player.getMap();
		for (AttackPair oned : attack.allDamage) {
			MapleMonster monster = map.getMonsterByOid(oned.objectid);

			if ((monster != null) && (monster.getLinkCID() <= 0)) {
				boolean Tempest = (monster.getStatusSourceID(MonsterStatus.FREEZE) == 21120006)
						&& (!monster.getStats().isBoss());
				long totDamageToOneMonster = 0;
				MapleMonsterStats monsterstats = monster.getStats();
				int fixeddmg = monsterstats.getFixedDamage();
				if ((!Tempest) && (!player.isGM())) {
					if ((!monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY))
							&& (!monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT))) {
						MaxDamagePerHit = CalculateMaxMagicDamagePerHit(player, theSkill, monster, monsterstats, stats,
								null, Integer.valueOf(CriticalDamage), maxDamagePerHit, effect);
					} else {
						MaxDamagePerHit = 1.0D;
					}
				}
				byte overallAttackCount = 0;

				for (Pair eachde : oned.attack) {
					Long eachd = (Long) eachde.left;
					overallAttackCount = (byte) (overallAttackCount + 1);
					if (fixeddmg != -1) {
						eachd = Long.valueOf(monsterstats.getOnlyNoramlAttack() ? 0 : fixeddmg);
					} else if (monsterstats.getOnlyNoramlAttack()) {
						eachd = Long.valueOf(0);
					} else if (!player.isGM()) {
						if (Tempest) {
							if (eachd.longValue() > monster.getMobMaxHp()) {
								eachd = Long.valueOf((int) Math.min(monster.getMobMaxHp(), 2147483647L));
							}
						} else if ((!monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY))
								&& (!monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT))) {
							if (eachd.longValue() > MaxDamagePerHit) {
								if (eachd.longValue() > MaxDamagePerHit * 2.0D) {
									eachd = Long.valueOf((int) (MaxDamagePerHit * 2.0D));

									if (eachd.longValue() >= 2499999) {
										player.getClient().getSession().close();
									}
								}
							}

						} else if (eachd.longValue() > MaxDamagePerHit) {
							eachd = Long.valueOf(((long) MaxDamagePerHit));
						}

					}

					totDamageToOneMonster += eachd.longValue();
				}

				totDamage += totDamageToOneMonster;
				player.checkMonsterAggro(monster);

				if ((GameConstants.getAttackDelay(attack.getSkillId(), theSkill) >= 100)
						&& (!GameConstants.isNoDelaySkill(attack.getSkillId())) && (!monster.getStats().isBoss())
						&& (player.getTruePosition().distanceSq(monster.getTruePosition()) > GameConstants
								.getAttackRange(effect, player.getStat().defRange))) {
				}
				if (GameConstants.isLuminous(player.getJob())) {
					player.handleLuminous(attack.getSkillId());
				}

				monster.damage(player, totDamage, true, attack.getSkillId());
			}
			// else if (player.getDamage() > 2147483647L) {
			// long randomDMG = player.getDamage();
			// monster.damage(player, monster.getMobMaxHp(), true,
			// attack.skill);
			// if (player.getshowdamage() == 1) {
			// player.dropMessage(5, new StringBuilder().append("You have done
			// ").append(randomDMG).append(" extra damage! (disable/enable this
			// with @dmgnotice)").toString());
			// }
			// int totDamageToOneMonster1 = 0;
			// if (totDamageToOneMonster1 > 0) {
			// monster.damage(player, totDamageToOneMonster1, true,
			// attack.skill);
			// if (monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT)) {
			// player.addHP(-(7000 + Randomizer.nextInt(8000)));
			// }
			// if (player.getBuffedValue(CharacterTemporaryStat.Slow) != null) {
			// MapleStatEffect eff = player.getStatForBuff(CharacterTemporaryStat.Slow);
			//
			// if ((eff != null) && (eff.makeChanceResult()) &&
			// (!monster.isBuffed(MonsterStatus.SPEED))) {
			// monster.applyStatus(player, new
			// MonsterStatusEffect(MonsterStatus.SPEED,
			// Integer.valueOf(eff.getX()), eff.getSourceId(), null, false),
			// false, eff.getY() * 1000, true, eff);
			// }
			//
			// }
			//
			// player.onAttack(monster.getMobMaxHp(), monster.getMobMaxMp(),
			// attack.skill, monster.getObjectId(), totDamage, 0);
			//
			// switch (attack.skill) {
			// case 2221003:
			// monster.setTempEffectiveness(Element.ICE, effect.getDuration());
			// break;
			// case 2121003:
			// monster.setTempEffectiveness(Element.FIRE, effect.getDuration());
			// }
			//
			// if ((effect != null) && (effect.getMonsterStati().size() > 0) &&
			// (effect.makeChanceResult()))
			// {
			// for (Map.Entry z : effect.getMonsterStati().entrySet()) {
			// monster.applyStatus(player, new
			// MonsterStatusEffect((MonsterStatus)z.getKey(),
			// (Integer)z.getValue(), theSkill.getId(), null, false),
			// effect.isPoison(), effect.getDuration(), true, effect);
			// }
			// }
			//
			// if (eaterLevel > 0) {
			// eaterSkill.getEffect(eaterLevel).applyPassive(player, monster);
			// }
			// }
			// }

			if (attack.getSkillId() != 2301002) {
				effect.applyTo(player);
			}
		}
	}

	private static double CalculateMaxMagicDamagePerHit(MapleCharacter chr, Skill skill, MapleMonster monster,
			MapleMonsterStats mobstats, PlayerStats stats, Element elem, Integer sharpEye, double maxDamagePerMonster,
			MapleStatEffect attackEffect) {
		int dLevel = Math.max(mobstats.getLevel() - chr.getLevel(), 0) * 2;
		int HitRate = Math.min(
				(int) Math.floor(Math.sqrt(stats.getAccuracy())) - (int) Math.floor(Math.sqrt(mobstats.getEva())) + 100,
				100);
		if (dLevel > HitRate) {
			HitRate = dLevel;
		}
		HitRate -= dLevel;
		if ((HitRate <= 0)
				&& ((!GameConstants.isBeginnerJob(skill.getId() / 10000)) || (skill.getId() % 10000 != 1000))) {
			return 0.0D;
		}

		int CritPercent = sharpEye.intValue();
		ElementalEffectiveness ee = monster.getEffectiveness(elem);
		double elemMaxDamagePerMob;
		switch (ee) {
		case IMMUNE:
			elemMaxDamagePerMob = 1.0D;
			break;
		default:
			elemMaxDamagePerMob = ElementalStaffAttackBonus(elem, maxDamagePerMonster * ee.getValue(), stats);
		}

		int MDRate = monster.getStats().getMDRate();
		MonsterStatusEffect pdr = monster.getBuff(MonsterStatus.MDEF);
		if (pdr != null) {
			MDRate += pdr.getX().intValue();
		}
		elemMaxDamagePerMob -= elemMaxDamagePerMob
				* (Math.max(MDRate - stats.ignoreTargetDEF - attackEffect.getIgnoreMob(), 0) / 100.0D);

		elemMaxDamagePerMob += elemMaxDamagePerMob / 100.0D * CritPercent;

		elemMaxDamagePerMob *= (monster.getStats().isBoss() ? chr.getStat().bossdam_r : chr.getStat().dam_r) / 100.0D;
		MonsterStatusEffect imprint = monster.getBuff(MonsterStatus.IMPRINT);
		if (imprint != null) {
			elemMaxDamagePerMob += elemMaxDamagePerMob * imprint.getX().intValue() / 100.0D;
		}
		elemMaxDamagePerMob += elemMaxDamagePerMob * chr.getDamageIncrease(monster.getObjectId()) / 100.0D;
		if (GameConstants.isBeginnerJob(skill.getId() / 10000)) {
			switch (skill.getId() % 10000) {
			case 1000:
				elemMaxDamagePerMob = 40.0D;
				break;
			case 1020:
				elemMaxDamagePerMob = 1.0D;
				break;
			case 1009:
				elemMaxDamagePerMob = monster.getStats().isBoss() ? monster.getMobMaxHp() / 30L * 100L
						: monster.getMobMaxHp();
			}
		}

		switch (skill.getId()) {
		case 32001000:
		case 32101000:
		case 32111002:
		case 32121002:
			elemMaxDamagePerMob *= 1.5D;
		}

		if (elemMaxDamagePerMob > 999999.0D) {
			elemMaxDamagePerMob = 999999.0D;
		} else if (elemMaxDamagePerMob <= 0.0D) {
			elemMaxDamagePerMob = 1.0D;
		}

		return elemMaxDamagePerMob;
	}

	private static double ElementalStaffAttackBonus(Element elem, double elemMaxDamagePerMob, PlayerStats stats) {
		if(elem == null){
                	return  elemMaxDamagePerMob / 100.0D * (stats.def);
            	}
		
		switch (elem) {
			case FIRE:
				return elemMaxDamagePerMob / 100.0D * (stats.element_fire + stats.getElementBoost(elem));
			case ICE:
				return elemMaxDamagePerMob / 100.0D * (stats.element_ice + stats.getElementBoost(elem));
			case LIGHTING:
				return elemMaxDamagePerMob / 100.0D * (stats.element_light + stats.getElementBoost(elem));
			case POISON:
				return elemMaxDamagePerMob / 100.0D * (stats.element_psn + stats.getElementBoost(elem));
		}
		return elemMaxDamagePerMob / 100.0D * (stats.def + stats.getElementBoost(elem));
	}

	private static void handlePickPocket(MapleCharacter player, MapleMonster mob, AttackPair oned) {
		int maxmeso = player.getBuffedValue(CharacterTemporaryStat.PickPocket).intValue();

		for (Pair eachde : oned.attack) {
			Long eachd = (Long) eachde.left;
			if ((player.getStat().pickRate >= 100) || (Randomizer.nextInt(99) < player.getStat().pickRate)) {
				player.getMap().spawnMesoDrop(
						Math.min((int) Math.max(eachd.longValue() / 20000.0D * maxmeso, 1.0D), maxmeso),
						new Point((int) (mob.getTruePosition().getX() + Randomizer.nextInt(100) - 50.0D),
								(int) mob.getTruePosition().getY()),
						mob, player, false, (byte) 0);
			}
		}
	}

	private static double CalculateMaxWeaponDamagePerHit(MapleCharacter player, MapleMonster monster, AttackInfo attack,
			Skill theSkill, MapleStatEffect attackEffect, double maximumDamageToMonster,
			Integer CriticalDamagePercent) {
		int dLevel = Math.max(monster.getStats().getLevel() - player.getLevel(), 0) * 2;
		int HitRate = Math.min((int) Math.floor(Math.sqrt(player.getStat().getAccuracy()))
				- (int) Math.floor(Math.sqrt(monster.getStats().getEva())) + 100, 100);
		if (dLevel > HitRate) {
			HitRate = dLevel;
		}
		HitRate -= dLevel;
		if ((HitRate <= 0) && ((!GameConstants.isBeginnerJob(attack.getSkillId() / 10000)) || (attack.getSkillId() % 10000 != 1000))
				&& (!GameConstants.isPyramidSkill(attack.getSkillId())) && (!GameConstants.isMulungSkill(attack.getSkillId()))
				&& (!GameConstants.isInflationSkill(attack.getSkillId()))) {
			return 0.0D;
		}
		if ((player.getMapId() / 1000000 == 914) || (player.getMapId() / 1000000 == 927)) {
			return 999999.0D;
		}

		List<Element> elements = new ArrayList();
		boolean defined = false;
		int CritPercent = CriticalDamagePercent.intValue();
		int PDRate = monster.getStats().getPDRate();
		MonsterStatusEffect pdr = monster.getBuff(MonsterStatus.WDEF);
		if (pdr != null) {
			PDRate += pdr.getX().intValue();
		}
		if (theSkill != null) {
			elements.add(theSkill.getElement());
			if (GameConstants.isBeginnerJob(theSkill.getId() / 10000)) {
				switch (theSkill.getId() % 10000) {
				case 1000:
					maximumDamageToMonster = 40.0D;
					defined = true;
					break;
				case 1020:
					maximumDamageToMonster = 1.0D;
					defined = true;
					break;
				case 1009:
					maximumDamageToMonster = monster.getStats().isBoss() ? monster.getMobMaxHp() / 30L * 100L
							: monster.getMobMaxHp();
					defined = true;
				}
			}

			switch (theSkill.getId()) {
			case 1311005:
				PDRate = monster.getStats().isBoss() ? PDRate : 0;
				break;
			case 3221001:
			case 33101001:
				maximumDamageToMonster *= attackEffect.getMobCount();
				defined = true;
				break;
			case 3101005:
				defined = true;
				break;
			case 32001000:
			case 32101000:
			case 32111002:
			case 32121002:
				maximumDamageToMonster *= 1.5D;
				break;
			case 1221009:
			case 3221007:
			case 4331003:
			case 23121003:
				if (!monster.getStats().isBoss()) {
					maximumDamageToMonster = monster.getMobMaxHp();
					defined = true;
				}
				break;
			case 1221011:
			case 21120006:
				maximumDamageToMonster = monster.getStats().isBoss() ? 500000.0D : monster.getHp() - 1L;
				defined = true;
				break;
			case 3211006:
				if (monster.getStatusSourceID(MonsterStatus.FREEZE) == 3211003) {
					defined = true;
					maximumDamageToMonster = 999999.0D;
				}
				break;
			}
		}
		double elementalMaxDamagePerMonster = maximumDamageToMonster;
		if ((player.getJob() == 311) || (player.getJob() == 312) || (player.getJob() == 321)
				|| (player.getJob() == 322)) {
			Skill mortal = SkillFactory
					.getSkill((player.getJob() == 311) || (player.getJob() == 312) ? 3110001 : 3210001);
			if (player.getTotalSkillLevel(mortal) > 0) {
				MapleStatEffect mort = mortal.getEffect(player.getTotalSkillLevel(mortal));
				if ((mort != null) && (monster.getHPPercent() < mort.getX())) {
					elementalMaxDamagePerMonster = 999999.0D;
					defined = true;
					if (mort.getZ() > 0) {
						player.addHP(player.getStat().getMaxHp() * mort.getZ() / 100);
					}
				}
			}
		} else if ((player.getJob() == 221) || (player.getJob() == 222)) {
			Skill mortal = SkillFactory.getSkill(2210000);
			if (player.getTotalSkillLevel(mortal) > 0) {
				MapleStatEffect mort = mortal.getEffect(player.getTotalSkillLevel(mortal));
				if ((mort != null) && (monster.getHPPercent() < mort.getX())) {
					elementalMaxDamagePerMonster = 999999.0D;
					defined = true;
				}
			}
		}
		if ((!defined) || ((theSkill != null) && ((theSkill.getId() == 33101001) || (theSkill.getId() == 3221001)))) {
			if (player.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null) {
				int chargeSkillId = player.getBuffSource(CharacterTemporaryStat.WeaponCharge);

				switch (chargeSkillId) {
				case 1211003:
				case 1211004:
					elements.add(Element.FIRE);
					break;
				case 1211005:
				case 1211006:
				case 21111005:
					elements.add(Element.ICE);
					break;
				case 1211007:
				case 1211008:
				case 15101006:
					elements.add(Element.LIGHTING);
					break;
				case 1221003:
				case 1221004:
				case 11111007:
					elements.add(Element.HOLY);
					break;
				case 12101005:
				}

			}

			if (player.getBuffedValue(CharacterTemporaryStat.AssistCharge) != null) {
				elements.add(Element.LIGHTING);
			}
			if (player.getBuffedValue(CharacterTemporaryStat.ElementalReset) != null) {
				elements.clear();
			}
			double elementalEffect;
			if (elements.size() > 0) {
				switch (attack.getSkillId()) {
				case 3111003:
				case 3211003:
					elementalEffect = attackEffect.getX() / 100.0D;
					break;
				default:
					elementalEffect = 0.5D / elements.size();
				}

				for (Element element : elements) {
					switch (monster.getEffectiveness(element)) {
					case IMMUNE:
						elementalMaxDamagePerMonster = 1.0D;
						break;
					case WEAK:
						elementalMaxDamagePerMonster *= (1.0D + elementalEffect
								+ player.getStat().getElementBoost(element));
						break;
					case STRONG:
						elementalMaxDamagePerMonster *= (1.0D - elementalEffect
								- player.getStat().getElementBoost(element));
					}

				}

			}

			elementalMaxDamagePerMonster -= elementalMaxDamagePerMonster
					* (Math.max(PDRate - Math.max(player.getStat().ignoreTargetDEF, 0)
							- Math.max(attackEffect == null ? 0 : attackEffect.getIgnoreMob(), 0), 0) / 100.0D);

			elementalMaxDamagePerMonster += elementalMaxDamagePerMonster / 100.0D * CritPercent;

			MonsterStatusEffect imprint = monster.getBuff(MonsterStatus.IMPRINT);
			if (imprint != null) {
				elementalMaxDamagePerMonster += elementalMaxDamagePerMonster * imprint.getX().intValue() / 100.0D;
			}

			elementalMaxDamagePerMonster += elementalMaxDamagePerMonster
					* player.getDamageIncrease(monster.getObjectId()) / 100.0D;
			elementalMaxDamagePerMonster *= ((monster.getStats().isBoss()) && (attackEffect != null)
					? player.getStat().bossdam_r + attackEffect.getBossDamage() : player.getStat().dam_r) / 100.0D;
		}
		if (elementalMaxDamagePerMonster > 999999.0D) {
			if (!defined) {
				elementalMaxDamagePerMonster = 999999.0D;
			}
		} else if (elementalMaxDamagePerMonster <= 0.0D) {
			elementalMaxDamagePerMonster = 1.0D;
		}
		return elementalMaxDamagePerMonster;
	}

	public static final AttackInfo DivideAttack(final AttackInfo attack, final int rate) {
		attack.real = false;
		if (rate <= 1) {
			return attack; // lol
		}
		for (AttackPair p : attack.allDamage) {
			if (p.attack != null) {
				for (Pair<Long, Boolean> eachd : p.attack) {
					eachd.left /= rate; // too ex.
				}
			}
		}
		return attack;
	}

	public static final AttackInfo ModifyAttackCrit(AttackInfo attack, MapleCharacter chr, int type,
			MapleStatEffect effect) {
		int CriticalRate;
		boolean shadow;
		List damages;
		List damage;
		if ((attack.getSkillId() != 4211006) && (attack.getSkillId() != 3211003) && (attack.getSkillId() != 4111004)) {
			CriticalRate = chr.getStat().passive_sharpeye_rate() + (effect == null ? 0 : effect.getCr());
			shadow = (chr.getBuffedValue(CharacterTemporaryStat.ShadowPartner) != null) && ((type == 1) || (type == 2));
			damages = new ArrayList();
			damage = new ArrayList();

			for (AttackPair p : attack.allDamage) {
				if (p.attack != null) {
					int hit = 0;
					int mid_att = shadow ? p.attack.size() / 2 : p.attack.size();

					int toCrit = (attack.getSkillId() == 4221001) || (attack.getSkillId() == 3221007) || (attack.getSkillId() == 23121003)
							|| (attack.getSkillId() == 4341005) || (attack.getSkillId() == 4331006) || (attack.getSkillId() == 21120005)
									? mid_att : 0;
					if (toCrit == 0) {
						for (Pair eachd : p.attack) {
							if ((!((Boolean) eachd.right).booleanValue()) && (hit < mid_att)) {
								if ((((Long) eachd.left).longValue() > 999999)
										|| (Randomizer.nextInt(100) < CriticalRate)) {
									toCrit++;
								}
								damage.add(eachd.left);
							}
							hit++;
						}
						if (toCrit == 0) {
							damage.clear();
						} else {
							Collections.sort(damage);
							for (int i = damage.size(); i > damage.size() - toCrit; i--) {
								damages.add(damage.get(i - 1));
							}
							damage.clear();
						}
					} else {
						hit = 0;
						for (Pair eachd : p.attack) {
							if (!((Boolean) eachd.right).booleanValue()) {
								if (attack.getSkillId() == 4221001) {
									eachd.right = Boolean.valueOf(hit == 3);
								} else if ((attack.getSkillId() == 3221007) || (attack.getSkillId() == 23121003)
										|| (attack.getSkillId() == 21120005) || (attack.getSkillId() == 4341005)
										|| (attack.getSkillId() == 4331006) || (((Long) eachd.left).longValue() > 999999)) {
									eachd.right = Boolean.valueOf(true);
								} else if (hit >= mid_att) {
									eachd.right = ((Pair) p.attack.get(hit - mid_att)).right;
								} else {
									eachd.right = Boolean.valueOf(damages.contains(eachd.left));
								}
							}
							hit++;
						}
						damages.clear();
					}
				}
			}
		}
		return attack;
	}
	
	public static AttackInfo parseCloseRangeAttack(LittleEndianAccessor lea, MapleCharacter chr, boolean energy) {
		AttackInfo ai = new AttackInfo();
		lea.skip(1); // bFieldKey
		ai.nMobCount = (int) lea.readByte();
		ai.skillid = lea.readInt();
		byte skillLevel = lea.readByte();

		if (ai.getTargets() == 0) {
			parseNormalAttack(lea, ai, chr);
		} else {
			parseMeleeAttack(lea, ai, chr, energy);
		}
		return ai;
	}
	
	private static void parseNormalAttack(LittleEndianAccessor lea, AttackInfo ai, MapleCharacter chr) {
		
		lea.skip(1); // bAddAttackProc
		lea.skip(4); // crc
		
		lea.skip(1);
		
		lea.skip(1);
		ai.display = lea.readShort();
		lea.skip(4);
		lea.skip(1);
		ai.speed = lea.readByte();
		
		ai.lastAttackTickCount = lea.readInt();
		
		lea.skip(4);
		lea.skip(4); // final attack
		lea.skip(2); // slot
		lea.skip(2); // csstar
	}
	
	private static void parseMeleeAttack(LittleEndianAccessor lea, AttackInfo ai, MapleCharacter chr, boolean energy) {
		int pGrenade = 0;

		lea.skip(1); // bAddAttackProc
		lea.skip(4); // skillid crc
		
		int skillid = ai.getSkillId();
		
		if (Skill.isKeyDownSkill(skillid) || Skill.isSuperNovaSkill(skillid))
			ai.charge = lea.readInt();
		
		if (Skill.isRushBombSkill(ai.getSkillId()))
			pGrenade = lea.readInt();
		
		// is_zero_skill
		if (GameConstants.isZero(chr.getJob()))
			lea.skip(1);
		
		// is_userclone_summoned_able_skill
		// nBySummonedID = lea.readInt();

		if (!energy) {
			lea.skip(1);
		}
		
		lea.skip(1);
		ai.display = lea.readShort();
		lea.skip(4);
		lea.skip(1);
		ai.speed = lea.readByte();
		
		if (!energy) {
			ai.lastAttackTickCount = lea.readInt();
		}
		
		lea.skip(4);
		int finalAttack = lea.readInt();

		// BYTE3(nSkillID) && v18->m_finalAttack.nLastSkillID > 0
		if (skillid > 0 && finalAttack != 0) {
			lea.skip(1);
		}
		
		if (skillid == 5111009) // spiral assault
			lea.skip(1);
		
		if (Skill.isUseBulletMeleeAttack(skillid)) {
			lea.skip(2);
			if (Skill.isNonConsumingBulletMeleeAttack(skillid)) {
				lea.skip(4);
			}
		}
		
		if (skillid == 25111005) { // spirit frenzy
			lea.skip(4); // nSpiritCoreEnhance
		}

		lea.skip(6); // ???
		if(skillid == 0) {
			lea.skip(1);
		}
		if(GameConstants.isMercedesSkill(skillid) && skillid != 23120011){
			// why is 2312011 the only merc skill that doesn't skip 4? (rolling moonsault)
			lea.skip(4);
		}

		ai.allDamage = new ArrayList<>();
		for(int mob = 0 ; mob < ai.getTargets(); mob++) {
			int oid = lea.readInt();
			lea.skip(1);
			lea.skip(1);
			lea.skip(1);
			lea.skip(1);
			lea.skip(1);
			lea.skip(4);
			lea.skip(1);
			lea.skip(2);
			lea.skip(2);
			lea.skip(2);
			lea.skip(2);
			lea.skip(2);
			
			List<Pair<Long, Boolean>> damageNumbers = new ArrayList<>();
			for(int hit = 0; hit < ai.getHits(); hit++) {
				long damage = lea.readLong();
				damageNumbers.add(new Pair<>(damage, false));
			}
			lea.skip(4); // GetMobUpDownYRange
			lea.skip(4); // mob crc
			
			// IsResWarriorLiftPress
			// slea.skip(1);
//			lea.readInt();
			// PACKETMAKER::MakeAttackInfoPacket
			byte bSkeleton = lea.readByte();
			if (bSkeleton == 1) {
				lea.readMapleAsciiString();
				lea.readInt();
				
				// v9 = v2->pHitPartProcessor.p;
				
				// if (v9) {
				
					// HitPartsProcessor::Encode
					// int size = lea.readInt();
					// for(int i = 0; i < size; i++) {
					//     lea.readMapleAsciiString(); // animation
					// }
					
				// } else {
					// lea.readMapleAsciiString();
					// lea.readInt();
				// }
				
			} else if (bSkeleton == 2) {
				lea.readMapleAsciiString();
				lea.readInt();
			}
			lea.readByte();
						
			ai.allDamage.add(new AttackPair(oid, damageNumbers));
		}
		
		if (skillid == 36121052 || skillid == 61121052 || Skill.isScreenCenterAttackSkill(skillid)) { // orbital cataclysm, ancestral prominence
			lea.skip(2);
			lea.skip(2); // swing mastery
		} else {
			
			if (Skill.isSuperNovaSkill(skillid)) {
				lea.skip(2); // ptAttackRefPoint.x
				lea.skip(2); // ptAttackRefPoint.y
			}
			
			if (skillid == 101000102) { // air riot
				lea.skip(2);
				lea.skip(2);
			}
			
		}
		
		lea.skip(2);
		lea.skip(2);
		
		if (Skill.isAranFallingStopSkill(skillid)) {
			lea.skip(1);
		}
		
		if (skillid == 21120019 || skillid == 37121052) { // finisher - hunter's prey, hyper magnum punch
			lea.skip(4); // m_teleport.pt.x
			lea.skip(4); // m_teleport.pt.y
		}
		
		if (skillid == 61121105 || skillid == 61121222 || skillid == 24121052) { // inferno breath, carte rose finale
			lea.skip(2); // count?
			if (lea.available() > 0) {
				// some sort of count here.
				
				// slea.skip(2);
				// slea.skip(2);
				System.out.printf("Find the count. Skill: %s%n", skillid);
			}
		} else if (skillid == 101120104){ // advanced earth break
			// ??
		}
		
		if (skillid == 14111006 && pGrenade > 0) {
			lea.skip(2); // x
			lea.skip(2); // y
		}
		
	}
	
	public static AttackInfo parseMagicAttack(LittleEndianAccessor lea) {
		AttackInfo ai = new AttackInfo();
		lea.skip(1); // bFieldKey
		ai.nMobCount = (int) lea.readByte();
		ai.skillid = lea.readInt();
		byte skillLevel = lea.readByte();
		
		// lea.skip(1); // bAddAttackProc
		lea.skip(4); // crc
		
		int skillid = ai.getSkillId();
		
		if (Skill.isKeyDownSkill(skillid)) {
			ai.charge = lea.readInt();
		}
		
		lea.skip(1);
		lea.skip(1);
		ai.display = lea.readShort();
		lea.skip(4);
		lea.skip(1);
		
		if (Skill.isEvanForceSkill(skillid)) {
			lea.skip(1); // bEvanForceAction
		}
		
		ai.speed = lea.readByte();
		ai.lastAttackTickCount = lea.readInt();
		// lea.skip(4);
		lea.skip(4); // final attack
		// lea.skip(2); // slot
		// lea.skip(2); // csstar

		lea.skip(7); // ???

		ai.allDamage = new ArrayList<>();
		
		for(int mob = 0 ; mob < ai.getTargets(); mob++) {
			int oid = lea.readInt();
			lea.skip(1);
			lea.skip(1);
			lea.skip(1);
			lea.skip(1);
			lea.skip(1);
			lea.skip(4);
			lea.skip(1);
			lea.skip(2);
			lea.skip(2);
			lea.skip(2);
			lea.skip(2);
			lea.skip(1); // m_nHPpercentage
			if (ai.skillid == 80001835) { // soul shear
				lea.skip(1);
			} else {
				lea.skip(2);
			}
			
			List<Pair<Long, Boolean>> damageNumbers = new ArrayList<>();
			for(int hit = 0; hit < ai.getHits(); hit++) {
				long damage = lea.readLong();
				damageNumbers.add(new Pair<>(damage, false));
			}
			lea.skip(4); // GetMobUpDownYRange
			lea.skip(4); // crc
			
			// PACKETMAKER::MakeAttackInfoPacket
			byte bSkeleton = lea.readByte();
			if (bSkeleton == 1) {
				lea.readMapleAsciiString();
				lea.readInt();
				
				// v9 = v2->pHitPartProcessor.p;
				
				// if (v9) {
				
					// HitPartsProcessor::Encode
					// int size = lea.readInt();
					// for(int i = 0; i < size; i++) {
					//     lea.readMapleAsciiString(); // animation
					// }
					
				// } else {
					// lea.readMapleAsciiString();
					// lea.readInt();
				// }
				
			} else if (bSkeleton == 2) {
				lea.readMapleAsciiString();
				lea.readInt();
			}
			lea.skip(1);
			ai.allDamage.add(new AttackPair(oid, damageNumbers));
		}
		
		if (ai.getTargets() <= 0 && skillid == 2121011) {
			lea.skip(2); // x
			lea.skip(2); // y
		}	
		
		if (ai.skillid > 27111303) {
	        if (ai.skillid == 80001837 || ai.skillid == 27121052) {
	        	lea.readShort(); //x
	        	lea.readShort(); //y
	        }
		} else {
			if (ai.skillid != 32111016) {
				short nForcedX = lea.readShort();
				short nForcedY = lea.readShort();
				
				boolean dragon = lea.readByte() > 0;
				if (dragon) {
					lea.readShort();
					lea.readShort();
					
					lea.readShort(); // x
					lea.readShort(); // y
					
					lea.readShort();
					
					lea.readByte();
					lea.readByte();
					lea.readByte();
					
					if (ai.skillid == 12100029) {
						/* if ( pExcept ) */
						lea.readInt();
					} else {
						switch(ai.skillid) {
						case 2121003:
							byte size = lea.readByte();
							for(int j = 0; j < size; j++) {
								lea.readInt();
							}
							break;
						case 2111003:
							lea.readByte(); // bForce
							lea.readShort(); // nForcedX
							lea.readShort(); // nForcedY
							break;
						case 80001835:
							byte size1 = lea.readByte();
							for(int s = 0; s < size1; s++) {
								lea.readInt(); // dwID
								lea.readShort();
							}
							lea.readShort(); // tDelay
							break;
						}
					}
					
				}
			}
		}
		return ai;
	}
	
	public static AttackInfo parseRangedAttack(LittleEndianAccessor lea, MapleClient c) {
		AttackInfo ai = new AttackInfo();
		lea.skip(1);
		lea.skip(1); // bFieldKey
		ai.nMobCount = (int) lea.readByte();
		ai.skillid = lea.readInt();
		byte skillLevel = lea.readByte();
		byte bAddAttackProc = lea.readByte();

		lea.skip(4); // crc

		int skillid = ai.skillid;

		if (Skill.isKeyDownSkill(skillid)) {
			if(skillid == 5221004) {
				ai.charge = lea.readByte();
			}else{
				ai.charge = lea.readInt();
			}

		}

		if (GameConstants.isZero(c.getPlayer().getJob())) {
			lea.readByte();
		}
		// is_userclone_summoned_able_skill
		// nBySummonedID = lea.readInt();

		lea.skip(1);
		ai.slot = ((byte) lea.readShort());
		lea.skip(1);

		lea.skip(4);
		lea.skip(1);

		if (ai.skillid == 3111013) {
			lea.skip(4);
			lea.skip(2); // x
			lea.skip(2); // y
		}

		ai.display = lea.readShort();
		lea.skip(4);
		lea.skip(1);

		if (ai.skillid == 23111001 || ai.skillid == 80001915 || ai.skillid == 36111010) {
			lea.skip(4);
			lea.skip(4);
			lea.skip(4);
		}

		ai.speed = lea.readByte();
		ai.lastAttackTickCount = lea.readInt();
		lea.skip(4);
		int finalAttack = lea.readInt();
		ai.csstar = ((byte) lea.readShort());
		ai.AOE = lea.readByte(); // nShootRange

		// !is_shoot_skill_not_consuming_bullet
		// lea.skip(4);

		lea.skip(2);
		lea.skip(2);
		lea.skip(2);
		lea.skip(2);

		lea.skip(5); // ???

		if(GameConstants.isMercedesSkill(skillid) && skillid != 23121052){ // ????
			lea.skip(4);
		}

		// TODO not good to hardcore, figure out what category these skills fall in
		if (skillid == 13111020) {
			lea.skip(4);
		}
		if (skillid == 13121001) {
			lea.skip(4);
		}
		if(skillid == 23121000){
			lea.skip(4);
		}

		ai.allDamage = new ArrayList<>();
		for(int mob = 0 ; mob < ai.getTargets(); mob++) {
			int oid = lea.readInt();
			lea.skip(1);
			lea.skip(1);
			lea.skip(1);
			lea.skip(1);
			lea.skip(1);
			lea.skip(4);
			lea.skip(1);
			lea.skip(2);
			lea.skip(2);
			lea.skip(2);
			lea.skip(2);
			lea.skip(2);
			
			/**
			 * if ( SHIDWORD(v747) > 0 )
			      {
			        v638 = (int)(v625 + 6);
			        do
			        {
			          COutPacket::Encode4((COutPacket *)((char *)&v763 + 8), *(_DWORD *)v638);
			          if ( TSingleton<CBattleRecordMan>::ms_pInstance )
			            CBattleRecordMan::SetBattleDamageInfo(
			              TSingleton<CBattleRecordMan>::ms_pInstance,
			              (int)v767,
			              v637,
			              SHIDWORD(v733),
			              *(_DWORD *)v638,
			              *(_DWORD *)(v638 + 60),
			              0);
			          ++v637;
			          v638 += 4;
			        }
			        while ( v637 < SHIDWORD(v747) );
			      }
			 */
			
			List<Pair<Long, Boolean>> damageNumbers = new ArrayList<>();
			for(int hit = 0; hit < ai.getHits(); hit++) {
				long damage = lea.readLong();
				damageNumbers.add(new Pair<>(damage, false));
			}
			lea.skip(4); // GetMobUpDownYRange
			lea.skip(4); // mob crc
			
			// PACKETMAKER::MakeAttackInfoPacket
			byte bSkeleton = lea.readByte();
			if (bSkeleton == 1) {
				lea.readMapleAsciiString();
				lea.readInt();
				
				// v9 = v2->pHitPartProcessor.p;
				
				// if (v9) {
				
					// HitPartsProcessor::Encode
					// int size = lea.readInt();
					// for(int i = 0; i < size; i++) {
					//     lea.readMapleAsciiString(); // animation
					// }
					
				// } else {
					// lea.readMapleAsciiString();
					// lea.readInt();
				// }
				
			} else if (bSkeleton == 2) {
				lea.readMapleAsciiString();
				lea.readInt();
			}
			lea.skip(1);
			ai.allDamage.add(new AttackPair(oid, damageNumbers));
		}
		
		if (Skill.isScreenCenterAttackSkill(skillid)) {
			lea.skip(2);
			lea.skip(2);
		} else {
			ai.position = lea.readPos();
		}
		
		if (c.getPlayer().getSummonsSize() > 1) {
			if (ai.skillid == 14111024 || ai.skillid > 14121053 && ai.skillid <= 14121056) {
	        	lea.readShort();
	        	lea.readShort();
	        }
		}
		
		if (Skill.isAranFallingStopSkill(skillid))
			lea.skip(1);
		
		if (skillid / 100 == 33) {
			lea.skip(2);
			lea.skip(2);
		}
		
		// is_keydown_skill_rect_move_xy
		if (ai.skillid == 13111020) {
        	lea.readShort();
        	lea.readShort();
        }
		
		if (ai.skillid == 23121002 || ai.skillid == 80001914) {
        	lea.readByte(); // m_pfh
        }
		
		return ai;
	}

	public static AttackInfo parseExplosionAttack(LittleEndianAccessor lea, AttackInfo ret, MapleCharacter chr) {
		if (ret.getHits() == 0) {
			lea.skip(4);
			byte bullets = lea.readByte();
			for (int j = 0; j < bullets; j++) {
				ret.allDamage.add(new AttackPair(Integer.valueOf(lea.readInt()).intValue(), null));
				lea.skip(1);
			}
			lea.skip(2);
			return ret;
		}

		for (int i = 0; i < ret.getTargets(); i++) {
			int oid = lea.readInt();

			lea.skip(12);
			byte bullets = lea.readByte();
			List<Pair<Long, Boolean>> allDamageNumbers = new ArrayList<>();
			for (int j = 0; j < bullets; j++) {
				allDamageNumbers.add(new Pair<>(Long.valueOf(lea.readLong()), Boolean.valueOf(false)));
			}
			ret.allDamage.add(new AttackPair(Integer.valueOf(oid).intValue(), allDamageNumbers));
			lea.skip(4);
		}
		lea.skip(4);
		byte bullets = lea.readByte();

		for (int j = 0; j < bullets; j++) {
			ret.allDamage.add(new AttackPair(Integer.valueOf(lea.readInt()).intValue(), null));
			lea.skip(2);
		}

		return ret;
	}
}
