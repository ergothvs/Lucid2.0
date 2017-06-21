var autoUnstuck;

function init() {
    scheduleNew();
}

function scheduleNew() {
    var cal = java.util.Calendar.getInstance();
    cal.set(java.util.Calendar.HOUR, 0);
    cal.set(java.util.Calendar.MINUTE, 10);
    cal.set(java.util.Calendar.SECOND, 0);
    var nextTime = cal.getTimeInMillis();
    while (nextTime <= java.lang.System.currentTimeMillis()) {
        nextTime += 1000 * 620;
    }
    autoUnstuck = em.scheduleAtTimestamp("start", nextTime);
}

function cancelSchedule() {
    autoUnstuck.cancel(true);
}

function start() {
    scheduleNew();
    em.AutoUnstucker();
}  