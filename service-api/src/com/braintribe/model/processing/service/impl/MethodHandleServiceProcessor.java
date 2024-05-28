package com.braintribe.model.processing.service.impl;

import java.lang.invoke.MethodHandle;

import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ServiceRequest;

public class MethodHandleServiceProcessor<P extends ServiceRequest, R> implements ServiceProcessor<P, R> {

	private final MethodHandle methodHandle;
	private final boolean passContext;

	public MethodHandleServiceProcessor(MethodHandle methodHandle, boolean passContext) {
		this.methodHandle = methodHandle;
		this.passContext = passContext;
	}

	@Override
	public R process(ServiceRequestContext requestContext, P request) {
		try {
			if (passContext)
				return (R) methodHandle.invoke(request, requestContext);
			else
				return (R) methodHandle.invoke(request);

		} catch (RuntimeException | Error e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

}
