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
package com.braintribe.cartridge.common.processing.session;

import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;


public class AccessIdBasedPersistenceGmSessionFactory implements Supplier<PersistenceGmSession> {

	private String accessId;
	private PersistenceGmSessionFactory sessionFactory;
	
	
	@Required @Configurable
	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}
	@Required @Configurable
	public void setSessionFactory(PersistenceGmSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public PersistenceGmSession get() throws RuntimeException {
		try {
			return sessionFactory.newSession(accessId);	
		} catch (Exception e) {
			throw new RuntimeException("Could not create new session for access: "+accessId,e);
		}
	}
	
}
