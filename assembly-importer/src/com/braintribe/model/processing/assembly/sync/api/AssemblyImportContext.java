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
package com.braintribe.model.processing.assembly.sync.api;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.assembly.sync.impl.AssemblyImporter;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.traversing.engine.api.customize.PropertyTransferExpert;
import com.braintribe.model.resource.Resource;

/**
 * Describes parameters for a single run of the {@link AssemblyImporter}.
 * 
 * 
 */
public interface AssemblyImportContext<T extends GenericEntity> {

	/**
	 * Assembly that is being imported, consisting (of course) of the the entity returned by this method (root) and all
	 * it's transitive dependencies.
	 */
	T getAssembly();

	/** Target session into which we import the data. */
	PersistenceGmSession getSession();

	/**
	 * Check whether the entity is an external reference.
	 * <p>
	 * If it is, we do not import it like other entities, but just make sure that an entity with such type and globalId
	 * is attached to the session. So if such an entity already existed in the target access, we would not do anything,
	 * otherwise we create a new instance and set only the globalId property.
	 */
	boolean isExternalReference(GenericEntity entity);

	/**
	 * Check whether given entity is envelope. In case the {@link #includeEnvelope()} returns <tt>false</tt>, we do not
	 * import entities marked as envelopes, but only their content (other entities reachable from these envelopes).
	 * 
	 */
	boolean isEnvelope(GenericEntity entity);

	/**
	 * @see #isEnvelope(GenericEntity)
	 */
	boolean includeEnvelope();

	/**
	 * If this is true, the importer throws an exception if a synced entity does not have a global id. The check is also
	 * performed on the envelope, in case it is being synced.
	 */
	boolean requireAllGlobalIds();

	/**
	 * Not needed anymore
	 */
	@Deprecated
	String getDefaultPartition();

	default void notifyImportStatistics(@SuppressWarnings("unused") ImportStatistics statistics) {
		// empty
	}

	default Resource findUploadResource(@SuppressWarnings("unused") String globalId) {
		return null;
	}
	
	default PropertyTransferExpert propertyTransferExpert() {
		return null;
	}

}
