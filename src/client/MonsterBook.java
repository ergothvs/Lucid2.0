package client;

import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import net.DatabaseConnection;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import server.quest.MapleQuestStatus;
import tools.Pair;
import tools.Triple;
import tools.data.PacketWriter;
import tools.packet.CField;

public final class MonsterBook
        implements Serializable {

    private static final long serialVersionUID = 7179541993413738569L;
    private boolean changed = false;
    private int currentSet = -1;
    private int level = 0;
    private int setScore;
    private int finishedSets;
    private final Map<Integer, Integer> cards;
    private final List<Integer> cardItems = new ArrayList<Integer>();
    private final Map<Integer, Pair<Integer, Boolean>> sets = new HashMap<Integer, Pair<Integer, Boolean>>();

    public MonsterBook(Map<Integer, Integer> cards, MapleCharacter chr) {
        this.cards = cards;
        calculateItem();
        calculateScore();

        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(122800));
        if ((stat != null) && (stat.getCustomData() != null)) {
            this.currentSet = Integer.parseInt(stat.getCustomData());
            if ((!this.sets.containsKey(Integer.valueOf(this.currentSet))) || (!((Boolean) ((Pair) this.sets.get(Integer.valueOf(this.currentSet))).right).booleanValue())) {
                this.currentSet = -1;
            }
        }
        applyBook(chr, true);
    }

    public void applyBook(MapleCharacter chr, boolean first_login) {
        Equip item = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -55);
        if (item == null) {
            item = (Equip) MapleItemInformationProvider.getInstance().getEquipById(1172000);
            item.setPosition((short) -55);
        }
        modifyBook(item);
        if (first_login) {
            chr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(item);
        } else {
            chr.forceReAddItem_Book(item, MapleInventoryType.EQUIPPED);
            chr.equipChanged();
        }
    }

    public byte calculateScore() {
        byte returnval = 0;
        sets.clear();
        int oldLevel = level, oldSetScore = setScore;
        setScore = 0;
        finishedSets = 0;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (int i : cardItems) {
            //we need the card id but we store the mob id lol
            final Integer x = ii.getSetId(i);
            if (x != null && x.intValue() > 0) {
                final Triple<Integer, List<Integer>, List<Integer>> set = ii.getMonsterBookInfo(x);
                if (set != null) {
                    if (!sets.containsKey(x)) {
                        Pair<Integer, Boolean> put = sets.put(x, new Pair<>(1, Boolean.FALSE));
                    } else {
                        sets.get(x).left++;
                    }
                    if (sets.get(x).left == set.mid.size()) {
                        sets.get(x).right = Boolean.TRUE;
                        setScore += set.left;
                        if (currentSet == -1) {
                            currentSet = x;
                            returnval = 2;
                        }
                        finishedSets++;
                    }
                }
            }
        }
        level = 10;
        for (byte i = 0; i < 10; i++) {
            if (GameConstants.getSetExpNeededForLevel(i) > setScore) {
                level = (byte) i;
                break;
            }
        }
        if (level > oldLevel) {
            returnval = 2;
        } else if (setScore > oldSetScore) {
            returnval = 1;
        }
        return returnval;
    }

    public void writeCharInfoPacket(PacketWriter mplew) {
        List cardSize = new ArrayList(10);
        for (int i = 0; i < 10; i++) {
            cardSize.add(Integer.valueOf(0));
        }
        for (Iterator i$ = this.cardItems.iterator(); i$.hasNext();) {
            int x = ((Integer) i$.next()).intValue();
            cardSize.set(0, Integer.valueOf(((Integer) cardSize.get(0)).intValue() + 1));
            cardSize.set(x / 1000 % 10 + 1, Integer.valueOf(((Integer) cardSize.get(x / 1000 % 10 + 1)).intValue() + 1));
        }
        for (Iterator i$ = cardSize.iterator(); i$.hasNext();) {
            int i = ((Integer) i$.next()).intValue();
            mplew.writeInt(i);
        }
        mplew.writeInt(this.setScore);
        mplew.writeInt(this.currentSet);
        mplew.writeInt(this.finishedSets);
    }

    public void writeFinished(PacketWriter mplew) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        mplew.write(1);
        mplew.writeShort(this.cardItems.size());
        List mbList = new ArrayList(ii.getMonsterBookList());
        Collections.sort(mbList);
        int fullCards = mbList.size() / 8 + (mbList.size() % 8 > 0 ? 1 : 0);
        mplew.writeShort(fullCards);

        for (int i = 0; i < fullCards; i++) {
            int currentMask = 1;
            int maskToWrite = 0;
            for (int y = i * 8; (y < i * 8 + 8)
                    && (mbList.size() > y); y++) {
                if (this.cardItems.contains(mbList.get(y))) {
                    maskToWrite |= currentMask;
                }
                currentMask *= 2;
            }
            mplew.write(maskToWrite);
        }

        int fullSize = this.cardItems.size() / 2 + (this.cardItems.size() % 2 > 0 ? 1 : 0);
        mplew.writeShort(fullSize);
        for (int i = 0; i < fullSize; i++) {
            mplew.write(i == this.cardItems.size() / 2 ? 1 : 17);
        }
    }

    public void writeUnfinished(PacketWriter mplew) {
        mplew.write(0);
        mplew.writeShort(this.cardItems.size());
        for (Iterator i$ = this.cardItems.iterator(); i$.hasNext();) {
            int i = ((Integer) i$.next()).intValue();
            mplew.writeShort(i % 10000);
            mplew.write(1);
        }
    }

    public void calculateItem() {
        this.cardItems.clear();
        for (Map.Entry s : this.cards.entrySet()) {
            addCardItem(((Integer) s.getKey()).intValue(), ((Integer) s.getValue()).intValue());
        }
    }

    public void addCardItem(int key, int value) {
        if (value >= 2) {
            Integer x = MapleItemInformationProvider.getInstance().getItemIdByMob(key);
            if ((x != null) && (x.intValue() > 0)) {
                this.cardItems.add(Integer.valueOf(x.intValue()));
            }
        }
    }

    public void modifyBook(Equip eq) {
        eq.setStr((short) this.level);
        eq.setDex((short) this.level);
        eq.setInt((short) this.level);
        eq.setLuk((short) this.level);
        eq.setPotentialByLine(0, 0);
        eq.setPotentialByLine(1, 0);
        eq.setPotentialByLine(2, 0);
        eq.setBonusPotentialByLine(0, 0);
        eq.setBonusPotentialByLine(1, 0);
        if (this.currentSet > -1) {
            Triple set = MapleItemInformationProvider.getInstance().getMonsterBookInfo(this.currentSet);
            if (set != null) {
                for (int i = 0; i < ((List) set.right).size(); i++) {
                    if (i == 0) {
                        eq.setPotentialByLine(0, ((Integer) ((List) set.right).get(i)).intValue());
                    } else if (i == 1) {
                        eq.setPotentialByLine(1, ((Integer) ((List) set.right).get(i)).intValue());
                    } else if (i == 2) {
                        eq.setPotentialByLine(2, ((Integer) ((List) set.right).get(i)).intValue());
                    } else {
                        if (i == 3) {
                            break;
                        }
                        if (i == 4) {
                            eq.setBonusPotentialByLine(2, ((Integer) ((List) set.right).get(i)).intValue());
                            break;
                        }
                    }
                }
            } else {
                this.currentSet = -1;
            }
        }
    }

    public int getSetScore() {
        return this.setScore;
    }

    public int getLevel() {
        return this.level;
    }

    public int getSet() {
        return this.currentSet;
    }

    public boolean changeSet(int c) {
        if ((this.sets.containsKey(Integer.valueOf(c))) && (((Boolean) ((Pair) this.sets.get(Integer.valueOf(c))).right).booleanValue())) {
            this.currentSet = c;
            return true;
        }
        return false;
    }

    public void changed() {
        this.changed = true;
    }

    public Map<Integer, Integer> getCards() {
        return this.cards;
    }

    public final int getSeen() {
        return this.cards.size();
    }

    public final int getCaught() {
        int ret = 0;
        for (Iterator i$ = this.cards.values().iterator(); i$.hasNext();) {
            int i = ((Integer) i$.next()).intValue();
            if (i >= 2) {
                ret++;
            }
        }
        return ret;
    }

    public final int getLevelByCard(int cardid) {
        return this.cards.get(Integer.valueOf(cardid)) == null ? 0 : (this.cards.get(Integer.valueOf(cardid))).intValue();
    }

    public static final MonsterBook loadCards(int charid, MapleCharacter chr) throws SQLException {
        Map cards;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM monsterbook WHERE charid = ? ORDER BY cardid ASC")) {
            ps.setInt(1, charid);
            try (ResultSet rs = ps.executeQuery()) {
                cards = new LinkedHashMap();
                while (rs.next()) {
                    cards.put(Integer.valueOf(rs.getInt("cardid")), Integer.valueOf(rs.getInt("level")));
                }
            }
        }
        return new MonsterBook(cards, chr);
    }

    public final void saveCards(int charid) throws SQLException {
        if (!this.changed) {
            return;
        }
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM monsterbook WHERE charid = ?");
        ps.setInt(1, charid);
        ps.execute();
        ps.close();
        this.changed = false;
        if (this.cards.isEmpty()) {
            return;
        }

        boolean first = true;
        StringBuilder query = new StringBuilder();

        for (Map.Entry all : this.cards.entrySet()) {
            if (first) {
                first = false;
                query.append("INSERT INTO monsterbook VALUES (DEFAULT,");
            } else {
                query.append(",(DEFAULT,");
            }
            query.append(charid);
            query.append(",");
            query.append(all.getKey());
            query.append(",");
            query.append(all.getValue());
            query.append(")");
        }
        ps = con.prepareStatement(query.toString());
        ps.execute();
        ps.close();
    }

    public final boolean monsterCaught(MapleClient c, int cardid, String cardname) {
        if ((!this.cards.containsKey(Integer.valueOf(cardid))) || ((this.cards.get(Integer.valueOf(cardid))).intValue() < 2)) {
            this.changed = true;
            c.getPlayer().dropMessage(-6, new StringBuilder().append("Book entry updated - ").append(cardname).toString());
            c.getSession().write(CField.EffectPacket.showForeignEffect(16));
            this.cards.put(Integer.valueOf(cardid), Integer.valueOf(2));
            if (c.getPlayer().getQuestStatus(50195) != 1) {
                MapleQuest.getInstance(50195).forceStart(c.getPlayer(), 9010000, "1");
            }
            if (c.getPlayer().getQuestStatus(50196) != 1) {
                MapleQuest.getInstance(50196).forceStart(c.getPlayer(), 9010000, "1");
            }
            addCardItem(cardid, 2);
            byte rr = calculateScore();
            if (rr > 0) {
                if (c.getPlayer().getQuestStatus(50197) != 1) {
                    MapleQuest.getInstance(50197).forceStart(c.getPlayer(), 9010000, "1");
                }
                c.getSession().write(CField.EffectPacket.showForeignEffect(59));//was43
                if (rr > 1) {
                    applyBook(c.getPlayer(), false);
                }
            }
            return true;
        }
        return false;
    }

    public boolean hasCard(int cardid) {
        return this.cardItems == null ? false : this.cardItems.contains(Integer.valueOf(cardid));
    }

    public final void monsterSeen(MapleClient c, int cardid, String cardname) {
        if (this.cards.containsKey(Integer.valueOf(cardid))) {
            return;
        }
        this.changed = true;

        c.getPlayer().dropMessage(-6, new StringBuilder().append("New book entry - ").append(cardname).toString());
        this.cards.put(Integer.valueOf(cardid), Integer.valueOf(1));
        c.getSession().write(CField.EffectPacket.showForeignEffect(16));
    }
}
