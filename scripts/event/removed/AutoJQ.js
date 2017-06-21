var setupTask;
var nextTime;
var maps = Array(100000202, 220000006, 280020000, 922020000, 690000066);

function init() {
    scheduleNew();
}

function scheduleNew() {
	var cal = java.util.Calendar.getInstance();
    	cal.set(java.util.Calendar.HOUR, 0);
    	cal.set(java.util.Calendar.MINUTE, 0); 
    	cal.set(java.util.Calendar.SECOND, 0);
    	nextTime = cal.getTimeInMillis();
        while (nextTime <= java.lang.System.currentTimeMillis()) {
		  // 900000 = 15mins (Will have this for open beta)
		  // 180000000 = 30mins (Will be this way once server is 100% ready)
	      nextTime += 1800 * 1000; 
        }
    setupTask = em.scheduleAtTimestamp("setup", nextTime);
}

function cancelSchedule() {
	if (setupTask != null) {
		setupTask.cancel(true);
	}
}

function setup() {
	scheduleNew();
	em.getChannelServer().blueWorldMessage("[Automatic Jump Quest System] An AutoJQ has opened, type @join to enter. Hurry, you got 1 minute!");
	em.AutoJQ(maps[Math.floor(Math.random() * maps.length)]);
}