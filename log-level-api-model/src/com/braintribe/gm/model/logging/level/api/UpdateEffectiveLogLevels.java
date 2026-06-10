package com.braintribe.gm.model.logging.level.api;

import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.Neutral;

public interface UpdateEffectiveLogLevels extends LogLevelRequest {
	
	EntityType<UpdateEffectiveLogLevels> T = EntityTypes.T(UpdateEffectiveLogLevels.class);
	
	EvalContext<Neutral> eval(Evaluator<ServiceRequest> evaluator);
}
