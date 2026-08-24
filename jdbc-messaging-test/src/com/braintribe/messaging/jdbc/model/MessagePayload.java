package com.braintribe.messaging.jdbc.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;

/**
 * @author peter.gazdik
 */
public interface MessagePayload extends GenericEntity {

	EntityType<MessagePayload> T = EntityTypes.T(MessagePayload.class);

	String getText();
	void setText(String text);

	/** Only transferred if the messaging is configured with a resource aware marshaller. */
	Resource getAttachment();
	void setAttachment(Resource attachment);

	static MessagePayload create(String text) {
		MessagePayload payload = T.create();
		payload.setText(text);
		return payload;
	}

}
