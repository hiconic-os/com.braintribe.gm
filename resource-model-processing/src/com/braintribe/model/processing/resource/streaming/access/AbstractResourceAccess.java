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

import java.io.IOException;
import java.net.URL;
import java.util.function.Supplier;

import com.braintribe.logging.Logger;
import com.braintribe.model.access.HasAccessId;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.resource.ResourceAccess;
import com.braintribe.model.processing.session.api.resource.ResourceUrlBuilder;
import com.braintribe.model.resource.Resource;

public abstract class AbstractResourceAccess implements ResourceAccess {

	protected PersistenceGmSession gmSession;
	protected URL baseStreamingUrl;
	protected String sessionId;
	protected Supplier<String> sessionIdProvider;
	protected String accessId;
	protected String responseMimeType;
	
	private static final Logger log = Logger.getLogger(AbstractResourceAccess.class);

	AbstractResourceAccess(PersistenceGmSession gmSession) {
		this.gmSession = gmSession;
		initialize();
	}
	
	protected void initialize() {

		if (gmSession instanceof HasAccessId)
			accessId = ((HasAccessId)gmSession).getAccessId();
		
		if (log.isTraceEnabled())
			log.trace(getClass().getName()+" initialized with access id [ "+accessId+" ]");
	}
	
	/**
	 * Gets the {@link #gmSession} property
	 */
	public PersistenceGmSession getPersistenceGmSession() {
		return gmSession;
	}
	
	/**
	 * @param gmSession the gmSession to set
	 */
	public void setPersistenceGmSession(PersistenceGmSession gmSession) {
		this.gmSession = gmSession;
	}

	/**
	 * Gets the {@link #baseStreamingUrl} property
	 */
	public URL getBaseStreamingUrl() {
		return this.baseStreamingUrl;
	}

	/**
	 * @param baseStreamingUrl the baseStreamingUrl to set
	 */
	public void setBaseStreamingUrl(URL baseStreamingUrl) {
		this.baseStreamingUrl = baseStreamingUrl;
	}

	/**
	 * <p>Sets the session id used to build the streaming URL.
	 * 
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	/**
	 * <p>Sets the provider of session id used to build the streaming URL.
	 * 
	 * <p>If configured, this provider takes precedence over the direct session id configured through {@link #setSessionId(String)}.
	 * 
	 * @param sessionIdProvider the sessionIdProvider to set
	 */
	public void setSessionIdProvider(Supplier<String> sessionIdProvider) {
		this.sessionIdProvider = sessionIdProvider;
	}
	
	/**
	 * @param accessId the accessId to set
	 */
	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public void setResponseMimeType(String responseMimeType) {
		this.responseMimeType = responseMimeType;
	}

	@Override
	public ResourceUrlBuilder url(Resource resource) {
		
		if (baseStreamingUrl == null)
			return null;
		
		BasicResourceUrlBuilder urlBuilder = new BasicResourceUrlBuilder();

		urlBuilder.setBaseStreamingUrl(baseStreamingUrl);
		urlBuilder.setResource(resource);
		urlBuilder.setAccessId(accessId);
		urlBuilder.setResponseMimeType(responseMimeType);
		
		enrichWithSessionId(urlBuilder);
		
		return urlBuilder;
		
	}

	@Deprecated
	public ResourceUrlBuilder buildStreamingUrl(@SuppressWarnings("unused") Resource resource) {
		throw new UnsupportedOperationException("Unsupported. Use url() instead.");
	}

	/**
	 * Enriches the given {@link BasicResourceUrlBuilder} with a session id, based on the configuration 
	 * possibilities ({@link #setSessionIdProvider(Supplier)} and {@link #setSessionId(String)}).
	 */
	protected void enrichWithSessionId(BasicResourceUrlBuilder urlBuilder) {

		String sessionId = null;
		
		//this.sessionIdProvider takes priority
		if (sessionIdProvider != null) {
			try {
				sessionId = sessionIdProvider.get();
				if (log.isTraceEnabled()) {
					log.trace("User session id [ "+sessionId+" ] provided with configured sessionIdProvider [ "+sessionIdProvider+" ]");
				}
			} catch (Exception e) {
				log.error("Failed to obtain an user session id from [ "+sessionIdProvider+" ] for the resource URL builder: "+e.getClass().getName()+(e.getMessage() != null ? ": "+e.getMessage() : ""), e);
			}
		}
		
		//this.sessionId is used if sessionIdProvider was not configured or if the configured one failed to provide a user session id
		if (sessionId == null) {
			sessionId = this.sessionId;
		}
		
		if (sessionId != null) {
			urlBuilder.setSessionId(sessionId);
		} else {
			if (log.isTraceEnabled()) {
				log.trace(urlBuilder.getClass().getName()+" was not enriched with a session id. Configured user session id provider: [ "+this.sessionIdProvider+" ] , user session id [ "+this.sessionId+" ].");
			}
		}
		
	}

	/**
	 * Wraps the given {@link Exception} into {@link IOException}, if the {@code ex} parameter 
	 * is not already an {@link IOException}
	 * 
	 * @param ex 
	 * 			{@link Exception} to be returned, casted to {@link IOException} 
	 *          or wrapped as {@link IOException}
	 */
	protected IOException asIOException(Exception ex) {
		return asIOException(ex, null);
	}

	/**
	 * Wraps the given {@link Exception} into {@link IOException}, if the {@code ex} parameter 
	 * is not already an {@link IOException}
	 * 
	 * @param ex 
	 * 			{@link Exception} to be returned, casted to {@link IOException} 
	 *          or wrapped as {@link IOException}
	 *          
	 * @param message 
	 * 			Optional complement message. Will prefix {@code ex}'s message as the message of 
	 *          the returned {@link IOException} in order to contextualize the error.
	 */
	protected IOException asIOException(Exception ex, String message) {
		message = (message == null || message.trim().isEmpty()) ? ex.getMessage() : message+": "+ex.getMessage();
		return (ex instanceof IOException) ? (IOException)ex : new IOException(message, ex);
	}
	
}
