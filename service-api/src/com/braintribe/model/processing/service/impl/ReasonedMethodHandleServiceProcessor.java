package com.braintribe.model.processing.service.impl;

import java.lang.invoke.MethodHandle;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.processing.service.api.ReasonedServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ServiceRequest;

public class ReasonedMethodHandleServiceProcessor<P extends ServiceRequest, R> implements ReasonedServiceProcessor<P, R> {

	private final MethodHandle methodHandle;
	private final boolean passContext;

	public ReasonedMethodHandleServiceProcessor(MethodHandle methodHandle, boolean passContext) {
		this.methodHandle = methodHandle;
		this.passContext = passContext;
	}

	@Override
	public Maybe<? extends R> processReasoned(ServiceRequestContext context, P request) {
		try {
			if (passContext)
				return (Maybe<R>) methodHandle.invoke(request, context);
			else
				return (Maybe<R>) methodHandle.invoke(request);

		} catch (RuntimeException | Error e) {
			throw e;

		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

}
