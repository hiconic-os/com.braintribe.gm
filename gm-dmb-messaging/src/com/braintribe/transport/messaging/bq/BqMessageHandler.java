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
package com.braintribe.transport.messaging.bq;

import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Topic;
import com.braintribe.transport.messaging.api.MessagingSession;

/**
 * Common message handler object referencing a {@link MessagingSession} and {@link Destination}.
 */
/* package */ class BqMessageHandler {

	private BqMessagingSession session;
	private Destination destination;
	private char destinationType;
	private String applicationId;
	private String nodeId;

	public BqMessagingSession getSession() {
		return session;
	}

	public void setSession(BqMessagingSession session) {
		this.session = session;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destinationType = getDestinationType(destination);
		this.destination = destination;
	}

	public char getDestinationType() {
		return destinationType;
	}

	public char getDestinationType(Destination destinationInstance) {
		return (destinationInstance instanceof Topic) ? 't' : 'q';
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

}
