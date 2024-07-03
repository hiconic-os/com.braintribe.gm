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
package com.braintribe.model.generic.proxy;

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.SimpleTypes;
import com.braintribe.model.meta.data.MetaData;

public class DynamicEntityType extends AbstractProxyEntityType {

	private Set<MetaData> metaData = newSet();
	private Set<MetaData> propertyMetaData = newSet();

	public DynamicEntityType(String typeSignature) {
		super(typeSignature);

		properties = newList();

		addProperty(GenericEntity.id, BaseType.INSTANCE);
		addProperty(GenericEntity.globalId, SimpleTypes.TYPE_STRING);
		addProperty(GenericEntity.partition, SimpleTypes.TYPE_STRING);
	}

	@Override
	public Property getProperty(String name) throws GenericModelException {
		Property property = findProperty(name);
		if (property == null)
			throw new GenericModelException("Property '" + name + "' not found for dynamic entity type: " + typeSignature);

		return property;
	}

	public DynamicProperty addProperty(String name, GenericModelType type) {
		if (propertiesByName.containsKey(name))
			throw new GenericModelException("Dynamic type '" + typeSignature + "' already contains a property named: " + name);

		DynamicProperty result = new DynamicProperty(this, name, type);
		propertiesByName.put(result.getName(), result);
		properties.add(result);

		return result;
	}

	public Set<MetaData> getMetaData() {
		return metaData;
	}

	public void setMetaData(Set<MetaData> metaData) {
		this.metaData = metaData;
	}

	public Set<MetaData> getPropertyMetaData() {
		return propertyMetaData;
	}

	public void setPropertyMetaData(Set<MetaData> propertyMetaData) {
		this.propertyMetaData = propertyMetaData;
	}

}
