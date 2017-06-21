


/*
 * 히나온라인 소스 스크립트 입니다.
 * Translated / Recoded by JakeK from AthenaMS .
 */

importPackage(java.lang)

function enter(pi) {
    if (pi.haveItem(4031013, 20)) {
        pi.getPlayer().setKeyValue("2ndJobTrialComplete", "1");
        pi.removeAll(4031013);
        pi.warp(100000000);
        var startTime = pi.getPlayer().getKeyValue("2ndTrialStartTime");
        pi.getPlayer().setKeyValue2("2ndJobTrialCompleteTime2", ((System.currentTimeMillis() / 1000) - startTime)+"");
        return true;
    } else {
        pi.getPlayer().message(5, "You have not collected all the Black Marbles yet !");
        return false;
    }
}
