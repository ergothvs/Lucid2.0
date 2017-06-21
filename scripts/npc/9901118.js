/* Coded by Alcandon */

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

         
         if (mode == -1) {
        cm.dispose();
    
    } else if (mode == 0){
        cm.sendOk ("#eOkay, please come back when you've collected them.");
        cm.dispose();
    } else{             
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
		cm.sendOk("Under construction.");
		cm.dispose();
        //cm.sendNext ("#eHey #r#h ##k, I forge tickets for those 3 NPCs, NoobStats, MediumStats, HardStats. The requirements are: \r\n1 #i4005004#\r\n1 #i4005002#\r\n1 #i4005003#\r\n1 #i4005000#\r\n1 #i4005001#\r\nAfter you've collected all that, I will give you #i5220010# and Try it out on One of those 3 NPCs.");	
 } else if (status == 1) {
cm.sendSimple("So you got the requirements..? \r\n#L0#Yes, I got them!\r\n#L1#Nope, I'm still missing some.");
} else if (status == 2) {
if (selection == 0) {
if (cm.haveItem(4005004, 1) && cm.haveItem(4005002, 1) && cm.haveItem(4005003, 1) && cm.haveItem(4005000, 1) && cm.haveItem(4005001, 1)) {
cm.gainItem(5220010, 1);
cm.sendOk("Nice! Here's your item! Now, go try out your luck!");
} else {
cm.sendOk("*Sigh*... Don't try to scam me...");
cm.dispose();
}
} else if (selection == 1) {
cm.sendOk("Oh.. Okay.. It's cool.");
cm.dispose();
}
}
}
}