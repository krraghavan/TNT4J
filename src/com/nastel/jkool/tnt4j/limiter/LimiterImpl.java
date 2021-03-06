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
package com.nastel.jkool.tnt4j.limiter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.common.util.concurrent.RateLimiter;

/**
 * Default rate limiter implementation (thread safe) based on Google Guava Library
 * {@code https://code.google.com/p/guava-libraries/}
 *
 * @version $Revision: 1 $
 */
public class LimiterImpl implements Limiter {

	boolean doLimit = false;
	long start = System.currentTimeMillis();

	AtomicLong byteCount = new AtomicLong(0);
	AtomicLong msgCount = new AtomicLong(0);
	AtomicLong delayCount = new AtomicLong(0);
	AtomicLong denyCount = new AtomicLong(0);

	AtomicDouble sleepCount = new AtomicDouble(0);
	AtomicDouble lastSleep = new AtomicDouble(0);

	RateLimiter bpsLimiter = null;
	RateLimiter mpsLimiter = null;

	public LimiterImpl(double maxMps, double maxBps, boolean enabled) {
		setLimits(maxMps, maxBps);
		setEnabled(enabled);
	}

	@Override
    public double getMaxMPS() {
	    return (mpsLimiter == null ? 0.0D : mpsLimiter.getRate());
    }

	@Override
    public double getMaxBPS() {
	    return (bpsLimiter == null ? 0.0D : bpsLimiter.getRate());
    }

	@Override
    public Limiter setLimits(double maxMps, double maxBps) {
		if (maxMps > 0.0D) {
			if (mpsLimiter == null)
				mpsLimiter = RateLimiter.create(maxMps);
			else
				mpsLimiter.setRate(maxMps);
		}
		else {
			mpsLimiter = null;
		}

		if (maxBps > 0.0D) {
			if (bpsLimiter == null)
				bpsLimiter = RateLimiter.create(maxBps);
			else
				bpsLimiter.setRate(maxBps);
		}
		else {
			bpsLimiter = null;
		}

		return this;
    }

	@Override
    public double getMPS() {
		return msgCount.get() * 1000.0 / getAge();
    }

	@Override
    public double getBPS() {
		return byteCount.get() * 1000.0 / getAge();
    }

	@Override
    public Limiter setEnabled(boolean flag) {
		doLimit = flag;
		if (doLimit) {
			start = System.currentTimeMillis();
		}
	    return this;
    }

	@Override
    public boolean isEnabled() {
	    return doLimit;
    }

	@Override
    public boolean tryObtain(int msgCount, int byteCount) {
	    return tryObtain(msgCount, byteCount, 0, TimeUnit.SECONDS);
    }

	@Override
    public boolean tryObtain(int msgs, int bytes, long timeout, TimeUnit unit) {
		count(msgs, bytes);
		if (!doLimit || (msgs == 0 && bytes == 0)) {
			return true;
		}

		boolean permit = true;
		if ((bpsLimiter != null) && (bytes > 0)) {
			permit = bpsLimiter.tryAcquire(bytes, timeout, unit);
		}
		if ((mpsLimiter != null) && (msgs > 0)) {
			permit = permit && mpsLimiter.tryAcquire(msgs, timeout, unit);
		}
		if (!permit) {
			denyCount.incrementAndGet();
		}
		return permit;
	}

	@Override
    public double obtain(int msgs, int bytes) {
		count(msgs, bytes);
		if (!doLimit || ( msgs == 0 && bytes == 0)) {
			return 0;
		}

		double elapsedSecByBps = 0;
		double elapsedSecByMps = 0;

		int delayCounter = 0;
		if (bpsLimiter != null) {
			elapsedSecByBps = bpsLimiter.acquire(bytes);
			if (elapsedSecByBps > 0) delayCounter++;
		}
		if (mpsLimiter != null) {
			elapsedSecByMps = mpsLimiter.acquire(msgs);
			if (elapsedSecByMps > 0) delayCounter++;
		}
		double sleepTime = elapsedSecByBps + elapsedSecByMps;
		if (sleepTime > 0) {
			lastSleep.set(sleepTime);
			sleepCount.addAndGet(sleepTime);
			delayCount.addAndGet(delayCounter);
		}
	    return sleepTime;
	}

	protected void count(int msgs, int bytes) {
		if (bytes > 0) {
			byteCount.addAndGet(bytes);
		}
		if (msgs > 0) {
			msgCount.addAndGet(msgs);
		}
	}

	@Override
    public Limiter reset() {
		byteCount.set(0);
		msgCount.set(0);
		sleepCount.set(0);
		delayCount.set(0);
		start = System.currentTimeMillis();
		return this;
	}

	@Override
    public long getStartTime() {
	    return start;
    }

	@Override
	public long getAge() {
		return Math.max(System.currentTimeMillis() - start, 1);
	}

	@Override
    public long getTotalBytes() {
	    return byteCount.get();
    }

	@Override
    public long getTotalMsgs() {
	    return msgCount.get();
    }

	@Override
    public double getLastDelayTime() {
	    return lastSleep.get();
    }

	@Override
    public double getTotalDelayTime() {
	    return sleepCount.get();
    }

	@Override
    public long getDelayCount() {
	    return delayCount.get();
    }

	@Override
    public long getDenyCount() {
	    return denyCount.get();
    }
}
