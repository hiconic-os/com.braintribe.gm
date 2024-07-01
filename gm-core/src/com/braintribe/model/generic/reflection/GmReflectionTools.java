// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.generic.reflection;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;

/**
 * @author peter.gazdik
 */
public class GmReflectionTools {

	/**
	 * Similar to {@link #setOptimalAbsenceInformation(GenericEntity, Property, AbsenceInformation)}, but first finds the correct
	 * {@link Property} instance.
	 */
	public static void setOptimalAbsenceInformation(GenericEntity entity, String propertyName, AbsenceInformation ai) {
		setOptimalAbsenceInformation(entity, entity.entityType().getProperty(propertyName), ai);
	}

	/**
	 * M
	 */
	public static void setOptimalAbsenceInformation(GenericEntity entity, Property property, AbsenceInformation ai) {
		if (ai.type() == AbsenceInformation.T) {
			ai = GMF.absenceInformation();
		}

		property.setAbsenceInformation(entity, ai);
	}

	public static AbsenceInformation getAbsenceInformation(GenericEntity entity, String propertyName) {
		return entity.entityType().getProperty(propertyName).getAbsenceInformation(entity);
	}

	/* TODO: There are some codecs that use this information as part of the output. I have no idea why, check this later. */
	public static boolean isPartial(GenericEntity entity) {
		for (Property p: entity.entityType().getProperties()) {
			if (p.isAbsent(entity)) {
				return true;
			}
		}
		return false;
	}

	public static <T extends GenericEntity> T makeShallowCopy(T entity) {
		EntityType<T> et = entity.entityType();

		T result = et.create();

		for (Property p: et.getProperties()) {
			Object value = p.get(entity);
			p.set(result, value);
		}

		return result;
	}

	public static <T> T makeDeepCopy(T o) {
		if (o == null)
			return null;
		
		GenericModelType type = GMF.getTypeReflection().getType(o);
		return type.clone(o, null, null);
	}

	/** Traverses given object and returns a collection of all encountered entities. */
	public static Collection<GenericEntity> collectReachableEntities(Object o) {
		if (o == null)
			return emptyList();

		StandardTraversingContext stc = new StandardTraversingContext();
		BaseType.INSTANCE.traverse(stc, o);

		return stc.getVisitedObjects();
	}

	public static boolean equals(Object o1, Object o2) {
		if (o1 == o2)
			return true;

		if (o1 == null || o2 == null)
			return false;

		return o1.equals(o2);
	}

}
