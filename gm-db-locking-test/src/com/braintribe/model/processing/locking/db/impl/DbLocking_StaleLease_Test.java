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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.ResultSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

import org.junit.Test;

import com.braintribe.common.db.DbVendor;
import com.braintribe.provider.Box;
import com.braintribe.util.jdbc.JdbcTools;

/**
 * Tests for {@link DbLocking} behavior when a lock's lease is lost, i.e. its row is deleted (and possibly re-created) by another node after the lease
 * expired, while the original holder still believes it holds the lock.
 * <p>
 * The lease loss is simulated by deleting the lock row directly via SQL - exactly what another node's {@code deleteLockIfExpired} does.
 *
 * @author peter.gazdik
 */
public class DbLocking_StaleLease_Test extends AbstractDbLockingTestBase {

	public DbLocking_StaleLease_Test(DbVendor vendor) {
		super(vendor);
	}

	/**
	 * Scenario: two threads share the same lock instance (normal j.u.c. usage - lock handle stored in a field).
	 * <ol>
	 * <li>Thread R1 acquires the read lock (incarnation 1 of the lock row).
	 * <li>The lease is lost: another node deletes the expired row.
	 * <li>The main thread acquires the read lock via the same instance (incarnation 2) and is now the legitimate holder.
	 * <li>R1 unlocks. Its acquisition belongs to the deleted incarnation 1, so this must NOT release (delete from DB) the main thread's lock.
	 * </ol>
	 * With the shared mutable {@code rwLock.created} field, R1's unlock uses incarnation 2's {@code created} timestamp and deletes the main thread's
	 * row - a writer can then acquire the lock while the main thread is still inside its read-locked section.
	 */
	@Test(timeout = TIMEOUT_MS)
	public void staleUnlockMustNotReleaseNewIncarnation() throws Exception {
		Lock readLock = newReentrantLock().readLock();

		CountDownLatch r1Acquired = new CountDownLatch(1);
		CountDownLatch r1MayUnlock = new CountDownLatch(1);
		CountDownLatch r1Unlocked = new CountDownLatch(1);

		Thread r1 = new Thread(() -> {
			readLock.lock();
			r1Acquired.countDown();
			try {
				r1MayUnlock.await();
				readLock.unlock();
			} catch (Exception e) {
				// A stale unlock is allowed to complain, it just must not release the other holder's lock
			} finally {
				r1Unlocked.countDown();
			}
		});
		r1.start();
		r1Acquired.await();

		simulateLeaseLoss();

		// make sure incarnation 2 gets a different 'created' timestamp
		Thread.sleep(50);

		// main thread becomes the legitimate holder, via the same lock instance
		assertThat(readLock.tryLock()).isTrue();

		// Original bug: R1 would unlock and delete the row, thus the test later for lockRowCount would fail, as the number would be 0
		r1MayUnlock.countDown();
		r1Unlocked.await();
		r1.join();

		assertThat(lockRowCount()).as("Stale unlock released the new legitimate holder's lock").isEqualTo(1);

		// ... and mutual exclusion must still hold: a writer must not be able to acquire
		Lock writeLock = locking.forIdentifier(LOCK_ID).writeLock();
		assertThat(writeLock.tryLock()).as("Writer acquired the lock while a reader still holds it").isFalse();

		readLock.unlock();
	}

	/**
	 * If a thread re-acquires a lock it already holds, but the underlying row belongs to a different incarnation (lease was lost and the row deleted
	 * or re-created in the meantime), the implementation must not silently acquire a fresh lock - nested unlocks would then over-release it. It must
	 * throw instead, as mutual exclusion was already violated and it cannot safely continue.
	 */
	@Test(timeout = TIMEOUT_MS)
	public void reacquireAfterLeaseLossThrows() throws Exception {
		Lock readLock = newReentrantLock().readLock();

		readLock.lock();

		simulateLeaseLoss();

		// make sure a possible new incarnation would get a different 'created' timestamp
		Thread.sleep(50);

		assertThatThrownBy(readLock::tryLock, "Re-entering a lock whose lease was lost must fail loudly, not silently acquire a fresh lock") //
				.isInstanceOf(IllegalStateException.class);
	}

	/** Analogous to {@link java.util.concurrent.locks.ReentrantReadWriteLock}: unlock by a thread that doesn't hold the lock is a caller bug. */
	@Test(timeout = TIMEOUT_MS)
	public void unlockWithoutHoldingThrowsIllegalMonitorState() {
		Lock readLock = newReentrantLock().readLock();

		assertThatThrownBy(readLock::unlock, "unlock() by a thread that does not hold the lock must throw") //
				.isInstanceOf(IllegalMonitorStateException.class);
	}

	// #############################################
	// ## . . . . . . . . Helpers . . . . . . . . ##
	// #############################################

	/** Deletes the lock row, exactly like another node's {@code deleteLockIfExpired} would after the lease expired. */
	private void simulateLeaseLoss() {
		JdbcTools.withStatement(dataSource, () -> "Simulating lease expiration + cleanup by another node", s -> {
			s.executeUpdate("delete from " + DbLocking.DB_TABLE_NAME);
		});
	}

	private int lockRowCount() {
		Box<Integer> count = Box.of(0);
		JdbcTools.withStatement(dataSource, () -> "Counting lock rows", s -> {
			try (ResultSet rs = s.executeQuery("select count(*) from " + DbLocking.DB_TABLE_NAME + " where id = '" + LOCK_ID + "'")) {
				rs.next();
				count.value = rs.getInt(1);
			}
		});
		return count.value;
	}

}
