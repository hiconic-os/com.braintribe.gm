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
package com.braintribe.model.processing.vde.clone.async;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

import com.braintribe.processing.async.api.AsyncCallback;

/**
 * @author peter.gazdik
 */
/* package */ interface WorkerContext {

	int MAX_NUMBER_OF_OPS = 1000;

	void submit(Runnable task);
	int allowedNumberOfOps();
	void notifyNumberOfOps(int numberOfOps);

	default <T> AsyncCallback<T> submittingCallbackOf(Consumer<? super T> onSuccess, Consumer<Throwable> onFailure) {
		return AsyncCallback.of( //
				future -> submit(() -> onSuccess.accept(future)), //
				t -> submit(() -> onFailure.accept(t)));
	}

	default <T> AsyncCallback<T> submittingCallbackOf(AsyncCallback<? super T> callback) {
		return AsyncCallback.of( //
				future -> submit(() -> callback.onSuccess(future)), //
				t -> submit(() -> callback.onFailure(t)));
	}
}

/* package */ interface VdeWorkerContext extends WorkerContext {
	int MAX_NUMBER_OF_OPS = 1000;

	boolean evaluateVds();
	VdeWorkerContext nonVdEvaluatingContext();
}

/* package */ class VdYesWorkerContext<R> implements VdeWorkerContext, AsyncCallback<R> {
	private final AsyncCallback<? super R> callback;
	private final Executor executor;

	private final int maxNumberOfOps;
	// pointer to the last job and also an indicator if there are jobs being processed - if null, no jobs are being processed
	private Job lastJob; 
	private VdeWorkerContext nonVdEvaluatingContext;

	private int currentNumberOfOps;

	public VdYesWorkerContext(AsyncCallback<? super R> callback, Executor executor) {
		this.callback = callback;
		this.executor = executor;
		this.maxNumberOfOps = MAX_NUMBER_OF_OPS;
	}

	@Override
	public boolean evaluateVds() {
		return true;
	}

	@Override
	public int allowedNumberOfOps() {
		return Math.max(maxNumberOfOps - currentNumberOfOps, 0);
	}

	@Override
	public void notifyNumberOfOps(int numberOfOps) {
		currentNumberOfOps += numberOfOps;
	}

	@Override
	public VdeWorkerContext nonVdEvaluatingContext() {
		if (nonVdEvaluatingContext == null)
			nonVdEvaluatingContext = new VdNoWorkerContext(this);
		return nonVdEvaluatingContext;
	}

	@Override
	public void onSuccess(R future) {
		callback.onSuccess(future);
	}

	@Override
	public void onFailure(Throwable t) {
		callback.onFailure(t);
	}

	@Override
	public void submit(Runnable task) {
		Job job = new Job();
		job.task = task;

		boolean isWorking = lastJob != null;
		if (isWorking)
			lastJob.nextJob = job;

		lastJob = job;

		if (!isWorking)
			doAsync(job);
	}

	class Job {
		Runnable task;
		Job nextJob;

		public void run() {
			try {
				task.run();
			} catch (Throwable e) {
				callback.onFailure(e);
				nextJob = null;
			}
		}
	}

	private void doAsync(Job job) {
		executor.execute(() -> work(job));
	}

	private void work(Job job) {
		currentNumberOfOps = 0;
		while (job != null) {
			if (allowedNumberOfOps() == 0) {
				doAsync(job);
				return;
			}

			currentNumberOfOps++;
			job.run();
			job = job.nextJob;
		}
		// mark that no jobs are being processed so the next submit starts another working loop
		lastJob = null;
	}

}

/* package */ class VdNoWorkerContext implements VdeWorkerContext {

	private final VdeWorkerContext delegate;

	public VdNoWorkerContext(VdeWorkerContext delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean evaluateVds() {
		return false;
	}

	@Override
	public VdeWorkerContext nonVdEvaluatingContext() {
		return this;
	}

	@Override
	public void submit(Runnable task) {
		delegate.submit(task);
	}

	@Override
	public int allowedNumberOfOps() {
		return delegate.allowedNumberOfOps();
	}

	@Override
	public void notifyNumberOfOps(int numberOfOps) {
		delegate.notifyNumberOfOps(numberOfOps);
	}
}
