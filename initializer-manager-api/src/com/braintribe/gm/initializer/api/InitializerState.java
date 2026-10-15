package com.braintribe.gm.initializer.api;

/**
 * The outcome of the last run of an {@link InitializerTask}, as an {@link InitializerRegistry} persisted it.
 *
 * @see InitializerStatus
 */
public enum InitializerState {

	/** The last run succeeded, and its fingerprint is stored. */
	SUCCEEDED,

	/** The last run failed, either with a reason or with an exception. The stored fingerprint, if any, is the one of the last successful run. */
	FAILED,

	/** The task is registered, but there is no record of it, i.e. it never ran. */
	NEVER_RUN,

	/** The task is not registered, so its status is not checked at all, whatever was persisted for that name earlier. */
	NOT_REGISTERED

}
