/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package constants;

/**
 *
 * @author Itzik
 */
public class EventConstants {

    public static boolean DoubleMiracleTime = true;
    public static boolean DoubleTime = false;
    
	private static final String [] EVENTS = {
			"PinkZakumEntrance",
			"PVP",
			"CygnusBattle",
			"ScarTarBattle",
			"BossBalrog_EASY",
			"BossBalrog_NORMAL",
			"HorntailBattle",
			"Nibergen",
			"PinkBeanBattle",
			"ZakumBattle",
			"NamelessMagicMonster",
			"Dunas",
			"Dunas2",
			"2095_tokyo",
			"ZakumPQ",
			"LudiPQ",
			"KerningPQ",
			"ProtectTylus",
			"WitchTower_EASY",
			"WitchTower_Med",
			"WitchTower_Hard",
			"Vergamot",
			"ChaosHorntail",
			"ChaosZakum",
			"CoreBlaze",
			"BossQuestEASY",
			"BossQuestMed",
			"BossQuestHARD",
			"BossQuestHELL",
			"Ravana_EASY",
			"Ravana_HARD",
			"Ravana_MED",
			"GuildQuest",
			"Aufhaven",
			"Dragonica",
			"Rex",
			"MonsterPark",
			"KentaPQ",
			"ArkariumBattle",
			"AswanOffSeason",
			"HillaBattle",
			"The Dragon Shout",
			"VonLeonBattle",
			"Ghost",
			"OrbisPQ",
			"Romeo",
			"JobAdvance2",
			"JobAdvance3",
			"JobAdvance4",
			"JobAdvance5",
			"Juliet",
			"Pirate",
			"Amoria",
			"Ellin",
			"CWKPQ",
			"DollHouse",
			"Kenta",
			"Prison",
			"Azwan",
			"Dragonica",
			"MagnusBattle",
			"MagnusMed",
			"DimensionInvasion",
			"MiniDungeon",
			"RanmaruBattle",
			"RanmaruNorm",
			"DarkHillaBattle",
			"ChaosQueen",
			"ChaosVellum",
			"ChaosRootPierre",
			"ChaosVonBon",
			"RootQueen",
			"RootVellum",
			"RootVonBon",
			"RootPierre",
			"ChaosMagnus",
			"HeliMagnus",
			//"AutoSave"
			//"lolcastle"
		};

		public static String getEvents() {
			String events = "";
			for (String event : EVENTS){
				events += event + ",";
			}
			return events.substring(0, events.length() - 1);
		}
}
