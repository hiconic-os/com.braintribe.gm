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
package com.braintribe.model.io.metamodel.render.context;

import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.List;
import java.util.Set;

import com.braintribe.logging.Logger;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.GmType;

/**
 * Used for detecting whether a given property is inherited from a supertype
 */
public class PropertyFilteringUtils {

	private static final Logger log = Logger.getLogger(PropertyFilteringUtils.class);

	public static boolean propertyNeedsToBeImplemented(GmProperty gmProperty, GmEntityType gmEntityType, GmEntityType declaringSuperType) {
		if (declaringSuperType != null) {
			// this is an interface -> don't implement
			return isInterfaceWithInitializer(gmEntityType, gmProperty);
		}

		return true;
	}

	private static boolean isInterfaceWithInitializer(GmEntityType gmEntityType, GmProperty gmProperty) {
		// we know it is interface (cause otherwise we would not call it), so we do not test that
		return gmProperty.getDeclaringType().getTypeSignature().equals(gmEntityType.getTypeSignature()) && gmProperty.getInitializer() != null;
	}

	/**
	 * @return true iff the property of this entityType is inherited from some of it's supertypes
	 */
	public static GmEntityType findDeclaringTypeIfInherited(GmEntityType entitType, GmProperty property) {
		return findDeclaringTypeHelper(entitType.getSuperTypes(), property, newSet());
	}

	private static GmEntityType findDeclaringTypeHelper(List<GmEntityType> superTypes, GmProperty property, Set<GmEntityType> visitedTypes) {
		if (superTypes == null)
			return null;

		for (GmEntityType superType : superTypes) {
			GmEntityType owner = findDeclaringTypeHelper(superType, property, visitedTypes);
			if (owner != null)
				return owner;
		}

		return null;
	}

	private static GmEntityType findDeclaringTypeHelper(GmEntityType superType, GmProperty property, Set<GmEntityType> visitedTypes) {
		if (visitedTypes.contains(superType))
			return null;

		visitedTypes.add(superType);

		try {
			if (containsProperty(superType.getProperties(), property))
				return superType;

		} catch (Exception e) {
			log.warn("Error while checking properties for entity: " + superType.getTypeSignature());
		}

		return findDeclaringTypeHelper(superType.getSuperTypes(), property, visitedTypes);
	}

	private static boolean containsProperty(List<GmProperty> properties, GmProperty property) {
		if (properties == null)
			return false;

		for (GmProperty someProperty : properties)
			if (isSameProprty(someProperty, property))
				return true;

		return false;
	}

	private static boolean isSameProprty(GmProperty someProperty, GmProperty property) {
		if (!property.getName().equals(someProperty.getName())) {
			return false;
		}

		GmType propType = property.getType();
		if (propType == null)
			throw new IllegalStateException("Property type is nulle. Property: " + property);

		return areEqual(propType, someProperty.getType());
	}

	private static boolean areEqual(GmType type1, GmType type2) {
		return type1.equals(type2) || (type1.getTypeSignature().equals(type2.getTypeSignature()));
	}

}
