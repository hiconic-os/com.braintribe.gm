package com.braintribe.model.processing.service.api;

import java.util.function.Predicate;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.service.api.ServiceRequest;

public interface InterceptorRegistration {
	InterceptorRegistration before(String identification);
	InterceptorRegistration after(String identification);
	void register(ServiceInterceptorProcessor interceptor);
	void registerWithPredicate(Predicate<ServiceRequest> predicate, ServiceInterceptorProcessor interceptor);
	<R extends ServiceRequest> void registerForType(EntityType<R> requestType, ServiceInterceptorProcessor interceptor);
}