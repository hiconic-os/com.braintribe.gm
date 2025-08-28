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
package com.braintribe.transport.messaging.api;

import java.util.Comparator;

import com.braintribe.model.messaging.Message;

/**
 * <p>
 * A basic {@link Comparator} for {@link Message}(s).
 * 
 * <p>
 * Allows GM messaging implementations to enable {@link Message} prioritization.
 * 
 * <p>
 * The greater {@link Message#getPriority()} is, higher is the priority.
 * 
 */
public class MessagePriorityComparator implements Comparator<Message> {

	private static final Integer defaultPriority = Integer.valueOf(4);

	@Override
	public int compare(Message message1, Message message2) {
		return priority(message2).compareTo(priority(message1));
	}

	private static Integer priority(Message message) {
		return (message.getPriority() != null) ? message.getPriority() : defaultPriority;
	}

}
