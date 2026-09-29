// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.aspect.crypto;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.crypto.Cryptor;
import com.braintribe.logging.Logger;
import com.braintribe.model.meta.data.crypto.PropertyCrypting;
import com.braintribe.model.processing.aop.api.aspect.AccessAspect;
import com.braintribe.model.processing.aop.api.aspect.AccessAspectRuntimeException;
import com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint;
import com.braintribe.model.processing.aop.api.aspect.PointCutConfigurationContext;
import com.braintribe.model.processing.aspect.crypto.interceptor.CryptoInterceptorConfiguration;
import com.braintribe.model.processing.aspect.crypto.interceptor.manipulation.CryptoManipulationInterceptor;
import com.braintribe.model.processing.crypto.provider.CryptorProvider;

/** Access aspect which applies cryptography described by {@link PropertyCrypting} metadata. */
public class CryptoAspect implements AccessAspect {
	private static final Logger log = Logger.getLogger(CryptoAspect.class);

	private CryptorProvider<Cryptor, PropertyCrypting> cryptorProvider;
	private boolean cacheCryptorsPerContext = true;

	@Required
	@Configurable
	public void setCryptorProvider(CryptorProvider<Cryptor, PropertyCrypting> cryptorProvider) {
		this.cryptorProvider = cryptorProvider;
	}

	@Configurable
	public void setCacheCryptorsPerContext(boolean cacheCryptorsPerContext) {
		this.cacheCryptorsPerContext = cacheCryptorsPerContext;
	}

	@Override
	public void configurePointCuts(PointCutConfigurationContext context) throws AccessAspectRuntimeException {
		try {
			CryptoInterceptorConfiguration configuration = new CryptoInterceptorConfiguration();
			configuration.setCryptorProvider(cryptorProvider);
			configuration.setCacheCryptorsPerContext(cacheCryptorsPerContext);
			context.addPointCutBinding(AccessJoinPoint.applyManipulation, new CryptoManipulationInterceptor(configuration));
		} catch (Exception e) {
			log.error("Failed to configure CryptoAspect point cuts", e);
			throw new AccessAspectRuntimeException("Failed to configure CryptoAspect point cuts", e);
		}
	}
}
