package handling.handlers;

import java.awt.Point;
import java.util.List;

import client.MapleClient;
import constants.GameConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.handler.MovementParse;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.movement.LifeMovementFragment;
import tools.Randomizer;
import tools.data.LittleEndianAccessor;
import tools.packet.MobPacket;

public class MoveLifeHandler {

	@PacketHandler(opcode = RecvPacketOpcode.MOVE_LIFE)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		
		if (c.getPlayer() == null) {
            return;
        }
        
        MapleMap map = c.getPlayer().getMap();
        if (map == null) {
        	return;
        }
        
        int oid = lea.readInt();
        
        MapleMonster monster = map.getMonsterByOid(oid);
        if (monster == null || monster.getLinkCID() > 0) {
            return;
        }
        
        lea.skip(1);
        short moveid = lea.readShort();
        boolean useSkill = lea.readByte() > 0;
        byte skill = lea.readByte();
        int unk = lea.readInt();
        
        int realskill = 0;
        int level = 0;

        /*if (useSkill) {
            byte size = monster.getNoSkills();
            boolean used = false;

            if (size > 0) {
                Pair skillToUse = (Pair) monster.getSkills().get((byte) Randomizer.nextInt(size));
                realskill = ((Integer) skillToUse.getLeft());
                level = ((Integer) skillToUse.getRight());

                MobSkill mobSkill = MobSkillFactory.getMobSkill(realskill, level);

                if ((mobSkill != null) && (!mobSkill.checkCurrentBuff(c.getPlayer(), monster))) {
                    long now = System.currentTimeMillis();
                    long ls = monster.getLastSkillUsed(realskill);

                    if ((ls == 0L) || ((now - ls > mobSkill.getCoolTime()) && (!mobSkill.onlyOnce()))) {
                        monster.setLastSkillUsed(realskill, now, mobSkill.getCoolTime());

                        int reqHp = (int) ((float) monster.getHp() / (float) monster.getMobMaxHp() * 100.0F);
                        if (reqHp <= mobSkill.getHP()) {
                            used = true;
                            mobSkill.applyEffect(c.getPlayer(), monster, true);
                        }
                    }
                }
            }
            if (!used) {
                realskill = 0;
                level = 0;
            }
        }*/
        
        lea.readByte();
        // for: short, short
        
        lea.readByte();
        // for: short
        
        // ?
        lea.readByte();
        
        // ?
        lea.readInt();
        
        // ?
        lea.readInt();
        lea.readInt();
        
        // ?
        lea.skip(5);
        
        lea.readInt(); // tEncodedGatherDuration
        short x = lea.readShort();
        short y = lea.readShort();
        short vx = lea.readShort();
        short vy = lea.readShort();
        
        Point startPos = monster.getPosition();
        
        List<LifeMovementFragment> res = MovementParse.parseMovement(lea, 2);
        
        if ((GameConstants.isLuminous(c.getPlayer().getJob())) && (Randomizer.nextInt(100) < 20)) {
            c.getPlayer().applyBlackBlessingBuff(1);
        }

        if ((res != null) && (c.getPlayer() != null) && (res.size() > 0)) {
            c.getSession().write(MobPacket.moveMonsterResponse(monster.getObjectId(), moveid, monster.getMp(), monster.isControllerHasAggro(), realskill, level));
            
            if (monster.isControllerHasAggro()) {
                c.getSession().write(MobPacket.getMonsterSkill(monster.getObjectId()));
            }
            
            MovementParse.updatePosition(res, monster, -1);
            Point endPos = monster.getTruePosition();
            map.moveMonster(monster, endPos);
            map.broadcastMessage(c.getPlayer(), MobPacket.moveMonster(useSkill, skill, unk, monster.getObjectId(), startPos, res), endPos);
        }
        
	}
}
