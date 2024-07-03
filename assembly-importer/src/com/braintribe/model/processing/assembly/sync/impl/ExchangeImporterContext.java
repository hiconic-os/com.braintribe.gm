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
package com.braintribe.model.processing.assembly.sync.impl;

import java.util.Set;

import com.braintribe.model.exchange.ExchangePayload;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.assembly.sync.api.AssemblyImportContext;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * @author peter.gazdik
 */
public class ExchangeImporterContext implements AssemblyImportContext<ExchangePayload> {

	private final PersistenceGmSession session;
	private final ExchangePayload payload;
	private final boolean includeEnvelope;
	private final boolean requireAllGlobalIds;
	private final Set<GenericEntity> externalReferences;
	private final String defaultPartition;

	public ExchangeImporterContext(PersistenceGmSession session, ExchangePayload payload, boolean includeEnvelope, boolean requireAllGlobalIds,
			String defaultPartition) {

		this.session = session;
		this.payload = payload;
		this.includeEnvelope = includeEnvelope;
		this.requireAllGlobalIds = requireAllGlobalIds;
		this.defaultPartition = defaultPartition;
		this.externalReferences = payload.getExternalReferences();
	}

	@Override
	public ExchangePayload getAssembly() {
		return payload;
	}

	@Override
	public PersistenceGmSession getSession() {
		return session;
	}

	@Override
	public boolean isExternalReference(GenericEntity entity) {
		return externalReferences.contains(entity);
	}

	@Override
	public boolean isEnvelope(GenericEntity entity) {
		return entity == payload;
	}

	@Override
	public boolean requireAllGlobalIds() {
		return requireAllGlobalIds;
	}

	@Override
	public boolean includeEnvelope() {
		return includeEnvelope;
	}

	@Override
	@Deprecated
	public String getDefaultPartition() {
		return defaultPartition;
	}
}
