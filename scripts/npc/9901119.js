var map = 551030200;
var minLvl = 100;
var maxLvl = 200;
var minAmt = 3;
var maxAmt = 6;

function start() {
    if (cm.getParty() == null) {
        cm.sendOk("If you want to Targar/Scar, #bthe leader of your party must talk to me#k. Level range 120 ~ 200, 3+ person party.");
        cm.dispose();
    } else if (!cm.isLeader()) {
        cm.sendOk("If you want to try the quest, please tell the #bleader of your party#k to talk to me.");
        cm.dispose();
    }else{
        var party = cm.getParty().getMembers();
        var inMap = cm.partyMembersInMap();
        var lvlOk = 0;
        for (var i = 0; i < party.size(); i++) {
        if (party.get(i).getLevel() >= minLvl && party.get(i).getLevel() <= maxLvl)
            lvlOk++;
        }
        if (inMap < minAmt || inMap > maxAmt) {
            cm.sendOk("You don't have enough people in your party. You need a party of #b"+minAmt+"#k - #r"+maxAmt+"#k members and they must be in the map with you. There are #b"+inMap+"#k members here.");
            cm.dispose();
        } else if (lvlOk != inMap) {
            cm.sendOk("Someone in your party isn't the proper level. Everyone needs to be Lvl. #b"+minLvl+"#k - #r"+maxLvl+"#k.");
            cm.dispose();
        }else{
            cm.warpParty(map);
            cm.dispose();
        }
    }
}  