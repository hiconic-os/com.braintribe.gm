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
package com.braintribe.model.processing.locking.db.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;

import org.junit.Test;

import com.braintribe.common.db.DbVendor;
import com.braintribe.model.processing.locking.db.test.wire.contract.DbLockingTestContract;
import com.braintribe.utils.StringTools;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;

/**
 * Tests for {@link DbLocking}
 * 
 * @see DbLockingTestContract
 */
public class DbLockingTest extends AbstractDbLockingTestBase {

	public DbLockingTest(DbVendor vendor) {
		super(vendor);
	}

	// ###############################################
	// ## . . . . . . . . . Tests . . . . . . . . . ##
	// ###############################################

	@Test(timeout = TIMEOUT_MS)
	public void testLockTryLock() throws Exception {
		Lock lock = createLock();
		lock.lock();
		lock.unlock();

		assertThat(lock.tryLock()).isTrue();
		lock.unlock();
	}

	/* Test a long identifier */
	@Test(timeout = TIMEOUT_MS)
	public void testLockWithLongIdentifier() throws Exception {
		String id = "MoreThan300Chars" + StringTools.repeat("1234567890", 30);
		Lock lock = createLock(id);
		lock.lock();
		lock.unlock();

		assertThat(lock.tryLock()).isTrue();
		lock.unlock();
	}

	/**
	 * Simulate that a lock is stuck because of e.g. a crash; check if tryLock without parameter works
	 */
	@Test(timeout = TIMEOUT_MS)
	public void testLockRecover1() throws Exception {
		Lock lock = createLock();
		lock.lock();

		boolean tryLock = createLock().tryLock();
		assertThat(tryLock).isFalse();
	}

	/**
	 * Simulate that a lock is stuck because of e.g. a crash; there is a lock - check if tryLock with parameter works
	 */
	@Test(timeout = TIMEOUT_MS)
	public void testLockRecover2() throws Exception {
		Lock lock = createLock();
		lock.lock();

		boolean tryLock = createLock().tryLock(1, TimeUnit.SECONDS);
		assertThat(tryLock).isFalse();
	}

	@Test(timeout = 15000, expected = TimeoutException.class)
	public void testLockLock() throws Exception {
		Lock lock = createLock();
		lock.lock();

		// This will time-out, because the lock is already locked
		// And thus also tests the refresher, as the lock expires after 2 seconds
		runInParallelWithTimeout(3, TimeUnit.SECONDS, createLock()::lock);
	}

	// ###############################################
	// ## . . . . . . . . Helpers . . . . . . . . . ##
	// ###############################################

	private void runInParallelWithTimeout(long timeout, TimeUnit unit, Runnable runnable) throws TimeoutException, InterruptedException {
		ExecutorService pool = Executors.newFixedThreadPool(1);

		try {
			TimeLimiter timeLimiter = SimpleTimeLimiter.create(pool);
			timeLimiter.runWithTimeout(runnable, timeout, unit);

		} finally {
			pool.shutdown();
		}
	}

	private Lock createLock() {
		return createLock(LOCK_ID);
	}

	private Lock createLock(String identifier) {
		return locking.forIdentifier(identifier).writeLock();
	}

}
