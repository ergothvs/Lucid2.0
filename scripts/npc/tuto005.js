var chat = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 /*End Chat*/ || mode == 0 && chat == 0 /*Due to no chat -1*/) {
        cm.dispose();
        return;
    }
    mode == 1 ? chat++ : chat--;
    if (chat == 0)
        cm.sendNextS("Who are you? Are you lost?", 3);
    else if (chat == 1)
        cm.sendNextPrevS("I have sought after you for some time, and now you stand before me. The warrior with the destiny of light.", 1, 0, 1106001);
    else if (chat == 2)
        cm.sendNextPrevS("What are you talking about?", 3);
    else if (chat == 3)
        cm.sendNextPrevS("Mind your manners, peon! This is the Empress!", 1, 0, 1106000);
    else if (chat == 4)
        cm.sendNextPrevS("You're the guy from before! What's going on here? That guy you talked about, Chromile... I found a letter from him in the attic. Is that Limbert's real name or something?", 3);
    else if (chat == 5)
        cm.sendNextPrevS("Do you know the name Chromile? The Knight of Light?", 1, 0, 1106000);
    else if (chat == 6)
        cm.sendNextPrevS("Chromile and Mr. Limbert have no connection, save you. Chromile... is your father.", 1, 0, 1106001);
    else if (chat == 7)
        cm.sendNextPrevS("My father left me here when I was little. He abandoned me to this old chicken-keeper.", 3);
    else if (chat == 8)
        cm.sendNextPrevS("He did not abandon you. Your father left you here after your mother passed away to save your life. His path was not one you could follow...", 1, 0, 1106001);
    else if (chat == 9)
        cm.sendNextPrevS("Save me? He didn't save me. He left me to be a slave in this shack. He didn't even give me a name! And now I find out I've been here waiting for a father that'll never return...", 3);
    else if (chat == 10)
        cm.sendNextPrevS("Only the darkest night can produce a brilliant sunrise. Put aside your anger and come with me. You will find the light you seek.", 1, 0, 1106001);
    else if (chat == 11)
        cm.sendNextPrevS("Empress, I do not have faith in this boy. We know nothing about him. I don't think he is fit to be the knight of light.", 1, 0, 1106000);
    else if (chat == 12)
        cm.sendNextPrevS("Dear Neinheart, I should have known better than to assume you would trust in faith. Go ahead and test him, but be gentle.", 1, 0, 1106001);
    else if (chat == 13)
        cm.sendNextPrevS("Wait, what?", 3);
    else if (chat == 14) {
        cm.introEnableUI(0);
        cm.introDisableUI(false);
        cm.forceCompleteQuest(20034);
        cm.forceStartQuest(20035);
        cm.mihileAssailantSummon();
        cm.dispose();
    }
}