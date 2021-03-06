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
package com.nastel.jkool.tnt4j.format;


import java.util.Map;
import java.util.TimeZone;

import com.nastel.jkool.tnt4j.config.Configurable;
import com.nastel.jkool.tnt4j.core.OpLevel;
import com.nastel.jkool.tnt4j.core.Snapshot;
import com.nastel.jkool.tnt4j.core.UsecTimestamp;
import com.nastel.jkool.tnt4j.source.DefaultSourceFactory;
import com.nastel.jkool.tnt4j.source.Source;
import com.nastel.jkool.tnt4j.tracker.TrackingActivity;
import com.nastel.jkool.tnt4j.tracker.TrackingEvent;
import com.nastel.jkool.tnt4j.utils.Utils;

/**
 * <p>
 * Default implementation of <code>Formatter</code> interface provides
 * default formatting of <code>TrackingActvity</code> and <code>TrackingEvent</code>
 * as well as any object passed to <code>format()</code> method call.
 * </p>
 * 
 * 
 * @version $Revision: 4 $
 * 
 * @see Formatter
 * @see TrackingActivity
 * @see TrackingEvent
 */
public class DefaultFormatter implements EventFormatter, Configurable   {
	public static final String SEPARATOR = System.getProperty("tnt4j.formatter.default.separator", " | ");

	protected String separator = SEPARATOR;
	protected TimeZone timeZone = TimeZone.getTimeZone("UTC");
	protected String formatString = "{2} | {1} | {0} | {3}";

	private Map<String, Object> config = null;
	
	/**
	 * Create a default event formatter
	 *
	 */
	public DefaultFormatter() {
	}
	
	/**
	 * Create a default event formatter
	 *
	 * @param format string (e.g. "{2} | {1} | {0} | {3}")
	 */
	public DefaultFormatter(String format) {
		formatString = format;
	}
	
	/**
	 * Create a default event formatter
	 *
	 * @param format string (e.g. "{2} | {1} | {0} | {3}")
	 * @param tz time zone
	 */
	public DefaultFormatter(String format, TimeZone tz) {
		formatString = format;
		timeZone = tz;
	}
	
	/**
	 * Create a default event formatter
	 *
	 * @param format string (e.g. "{2} | {1} | {0} | {3}")
	 * @param tzid time zone id
	 */
	public DefaultFormatter(String format, String tzid) {
		formatString = format;
		timeZone = TimeZone.getTimeZone(tzid);;
	}
	
    @Override
	public String format(Object obj, Object...args) {
		if (obj instanceof TrackingActivity) {
			return format((TrackingActivity) obj);
		} else if (obj instanceof TrackingEvent) {
			return format((TrackingEvent) obj);
		} else {
			return Utils.format(obj.toString(), args);
		}
	}

	@Override
	public String format(TrackingEvent event) {
		return event.toString();
	}
	
	@Override
	public String format(TrackingActivity activity) {
		return activity.getStatus() + separator + activity + separator + activity.getSource();
	}

	@Override
    public String format(Snapshot snapshot) {
		return format(snapshot.getSeverity(), snapshot.toString());
    }
	
	@Override
    public String format(long ttl, Source src, OpLevel level, String msg, Object...args) {
		String srcName = src != null? src.getFQName(): DefaultSourceFactory.getInstance().getRootSource().getFQName();
		return Utils.format(formatString, UsecTimestamp.getTimeStamp(timeZone), level, Utils.format(msg, args), srcName);
    }
	
	@Override
	public Map<String, Object> getConfiguration() {
		return config;
	}

	@Override
	public void setConfiguration(Map<String, Object> settings) {
		config = settings;
		Object sep = config.get("Separator");
		Object format = config.get("Format");
		Object tz = config.get("TimeZone");
		separator = (sep != null? sep.toString(): SEPARATOR);
		formatString = format != null? format.toString(): formatString;
		timeZone = (tz != null? TimeZone.getTimeZone(tz.toString()): timeZone);
	}	
}
