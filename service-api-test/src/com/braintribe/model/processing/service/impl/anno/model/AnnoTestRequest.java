package com.braintribe.model.processing.service.impl.anno.model;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

@Abstract
public interface AnnoTestRequest extends ServiceRequest {

	EntityType<AnnoTestRequest> T = EntityTypes.T(AnnoTestRequest.class);

	String getParameter();
	void setParameter(String parameter);

	@Override
	EvalContext<String> eval(Evaluator<ServiceRequest> evaluator);

}