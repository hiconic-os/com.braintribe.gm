// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.lock.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.provider.Box;

/**
 * Tests for {@link SimpleCdlLocking}
 * 
 * @author peter.gazdik
 */
public class SimpleCdlLocking_MultiThread_Test extends AbstractSimpleCdlLockingTest {

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
		Box<Boolean> wasConcurrent = Box.of(Boolean.FALSE);

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
