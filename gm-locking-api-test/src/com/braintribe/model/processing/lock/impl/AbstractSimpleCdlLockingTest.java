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
