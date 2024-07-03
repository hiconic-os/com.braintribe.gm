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
package com.braintribe.model.processing.query.test.check;

import java.util.Comparator;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;

/**
 * 
 */
public class PropertyBasedComparator implements Comparator<Object> {

	private final String[] propertyChain;

	public PropertyBasedComparator(String propertyPath) {
		this.propertyChain = propertyPath.split("\\.");
	}

	@Override
	public int compare(Object o1, Object o2) {
		for (String propertyName: propertyChain) {
			o1 = evaluate(o1, propertyName);
			o2 = evaluate(o2, propertyName);
		}

		return compareObjects(o1, o2);
	}

	private int compareObjects(Object o1, Object o2) {
		if (o1 == null)
			return -1;

		if (o2 == null)
			return 1;

		return ((Comparable<Object>) o1).compareTo(o2);
	}

	private Object evaluate(Object o, String property) {
		if (o == null)
			return null;

		if (!(o instanceof GenericEntity))
			throw new RuntimeException("Cannot resolve property '" + property + "' for: " + o + ". The object is not a GenericEntity.");

		GenericEntity entity = (GenericEntity) o;
		Property p = entity.entityType().findProperty(property);

		return p.get(entity);
	}

}
