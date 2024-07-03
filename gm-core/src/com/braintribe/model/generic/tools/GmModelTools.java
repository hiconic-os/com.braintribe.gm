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
package com.braintribe.model.generic.tools;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmLinearCollectionType;
import com.braintribe.model.meta.GmMapType;
import com.braintribe.model.meta.GmType;

/**
 * @author peter.gazdik
 */
public class GmModelTools {

	public static boolean areEntitiesReachable(GmType type) {
		switch (type.typeKind()) {
			case BASE:
			case ENTITY:
				return true;

			case LIST:
			case SET:
				return areEntitiesReachable(((GmLinearCollectionType) type).getElementType());

			case MAP: {
				GmMapType mapType = (GmMapType) type;
				return areEntitiesReachable(mapType.getKeyType()) || areEntitiesReachable(mapType.getValueType());
			}

			default:
				return false;
		}
	}

	public static <T extends GenericEntity> T createShallow(EntityType<T> entityType) {
		T entity = entityType.create();
		
		for (Property p : entityType.getProperties()) {
			if (!p.isIdentifying())
				p.setAbsenceInformation(entity, GMF.absenceInformation());
		}
		
		return entity;
	}
}
