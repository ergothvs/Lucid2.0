package tools.packet.enums;


public enum ReviveType {
    DEFAULT(0),
    PARTY_POINTS(1),
    EMPTY(2),
    CURRENT_MAP(3),
    REVIVE_AFTER_BATTLE(4),
    SOUL_STONE(5),
    WHEEL_OF_DESTINY(6),
    WHEEL_OF_DESTINY_2(7),
    DEFAULT_2(8),
    ANNIVERSARY_SURPRISE_CHANCE(9),
    MAPLEPOINT_200(10),
    CURRENT_MAP_2(11);

    private int value;
    ReviveType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
