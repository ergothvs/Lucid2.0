package lib.data;
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


import java.io.File;

import lib.xml.XMLWZFile;

public class MapleDataProviderFactory {
    
    public static MapleDataProvider getDataProvider(String file) {
        return getWZ(new File(System.getProperty("wzpath"), file), false);
    }
    
    private static MapleDataProvider getWZ(Object in, boolean provideImages) {
    	if(in instanceof File) {
			File file = ((File) in);
			if (file.isDirectory()) {
				try {
					return new XMLWZFile((File) in);
				} catch(NullPointerException npe) {
					throw new IllegalArgumentException("The file '"+in+"' was not found!");
				}
			}
		}
		throw new IllegalArgumentException("Can't create data provider!");
    }

}
