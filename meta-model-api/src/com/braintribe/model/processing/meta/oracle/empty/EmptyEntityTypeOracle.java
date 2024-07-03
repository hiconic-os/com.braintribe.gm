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
package com.braintribe.model.processing.meta.oracle.empty;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEntityTypeInfo;
import com.braintribe.model.processing.meta.oracle.ElementInOracleNotFoundException;
import com.braintribe.model.processing.meta.oracle.EntityTypeOracle;
import com.braintribe.model.processing.meta.oracle.EntityTypeProperties;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.model.processing.meta.oracle.PropertyOracle;
import com.braintribe.model.processing.meta.oracle.QualifiedMetaData;
import com.braintribe.model.processing.meta.oracle.TypeHierarchy;

/**
 * @author peter.gazdik
 */
public class EmptyEntityTypeOracle extends EmptyTypeOracle implements EntityTypeOracle {

	public EmptyEntityTypeOracle(ModelOracle modelOracle) {
		super(modelOracle);
	}

	protected final PropertyOracle emptyPropertyOracle = new EmptyPropertyOracle(this);

	@Override
	public GmEntityType asGmEntityType() {
		return null;
	}

	@Override
	public TypeHierarchy getSubTypes() {
		return EmptyTypeHierarchy.INSTANCE;
	}

	@Override
	public TypeHierarchy getSuperTypes() {
		return EmptyTypeHierarchy.INSTANCE;
	}

	@Override
	public List<GmEntityTypeInfo> getGmEntityTypeInfos() {
		return Collections.emptyList();
	}

	@Override
	public EntityTypeProperties getProperties() {
		return EmptyEntityTypeProperties.INSTANCE;
	}

	@Override
	public PropertyOracle getProperty(String propertyName) {
		PropertyOracle result = findProperty(propertyName);
		if (result == null) {
			throw new ElementInOracleNotFoundException("Property not found: " + propertyName);
		}

		return result;
	}

	@Override
	public PropertyOracle getProperty(GmProperty gmProperty) {
		return getProperty(gmProperty.getName());
	}

	@Override
	public PropertyOracle getProperty(Property property) {
		return getProperty(property.getName());
	}

	@Override
	public PropertyOracle findProperty(String propertyName) {
		return emptyPropertyOracle;
	}

	@Override
	public PropertyOracle findProperty(GmProperty gmProperty) {
		return findProperty(gmProperty.getName());
	}

	@Override
	public PropertyOracle findProperty(Property property) {
		return findProperty(property.getName());
	}

	@Override
	public Stream<MetaData> getPropertyMetaData() {
		return Stream.empty();
	}

	@Override
	public Stream<QualifiedMetaData> getQualifiedPropertyMetaData() {
		return Stream.empty();
	}

	@Override
	public boolean hasProperty(String propertyName) {
		return false;
	}

	@Override
	public boolean isEvaluable() {
		return false;
	}

	@Override
	public Optional<GmType> getEvaluatesTo() {
		return Optional.empty();
	}

}
