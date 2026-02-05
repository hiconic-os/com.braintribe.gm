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
package com.braintribe.model.processing.mqrpc.client;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.messaging.Destination;
import com.braintribe.model.processing.rpc.commons.api.config.GmRpcClientConfig;
import com.braintribe.model.service.api.InstanceId;
import com.braintribe.transport.messaging.api.MessagingSessionProvider;

/**
 * Basic {@link GmRpcClientConfig} for MQ RPC.
 * 
 */
public class BasicGmMqRpcClientConfig extends GmRpcClientConfig {

	private MessagingSessionProvider messagingSessionProvider;
	private String requestDestinationName;
	private EntityType<? extends Destination> requestDestinationType;
	private boolean ignoreResponses;
	private String responseTopicName;
	private InstanceId producerId;
	private long responseTimeout = 10000L;
	private int retries = 3;

	@Required
	@Configurable
	public void setMessagingSessionProvider(MessagingSessionProvider messagingSessionProvider) {
		this.messagingSessionProvider = messagingSessionProvider;
	}

	@Required
	@Configurable
	public void setRequestDestinationName(String requestDestinationName) {
		this.requestDestinationName = requestDestinationName;
	}

	@Required
	@Configurable
	public void setRequestDestinationType(EntityType<? extends Destination> requestDestinationType) {
		this.requestDestinationType = requestDestinationType;
	}

	@Configurable
	public void setIgnoreResponses(boolean ignoreResponses) {
		this.ignoreResponses = ignoreResponses;
	}

	@Configurable
	public void setResponseTopicName(String responseTopicName) {
		this.responseTopicName = responseTopicName;
	}

	@Configurable
	public void setResponseTimeout(long responseTimeout) {
		this.responseTimeout = responseTimeout;
	}

	@Configurable
	public void setRetries(int retries) {
		this.retries = retries;
	}

	public MessagingSessionProvider getMessagingSessionProvider() {
		return messagingSessionProvider;
	}

	public String getRequestDestinationName() {
		return requestDestinationName;
	}

	public EntityType<? extends Destination> getRequestDestinationType() {
		return requestDestinationType;
	}

	public boolean isIgnoreResponses() {
		return ignoreResponses;
	}

	public String getResponseTopicName() {
		return responseTopicName;
	}

	public InstanceId getProducerId() {
		return producerId;
	}

	public long getResponseTimeout() {
		return responseTimeout;
	}

	public int getRetries() {
		return retries;
	}

}
