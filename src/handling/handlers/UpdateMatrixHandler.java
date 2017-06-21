package handling.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.inventory.VMatrixRecord;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

/**
 * Based off of PacketBakery's release at http://forum.ragezone.com/f427/xmas-release-matrix-system-5th-1122677/.
 */
public class UpdateMatrixHandler {

    @PacketHandler(opcode = RecvPacketOpcode.UPDATE_MATRIX)
    public static void handle(MapleClient c, LittleEndianAccessor lea){
        MapleCharacter chr = c.getPlayer();
        boolean active = lea.readInt() > 0;
        int slot = lea.readInt();
        lea.readInt(); // always -1
        VMatrixRecord vmr = null;
        if(slot < chr.getVMatrixRecords().size()) {
            vmr = chr.getVMatrixRecords().get(slot);
        }
        if(vmr != null){
            if(active){
                vmr.setActive(false);
                // first skill info
                Skill skill = chr.getSkill(vmr.getSkillID1());
                if(chr.getSkill(vmr.getSkillID1()) == null){
                    skill = SkillFactory.getSkill(vmr.getSkillID1());
                }
                int skillLv = chr.getSkillLevel(vmr.getSkillID1()) - vmr.getSkillLv();
                chr.changeSingleSkillLevel(skill, skillLv, (byte) skill.getMasterLevel());
                if(vmr.isBoostNode()){
                    // second skill info
                    skill = chr.getSkill(vmr.getSkillID2());
                    if(chr.getSkill(vmr.getSkillID2()) == null){
                        skill = SkillFactory.getSkill(vmr.getSkillID2());
                    }
                    skillLv = chr.getSkillLevel(vmr.getSkillID2()) - vmr.getSkillLv();
                    chr.changeSingleSkillLevel(skill, skillLv, (byte) skill.getMasterLevel());

                    // third skill info
                    skill = chr.getSkill(vmr.getSkillID3());
                    if(chr.getSkill(vmr.getSkillID3()) == null){
                        skill = SkillFactory.getSkill(vmr.getSkillID3());
                    }
                    skillLv = chr.getSkillLevel(vmr.getSkillID3()) - vmr.getSkillLv();
                    chr.changeSingleSkillLevel(skill, skillLv, (byte) skill.getMasterLevel());
                }
            }else{
                vmr.setActive(true);
                // first skill info
                Skill skill = chr.getSkill(vmr.getSkillID1());
                if(chr.getSkill(vmr.getSkillID1()) == null){
                    skill = SkillFactory.getSkill(vmr.getSkillID1());
                }
                int skillLv = chr.getSkillLevel(vmr.getSkillID1()) + vmr.getSkillLv();
                chr.changeSingleSkillLevel(skill, skillLv, (byte) skill.getMasterLevel());
                if(vmr.isBoostNode()){
                    // second skill info
                    skill = chr.getSkill(vmr.getSkillID2());
                    if(chr.getSkill(vmr.getSkillID2()) == null){
                        skill = SkillFactory.getSkill(vmr.getSkillID2());
                    }
                    skillLv = chr.getSkillLevel(vmr.getSkillID2()) + vmr.getSkillLv();
                    chr.changeSingleSkillLevel(skill, skillLv, (byte) skill.getMasterLevel());

                    // third skill info
                    skill = chr.getSkill(vmr.getSkillID3());
                    if(chr.getSkill(vmr.getSkillID3()) == null){
                        skill = SkillFactory.getSkill(vmr.getSkillID3());
                    }
                    skillLv = chr.getSkillLevel(vmr.getSkillID3()) + vmr.getSkillLv();
                    chr.changeSingleSkillLevel(skill, skillLv, (byte) skill.getMasterLevel());
                }
            }
            c.getSession().write(CWvsContext.updateVMatrix(chr.getVMatrixRecords()));
        }else{
            chr.dropMessage(6, "You tried to update a vSkill that was not in your current list of skills.");
        }
        c.getSession().write(CWvsContext.updateVMatrix(chr.getVMatrixRecords()));
    }
}
