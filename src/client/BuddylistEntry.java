package client;

import tools.data.PacketWriter;

public class BuddylistEntry {

    private String name;
    private String group;
    private String nick;
    private String memo;
    private int cid;
    private int channel;
    private int mobile;
    private int accountID;
    private boolean visible, inShop;

    /**
     *
     * @param name
     * @param characterId
     * @param group
     * @param channel should be -1 if the buddy is offline
     * @param visible
     */
    public BuddylistEntry(String name, int characterId, String group, int channel, boolean visible, int mobile, int accountID, String nick, String memo, boolean inShop) {
        super();
        this.cid = characterId;
        this.name = name;
        this.channel = channel;
        this.group = group;
        this.visible = visible;
        this.mobile = mobile;
        this.accountID = accountID;
        this.nick = nick;
        this.memo = memo;
        this.inShop = inShop;
    }

    /**
     *
     * @param name
     * @param characterId
     * @param group
     * @param channel should be -1 if the buddy is offline
     * @param visible
     */
    public BuddylistEntry(String name, int characterId, String group, int channel, boolean visible) {
        this(name, characterId, group, channel, visible, 0, characterId, name, "", false);
    }

    /**
     * @return the channel the character is on. If the character is offline
     * returns -1.
     */
    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public boolean isOnline() {
        return channel >= 0;
    }

    public void setOffline() {
        channel = -1;
    }

    public String getName() {
        return name;
    }

    public int getCharacterId() {
        return cid;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String g) {
        this.group = g;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cid;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BuddylistEntry other = (BuddylistEntry) obj;
        return cid == other.cid;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public boolean isInShop() {
        return inShop;
    }

    public void setInShop(boolean inShop) {
        this.inShop = inShop;
    }

    public void encode(PacketWriter pw) {
        pw.writeInt(getCharacterId());
        pw.writeAsciiString(getName(), 13);
        pw.write(isVisible() ? 0 : 1);//if adding = 2 <= nFlag
        pw.writeInt(getChannel() == -1 ? -1 : getChannel());
        pw.writeAsciiString(getGroup(), 17);
        pw.writeInt(getMobile());
        pw.writeInt(getAccountID());
        pw.writeAsciiString(getNick(), 13);
        pw.writeAsciiString(getMemo(), 256);
        pw.writeInt(isInShop() ? 1 : 0);
        // total size: 317/0x13D
    }
}
