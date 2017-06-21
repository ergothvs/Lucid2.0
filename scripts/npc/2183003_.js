/*
 *	阿斯旺 - 阿斯旺解放戰
 */

var status = -1;
var minLevel = 40;
var maxCount = 5;
var minPartySize = 1;
var maxPartySize = 4;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            cm.sendSimple("#e<阿斯旺解放戰>#n\r\n你願意去消滅依然徘徊在阿斯旺地區的希拉的餘黨嗎？#b\r\n\r\n\r\n#L1#消滅希拉的餘黨。(40級以上。剩餘入場次數: 次)#l\r\n#L0#直接迎戰希拉(120級以上)#l");
        } else if (status == 1) {
            if (selection == 0) {
                if (cm.getPlayer().getLevel() >= 120) {
                    cm.sendNext("現在你將到達希拉之塔入口，請務必消滅希拉吧。");
                } else {
                    cm.sendOk("以你現在的實力，對戰希拉有些勉強。必須達到120級以上才能進行挑戰。");
                    cm.dispose();
                }
            } else {
                if (cm.getPlayer().getParty() == null) {
                    cm.sendOk("必須組隊入場。");
                } else if (!cm.isLeader()) {
                    cm.sendOk("你不是隊長啊？讓隊長來和我說話。");
                } else {
                    var party = cm.getPlayer().getParty().getMembers();
                    var mapId = cm.getPlayer().getMapId();
                    var next = 0;
                    var levelValid = 0;
                    var inMap = 0;
                    var it = party.iterator();
                    while (it.hasNext()) {
                        var cPlayer = it.next();
                        var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                        if (ccPlayer == null) {
                            next = 1;
                        }
                        if (ccPlayer != null && ccPlayer.getLevel() >= minLevel) {
                            levelValid += 1;
                        } else {
                            next = 2;
                        }
                        if (ccPlayer != null && ccPlayer.getMapId() == mapId) {
                            inMap += 1;
                        }
                        //if (ccPlayer != null && ccPlayer.getBossLog("阿斯旺") >= maxCount) {
                          //  next = 4;
                        //}
                    }
                    if (party.size() > maxPartySize || inMap < minPartySize) {
                        next = 3;
                    }
                    if (next == 1) {
                        cm.sendOk("隊伍中有玩家不在此地圖。");
                    } else if (next == 2) {
                        cm.sendOk("隊伍中有玩家的等級不符合。必須 " + minLevel + "級以上的隊員，才能進去。");
                    } else if (next == 3) {
                        cm.sendOk("隊員不夠" + minPartySize + "人。至少必須有" + minPartySize + "個" + minLevel + "級以上的隊員，才能進去。");
                    } else if (next == 4) {
                        cm.sendOk("隊伍中有玩家的入場次數已經用完。");
                    } else if (next == 0) {
					cm.getPlayer().dropMessage(6, "test");
                        var em = cm.getEventManager("Aswan");
                        if (em == null) {
                            cm.sendOk("當前服務器未開啟此功能，請稍後在試...");
                        } else {
                            var prop = em.getProperty("state");
                            if (prop.equals("0") || prop == null) {
                       //         cm.setPartyBossLog("阿斯旺");
                                em.startInstance(cm.getPlayer().getParty(), cm.getPlayer().getMap(), 200);
                            } else {
                                cm.sendOk("當前頻道已有玩家在進行任務中，請稍後在試。");
                            }
                        }
                    } else {
                        cm.sendOk("隊員不夠" + minPartySize + "人。這裡非常危險。至少必須有" + minPartySize + "個" + minLevel + "級以上的隊員，才能進去。");
                    }
                }
                cm.dispose();
            }
        } else if (status == 2) {
            cm.warp(262030000, 0); //希拉之塔 - 希拉之塔入口
            cm.dispose();
        }
    }
}