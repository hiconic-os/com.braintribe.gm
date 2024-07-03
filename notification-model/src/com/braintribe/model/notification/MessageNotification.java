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
package com.braintribe.model.notification;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * TODO
 * 
 */

public interface MessageNotification extends Notification {

	EntityType<MessageNotification> T = EntityTypes.T(MessageNotification.class);

	String getMessage();
	void setMessage(String message);

	String getDetails();
	void setDetails(String details);

	Level getLevel();
	void setLevel(Level level);

	boolean getConfirmationRequired();
	void setConfirmationRequired(boolean confirmationRequired);

	boolean getManualClose();
	void setManualClose(boolean manualClose);

	boolean getTextBold();
	void setTextBold(boolean useBold);

	boolean getTextItalic();
	void setTextItalic(boolean useItalic);

	boolean getTextStrikeout();
	void setTextStrikeout(boolean useStrikeout);

	boolean getTextUnderline();
	void setTextUnderline(boolean useUnderline);

	static MessageNotification create(String message) {
		MessageNotification result = MessageNotification.T.create();
		result.setMessage(message);
		return result;
	}

	static MessageNotification create(String message, String details) {
		MessageNotification result = create(message);
		result.setDetails(details);
		return result;
	}

}