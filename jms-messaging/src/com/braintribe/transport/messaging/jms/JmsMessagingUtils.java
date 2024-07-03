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
package com.braintribe.transport.messaging.jms;

import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

import com.braintribe.logging.Logger;
import com.braintribe.transport.messaging.api.MessagingException;

public class JmsMessagingUtils {

	public static boolean compareJmsDestination(Destination left, Destination right) throws MessagingException {
		String leftName = getJmsDestinationName(left);
		String rightName = getJmsDestinationName(right);
		return leftName.equalsIgnoreCase(rightName);
	}

	public static boolean compareDestination(com.braintribe.model.messaging.Destination left, com.braintribe.model.messaging.Destination right) {
		String leftName = left.getName();
		String rightName = right.getName();
		return leftName.equalsIgnoreCase(rightName);
	}
	public static String getJmsDestinationName(Destination dest) throws MessagingException {
		if (dest == null) {
			return null;
		}
		try {
			String destinationName = null;
			if (dest instanceof Queue) {
				destinationName = ((Queue) dest).getQueueName();
			} else if (dest instanceof Topic) {
				destinationName = ((Topic) dest).getTopicName();
			} else {
				destinationName = dest.toString();
			}
			return destinationName;
		} catch(Exception e) {
			throw new MessagingException("Could not get name of destination "+dest, e);
		}
	}
	
	public static Destination getJmsDestination(
			Map<com.braintribe.model.messaging.Destination,javax.jms.Destination> destinations,
			com.braintribe.model.messaging.Destination needle) {
		for (Map.Entry<com.braintribe.model.messaging.Destination,javax.jms.Destination> entry : destinations.entrySet()) {
			com.braintribe.model.messaging.Destination destination = entry.getKey();
			if (compareDestination(destination, needle)) {
				return entry.getValue();
			}
		}
		return null;
	}

	public static void logError(Logger logger, Throwable ext, String text) {
		logger.error(text, ext);
		if ((ext != null) && (ext instanceof JMSException)) {
			logger.error("Linked Exception:", ((JMSException) ext).getLinkedException());
		}
	}
}
