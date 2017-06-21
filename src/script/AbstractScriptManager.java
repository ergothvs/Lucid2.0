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
package script;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import client.MapleClient;

/**
 *
 * @author Matze
 */
public abstract class AbstractScriptManager {

    private static final ScriptEngineManager sem = new ScriptEngineManager();

    protected Invocable getInvocable(String path, MapleClient c) {
        return getInvocable(path, c, false);
    }

    protected Invocable getInvocable(String path, MapleClient c, boolean npc) {
        path = "scripts/" + path;
		ScriptEngine engine = null;

		if (c != null)
		    engine = c.getScriptEngine(path);

		if (engine == null) {
		    File scriptFile = new File(path);
		    if (!scriptFile.exists()) {
		        return null;
		    }
		    engine = sem.getEngineByName("javascript");
		    if (c != null) {
		        c.setScriptEngine(path, engine);
		    }
		    try (Stream<String> stream = Files.lines(scriptFile.toPath())) {
		    	String lines = "load('nashorn:mozilla_compat.js');";
		    	lines += stream.collect(Collectors.joining(System.lineSeparator()));
		    	
		    	engine.eval(lines);
		    	
		    } catch (ScriptException | IOException e) {
		    	return null;
		    }
		    // fr = new FileReader(scriptFile);
		    
		} else if (c != null && npc) {
		    //c.getPlayer().dropMessage(-1, "You already are talking to this NPC. Use @ea if this is not intended.");
		}
		return (Invocable) engine;
    }
}
