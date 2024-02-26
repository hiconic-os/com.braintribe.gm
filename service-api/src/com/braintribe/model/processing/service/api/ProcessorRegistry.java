package com.braintribe.model.processing.service.api;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.service.api.ServiceRequest;

public interface ProcessorRegistry {
	<R extends ServiceRequest> void register(EntityType<R> requestType, ServiceProcessor<? super R, ?> serviceProcessor);
}
