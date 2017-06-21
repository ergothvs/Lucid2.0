/* 
Script by Alcandon
*/	

function start() {


	cm.sendSimple ("Hello there and welcome to MapleBlade! Please select your starter pack: (I will also max your skills :D) \r\n#L0##bWarrior Set#k #l\r\n#L1##bThief Set#k #l\r\n#L2##bMagician Set#k #l\r\n#L3##bArcher Set#k #l\r\n#L4##bPirate Set#k #l\r\n#L5##bAran Set#k\r\n#L6##bHaven't decided yet..#k");
}

function action(mode, type, selection) {
cm.dispose();
    if (selection == 0) { //Warrior
	    cm.gainItem(1302073, 1); //Beginner Flag
		cm.gainItem(1142107, 1); //Beginner Medal
		cm.gainItem(1002002, 1); //Warrior Head
		cm.gainItem(1040038, 1); //Warrior Armor
		cm.gainItem(1060028, 1); //Warrior Pants
		cm.gainItem(1082002, 1); //Gloves
		cm.gainItem(2040804, 7); //7 Gloves for Att Scrolls 60%
		cm.gainItem(1432012, 1); //Maple Impaler
		cm.gainItem(1302030, 1); //Maple Soul Singer
		cm.gainItem(2022179, 2); //2 Onyx Apples
		        //Start of Noblesse Skills
cm.teachSkill(10001001,3,3); //Recovery
cm.teachSkill(10001002,3,3); //Nimble Feet
cm.teachSkill(10001000,3,3); //Three Snails
cm.teachSkill(10001004,1,1); //Monster Rider
cm.teachSkill(10001005,1,1); //Echo of hero
//End of Noblesse Skills
//Start of Dawn Warrior Skills
//Job1
cm.teachSkill(11000000,10,10); // Improving MaxHP Increase
cm.teachSkill(11001001,10,10); // Iron Body
cm.teachSkill(11001002,20,20); // Power Strike
cm.teachSkill(11001003,20,20); // Slash Blast
cm.teachSkill(11001004,20,20); // Soul
//Job2
cm.teachSkill(11100000,20,20); // Sword Mastery
cm.teachSkill(11101001,20,20); // Sword Booster
cm.teachSkill(11101002,30,30); // Final Attack
cm.teachSkill(11101003,20,20); // Rage
cm.teachSkill(11101004,30,30); // Soul Blade
cm.teachSkill(11101005,10,10); // Soul Rush
//Job3
cm.teachSkill(11110000,20,20); // Improving MP Recovery
cm.teachSkill(11111001,20,20); // Combo Attack
cm.teachSkill(11111002,20,20); // Sword- Panic
cm.teachSkill(11111003,20,20); // Sword- Coma
cm.teachSkill(11111004,30,30); // Brandish
cm.teachSkill(11110005,20,20); // Advance Combo
cm.teachSkill(11111006,30,30); // Soul Blow
cm.teachSkill(11111007,20,20); // Soul Charge
//End of Dawn Warrior Skills
                //Start of Beginner Skills
cm.teachSkill(1000,3,3); //Three Snails
cm.teachSkill(1001,3,3); //Recovery
cm.teachSkill(1002,3,3); //Nimble Feet
cm.teachSkill(1004,1,1); //Monster Rider
cm.teachSkill(1005,1,1); //Echo of Hero
//End of Beginner Skills
//Start of Swordsman Skills
cm.teachSkill(1000000,16,16); //Improving HP Recovery
cm.teachSkill(1000001,10,10); //Improving MaxHP Increase
cm.teachSkill(1000002,8,8); // Endure
cm.teachSkill(1001003,20,20); //Iron Body
cm.teachSkill(1001004,20,20); //Power Strike
cm.teachSkill(1001005,20,20); //Slash Blast
//Start of Fighter Skills
cm.teachSkill(1100000,20,20); //Sword Mastery
cm.teachSkill(1100001,20,20); //Axe Mastery
cm.teachSkill(1100002,30,30); //Final Attack - Sword
cm.teachSkill(1100003,30,30); //Final Attack - Axe
cm.teachSkill(1101004,20,20); //Sword Booster
cm.teachSkill(1101005,20,20); //Axe Booster
cm.teachSkill(1101006,20,20); //Rage
cm.teachSkill(1101007,30,30); //Power Guard
//End of Fighter Skills
//Start of Page Skills
cm.teachSkill(1200000,20,20); //Sword Mastery
cm.teachSkill(1200001,20,20); //BW Mastery
cm.teachSkill(1200002,30,30); //Final Attack - Sword
cm.teachSkill(1200003,30,30); //Final Attack - BW
cm.teachSkill(1201004,20,20); //Sword Booster
cm.teachSkill(1201005,20,20); //BW Booster
cm.teachSkill(1201006,20,20); //Threaten
cm.teachSkill(1201007,30,30); //Power Guard
//End of Page Skills
//Start of Spearman Skills
cm.teachSkill(1300000,20,20); //Spear Mastery
cm.teachSkill(1300001,20,20); //Pole Arm Mastery
cm.teachSkill(1300002,30,30); //Final Attack - Spear
cm.teachSkill(1300003,30,30); //Final Attack - Pole Arm
cm.teachSkill(1301004,20,20); //Spear Booster
cm.teachSkill(1301005,20,20); //Pole Arm Booster
cm.teachSkill(1301006,20,20); //Iron Will
cm.teachSkill(1301007,30,30); //Hyper Body
//End of Spearman Skills
//Start of Crusader Skills
cm.teachSkill(1110000,20,20); //Improving MP recovery
cm.teachSkill(1110001,20,20); //Shield Mastery
cm.teachSkill(1111002,30,30); //Combo Attack
cm.teachSkill(1111003,30,30); //Panic- Sword
cm.teachSkill(1111004,30,30); //Panic- Axe
cm.teachSkill(1111005,30,30); //Coma- Sword
cm.teachSkill(1111006,30,30); //Coma- Axe
cm.teachSkill(1111007,20,20); //Armor Crash
cm.teachSkill(1111008,30,30); //Shout
//End of Crusader Skills
//Start of White Knight Skills
cm.teachSkill(1210000,20,20); //Improving MP recovery
cm.teachSkill(1210001,20,20); //Shield Mastery
cm.teachSkill(1211002,30,30); //Charged Blow
cm.teachSkill(1211003,30,30); //Fire Charge- Sword
cm.teachSkill(1211004,30,30); //Fire Charge- BW
cm.teachSkill(1211005,30,30); //Ice Charge- Sword
cm.teachSkill(1211006,30,30); //Blizzard Charge- BW
cm.teachSkill(1211007,30,30); //Thunder Charge- Sword
cm.teachSkill(1211008,30,30); //Lightning Charge- BW
cm.teachSkill(1211009,20,20); //Magic Crash
//End of White Knight Skills
//Start of Dragon Knight Skills
cm.teachSkill(1310000,20,20); //Elemental Resistance
cm.teachSkill(1311001,30,30); //Spear Crusher
cm.teachSkill(1311002,30,30); //Pole Arm Crusher
cm.teachSkill(1311003,30,30); //Dragon Fury- Spear
cm.teachSkill(1311004,30,30); //Dragon Fury- Pole Arm
cm.teachSkill(1311005,30,30); //Sacrifice
cm.teachSkill(1311006,30,30); //Dragon Roar
cm.teachSkill(1311007,20,20); //Power Crash
cm.teachSkill(1311008,20,20); //Dragon Blood
//End of Dragon Knight Skills
//Start Hero Skills
cm.teachSkill(1121000,30,30); //Maple Warrior
cm.teachSkill(1121002,30,30); //Power Stance
cm.teachSkill(1120003,30,30); //Advanced Combo
cm.teachSkill(1120004,30,30); //Achilles
cm.teachSkill(1120005,30,30); //Guardian
cm.teachSkill(1121006,30,30); //Rush
cm.teachSkill(1121008,30,30); //Brandish
cm.teachSkill(1121010,30,30); //Enrage
cm.teachSkill(1121011,5,5); //Hero's Will
//End Hero Skills
//Start Paladin Skills
cm.teachSkill(1221000,30,30); //Maple Warrior
cm.teachSkill(1221002,30,30); // Power Stance
cm.teachSkill(1221003,20,20); //Holy Charge
cm.teachSkill(1221004,20,20); //Divine Charge
cm.teachSkill(1220005,30,30); //Achilles
cm.teachSkill(1220006,30,30); //Guardian
cm.teachSkill(1221007,30,30); //Rush
cm.teachSkill(1221009,30,30); //Blast
cm.teachSkill(1220010,10,10); //Advanced Charge
cm.teachSkill(1221011,30,30); //Sanctuary
cm.teachSkill(1221012,5,5); //Hero's Will
//End of Paladin Skills
//Start of Dark Knight Skills
cm.teachSkill(1321000,30,30); //Maple Warrior
cm.teachSkill(1321002,30,30); //Power Stance
cm.teachSkill(1321003,30,30); //Rush
cm.teachSkill(1320005,30,30); //Achilles
cm.teachSkill(1320006,30,30); //Berserk
cm.teachSkill(1321007,10,10); //Beholder
cm.teachSkill(1320008,25,25); //Beholder's Healing
cm.teachSkill(1320009,25,25); //Beholder's Buff
cm.teachSkill(1321010,5,5); //Hero's Will
//End of Dark Knight Skills
//End of Swordsman Skills
		cm.warp(100000000);
	cm.dispose();
     }


	if (selection == 1) { //Thief
	cm.gainItem(1332063, 1); //Beginner Thief's Sword
		cm.gainItem(1472061, 1); //Beginner Thief's Wrist Guard
		cm.gainItem(1142107, 1); //Beginner Medal
		cm.gainItem(2070001, 700);
		cm.gainItem(2070001, 700);
		cm.gainItem(2070001, 700);
		cm.gainItem(1002125, 1);
		cm.gainItem(1040033, 1);
		cm.gainItem(1060023, 1);
		cm.gainItem(1082002, 1); //Gloves
		cm.gainItem(2040804, 7); //7 Gloves for Att Scrolls 60%
		cm.gainItem(1472032, 1);
		cm.gainItem(1332025, 1);
		cm.gainItem(2022179, 2); //2 Onyx Apples
		        //Start of Noblesse Skills
cm.teachSkill(10001001,3,3); //Recovery
cm.teachSkill(10001002,3,3); //Nimble Feet
cm.teachSkill(10001000,3,3); //Three Snails
cm.teachSkill(10001004,1,1); //Monster Rider
cm.teachSkill(10001005,1,1); //Echo of hero
//End of Noblesse Skills
//Start of Night Walker Skills
//Job1
cm.teachSkill(14000000,10,10); // Nimble Body
cm.teachSkill(14000001,8,8); // Keen Eyes
cm.teachSkill(14001002,10,10); // Disorder
cm.teachSkill(14001003,10,10); // Dark Sight
cm.teachSkill(14001004,20,20); // Lucky Seven
cm.teachSkill(14001005,20,20); // Darkness
//Job2
cm.teachSkill(14100000,20,20); // Claw Mastery
cm.teachSkill(14100001,30,30); // Critical Throw
cm.teachSkill(14101002,20,20); // Claw Booster
cm.teachSkill(14101003,20,20); // Haste
cm.teachSkill(14101004,20,20); // Flash Jump
cm.teachSkill(14100005,10,10); // Vanish
cm.teachSkill(14101006,20,20); // Vampire
//Job3
cm.teachSkill(14111000,30,30); // Shadow Partner
cm.teachSkill(14111001,20,20); // Shadow Web
cm.teachSkill(14111002,30,30); // Avenger
cm.teachSkill(14110003,20,20); // Alchemist
cm.teachSkill(14110004,20,20); // Venom
cm.teachSkill(14111005,20,20); // Triple Throw
cm.teachSkill(14111006,30,30); // Poison Bomb
//End of Night Walker Skills
		                //Start of Beginner Skills
cm.teachSkill(1000,3,3); //Three Snails
cm.teachSkill(1001,3,3); //Recovery
cm.teachSkill(1002,3,3); //Nimble Feet
cm.teachSkill(1004,1,1); //Monster Rider
cm.teachSkill(1005,1,1); //Echo of Hero
//End of Beginner Skills
//Start of Rouge Skills
cm.teachSkill(4000000,20,20); //Nimble Body
cm.teachSkill(4000001,8,8); //Keen Eyes
cm.teachSkill(4001002,20,20); //Disorder
cm.teachSkill(4001003,20,20); //Dark Sight
cm.teachSkill(4001334,20,20); //Double Stab
cm.teachSkill(4001344,20,20); //Lucky Seven
//Start of Assassin Skills
cm.teachSkill(4100000,20,20); //Claw Mastery
cm.teachSkill(4100001,30,30); //Critical Throw
cm.teachSkill(4100002,20,20); //Endure
cm.teachSkill(4101003,20,20); //Claw Booster
cm.teachSkill(4101004,20,20); //Haste
cm.teachSkill(4101005,30,30); //Drain
//End of Assassin Skills
//Start of Bandit Skills
cm.teachSkill(4200000,20,20); //Dagger Mastery
cm.teachSkill(4200001,20,20); //Endure
cm.teachSkill(4201002,20,20); //Dagger Booster
cm.teachSkill(4201003,20,20); //Haste
cm.teachSkill(4201004,30,30); //Steal
cm.teachSkill(4201005,30,30); //Savage Blow
//End of Bandit Skills
//Start of Hermit Skills
cm.teachSkill(4110000,20,20); //Alchemist
cm.teachSkill(4111001,20,20); //Meso Up
cm.teachSkill(4111002,30,30); //Shadow Partner
cm.teachSkill(4111003,20,20); //Shadow Web
cm.teachSkill(4111004,30,30); //Shadow Meso
cm.teachSkill(4111005,30,30); //Avenger
cm.teachSkill(4111006,20,20); //Flash Jump
//End of Hermit Skills
//Start of Cheif Bandit Skills
cm.teachSkill(4210000,20,20); //Shield Mastery
cm.teachSkill(4211001,30,30); //Chakra
cm.teachSkill(4211002,30,30); //Assaulter
cm.teachSkill(4211003,20,20); //Pickpocket
cm.teachSkill(4211004,30,30); //Band of Thieves
cm.teachSkill(4211005,20,20); //Meso Guard
cm.teachSkill(4211006,30,30); //Meso Explosion
//End of Chief Bandit Skills
//Start of Night Lord Skills
cm.teachSkill(4121000,30,30); //Maple Warrior
cm.teachSkill(4120002,30,30); //Shadow Shifter
cm.teachSkill(4121003,30,30); //Taunt
cm.teachSkill(4121004,30,30); //Ninja Ambush
cm.teachSkill(4120005,30,30); //Venomous Star
cm.teachSkill(4121006,30,30); //Spirit Claw
cm.teachSkill(4121007,30,30); //Triple Throw
cm.teachSkill(4121008,30,30); //Ninja Storm
cm.teachSkill(4121009,5,5); //Hero's Will
//End of Night Lord Skills
//Start of Shadower Skills
cm.teachSkill(4221000,30,30); //Maple Warrior
cm.teachSkill(4220002,30,30); //Shadow Shifter
cm.teachSkill(4221003,30,30); //Taunt
cm.teachSkill(4221004,30,30); //Ninja Ambush
cm.teachSkill(4220005,30,30); //Venomous Stab
cm.teachSkill(4221006,30,30); //Smokescreen
cm.teachSkill(4221007,30,30); //Boomerang Step
cm.teachSkill(4221001,30,30); //Assassinate
cm.teachSkill(4221008,5,5); //Hero's Will
		cm.warp(100000000);
	    cm.dispose();
      }

	if (selection == 2) { //Magician
	    cm.gainItem(1372043, 1); //Beginner Wand
		cm.gainItem(1142107, 1); //Beginner Medal
		cm.gainItem(1092021, 1); 
		cm.gainItem(1050008, 1); 
		cm.gainItem(1372018, 1);
		cm.gainItem(1082145, 1); //Gloves
		cm.gainItem(2040817, 7); //7 Gloves for M.Att Scrolls 60%
		cm.gainItem(1382012, 1); //Maple Lama Staff
		cm.gainItem(2022179, 2); //2 Onyx Apples
		//Start of Noblesse Skills
cm.teachSkill(10001001,3,3); //Recovery
cm.teachSkill(10001002,3,3); //Nimble Feet
cm.teachSkill(10001000,3,3); //Three Snails
cm.teachSkill(10001004,1,1); //Monster Rider
cm.teachSkill(10001005,1,1); //Echo of hero
//End of Noblesse Skills
//Start of Blaze Wizard Skills
//Job1
cm.teachSkill(12000000,10,10); // Improving Max MP Increase
cm.teachSkill(12001001,10,10); // Magic Guard
cm.teachSkill(12001002,10,10); // Magic Armor
cm.teachSkill(12001003,20,20); // Magic Claw
cm.teachSkill(12001004,20,20); // Sprite Summon
//Job2
cm.teachSkill(12101000,20,20); // Meditation
cm.teachSkill(12101001,20,20); // Slow
cm.teachSkill(12101002,20,20); // Fire Arrow
cm.teachSkill(12101003,20,20); // Teleport
cm.teachSkill(12101004,20,20); // Magic Booster
cm.teachSkill(12101005,20,20); // Elemental Reset
cm.teachSkill(12101006,20,20); // Fire Pillar
//Job3
cm.teachSkill(12110000,20,20); // Element Amplification
cm.teachSkill(12110001,20,20); // Elemental Resistance
cm.teachSkill(12111002,20,20); // Seal
cm.teachSkill(12111003,20,20); // Meteor Shower
cm.teachSkill(12111004,20,20); // Ifrit
cm.teachSkill(12111005,30,30); // Flame Gear
cm.teachSkill(12111006,30,30); // Fire Strike
//End of Blaze Wizard Skills
		//Start of Beginner Skills
cm.teachSkill(1000,3,3); //Three Snails
cm.teachSkill(1001,3,3); //Recovery
cm.teachSkill(1002,3,3); //Nimble Feet
cm.teachSkill(1004,1,1); //Monster Rider
cm.teachSkill(1005,1,1); //Echo of Hero
//End of Beginner Skills
//Start of Magician Skills
cm.teachSkill(2000000,16,16); //Improving MP recovery
cm.teachSkill(2000001,10,10); //Improving Max MP Increase
cm.teachSkill(2001002,20,20); //Magic Guard
cm.teachSkill(2001003,20,20); //Magic Armor
cm.teachSkill(2001004,20,20); //Energy Bolt
cm.teachSkill(2001005,20,20); //Magic Claw
//Start of FP Wizard Skills
cm.teachSkill(2100000,20,20); //MP Eater
cm.teachSkill(2101001,20,20); //Meditation
cm.teachSkill(2101002,20,20); //Teleport
cm.teachSkill(2101003,20,20); //Slow
cm.teachSkill(2101004,30,30); //Fire Arrow
cm.teachSkill(2101005,30,30); //Poison Breath
//End of FP Wizard Skills
//Start of IL Wizard Skills
cm.teachSkill(2200000,20,20); //MP Eater
cm.teachSkill(2201001,20,20); //Meditation
cm.teachSkill(2201002,20,20); //Teleport
cm.teachSkill(2201003,20,20); //Slow
cm.teachSkill(2201004,30,30); //Cold Beam
cm.teachSkill(2201005,30,30); //Thunderbolt
//End of IL Wizard Skills
//Start of Cleric Skills
cm.teachSkill(2300000,20,20); //MP Eater
cm.teachSkill(2301001,20,20); //Teleport
cm.teachSkill(2301002,30,30); //Heal
cm.teachSkill(2301003,20,20); //Invincible
cm.teachSkill(2301004,20,20); //Bless
cm.teachSkill(2301005,30,30); //Holy Arrow
//End of Cleric Skills
//Start of FP Mage Skills
cm.teachSkill(2110000,20,20); //Partial Resistance
cm.teachSkill(2110001,30,30); //Element Amplification
cm.teachSkill(2111002,30,30); //Explosion
cm.teachSkill(2111003,30,30); //Poison Mist
cm.teachSkill(2111004,20,20); //Seal
cm.teachSkill(2111005,20,20); //Spell Booster
cm.teachSkill(2111006,30,30); //Element Composition
//End of FP Mage Skills
//Start of IL Mage Skills
cm.teachSkill(2210000,20,20); //Partial Resistance
cm.teachSkill(2210001,30,30); //Element Amplification
cm.teachSkill(2211002,30,30); //Ice Strike
cm.teachSkill(2211003,30,30); //Thunder Spear
cm.teachSkill(2211004,20,20); //Seal
cm.teachSkill(2211005,20,20); //Spell Booster
cm.teachSkill(2211006,30,30); //Element Composition
//End of IL Mage Skills
//Start of Priest Skills
cm.teachSkill(2310000,20,20); //Elemental Resistance
cm.teachSkill(2311001,20,20); //Dispel
cm.teachSkill(2311002,20,20); //Mystic Door
cm.teachSkill(2311003,30,30); //Holy Symbol
cm.teachSkill(2311004,30,30); //Shining Ray
cm.teachSkill(2311005,30,30); //Doom
cm.teachSkill(2311006,30,30); //Summon Dragon
//End of Priest Skills
//Start of FP Arch Mage Skills
cm.teachSkill(2121000,30,30); //Maple Warrior
cm.teachSkill(2121001,30,30); //Big Bang
cm.teachSkill(2121002,30,30); //Mana Reflection
cm.teachSkill(2121003,30,30); //Fire Demon
cm.teachSkill(2121004,30,30); //Infinity
cm.teachSkill(2121005,30,30); //Ifrit
cm.teachSkill(2121006,30,30); //Paralyze
cm.teachSkill(2121007,30,30); //Meteor Shower
cm.teachSkill(2121008,5,5); //Hero's Will
//End of FP Arch Mage Skills
//Start of IL Arch Mage Skills
cm.teachSkill(2221000,30,30); //Maple Warrior
cm.teachSkill(2221001,30,30); //Big Bang
cm.teachSkill(2221002,30,30); //Mana Reflection
cm.teachSkill(2221003,30,30); //Ice Demon
cm.teachSkill(2221004,30,30); //Infinity
cm.teachSkill(2221005,30,30); //Elquines
cm.teachSkill(2221006,30,30); //Chain Lightning
cm.teachSkill(2221007,30,30); //Blizzard
cm.teachSkill(2221008,5,5); //Hero's Will
//End of IL Arch Mage Skills
//Start of Bishop Skills
cm.teachSkill(2321000,30,30); //Maple Warrior
cm.teachSkill(2321001,30,30); //Big Bang
cm.teachSkill(2321002,30,30); //Mana Reflection
cm.teachSkill(2321003,30,30); //Bahamut
cm.teachSkill(2321004,30,30); //Infinity
cm.teachSkill(2321005,30,30); //Holy Shield
cm.teachSkill(2321006,10,10); //Resurrection
cm.teachSkill(2321007,30,30); //Angel's Ray
cm.teachSkill(2321008,30,30); //Genesis
cm.teachSkill(2321009,5,5); //Hero's Will
//End of Bishop Skills
//End of Magician Skills
		cm.warp(100000000);
	cm.dispose();
      }

	if (selection == 3) { //Archer
	    cm.gainItem(1452051, 1); //Beginner Bow
		cm.gainItem(1142107, 1); //Beginner Medal
		cm.gainItem(1002010, 1); 
		cm.gainItem(1040008, 1); 
		cm.gainItem(1062004, 1); 
		cm.gainItem(1462001, 1);
		cm.gainItem(1082002, 1); //Gloves
		cm.gainItem(2040804, 7); //7 Gloves for Att Scrolls 60%
		cm.gainItem(1452022, 1);
		cm.gainItem(2060003, 1200);
		cm.gainItem(2060003, 1200);
		cm.gainItem(2060003, 1200);
		cm.gainItem(2061003, 1200);
		cm.gainItem(2061003, 1200);
		cm.gainItem(2061003, 1200);
		cm.gainItem(1462019, 1);
		cm.gainItem(2022179, 2); //2 Onyx Apples
		        //Start of Noblesse Skills
cm.teachSkill(10001001,3,3); //Recovery
cm.teachSkill(10001002,3,3); //Nimble Feet
cm.teachSkill(10001000,3,3); //Three Snails
cm.teachSkill(10001004,1,1); //Monster Rider
cm.teachSkill(10001005,1,1); //Echo of hero
//End of Noblesse Skills
//Start of Wind Breaker Skills
cm.teachSkill(13000001,8,8); // The Eye of Amazon 
cm.teachSkill(13001002,10,10); // Focus 
cm.teachSkill(13001003,20,20); // Double Shot 
cm.teachSkill(13000000,20,20); // Critical Shot 
cm.teachSkill(13001004,20,20); // Sprite Summon 
//job2
cm.teachSkill(13100000,20,20); // Bow Mastery 
cm.teachSkill(13101001,20,20); // Bow Booster 
cm.teachSkill(13101002,30,30); // Final Attack 
cm.teachSkill(13101003,20,20); // Soul Arrow 
cm.teachSkill(13101005,20,20); // Storm Breath 
cm.teachSkill(13101006,10,10); // Wind Walk 
//Job3
cm.teachSkill(13111000,20,20); // Arrow Rain 
cm.teachSkill(13111001,30,30); // Strafe 
cm.teachSkill(13110003,20,20); // Bow Expert 
cm.teachSkill(13111002,20,20); // Hurricane 
cm.teachSkill(13111004,20,20); // Puppet 
cm.teachSkill(13111005,10,10); // Eagle Eye 
cm.teachSkill(13111006,20,20); // Wind Piercing 
cm.teachSkill(13111007,20,20); // Wind Shot 
//End of Wind Breaker Skills
		                //Start of Beginner Skills
cm.teachSkill(1000,3,3); //Three Snails
cm.teachSkill(1001,3,3); //Recovery
cm.teachSkill(1002,3,3); //Nimble Feet
cm.teachSkill(1004,1,1); //Monster Rider
cm.teachSkill(1005,1,1); //Echo of Hero
//End of Beginner Skills
//Start of Archer Skills
cm.teachSkill(3000000,16,16); //The Blessing of Amazon
cm.teachSkill(3000001,20,20); //Critical Shot
cm.teachSkill(3000002,8,8); //The Eye of Amazon
cm.teachSkill(3001003,20,20); //Focus
cm.teachSkill(3001004,20,20); //Arrow Blow
cm.teachSkill(3001005,20,20); //Double Shot
//Start of Hunter Skills
cm.teachSkill(3100000,20,20); //Bow Mastery - Bow
cm.teachSkill(3100001,30,30); //Final Attack - Bow
cm.teachSkill(3101002,20,20); //Bow Booster - Bow
cm.teachSkill(3101003,20,20); //Power Knock-Back - Crossbow
cm.teachSkill(3101004,20,20); //Soul Arrow - Bow
cm.teachSkill(3101005,30,30); //Arrow Bomb - Bow
//End of Hunter Skills
//Start of Ranger Skills
cm.teachSkill(3110000,20,20); //Thrust - Bow
cm.teachSkill(3110001,20,20); //Mortal Blow - Bow
cm.teachSkill(3111002,20,20); //Puppet - Bow
cm.teachSkill(3111003,30,30); //Inferno - Bow
cm.teachSkill(3111004,30,30); //Arrow Rain - Bow
cm.teachSkill(3111005,30,30); //Silver Hawk - Bow
cm.teachSkill(3111006,30,30); //Strafe - Bow
//End of Ranger Skills
//Start of Bowmaster Skills
cm.teachSkill(3121000,30,30); //Maple Warrior - Bow
cm.teachSkill(3121002,30,30); //Sharp Eyes - Bow
cm.teachSkill(3121003,30,30); //Dragon Breath - Bow
cm.teachSkill(3121004,30,30); //Hurricane - Bow
cm.teachSkill(3120005,30,30); //Bow Expert - Bow
cm.teachSkill(3121006,30,30); //Phoenix - Bow
cm.teachSkill(3121007,30,30); //Hamstring - Bow
cm.teachSkill(3121008,30,30); //Concentrate - Bow
cm.teachSkill(3121009,5,5); //Hero's Will - Bow
//End of Bowmaster Skills
//Start of Crossbow Man Skills
cm.teachSkill(3200000,20,20); //Crossbow Mastery - Crossbow
cm.teachSkill(3200001,30,30); //Final Attack - Crossbow
cm.teachSkill(3201002,20,20); //Crossbow Booster - Crossbow
cm.teachSkill(3201003,20,20); //Power Knock-Back - Crossbow
cm.teachSkill(3201004,20,20); //Soul Arrow - Crossbow
cm.teachSkill(3201005,30,30); //Iron Arrow - Crossbow
//End of Crossbow Man Skills
//Start of Sniper Skills
cm.teachSkill(3210000,20,20); //Thrust - Crossbow
cm.teachSkill(3210001,20,20); //Mortal Blow - Crossbow
cm.teachSkill(3211002,20,20); //Puppet - Crossbow
cm.teachSkill(3211003,30,30); //Blizzard - Crossbow
cm.teachSkill(3211004,30,30); //Arrow Eruption - Crossbow
cm.teachSkill(3211005,30,30); //Golden Eagle - Crossbow
cm.teachSkill(3211006,30,30); //Strafe - Crossbow
//End of Sniper Skills
//Start of Marksman Skills
cm.teachSkill(3221000,30,30); //Maple Warrior - Crossbow
cm.teachSkill(3221001,30,30); //Piercing - Crossbow
cm.teachSkill(3221002,30,30); //Sharp Eyes - Crossbow
cm.teachSkill(3221003,30,30); //Dragon Breath - Crossbow
cm.teachSkill(3220004,30,30); //Crossbow Expertness  - Crossbow
cm.teachSkill(3221005,30,30); //Freezer - Crossbow
cm.teachSkill(3221006,30,30); //Blind - Crossbow
cm.teachSkill(3221007,30,30); //Sniping - Crossbow
cm.teachSkill(3221008,5,5); //Hero's Will - Crossbow
//End of Marksman Skills
//End of Archer Skills
		cm.warp(100000000);
	cm.dispose();
      }

	if (selection == 4) { //Pirate
cm.gainItem(1492000, 1); //Beginner Pistol
		cm.gainItem(1142107, 1); //Beginner Medal
		cm.gainItem(1482000, 1); //Knuckler
		cm.gainItem(1002610, 1); //Warrior Armor
		cm.gainItem(1052095, 1); //Warrior Pants
		cm.gainItem(1082002, 1); //Gloves
		cm.gainItem(2040804, 7); //7 Gloves for Att Scrolls 60%
		cm.gainItem(1492022, 1); //Maple Impaler
		cm.gainItem(1482021, 1); //Maple Soul Singer
		cm.gainItem(2022179, 2); //2 Onyx Apples
		cm.gainItem(2330000, 1000);
		cm.gainItem(2330000, 1000);
		cm.gainItem(2330000, 1000);
		cm.gainItem(2330000, 1000);
		        //Start of Noblesse Skills
cm.teachSkill(10001001,3,3); //Recovery
cm.teachSkill(10001002,3,3); //Nimble Feet
cm.teachSkill(10001000,3,3); //Three Snails
cm.teachSkill(10001004,1,1); //Monster Rider
cm.teachSkill(10001005,1,1); //Echo of hero
//End of Noblesse Skills
//Start of Thunder Breaker Skills
//Job1
cm.teachSkill(15000000,10,10); // Bullet Time
cm.teachSkill(15001001,20,20); // Flash Fist
cm.teachSkill(15001002,20,20); // Sommersault Kick
cm.teachSkill(15001003,10,10); // Dash
cm.teachSkill(15001004,20,20); // Lightning
//Job2
cm.teachSkill(15100000,10,10); // Improve MaxHP
cm.teachSkill(15100001,20,20); // Knuckle Mastery
cm.teachSkill(15100004,20,20); // Energy Charge
cm.teachSkill(15101002,20,20); // Knuckle Booster
cm.teachSkill(15101003,20,20); // Corkscrew Blow
cm.teachSkill(15101005,20,20); // Energy Blast
cm.teachSkill(15101006,20,20); // Lightning Charge
//Job3
cm.teachSkill(15110000,20,20); // Critical Punch
cm.teachSkill(15111001,20,20); // Energy Drain
cm.teachSkill(15111002,10,10); // Transformation
cm.teachSkill(15111003,20,20); // Shockwave
cm.teachSkill(15111004,20,20); // Barrage
cm.teachSkill(15111005,20,20); // Speed Infusion
cm.teachSkill(15111006,20,20); // Spark
cm.teachSkill(15111007,30,30); // Shark Wave
//End of Thunder Breaker Skills
		                //Start of Beginner Skills
cm.teachSkill(1000,3,3); //Three Snails
cm.teachSkill(1001,3,3); //Recovery
cm.teachSkill(1002,3,3); //Nimble Feet
cm.teachSkill(1004,1,1); //Monster Rider
cm.teachSkill(1005,1,1); //Echo of Hero
//End of Beginner Skills
//Start of Pirate Skills
cm.teachSkill(5000000,20,20); //Bullet Time
cm.teachSkill(5001001,20,20); //Flash Fist
cm.teachSkill(5001002,20,20); //Sommersault Kick
cm.teachSkill(5001003,20,20); //Double Shot
cm.teachSkill(5001005,10,10); //Dash
//Start of Infighter Skills
cm.teachSkill(5100000,10,10); //Improve MaxHP
cm.teachSkill(5100001,20,20); //Knuckler Mastery
cm.teachSkill(5101002,20,20); //Backspin Blow
cm.teachSkill(5101003,20,20); //Double Uppercut
cm.teachSkill(5101004,20,20); //Corkscrew Blow
cm.teachSkill(5101005,10,10); //MP Recovery
cm.teachSkill(5101006,20,20); //Knuckler Booster
cm.teachSkill(5101007,10,10); //Oak Barrel
//End of Infighter Skills
//Start of Gunslinger Skills
cm.teachSkill(5200000,20,20); //Gun Mastery
cm.teachSkill(5201001,20,20); //Invisible Shot
cm.teachSkill(5201002,20,20); //Grenade
cm.teachSkill(5201003,20,20); //Gun Booster
cm.teachSkill(5201004,20,20); //Blank Shot
cm.teachSkill(5201005,10,10); //Wings
cm.teachSkill(5201006,20,20); //Recoil Shot
//End of Gunslinger Skills
//Start of Marauder Skills
cm.teachSkill(5110000,20,20); //Stun Mastery
cm.teachSkill(5110001,40,40); //Energy Charge
cm.teachSkill(5111002,30,30); //Energy Blast
cm.teachSkill(5111004,20,20); //Energy Drain
cm.teachSkill(5111005,20,20); //Transformation
cm.teachSkill(5111006,30,30); //Shockwave
//End of Marauder Skills
//Start of Outlaw Skills
cm.teachSkill(5210000,20,20); //Burst Fire
cm.teachSkill(5211001,30,30); //Octopus
cm.teachSkill(5211002,30,30); //Gaviota
cm.teachSkill(5211004,30,30); //Flamethrower
cm.teachSkill(5211005,30,30); //Ice Splitter
cm.teachSkill(5211006,30,30); //Homing Beacon
//End of Outlaw Skills
//Start of Buccaneer Skills
cm.teachSkill(5121000,30,30); //Maple Warrior
cm.teachSkill(5121001,30,30); //Dragon Strike
cm.teachSkill(5121002,30,30); //Energy Orb
cm.teachSkill(5121003,20,20); //Super Transformation
cm.teachSkill(5121004,30,30); //Demolition
cm.teachSkill(5121005,30,30); //Snatch
cm.teachSkill(5121007,30,30); //Barrage
cm.teachSkill(5121008,5,5); //Pirate's Rage
cm.teachSkill(5121009,20,20); //Speed Infusion
cm.teachSkill(5121010,30,30); //Time Leap
//End of Buccaneer Skills
//Start of Crosair Skills
cm.teachSkill(5220001,30,30); //Elemental Boost
cm.teachSkill(5220002,20,20); //Wrath of the Octopi
cm.teachSkill(5220011,20,20); //Bullseye
cm.teachSkill(5221000,30,30); //Maple Warrior
cm.teachSkill(5221003,30,30); //Aerial Strike
cm.teachSkill(5221004,30,30); //Rapid Fire
cm.teachSkill(5221006,10,10); //Battleship
cm.teachSkill(5221007,30,30); //Battleship Cannon
cm.teachSkill(5221008,30,30); //Battleship Torpedo
cm.teachSkill(5221009,20,20); //Hypnotize
cm.teachSkill(5221010,5,5); //Speed Infusion
//End of Crosair Skills
//End of Pirate Skills
		cm.warp(100000000);
		}
		if (selection == 5) { //Aran
		cm.gainItem(1082002, 1); //Gloves
		cm.gainItem(2040804, 7); //7 Gloves for Att Scrolls 60%
		cm.gainItem(2022179, 2); //2 Onyx Apples
		cm.gainItem(1142107, 1); //Beginner Medal
		cm.gainItem(1442024, 1); //Beginner Medal
		cm.gainItem(1442078, 1); //Beginner Medal
		cm.gainItem(1442071, 1); //Beginner Medal
cm.warp(100000000);
cm.maxAranskills();
cm.teachSkill(5001005,0,20); //Hypnotize
cm.teachSkill(15001003,0,20); //Hypnotize
		}
	if (selection == 6) {
	cm.sendOk ("Okay, tell me when you've decided what you want.");
	cm.dispose();
}
}
