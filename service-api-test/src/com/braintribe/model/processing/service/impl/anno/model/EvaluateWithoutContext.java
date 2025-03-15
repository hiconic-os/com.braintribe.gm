package com.braintribe.model.processing.service.impl.anno.model;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface EvaluateWithoutContext extends AnnoTestRequest {

	EntityType<EvaluateWithoutContext> T = EntityTypes.T(EvaluateWithoutContext.class);

}