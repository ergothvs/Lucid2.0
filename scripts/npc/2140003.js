/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/* Ria
	lolcastle NPC
*/

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
/*		if (cm.getChar().getMapId() != 101000000) {
			cm.dispose();
			return;
		}*/
		if (mode == 0) {
			cm.sendOk("Alright, see you next time.");
			cm.dispose();
			return;
		}
		status++;
		if (status == 0) {
			cm.sendNext("I am some random NPC Simon hasn't properly decided on yet. I can send you to the #rTime Temple PQ#k.");
		} else if (status == 1) {
			cm.sendYesNo("Do you wish to enter the #rTime Temple PQ#k now?");
		} else if (status == 2) {
			var em = cm.getEventManager("TTPQ");
			if (em == null || !em.getProperty("TTPQOpen").equals("true")) {
				cm.sendOk("Sorry, but #rTime Temple PQ#k is currently unavailable.");
			}
	//		} else if (cm.getChar().getLevel() >= 21 && cm.getChar().getLevel() < 31) {
		//		cm.gainMeso(-1000000);
		        else
		        {
                                em.startInstance(cm.getPlayer(), cm.getPlayer().getMap()); // hack, remove faek mapid once i fix in sauce
			//	em.getInstance("OmegaPQ1").registerPlayer(cm.getChar());
		       }
			cm.dispose();
		}
	}
}
