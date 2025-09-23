package com.braintribe.model.processing.worker.api;

/**
 * Context passed to the {@link WorkerAspect} in order to call following aspects or the wrapped callable.
 */
public interface WorkerAspectContext {

	/**
	 * Calls the rest of the aspect chain, consisting of the remaining aspects and the wrapped callable.
	 */
	Object proceed() throws Exception;

}
