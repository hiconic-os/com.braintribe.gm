// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.aspect.crypto.interceptor;

import com.braintribe.model.processing.aop.api.interceptor.InterceptionException;

public class CryptoInterceptionException extends InterceptionException {
	private static final long serialVersionUID = 1L;

	public CryptoInterceptionException(String message, Throwable cause) {
		super(message, cause);
	}

	public CryptoInterceptionException(String message) {
		super(message);
	}
}
