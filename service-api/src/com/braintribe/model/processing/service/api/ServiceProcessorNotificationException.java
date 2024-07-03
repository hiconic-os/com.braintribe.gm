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
package com.braintribe.model.processing.service.api;

import com.braintribe.model.service.api.ServiceRequest;

public class ServiceProcessorNotificationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private ServiceRequest notification;

	
	public ServiceProcessorNotificationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServiceProcessorNotificationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceProcessorNotificationException(String message) {
		super(message);
	}

	public ServiceProcessorNotificationException(Throwable cause) {
		super(cause);
	}

	public ServiceProcessorNotificationException() {
		super();
	}

	public ServiceProcessorNotificationException(ServiceRequest notification) {
		super();
		this.notification = notification;
	}

	public ServiceRequest getNotification() {
		return notification;
	}

	public void setNotification(ServiceRequest notification) {
		this.notification = notification;
	}

}
