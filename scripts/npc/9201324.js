var points;

function start() {
    var record = cm.getQuestRecord(150001);
    points = record.getCustomData() == null ? "0" : record.getCustomData();
    cm.sendSimple("Which job advancement would you like to do? \r\n #L0# #d2nd Job Advancement #l \r\n #L1# #d3rd Job Advancement #l \r\n #L2# #d4th Job Advancement #l \r\n #L3# #d5th Job Advancement #l");
}

function action(mode, type, selection) {
    if (mode == 1) {
        switch (selection) {
            case 0:
                if (cm.getParty() != null && cm.getQuestRecord && cm.getQuestStatus(2013) != 2) {
                    if (cm.getDisconnected("JobAdvance2") != null) {
                        cm.getDisconnected("JobAdvance2").registerPlayer(cm.getPlayer());
                    } else if (cm.isLeader()) {
                        var party = cm.getPlayer().getParty().getMembers();
                        var mapId = cm.getPlayer().getMapId();
                        var next = true;
                        var it = party.iterator();
                        while (it.hasNext()) {
                            var cPlayer = it.next();
                            var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                            if (ccPlayer == null || ccPlayer.getLevel() < 30) {
                                next = false;
                                break;
                            }
                        }	
                        if (next) {
                            var q = cm.getEventManager("JobAdvance2");
                            if (q == null) {
                                cm.sendOk("Unknown error occured");
                            } else {
                                q.startInstance(cm.getParty(), cm.getMap());
                            }
                        } else {
                            cm.sendOk("All players must be in map and above level 30.");
                        }
                    } else {
                        cm.sendOk("You are not the leader of the party, please ask your leader to talk to me.");
                    }
                } else {
                    cm.sendOk("You haven't formed a party or you have already done your 2nd job advancement.");
                }
                break;
            case 1:
                if (cm.getParty() != null) {
                    if (cm.getDisconnected("JobAdvance3") != null) {
                        cm.getDisconnected("JobAdvance3").registerPlayer(cm.getPlayer());
                    } else if (cm.isLeader()) {
                        var party = cm.getPlayer().getParty().getMembers();
                        var mapId = cm.getPlayer().getMapId();
                        var next = true;
                        var it = party.iterator();
                        while (it.hasNext()) {
                            var cPlayer = it.next();
                            var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                            if (ccPlayer == null || ccPlayer.getLevel() < 60) {
                                next = false;
                                break;
                            }
                        }	
                        if (next) {
                            var q = cm.getEventManager("JobAdvance3");
                            if (q == null) {
                                cm.sendOk("Unknown error occured");
                            } else {
                                q.startInstance(cm.getParty(), cm.getMap());
                            }
                        } else {
                            cm.sendOk("All players must be in map and above level 60.");
                        }
                    } else {
                        cm.sendOk("You are not the leader of the party, please ask your leader to talk to me.");
                    }
                } else {
                    cm.sendOk("You either haven't formed a party yet or you have already advanced.");
                }
                break;
            case 2:
                if (cm.getParty() != null) {
                    if (cm.getDisconnected("JobAdvance4") != null) {
                        cm.getDisconnected("JobAdvance4").registerPlayer(cm.getPlayer());
                    } else if (cm.isLeader()) {
                        var party = cm.getPlayer().getParty().getMembers();
                        var mapId = cm.getPlayer().getMapId();
                        var next = true;
                        var it = party.iterator();
                        while (it.hasNext()) {
                            var cPlayer = it.next();
                            var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                            if (ccPlayer == null || ccPlayer.getLevel() < 100) {
                                next = false;
                                break;
                            }
                        }	
                        if (next) {
                            var q = cm.getEventManager("JobAdvance4");
                            if (q == null) {
                                cm.sendOk("Unknown error occured");
                            } else {
                                q.startInstance(cm.getParty(), cm.getMap());
                            }
                        } else {
                            cm.sendOk("All players must be in map and above level 100.");
                        }
                    } else {
                        cm.sendOk("You are not the leader of the party, please ask your leader to talk to me.");
                    }
                } else {
                    cm.sendOk("You either haven't formed a party yet or you have already advanced.");
                }
                break;
            case 3:
                if (cm.getParty() != null) {
                    if (cm.getDisconnected("JobAdvance5") != null) {
                        cm.getDisconnected("JobAdvance5").registerPlayer(cm.getPlayer());
                    } else if (cm.isLeader()) {
                        var party = cm.getPlayer().getParty().getMembers();
                        var mapId = cm.getPlayer().getMapId();
                        var next = true;
                        var it = party.iterator();
                        while (it.hasNext()) {
                            var cPlayer = it.next();
                            var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                            if (ccPlayer == null || ccPlayer.getLevel() < 200) {
                                next = false;
                                break;
                            }
                        }	
                        if (next) {
                            var q = cm.getEventManager("JobAdvance5");
                            if (q == null) {
                                cm.sendOk("Unknown error occured");
                            } else {
                                q.startInstance(cm.getParty(), cm.getMap());
                            }
                        } else {
                            cm.sendOk("All players must be in map and above level 200.");
                        }
                    } else {
                        cm.sendOk("You are not the leader of the party, please ask your leader to talk to me.");
                    }
                } else {
                    cm.sendOk("You either haven't formed a party yet or you have already advanced.");
                }
                break;
            case 3:
                cm.sendOk("#bCurrent Points : " + points);
                break;
            case 99:
                cm.warp(100000000);
                break;
        }
    }
    cm.dispose();
}