/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/    
/*
        Author : XxOsirisxX (BubblesDev)
        NPC Name: Kiruru
*/

status = -1;
text = [["Ereve is the stronghold of the Cygnus Knights. It is where the fair and beautiful Empress Cygnus trains the Cygnus Knights. They say that the Empress is physically very weak and must be protected by Shinsoo.", "Aside from Shinsoo, the tactician Neinheart, who assists the Empress, is also there. He is famous for being able to spit out malicious criticism all while maintaining a smile. They say that even the Chief Knights are careful around him, fearing cuts in their budget.", "The Chief Knights also protect the Empress. While the curt Mihile and the cold Eckhart don't really get along, the dense Oz and the righteous Irena are closer than you'd think. Ah, and Hawkeye, well he would get along even with a Horntail.", "Those of us who have blue feathers are of the Piyo race, a race that likes humans. We can only live in high altitudes so we don't usually get the chance to meet humans. But in Ereve, we enjoy befriending humans.", "I guess you already know a lot about Ereve. Have a good trip... ~ Not GMS like"], ["No text", "No text", "No text", "no text", "I guess you already know a lot about Victoria Island. Have a good trip..."]];

function start() {
    if (cm.getPlayer().getMapId() % 2 == 0)
        cm.sendSimple("The weather is so nice. At this rate, we should arrive at Ereve in no time...Do you know much about Ereve? It's a floating island protected by Shinsoo. Would you like to know more about Ereve?#b\r\n#L0#Yes, please tell me.\r\n#L1#No, It's okay.");
    else
        cm.sendSimple("The weather is so nice. At this rate, we'll get to Ellinia in no time...Is this your first time to Victoria Island? If so, would you like me to tell you a little bit about the place?#b\r\n#L0#Yes, please tell me.\r\n#L1#No, it's okay.");
}

function action(mode, type, selection){
    if (mode == 0 && status == 0) { // I don't want to listen to the bs
        cm.sendOk("Well, okay. Don't do anything weird because you're bored.");
    }
    status++;
    if(mode == 0 && type == 0)
        status -= 2;
    else if (mode != 1){
        cm.dispose();
        return;
    }
    if (status == 0){
        if (selection == 1){
            cm.sendNext(text[cm.getPlayer().getMapId() % 2][4]);
            cm.dispose();
            return;
        }
        cm.sendNext(text[cm.getPlayer().getMapId() % 2][status]);
    }else if (status == 1)
        cm.sendNextPrev(text[cm.getPlayer().getMapId() % 2][status]);
    else if (status == 2)
        cm.sendNextPrev(text[cm.getPlayer().getMapId() % 2][status]);
    else if (status == 3)
        cm.sendNextPrev(text[cm.getPlayer().getMapId() % 2][status]);
    else if (status == 4)
        cm.sendPrev("#b(Kiruru continued to talk for a long time...)");
    else if (status == 5)
        cm.dispose();
}