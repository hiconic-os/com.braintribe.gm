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
package com.braintribe.model.processing.manipulation.lab;

import static com.braintribe.utils.lcd.CollectionTools2.asSet;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.EntityProperty;
import com.braintribe.model.generic.manipulation.InstantiationManipulation;
import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.value.EntityReference;

/**
 * @author peter.gazdik
 */
public class ManipulationFilter {

	public static List<AtomicManipulation> filterByOwnerType(List<AtomicManipulation> manipulations, String signature) {
		return filterByOwnerTypes(manipulations, asSet(signature));
	}

	public static List<AtomicManipulation> filterByOwnerTypes(List<AtomicManipulation> manipulations, Set<String> signatures) {
		List<AtomicManipulation> result = newList();

		for (AtomicManipulation am: manipulations) {
			if (hasOwnerTypeOneOf(am, signatures)) {
				result.add(am);
			}
		}

		return result;
	}

	private static boolean hasOwnerTypeOneOf(AtomicManipulation am, Set<String> signatures) {
		return signatures.contains(getOwnerTypeSignature(am));
	}

	private static String getOwnerTypeSignature(AtomicManipulation am) {
		switch (am.manipulationType()) {
			case ADD:
			case CHANGE_VALUE:
			case CLEAR_COLLECTION:
			case REMOVE:
				return getSignatureFromOwner(((PropertyManipulation) am).getOwner());
			case DELETE:
				return getSignatureFromReference(((DeleteManipulation) am).getEntity());
			case INSTANTIATION:
				return getSignatureFromReference(((InstantiationManipulation) am).getEntity());
			default:
				throw new RuntimeException("Unexpected manipulation: " + am);
		}
	}

	private static String getSignatureFromOwner(Owner owner) {
		return ((EntityProperty) owner).getReference().getTypeSignature();
	}

	private static String getSignatureFromReference(GenericEntity entity) {
		return ((EntityReference) entity).getTypeSignature();
	}

}
