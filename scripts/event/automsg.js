/**
 * @author: Eric
 * @rev: 1.1 - Lithium Support for Maple Ascension
 * @desc: Auto-Tip for v1.17.2
*/

var setupTask;
var serverName = "Development"

function init() {
    scheduleNew();
}

function scheduleNew() {
    var cal = java.util.Calendar.getInstance();
    cal.set(java.util.Calendar.HOUR, 0);
    cal.set(java.util.Calendar.MINUTE, 0);
    cal.set(java.util.Calendar.SECOND, 0);
    var nextTime = cal.getTimeInMillis();
    while (nextTime <= java.lang.System.currentTimeMillis())
        nextTime += 900 * 1000; //420 * 1000 = 7minutes
    setupTask = em.scheduleAtTimestamp("start", nextTime);
}

function cancelSchedule() {
    setupTask.cancel(true);
}

function start() {
    scheduleNew();
    var Message = new Array("Welcome to " + serverName + ", we hope you enjoy your stay!", "Did you know our rates are 5x EXP 3x MESO 2x DROP?", "Did you know that " + serverName + " supports Windows 8 (Pro, not 8.1)? Well, we do!", "Are you l33t? Speak 1337? Try using the command @leet!", "To view the list of commands, type @commands!", "Fight your friends in " + serverName + "'s very own Player vs. Player! Type @pvp", "Voting for us every 6 hours guarantee's you some currency and NX!", "Don't use any .WZ edits other than the ones we provide you with.", "Don't forget to use @save to avoid rollback because we can't provoke it!", "Do not use any Hacking Program, or else!", "Packet Editors get pwnt, don't even try.", "Using a custom client will result in a ban, use ours.", "Our client currently has support of: UFJ, Tubi, No Breath, No Damage Cap, and Droppable NX!", "Donations are acceptable via Paypal and Donors will receive special prizes in-game!");
    em.getChannelServer().yellowWorldMessage("[" + serverName + "] " + Message[Math.floor(Math.random() * Message.length)]);
}
