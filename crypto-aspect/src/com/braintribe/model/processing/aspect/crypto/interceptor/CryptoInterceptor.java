// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.aspect.crypto.interceptor;

import com.braintribe.model.processing.aop.api.context.AroundContext;
import com.braintribe.model.processing.aop.api.interceptor.AroundInterceptor;
import com.braintribe.model.processing.aop.api.interceptor.InterceptionException;

public abstract class CryptoInterceptor<I, O, C extends CryptoInterceptorProcessor<O>> implements AroundInterceptor<I, O> {
	protected final CryptoInterceptorConfiguration configuration;

	protected CryptoInterceptor(CryptoInterceptorConfiguration configuration) {
		this.configuration = configuration;
	}

	protected abstract C createProcessor(AroundContext<I, O> context) throws InterceptionException;

	@Override
	public O run(AroundContext<I, O> context) throws InterceptionException {
		if (context == null)
			throw new InterceptionException("Invalid null context");
		return createProcessor(context).proceed();
	}
}
