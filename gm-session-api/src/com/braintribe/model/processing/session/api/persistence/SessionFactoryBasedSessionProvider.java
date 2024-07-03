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
package com.braintribe.model.processing.session.api.persistence;

import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.model.generic.session.exception.GmSessionException;


public class SessionFactoryBasedSessionProvider implements Supplier<PersistenceGmSession>{

	private PersistenceGmSessionFactory persistenceGmSessionFactory;
	private String accessId;

	@Configurable @Required
	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	@Configurable @Required
	public void setPersistenceGmSessionFactory(PersistenceGmSessionFactory persistenceGmSessionFactory) {
		this.persistenceGmSessionFactory = persistenceGmSessionFactory;
	}

	@Override
	public PersistenceGmSession get() {
		try {
			return persistenceGmSessionFactory.newSession(accessId);
		} catch (GmSessionException e) {
			throw new RuntimeException(e);
		}
	}

}
