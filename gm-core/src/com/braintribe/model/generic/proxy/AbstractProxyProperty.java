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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.AbstractProperty;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;

public abstract class AbstractProxyProperty extends AbstractProperty {

	private final AbstractProxyEntityType entityType;

	public AbstractProxyProperty(AbstractProxyEntityType entityType, String name) {
		super(name, true, false);
		this.entityType = entityType;
	}

	@Override
	public abstract GenericModelType getType();

	@Override
	public EntityType<?> getDeclaringType() {
		return entityType;
	}

	@Override
	public EntityType<?> getFirstDeclaringType() {
		return entityType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getDirectUnsafe(GenericEntity entity) {
		ProxyEntity proxyEntity = (ProxyEntity) entity;
		return (T) proxyEntity.properties().get(this);
	}

	@Override
	public void setDirectUnsafe(GenericEntity entity, Object value) {
		ProxyEntity proxyEntity = (ProxyEntity) entity;
		proxyEntity.properties().put(this, value);
	}

	@Override
	public <T> T getDirect(GenericEntity entity) {
		return getDirectUnsafe(entity);
	}

	@Override
	public Object setDirect(GenericEntity entity, Object value) {
		ProxyEntity proxyEntity = (ProxyEntity) entity;
		return proxyEntity.properties().put(this, value);
	}

}
