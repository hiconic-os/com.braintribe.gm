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
package com.braintribe.model.processing.lock.api;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author peter.gazdik
 */
@SuppressWarnings("deprecation")
public class LockManagerBasedOnLocking implements LockManager {

	private final Locking locking;
	private final String description;

	public LockManagerBasedOnLocking(Locking locking, String description) {
		this.locking = locking;
		this.description = description;
	}

	@Override
	public LockBuilder forIdentifier(String id) {
		ReadWriteLock rwLock = locking.forIdentifier(id);

		return new LockBuilder() {
			// @formatter:off
			@Override public Lock shared()    { return rwLock.readLock(); }
			@Override public Lock exclusive() { return rwLock.writeLock(); }

			@Override public LockBuilder machine(String machineSignature)      { return this; }
			@Override public LockBuilder lockTtl(long time, TimeUnit unit)     { return this; }
			@Override public LockBuilder lockTimeout(long time, TimeUnit unit) { return this; }
			@Override public LockBuilder holderId(String holderId)             { return this; }
			@Override public LockBuilder caller(String callerSignature)        { return this; }		
			// @formatter:on
		};
	}

	@Override
	public String description() {
		return description;
	}

}
