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

import static com.braintribe.utils.lcd.CollectionTools2.first;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;
import java.util.function.BiConsumer;

import com.braintribe.processing.async.api.AsyncCallback;

public class AsyncCollector<A> {
	protected final A accumulator;
	private AsyncCallback<A> callback;
	private int outstandingCallbacks;
	private List<Throwable> errors;

	public AsyncCollector(int outstandingConsumptions, A accumulator) {
		this.outstandingCallbacks = outstandingConsumptions;
		this.accumulator = accumulator;
	}

	public static <A> AsyncCollector<A> into(A accumulator, int outstandingConsumptions) {
		return new AsyncCollector<>(outstandingConsumptions, accumulator);
	}

	protected <X> AsyncCallback<X> collect(BiConsumer<A, X> assigner) {
		return new AsyncCallback<X>() {
			@Override
			public void onSuccess(X future) {
				assigner.accept(accumulator, future);
				dec();
			}

			@Override
			public void onFailure(Throwable t) {
				if (errors == null)
					errors = newList();
				errors.add(t);
				dec();
			}
		};
	}

	private void dec() {
		if (--outstandingCallbacks == 0 && callback != null) {
			notifyCallback(callback);
			callback = null;
		}
	}

	public void andThenSubmit(AsyncCallback<? super A> callback, WorkerContext wc) {
		andThen(wc.submittingCallbackOf(callback));
	}
	
	public void andThen(AsyncCallback<? super A> callback) {
		if (outstandingCallbacks == 0)
			notifyCallback(callback);

		if (this.callback == null)
			this.callback = (AsyncCallback<A>) callback;
		else
			this.callback = andThen(this.callback, (AsyncCallback<A>)  callback);
	}

	private void notifyCallback(AsyncCallback<? super A> callback) {
		if (errors == null)
			callback.onSuccess(accumulator);
		else
			callback.onFailure(failure());
	}
	
	// TODO extract to platform-exceptions
	private Throwable failure() {
		if (errors.size() == 1)
			return first(errors);
		
		RuntimeException e = new RuntimeException("Multiple errors occured in: " + getClass().getSimpleName());
		errors.forEach(e::addSuppressed);
		
		return e;
	}

	public static <T> AsyncCallback<T> andThen(AsyncCallback<T> first, AsyncCallback<T> second)  {
		return new AsyncCallback<T>() {
			@Override
			public void onSuccess(T future) {
				first.onSuccess(future);
				second.onSuccess(future);
			}

			@Override
			public void onFailure(Throwable t) {
				first.onFailure(t);
				second.onFailure(t);
			}
		};
	}
	
}
