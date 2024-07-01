// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.notification.api.builder;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.eval.DelegatingEvalContext;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.EvalContextAspect;
import com.braintribe.model.generic.eval.EvalException;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.notification.Notifications;
import com.braintribe.model.notification.Notify;
import com.braintribe.model.processing.notification.api.NotificationAwareEvalContext;
import com.braintribe.model.processing.service.api.ServiceProcessorNotificationException;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.processing.async.api.AsyncCallback;

public class FailureNotificationAwareEvalContext<R> extends DelegatingEvalContext<R> implements NotificationAwareEvalContext<R> {
	private static final Logger logger = Logger.getLogger(FailureNotificationAwareEvalContext.class);

	private Notifications notifications;
	private NotificationAwareEvaluator evaluator = new NotificationAwareEvaluator();
	
	public FailureNotificationAwareEvalContext(EvalContext<R> other) {
		super(other);
	}
	
	@Override
	public Notifications getReceivedNotifications() {
		return notifications;
	}
	
	@Override
	public R get() throws EvalException {
		try {
			return getDelegate().get();
		}
		catch (RuntimeException | Error e) {
			scanAndNotify(e);
			throw e;
		}
	}
	
	@Override
	public void get(AsyncCallback<? super R> callback) {
		getDelegate().get(new AsyncCallback<R>() {

			@Override
			public void onSuccess(R result) {
				callback.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable t) {
				scanAndNotify(t);
				callback.onFailure(t);
			}
		});
	}
	
	private void scanAndNotify(Throwable e) {
		Throwable cause = e.getCause();
		
		if (cause != null) {
			scanAndNotify(cause);
		}
		
		for (Throwable suppressed: e.getSuppressed()) {
			scanAndNotify(suppressed);
		}

		if (e instanceof ServiceProcessorNotificationException) {
			ServiceProcessorNotificationException spne = (ServiceProcessorNotificationException)e;
			ServiceRequest notification = spne.getNotification();
			try {
				notification.eval(evaluator).get();
			} catch (EvalException e1) {
				logger.error("Error while evaluating exception notification: " + notification, e);
			}
		}
	}

	private class NotificationAwareEvaluator implements Evaluator<ServiceRequest> {
		@Override
		public <T> EvalContext<T> eval(ServiceRequest request) {
			return new EvalContext<T>() {
				@Override
				public T get() throws EvalException {
					if (request instanceof Notify) {
						Notify notify = (Notify)request;
						
						if (notifications == null)
							notifications = Notifications.T.create();
						
						notifications.getNotifications().addAll(notify.getNotifications());
					}
					else {
						// noop
					}
					
					return null;
				}

				@Override
				public void get(AsyncCallback<? super T> callback) {
					try {
						callback.onSuccess(get());
					}
					catch (Throwable t) {
						callback.onFailure(t);
					}
				}

				@Override
				public <E, A extends EvalContextAspect<? super E>> EvalContext<T> with(Class<A> aspect, E value) {
					// no attribute support
					return this;
				}
			};
		}
	}
}
