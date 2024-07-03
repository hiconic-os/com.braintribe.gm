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

import java.util.HashMap;
import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.GmSystemInterface;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GmtsEnhancedEntityStub;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;

@GmSystemInterface
public class ProxyEnhancedEntity extends GmtsEnhancedEntityStub implements ProxyEntity {

	private final AbstractProxyEntityType type;
	private final Map<AbstractProxyProperty, Object> properties = new HashMap<>();
	private GenericEntity actualEntity;

	public ProxyEnhancedEntity(AbstractProxyEntityType type, PropertyAccessInterceptor pai) {
		super(false);

		this.type = type;
		this.pai = pai;
	}

	/**
	 * The values in the map are instances of the following types:
	 * <ul>
	 * <li>SimpleType</li>
	 * <li>EnumType</li>
	 * <li>EntityType</li>
	 * </ul>
	 * 
	 * If a map, set, list or Escape is to be hold it needs to be wrapped by com.braintribe.model.generic.value.Escape
	 */
	@Override
	public Map<AbstractProxyProperty, Object> properties() {
		return properties;
	}

	@Override
	public void linkActualEntity(GenericEntity actualEntity) {
		this.actualEntity = actualEntity;
	}

	@Override
	public GenericEntity actualValue() {
		return actualEntity;
	}

	@Override
	public GenericModelType type() {
		return type;
	}

	@Override
	public AbstractProxyEntityType entityType() {
		return type;
	}

	@Override
	public String toString() {
		return type.toString(this);
	}

	// ######################################################
	// ## . . . . . . GenericEntity properties . . . . . . ##
	// ######################################################

	@Override
	public <T> T getId() {
		return type.getProperty(id).get(this);
	}

	@Override
	public void setId(Object value) {
		type.getProperty(id).set(this, value);
	}

	@Override
	public String getPartition() {
		return type.getProperty(partition).get(this);
	}

	@Override
	public void setPartition(String value) {
		type.getProperty(partition).set(this, value);
	}

	@Override
	public String getGlobalId() {
		return type.getProperty(globalId).get(this);
	}

	@Override
	public void setGlobalId(String value) {
		type.getProperty(globalId).set(this, value);
	}

}
