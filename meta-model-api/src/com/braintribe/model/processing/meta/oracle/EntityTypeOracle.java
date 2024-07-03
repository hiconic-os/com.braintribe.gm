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
package com.braintribe.model.processing.meta.oracle;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEntityTypeInfo;

/**
 * @author peter.gazdik
 */
public interface EntityTypeOracle extends TypeOracle {

	GmEntityType asGmEntityType();

	TypeHierarchy getSubTypes();

	TypeHierarchy getSuperTypes();

	/**
	 * List of all {@link GmEntityTypeInfo}s for given entity type, in the BFS order, i.e. actual {@link GmEntityType}
	 * is the last one in the list.
	 */
	List<GmEntityTypeInfo> getGmEntityTypeInfos();

	EntityTypeProperties getProperties();

	PropertyOracle getProperty(String propertyName);

	PropertyOracle getProperty(GmProperty gmProperty);

	PropertyOracle getProperty(Property property);

	PropertyOracle findProperty(String propertyName);

	PropertyOracle findProperty(GmProperty gmProperty);

	PropertyOracle findProperty(Property property);

	/** Returns all property MD from all GmEntityTypeInfos (those returned via getGmEntityTypeInfos()) */
	Stream<MetaData> getPropertyMetaData();

	/** Qualified version of {@link #getQualifiedPropertyMetaData()} */
	Stream<QualifiedMetaData> getQualifiedPropertyMetaData();

	/**
	 * Returns true iff this entity type declares or inherits a property with given name. This might be useful when
	 * trying to follow a path of inheritance for given property, starting from the leaf.
	 */
	boolean hasProperty(String propertyName);

	/**
	 * Returns true if this type or some of it's super-types also has the {@link GmEntityType#getEvaluatesTo()
	 * evaluatesTo} set.
	 */
	boolean isEvaluable();

	/**
	 * If this type is {@link #isEvaluable() is evaluable}, returns the corresponding {@link GmType} (which might also
	 * be inherited from a super-type). Otherwise returns null.
	 */
	Optional<GmType> getEvaluatesTo();

}
