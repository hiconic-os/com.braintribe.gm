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
package com.braintribe.model.processing.lock.impl;

import org.junit.Before;

import com.braintribe.model.processing.lock.api.ReentrableReadWriteLock;

/**
 * @author peter.gazdik
 */
public class AbstractSimpleCdlLockingTest {

	protected static final int TIMEOUT_MS = 5000;

	protected static final String REENTRANCE_ID = "reentrance-id";
	protected static final String LOCK_ID = "test-use-case";

	protected SimpleCdlLocking locking;

	@Before
	public void prepareLocking() {
		locking = new SimpleCdlLocking();
	}

	protected ReentrableReadWriteLock newReentrantLock() {
		return locking.withReentranceId(REENTRANCE_ID).forIdentifier(LOCK_ID);
	}

	protected ReentrableReadWriteLock newRandomReentrantLock() {
		return locking.forIdentifier(LOCK_ID);
	}
}
