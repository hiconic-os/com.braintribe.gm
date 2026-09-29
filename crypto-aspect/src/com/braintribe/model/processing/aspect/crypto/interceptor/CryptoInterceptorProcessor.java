// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.aspect.crypto.interceptor;

import com.braintribe.model.processing.aop.api.interceptor.InterceptionException;

public interface CryptoInterceptorProcessor<O> {
	O proceed() throws InterceptionException;
}
