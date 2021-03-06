/*
 * Copyright 2014-2015 JKOOL, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nastel.jkool.tnt4j.dump;

import java.util.Map.Entry;

import com.nastel.jkool.tnt4j.tracker.TimeStats;
import com.nastel.jkool.tnt4j.tracker.TimeTracker;

/**
 * This class implements a dump handler for {@link TimeTracker}.
 * It dumps the contents of a timing table. The timings maintain the number of nanoseconds
 * since last hit on a given key.
 *
 * @version $Revision: 1$
 */
public class TimeTrackerDumpProvider extends DefaultDumpProvider{
	private TimeTracker timeTracker;
	
	public TimeTrackerDumpProvider(String name, TimeTracker tTracker) {
	    super(name, "HitTimings");
	    this.timeTracker = tTracker;
    }

	@Override
    public DumpCollection getDump() {
		Dump dump = new Dump("TimerTable", this);	
		for (Entry<String, TimeStats> entry: timeTracker.getTimeStats().entrySet()) {
			dump.add(entry.getKey() + "/hits", entry.getValue().getHitCount());
			dump.add(entry.getKey() + "/age.nano", entry.getValue().getAgeNanos());
		}
	    return dump;
    }
}
