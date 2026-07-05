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
import com.braintribe.model.generic.path.PropertyRelatedModelPathElement;
import com.braintribe.model.meta.data.prompt.Visible;
import com.braintribe.model.processing.meta.cmd.CascadingMetaDataException;
import com.braintribe.model.processing.meta.cmd.builders.ModelMdResolver;
import com.braintribe.model.processing.security.query.context.PropertyExpertContext;
import com.braintribe.model.processing.security.query.expert.PropertyRelatedAccessExpert;

/**
 * Verifies whether a given property is visible.
 */
public class PropertyVisibilityIlsExpert implements PropertyRelatedAccessExpert {

	private static final Logger log = Logger.getLogger(PropertyVisibilityIlsExpert.class);

	/**
	 * @returns <tt>true</tt> iff we are visiting a visible property
	 */
	@Override
	public boolean isAccessGranted(PropertyExpertContext expertContext) {
		PropertyRelatedModelPathElement path = expertContext.getPropertyRelatedModelPathElement();

		String propertyName = path.getProperty().getName();
		GenericEntity entity = path.getEntity();
		ModelMdResolver mdResolver = expertContext.getMetaData();

		return isPropertyVisible(entity, propertyName, mdResolver);
	}

	private boolean isPropertyVisible(GenericEntity entity, String propName, ModelMdResolver mdResolver) {
		try {
			return mdResolver.entity(entity).property(propName).is(Visible.T);

		} catch (CascadingMetaDataException e) {
			log.warn("Error while resolving PropertyVisibility meta data. ", e);

			return false;
		}
	}

}
