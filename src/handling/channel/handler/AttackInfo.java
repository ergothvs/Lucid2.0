/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.channel.handler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import client.MapleCharacter;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import server.MapleStatEffect;
import tools.AttackPair;
import tools.HexTool;

public class AttackInfo {

	public byte speed;  // attack speed
	public byte csstar; 
	public byte AOE; 
	public byte slot; 
	public byte unk;
	
	public int skillid;
    public int charge;
    public int lastAttackTickCount;
    public int nMobCount;
    public int display;
    
    public boolean real = true;
    
    public Point position;
    public List<AttackPair> allDamage = new ArrayList<AttackPair>();
    
    public byte getHits() {
    	return ((byte) (nMobCount & 0xF));
    }

    public byte getTargets() {
    	return ((byte) (nMobCount >>> 4 & 0xF));
    }
    
    public byte getSpeed() {
    	return speed;
    }
    
    public void setSpeed(byte speed) {
    	this.speed = speed;
    }
    
    public int getSkillId() {
    	return skillid;
    }
    
    public void setSkillId(int skillid) {
    	this.skillid = skillid;
    }
    
    public final MapleStatEffect getAttackEffect(final MapleCharacter chr, int skillLevel, final Skill skill_) {
        
    	if (GameConstants.isMulungSkill(skillid) || GameConstants.isPyramidSkill(skillid) || GameConstants.isInflationSkill(skillid)) {
            skillLevel = 1;
        } else if (skillLevel <= 0) {
            return null;
        }
        
        int dd = ((display & 0x8000) != 0 ? (display - 0x8000) : display);
        if (GameConstants.isLinkedAttackSkill(skillid)) {
            final Skill skillLink = SkillFactory.getSkill(skillid);
            if (1 == 1) { //is bugged after red
                return skillLink.getEffect(skillLevel);
            }

            if (dd > SkillFactory.Delay.magic6.i && dd != SkillFactory.Delay.shot.i && dd != SkillFactory.Delay.fist.i) {
                
            	if (skillLink.getAnimation() == -1 || Math.abs(skillLink.getAnimation() - dd) > 0x10) {
                    chr.dropMessage(-1, "Animation: " + skillLink.getAnimation() + " | " + HexTool.getOpcodeToString(skillLink.getAnimation()));
                    
                    if (skillLink.getAnimation() == -1) {
                        chr.dropMessage(5, "Please report this: animation for skill " + skillLink.getId() + " doesn't exist");
                    } else {
                        //AutobanManager.getInstance().autoban(chr.getClient(), "No delay hack, SkillID : " + skillLink.getId() + ", animation: " + dd + ", expected: " + skillLink.getAnimation());
                    }
                    
                    if (skill_.getId() == 24121003) {
                        return skillLink.getEffect(skillLevel);
                    }
                    
                    if (GameConstants.isZero(skill_.getId() / 10000)) {
                        return skillLink.getEffect(skillLevel);
                    }
                    
                    return null;
                }
                
            }
            return skillLink.getEffect(skillLevel);
        } 
        return skill_.getEffect(skillLevel);
    }
}
