/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


status = -1;
sel = -1;

function start() {
    cm.sendNext("#eWelcome to Instagram !Here you can to upload photos from the internet and other peoples from the server can see them!\r\n#r(!! Based of XxОsirisxX System !!)\r\n(!! Made by MechAviv(avivii12345) !!)#k");
}

function action(mode, type, selection) {
    status++;
    if (mode < 1) {
        cm.dispose();
        return;
    }
    switch (status) {
    case 0x00:
        cm.sendSimple("What do you want to do?\r\n\r\n#b#L0#Check my wall if I ever have any friend.#l\r\n#L1#Post photo in my wall, so I can brag about myself to others.#l\r\n#L2#Stalk a friend wall.#l");
        break;
    case 0x01:
        sel = selection;
        if (sel == 0) {
            var wall = "#h # wall!\r\n";
            for (var i = 0; i < cm.getPlayer().getInstagram().getMessages().size(); i++) {
                wall += "\r\n----------------------------------------------\r\n";
                wall += "\t\t\t\t\t\t\t\t\t\t\t\t#L-1" + cm.getPlayer().getInstagram().getMessages().get(i).getPostId() + "##i3991023##l\r\n"
                wall += cm.getPlayer().getInstagram().getMessages().get(i).getMessage();
                wall += "\r\n\r\n";
            }
            wall += "\r\n----------------------------------------------\r\n";
            wall += "#b#L0#I wanna go back to main menu!#l";
            cm.sendSimple(wall);
        } else if (sel == 1) {
            cm.sendGetText("Type photo url here");
        } else if (sel == 2) {
            cm.sendGetText("Type the name of the 'friend' (He is not your friend, he lies) you wants to stalk.");
        } else {
            cm.sendNext("Go eat a bacon, bye!");
            cm.dispose();
        }
        break;
    case 0x02:
        if (sel == 0) {
            status = -1;
            if (selection == 0) {
                action(1, -1, -1);
                return;
            } else {
                var number = Math.abs(selection) + "";
                var postid = parseInt(number.substring(1));
                var act = parseInt(number.substring(0, 1));
                switch (act) {
                    case 0x01: //Remove
                        cm.removePhoto(postid);
                        cm.sendNext("Photo removed succesfully.");
                        break;
                    default:
                        cm.sendNext("Bacon.");
                        cm.dispose();
                        break;
                }
            }
        } else if (sel == 1) {
            if(cm.isExists(cm.getText())) {
            cm.postPhoto(cm.getText());
            cm.sendNext("Your photo was uploaded succesfully!");
            status = -1;
            } else {
                cm.sendNext("The photo was not found...");
            }
        } else if (sel == 2) {
            if (cm.checkWall(cm.getText())) {
                var wall = "#h # wall!\r\n";
                for (var i = 0; i < cm.getPlayer().getInstagram().getStalk().getMessages().size(); i++) {
                    wall += "\r\n----------------------------------------------\r\n";
                    wall += cm.getPlayer().getInstagram().getStalk().getMessages().get(i).getMessage();
                    wall += "\r\n\r\n";
                }
                wall += "\r\n----------------------------------------------\r\n";
                wall += "#b#L0#I wanna go back to main menu!#l";
                cm.sendSimple(wall);
            } else {
                cm.sendNext("The username was not found...");
            }
            status = -1;
        }
        break;
    }
}  