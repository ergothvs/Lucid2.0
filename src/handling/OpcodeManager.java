package handling;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.MapleClient;
import com.sun.org.apache.regexp.internal.RE;
import handling.handlers.*;
import handling.handlers.cashshop.*;
import handling.handlers.login.*;
import handling.handlers.npc.*;
import tools.HexTool;
import tools.data.LittleEndianAccessor;


public class OpcodeManager {
	
	private static Map<Integer, Method> handlers = new HashMap<Integer, Method>();
	public static List<Integer> spamRecvHandlers = new ArrayList<>();
	public static List<String> spamSendHandlers = new ArrayList<>();
	
	private static Class<?>[] packethandlers = new Class<?>[] {
		
		PongHandler.class,
		
		// Login
		ServerStatusRequest.class,
		ServerListRequest.class,
		AuthServerHandler.class,
		PlayerLoggedInHandler.class,
		CheckCharacterName.class,
		DeleteCharHandler.class,
		HeartbeatRequest.class,
		ClientStartHandler.class,
		LoginPasswordHandler.class,
		CharacterListRequest.class,
		CreateNewCharacter.class,
		ClientErrorHandler.class,
		CharacterWithSecondPassword.class,
		CreateWithoutSecondPassword.class,

		CharacterWithoutSecondPassword.class,
		CharSelectHandler.class,
		
		// Channel
		ChangeMapHandler.class,
		EnterCashShopHandler.class,
		MovePlayerHandler.class,
		CancelChairHandler.class,
		UseChairHandler.class,
		ChangeChannelHandler.class,
		ChangeFmMapHandler.class,

		CloseRangeAttackHandler.class,
		RangedAttackHandler.class,
		PassiveEnergyAttackHandler.class,
		MagicAttackHandler.class,
		TakeDamageHandler.class,
		GeneralChatHandler.class,

		DistributeApHandler.class,
		DistributeSpHandler.class,

		PartyOperationHandler.class,
		PartyMemberCandidateRequestHandler.class,

		BuddylistModifyHandler.class,

		ItemMoveHandler.class,
		UseItemHandler.class,
		UseUpgradeScrollHandler.class,
		UsePotentialScrollHandler.class,
		UseMagnifyGlassHandler.class,
		UseCashItemHandler.class,
		UseBonusPotentialScrollHandler.class,
		BlackCubeResultHandler.class,
		UseEnhancementHandler.class,

		MesoDropHandler.class,
		QuestActionHandler.class,
		
		MoveLifeHandler.class,
		NpcActionHandler.class,
		ItemPickupHandler.class,
		NpcTalkHandler.class,
		NpcTalkMoreHandler.class,
		NpcShopHandler.class,
		QuickMoveRequestHandler.class,
		
		ChangeMapSpecialHandler.class,
		UseInnerPortalHandler.class,
		ChangeKeymapHandler.class,

		UpdateMatrixHandler.class,
		BattleAnalysisRequestHandler.class

    };
    
	public static void load() {
		try {
			for (Class<?> c : packethandlers) {
		        for (Method method : c.getMethods()) {
		            PacketHandler annotation = method.getAnnotation(PacketHandler.class);
		            if (annotation != null) {
		                if (isValidMethod(method)) {
		                    if (handlers.containsKey(annotation.opcode())) {
		                        System.out.println("Duplicate handler for opcode: " + annotation.opcode());
		                    } else {
		                        handlers.put(annotation.opcode().getValue(), method);
		                    }
		                } else {
		                    System.out.println("Failed to add handler with method name of: " + method.getName() + " in " + c.getName());
		                }
		            }
		        }
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.printf("A total of %s handlers have been loaded.\r\n", handlers.size());
	}
    
    private static boolean isValidMethod(Method method) {
		Class[] types = method.getParameterTypes();
        
        return types.length == 2 && types[0].equals(MapleClient.class) && types[1].equals(LittleEndianAccessor.class);
    }

    public static boolean handle(MapleClient client, int opcode, LittleEndianAccessor lea) {
        Method method = handlers.get(opcode);
        try {
            if (method != null) { 
                method.invoke(null, client, lea);
                return true;
            } else {
				if(!isSpamRecvHeader(opcode)){
					System.out.println("[Unhandled] [Recv] (" + opcode + ") " + lea);
				}
			}
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
        	e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

	public static boolean isSpamRecvHeader(int opcode) {
		if(spamRecvHandlers.size() == 0){
			spamRecvHandlers.add(RecvPacketOpcode.UPDATE_HYPER.getValue());
			spamRecvHandlers.add(RecvPacketOpcode.UPDATE_HYPER_SKILL.getValue());
			spamRecvHandlers.add(0x169); // UPDATE_HYPER_SKILL
			spamRecvHandlers.add(0x14D);
			spamRecvHandlers.add(0x135);
			spamRecvHandlers.add(RecvPacketOpcode.MOVE_PLAYER.getValue());
			spamRecvHandlers.add(RecvPacketOpcode.MOVE_LIFE.getValue());
			spamRecvHandlers.add(0x24B);
			spamRecvHandlers.add(0x152);
			spamRecvHandlers.add(RecvPacketOpcode.NPC_ACTION.getValue());
			spamRecvHandlers.add(RecvPacketOpcode.HEAL_OVER_TIME.getValue());
			spamRecvHandlers.add(RecvPacketOpcode.AUTO_AGGRO.getValue());
		}
		return spamRecvHandlers.contains(opcode);
	}

	public static boolean isSpamSendHeader(String packet) {
		String header = packet.substring(0, 5); // header: XX XX
		if(spamSendHandlers.size() == 0){
			spamSendHandlers.add("4A 00"); //
			spamSendHandlers.add("BD 03"); // MOVE_MONSTER
			spamSendHandlers.add("BE 03"); // MOVE_MONSTER_REPONSE
			spamSendHandlers.add("93 02"); // MOVE_PLAYER
			spamSendHandlers.add("E8 03");
			spamSendHandlers.add("4B 00");
			spamSendHandlers.add("0B 04");
			spamSendHandlers.add("D7 03"); // MONSTER_SKILL
			spamSendHandlers.add("B7 03"); // MONSTER_ENTER_FIELD
			spamSendHandlers.add("B8 03"); // MONSTER_LEAVE_FIELD
		}
		return spamSendHandlers.contains(header);
	}

	public static String getSendOpByHeader(short header){
		String res = null;
		for(SendPacketOpcode spo : SendPacketOpcode.values()){
			if(header == spo.getValue()){
				res = spo.toString();
				break;
			}
		}
		return res;
	}

	public static String getRecvOpByHeader(short header){
		String res = null;
		for(RecvPacketOpcode rpo :RecvPacketOpcode.values()){
			if(header == rpo.getValue()){
				res = rpo.toString();
				break;
			}
		}
		return res;
	}
}
