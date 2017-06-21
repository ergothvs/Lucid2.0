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
/* Fredrick NPC (9030000)
 * By Moogra
 */
importPackage(Packages.server);

var status = 0;
var choice;

function start() {
    cm.sendNext("Hi, I'm the store banker.");
} 

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.dispose();
        return;
    }
    if (status == 1)
        cm.sendSimple("Would you like to withdraw\r\n#b#L0#Mesos#l#L1#Items#l");
    else if (status == 2) {
        cm.sendNext("Let me check if you have any....");
        choice = selection;
    } else {
        if (choice == 0) {
            if (status == 3) {
                var mesoEarnt = cm.getHiredMerchantMesos(false);
                if (mesoEarnt > 0)
                    cm.sendYesNo("You have made " + mesoEarnt + " mesos in your store so far. Would you like to withdraw them?");
                else {
                    cm.sendNext("You have not made any mesos");
                    cm.dispose();
                }
            } else if (status == 4) {
                cm.sendNext("Thank you for using my services, your mesos has been recieved");
                cm.gainMeso(cm.getHiredMerchantMesos(true));
                cm.dispose();
            }
        } else {
            if (status == 3) {
                var items = cm.getHiredMerchantItems();
                if (items.size() > 0) {
                    var text = "Please select an item\r\n";
                    for (var i = 0; i < items.size(); i++)
                        text += "#L"+i+"##i"+items.get(i).getRight().getItemId()+"##l ";
                    cm.sendSimple(text);
                } else {
                    cm.sendNext("You do not have any items from your store");
                    cm.dispose();
                }
            } else if (status == 4) {
                var items = cm.getHiredMerchantItems();
                MapleInventoryManipulator.addFromDrop(cm.getClient(), items.get(selection).getRight());
                cm.sendNext("Thank you for using my services, your item has been recieved");
                cm.removeHiredMerchantItem(items.get(selection).getLeft());
                cm.dispose();
            }
        }
    }
}