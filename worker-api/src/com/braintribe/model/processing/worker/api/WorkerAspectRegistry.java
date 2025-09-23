package com.braintribe.model.processing.worker.api;

import java.util.Map;

/**
 * Provides access to registered {@link WorkerAspect}s.
 */
public interface WorkerAspectRegistry {

	/**
	 * @return Map of [identifier -> aspect]. Order of iteration is the same as order of execution, determined by insertion order and
	 *         {@link ConfigurableWorkerAspectRegistry#order(String...)}
	 */
	Map<String, WorkerAspect> aspects();

}
