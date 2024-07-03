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

import static com.braintribe.utils.lcd.CollectionTools2.nullSafe;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmProperty;

/**
 * @author peter.gazdik
 */
public class BasicEntityTypeProperties implements EntityTypeProperties {

	private final BasicEntityTypeOracle entityTypeOracle;

	private Predicate<? super GmProperty> filter;
	private TypeSource typeSource = TypeSource.all;

	public BasicEntityTypeProperties(BasicEntityTypeOracle entityTypeOracle) {
		this.entityTypeOracle = entityTypeOracle;
	}

	@Override
	public EntityTypeProperties filter(Predicate<? super GmProperty> filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public EntityTypeProperties onlyDeclared() {
		typeSource = TypeSource.declared;
		return this;
	}

	@Override
	public EntityTypeProperties onlyInherited() {
		typeSource = TypeSource.inherited;
		return this;
	}

	@Override
	public Stream<Property> asProperties() {
		EntityType<GenericEntity> et = BasicModelOracle.typeReflection.getEntityType(entityTypeOracle.asGmType().getTypeSignature());
		return asGmProperties().map(gmProperty -> et.getProperty(gmProperty.getName()));
	}

	@Override
	public Stream<PropertyOracle> asPropertyOracles() {
		return asGmProperties().map(gmProperty -> entityTypeOracle.getProperty(gmProperty));
	}

	@Override
	public Stream<GmProperty> asGmProperties() {
		Stream<GmProperty> result = getPropertiesBasedOnTypeSource();

		if (filter != null) {
			result = result.filter(filter);
		}

		return result;
	}

	private Stream<GmProperty> getPropertiesBasedOnTypeSource() {
		switch (typeSource) {
			case all:
				return Stream.concat(getDeclaredProperties(), getInheritedProperties());
			case declared:
				return getDeclaredProperties();
			case inherited:
				return getInheritedProperties();
		}

		throw new RuntimeException("Unsupported TypeSource: " + typeSource);
	}

	private Stream<GmProperty> getDeclaredProperties() {
		return nullSafe(entityTypeOracle.flatEntityType.type.getProperties()).stream();
	}

	private Stream<GmProperty> getInheritedProperties() {
		return entityTypeOracle.acquireInheritedProperties().stream();
	}

}
