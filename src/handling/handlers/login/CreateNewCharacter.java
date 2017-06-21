package handling.handlers.login;

import java.util.HashMap;
import java.util.Map;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.JobConstants;
import handling.PacketHandler;
import handling.RecvPacketOpcode;
import handling.login.LoginInformationProvider;
import handling.login.LoginInformationProvider.JobType;
import server.MapleItemInformationProvider;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class CreateNewCharacter {

	@PacketHandler(opcode = RecvPacketOpcode.CREATE_CHAR)
	public static void handle(MapleClient c, LittleEndianAccessor lea) {
		
		String name;
		byte gender, skin;
		short subcategory;
		int face, hair, hairColor = -1, hat = -1, top, bottom = -1, shoes = -1, weapon = -1, cape = -1, faceMark = -1, shield = -1;
		
		name = lea.readMapleAsciiString();

		if (!MapleCharacterUtil.canCreateChar(name, false)) {
			System.out.println("char name hack: " + name);
			return;
		}

		lea.skip(4); // key type setting
		lea.skip(4); // -1
		
		int job_type = lea.readInt();
		JobType job = JobType.getByType(job_type);
		
		if (job == null) {
			System.out.println("New job type found: " + job_type);
			return;
		}

		for (JobConstants.LoginJob j : JobConstants.LoginJob.values()) {
			if (j.getJobType() == job_type) {
				if (j.getFlag() != JobConstants.LoginJob.JobFlag.ENABLED.getFlag()) {
					System.out.println("job was tried to be created while not enabled");
					return;
				}
			}
		}

		subcategory = lea.readShort();
		gender = lea.readByte();
		skin = lea.readByte();
		
		lea.skip(1); // the amount of items a new character will receive.
		
		face = lea.readInt();
		hair = lea.readInt();
		
		if (job.hairColor) {
			hairColor = lea.readInt();
		}
		
		if (job.skinColor) {
			lea.readInt();
		}
		
		if (job.faceMark) {
			faceMark = lea.readInt();
		}
		
		if (job.hat) {
			hat = lea.readInt();
		}
		
		top = lea.readInt();
		
		if (job.bottom) {
			bottom = lea.readInt();
		}
		
		if (job.cape) {
			cape = lea.readInt();
		}
                if (job.shoes && job != JobType.KINESIS) {
		shoes = lea.readInt();
                }
                
                if (job.weapon && job != JobType.KINESIS) {
		weapon = lea.readInt();
                }
                
		if (lea.available() >= 4) {
			shield = lea.readInt();
		}

		int index = 0;
		boolean noSkin = job == JobType.Demon || job == JobType.Mercedes || job == JobType.Jett;
		int[] items = new int[] { face, hair, hairColor, noSkin ? -1 : skin, faceMark, hat, top, bottom, cape, shoes, weapon, shield };
		for (int i : items) {
			if (i > -1) {
				index++;
			}
		}

		MapleCharacter newchar = MapleCharacter.getDefault(c, job);
		newchar.setWorld((byte) c.getWorld());
		newchar.setFace(face);
		newchar.setSecondFace(face);
		
		if (hairColor < 0) {
			hairColor = 0;
		}
		
		if (job != JobType.Mihile) {
			hair += hairColor;
		}
		
		newchar.setHair(hair);
		newchar.setSecondHair(hair);
		
		if (job == JobType.AngelicBuster) {
			newchar.setSecondFace(21173);
			newchar.setSecondHair(37141);
			newchar.setLevel((short) 10);
			newchar.getStat().int_ = 4;
			newchar.getStat().dex = 57;
			newchar.getStat().maxhp = 1500;
			newchar.getStat().hp = 1500;
			newchar.getStat().maxmp = 1500;
			newchar.getStat().mp = 1500;
			newchar.setRemainingSp(3);
		} else if (job == JobType.Zero) {
			newchar.setSecondFace(21290);
			newchar.setSecondHair(37623);
		}
		
		newchar.setGender(gender);
		newchar.setName(name);
		newchar.setSkinColor(skin);
		
		if (faceMark < 0) {
			faceMark = 0;
		}
		
		newchar.setFaceMarking(faceMark);
		final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
		final MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
		Item item;
		
		// TODO: Check zero's beta weapon slot
		
		/**
		 * -1 Hat | -2 Face | -3 Eye acc | -4 Ear acc | -5 Topwear
		 * -6 Bottom | -7 Shoes | -9 Cape | -10 Shield | -11 Weapon
		 * 
		 */
		int[][] equips = new int[][] { { hat, -1 }, { top, -5 }, { bottom, -6 }, { cape, -9 },  { shoes, -7 }, { weapon, -11 },  { shield, -10 } };
		for (int[] i : equips) {
			if (i[0] > 0) {
				item = ii.getEquipById(i[0]);
				item.setPosition((byte) i[1]);
				item.setGMLog("Character Creation");
				equip.addFromDB(item);
			}
		}

		
		/**
		 * Additional skills for all first job classes. Some skills are not added by default,
		 * so adding the skillid ID here between the {}, will give the skills you entered to the desired job.
		 * 
		 */
		
		int[][] skills = new int[][] { { 80001152 }, // Resistance
				{ 80001152, 1281 }, // Explorer
				{ 10001244, 10000252, 80001152 }, // Cygnus
				{ 20000194 }, // Aran
				{ 20010022, 20010194 }, // Evan
				{ 20020109, 20021110, 20020111, 20020112 }, // Mercedes
				{ 30010112, 30010110, 30010111, 30010185 }, // Demon
				{ 20031251, 20030204, 20030206, 20031208, 20031207, 20031203 }, // Phantom
				{ 80001152, 1281 }, // Dualblade
				{ 50001214 }, // Mihile
				// {},// Luminous
				{ 20040216, 20040217, 20040218, 20040219, 20040220, 20040221, 20041222, 27001100, 27000207, 27001201 }, // Luminous
				{}, // Kaiser
				{ 60011216, 60010217, 60011218, 60011219, 60011220, 60011221, 60011222 }, // Angelic
																							// Buster
				{}, // Cannoneer
				{ 30020232, 30020233, 30020234, 30020240, 30021238 }, // Xenon
				{ 100000279, 100000282, 100001262, 100001263, 100001264, 100001265, 100001266, 100001268 }, // Zero
				{ 228, 80001151 }, // Jett
				{}, // Hayato
				{ 40020000, 40020001, 40020002, 40021023, 40020109 }// Kanna
		}; /*

		if (skills[job.type].length > 0) {
			final Map<Skill, SkillEntry> ss = new HashMap<>();
			Skill s;
			for (int i : skills[job.type]) {
				s = SkillFactory.getSkill(i);
				int maxLevel = s.getMaxLevel();
				if (maxLevel < 1) {
					maxLevel = s.getMasterLevel();
				}
				ss.put(s, new SkillEntry((byte) 1, (byte) maxLevel, -1));
			}
			if (job == JobType.Zero) {
				ss.put(SkillFactory.getSkill(101000103), new SkillEntry((byte) 8, (byte) 10, -1));
				ss.put(SkillFactory.getSkill(101000203), new SkillEntry((byte) 8, (byte) 10, -1));
			}
			newchar.changeSkillLevel_Skip(ss, false);
		}*/
		
		final Map<Skill, SkillEntry> ss = new HashMap<>();
		ss.put(SkillFactory.getSkill(80001770), new SkillEntry((byte) 1, (byte) 1, -1));
		newchar.changeSkillLevel_Skip(ss, false);
		
		int[][] guidebooks = new int[][] { { 4161001, 0 }, { 4161047, 1 }, { 4161048, 2000 }, { 4161052, 2001 },
				{ 4161054, 3 }, { 4161079, 2002 } };
		int guidebook = 0;
		for (int[] i : guidebooks) {
			if (newchar.getJob() == i[1]) {
				guidebook = i[0];
			} else if (newchar.getJob() / 1000 == i[1]) {
				guidebook = i[0];
			}
		}

		if (guidebook > 0) {
			newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(guidebook, (byte) 0, (short) 1, (byte) 0));
		}

		if (job == JobType.Zero) {
			newchar.setLevel((short) 100);
			newchar.getStat().str = 518;
			newchar.getStat().maxhp = 6910;
			newchar.getStat().hp = 6910;
			newchar.getStat().maxmp = 100;
			newchar.getStat().mp = 100;
			newchar.setRemainingSp(3, 0); // alpha
			newchar.setRemainingSp(3, 1); // beta
		}
		
		if (job == JobType.Luminous) {
			newchar.setJob((short) 2700);
			newchar.setLevel((short) 10);
			newchar.getStat().str = 4;
			newchar.getStat().int_ = 57;
			newchar.getStat().maxhp = 500;
			newchar.getStat().hp = 500;
			newchar.getStat().maxmp = 1000;
			newchar.getStat().mp = 1000;
			newchar.setRemainingSp(3);
		}

		if(job == JobType.Resistance) {
			newchar.setMap(310000000);
			newchar.setLevel((short) 10);
			newchar.setRemainingAp(50);
		}

		if (MapleCharacterUtil.canCreateChar(name, c.isGm())
				&& (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm())
				&& (c.isGm() || c.canMakeCharacter(c.getWorld()))) {
			MapleCharacter.saveNewCharToDB(newchar, job, subcategory);
			c.getSession().write(LoginPacket.addNewCharEntry(newchar, true));
			c.createdChar(newchar.getId());
			// newchar.newCharRewards();
		} else {
			c.getSession().write(LoginPacket.addNewCharEntry(newchar, false));
		}
		
	}
}
