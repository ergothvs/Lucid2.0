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

import server.RandomRewards;
import tools.Randomizer;

/**
 * 
 * @author Maxcloud
 */
class QuestItem {

	private int itemid; 
	private int count; 
	private int period; 
	private int gender; 
	private int job; 
	private int jobEx; 
	private int prop;

    public QuestItem(int itemid, int count, int period, int gender, int job, int jobEx, int prop) {
        if (RandomRewards.getTenPercent().contains(itemid)) {
            count += Randomizer.nextInt(3);
        }
        this.itemid = itemid;
        this.count = count;
        this.period = period;
        this.gender = gender;
        this.job = job;
        this.jobEx = jobEx;
        this.prop = prop;
    }
    
    public int getItemId() {
    	return itemid;
    }
    
    public int getCount() {
    	return count;
    }
    
    public int getPeriod() {
    	return period;
    }
    
    public int getGender() {
    	return gender;
    }
    
    public int getJob() {
    	return job;
    }
    
    public int getJobEx() {
    	return jobEx;
    }
    
    public int getProp() {
    	return prop;
    }
}
