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
import static org.assertj.core.api.Assertions.fail;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.common.db.DbVendor;
import com.braintribe.model.processing.lock.api.ReentrableReadWriteLock;
import com.braintribe.model.processing.locking.db.test.wire.contract.DbLockingTestContract;

/**
 * Tests for {@link DbLocking}
 * 
 * @see DbLockingTestContract
 */
public class DbLocking_SingleThread_Test extends AbstractDbLockingTestBase {

	public DbLocking_SingleThread_Test(DbVendor vendor) {
		super(vendor);
	}

	// ###############################################
	// ## . . . . . . . . . Tests . . . . . . . . . ##
	// ###############################################

	private ReentrableReadWriteLock rwLock;
	private Lock readLock;
	private Lock writeLock;

	@Before
	public void prepareLocks() {
		rwLock = newReentrantLock();
		readLock = rwLock.readLock();
		writeLock = rwLock.writeLock();
	}

	@Test(timeout = TIMEOUT_MS)
	public void smokeTest() throws InterruptedException {
		assertThat(readLock).isNotNull();
		assertThat(writeLock).isNotNull();
		assertThat(rwLock.reentranceId()).isEqualTo(REENTRANCE_ID);

		readLock.lock();
		readLock.unlock();
		writeLock.lock();
		writeLock.unlock();

		assertThat(readLock.tryLock()).isTrue();
		readLock.unlock();
		assertThat(writeLock.tryLock()).isTrue();
		writeLock.unlock();

		assertThat(readLock.tryLock(1, TimeUnit.SECONDS)).isTrue();
		readLock.unlock();
		assertThat(writeLock.tryLock(1, TimeUnit.SECONDS)).isTrue();
		writeLock.unlock();
	}

	@Test(timeout = TIMEOUT_MS)
	public void writeIsNotReentrant() throws InterruptedException {
		writeLock.lock();
		assertThat(writeLock.tryLock(1, TimeUnit.SECONDS)).isFalse();
		writeLock.unlock();

		// can lock again
		assertCanLock(writeLock);
	}

	@Test(timeout = TIMEOUT_MS)
	public void independentLocksDontBlock() {
		Lock otherWriteLock = locking.forIdentifier("OtherId").writeLock();

		otherWriteLock.lock();
		assertCanLock(writeLock);
		otherWriteLock.unlock();
	}

	@Test(timeout = TIMEOUT_MS)
	public void anotherWriteIsReentrant() {
		writeLock.lock();
		assertCanLock(newReentrantLock().writeLock());
		writeLock.unlock();
	}

	@Test(timeout = TIMEOUT_MS)
	public void readIstReentrant() {
		readLock.lock();
		assertCanLock(readLock);
		readLock.unlock();
	}

	@Test(timeout = TIMEOUT_MS)
	public void reentersManyLocks() {
		ReentrableReadWriteLock rw1 = newReentrantLock();
		ReentrableReadWriteLock rw2 = newReentrantLock();
		ReentrableReadWriteLock rw3 = newReentrantLock();

		testLocks(readLock, rw1.readLock(), rw2.readLock(), rw3.readLock());
		testLocks(writeLock, rw1.writeLock(), rw2.writeLock(), rw3.writeLock());
	}

	private void testLocks(Lock l1, Lock l2, Lock l3, Lock l4) {
		l1.lock();
		l2.lock();
		l3.lock();
		l4.lock();

		l1.unlock();
		l2.unlock();
		l3.unlock();
		l4.unlock();
	}

	@Test(timeout = TIMEOUT_MS)
	public void writeBlocksRead() {
		writeLock.lock();
		assertCannotLock(readLock);
		writeLock.unlock();
	}

	@Test(timeout = TIMEOUT_MS)
	public void readBlocksWrite() {
		readLock.lock();
		assertCannotLock(writeLock);
		readLock.unlock();

		readLock.lock();
		readLock.lock();
		readLock.unlock();
		assertCannotLock(writeLock);
		readLock.unlock();

		assertCanLock(writeLock);
	}

	private void assertCanLock(Lock lock) {
		try {
			if (!lock.tryLock())
				fail("Write-Lock copy should be re-entrant");

		} finally {
			lock.unlock();
		}
	}

	private void assertCannotLock(Lock lock) {
		if (lock.tryLock()) {
			lock.unlock();
			fail("Write-Lock copy should NOT be re-entrant");
		}
	}

}
