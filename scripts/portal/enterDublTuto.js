


/*
 * 히나온라인 소스 스크립트 입니다.
 * Translated / Recoded by JakeK from AthenaMS .
 */


function enter(pi) {
    if (pi.getQuestStatus(2605) >= 1 && pi.getQuestStatus(2609) <= 1) {
	pi.warp(103050500, 0);
	pi.playPortalSE();
	return true;
    } else {
	pi.getPlayer().dropMessage(5, "You can't enter since door is locked.");
	return false;
    }
    
}
