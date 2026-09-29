// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.aspect.crypto.interceptor;

import java.util.HashMap;
import java.util.Map;

import com.braintribe.crypto.Cryptor;
import com.braintribe.crypto.Encryptor;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.crypto.PropertyCrypting;
import com.braintribe.model.processing.aop.api.context.AroundContext;
import com.braintribe.model.processing.aop.api.interceptor.InterceptionException;
import com.braintribe.model.processing.crypto.provider.CryptorProvider;
import com.braintribe.model.processing.meta.cmd.CmdResolver;

public abstract class AbstractCryptoInterceptorProcessor<I, O> implements CryptoInterceptorProcessor<O> {
	protected final AroundContext<I, O> aroundContext;
	private final CmdResolver metaDataResolver;
	private final CryptorProvider<Cryptor, PropertyCrypting> cryptorProvider;
	private final Map<String, Cryptor> cachedCryptors;

	protected AbstractCryptoInterceptorProcessor(CryptoInterceptorConfiguration configuration, AroundContext<I, O> aroundContext, Logger log) {
		if (configuration == null)
			throw new IllegalArgumentException("configuration must not be null");
		if (aroundContext == null)
			throw new IllegalArgumentException("aroundContext must not be null");

		this.aroundContext = aroundContext;
		this.metaDataResolver = aroundContext.getSession().getModelAccessory().getCmdResolver();
		this.cryptorProvider = configuration.getCryptorProvider();
		this.cachedCryptors = configuration.isCacheCryptorsPerContext() ? new HashMap<>() : null;

		if (cryptorProvider == null)
			throw new IllegalArgumentException("No CryptorProvider configured");
		if (metaDataResolver == null)
			throw new IllegalArgumentException("The access context provides no CmdResolver");
	}

	protected abstract boolean mustProcessRequest() throws CryptoInterceptionException;
	protected abstract I processRequest() throws CryptoInterceptionException;

	@Override
	public O proceed() throws InterceptionException {
		I request = mustProcessRequest() ? processRequest() : aroundContext.getRequest();
		return aroundContext.proceed(request);
	}

	protected Encryptor mustEncrypt(EntityType<? extends GenericEntity> entityType, String propertyName) throws CryptoInterceptionException {
		String key = entityType.getTypeSignature() + ":" + propertyName;
		if (cachedCryptors != null) {
			Cryptor cached = cachedCryptors.get(key);
			if (cached instanceof Encryptor)
				return (Encryptor) cached;
		}

		PropertyCrypting propertyCrypting;
		try {
			propertyCrypting = metaDataResolver.getMetaData().entityType(entityType).property(propertyName).meta(PropertyCrypting.T).exclusive();
		} catch (Exception e) {
			throw cryptoError("Failed to resolve PropertyCrypting for " + key, e);
		}

		if (propertyCrypting == null)
			return null;

		try {
			Encryptor encryptor = cryptorProvider.provideFor(Encryptor.class, propertyCrypting);
			if (cachedCryptors != null && encryptor != null)
				cachedCryptors.put(key, encryptor);
			return encryptor;
		} catch (Exception e) {
			throw cryptoError("Failed to provide a cryptor for " + key, e);
		}
	}

	protected static CryptoInterceptionException cryptoError(String message, Throwable cause) {
		return new CryptoInterceptionException(message + (cause.getMessage() == null ? "" : ": " + cause.getMessage()), cause);
	}
}
