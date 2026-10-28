package com.braintribe.gm.initializer.api;

/**
 * The outcome of the last run of an {@link InitializerTask}, as an {@link InitializerRegistry} persisted it.
 *
 * @see InitializerStatus
 */
public enum InitializerState {

	/**
	 * The last run (corresponding to current initializer) succeeded, and its fingerprint is stored.
	 * <p>
	 * The status always reflects the current initializer (based on fingerprint). E.g. if some initializer fails, a FAILED status is stored. If we
	 * then revert the initializer to the previous one, on startup it will not run, but the persisted state must be reverted so SUCCEEDED.
	 */
	SUCCEEDED,

	/** The last run (corresponding to current initializer - @see #SUCCEEDED) failed, either with a reason or with an exception. */
	FAILED,

	/** The task is registered, but there is no record of it, i.e. it never ran. */
	NEVER_RUN,

	/** The task is not registered, so its status is not checked at all, whatever was persisted for that name earlier. */
	NOT_REGISTERED

}
