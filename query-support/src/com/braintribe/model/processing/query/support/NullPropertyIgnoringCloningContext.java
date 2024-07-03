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
package com.braintribe.model.processing.query.support;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyTransferCompetence;
import com.braintribe.model.generic.reflection.StandardCloningContext;

/**
 * 
 * @author peter.gazdik
 */
public class NullPropertyIgnoringCloningContext extends StandardCloningContext implements PropertyTransferCompetence {

	@Override
	public void transferProperty(EntityType<?> sentityType, GenericEntity originalEntity, GenericEntity entityClone, Property property,
			Object propertyValue) throws GenericModelException {

		if (propertyValue != null) {
			property.set(entityClone, propertyValue);
		}

	}

}
