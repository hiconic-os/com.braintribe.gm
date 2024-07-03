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
package com.braintribe.model.processing.notification.test;

import org.junit.Test;

import com.braintribe.model.processing.notification.api.NotificationAwareEvalContext;
import com.braintribe.model.processing.notification.api.builder.Notifications;
import com.braintribe.model.processing.notification.test.mock.FaultyServiceEvaluator;
import com.braintribe.model.service.api.CompositeRequest;
import com.braintribe.model.service.api.result.CompositeResponse;
import com.braintribe.processing.async.api.AsyncCallback;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

public class FailureNotificationTest {

	@Test
	public void testSynchronousFailureNotification() throws Exception {
		FaultyServiceEvaluator evaluator = new FaultyServiceEvaluator();
		
		CompositeRequest request = CompositeRequest.T.create();

		NotificationAwareEvalContext<? extends CompositeResponse> evalContext = Notifications.makeNotificationAware(request.eval(evaluator));
		
		try {
			evalContext.get();
			Assertions.fail("missing expected exception");
		}
		catch (Exception e) {
			// noop -> it is expected to be faulty
			com.braintribe.model.notification.Notifications receivedNotifications = evalContext.getReceivedNotifications();
			
			Assertions.assertThat(receivedNotifications).isNotNull();
			Assertions.assertThat(receivedNotifications.getNotifications().size()).isEqualTo(2);
		}
	}
	
	@Test
	public void testAsynchronousFailureNotification() throws Exception {
		FaultyServiceEvaluator evaluator = new FaultyServiceEvaluator();
		
		CompositeRequest request = CompositeRequest.T.create();
		
		NotificationAwareEvalContext<? extends CompositeResponse> evalContext = Notifications.makeNotificationAware(request.eval(evaluator));
		
		evalContext.get(new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object future) {
				Assertions.fail("missing expected exception");
			}
			
			@Override
			public void onFailure(Throwable t) {
				// noop -> it is expected to be faulty
				com.braintribe.model.notification.Notifications receivedNotifications = evalContext.getReceivedNotifications();
				
				Assertions.assertThat(receivedNotifications).isNotNull();
				Assertions.assertThat(receivedNotifications.getNotifications().size()).isEqualTo(2);
			}
		});
	}
}
