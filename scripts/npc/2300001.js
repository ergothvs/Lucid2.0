var status = 0; 

function start() { 
    status = -1; 
    action(1, 0, 0); 
}  


function action(mode, type, selection) { 
    if (mode == 1) {
        status++; 
    }else if (mode == -1) {
        status--; 
    }else { 
        cm.dispose(); 
        return; 
    } 
    if (status == 0) { 
        if (cm.getPlayer().getLevel() >= 180) {
            cm.sendNext("Oh hello there! Have you heard about Hyper skills?\r\n\r\nThey are really usefull and I could max them out for you if you want."); 
        } else { 
            cm.sendOk("Oh Hi. Looks like you aren't level 180 yet. I'm doing research in Hyper Skills. Come back to me if you reach such a highe level as 180 and we could talk."); 
            cm.dispose(); 
        } 
    } else if (status == 1) { 
        cm.sendOk("Allright, here you go!");
	cm.maxSkillsByJob();
	cm.dispose()
	}
}