// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.processing.lock.impl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import com.braintribe.model.processing.lock.api.ReentrableLocking;
import com.braintribe.model.processing.lock.api.ReentrableReadWriteLock;
import com.braintribe.model.processing.lock.api.Locking;
import com.braintribe.utils.lcd.NullSafe;

/**
 * Simple (non-distributed) {@link Locking} implementation based on Java concurrency API
 * 
 * @author peter.gazdik
 */
public class SimpleCdlLocking implements Locking {

	/* This implementation is here as it brings no dependencies and it's convenient for testing code that involves Locking */

	private final Map<String, LockEntry> locks = new ConcurrentHashMap<>();

	@Override
	public ReentrableLocking withReentranceId(String reentranceId) {
		NullSafe.nonNull(reentranceId, "reentranceId");
		if (READ_LOCK_REENTRANCE_ID.equals(reentranceId))
			throw new IllegalArgumentException("reentranceId cannot be " + READ_LOCK_REENTRANCE_ID);

		return new ReentrableLocking() {
			@Override
			public ReentrableReadWriteLock forIdentifier(String id) {
				return forIdentifierAndReentranceId(id, reentranceId);
			}
		};
	}

	@Override
	public ReentrableReadWriteLock forIdentifier(String id) {
		return forIdentifierAndReentranceId(id, UUID.randomUUID().toString());
	}

	private ReentrableReadWriteLock forIdentifierAndReentranceId(String id, String reentranceId) {
		LockEntry lockEntry = locks.computeIfAbsent(id, k -> new LockEntry(id));

		return new ReentrableRwLock(lockEntry, reentranceId);
	}

	class LockEntry {

		public final String lockId;
		public final ReentrableLock readLock;

		private volatile CountDownLatch cdl;
		private String currentReentranceId;
		private int reentranceCount = 0; // how many lock we have locked

		public LockEntry(String id) {
			this.lockId = id;
			this.readLock = new ReentrableLock(this, READ_LOCK_REENTRANCE_ID, ReentrableLock.STATE_READ_LOCK);
		}

		public boolean waitUntilCanLock(ReentrableLock lock, long msLeft, boolean canInterrupt) throws InterruptedException {
			long start = System.currentTimeMillis();

			while (true) {
				if (s_tryLockFor(lock))
					return true;

				msLeft -= System.currentTimeMillis() - start;
				if (msLeft <= 0)
					return false;

				CountDownLatch _cdl = this.cdl;
				if (_cdl == null)
					// someone has unlocked in the meantime, let's try locking again
					continue;

				try {
					boolean isZero = _cdl.await(msLeft, TimeUnit.MILLISECONDS);

					if (!isZero)
						// timeout
						return false;

				} catch (InterruptedException e) {
					if (canInterrupt)
						throw e;
				}
			}
		}

		private synchronized boolean s_tryLockFor(ReentrableLock lock) {
			if (lock.s_state == ReentrableLock.STATE_WRITE_LOCKED)
				return false;

			boolean locked = false;

			if (currentReentranceId == null) {
				currentReentranceId = lock.reentranceId;
				cdl = new CountDownLatch(1);
				locked = true;
			}

			if (currentReentranceId.equals(lock.reentranceId))
				locked = true;

			if (locked) {
				reentranceCount++;

				if (lock.s_state == ReentrableLock.STATE_WRITE_UNLOCKED)
					lock.s_state = ReentrableLock.STATE_WRITE_LOCKED;
			}

			return locked;
		}

		public synchronized void s_unlock(ReentrableLock lock) {
			if (!currentReentranceId.equals(lock.reentranceId))
				throw new IllegalStateException(
						"Cannot unlock a lock as it's not locked. " + lock.lockContext() + ", lockedReentranceId " + currentReentranceId);

			reentranceCount--;
			if (reentranceCount == 0) {
				currentReentranceId = null;
				cdl.countDown();

				if (lock.s_state == ReentrableLock.STATE_WRITE_LOCKED)
					lock.s_state = ReentrableLock.STATE_WRITE_UNLOCKED;
			}
		}
	}

	/**
	 * Two different instances with the same lockId (thus same {@link LockEntry}) and the same reentranceId can both get the lock at the same time.
	 */
	class ReentrableRwLock implements ReentrableReadWriteLock {

		private final ReentrableLock writeLock;

		public ReentrableRwLock(LockEntry lockEntry, String reentranceId) {
			this.writeLock = new ReentrableLock(lockEntry, reentranceId, ReentrableLock.STATE_WRITE_UNLOCKED);
		}

		// @formatter:off
		@Override public String lockId() { return writeLock.lockEntry.lockId; }
		@Override public String reentranceId() { return writeLock.reentranceId; }
		@Override public Lock readLock() { return writeLock.lockEntry.readLock; }
		@Override public Lock writeLock() { return writeLock; }
		// @formatter:on

	}

	class ReentrableLock implements Lock {
		public static final int STATE_READ_LOCK = 0;
		public static final int STATE_WRITE_UNLOCKED = 1;
		public static final int STATE_WRITE_LOCKED = 2;

		public final LockEntry lockEntry;
		public final String reentranceId;

		public int s_state;

		public ReentrableLock(LockEntry lockEntry, String reentranceId, int initialState) {
			this.lockEntry = lockEntry;
			this.reentranceId = reentranceId;
			this.s_state = initialState;
		}

		@Override
		public void lock() {
			try {
				if (!lockEntry.waitUntilCanLock(this, Long.MAX_VALUE, false))
					throw new IllegalStateException("Could not acquire lock " + lockContext());

			} catch (InterruptedException e) {
				throw new IllegalStateException("Interrupted exception was not expected.", e);
			}
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			if (Thread.interrupted())
				throw new InterruptedException();

			if (!lockEntry.waitUntilCanLock(this, Long.MAX_VALUE, true))
				throw new IllegalStateException("Could not acquire lock " + lockContext());
		}

		@Override
		public boolean tryLock() {
			try {
				return lockEntry.waitUntilCanLock(this, 0, false);

			} catch (InterruptedException e) {
				throw new IllegalStateException("Interrupted exception was not expected for lock " + lockContext(), e);
			}
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			return lockEntry.waitUntilCanLock(this, unit.toMillis(time), true);
		}

		@Override
		public void unlock() {
			lockEntry.s_unlock(this);
		}

		@Override
		public Condition newCondition() {
			return null;
		}

		public String lockContext() {
			return "id " + lockEntry.lockId + ", reentranceId " + reentranceId;
		}
	}

}
