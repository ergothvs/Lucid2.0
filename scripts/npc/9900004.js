
function action(mode, type, selection) {
if(cm.getPlayerStat("GM")==true){
    cm.openShop(9031006);
    cm.dispose();
	}else{
	cm.sendOk("Gm only");
	cm.dispose();
	}
}