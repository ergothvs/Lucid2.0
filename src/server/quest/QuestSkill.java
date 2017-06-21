/*  
  This file is part of Mushy.
  Copyright (c) 2015 ~ 2016 Maxcloud <no-email@provided.com>
  
  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 2 of the
  License, or (at your option) any later version.  See the file
  COPYING included with this distribution for more information.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.quest;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Maxcloud
 */
class QuestSkill {

	private int skillid;
	private int skillLevel;
	private int masterLevel;
	
	private List<Short> jobs = new ArrayList<>();
	
	protected QuestSkill(int skillid, int skillLevel, int masterLevel, List<Short> jobs) {
		this.skillid = skillid;
		this.skillLevel = skillLevel;
		this.masterLevel = masterLevel;
		this.jobs = jobs;
	}
	
	public int getSkillId() {
		return skillid;
	}
	
	public int getSkillLevel() {
		return skillLevel;
	}
	
	public int getMasterLevel() {
		return masterLevel;
	}
	
	public List<Short> getJobs() {
		return jobs;
	}
}
