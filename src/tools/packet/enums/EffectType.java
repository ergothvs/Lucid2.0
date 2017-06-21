package tools.packet.enums;

public enum EffectType {
    LEVEL_UP(0x00),
    UNK_29(0x01), // idk, something with skills
    UNK_30(0x02), // idk, something with skills as well. handled in the same part as 0x1
    UNK_31(0x03),
    UNK_32(0x04),
    SHOW_ITEM_GAIN(0x05), // int byte int int
    UNK_35(0x06),
    UNK_36(0x07),
    UNK_38(0x08), // bool str int
    UNK_40(0x09), // byte int
    UNK_34(0x0A),
    RESIST(0x0B),
    UNK_41(0x0C), // byte byte byte int (something with skills?)
    QUEST_COMPLETION(0x0D),
    KANNA_JOB_ADV(0x0E),
    SMALL_OUTWARDS_RING(0x0F), // used for experience gain?
    HP_RECOVERY(0x10), // byte amount, 0 = miss
    REWARD_ITEM(0x11),
    UNK_45(0x12), // string
    UNK_46(0x14), // int if(byte) {string}
    ITEM_LEVEL_UP(0x15),
    MAKER_SUCCEED(0x16),
    MESO_ABOVE_HEAD(0x17),
    SUCK_IN_CHARGE(0x18),
    EXP_ABOVE_HEAD(0x19),
    UNK_16(0x1A), // bool int int str, but crashed when I tried doing this
    WHEEL_OF_DESTINY(0x1C), // byte
    PREMIUM_WHEEL_OF_DESTINY(0x1D), // long (time until when you can use the item)
    UNK_18(0x1F), // string
    UNK_19(0x20), // if(byte) string int int
    UNK_20(0x21), // string int int
    UNK(0x22), // int + str
    UNK_21(0x23), // str
    UNK_22(0x24), // str + int
    SPIRIT_STONE_REVIVE(0x25),
    HP_RECOVERY_INT_BYTE(0x26), // int amount, byte idk
    HP_RECOVERY_INT(0x27), // int amount
    UNK_1(0x29),
    UNK_2(0x2A),
    GRADE_UP(0x2B),
    BLINK(0x2C),
    UNK_28(0x2D),
    UNK_23(0x2E), // int int int byte
    UNK_3(0x2F), // int int
    RAINING_SPEARS(0x30),
    DARKNESS(0x31), // flashlight effect in darkness, like that one Oz JQ. bool = active
    UNK_4(0x32),
    ANGELIC_RECHARGE(0x33),
    JEWEL_CRAFT(0x34), // byte: 0/2 = successful (int), 3 = fail (int), 5 = idk, 1/4 = fail (msg if int != 0)
    UNK_5(0x35),
    ARAN_QUESTION_MARK(0x36),
    UNK_39(0x37), // byte byte int int
    STICK_WITH_NUMBER(0x38), // displays 1~5
    UNK_17(0x39), // displays 1~5
    BOMB_WITH_TIMER(0x3A), // byte (1 = remove bomb, 0 => 1st int = number displayed, 2nd int = ?)
    UNK_6(0x3B),
    UNK_43(0x3C),
    UNK_7(0x3D),
    UNK_24(0x3E), // bool int int str int int int int int int, maybe one more int (in an else)
    UNK_26(0x3F),
    UNK_37(0x40), // int int
    UNK_25(0x41),
    UNK_27(0x42), // ItemSlotBase::Decode
    UNK_44(0x43),
    CATCH(0x44),
    FAIL_CATCH(0x45),
    HP_REGEN(0x46), // another one? int (amount) int (idk)
    UNK_8(0x47), // if(byte) int
    UNK_9(0x48), // short int bool byte bool
    UNK_13(0x49),
    UNK_14(0x4A),
    UNK_10(0x4B),
    UNK_11(0x4C),
    UNK_12(0x4E),
    FAMILIAR_ESCAPE(0x4F),
    UNK_15(0x50); // idk, something that can say "Draw"



    private int value;
    EffectType(int code){
        this.value = code;
    }

    public int getValue(){
        return value;
    }


}
