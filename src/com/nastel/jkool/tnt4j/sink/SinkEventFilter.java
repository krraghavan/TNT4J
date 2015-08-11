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
package com.nastel.jkool.tnt4j.sink;

import com.nastel.jkool.tnt4j.core.OpLevel;
import com.nastel.jkool.tnt4j.core.Snapshot;
import com.nastel.jkool.tnt4j.core.TTL;
import com.nastel.jkool.tnt4j.source.Source;
import com.nastel.jkool.tnt4j.tracker.TrackingActivity;
import com.nastel.jkool.tnt4j.tracker.TrackingEvent;

/**
 * <p>
 * A simple event filter interface. Implementations of this interface are used with <code>EventSink</code> to filter out
 * logging events.
 * </p>
 * 
 * @see EventSink
 * @see OpLevel
 * @see TrackingEvent
 * @see TrackingActivity
 * 
 * @version $Revision: 2 $
 * 
 */
public interface SinkEventFilter {
	/**
	 * Returns true if a given logging event passes the filter, false otherwise
	 * 
	 * @param sink
	 *            event sink where filter request is coming from
	 * @param event
	 *            tracking event
	 * @return true if event passed all filters, false otherwise
	 * @see OpLevel
	 * @see EventSink
	 */
	boolean filter(EventSink sink, TrackingEvent event);

	/**
	 * Returns true if a given logging event passes the filter, false otherwise
	 * 
	 * @param sink
	 *            event sink where filter request is coming from
	 * @param activity
	 *            tracking activity
	 * @return true if event passed all filters, false otherwise
	 * @see EventSink
	 * @see TrackingActivity
	 */
	boolean filter(EventSink sink, TrackingActivity activity);

	/**
	 * Returns true if a given logging event passes the filter, false otherwise
	 * 
	 * @param sink
	 *            event sink where filter request is coming from
	 * @param snapshot
	 *            a set of properties
	 * @return true if event passed all filters, false otherwise
	 * @see EventSink
	 */
	boolean filter(EventSink sink, Snapshot snapshot);

	/**
	 * Returns true if a given logging event passes the filter, false otherwise
	 * 
	 * @param sink
	 *            event sink where filter request is coming from
	 * @param ttl
	 *            time to live in seconds {@link TTL}
	 * @param source
	 *            event message source
	 * @param level
	 *            severity level
	 * @param msg
	 *            event message
	 * @param args
	 *            arguments passed along side event message
	 * @return true if event passed all filters, false otherwise
	 * @see OpLevel
	 * @see EventSink
	 */
	boolean filter(EventSink sink, long ttl, Source source, OpLevel level, String msg, Object... args);
}
