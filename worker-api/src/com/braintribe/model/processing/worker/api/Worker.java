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
package com.braintribe.model.processing.worker.api;

import java.util.concurrent.Callable;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.value.PreliminaryEntityReference;

/**
 * Worker represents a component than can submit tasks for execution.
 * <p>
 * Its activity is controlled by the framework via the {@link #start(WorkerContext)} and {@link #stop(WorkerContext)} methods, by which it also gets
 * hold of a {@link WorkerContext}. While it is active, it can submit individual tasks via this context, by invoking
 * {@link WorkerContext#submit(Callable)} (or {@link WorkerContext#submit(Runnable)}).
 */
public interface Worker {

	/** Start the work. This method must return immediately (hence, it must not block).	 */
	void start(WorkerContext workerContext) throws WorkerException;

	/**
	 * Instructs the worker to stop the current task. If any threads have been started via the thread context, they would be automatically stopped
	 * later (if they are cancelable, which means that it either repeatedly checks {@link Thread#isInterrupted()} or catching an
	 * {@link InterruptedException} during a blocking operation that supports this exception).
	 */
	void stop(WorkerContext workerContext) throws WorkerException;

	/**
	 * Provides a unique identification object that is used, for example, for leadership management.
	 * 
	 * @return A GenericEntity that identifies this worker.
	 */
	default GenericEntity getWorkerIdentification() {
		if (isSingleton())
			throw new UnsupportedOperationException(
					"'getWorkerIdentification()' must be implemented explicitly for singleton workers! Worker: " + this);

		// TODO this used to be HardwiredWorker, but that is no tf.cortex.
		PreliminaryEntityReference ref = PreliminaryEntityReference.T.create();
		ref.setId(getClass().getName() + "-defaultId");

		return ref;
	}

	/**
	 * Tells the system whether this worker can be instantiated (and thus executed) multiple times for parallel processing and/or clustering.
	 * 
	 * @return <code>true</code> iff this worker can be executed only once.
	 */
	default boolean isSingleton() {
		return false;
	}

}
