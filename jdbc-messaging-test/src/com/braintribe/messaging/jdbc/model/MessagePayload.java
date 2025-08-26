package com.braintribe.messaging.jdbc.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author peter.gazdik
 */
public interface MessagePayload extends GenericEntity {

	EntityType<MessagePayload> T = EntityTypes.T(MessagePayload.class);

	String getText();
	void setText(String text);

	static MessagePayload create(String text) {
		MessagePayload payload = T.create();
		payload.setText(text);
		return payload;
	}

}
