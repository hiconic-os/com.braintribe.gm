package com.braintribe.gm.model.logging.level.api;

import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

public interface GetLogLevelState extends LogLevelRequest {
	EntityType<GetLogLevelState> T = EntityTypes.T(GetLogLevelState.class);

	EvalContext<LogLevelState> eval(Evaluator<ServiceRequest> evaluator);
}
