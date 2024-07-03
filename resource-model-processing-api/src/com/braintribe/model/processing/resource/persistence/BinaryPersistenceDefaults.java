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
package com.braintribe.model.processing.resource.persistence;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.resource.ResourceProcessingDefaults;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.ResourceSource;

/**
 * <p>
 * A collection of default utility methods at disposal of {@link BinaryPersistence} implementations for use and
 * overriding.
 * 
 */
public interface BinaryPersistenceDefaults extends ResourceProcessingDefaults {

	default Resource createResource(Resource source) {
		
		return createResource(null, source);
		
	}
	
	default Resource createResource(PersistenceGmSession session, Resource source) {

		final Resource resource;
		
		resource = createEntity(session, Resource.T);

		transferProperties(source, resource, e -> createEntity(session, e));

		return resource;

	}

	default Resource createResource(PersistenceGmSession session, Resource source, ResourceSource resourceSource) {

		Resource resource = createResource(session, source);
		
		resource.setResourceSource(resourceSource);
		
		return resource;

	}
	
	default <T extends GenericEntity> T createEntity(PersistenceGmSession session, EntityType<T> entityType) {
		if (session == null) {
			return entityType.create();
		} else {
			return session.create(entityType);
		}
	}

}
