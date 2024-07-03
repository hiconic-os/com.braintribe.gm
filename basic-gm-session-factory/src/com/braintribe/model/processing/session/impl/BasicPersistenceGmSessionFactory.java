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
package com.braintribe.model.processing.session.impl;

import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.model.access.AccessService;
import com.braintribe.model.access.impl.AccessServiceDelegatingAccess;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.generic.session.exception.GmSessionRuntimeException;
import com.braintribe.model.processing.session.api.managed.ModelAccessoryFactory;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;
import com.braintribe.model.processing.session.api.persistence.auth.SessionAuthorization;
import com.braintribe.model.processing.session.api.resource.ResourceAccessFactory;
import com.braintribe.model.processing.session.impl.persistence.BasicPersistenceGmSession;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.provider.Holder;

/**
 * This should in almost all cases be the only necessary implementation of {@link PersistenceGmSessionFactory}, because
 * all its functionality can be evaluated via DDSA in a normalized way. E.g. you can get local or remote sessions
 * depending on how you configure it - i.e. the used evaluator.
 * 
 * @author Neidhart.Orlich
 * @author Michael.Lafite
 *
 */
public class BasicPersistenceGmSessionFactory implements PersistenceGmSessionFactory {

	private AccessService accessService;
	private ResourceAccessFactory<? super BasicPersistenceGmSession> resourceAccessFactory;
	private Supplier<SessionAuthorization> sessionAuthorizationProvider;
	private ModelAccessoryFactory modelAccessoryFactory;
	private Evaluator<ServiceRequest> requestEvaluator;

	@Configurable
	@Required
	public void setModelAccessoryFactory(ModelAccessoryFactory modelAccessoryFactory) {
		this.modelAccessoryFactory = modelAccessoryFactory;
	}

	@Override
	public PersistenceGmSession newSession(final String accessId) throws GmSessionException {
		try {

			final AccessServiceDelegatingAccess access = new AccessServiceDelegatingAccess();
			access.setAccessId(accessId);
			access.setAccessService(this.accessService);

			final BasicPersistenceGmSession persistenceGmSession = new BasicPersistenceGmSession();
			persistenceGmSession.setIncrementalAccess(access);
			persistenceGmSession.setResourcesAccessFactory(resourceAccessFactory);
			persistenceGmSession.setAccessId(accessId);
			persistenceGmSession.setRequestEvaluator(requestEvaluator);

			if (modelAccessoryFactory != null)
				persistenceGmSession.setModelAccessory(modelAccessoryFactory.getForAccess(accessId));

			if (sessionAuthorizationProvider != null)
				persistenceGmSession.setSessionAuthorization(sessionAuthorizationProvider.get());

			return persistenceGmSession;

		} catch (final Exception e) {
			throw new GmSessionRuntimeException("Could not create gm session.", e);
		}
	}

	public AccessService getAccessService() {
		return this.accessService;
	}

	@Required
	@Configurable
	public void setResourceAccessFactory(ResourceAccessFactory<? super BasicPersistenceGmSession> resourceAccessFactory) {
		this.resourceAccessFactory = resourceAccessFactory;
	}

	@Required
	@Configurable
	public void setAccessService(final AccessService accessService) {
		this.accessService = accessService;
	}

	@Configurable
	public void setSessionAuthorizationProvider(Supplier<SessionAuthorization> sessionAuthorizationProvider) {
		this.sessionAuthorizationProvider = sessionAuthorizationProvider;
	}

	@Configurable
	public void setSessionAuthorization(SessionAuthorization sessionAuthorization) {
		setSessionAuthorizationProvider(new Holder<>(sessionAuthorization));
	}

	@Configurable
	public void setRequestEvaluator(Evaluator<ServiceRequest> requestEvaluator) {
		this.requestEvaluator = requestEvaluator;
	}

}
