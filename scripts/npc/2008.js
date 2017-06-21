 var status;    

function start() {
    status = -1; 
    action(1, 0, 0); 
} 
var status; 

function start() { 
    status = -1; 
    action(1, 0, 0); 
} 

function action(mode, type, selection) { 
    if (mode == 1) { 
        status++; 
    }else{ 
        status--; 
    } 
     
    if (status == 0) {
        cm.sendNext("Hi welcome to Lucid. Have fun telling you own story!");
    } else if (status == 1) {
        cm.sendOk("You are now at Maple Island. Here you can calmly discover the basics of the game and train until you are at least level 10.\r\n\r\nOn the far right is a harbour, you can leave the island there when you are level 10.\r\n\r\nMaple Island also exists to evaluate your job decision. You may at the end of the island pick the job you prefer. You will get a starter pack and will be sent of to start your adventure!\r\n\r\nYou may also use @help! for a few commands! @home is useful, but you need to be level 50 in order to use it. Have fun!~~");
    } else {
			cm.dispose();
		}
    }




/* var status;    

function start() {
    status = -1; 
    action(1, 0, 0); 
} 
var status; 

function start() { // starts the NPC 
    status = -1; // sets the status of the NPC to -1 
    action(1, 0, 0); // sets the mode to 1, type to 0, and selection to 0 for the NPC 
} // closes off the start function 

function action(mode, type, selection) { // calls what you set above in function start, almost all actions are done here 
    if (mode == 1) { // the mode is set to 1 because of the function start, as shown above 
        status++; // advances the NPC to the next status, in this case, status 0 
    }else{ // if mode does not equal 1 
        status--; // does not advance the NPC to the next status. 
    } 
     
    if (status == 0) {
        cm.sendNext("Hello #e#h ##n! I am Here for testing purposes. Use me for things..");
    }else if (status == 1) {
        cm.sendSimple("#L0#Equip Kaiser Lv.10 Secondary Weapon#l\r\n#L1#Max JobSkills#l");
    }else if (status == 2) {
        if (selection == 1) {
            cm.maxSkillsByJob();
            cm.dispose();
        }
		else if (selection == 0) {
            cm.equipSecondaryByID(1352500);
			cm.reloadChar();
            cm.sendOk("I have equiped your secondary weapon!");
            cm.dispose();
        }
		else {
			cm.dispose();
		}
    }
}*/