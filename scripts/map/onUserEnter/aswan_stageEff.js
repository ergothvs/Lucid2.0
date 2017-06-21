/* 
 * Aswan PQ Stage Effects. I decided to handle the effects at portal load rather than map load, but to remove the bat spam of this error this script will re-enable your actions.
*/
importPackages(Packages.tools.packet);

function start(ms) {
	ms.getPlayer().getClient().getSession().write(CWvsContext.enableActions());
}