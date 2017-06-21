package server.life;

import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import handling.channel.ChannelServer;
import handling.world.MapleCharacterLook;
import handling.world.World;
import net.DatabaseConnection;
import server.maps.MapleMap;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;

public class PlayerNPC extends MapleNPC implements MapleCharacterLook {

    public static final boolean Auto_Update = false;
    private Map<Byte, Integer> equips = new HashMap<>();
    private Map<Byte, Integer> secondEquips = new HashMap<>();
    private int mapid, face, hair, charId, elf, faceMarking, secondFace, secondHair;
    private short job;
    private byte skin, gender, secondSkin, secondGender;
    private final int[] pets = new int[3];

    public PlayerNPC(ResultSet rs) throws Exception {
        super(rs.getInt("ScriptId"), rs.getString("name"));
        hair = rs.getInt("hair");
        secondHair = hair;
        face = rs.getInt("face");
        secondFace = face;
        mapid = rs.getInt("map");
        skin = rs.getByte("skin");
        secondSkin = skin;
        charId = rs.getInt("charid");
        gender = rs.getByte("gender");
        secondGender = gender;
        job = rs.getShort("job");
        elf = rs.getInt("elf");
        faceMarking = rs.getInt("faceMarking");
        setCoords(rs.getInt("x"), rs.getInt("y"), rs.getInt("dir"), rs.getInt("Foothold"));
        String[] pet = rs.getString("pets").split(",");
        for (int i = 0; i < 3; i++) {
            if (pet[i] != null) {
                pets[i] = Integer.parseInt(pet[i]);
            } else {
                pets[i] = 0;
            }
        }

        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM playernpcs_equip WHERE NpcId = ?")) {
            ps.setInt(1, getId());
            try (ResultSet rs2 = ps.executeQuery()) {
                while (rs2.next()) {
                    equips.put(rs2.getByte("equippos"), rs2.getInt("equipid"));
                    secondEquips.put(rs2.getByte("equippos"), rs2.getInt("equipid"));
                }
            }
        }
    }

    public PlayerNPC(MapleCharacter cid, int npc, MapleMap map, MapleCharacter base) {
        super(npc, cid.getName());
        this.charId = cid.getId();
        this.mapid = map.getId();
        setCoords(base.getTruePosition().x, base.getTruePosition().y, 0, base.getFH()); //0 = facing dir? no idea, but 1 dosnt work
        update(cid);
    }

    private void setCoords(int x, int y, int f, int fh) {
        setPosition(new Point(x, y));
        setCy(y);
        setRx0(x - 50);
        setRx1(x + 50);
        setF(f);
        setFh(fh);
    }

    public static void loadAll() {
        List<PlayerNPC> toAdd = new ArrayList<>();
        Connection con = DatabaseConnection.getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM playernpcs"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    toAdd.add(new PlayerNPC(rs));
                }
            }
        } catch (Exception se) {
        }
        for (PlayerNPC npc : toAdd) {
            npc.addToServer();
        }
    }

    public static void updateByCharId(MapleCharacter chr) {
        if (World.Find.findChannel(chr.getId()) > 0) { //if character is in cserv
            for (PlayerNPC npc : ChannelServer.getInstance(World.Find.findChannel(chr.getId())).getAllPlayerNPC()) {
                npc.update(chr);
            }
        }
    }

    public void addToServer() {
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.addPlayerNPC(this);
        }
    }

    public void removeFromServer() {
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.removePlayerNPC(this);
        }
    }

    private void update(MapleCharacter chr) {
        if (chr == null || charId != chr.getId()) {
            return; //cant use name as it mightve been change actually..
        }
        setName(chr.getName());
        setHair(chr.getHair());
        setSecondHair(chr.getHair());
        setFace(chr.getFace());
        setSecondFace(chr.getFace());
        setSkin((byte) (chr.getSkinColor()));
        setSecondSkin((byte) (chr.getSkinColor()));
        setGender(chr.getGender());
        setSecondGender(chr.getGender());
        setPets(chr.getPets());
        setJob(chr.getJob());
        setElf(chr.getElf());
        setFaceMarking(chr.getFaceMarking());

        equips = new HashMap<>();
        for (Item item : chr.getInventory(MapleInventoryType.EQUIPPED).newList()) {
            if (item.getPosition() < -127) {
                continue;
            }
            equips.put((byte) item.getPosition(), item.getItemId());
        }

        secondEquips = new HashMap<>();
        for (Item item : chr.getInventory(MapleInventoryType.EQUIPPED).newList()) {
            if (item.getPosition() < -127) {
                continue;
            }
            secondEquips.put((byte) item.getPosition(), item.getItemId());
        }
        saveToDB();
    }

    public void destroy() {
        destroy(false); //just sql
    }

    public void destroy(boolean remove) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM playernpcs WHERE scriptid = ?");
            ps.setInt(1, getId());
            ps.executeUpdate();
            ps.close();

            ps = con.prepareStatement("DELETE FROM playernpcs_equip WHERE npcid = ?");
            ps.setInt(1, getId());
            ps.executeUpdate();
            ps.close();
            if (remove) {
                removeFromServer();
            }
        } catch (SQLException se) {
        }
    }

    public void saveToDB() {
        Connection con = DatabaseConnection.getConnection();
        try {

            if (getNPCFromWZ() == null) {
                destroy(true);
                return;
            }
            destroy();
            PreparedStatement ps = con.prepareStatement("INSERT INTO playernpcs(name, hair, face, skin, x, y, map, charid, scriptid, foothold, dir, gender, pets, job, elf, faceMarking) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int k = 0;
            ps.setString(++k, getName());
            ps.setInt(++k, getHair());
            ps.setInt(++k, getFace());
            ps.setInt(++k, getSkinColor());
            ps.setInt(++k, getTruePosition().x);
            ps.setInt(++k, getTruePosition().y);
            ps.setInt(++k, getMapId());
            ps.setInt(++k, getCharId());
            ps.setInt(++k, getId());
            ps.setInt(++k, getFh());
            ps.setInt(++k, getF());
            ps.setInt(++k, getGender());
            String[] pet = {"0", "0", "0"};
            for (int i = 0; i < 3; i++) {
                if (pets[i] > 0) {
                    pet[i] = String.valueOf(pets[i]);
                }
            }
            ps.setString(++k, pet[0] + "," + pet[1] + "," + pet[2]);
            ps.setShort(++k, getJob());
            ps.setInt(++k, getElf());
            ps.setInt(++k, getFaceMarking());
            ps.executeUpdate();
            ps.close();

            ps = con.prepareStatement("INSERT INTO playernpcs_equip(npcid, charid, equipid, equippos) VALUES (?, ?, ?, ?)");
            ps.setInt(1, getId());
            ps.setInt(2, getCharId());
            for (Entry<Byte, Integer> equip : equips.entrySet()) {
                ps.setInt(3, equip.getValue());
                ps.setInt(4, equip.getKey());
                ps.executeUpdate();
            }
            ps.close();
        } catch (SQLException se) {
        }
    }

    @Override
    public short getJob() {
        return job;
    }

    @Override
    public int getFaceMarking() {
        return faceMarking;
    }

    @Override
    public int getElf() {
        return elf;
    }

    @Override
    public Map<Byte, Integer> getEquips(boolean fusionAnvil) {
        return equips;
    }

    @Override
    public Map<Byte, Integer> getSecondEquips(boolean fusionAnvil) {
        return secondEquips;
    }

    @Override
    public Map<Byte, Integer> getTotems() {
        return new HashMap<>();
    }

    @Override
    public byte getSkinColor() {
        return skin;
    }

    @Override
    public byte getGender() {
        return gender;
    }

    @Override
    public int getFace() {
        return face;
    }

    @Override
    public int getHair() {
        return hair;
    }

    @Override
    public byte getSecondSkinColor() {
        return secondSkin;
    }

    @Override
    public byte getSecondGender() {
        return secondGender;
    }

    @Override
    public int getSecondFace() {
        return secondFace;
    }

    @Override
    public int getSecondHair() {
        return secondHair;
    }

    public int getCharId() {
        return charId;
    }

    public int getMapId() {
        return mapid;
    }

    public void setJob(short job) {
        this.job = job;
    }

    public void setFaceMarking(int faceMarking) {
        this.faceMarking = faceMarking;
    }

    public void setElf(int elf) {
        this.elf = elf;
    }

    public void setSkin(byte s) {
        this.skin = s;
    }

    public void setFace(int f) {
        this.face = f;
    }

    public void setHair(int h) {
        this.hair = h;
    }

    public void setGender(int g) {
        this.gender = (byte) g;
    }

    public void setSecondSkin(byte s) {
        this.secondSkin = s;
    }

    public void setSecondFace(int f) {
        this.secondFace = f;
    }

    public void setSecondHair(int h) {
        this.secondHair = h;
    }

    public void setSecondGender(int g) {
        this.secondGender = (byte) g;
    }

    public int getPet(int i) {
        return pets[i] > 0 ? pets[i] : 0;
    }

    public void setPets(List<MaplePet> p) {
        for (int i = 0; i < 3; i++) {
            if (p != null && p.size() > i && p.get(i) != null) {
                this.pets[i] = p.get(i).getPetItemId();
            } else {
                this.pets[i] = 0;
            }
        }
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.getSession().write(NPCPacket.spawnNPC(this, true));
        client.getSession().write(CWvsContext.spawnPlayerNPC(this));
        client.getSession().write(NPCPacket.spawnNPCRequestController(this, true));
    }

    public MapleNPC getNPCFromWZ() {
        MapleNPC npc = MapleLifeFactory.getNPC(getId());
        if (npc != null) {
            npc.setName(getName());
        }
        return npc;
    }
}
