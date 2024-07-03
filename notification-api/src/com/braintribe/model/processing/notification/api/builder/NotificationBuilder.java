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

import java.util.Set;

import com.braintribe.model.command.Command;
import com.braintribe.model.notification.CommandNotification;
import com.braintribe.model.notification.MessageNotification;
import com.braintribe.model.notification.MessageWithCommand;
import com.braintribe.model.notification.Notification;

/**
 * Builder API to create a {@link Notification}. 
 */
public interface NotificationBuilder {
	
	/**
	 * Returns a {@link CommandBuilder} that can be used to create a {@link Command} for this {@link Notification}. <br /> 
	 * If called the resulting {@link Notification} will be of type {@link CommandNotification}. 
	 */
	CommandBuilder command();
	/**
	 * Returns a {@link MessageBuilder} that can be used to create a message for this {@link Notification}. <br />
	 * If called the resulting {@link Notification} will be of type {@link MessageNotification}.
	 */
	MessageBuilder message();
	/**
	 * Sets the context of the {@link Notification}. Must be called before close()
	 */
	NotificationBuilder context(Set<String> context);
	NotificationBuilder context(String... context);
	/**
	 * Based on the settings before either a {@link CommandNotification}, a {@link MessageNotification} or a {@link MessageWithCommand} is created.
	 */
	NotificationsBuilder  close();
	
	
}
