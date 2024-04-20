package com.braintribe.model.processing.service.impl;

import java.lang.invoke.MethodHandle;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.processing.service.api.ReasonedServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ServiceRequest;

public class ReasonedMethodHandleServiceProcessor<P extends ServiceRequest, R> implements ReasonedServiceProcessor<P, R> {
	
	private MethodHandle methodHandle;
	
	public ReasonedMethodHandleServiceProcessor(MethodHandle methodHandle) {
		this.methodHandle = methodHandle;
	}
	
	@Override
	public Maybe<? extends R> processReasoned(ServiceRequestContext context, P request) {
		try {
			return (Maybe<R>) methodHandle.invoke(context, request);
		} catch (RuntimeException | Error e) {
			throw e;
		}
		catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

}
