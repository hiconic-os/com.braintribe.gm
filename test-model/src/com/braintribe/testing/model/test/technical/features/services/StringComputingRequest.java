package com.braintribe.testing.model.test.technical.features.services;

import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

public interface StringComputingRequest extends ServiceRequest {

	EntityType<StringComputingRequest> T = EntityTypes.T(StringComputingRequest.class);

	String getArg1();
	void setArg1(String arg1);

	String getArg2();
	void setArg2(String arg2);

	@Override
	EvalContext<String> eval(Evaluator<ServiceRequest> evaluator);

}
