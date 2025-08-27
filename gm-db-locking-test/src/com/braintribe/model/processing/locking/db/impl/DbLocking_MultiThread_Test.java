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
		cdl = new CountDownLatch(50);
		var wasConcurrent = Box.of(Boolean.FALSE);

		for (int i = 0; i < 50; i++)
			submit(() -> {
				Lock writeLock = newRandomReentrantLock().writeLock();
				writeLock.lock();

				active++;
				if (active > 1)
					wasConcurrent.value = true;
				cdl.countDown();
				active--;

				writeLock.unlock();
			});

		awaitCdl();

		assertThat(wasConcurrent.value).isFalse();
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
