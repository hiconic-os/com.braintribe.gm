// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.aspect.crypto.interceptor;

import com.braintribe.crypto.Cryptor;
import com.braintribe.model.meta.data.crypto.PropertyCrypting;
import com.braintribe.model.processing.crypto.provider.CryptorProvider;

public class CryptoInterceptorConfiguration {
	private CryptorProvider<Cryptor, PropertyCrypting> cryptorProvider;
	private boolean cacheCryptorsPerContext = true;

	public CryptorProvider<Cryptor, PropertyCrypting> getCryptorProvider() {
		return cryptorProvider;
	}

	public void setCryptorProvider(CryptorProvider<Cryptor, PropertyCrypting> cryptorProvider) {
		this.cryptorProvider = cryptorProvider;
	}

	public boolean isCacheCryptorsPerContext() {
		return cacheCryptorsPerContext;
	}

	public void setCacheCryptorsPerContext(boolean cacheCryptorsPerContext) {
		this.cacheCryptorsPerContext = cacheCryptorsPerContext;
	}
}
