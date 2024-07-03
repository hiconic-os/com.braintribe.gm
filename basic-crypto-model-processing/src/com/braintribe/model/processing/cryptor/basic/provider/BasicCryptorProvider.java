// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.processing.cryptor.basic.provider;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.crypto.Cryptor;
import com.braintribe.logging.Logger;
import com.braintribe.model.crypto.configuration.CryptoConfiguration;
import com.braintribe.model.meta.data.crypto.PropertyCrypting;
import com.braintribe.model.processing.core.expert.api.GmExpertRegistry;
import com.braintribe.model.processing.crypto.factory.CryptorFactory;
import com.braintribe.model.processing.crypto.provider.CryptorProvider;
import com.braintribe.model.processing.crypto.provider.CryptorProviderException;

public class BasicCryptorProvider implements CryptorProvider<Cryptor, PropertyCrypting> {

	private static final Logger log = Logger.getLogger(BasicCryptorProvider.class);

	private GmExpertRegistry expertRegistry;

	/**
	 * <p>
	 * Sets the {@link GmExpertRegistry} used to fetch {@link CryptorFactory} experts based on the provided {@link PropertyCrypting} instances.
	 * 
	 * @param expertRegistry
	 *            the GmExpertRegistry to be set
	 */
	@Required
	@Configurable
	public void setExpertRegistry(GmExpertRegistry expertRegistry) {
		this.expertRegistry = expertRegistry;
	}

	@Override
	public Cryptor provideFor(PropertyCrypting propertyCrypting) throws CryptorProviderException {

		if (propertyCrypting == null) {
			throw new IllegalArgumentException(PropertyCrypting.class.getSimpleName() + " argument cannot be null");
		}

		CryptoConfiguration cryptoConfiguration = propertyCrypting.getCryptoConfiguration();

		if (cryptoConfiguration == null) {
			throw new IllegalStateException("The crypto configuration property of " + propertyCrypting.getClass().getName() + " cannot be null.");
		}

		Cryptor cryptor = provideForCryptoConfiguration(cryptoConfiguration);

		if (log.isTraceEnabled()) {
			log.trace("Providing [ " + cryptor + " ] based on given [ " + propertyCrypting + " ]");
		}

		return cryptor;
	}

	@Override
	public <R extends Cryptor> R provideFor(Class<R> cryptorType, PropertyCrypting propertyCrypting) throws CryptorProviderException {

		Cryptor cryptor = provideFor(propertyCrypting);
		
		if (cryptor == null) {
			return null;
		}
		
		if (cryptorType.isInstance(cryptor)) {
			return cryptorType.cast(cryptor);
		}
		
		if (log.isTraceEnabled()) {
			log.trace("Provided cryptor [ "+cryptor.getClass().getName()+" ] is not compatible with the required type [ "+cryptorType.getName()+" ]");
		}
		
		return null;
		
	}

	private Cryptor provideForCryptoConfiguration(CryptoConfiguration cryptoConfiguration) throws CryptorProviderException {

		CryptorFactory<CryptoConfiguration, Cryptor> cryptorFactory = null;

		try {
			cryptorFactory = expertRegistry.findExpert(CryptorFactory.class).forInstance(cryptoConfiguration);
		} catch (Exception e) {
			throw new CryptorProviderException("Failed to obtain a cryptor factory for the given configuration [" + cryptoConfiguration + "]", e);
		}

		if (cryptorFactory == null) {
			throw new CryptorProviderException("Failed to obtain a cryptor factory. Make sure the expert registry is properly configured to provide " + CryptorFactory.class.getName() + "(s) for the given denotation type: " + cryptoConfiguration);
		}

		Cryptor cryptor = null;

		try {
			cryptor = cryptorFactory.getCryptor(cryptoConfiguration);
		} catch (Exception e) {
			throw new CryptorProviderException("Failed to create a cryptor with factory " + cryptorFactory.getClass().getSimpleName() + (e.getMessage() != null ? ": " + e.getMessage() : ""), e);
		}

		if (cryptor == null) {
			throw new CryptorProviderException("Failed to create a cryptor. null was returned by the factory: " + cryptorFactory);
		}

		return cryptor;

	}

}
