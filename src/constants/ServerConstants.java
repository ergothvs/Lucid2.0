package constants;

public class ServerConstants {
	
    public static final short MAPLE_VERSION = (short) 179;
    public static final String MAPLE_PATCH = "4";

    public enum PlayerGMRank {

        NORMAL('@', 0),
        INTERN('!', 1),
        GM('!', 2),
        SUPERGM('!', 3),
        ADMIN('!', 4);
        private final char commandPrefix;
        private final int level;

        PlayerGMRank(char ch, int level) {
            commandPrefix = ch;
            this.level = level;
        }

        public String getCommandPrefix() {
            return String.valueOf(commandPrefix);
        }

        public int getLevel() {
            return level;
        }
    }

    public enum CommandType {

        NORMAL(0),
        TRADE(1);
        private final int level;

        CommandType(int level) {
            this.level = level;
        }

        public int getType() {
            return level;
        }
    }
}
