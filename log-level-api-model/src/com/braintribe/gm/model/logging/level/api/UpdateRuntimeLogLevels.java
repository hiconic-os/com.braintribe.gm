package com.braintribe.gm.model.logging.level.api;

import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.Neutral;

public interface UpdateRuntimeLogLevels extends LogLevelRequest {
	EntityType<UpdateRuntimeLogLevels> T = EntityTypes.T(UpdateRuntimeLogLevels.class);

	Map<String, String> getLevels();
	void setLevels(Map<String, String> levels);

	Set<String> getNamesToRemove();
	void setNamesToRemove(Set<String> namesToRemove);

	boolean getClearAll();
	void setClearAll(boolean clearAll);

	EvalContext<Neutral> eval(Evaluator<ServiceRequest> evaluator);
}
