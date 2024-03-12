package com.braintribe.model.processing.lock.api;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * A non-reentrant {@link ReadWriteLock} with a re-entry possible with another instance (as for writes, reads are re-entrant of course).
 * <p>
 * This lock has a special value - {@link #reentranceId()}, which can be used to create another lock with the same lockId. Such locks are not blocking
 * each other.
 * 
 * @see Locking
 * 
 * @author peter.gazdik
 */
public interface ReentrableReadWriteLock extends ReadWriteLock {

	String lockId();

	/** See {@link Locking#withReentranceId(String)}. */
	String reentranceId();

}
