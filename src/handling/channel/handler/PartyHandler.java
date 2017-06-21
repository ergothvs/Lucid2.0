package handling.channel.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import constants.MapConstants;
import handling.channel.ChannelServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.exped.ExpeditionType;
import handling.world.exped.MapleExpedition;
import handling.world.exped.PartySearch;
import handling.world.exped.PartySearchType;
import server.events.MapleDojoAgent;
import server.maps.FieldLimitType;
import server.quest.MapleQuest;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.enums.PartyType;

public class PartyHandler {

    public static final void PartyRequestResponse(LittleEndianAccessor slea, MapleClient c) {
        int action = slea.readByte();
        if ((action == 50)) {
            MapleCharacter chr = c.getPlayer().getMap().getCharacterById(slea.readInt());
            if ((chr != null) && (chr.getParty() == null) && (c.getPlayer().getParty() != null) && (c.getPlayer().getParty().getLeader().getId() == c.getPlayer().getId()) && (c.getPlayer().getParty().getMembers().size() < 8) && (c.getPlayer().getParty().getExpeditionId() <= 0) && (chr.getQuestNoAdd(MapleQuest.getInstance(122901)) == null) && (c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122900)) == null)) {
                chr.setParty(c.getPlayer().getParty());
                World.Party.updateParty(c.getPlayer().getParty().getId(), PartyOperation.JOIN, new MaplePartyCharacter(chr));
                chr.receivePartyMemberHP();
                chr.updatePartyMemberHP();
            }
            return;
        }
        int partyid = slea.readInt();
        if ((c.getPlayer().getParty() == null) && (c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
            MapleParty party = World.Party.getParty(partyid);
            if (party != null) {
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                if (action == PartyType.PARTY_REQUEST_ACCEPT.getValue()) {
                    if (party.getMembers().size() < 8) {
                        c.getPlayer().setParty(party);
                        World.Party.updateParty(partyid, PartyOperation.JOIN, new MaplePartyCharacter(c.getPlayer()));
                        c.getPlayer().receivePartyMemberHP();
                        c.getPlayer().updatePartyMemberHP();
                    } else {
                        c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(PartyType.PARTY_FULL_ERROR.getValue(), null));
                    }
                } else if (action != PartyType.PARTY_REQUEST_ACCEPT.getValue()) {
                    MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterById(party.getLeader().getId());
                    if (cfrom != null) {
                        cfrom.getClient().getSession().write(CWvsContext.PartyPacket.partyStatusMessage(29, c.getPlayer().getName()));
                    }
                }
            } else {
                c.getPlayer().dropMessage(5, "The party you are trying to join does not exist");
            }
        } else {
            c.getPlayer().dropMessage(5, "You can't join the party as you are already in one");
        }
    }

    public static final void PartyOperation(LittleEndianAccessor lea, MapleClient c) {
        int operation = lea.readByte();
        MapleParty party = c.getPlayer().getParty();
        MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());

        switch (operation) {
            case 1:
                if (party == null) {
                    boolean appliable = lea.readByte() != 0;
                    String name = lea.readMapleAsciiString();
                    party = World.Party.createParty(partyplayer, name, appliable);
                    c.getPlayer().setParty(party);
                    c.getSession().write(CWvsContext.PartyPacket.partyCreated(party));
                } else {
                    if (party.getExpeditionId() > 0) {
                        c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                        return;
                    }
                    if ((partyplayer.equals(party.getLeader())) && (party.getMembers().size() == 1)) {
                        c.getSession().write(CWvsContext.PartyPacket.partyCreated(party));
                    } else {
                        c.getPlayer().dropMessage(5, "You can't create a party as you are already in one");
                    }
                }
                break;
            case 2://dispand and leave?
                if (party == null) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                if (partyplayer.equals(party.getLeader())) {
                    if (MapConstants.isDojo(c.getPlayer().getMapId())) {
                        MapleDojoAgent.failed(c.getPlayer());
                    }
                    if (c.getPlayer().getPyramidSubway() != null) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().disbandParty();
                    }
                } else {
                    if (MapConstants.isDojo(c.getPlayer().getMapId())) {
                        MapleDojoAgent.failed(c.getPlayer());
                    }
                    if (c.getPlayer().getPyramidSubway() != null) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().leftParty(c.getPlayer());
                    }
                }
                c.getPlayer().setParty(null);
                break;
            case 3:
                int partyid = lea.readInt();
                if (party == null) {
                    party = World.Party.getParty(partyid);
                    if (party != null) {
                        if (party.getExpeditionId() > 0) {
                            c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                            return;
                        }
                        if ((party.getMembers().size() < 8) && (c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
                            c.getPlayer().setParty(party);
                            World.Party.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
                            c.getPlayer().receivePartyMemberHP();
                            c.getPlayer().updatePartyMemberHP();
                        } else {
                            c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(22, null));
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "The party you are trying to join does not exist");
                    }
                } else {
                    c.getPlayer().dropMessage(5, "You can't join the party as you are already in one");
                }
                break;
            case 4:
                if (party == null) {
                    party = World.Party.createParty(partyplayer);
                    c.getPlayer().setParty(party);
                    c.getSession().write(CWvsContext.PartyPacket.partyCreated(party));
                }

                String theName = lea.readMapleAsciiString();
                int theCh = World.Find.findChannel(theName);
                if (theCh > 0) {
                    MapleCharacter invited = ChannelServer.getInstance(theCh).getPlayerStorage().getCharacterByName(theName);
                    if ((invited != null) && (invited.getParty() == null) && (invited.getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
                        if (party.getExpeditionId() > 0) {
                            c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                            return;
                        }
                        if (party.getMembers().size() < 8) {
                            c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(PartyType.INVITE_MESSAGE.getValue(), invited.getName()));
                            invited.getClient().getSession().write(CWvsContext.PartyPacket.partyInvite(c.getPlayer()));
                        } else {
                            c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(22, null));
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "The person you are trying to invite is already in a party.");
                    }
                } else {
                    c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(17, null));
                }
                break;
            case 6://was5
                if ((party == null) || (partyplayer == null) || (!partyplayer.equals(party.getLeader()))) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MaplePartyCharacter expelled = party.getMemberById(lea.readInt());
                if (expelled != null) {
                    if ((MapConstants.isDojo(c.getPlayer().getMapId())) && (expelled.isOnline())) {
                        MapleDojoAgent.failed(c.getPlayer());
                    }
                    if ((c.getPlayer().getPyramidSubway() != null) && (expelled.isOnline())) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.EXPEL, expelled);
                    if (c.getPlayer().getEventInstance() != null) {
                        if (expelled.isOnline()) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                    }
                }
                break;
            case 7://was 6
                if (party == null) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MaplePartyCharacter newleader = party.getMemberById(lea.readInt());
                if ((newleader != null) && (partyplayer.equals(party.getLeader()))) {
                    World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, newleader);
                }
                break;
            case 13:
                if(party != null){
                    if ((c.getPlayer().getEventInstance() != null) || (c.getPlayer().getPyramidSubway() != null) || (party.getExpeditionId() > 0) || (MapConstants.isDojo(c.getPlayer().getMapId()))) {
                        c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                        return;
                    }
                }

                break;
            case 66://was 7
                if (party != null) {
                    if ((c.getPlayer().getEventInstance() != null) || (c.getPlayer().getPyramidSubway() != null) || (party.getExpeditionId() > 0) || (MapConstants.isDojo(c.getPlayer().getMapId()))) {
                        c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                        return;
                    }
                    if (partyplayer.equals(party.getLeader())) {
                        World.Party.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
                    } else {
                        World.Party.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
                    }
                    c.getPlayer().setParty(null);
                }
                int partyid_ = lea.readInt();
                party = World.Party.getParty(partyid_);
                if ((party == null) || (party.getMembers().size() >= 8)) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MapleCharacter cfrom = c.getPlayer().getMap().getCharacterById(party.getLeader().getId());
                if ((cfrom != null) && (cfrom.getQuestNoAdd(MapleQuest.getInstance(122900)) == null)) {
                    c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(50, c.getPlayer().getName()));
                    cfrom.getClient().getSession().write(CWvsContext.PartyPacket.partyRequestInvite(c.getPlayer()));
                } else {
                    c.getPlayer().dropMessage(5, "Player was not found or player is not accepting party requests.");
                }
                break;
            case 8:
                if (lea.readByte() > 0) {
                    c.getPlayer().getQuestRemove(MapleQuest.getInstance(122900));
                } else {
                    c.getPlayer().getQuestNAdd(MapleQuest.getInstance(122900));
                }
                break;
            default:
                System.out.println("Unhandled Party function." + operation);
        }
    }

    public static void AllowPartyInvite(LittleEndianAccessor slea, MapleClient c) {
        if (slea.readByte() > 0) {
            c.getPlayer().getQuestRemove(MapleQuest.getInstance(122901));
        } else {
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(122901));
        }
    }

    public static void MemberSearch(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer().isInBlockedMap()) || (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()))) {
            c.getPlayer().dropMessage(5, "You may not do party search here.");
            return;
        }
        c.getSession().write(CWvsContext.PartyPacket.partyMemberCandidateResult(c.getPlayer()));
    }

    public static final void PartySearch(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer().isInBlockedMap()) || (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()))) {
            c.getPlayer().dropMessage(5, "You may not do party search here.");
            return;
        }
        List parties = new ArrayList<MapleParty>();
        for (MapleCharacter chr : c.getPlayer().getMap().getCharacters()) {
            if ((chr.getParty() != null)
                    && (chr.getParty().getId() != c.getPlayer().getParty().getId()) && (!parties.contains(chr.getParty()))) {
                parties.add(chr.getParty());
            }
        }

        c.getSession().write(CWvsContext.PartyPacket.showPartySearch(parties));
    }

    public static final void PartyListing(LittleEndianAccessor slea, MapleClient c) {
        int mode = slea.readByte();
        PartySearchType pst;
        switch (mode) {
            case -105:
            case -97:
            case 81:
            case 159:
                pst = PartySearchType.getById(slea.readInt());
                if ((pst == null) || (c.getPlayer().getLevel() > pst.maxLevel) || (c.getPlayer().getLevel() < pst.minLevel)) {
                    return;
                }
                if ((c.getPlayer().getParty() == null) && (World.Party.searchParty(pst).size() < 10)) {
                    MapleParty party = World.Party.createParty(new MaplePartyCharacter(c.getPlayer()), pst.id);
                    c.getPlayer().setParty(party);
                    c.getSession().write(CWvsContext.PartyPacket.partyCreated(party));
                    PartySearch ps = new PartySearch(slea.readMapleAsciiString(), pst.exped ? party.getExpeditionId() : party.getId(), pst);
                    World.Party.addSearch(ps);
                    if (pst.exped) {
                        c.getSession().write(CWvsContext.ExpeditionPacket.expeditionStatus(World.Party.getExped(party.getExpeditionId()), true, false));
                    }
                    c.getSession().write(CWvsContext.PartyPacket.partyListingAdded(ps));
                } else {
                    c.getPlayer().dropMessage(1, "Unable to create. Please leave the party.");
                }
                break;
            case -103:
            case -95:
            case 83:
            case 161:
                pst = PartySearchType.getById(slea.readInt());
                if ((pst == null) || (c.getPlayer().getLevel() > pst.maxLevel) || (c.getPlayer().getLevel() < pst.minLevel)) {
                    return;
                }
                c.getSession().write(CWvsContext.PartyPacket.getPartyListing(pst));
                break;
            case -102:
            case -94:
            case 84:
            case 162:
                break;
            case -101:
            case -93:
            case 85:
            case 163:
                MapleParty party = c.getPlayer().getParty();
                MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());
                if (party != null) {
                    break;
                }
                int theId = slea.readInt();
                party = World.Party.getParty(theId);
                if (party != null) {
                    PartySearch ps = World.Party.getSearchByParty(party.getId());
                    if ((ps != null) && (c.getPlayer().getLevel() <= ps.getType().maxLevel) && (c.getPlayer().getLevel() >= ps.getType().minLevel) && (party.getMembers().size() < 8)) {
                        c.getPlayer().setParty(party);
                        World.Party.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
                        c.getPlayer().receivePartyMemberHP();
                        c.getPlayer().updatePartyMemberHP();
                    } else {
                        c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(21, null));
                    }
                } else {
                    MapleExpedition exped = World.Party.getExped(theId);
                    if (exped != null) {
                        PartySearch ps = World.Party.getSearchByExped(exped.getId());
                        if ((ps != null) && (c.getPlayer().getLevel() <= ps.getType().maxLevel) && (c.getPlayer().getLevel() >= ps.getType().minLevel) && (exped.getAllMembers() < exped.getType().maxMembers)) {
                            int partyId = exped.getFreeParty();
                            if (partyId < 0) {
                                c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(21, null));
                            } else if (partyId == 0) {
                                party = World.Party.createPartyAndAdd(partyplayer, exped.getId());
                                c.getPlayer().setParty(party);
                                c.getSession().write(CWvsContext.PartyPacket.partyCreated(party));
                                c.getSession().write(CWvsContext.ExpeditionPacket.expeditionStatus(exped, true, false));
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                            } else {
                                c.getPlayer().setParty(World.Party.getParty(partyId));
                                World.Party.updateParty(partyId, PartyOperation.JOIN, partyplayer);
                                c.getPlayer().receivePartyMemberHP();
                                c.getPlayer().updatePartyMemberHP();
                                c.getSession().write(CWvsContext.ExpeditionPacket.expeditionStatus(exped, true, false));
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                            }
                        } else {
                            c.getSession().write(CWvsContext.ExpeditionPacket.expeditionError(0, c.getPlayer().getName()));
                        }
                    }
                }
                break;
            default:
                if (!c.getPlayer().isGM()) {
                    break;
                }
                System.out.println("Unknown PartyListing : " + mode + "\n" + slea);
        }
    }

    public static final void Expedition(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
            return;
        }
        int mode = slea.readByte();
        String name;
        MapleParty part;
        MapleExpedition exped;
        int cid;
        Iterator i$;

        switch (mode) {
            case 76://64
            case 134:
                ExpeditionType et = ExpeditionType.getById(slea.readInt());
                if ((et != null) && (c.getPlayer().getParty() == null) && (c.getPlayer().getLevel() <= et.maxLevel) && (c.getPlayer().getLevel() >= et.minLevel)) {
                    MapleParty party = World.Party.createParty(new MaplePartyCharacter(c.getPlayer()), et.exped);
                    c.getPlayer().setParty(party);
                    c.getSession().write(CWvsContext.PartyPacket.partyCreated(party));
                    c.getSession().write(CWvsContext.ExpeditionPacket.expeditionStatus(World.Party.getExped(party.getExpeditionId()), true, false));
                } else {
                    c.getSession().write(CWvsContext.ExpeditionPacket.expeditionError(0, ""));
                }
                break;
            case 77://65
            case 135:
                name = slea.readMapleAsciiString();
                int theCh = World.Find.findChannel(name);
                if (theCh > 0) {
                    MapleCharacter invited = ChannelServer.getInstance(theCh).getPlayerStorage().getCharacterByName(name);
                    MapleParty party = c.getPlayer().getParty();
                    if ((invited != null) && (invited.getParty() == null) && (party != null) && (party.getExpeditionId() > 0)) {
                        MapleExpedition me = World.Party.getExped(party.getExpeditionId());
                        if ((me != null) && (me.getAllMembers() < me.getType().maxMembers) && (invited.getLevel() <= me.getType().maxLevel) && (invited.getLevel() >= me.getType().minLevel)) {
                            c.getSession().write(CWvsContext.ExpeditionPacket.expeditionError(7, invited.getName()));
                            invited.getClient().getSession().write(CWvsContext.ExpeditionPacket.expeditionInvite(c.getPlayer(), me.getType().exped));
                        } else {
                            c.getSession().write(CWvsContext.ExpeditionPacket.expeditionError(3, invited.getName()));
                        }
                    } else {
                        c.getSession().write(CWvsContext.ExpeditionPacket.expeditionError(2, name));
                    }
                } else {
                    c.getSession().write(CWvsContext.ExpeditionPacket.expeditionError(0, name));
                }
                break;
            case 78://66
            case 136:
                name = slea.readMapleAsciiString();
                int action = slea.readInt();
                int theChh = World.Find.findChannel(name);
                if (theChh <= 0) {
                    break;
                }
                MapleCharacter cfrom = ChannelServer.getInstance(theChh).getPlayerStorage().getCharacterByName(name);
                if ((cfrom != null) && (cfrom.getParty() != null) && (cfrom.getParty().getExpeditionId() > 0)) {
                    MapleParty party = cfrom.getParty();
                    exped = World.Party.getExped(party.getExpeditionId());
                    if ((exped != null) && (action == 8)) {
                        if ((c.getPlayer().getLevel() <= exped.getType().maxLevel) && (c.getPlayer().getLevel() >= exped.getType().minLevel) && (exped.getAllMembers() < exped.getType().maxMembers)) {
                            int partyId = exped.getFreeParty();
                            if (partyId < 0) {
                                c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(21, null));
                            } else if (partyId == 0) {
                                party = World.Party.createPartyAndAdd(new MaplePartyCharacter(c.getPlayer()), exped.getId());
                                c.getPlayer().setParty(party);
                                c.getSession().write(CWvsContext.PartyPacket.partyCreated(party));
                                c.getSession().write(CWvsContext.ExpeditionPacket.expeditionStatus(exped, true, false));
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                            } else {
                                c.getPlayer().setParty(World.Party.getParty(partyId));
                                World.Party.updateParty(partyId, PartyOperation.JOIN, new MaplePartyCharacter(c.getPlayer()));
                                c.getPlayer().receivePartyMemberHP();
                                c.getPlayer().updatePartyMemberHP();
                                c.getSession().write(CWvsContext.ExpeditionPacket.expeditionStatus(exped, false, false));
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                            }
                        } else {
                            c.getSession().write(CWvsContext.ExpeditionPacket.expeditionError(3, cfrom.getName()));
                        }
                    } else if (action == 9) {
                        cfrom.getClient().getSession().write(CWvsContext.PartyPacket.partyStatusMessage(23, c.getPlayer().getName()));
                    }
                }
                break;
            case 79://67
            case 137:
                part = c.getPlayer().getParty();
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                exped = World.Party.getExped(part.getExpeditionId());
                if (exped != null) {
                    if (MapConstants.isDojo(c.getPlayer().getMapId())) {
                        MapleDojoAgent.failed(c.getPlayer());
                    }
                    if (exped.getLeader() == c.getPlayer().getId()) {
                        World.Party.disbandExped(exped.getId());
                        if (c.getPlayer().getEventInstance() != null) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                    } else if (part.getLeader().getId() == c.getPlayer().getId()) {
                        World.Party.updateParty(part.getId(), PartyOperation.DISBAND, new MaplePartyCharacter(c.getPlayer()));
                        if (c.getPlayer().getEventInstance() != null) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                        World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionLeft(c.getPlayer().getName()), null);
                    } else {
                        World.Party.updateParty(part.getId(), PartyOperation.LEAVE, new MaplePartyCharacter(c.getPlayer()));
                        if (c.getPlayer().getEventInstance() != null) {
                            c.getPlayer().getEventInstance().leftParty(c.getPlayer());
                        }
                        World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionLeft(c.getPlayer().getName()), null);
                    }
                    if (c.getPlayer().getPyramidSubway() != null) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    c.getPlayer().setParty(null);
                }
                break;
            case 80://68
            case 138:
                part = c.getPlayer().getParty();
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                exped = World.Party.getExped(part.getExpeditionId());
                if ((exped != null) && (exped.getLeader() == c.getPlayer().getId())) {
                    cid = slea.readInt();
                    for (i$ = exped.getParties().iterator(); i$.hasNext();) {
                        int i = ((Integer) i$.next()).intValue();
                        MapleParty par = World.Party.getParty(i);
                        if (par != null) {
                            MaplePartyCharacter expelled = par.getMemberById(cid);
                            if (expelled != null) {
                                if ((expelled.isOnline()) && (MapConstants.isDojo(c.getPlayer().getMapId()))) {
                                    MapleDojoAgent.failed(c.getPlayer());
                                }
                                World.Party.updateParty(i, PartyOperation.EXPEL, expelled);
                                if ((c.getPlayer().getEventInstance() != null)
                                        && (expelled.isOnline())) {
                                    c.getPlayer().getEventInstance().disbandParty();
                                }

                                if ((c.getPlayer().getPyramidSubway() != null) && (expelled.isOnline())) {
                                    c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                                }
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionLeft(expelled.getName()), null);
                                break;
                            }
                        }
                    }
                }
                break;
            case 81://69
            case 139:
                part = c.getPlayer().getParty();
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                exped = World.Party.getExped(part.getExpeditionId());
                if ((exped != null) && (exped.getLeader() == c.getPlayer().getId())) {
                    MaplePartyCharacter newleader = part.getMemberById(slea.readInt());
                    if (newleader != null) {
                        World.Party.updateParty(part.getId(), PartyOperation.CHANGE_LEADER, newleader);
                        exped.setLeader(newleader.getId());
                        World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionLeaderChanged(0), null);
                    }
                }
                break;
            case 82://70
            case 140:
                part = c.getPlayer().getParty();
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                exped = World.Party.getExped(part.getExpeditionId());
                if ((exped != null) && (exped.getLeader() == c.getPlayer().getId())) {
                    cid = slea.readInt();
                    for (i$ = exped.getParties().iterator(); i$.hasNext();) {
                        int i = ((Integer) i$.next()).intValue();
                        MapleParty par = World.Party.getParty(i);
                        if (par != null) {
                            MaplePartyCharacter newleader = par.getMemberById(cid);
                            if ((newleader != null) && (par.getId() != part.getId())) {
                                World.Party.updateParty(par.getId(), PartyOperation.CHANGE_LEADER, newleader);
                            }
                        }
                    }
                }
                break;
            case 83://71
            case 141:
                part = c.getPlayer().getParty();
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                exped = World.Party.getExped(part.getExpeditionId());
                if ((exped != null) && (exped.getLeader() == c.getPlayer().getId())) {
                    int partyIndexTo = slea.readInt();
                    if ((partyIndexTo < exped.getType().maxParty) && (partyIndexTo <= exped.getParties().size())) {
                        cid = slea.readInt();
                        for (i$ = exped.getParties().iterator(); i$.hasNext();) {
                            int i = ((Integer) i$.next()).intValue();
                            MapleParty par = World.Party.getParty(i);
                            if (par != null) {
                                MaplePartyCharacter expelled = par.getMemberById(cid);
                                if ((expelled != null) && (expelled.isOnline())) {
                                    MapleCharacter chr = World.getStorage(expelled.getChannel()).getCharacterById(expelled.getId());
                                    if (chr == null) {
                                        break;
                                    }
                                    if (partyIndexTo < exped.getParties().size()) {
                                        MapleParty party = World.Party.getParty((exped.getParties().get(partyIndexTo)).intValue());
                                        if ((party == null) || (party.getMembers().size() >= 8)) {
                                            c.getPlayer().dropMessage(5, "Invalid party.");
                                            break;
                                        }
                                    }
                                    if (MapConstants.isDojo(c.getPlayer().getMapId())) {
                                        MapleDojoAgent.failed(c.getPlayer());
                                    }
                                    World.Party.updateParty(i, PartyOperation.EXPEL, expelled);
                                    if (partyIndexTo < exped.getParties().size()) {
                                        MapleParty party = World.Party.getParty((exped.getParties().get(partyIndexTo)).intValue());
                                        if ((party != null) && (party.getMembers().size() < 8)) {
                                            World.Party.updateParty(party.getId(), PartyOperation.JOIN, expelled);
                                            chr.receivePartyMemberHP();
                                            chr.updatePartyMemberHP();
                                            chr.getClient().getSession().write(CWvsContext.ExpeditionPacket.expeditionStatus(exped, true, false));
                                        }
                                    } else {
                                        MapleParty party = World.Party.createPartyAndAdd(expelled, exped.getId());
                                        chr.setParty(party);
                                        chr.getClient().getSession().write(CWvsContext.PartyPacket.partyCreated(party));
                                        chr.getClient().getSession().write(CWvsContext.ExpeditionPacket.expeditionStatus(exped, true, false));
                                        World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                                    }
                                    if ((c.getPlayer().getEventInstance() != null)
                                            && (expelled.isOnline())) {
                                        c.getPlayer().getEventInstance().disbandParty();
                                    }

                                    if (c.getPlayer().getPyramidSubway() == null) {
                                        break;
                                    }
                                    c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                                    break;
                                }
                            }
                        }
                    }

                }

                break;
            default:
                if (!c.getPlayer().isGM()) {
                    break;
                }
                System.out.println("Unknown Expedition : " + mode + "\n" + slea);
        }
    }
}
