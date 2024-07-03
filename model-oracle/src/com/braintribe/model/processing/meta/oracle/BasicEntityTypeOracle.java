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

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;
import static com.braintribe.utils.lcd.CollectionTools2.nullSafe;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmCustomType;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEntityTypeInfo;
import com.braintribe.model.processing.index.ConcurrentCachedIndex;
import com.braintribe.model.processing.meta.oracle.empty.EmptyPropertyOracle;
import com.braintribe.model.processing.meta.oracle.flat.FlatEntityType;
import com.braintribe.model.processing.meta.oracle.flat.FlatProperty;

/**
 * @author peter.gazdik
 */
public class BasicEntityTypeOracle extends BasicTypeOracle implements EntityTypeOracle {

	protected final FlatEntityType flatEntityType;
	protected final PropertyOraclesIndex propertyTypeOracles = new PropertyOraclesIndex();

	private volatile List<GmProperty> inheritedProperties;
	private ReentrantLock inheritedPropertiesLock = new ReentrantLock();
	private volatile Optional<GmType> evaluatesTo;

	private final PropertyOracle emptyPropertyOracle = new EmptyPropertyOracle(this);

	public BasicEntityTypeOracle(BasicModelOracle modelOracle, FlatEntityType flatEntityType) {
		super(modelOracle);

		this.flatEntityType = flatEntityType;
	}

	class PropertyOraclesIndex extends ConcurrentCachedIndex<String, PropertyOracle> {
		@Override
		protected PropertyOracle provideValueFor(String propertyName) {
			FlatProperty flatProperty = flatEntityType.acquireFlatProperties().get(propertyName);
			return flatProperty != null ? new BasicPropertyOracle(BasicEntityTypeOracle.this, flatProperty) : emptyPropertyOracle;
		}
	}

	@Override
	public <T extends GmCustomType> T asGmType() {
		return (T) flatEntityType.type;
	}

	@Override
	public GmEntityType asGmEntityType() {
		return flatEntityType.type;
	}

	@Override
	public TypeHierarchy getSubTypes() {
		return newTypeHierarchy(HierarchyKind.subTypes);
	}

	@Override
	public TypeHierarchy getSuperTypes() {
		return newTypeHierarchy(HierarchyKind.superTypes);
	}

	private BasicTypeHierarchy newTypeHierarchy(HierarchyKind hierarchyKind) {
		return new BasicTypeHierarchy(modelOracle, flatEntityType.type, hierarchyKind);
	}

	@Override
	public List<GmEntityTypeInfo> getGmEntityTypeInfos() {
		return flatEntityType.infos;
	}

	@Override
	public Stream<MetaData> getMetaData() {
		return getGmEntityTypeInfos().stream().flatMap(gmEntityTypeInfo -> nullSafe(gmEntityTypeInfo.getMetaData()).stream());
	}

	@Override
	public Stream<MetaData> getPropertyMetaData() {
		return getGmEntityTypeInfos().stream().flatMap(gmEntityTypeInfo -> nullSafe(gmEntityTypeInfo.getPropertyMetaData()).stream());
	}

	@Override
	public Stream<QualifiedMetaData> getQualifiedMetaData() {
		return getGmEntityTypeInfos().stream().flatMap(QualifiedMetaDataTools::ownMetaData);
	}

	@Override
	public Stream<QualifiedMetaData> getQualifiedPropertyMetaData() {
		return getGmEntityTypeInfos().stream().flatMap(QualifiedMetaDataTools::entityPropertyMetaData);
	}

	@Override
	public EntityTypeProperties getProperties() {
		return new BasicEntityTypeProperties(this);
	}

	@Override
	public PropertyOracle getProperty(String propertyName) {
		PropertyOracle result = findProperty(propertyName);
		if (result == emptyPropertyOracle)
			throw new ElementInOracleNotFoundException("Property '" + propertyName + "' of '" + flatEntityType.type.getTypeSignature()
					+ "' not found in model: " + modelOracle.getGmMetaModel().getName());

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
		PropertyOracle result = propertyTypeOracles.acquireFor(propertyName);
		return result == emptyPropertyOracle ? null : result;
	}

	@Override
	public PropertyOracle findProperty(GmProperty gmProperty) {
		return findProperty(gmProperty.getName());
	}

	@Override
	public PropertyOracle findProperty(Property property) {
		return findProperty(property.getName());
	}

	protected List<GmProperty> acquireInheritedProperties() {
		if (inheritedProperties == null)
			initializeInheritedProperties();

		return inheritedProperties;
	}

	private void initializeInheritedProperties() {
		inheritedPropertiesLock.lock();
		try {
			if (inheritedProperties != null)
				return;

			List<GmProperty> newValue = newList();

			Set<String> alreadyUsedProperties = newSet();
			Set<GmEntityType> superTypes = getSuperTypes().transitive().asGmTypes();

			for (GmEntityType superType : superTypes)
				for (GmProperty gmProperty : superType.getProperties())
					if (alreadyUsedProperties.add(gmProperty.getName()))
						newValue.add(gmProperty);

			inheritedProperties = newValue;
		} finally {
			inheritedPropertiesLock.unlock();
		}
	}

	@Override
	public boolean hasProperty(String propertyName) {
		return flatEntityType.acquireFlatProperties().containsKey(propertyName);
	}

	@Override
	public boolean isEvaluable() {
		return getEvaluatesTo().isPresent();
	}

	@Override
	public Optional<GmType> getEvaluatesTo() {
		if (evaluatesTo == null)
			initializeEvaluatesToOracle();

		return evaluatesTo;
	}

	private synchronized void initializeEvaluatesToOracle() {
		if (evaluatesTo == null)
			evaluatesTo = EvaluatesToResolver.resolveEvaluatesTo(this);
	}

}
