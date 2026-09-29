// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.aspect.crypto.interceptor.manipulation;

import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.processing.aop.api.context.AroundContext;
import com.braintribe.model.processing.aspect.crypto.interceptor.CryptoInterceptor;
import com.braintribe.model.processing.aspect.crypto.interceptor.CryptoInterceptorConfiguration;

public class CryptoManipulationInterceptor
		extends CryptoInterceptor<ManipulationRequest, ManipulationResponse, CryptoManipulationInterceptorProcessor> {

	public CryptoManipulationInterceptor(CryptoInterceptorConfiguration configuration) {
		super(configuration);
	}

	@Override
	protected CryptoManipulationInterceptorProcessor createProcessor(AroundContext<ManipulationRequest, ManipulationResponse> context) {
		return new CryptoManipulationInterceptorProcessor(configuration, context);
	}
}
