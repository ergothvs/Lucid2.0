package client.inventory;

public enum EnhanceStat {

    WATK(0x1),
    MATK(0x2),
    STR(0x4),
    DEX(0x8),
    INT(0x10),
    LUK(0x20),
    WDEF(0x40),
//    MDEF(0x80),
    MHP(0x100),
    MMP(0x200),
//    ACC(0x400),
//    AVOID(0x800),
    JUMP(0x1000),
    SPEED(0x2000);

    private int value;

    EnhanceStat(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

}
