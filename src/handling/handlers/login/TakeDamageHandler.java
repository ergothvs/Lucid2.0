package handling.handlers.login;

import java.awt.Point;

import client.*;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.life.MapleMonster;
import server.life.MobAttackInfo;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import tools.Pair;
import tools.Randomizer;
import tools.data.LittleEndianAccessor;
import tools.packet.*;
import tools.packet.enums.ReviveType;

public class TakeDamageHandler {

	@PacketHandler(opcode = RecvPacketOpcode.TAKE_DAMAGE)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		lea.skip(4);
		lea.skip(4); // update tick
		byte type = lea.readByte();
		lea.skip(1);
		int damage = lea.readInt();
		lea.skip(2);
		boolean isDeadlyAttack = false;
		boolean pPhysical = false;
		int oid = 0;
		int monsteridfrom = 0;
		int fake = 0;
		int mpattack = 0;
		int skillid = 0;
		int pID = 0;
		int pDMG = 0;
		byte direction = 0;
		byte pType = 0;
		Point pPos = new Point(0, 0);
		MapleMonster attacker = null;

		if (GameConstants.isXenon(c.getPlayer().getJob())) {
			if (c.getPlayer().getSkillLevel(36110004) > 0) {
				c.getPlayer().getMap().broadcastMessage(JobPacket.XenonPacket.EazisSystem(c.getPlayer().getId(), oid));
			}
		}

		if ((c.getPlayer() == null) || (c.getPlayer().isHidden()) || (c.getPlayer().getMap() == null)) {
			c.getSession().write(CWvsContext.enableActions());
			return;
		}
		if ((c.getPlayer().isGM()) && (c.getPlayer().isInvincible())) {
			c.getSession().write(CWvsContext.enableActions());
			return;
		}
		PlayerStats stats = c.getPlayer().getStat();
		if ((type != -2) && (type != -3) && (type != -4)) {
			monsteridfrom = lea.readInt();
			oid = lea.readInt();
			attacker = c.getPlayer().getMap().getMonsterByOid(oid);
			direction = lea.readByte();
			if ((attacker == null) || (attacker.getId() != monsteridfrom) || (attacker.getLinkCID() > 0)
					|| (attacker.isFake()) || (attacker.getStats().isFriendly())) {
				return;
			}
			if (c.getPlayer().getMapId() == 915000300) {
				MapleMap to = c.getPlayer().getClient().getChannelServer().getMapFactory().getMap(915000200);
				c.getPlayer().dropMessage(5, "You've been found out! Retreat!");
				c.getPlayer().changeMap(to, to.getPortal(1));
				return;
			}
			if (attacker.getId() == 9300166 && c.getPlayer().getMapId() == 910025200) {
				int rocksLost = Randomizer.rand(1, 5);
				while (c.getPlayer().itemQuantity(4031469) < rocksLost) {
					rocksLost--;
				}
				if (rocksLost > 0) {
					c.getPlayer().gainItem(4031469, -rocksLost);
					Item toDrop = MapleItemInformationProvider.getInstance().getEquipById(4031469);
					for (int i = 0; i < rocksLost; i++) {
						c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(),
								true, true);
					}
				}
			}
			if ((type != -1) && (damage > 0)) {
				MobAttackInfo attackInfo = attacker.getStats().getMobAttack(type);
				if (attackInfo != null) {
					if ((attackInfo.isElement) && (stats.TER > 0) && (Randomizer.nextInt(100) < stats.TER)) {
						System.out.println(new StringBuilder().append("Avoided ER from mob id: ").append(monsteridfrom)
								.toString());
						return;
					}
					if (attackInfo.isDeadlyAttack()) {
						isDeadlyAttack = true;
						mpattack = stats.getMp() - 1;
					} else {
						mpattack += attackInfo.getMpBurn();
					}
					MobSkill skill = MobSkillFactory.getMobSkill(attackInfo.getDiseaseSkill(),
							attackInfo.getDiseaseLevel());
					if ((skill != null) && ((damage == -1) || (damage > 0))) {
						skill.applyEffect(c.getPlayer(), attacker, false);
					}
					attacker.setMp(attacker.getMp() - attackInfo.getMpCon());
				}
			}
			skillid = lea.readInt();
			pDMG = lea.readInt();
			byte defType = lea.readByte();
			lea.skip(1);
			if (defType == 1) {
				Skill bx = SkillFactory.getSkill(31110008);
				int bof = c.getPlayer().getTotalSkillLevel(bx);
				if (bof > 0) {
					MapleStatEffect eff = bx.getEffect(bof);
					if (Randomizer.nextInt(100) <= eff.getX()) {
						c.getPlayer().handleForceGain(oid, 31110008, eff.getZ());
					}
				}
			}
			if (skillid != 0) {
				pPhysical = lea.readByte() > 0;
				pID = lea.readInt();
				pType = lea.readByte();
				lea.skip(4);
				pPos = lea.readPos();
			}
		}
		if (damage == -1) {
			fake = 4020002 + (c.getPlayer().getJob() / 10 - 40) * 100000;
			if ((fake != 4120002) && (fake != 4220002)) {
				fake = 4120002;
			}
			if ((type == -1) && (c.getPlayer().getJob() == 122) && (attacker != null)
					&& (c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10) != null)
					&& (c.getPlayer().getTotalSkillLevel(1220006) > 0)) {
				MapleStatEffect eff = SkillFactory.getSkill(1220006).getEffect(c.getPlayer().getTotalSkillLevel(1220006));
				attacker.applyStatus(c.getPlayer(),
						new MonsterStatusEffect(MonsterStatus.STUN, Integer.valueOf(1), 1220006, null, false), false,
						eff.getDuration(), true, eff);
				fake = 1220006;
			}

			if (c.getPlayer().getTotalSkillLevel(fake) <= 0) {
				return;
			}
		} else if ((damage < -1) || (damage > 200000)) {
			c.getSession().write(CWvsContext.enableActions());
			return;
		}
		if ((c.getPlayer().getStat().dodgeChance > 0) && (Randomizer.nextInt(100) < c.getPlayer().getStat().dodgeChance)) {
			c.getSession().write(CField.EffectPacket.showForeignEffect(20));
			return;
		}
		if ((pPhysical) && (skillid == 1201007) && (c.getPlayer().getTotalSkillLevel(1201007) > 0)) {
			damage -= pDMG;
			if (damage > 0) {
				MapleStatEffect eff = SkillFactory.getSkill(1201007).getEffect(c.getPlayer().getTotalSkillLevel(1201007));
				long enemyDMG = Math.min(damage * (eff.getY() / 100), attacker.getMobMaxHp() / 2L);
				if (enemyDMG > pDMG) {
					enemyDMG = pDMG;
				}
				if (enemyDMG > 1000L) {
					enemyDMG = 1000L;
				}
				attacker.damage(c.getPlayer(), enemyDMG, true, 1201007);
			} else {
				damage = 1;
			}
		}
		Pair modify = c.getPlayer().modifyDamageTaken(damage, attacker);
		damage = ((Double) modify.left).intValue();
		if (damage > 0) {

			if (c.getPlayer().getBuffedValue(CharacterTemporaryStat.Morph) != null) {
				c.getPlayer().cancelMorphs();
			}

			boolean mpAttack = (c.getPlayer().getBuffedValue(CharacterTemporaryStat.Mechanic) != null)
					&& (c.getPlayer().getBuffSource(CharacterTemporaryStat.Mechanic) != 35121005);
			if (c.getPlayer().getBuffedValue(CharacterTemporaryStat.MagicGuard) != null) {
				int hploss = 0;
				int mploss = 0;
				if (isDeadlyAttack) {
					if (stats.getHp() > 1) {
						hploss = stats.getHp() - 1;
					}
					if (stats.getMp() > 1) {
						mploss = stats.getMp() - 1;
					}
					if (c.getPlayer().getBuffedValue(CharacterTemporaryStat.Infinity) != null) {
						mploss = 0;
					}
					c.getPlayer().addMPHP(-hploss, -mploss);
				} else {
					mploss = (int) (damage * (c.getPlayer().getBuffedValue(CharacterTemporaryStat.MagicGuard).doubleValue() / 100.0D))
							+ mpattack;
					hploss = damage - mploss;
					if (c.getPlayer().getBuffedValue(CharacterTemporaryStat.Infinity) != null) {
						mploss = 0;
					} else if (mploss > stats.getMp()) {
						mploss = stats.getMp();
						hploss = damage - mploss + mpattack;
					}
					c.getPlayer().addMPHP(-hploss, -mploss);
				}
			} else if (c.getPlayer().getStat().mesoGuardMeso > 0.0D) {
				int mesoloss = (int) (damage * (c.getPlayer().getStat().mesoGuardMeso / 100.0D));
				if (c.getPlayer().getMeso() < mesoloss) {
					c.getPlayer().gainMeso(-c.getPlayer().getMeso(), false);
					c.getPlayer().cancelBuffStats(new CharacterTemporaryStat[] { CharacterTemporaryStat.MesoGuard });
				} else {
					c.getPlayer().gainMeso(-mesoloss, false);
				}
				if ((isDeadlyAttack) && (stats.getMp() > 1)) {
					mpattack = stats.getMp() - 1;
				}
				c.getPlayer().addMPHP(-damage, -mpattack);
			} else if (isDeadlyAttack) {
				c.getPlayer().addMPHP(stats.getHp() > 1 ? -(stats.getHp() - 1) : 0,
						(stats.getMp() > 1) && (!mpAttack) ? -(stats.getMp() - 1) : 0);
			} else {
				c.getPlayer().addMPHP(-damage, mpAttack ? 0 : -mpattack);
			}
			if ((c.getPlayer().inPVP()) && (c.getPlayer().getStat().getHPPercent() <= 20)) {
				c.getPlayer().getStat();
				SkillFactory.getSkill(PlayerStats.getSkillByJob(93, c.getPlayer().getJob())).getEffect(1).applyTo(c.getPlayer());
			}
		}
		byte offset = 0;
		int offset_d = 0;
		if (lea.available() == 1L) {
			offset = lea.readByte();
			if ((offset == 1) && (lea.available() >= 4L)) {
				offset_d = lea.readInt();
			}
			if ((offset < 0) || (offset > 2)) {
				offset = 0;
			}
		}

		c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.damagePlayer(c.getPlayer().getId(), type, damage, monsteridfrom, direction,
				skillid, pDMG, pPhysical, pID, pType, pPos, offset, offset_d, fake), false);
		if(!c.getPlayer().isAlive()){
			MapleCharacter chr = c.getPlayer();

			boolean hasExpProtector = chr.hasExpProtector();
			boolean hasBuffProtector = chr.hasBuffProtector();
			boolean hasWheel = chr.hasWheel();
			int reviveType = hasWheel ? ReviveType.CURRENT_MAP.getValue() : ReviveType.DEFAULT.getValue();
			c.getSession().write(UserLocal.openUIOnDead(true, hasBuffProtector, hasExpProtector, reviveType));
		}
	}
}
