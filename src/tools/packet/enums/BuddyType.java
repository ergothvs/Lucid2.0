package tools.packet.enums;


public enum BuddyType {
    UPDATE(23),
    RECEIVE_REQUEST(25),
    REQUEST_SENT(26),
    FULL_BUDDYLIST(27),
    USER_BUDDYLIST_FULL(28),
    CHAR_ALREADY_IS_BUDDY(29),
    ACCOUNT_BUDDY_ALREADY_SEND(30),
    WAITING_ADDED_FOR_BUDDY(31),
    CANT_BUDDY_YOURSELF(32),
    CANT_BUDDY_GM(33),
    CHAR_NOT_REGISTERED(34),
    YOU_WAITING_FOR_BUDDY(36),
    ADD(38), // pure guess
    UPDATE_FRIEND_MAX(44),
    STILL_RECOVERING(50),
    DECLINE_MESSAGE(51),
    BESTIE_REQUEST(52),
    DECLINE_BESTIE_MESSAGE(53),
    STAR_PLANET_BUDDY_ERROR(65),
    ALREADY_MADE_FRIEND_REQUEST(49),
    BLOCKED_LIST_FULL(61),
    FRIEND_REJECTED_INVITE(78),
    FRIEND_NOT_FOUND(79),
    FAILED_VISIT_UNKNOWN_ERROR(80);


    private int value;

    BuddyType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
