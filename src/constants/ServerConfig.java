package constants;

public class ServerConfig {

	// Database Connection
	public static String SQL_PORT = "3306";
	public static String SQL_DATABASE = "mushy";
	public static String SQL_USER = "root";
	public static String SQL_PASS = "";

	// Server Configuration	
	public static String SERVER_NAME;
	public static String EVENT_MSG;
	public static String SCROLL_MESSAGE;
	public static String IP_ADDRESS;
	public static byte MAX_CHARACTERS;
	public static byte CHANNEL_COUNT;
	public static short USER_LIMIT;

	/* Rates */
	public static final int EXP_RATE = 9;
	public static final int MESO_RATE = 5;
	public static final int DROP_RATE = 3;
	
	public static final int CASH_DROP_RATE = 20; // out of 100
	
	/* Red Events */
	public static boolean RED_EVENT_10 = false; // Makes cassandra popup when you login at lvl<10 (maple island)
	public static boolean RED_EVENT = false; // Makes red even notification popup (cassandra) When you login at level 11+
}
