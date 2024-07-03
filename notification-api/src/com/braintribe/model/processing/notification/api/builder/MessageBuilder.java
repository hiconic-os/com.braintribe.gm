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
package com.braintribe.model.processing.notification.api.builder;

import com.braintribe.model.notification.Level;
import com.braintribe.model.notification.MessageNotification;
import com.braintribe.model.notification.Notification;

/**
 * Builder API to fluently create a {@link MessageNotification}   
 */
public interface MessageBuilder {
	
	/**
	 * Returns a PatternBuilder which allows formatting the message with a variable context.
	 */
	PatternFormatter pattern(String messagePattern);
	
	/**
	 * Sets the message of the {@link Notification}. 
	 */
	MessageBuilder message(String message);
	/**
	 * Sets the details of the {@link Notification}.
	 */
	MessageBuilder details(String details);
	/**
	 * Sets the details of the {@link Notification} based on the given Throwable.
	 */
	MessageBuilder details(Throwable t);
	
	/**
	 * Sets the level of the {@link MessageNotification}
	 */
	MessageBuilder level(Level level);

	/**
	 * Indicate that the {@link MessageNotification} is of type {@link Level#INFO}
	 */
	MessageBuilder info();
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#INFO} with a message. 
	 */
	NotificationBuilder info(String message);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#INFO} with a message and with details. 
	 */
	NotificationBuilder info(String message, Throwable t);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#INFO} with a message and with details based on given Throwable. 
	 */
	NotificationBuilder info(String message, String details);

	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#INFO} with a message and confirmation required.
	 */
	NotificationBuilder confirmInfo(String message);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#INFO} with a message and with details and confirmation required.
	 */
	NotificationBuilder confirmInfo(String message, Throwable t);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#INFO} with a message and with details based on given Throwable and confirmation required.
	 */
	NotificationBuilder confirmInfo(String message, String details);

	/**
	 * Indicate that the {@link MessageNotification} is of type {@link Level#WARNING}
	 */
	MessageBuilder warn();
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#WARNING} with a message. 
	 */
	NotificationBuilder warn(String message);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#WARNING} with a message and with details. 
	 */
	NotificationBuilder warn(String message, Throwable t);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#WARNING} with a message and with details based on given Throwable. 
	 */
	NotificationBuilder warn(String message, String details);

	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#WARNING} with a message and confirmation required.
	 */
	NotificationBuilder confirmWarn(String message);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#WARNING} with a message and with details and confirmation required.
	 */
	NotificationBuilder confirmWarn(String message, Throwable t);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#WARNING} with a message and with details based on given Throwable and confirmation required.
	 */
	NotificationBuilder confirmWarn(String message, String details);

	/**
	 * Indicate that the {@link MessageNotification} is of type {@link Level#ERROR}
	 */
	MessageBuilder error();
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#ERROR} with a message. 
	 */
	NotificationBuilder error(String message);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#ERROR} with a message and with details. 
	 */
	NotificationBuilder error(String message, Throwable t);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#ERROR} with a message and with details based on given Throwable. 
	 */
	NotificationBuilder error(String message, String details);

	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#ERROR} with a message and confirmation required. 
	 */
	NotificationBuilder confirmError(String message);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#ERROR} with a message and with details and confirmation required. 
	 */
	NotificationBuilder confirmError(String message, Throwable t);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#ERROR} with a message and with details based on given Throwable and confirmation required.
	 */
	NotificationBuilder confirmError(String message, String details);
	
	/**
	 * Indicate that the {@link MessageNotification} is of type {@link Level#SUCCESS}
	 */
	MessageBuilder success();
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#SUCCESS} with a message. 
	 */
	NotificationBuilder success(String message);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#SUCCESS} with a message and with details. 
	 */
	NotificationBuilder success(String message, Throwable t);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#SUCCESS} with a message and with details based on given Throwable. 
	 */
	NotificationBuilder success(String message, String details);

	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#SUCCESS} with a message and confirmation required.
	 */
	NotificationBuilder confirmSuccess(String message);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#SUCCESS} with a message and with details and confirmation required.
	 */
	NotificationBuilder confirmSuccess(String message, Throwable t);
	/**
	 * Convenient way to directly build a {@link MessageNotification} of type {@link Level#SUCCESS} with a message and with details based on given Throwable and confirmation required.
	 */
	NotificationBuilder confirmSuccess(String message, String details);


	/**
	 * Indicates that a user confirmation is required for this message.
	 */
	MessageBuilder confirmationRequired();
	
	/**
	 * Builds the {@link MessageNotification} based on previous settings. 
	 */
	NotificationBuilder close();
	
}
