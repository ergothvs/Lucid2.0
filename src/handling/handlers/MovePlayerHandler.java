package handling.handlers;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.List;

import client.CharacterTemporaryStat;
import client.MapleCharacter;
import client.MapleClient;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.channel.handler.MovementParse;
import server.Timer;
import server.maps.MapleMap;
import server.movement.LifeMovementFragment;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;

public class MovePlayerHandler {

	@PacketHandler(opcode = RecvPacketOpcode.MOVE_PLAYER)
	public static void handle(MapleClient c, LittleEndianAccessor slea) {
		MapleCharacter chr = c.getPlayer();
		
		slea.skip(1); // the type.
		slea.skip(13);
        short x = slea.readShort();
        short y = slea.readShort();
        slea.skip(4);
                
        if (chr == null) {
            return;
        }
        
        final Point originalPos = chr.getPosition();
        List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 1, chr);

        if (res != null && c.getPlayer().getMap() != null) {
            if ((slea.available() < 10L) || (slea.available() > 26L)) {
//                 if (slea.available() != 18L) {
                return;
            }
            final MapleMap map = c.getPlayer().getMap();

            if (chr.isHidden()) {
                chr.setLastRes(res);
                c.getPlayer().getMap().broadcastGMMessage(chr, CField.movePlayer(chr.getId(), res, originalPos), false);
            } else {
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.movePlayer(chr.getId(), res, originalPos), false);
            }

            MovementParse.updatePosition(res, chr, 0);
            
            final Point pos = chr.getTruePosition();
            map.movePlayer(chr, pos);
            if ((chr.getFollowId() > 0) && (chr.isFollowOn()) && (chr.isFollowInitiator())) {
                MapleCharacter fol = map.getCharacterById(chr.getFollowId());
                if (fol != null) {
                    Point original_pos = fol.getPosition();
                    fol.getClient().getSession().write(CField.moveFollow(originalPos, original_pos, pos, res));
                    MovementParse.updatePosition(res, fol, 0);
                    map.movePlayer(fol, pos);
                    map.broadcastMessage(fol, CField.movePlayer(fol.getId(), res, original_pos), false);
                } else {
                    chr.checkFollow();
                }
            }
            
            WeakReference<MapleCharacter>[] clones = chr.getClones();
            for (int i = 0; i < clones.length; i++) {
                if (clones[i].get() != null) {
                    final MapleCharacter clone = clones[i].get();
                    final List<LifeMovementFragment> res3 = res;
                    Timer.CloneTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (clone.getMap() == map) {
                                	byte [] packet = CField.movePlayer(clone.getId(), res3, originalPos);
                                    if (clone.isHidden()) {
                                        map.broadcastGMMessage(clone, packet, false);
                                    } else {
                                        map.broadcastMessage(clone, packet, false);
                                    }
                                    MovementParse.updatePosition(res3, clone, 0);
                                    map.movePlayer(clone, pos);
                                }
                            } catch (Exception e) {
                                System.out.println("Something bad happened idk");
                            }
                        }
                    }, 500 * i + 500);
                }
            }

            int count = c.getPlayer().getFallCounter();
            boolean samepos = (pos.y > c.getPlayer().getOldPosition().y) && (Math.abs(pos.x - c.getPlayer().getOldPosition().x) < 5);
            if ((samepos) && ((pos.y > map.getBottom() + 250) || (map.getFootholds().findBelow(pos) == null))) {
                if (count > 5) {
                    c.getPlayer().changeMap(map, map.getPortal(0));
                    c.getPlayer().setFallCounter(0);
                } else {
                    count++;
                    c.getPlayer().setFallCounter(count);
                }
            } else if (count > 0) {
                c.getPlayer().setFallCounter(0);
            }
            c.getPlayer().setOldPosition(pos);
            if ((!samepos) && (c.getPlayer().getBuffSource(CharacterTemporaryStat.BMageAura) == 32120000)) {
                c.getPlayer().getStatForBuff(CharacterTemporaryStat.BMageAura).applyMonsterBuff(c.getPlayer());
            } else if ((!samepos) && (c.getPlayer().getBuffSource(CharacterTemporaryStat.BMageAura) == 32120001)) {
                c.getPlayer().getStatForBuff(CharacterTemporaryStat.BMageAura).applyMonsterBuff(c.getPlayer());
            }
        }
	}
}
