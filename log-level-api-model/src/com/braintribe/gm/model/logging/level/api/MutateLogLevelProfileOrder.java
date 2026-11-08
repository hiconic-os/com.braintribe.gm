package com.braintribe.gm.model.logging.level.api;

import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.Neutral;

public interface MutateLogLevelProfileOrder extends LogLevelRequest {
	EntityType<MutateLogLevelProfileOrder> T = EntityTypes.T(MutateLogLevelProfileOrder.class);

	LogLevelProfileOrderMutation getMutation();
	void setMutation(LogLevelProfileOrderMutation mutation);

	String getProfile();
	void setProfile(String profile);

	Integer getPosition();
	void setPosition(Integer position);

	EvalContext<Neutral> eval(Evaluator<ServiceRequest> evaluator);
}
