package com.braintribe.model.processing.worker.api;

import java.util.concurrent.Callable;

/**
 * Aspect around a {@link Callable} that was submitted by a {@link Worker}.
 * <p>
 * These aspects are configured initially via {@link ConfigurableWorkerAspectRegistry}, and each {@link Callable} submitted by a {@link Worker} is wrapped. 
 * 
 * @author peter.gazdik
 */
public interface WorkerAspect {

	Object run(WorkerAspectContext context) throws Exception;

}

