package com.braintribe.gm.model.logging.level.api;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.AuthorizedRequest;

@Abstract
public interface LogLevelRequest extends AuthorizedRequest {
	EntityType<LogLevelRequest> T = EntityTypes.T(LogLevelRequest.class);

	String getApplicationId();
	void setApplicationId(String applicationId);

	@Override
	default boolean system() {
		return true;
	}
}
