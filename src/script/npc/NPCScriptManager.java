/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package script.npc;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import client.MapleClient;
import script.AbstractScriptManager;
import server.quest.MapleQuest;
import tools.packet.CWvsContext;

public class NPCScriptManager extends AbstractScriptManager {

    private final Map<MapleClient, NPCConversationManager> cms = new WeakHashMap<>();
    private static final NPCScriptManager instance = new NPCScriptManager();

    public static final NPCScriptManager getInstance() {
        return instance;
    }

    public final boolean hasScript(final MapleClient c, final int npc, String script) {
        Invocable iv = getInvocable("npc/" + npc + ".js", c, true);
        if (script != null && !script.isEmpty()) {
            iv = getInvocable("npc/" + script + ".js", c, true);
        }
        return iv != null;
    }

    public final void start(final MapleClient c, final int npc, String script) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
        	
            if (!cms.containsKey(c) && c.canClickNPC()) {
                Invocable iv = getInvocable("npc/" + npc + ".js", c, true); //safe disposal
                if (script != null) {
                    iv = getInvocable("npc/" + script + ".js", c, true); //safe disposal
                }
                
                if (iv == null) {
                    iv = getInvocable("npc/notcoded.js", c, true); //safe disposal
                    if (iv == null) {
                        dispose(c);
                        return;
                    }
                }
                
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, -1, script, (byte) -1, iv);
                cms.put(c, cm);
                scriptengine.put("cm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                
                // TODO: Remove all start function(s) from the scripts.
                try {
                    iv.invokeFunction("start");
                } catch (NoSuchMethodException nsme) {
                	// nsme.printStackTrace();
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            System.err.println("Error executing NPC script, NPC ID : " + npc + "." + e);
            e.printStackTrace();
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void action(final MapleClient c, final byte mode, final byte type, final int selection) {
        if (mode != -1) {
            final NPCConversationManager cm = cms.get(c);
            if (cm == null || cm.getLastMsg() > -1) {
                return;
            }
            final Lock lock = c.getNPCLock();
            lock.lock();
            try {
                if (cm.pendingDisposal) {
                    dispose(c);
                } else {
                    c.setClickedNPC();
                    cm.getIv().invokeFunction("action", mode, type, selection);
                }
            } catch (final ScriptException | NoSuchMethodException e) {
                System.err.println("Error executing NPC script. NPC ID : " + cm.getNpc() + ":" + e);
                dispose(c);
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public final void startQuest(final MapleClient c, final int npcid, final int quest) {
        if (!MapleQuest.getInstance(quest).canStart(c.getPlayer(), 0)) {
        	// System.out.printf("[Quest]: The system tried to start '%s' with the npc '%s' %n", quest, npc);
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npcid, quest, null, (byte) 0, iv);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                iv.invokeFunction("start", (byte) 1, (byte) 0, 0); // start it off as something
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npcid + ":" + e);
            e.printStackTrace();
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void startQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("start", mode, type, selection);
            }
        } catch (ScriptException | NoSuchMethodException e) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            e.printStackTrace();
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final int npcid, final int questid, final boolean customEnd) {
        if (!customEnd && !MapleQuest.getInstance(questid).canComplete(c.getPlayer(), npcid)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + questid + ".js", c, true);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npcid, questid, null, (byte) 1, iv);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                iv.invokeFunction("end", (byte) 1, (byte) 0, 0); // start it off as something
            }
        } catch (ScriptException | NoSuchMethodException e) {
            System.err.println("Error executing Quest script. (" + questid + ")..NPCID: " + npcid + ":" + e);
            e.printStackTrace();
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("end", mode, type, selection);
            }
        } catch (ScriptException | NoSuchMethodException e) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            e.printStackTrace();
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void startItemScript(final MapleClient c, final int npc, final String script) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("item/" + script + ".js", c, true);
                if (iv == null) {
                    System.out.println("New scripted item : " + script);
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, -1, script, (byte) -1, iv);
                cms.put(c, cm);
                scriptengine.put("im", cm);
                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                iv.invokeFunction("use");
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            System.err.println("Error executing Item script, SCRIPT : " + script + ". " + e);
            e.printStackTrace();
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void dispose(final MapleClient c) {
        final NPCConversationManager npccm = cms.get(c);
        if (npccm != null) {
            cms.remove(c);
            if (npccm.getType() == -1) {
                c.removeScriptEngine("scripts/npc/" + npccm.getNpc() + ".js");
                c.removeScriptEngine("scripts/npc/" + npccm.getScript() + ".js");
                c.removeScriptEngine("scripts/npc/notcoded.js");
            } else {
                c.removeScriptEngine("scripts/quest/" + npccm.getQuest() + ".js");
            }
        }
        if (c.getPlayer() != null && c.getPlayer().getConversation() == 1) {
            c.getPlayer().setConversation(0);
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public final NPCConversationManager getCM(final MapleClient c) {
        return cms.get(c);
    }
}
