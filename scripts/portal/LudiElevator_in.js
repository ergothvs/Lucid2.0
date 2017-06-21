/**
 * @author: Eric
 * @script: LudiElevator_in
 * @func: Helios Tower Elevator
*/

function enter(pi) {
	try {
		var elevator = pi.getEventManager("elevator");
		if (elevator == null) {
			pi.getPlayer().dropMessage(5, "The elevator is not available for riding at this time. Please try again later.");
		} else if (elevator.getProperty(pi.getMapId() == 222020100 ? ("goingUp") : ("goingDown")).equals("false")) {
			pi.warp(pi.getMapId() == 222020100 ? 222020110 : 222020210, 0);
			if (pi.getPlayer().getChannelServer().getMapFactory().getMap(pi.getMapId() == 222020100 ? 222020110 : 222020210).getCharacters().size() == 0)
				elevator.getIv().invokeFunction(pi.getMapId() == 222020110 ? "goUp" : "goDown");
		} else if (elevator.getProperty(pi.getMapId() == 222020100 ? ("goingUp") : ("goingDown")).equals("true")) {
			pi.getPlayer().dropMessage(5, "The elevator is not available for riding at this time. Please try again later.");
		}
	} catch(e) {
		pi.getPlayer().dropMessage(5, "Error: " + e);
	}
}