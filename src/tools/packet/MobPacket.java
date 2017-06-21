package tools.packet;

import java.awt.Point;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import client.MonsterStatus;
import client.MonsterStatusEffect;
import handling.SendPacketOpcode;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.maps.MapleMap;
import server.maps.MapleNodes;
import server.movement.LifeMovementFragment;
import tools.data.PacketWriter;

public class MobPacket {

	public static byte[] damageMonster(int oid, long damage) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
		pw.writeInt(oid);
		pw.write(0);
		pw.writeLong(damage);

		return pw.getPacket();
	}

	public static byte[] damageFriendlyMob(MapleMonster mob, long damage, boolean display) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
		pw.writeInt(mob.getObjectId());
		pw.write(display ? 1 : 2);
		if (damage > 2147483647L) {
			pw.writeInt(2147483647);
		} else {
			pw.writeInt((int) damage);
		}
		if (mob.getHp() > 2147483647L) {
			pw.writeInt((int) (mob.getHp() / mob.getMobMaxHp() * 2147483647.0D));
		} else {
			pw.writeInt((int) mob.getHp());
		}
		if (mob.getMobMaxHp() > 2147483647L) {
			pw.writeInt(2147483647);
		} else {
			pw.writeInt((int) mob.getMobMaxHp());
		}

		return pw.getPacket();
	}

	public static byte[] killMonster(int oid, int animation, boolean azwan) {
		PacketWriter pw = new PacketWriter();

		if (azwan) {
			pw.writeShort(SendPacketOpcode.AZWAN_KILL_MONSTER.getValue());
		} else {
			pw.writeShort(SendPacketOpcode.KILL_MONSTER.getValue());
		}
		boolean a = false; // idk
		boolean b = false; // idk
		if (azwan) {
			pw.write(a ? 1 : 0);
			pw.write(b ? 1 : 0);
		}
		pw.writeInt(oid);
		if (azwan) {
			if (a) {
				pw.write(0);
				if (b) {
					// set mob temporary stat
				} else {
					// set mob temporary stat
				}
			} else {
				if (b) {
					// idk
				} else {
					// idk
				}
			}
			return pw.getPacket();
		}
		pw.write(animation);
		if (animation == 4) {
			pw.writeInt(-1);
		}

		return pw.getPacket();
	}

	public static byte[] suckMonster(int oid, int chr) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.KILL_MONSTER.getValue());
		pw.writeInt(oid);
		pw.write(4);
		pw.writeInt(chr);

		return pw.getPacket();
	}

	public static byte[] healMonster(int oid, int heal) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
		pw.writeInt(oid);
		pw.write(0);
		pw.writeInt(-heal);

		return pw.getPacket();
	}

	public static byte[] MobToMobDamage(int oid, int dmg, int mobid, boolean azwan) {
		PacketWriter pw = new PacketWriter();

		if (azwan) {
			pw.writeShort(SendPacketOpcode.AZWAN_MOB_TO_MOB_DAMAGE.getValue());
		} else {
			pw.writeShort(SendPacketOpcode.MOB_TO_MOB_DAMAGE.getValue());
		}
		pw.writeInt(oid);
		pw.write(0);
		pw.writeInt(dmg);
		pw.writeInt(mobid);
		pw.write(1);

		return pw.getPacket();
	}

	public static byte[] getMobSkillEffect(int oid, int skillid, int cid, int skilllevel) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.SKILL_EFFECT_MOB.getValue());
		pw.writeInt(oid);
		pw.writeInt(skillid);
		pw.writeInt(cid);
		pw.writeShort(skilllevel);

		return pw.getPacket();
	}

	public static byte[] getMobCoolEffect(int oid, int itemid) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.ITEM_EFFECT_MOB.getValue());
		pw.writeInt(oid);
		pw.writeInt(itemid);

		return pw.getPacket();
	}

	public static byte[] showMonsterHP(int oid, int remhppercentage) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.SHOW_MONSTER_HP.getValue());
		pw.writeInt(oid);
		pw.write(remhppercentage);

		return pw.getPacket();
	}

	public static byte[] showCygnusAttack(int oid) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.CYGNUS_ATTACK.getValue());
		pw.writeInt(oid);

		return pw.getPacket();
	}

	public static byte[] showMonsterResist(int oid) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.MONSTER_RESIST.getValue());
		pw.writeInt(oid);
		pw.writeInt(0);
		pw.writeInt(0); // new
		pw.writeShort(1);
		pw.writeInt(0);

		return pw.getPacket();
	}

	public static byte[] showBossHP(MapleMonster mob) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
		pw.write(6);
		pw.writeInt(mob.getId() == 9400589 ? 9300184 : mob.getId());
		if (mob.getHp() > Long.MAX_VALUE) {
			pw.writeLong((long) (mob.getHp() / mob.getMobMaxHp() * 2147483647.0D));
		} else {
			pw.writeLong(mob.getHp());
		}
		if (mob.getMobMaxHp() > Long.MAX_VALUE) {
			pw.writeLong(Long.MAX_VALUE);
		} else {
			pw.writeLong(mob.getMobMaxHp());
		}
		pw.write(mob.getStats().getTagColor());
		pw.write(mob.getStats().getTagBgColor());

		return pw.getPacket();
	}

	public static byte[] showBossHP(int monsterId, long currentHp, long maxHp) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
		pw.write(6);
		pw.writeInt(monsterId);
		if (currentHp > Long.MAX_VALUE) {
			pw.writeLong(currentHp / maxHp * Long.MAX_VALUE); // can't happen, but whatever
		} else {
			pw.writeLong(currentHp <= 0L ? -1L : currentHp);
		}
		if (maxHp > Long.MAX_VALUE) {
			pw.writeLong(Long.MAX_VALUE);
		} else {
			pw.writeLong(maxHp);
		}
		pw.write(6);
		pw.write(5);

		return pw.getPacket();
	}

	public static byte[] moveMonster(boolean useskill, int skill, int unk, int oid, Point xy, List<LifeMovementFragment> moves) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.MOVE_MONSTER.getValue());
		pw.writeInt(oid);
		
		pw.write(useskill ? 1 : 0);
		pw.write(skill);
		pw.writeInt(unk);
		
		pw.write(0); // unk3 == null ? 0 : unk3.size());
		
		/*if (unk3 != null) {
			for (Pair i : unk3) {
				pw.writeShort(((Integer) i.left).intValue());
				pw.writeShort(((Integer) i.right).intValue());
			}
		}*/
		
		pw.write(0); // unk2 == null ? 0 : unk2.size());
		
		/*if (unk2 != null) {
			for (Integer i : unk2) {
				pw.writeShort(i.intValue());
			}
		}*/

		pw.writeInt(0); // EncodedGatherDuration
		pw.writePos(xy);
		pw.writeShort(0); // vx
		pw.writeShort(0); // vy
		
		PacketHelper.serializeMovementList(pw, moves);
		pw.write(0); // ...
		return pw.getPacket();
	}

	public static byte[] spawnMonster(MapleMonster life, int spawnType, int link, boolean azwan) {
		PacketWriter pw = new PacketWriter();
		pw.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());

		pw.write(0);
		pw.writeInt(life.getObjectId());

		pw.write(1);
		pw.writeInt(life.getId());

		addMonsterStatus(pw, life);
		addMonsterInformation(pw, life, true, (byte) spawnType, link, false);
		/*pw.writePos(life.getTruePosition());
		pw.write(0);// ...
		pw.write(life.getStance());
		pw.writeShort(life.getFh());
		pw.writeShort(life.getFh());
		pw.writeShort(spawnType); // ...
		if ((spawnType == -3) || (spawnType >= 0)) {
			pw.writeInt(link);
		}
		pw.write(life.getCarnivalTeam());
		pw.writeInt(life.getHp() > 2147483647 ? 2147483647 : (int) life.getHp());
		pw.writeInt(0);

		pw.writeInt(0);
		pw.writeInt(0);
		pw.writeInt(0);

		pw.writeInt(0);
		pw.write(0);

		pw.writeInt(-1);
		pw.writeInt(-1);
		pw.write(0);
		pw.writeInt(0);
		pw.writeInt(0); // 0x64
		pw.writeInt(-1);
		pw.write(0);
		pw.writeInt(0);
		pw.writeInt(0);
		pw.writeShort(0); */
		return pw.getPacket();
	}

	private static void addMonsterStatus(PacketWriter pw, MapleMonster life) {

		// ForcedMobStat::Decode
		pw.write(life.getChangedStats() != null);
		if (life.getChangedStats() != null) {
			pw.writeInt(life.getChangedStats().hp > 2147483647L ? 2147483647 : (int) life.getChangedStats().hp);
			pw.writeInt(life.getChangedStats().mp);
			pw.writeInt(life.getChangedStats().exp);
			pw.writeInt(life.getChangedStats().watk);
			pw.writeInt(life.getChangedStats().matk);
			pw.writeInt(life.getChangedStats().PDRate);
			pw.writeInt(life.getChangedStats().MDRate);
			pw.writeInt(life.getChangedStats().acc);
			pw.writeInt(life.getChangedStats().eva);
			pw.writeInt(life.getChangedStats().pushed);
			pw.writeInt(life.getChangedStats().speed);
			pw.writeInt(life.getChangedStats().level);
			pw.writeInt(0); // nUserCount
		}

		// CMob::SetTemporaryStat
		pw.write(new byte[12]);
		
		// MobStat::DecodeTemporary
	}

	public static byte[] controlMonster(MapleMonster life, boolean newSpawn, boolean aggro, boolean azwan) {
		PacketWriter pw = new PacketWriter();

		if (azwan) {
			pw.writeShort(SendPacketOpcode.AZWAN_SPAWN_MONSTER_CONTROL.getValue());
		} else {
			pw.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
		}

		if (!azwan) {
			pw.write(aggro ? 2 : 1);
		}
		pw.writeInt(life.getObjectId());

		pw.write(1); // 1 = Control normal, 5 = Control none?
		pw.writeInt(life.getId());

		addMonsterStatus(pw, life);
		addMonsterInformation(pw, life, false, (byte) 0, 0, newSpawn);
		
		/*pw.writePos(life.getTruePosition());
		pw.write(life.getStance());
		pw.writeShort(life.getFh());// was0
		pw.writeShort(life.getFh());
		pw.write(newSpawn ? -2 : life.isFake() ? -4 : -1);
		pw.write(life.getCarnivalTeam());
		pw.writeInt(125);
		pw.writeInt(0);
		pw.writeInt(0);
		pw.writeInt(0);
		pw.writeInt(0);
		pw.writeInt(0);
		pw.write(0);
		pw.writeLong(-1);
		pw.writeInt(0);
		pw.writeInt(0);
		pw.write(0);
		pw.write(-1);*/
		return pw.getPacket();
	}

	private static void addMonsterInformation(PacketWriter pw, MapleMonster life, boolean summon,
			byte spawnType, int link, boolean newSpawn) {
		pw.writePos(life.getTruePosition());
		// pw.write(0); // ...
		pw.write(life.getStance());
		pw.writeShort(0);
		pw.writeShort(life.getFh());

		if (summon) {

			pw.writeShort(spawnType); // ...
			if ((spawnType == -3) || (spawnType >= 0)) {
				pw.writeInt(link); // link
			}

		} else {
			
			// pw.write(newSpawn ? -2 : life.isFake() ? -4 : -1);
			if(newSpawn) {
				pw.writeShort(-2); // ...
			} else if (life.isFake()) {
				pw.writeShort(-4); // ...
			} else {
				pw.writeShort(-1); // ...
			}

		}
		pw.write(life.getCarnivalTeam());
		
		long value = Long.MAX_VALUE; // 179 hp is a long instead of int
		if(life.getHp() > value) {
			pw.writeLong(value);
		} else {
			pw.writeLong(life.getHp());
		}
		
		pw.writeInt(0);
		
		pw.writeInt(0);
		pw.writeInt(0);
		pw.writeInt(0);
		
		pw.writeInt(0);
		pw.write(0);
		
		pw.writeInt(-1);
		pw.writeInt(-1);
		pw.write(0);
		pw.writeInt(0);
		pw.writeInt(0x64); // monster scale
		pw.writeInt(-1);
		pw.write(0);
		pw.writeInt(0);
		pw.writeInt(0);
		pw.writeShort(0);
	}

	public static byte[] stopControllingMonster(MapleMonster life, boolean azwan) {
		PacketWriter pw = new PacketWriter();

		if (azwan) {
			pw.writeShort(SendPacketOpcode.AZWAN_SPAWN_MONSTER_CONTROL.getValue());
		} else {
			pw.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
		}
		if (!azwan) {
			pw.write(0);
		}
		pw.writeInt(life.getObjectId());
		if (azwan) {
			pw.write(0);
			pw.writeInt(0);
			
			pw.write(0);
			addMonsterStatus(pw, life);
			addMonsterInformation(pw, life, false, (byte) 0, 0, false);

			/*pw.writePos(life.getTruePosition());
			pw.write(life.getStance());
			pw.writeShort(0);
			pw.writeShort(life.getFh());
			pw.write(life.isFake() ? -4 : -1);
			pw.write(life.getCarnivalTeam());
			pw.writeInt(63000);
			pw.writeInt(0);
			pw.writeInt(0);
			pw.write(-1);*/
		}

		return pw.getPacket();
	}

	public static byte[] makeMonsterReal(MapleMonster life, boolean azwan) {
		return spawnMonster(life, -1, 0, azwan);
	}

	public static byte[] makeMonsterFake(MapleMonster life, boolean azwan) {
		return spawnMonster(life, -4, 0, azwan);
	}

	public static byte[] makeMonsterEffect(MapleMonster life, int effect, boolean azwan) {
		return spawnMonster(life, effect, 0, azwan);
	}

	public static byte[] moveMonsterResponse(int objectid, short moveid, int currentMp, boolean useSkills, int skillId,
			int skillLevel) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.MOVE_MONSTER_RESPONSE.getValue());
		pw.writeInt(objectid);
		
		pw.writeShort(moveid);
		pw.write(useSkills ? 1 : 0);
		pw.writeInt(currentMp); // ...
		pw.writeInt(skillId); // ...
		pw.write(skillLevel);
		pw.writeInt(0); // // attack id
		return pw.getPacket();
	}

	public static byte[] getMonsterSkill(int objectid) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.MONSTER_SKILL.getValue());
		pw.writeInt(objectid);
		pw.writeLong(0);

		return pw.getPacket();
	}

	public static byte[] getMonsterTeleport(int objectid, int x, int y) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.TELE_MONSTER.getValue());
		pw.writeInt(objectid);
		pw.writeInt(x);
		pw.writeInt(y);

		return pw.getPacket();
	}

	private static void getLongMask_NoRef(PacketWriter pw, Collection<MonsterStatusEffect> ss,
			boolean ignore_imm) {
		int[] mask = new int[12];
		for (MonsterStatusEffect statup : ss) {
			if ((statup != null) && (statup.getStati() != MonsterStatus.WEAPON_DAMAGE_REFLECT)
					&& (statup.getStati() != MonsterStatus.MAGIC_DAMAGE_REFLECT)
					&& ((!ignore_imm) || ((statup.getStati() != MonsterStatus.WEAPON_IMMUNITY)
							&& (statup.getStati() != MonsterStatus.MAGIC_IMMUNITY)
							&& (statup.getStati() != MonsterStatus.DAMAGE_IMMUNITY)))) {
				mask[(statup.getStati().getPosition() - 1)] |= statup.getStati().getValue();
			}
		}
		for (int i = mask.length; i >= 1; i--) {
			pw.writeInt(mask[(i - 1)]);
		}
	}

	public static byte[] applyMonsterStatus(int oid, MonsterStatus mse, int x, MobSkill skil) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
		pw.writeInt(oid);
		PacketHelper.writeSingleMask(pw, mse);

		pw.writeInt(x);
		pw.writeShort(skil.getSkillId());
		pw.writeShort(skil.getSkillLevel());
		pw.writeShort(mse.isEmpty() ? 1 : 0);

		pw.writeShort(0);
		pw.write(2);// was 1
		pw.write(new byte[30]);

		return pw.getPacket();
	}

	public static byte[] applyMonsterStatus(MapleMonster mons, MonsterStatusEffect ms) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
		pw.writeInt(mons.getObjectId());
		PacketHelper.writeSingleMask(pw, ms.getStati());

		pw.writeInt(ms.getX().intValue());
		if (ms.isMonsterSkill()) {
			pw.writeShort(ms.getMobSkill().getSkillId());
			pw.writeShort(ms.getMobSkill().getSkillLevel());
		} else if (ms.getSkill() > 0) {
			pw.writeInt(ms.getSkill());
		}
		pw.writeShort((short) ((ms.getCancelTask() - System.currentTimeMillis()) / 1000));

		pw.writeLong(0L);
		pw.writeShort(0);
		pw.write(1);

		return pw.getPacket();
	}

	public static byte[] applyMonsterStatus(MapleMonster mons, List<MonsterStatusEffect> mse) {
		if ((mse.size() <= 0) || (mse.get(0) == null)) {
			return CWvsContext.enableActions();
		}
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
		pw.writeInt(mons.getObjectId());
		MonsterStatusEffect ms = (MonsterStatusEffect) mse.get(0);
		if (ms.getStati() == MonsterStatus.POISON) {
			PacketHelper.writeSingleMask(pw, MonsterStatus.EMPTY);
			pw.write(mse.size());
			for (MonsterStatusEffect m : mse) {
				pw.writeInt(m.getFromID());
				if (m.isMonsterSkill()) {
					pw.writeShort(m.getMobSkill().getSkillId());
					pw.writeShort(m.getMobSkill().getSkillLevel());
				} else if (m.getSkill() > 0) {
					pw.writeInt(m.getSkill());
				}
				pw.writeInt(m.getX().intValue());
				pw.writeInt(1000);
				pw.writeInt(0);
				pw.writeInt(8000);// new v141
				pw.writeInt(6);
				pw.writeInt(0);
			}
			pw.writeShort(1000);// was 300
			pw.write(2);// was 1
			// pw.write(1);
		} else {
			PacketHelper.writeSingleMask(pw, ms.getStati());

			pw.writeInt(ms.getX().intValue());
			if (ms.isMonsterSkill()) {
				pw.writeShort(ms.getMobSkill().getSkillId());
				pw.writeShort(ms.getMobSkill().getSkillLevel());
			} else if (ms.getSkill() > 0) {
				pw.writeInt(ms.getSkill());
			}
			pw.writeShort((short) ((ms.getCancelTask() - System.currentTimeMillis()) / 1000));
			pw.writeLong(0L);
			pw.writeShort(0);
			pw.write(1);
		}
		// System.out.println("Monsterstatus3");
		return pw.getPacket();
	}

	public static byte[] applyMonsterStatus(int oid, Map<MonsterStatus, Integer> stati, List<Integer> reflection,
			MobSkill skil) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
		pw.writeInt(oid);
		PacketHelper.writeMask(pw, stati.keySet());

		for (Map.Entry mse : stati.entrySet()) {
			pw.writeInt(((Integer) mse.getValue()).intValue());
			pw.writeInt(skil.getSkillId());
			pw.writeShort((short) skil.getDuration());
		}

		for (Integer ref : reflection) {
			pw.writeInt(ref.intValue());
		}
		pw.writeLong(0L);
		pw.writeShort(0);

		int size = stati.size();
		if (reflection.size() > 0) {
			size /= 2;
		}
		pw.write(size);
		return pw.getPacket();
	}

	public static byte[] applyPoison(MapleMonster mons, List<MonsterStatusEffect> mse) {
		if ((mse.size() <= 0) || (mse.get(0) == null)) {
			return CWvsContext.enableActions();
		}
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
		pw.writeInt(mons.getObjectId());
		PacketHelper.writeSingleMask(pw, MonsterStatus.EMPTY);
		pw.write(mse.size());
		for (MonsterStatusEffect m : mse) {
			pw.writeInt(m.getFromID());
			if (m.isMonsterSkill()) {
				pw.writeShort(m.getMobSkill().getSkillId());
				pw.writeShort(m.getMobSkill().getSkillLevel());
			} else if (m.getSkill() > 0) {
				pw.writeInt(m.getSkill());
			}
			pw.writeInt(m.getX().intValue());
			pw.writeInt(1000);
			pw.writeInt(0);// 600574518?
			pw.writeInt(8000);// war 7000
			pw.writeInt(6);// was 5
			pw.writeInt(0);
		}
		pw.writeShort(1000);// was 300
		pw.write(2);// was 1
		// pw.write(1);
		// System.out.println("Monsterstatus5");
		return pw.getPacket();
	}

	public static byte[] cancelMonsterStatus(int oid, MonsterStatus stat) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.CANCEL_MONSTER_STATUS.getValue());
		pw.writeInt(oid);
		PacketHelper.writeSingleMask(pw, stat);
		pw.write(4);// was1
		pw.write(2);

		return pw.getPacket();
	}

	public static byte[] cancelPoison(int oid, MonsterStatusEffect m) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.CANCEL_MONSTER_STATUS.getValue());
		pw.writeInt(oid);
		PacketHelper.writeSingleMask(pw, MonsterStatus.EMPTY);
		pw.writeInt(0);
		pw.writeInt(1);
		pw.writeInt(m.getFromID());
		if (m.isMonsterSkill()) {
			pw.writeShort(m.getMobSkill().getSkillId());
			pw.writeShort(m.getMobSkill().getSkillLevel());
		} else if (m.getSkill() > 0) {
			pw.writeInt(m.getSkill());
		}
		pw.write(3);

		return pw.getPacket();
	}

	public static byte[] talkMonster(int oid, int itemId, String msg) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.TALK_MONSTER.getValue());
		pw.writeInt(oid);
		pw.writeInt(500);
		pw.writeInt(itemId);
		pw.write(itemId <= 0 ? 0 : 1);
		pw.write((msg == null) || (msg.length() <= 0) ? 0 : 1);
		if ((msg != null) && (msg.length() > 0)) {
			pw.writeMapleAsciiString(msg);
		}
		pw.writeInt(1);

		return pw.getPacket();
	}

	public static byte[] removeTalkMonster(int oid) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.REMOVE_TALK_MONSTER.getValue());
		pw.writeInt(oid);

		return pw.getPacket();
	}

	public static final byte[] getNodeProperties(MapleMonster objectid, MapleMap map) {
		if (objectid.getNodePacket() != null) {
			return objectid.getNodePacket();
		}
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.MONSTER_PROPERTIES.getValue());
		pw.writeInt(objectid.getObjectId());
		pw.writeInt(map.getNodes().size());
		pw.writeInt(objectid.getPosition().x);
		pw.writeInt(objectid.getPosition().y);
		for (MapleNodes.MapleNodeInfo mni : map.getNodes()) {
			pw.writeInt(mni.x);
			pw.writeInt(mni.y);
			pw.writeInt(mni.attr);
			if (mni.attr == 2) {
				pw.writeInt(500);
			}
		}
		pw.writeInt(0);
		pw.write(0);
		pw.write(0);

		objectid.setNodePacket(pw.getPacket());
		return objectid.getNodePacket();
	}

	public static byte[] showMagnet(int mobid, boolean success) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.SHOW_MAGNET.getValue());
		pw.writeInt(mobid);
		pw.write(success ? 1 : 0);
		pw.write(0);

		return pw.getPacket();
	}

	public static byte[] catchMonster(int mobid, int itemid, byte success) {
		PacketWriter pw = new PacketWriter();

		pw.writeShort(SendPacketOpcode.CATCH_MONSTER.getValue());
		pw.writeInt(mobid);
		pw.writeInt(itemid);
		pw.write(success);

		return pw.getPacket();
	}
}
