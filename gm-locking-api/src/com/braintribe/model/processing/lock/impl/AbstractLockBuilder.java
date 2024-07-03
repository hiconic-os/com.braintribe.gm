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

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import com.braintribe.model.processing.lock.api.LockBuilder;

public abstract class AbstractLockBuilder implements LockBuilder, ReadWriteLock {
	protected String identifier;
	protected String callerSignature;
	protected String machineSignature;
	protected String holderId = UUID.randomUUID().toString();
	protected long lockTtlMs = -1L;

	protected AbstractLockBuilder(String id) {
		this.identifier = id;
	}

	@Override
	public Lock readLock() {
		return shared();
	}

	@Override
	public Lock writeLock() {
		return exclusive();
	}

	@Override
	public LockBuilder caller(String callerSignatureParam) {
		this.callerSignature = callerSignatureParam;
		return this;
	}

	@Override
	public LockBuilder machine(String machineSignatureParam) {
		this.machineSignature = machineSignatureParam;
		return this;
	}

	@Override
	@Deprecated
	public LockBuilder lockTimeout(long time, TimeUnit unit) {
		this.lockTtlMs = TimeUnit.MILLISECONDS.convert(time, unit);
		return this;
	}

	@Override
	public LockBuilder lockTtl(long time, TimeUnit unit) {
		this.lockTtlMs = TimeUnit.MILLISECONDS.convert(time, unit);
		return this;
	}

	@Override
	public LockBuilder holderId(String holderId) {
		this.holderId = holderId;
		return this;
	}

	public String getMachineSignature() {
		return machineSignature;
	}

	public String getCallerSignature() {
		return callerSignature;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getHolderId() {
		return holderId;
	}

	/**
	 * @deprecated Use {@link #getLockTtlMs()} instead
	 */
	@Deprecated
	public long getLockTimeoutMs() {
		return lockTtlMs;
	}

	public long getLockTtlMs() {
		return lockTtlMs;
	}
}
