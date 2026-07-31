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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.common.db.DbVendor;
import com.braintribe.provider.Box;

/**
 * Tests for {@link DbLocking}
 * 
 * @author peter.gazdik
 */
public class DbLocking_MultiThread_Test extends AbstractDbLockingTestBase {

	public DbLocking_MultiThread_Test(DbVendor vendor) {
		super(vendor);
	}

	private ExecutorService executor;
	private CountDownLatch cdl;

	private volatile int active = 0;

	@Before
	public void prepareExecutor() {
		executor = Executors.newCachedThreadPool();
	}

	@Test(timeout = TIMEOUT_MS)
	public void testWritesAreBlockingEachOther() {
		final int COUNT = 50;

		cdl = new CountDownLatch(COUNT);
		var wasConcurrent = Box.of(Boolean.FALSE);

		for (int i = 0; i < COUNT; i++)
			submit(() -> {
				try {
					Lock writeLock = newRandomReentrantLock().writeLock();
					writeLock.lock();

					active++;
					if (active > 1)
						wasConcurrent.value = true;
					cdl.countDown();
					active--;

					writeLock.unlock();

				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				}
			});

		awaitCdl();

		assertThat(wasConcurrent.value).isFalse();
	}

	/** Per {@link Lock#lock()} contract the method is not interruptible - it must keep waiting and only restore the interrupt status at the end. */
	@Test(timeout = TIMEOUT_MS)
	public void testLockIsNotInterruptible() throws Exception {
		Lock holderLock = newRandomReentrantLock().writeLock();
		holderLock.lock();

		Lock waiterLock = newRandomReentrantLock().writeLock();
		var interruptedAfterLock = new Box<Boolean>();

		Thread waiter = new Thread(() -> {
			waiterLock.lock(); // must survive the interrupt below and keep waiting
			interruptedAfterLock.value = Thread.currentThread().isInterrupted();
			waiterLock.unlock();
		});
		waiter.start();

		// let the waiter block on the lock, then interrupt it
		Thread.sleep(300);
		waiter.interrupt();
		// give the waiter a chance to (wrongly) die on the interrupt before we release the lock
		Thread.sleep(100);

		holderLock.unlock();
		waiter.join();

		assertThat(interruptedAfterLock.value).as("lock() must survive an interrupt, acquire the lock and restore the interrupt status").isTrue();
	}

	// #############################################
	// ## . . . . . . . . Helpers . . . . . . . . ##
	// #############################################

	private void awaitCdl() {
		try {
			cdl.await();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted", e);
		}
	}

	private void submit(Runnable runnable) {
		executor.submit(runnable);
	}

}
