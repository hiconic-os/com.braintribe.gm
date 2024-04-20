package com.braintribe.model.processing.service.impl;

import java.lang.invoke.MethodHandle;

import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ServiceRequest;

public class MethodHandleServiceProcessor<P extends ServiceRequest, R> implements ServiceProcessor<P, R> {
	
	private MethodHandle methodHandle;
	
	public MethodHandleServiceProcessor(MethodHandle methodHandle) {
		super();
		this.methodHandle = methodHandle;
	}
	
	@Override
	public R process(ServiceRequestContext requestContext, P request) {
		try {
			return (R) methodHandle.invoke(requestContext, request);
		} catch (RuntimeException | Error e) {
			throw e;
		}
		catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

}
