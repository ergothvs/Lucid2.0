package handling.login.handler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.PartTimeJob;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.WorldConstants.WorldOption;
import handling.channel.ChannelServer;
import handling.login.LoginInformationProvider;
import handling.login.LoginInformationProvider.JobType;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.data.LittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.CField;
import tools.packet.LoginPacket;

public class CharLoginHandler {

    private static boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 3;
    }

    public static void updateCCards(final LittleEndianAccessor slea, final MapleClient c) {
        if (slea.available() != 24 || !c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        final Map<Integer, Integer> cids = new LinkedHashMap<>();
        for (int i = 1; i <= 6; i++) { // 6 chars
            final int charId = slea.readInt();
            if ((!c.login_Auth(charId) && charId != 0) || ChannelServer.getInstance(c.getChannel()) == null || !WorldOption.isExists(c.getWorld())) {
                c.getSession().close();
                return;
            }
            cids.put(i, charId);
        }
        c.updateCharacterCards(cids);
    }

    public static void CreateUltimate(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.getPlayer().isGM() && (!c.isLoggedIn() || c.getPlayer() == null || c.getPlayer().getLevel() < 120 || c.getPlayer().getMapId() != 130000000 || c.getPlayer().getQuestStatus(20734) != 0 || c.getPlayer().getQuestStatus(20616) != 2 || !GameConstants.isKOC(c.getPlayer().getJob()) || !c.canMakeCharacter(c.getPlayer().getWorld()))) {
            c.getSession().write(CField.createUltimate(2));
            //Character slots are full. Please purchase another slot from the Cash Shop.
            return;
        }
        final String name = slea.readMapleAsciiString();
        final int job = slea.readInt(); //job ID

        final int face = slea.readInt();
        final int hair = slea.readInt();

        //No idea what are these used for:
        final int hat = slea.readInt();
        final int top = slea.readInt();
        final int glove = slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();

        final byte gender = c.getPlayer().getGender();

        //JobType errorCheck = JobType.Adventurer;
        //if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, errorCheck.type, face)) {
        //    c.getSession().write(CWvsContext.enableActions());
        //    return;
        //}
        JobType jobType = JobType.UltimateAdventurer;

        MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
        newchar.setJob(job);
        newchar.setWorld((byte) c.getPlayer().getWorld());
        newchar.setFace(face);
        newchar.setHair(hair);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor((byte) 3); //troll
        newchar.setLevel((short) 50);
        newchar.getStat().str = (short) 4;
        newchar.getStat().dex = (short) 4;
        newchar.getStat().int_ = (short) 4;
        newchar.getStat().luk = (short) 4;
        newchar.setRemainingAp((short) 254); //49*5 + 25 - 16
        newchar.setRemainingSp(job / 100 == 2 ? 128 : 122); //2 from job advancements. 120 from leveling. (mages get +6)
        newchar.getStat().maxhp += 150; //Beginner 10 levels
        newchar.getStat().maxmp += 125;
        switch (job) {
            case 110:
            case 120:
            case 130:
                newchar.getStat().maxhp += 600; //Job Advancement
                newchar.getStat().maxhp += 2000; //Levelup 40 times
                newchar.getStat().maxmp += 200;
                break;
            case 210:
            case 220:
            case 230:
                newchar.getStat().maxmp += 600;
                newchar.getStat().maxhp += 500; //Levelup 40 times
                newchar.getStat().maxmp += 2000;
                break;
            case 310:
            case 320:
            case 410:
            case 420:
            case 520:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 900; //Levelup 40 times
                newchar.getStat().maxmp += 600;
                break;
            case 510:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 450; //Levelup 20 times
                newchar.getStat().maxmp += 300;
                newchar.getStat().maxhp += 800; //Levelup 20 times
                newchar.getStat().maxmp += 400;
                break;
            default:
                return;
        }
        //TODO: Make this GMS - Like
        for (int i = 2490; i < 2507; i++) {
            newchar.setQuestAdd(MapleQuest.getInstance(i), (byte) 2, null);
        }
        newchar.setQuestAdd(MapleQuest.getInstance(29947), (byte) 2, null);
        newchar.setQuestAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER), (byte) 0, c.getPlayer().getName());

        final Map<Skill, SkillEntry> ss = new HashMap<>();
        ss.put(SkillFactory.getSkill(1074 + (job / 100)), new SkillEntry((byte) 5, (byte) 5, -1));
        ss.put(SkillFactory.getSkill(80), new SkillEntry((byte) 1, (byte) 1, -1));
        newchar.changeSkillLevel_Skip(ss, false);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        //TODO: Make this GMS - Like
        int[] items = new int[]{1142257, hat, top, shoes, glove, weapon, hat + 1, top + 1, shoes + 1, glove + 1, weapon + 1}; //brilliant = fine+1
        for (byte i = 0; i < items.length; i++) {
            Item item = li.getEquipById(items[i]);
            item.setPosition((byte) (i + 1));
            newchar.getInventory(MapleInventoryType.EQUIP).addFromDB(item);
        }

        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (byte) 0, (short) 200, (byte) 0));
        if (MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm())) {
            MapleCharacter.saveNewCharToDB(newchar, jobType, (short) 0);
            MapleQuest.getInstance(20734).forceComplete(c.getPlayer(), 1101000);
            c.getSession().write(CField.createUltimate(0));
        } else if (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()) {
            c.getSession().write(CField.createUltimate(3)); //"You cannot use this name."
        } else {
            c.getSession().write(CField.createUltimate(1));
        }
    }

    public static void DeleteChar(final LittleEndianAccessor lea, final MapleClient c) {
        String Secondpw_Client = lea.readMapleAsciiString();
        if (Secondpw_Client == null) {
            if (lea.readByte() > 0) { // Specific if user have second password or not
                Secondpw_Client = lea.readMapleAsciiString();
            }
            lea.readMapleAsciiString();
        }

        final int Character_ID = lea.readInt();

        if (!c.login_Auth(Character_ID) || !c.isLoggedIn() || loginFailCount(c)) {
            c.getSession().close();
            return; // Attempting to delete other character
        }
        byte state = 0;

        if (c.getSecondPassword() != null) { // On the server, there's a second password
            if (Secondpw_Client == null) { // Client's hacking
                c.getSession().close();
                return;
            } else {
                if (!c.CheckSecondPassword(Secondpw_Client)) { // Wrong Password
                    state = 20;
                }
            }
        }

        if (state == 0) {
            state = (byte) c.deleteCharacter(Character_ID);
        }
        c.getSession().write(LoginPacket.deleteCharResponse(Character_ID, state));
    }

    public static void partTimeJob(final LittleEndianAccessor slea, final MapleClient c) {
        System.out.println("[Part Time Job] data: " + slea);
        byte mode = slea.readByte(); //1 = start 2 = end
        int cid = slea.readInt(); //character id
        byte job = slea.readByte(); //part time job
        if (mode == 0) {
            LoginPacket.partTimeJob(cid, (byte) 0, System.currentTimeMillis());
        } else if (mode == 1) {
            LoginPacket.partTimeJob(cid, job, System.currentTimeMillis());
        }
    }

    public static void PartJob(LittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer() != null || !c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        final byte mode = slea.readByte();
        final int cid = slea.readInt();
        if (mode == 1) {
            final PartTimeJob partTime = MapleCharacter.getPartTime(cid);
            final byte job = slea.readByte();
            if (/*chr.getLevel() < 30 || */job < 0 || job > 5 || partTime.getReward() > 0
                    || (partTime.getJob() > 0 && partTime.getJob() <= 5)) {
                c.getSession().close();
                return;
            }
            partTime.setTime(System.currentTimeMillis());
            partTime.setJob(job);
            c.getSession().write(LoginPacket.updatePartTimeJob(partTime));
            MapleCharacter.removePartTime(cid);
            MapleCharacter.addPartTime(partTime);
        } else if (mode == 2) {
            final PartTimeJob partTime = MapleCharacter.getPartTime(cid);
            if (/*chr.getLevel() < 30 || */partTime.getReward() > 0
                    || partTime.getJob() < 0 || partTime.getJob() > 5) {
                c.getSession().close();
                return;
            }
            final long distance = (System.currentTimeMillis() - partTime.getTime()) / (60 * 60 * 1000L);
            if (distance > 1) {
                partTime.setReward((int) (((partTime.getJob() + 1) * 1000L) + distance));
            } else {
                partTime.setJob((byte) 0);
                partTime.setReward(0);
            }
            partTime.setTime(System.currentTimeMillis());
            MapleCharacter.removePartTime(cid);
            MapleCharacter.addPartTime(partTime);
            c.getSession().write(LoginPacket.updatePartTimeJob(partTime));
        }
    }
}
