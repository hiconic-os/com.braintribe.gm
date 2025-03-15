package com.braintribe.model.processing.service.impl.anno.model;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface EvaluateWithContext extends AnnoTestRequest {

	EntityType<EvaluateWithContext> T = EntityTypes.T(EvaluateWithContext.class);

}