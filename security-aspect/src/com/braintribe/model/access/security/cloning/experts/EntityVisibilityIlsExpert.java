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
package com.braintribe.model.access.security.cloning.experts;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.data.prompt.Visible;
import com.braintribe.model.processing.meta.cmd.CascadingMetaDataException;
import com.braintribe.model.processing.meta.cmd.builders.ModelMdResolver;
import com.braintribe.model.processing.security.query.context.EntityExpertContext;
import com.braintribe.model.processing.security.query.expert.EntityAccessExpert;

/**
 * Verifies whether a given entity is visible.
 */
public class EntityVisibilityIlsExpert implements EntityAccessExpert {

	private static final Logger log = Logger.getLogger(EntityVisibilityIlsExpert.class);

	/**
	 * @return <tt>true</tt> iff we are visiting an entity that is visible
	 */
	@Override
	public boolean isAccessGranted(EntityExpertContext expertContext) {
		GenericEntity entity = expertContext.getEntity();

		if (entity == null) {
			return true;
		}

		ModelMdResolver mdResolver = expertContext.getMetaData();

		return isEntityVisible(entity, mdResolver);
	}

	private boolean isEntityVisible(GenericEntity entity, ModelMdResolver mdResolver) {
		try {
			return mdResolver.entity(entity).is(Visible.T);

		} catch (CascadingMetaDataException e) {
			log.debug("Error while resolving EntityVisibility meta data. " + e);

			return false;
		}
	}

}
