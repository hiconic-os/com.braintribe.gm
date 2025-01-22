package com.braintribe.testing.model.test.technical.features.services;

import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.testing.model.test.demo.person.Person;

public interface EntityComputingRequest extends ServiceRequest {

	EntityType<EntityComputingRequest> T = EntityTypes.T(EntityComputingRequest.class);

	Person getArg();
	void setArg(Person arg);

	@Override
	EvalContext<Person> eval(Evaluator<ServiceRequest> evaluator);

}
