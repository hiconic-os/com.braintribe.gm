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
package com.braintribe.model.access.security.query;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.query.From;
import com.braintribe.model.query.PropertyOperand;
import com.braintribe.model.query.Source;

/**
 * 
 */
class SourcesDescriptor {

	private EntityType<?> propertyOwnerType;
	private final Set<EntityType<?>> sourceTypes = newSet();
	private final Map<Source, EntityType<?>> explicitSources = newMap();
	private final Map<PropertyOperand, EntityType<?>> implicitSources = newMap();

	public From defaultSource;

	public void addExplicitSource(Source source, EntityType<?> sourceType) {
		explicitSources.put(source, sourceType);
		sourceTypes.add(sourceType);
	}

	public EntityType<?> getSourceType(Source source) {
		if (source == null) {
			source = defaultSource;
		}

		return explicitSources.get(source);
	}

	public void addImplicitSource(PropertyOperand propertyOperand, EntityType<?> propertyType) {
		implicitSources.put(propertyOperand, propertyType);
		sourceTypes.add(propertyType);
	}

	public Set<Entry<Source, EntityType<?>>> explicitSources() {
		return explicitSources.entrySet();
	}

	public Set<Entry<PropertyOperand, EntityType<?>>> implicitSources() {
		return implicitSources.entrySet();
	}

	public EntityType<?> getPropertyOwnerType() {
		return propertyOwnerType;
	}

	public void setPropertyOwnerType(EntityType<?> propertyOwnerType) {
		this.propertyOwnerType = propertyOwnerType;
		this.sourceTypes.add(propertyOwnerType);
	}

	public Set<EntityType<?>> getSourceTypes() {
		return sourceTypes;
	}

	public Source sourceToInject(Source source) {
		return source == defaultSource ? null : source;
	}

}
