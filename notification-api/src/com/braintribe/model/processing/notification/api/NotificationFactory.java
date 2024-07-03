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
package com.braintribe.model.processing.notification.api;

import java.util.List;

import com.braintribe.model.command.Command;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.notification.InternalCommand;
import com.braintribe.model.notification.Level;
import com.braintribe.model.notification.MessageNotification;
import com.braintribe.model.notification.MessageWithCommand;
import com.braintribe.model.notification.Notification;
import com.braintribe.model.notification.NotificationEventSource;
import com.braintribe.model.processing.session.api.notifying.GenericManipulationListenerRegistry;

public interface NotificationFactory {

	/** create a new instance of an event source */
	public <NES extends NotificationEventSource> NES createEventSource(EntityType<NES> entityType);

	/** create a new instance of a command */
	public <C extends Command> C createCommand(EntityType<C> entityType);

	/** create a new notification */
	public <N extends Notification> N createNotification(EntityType<N> entityType);

	/** create a messge notification */
	public <MN extends MessageNotification> MN createNotification(EntityType<MN> entityType, Level level, String message);

	/** create a message with a command */
	public <C extends Command> MessageWithCommand createNotification(EntityType<C> entityType, Level level, String message, String name);

	/** broadcast the notification with given event source */
	public void broadcast(List<Notification> notifications, NotificationEventSource eventSource);

	/** return the manipilation registry */
	public GenericManipulationListenerRegistry listeners();

	/** return the transient object from a command */
	public Object getTransientObject(InternalCommand command);

	/** return the transient command from an object */
	public InternalCommand createTransientCommand(String name, Object object);
}
