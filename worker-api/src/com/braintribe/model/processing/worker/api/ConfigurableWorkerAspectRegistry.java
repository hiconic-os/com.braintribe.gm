package com.braintribe.model.processing.worker.api;

/**
 * {@link WorkerAspectRegistry} with additional methods for registering and ordering {@link WorkerAspect}s.
 */
public interface ConfigurableWorkerAspectRegistry extends WorkerAspectRegistry {

	void register(String identifier, WorkerAspect aspect);

	/**
	 * Specifies the order of registered {@link WorkerAspect}s.
	 * <p>
	 * If this method is called multiple times, only the last order is taken into account.
	 */
	void order(String... identifiers);

}
