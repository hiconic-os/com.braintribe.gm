// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.generic.proxy;

import java.util.HashMap;
import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.GmSystemInterface;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GmtsPlainEntityStub;

@GmSystemInterface
public class ProxyPlainEntity extends GmtsPlainEntityStub implements ProxyEntity {

	private final AbstractProxyEntityType type;
	private final Map<AbstractProxyProperty, Object> properties = new HashMap<>();
	private GenericEntity actualEntity;

	public ProxyPlainEntity(AbstractProxyEntityType type) {
		this.type = type;
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

	@SuppressWarnings("unchecked")
	@Override
	public AbstractProxyEntityType entityType() {
		return type;
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
		return type.findProperty(partition).get(this);
	}

	@Override
	public void setPartition(String partition) {
		type.findProperty(partition).set(this, partition);
	}

	@Override
	public String getGlobalId() {
		return type.findProperty(globalId).get(this);
	}

	@Override
	public void setGlobalId(String globalId) {
		type.findProperty(globalId).set(this, globalId);
	}

}
