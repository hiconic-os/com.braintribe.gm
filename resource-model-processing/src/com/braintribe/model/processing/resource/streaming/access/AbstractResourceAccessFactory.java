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
package com.braintribe.model.processing.resource.streaming.access;

import java.net.URL;
import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.logging.Logger;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.resource.ResourceAccessFactory;
import com.braintribe.provider.Holder;


public abstract class AbstractResourceAccessFactory implements ResourceAccessFactory<PersistenceGmSession> {
	
	protected Supplier<URL> baseStreamingUrlProvider;
	protected Supplier<String> sessionIdProvider;
	protected Supplier<String> responseMimeTypeProvider;
	
	private static Logger log = Logger.getLogger(AbstractResourceAccessFactory.class);

	@Configurable
	public void setBaseStreamingUrlProvider(Supplier<URL> baseStreamingUrlProvider) {
		this.baseStreamingUrlProvider = baseStreamingUrlProvider;
	}
	
	@Configurable
	public void setBaseStreamingUrl(URL baseStreamingUrl) {
		this.baseStreamingUrlProvider = new Holder<URL>(baseStreamingUrl);
	}
	
	@Configurable
	public void setSessionIdProvider(Supplier<String> sessionIdProvider) { 
		this.sessionIdProvider = sessionIdProvider;
	}
	
	@Configurable
	public void setResponseMimeTypeProvider(Supplier<String> responseMimeTypeProvider) { 
		this.responseMimeTypeProvider = responseMimeTypeProvider;
	}
	
	protected void provideBaseStreamingUrl(AbstractResourceAccess resourceAccess) {
		if (baseStreamingUrlProvider != null) {
			try {
				resourceAccess.setBaseStreamingUrl(baseStreamingUrlProvider.get());
			} catch (RuntimeException e) {
				throw new IllegalStateException("Factory in illegal state. Provider for base streaming URL caused an error: "+e.getMessage(), e);
			}
		}
	}
	
	protected void provideSessionId(AbstractResourceAccess resourceAccess) {
		resourceAccess.setSessionId(retrieveStringFromProvider(sessionIdProvider, "session id"));
	}
	
	protected void provideUploadResponseMimeType(AbstractResourceAccess resourceAccess) {
		resourceAccess.setResponseMimeType(retrieveStringFromProvider(responseMimeTypeProvider, "upload response mime type"));
	}
	
	private String retrieveStringFromProvider(Supplier<String> provider, String context) {
		if (provider == null)
			return null;
		try {
			return provider.get();
		} catch (RuntimeException e) {
			log.error("Failed to retrieve "+context+": "+e.getMessage(), e);
		}
		return null;
	}
	
}
