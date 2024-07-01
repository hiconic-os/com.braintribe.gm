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
package com.braintribe.codec.dom.genericmodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import com.braintribe.codec.dom.DomEncodingContext;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

public class EncodingContext extends DomEncodingContext {
	private int nextId = 0;
	private int version = 1;
	private Element poolElement;
	private Set<GenericModelType> requiredTypes = new HashSet<GenericModelType>();
	private Map<GenericEntity, Integer> visitedEntities = new HashMap<GenericEntity, Integer>();
	
	public void setPoolElement(Element entitiesElement) {
		this.poolElement = entitiesElement;
	}
	
	public Element getPoolElement() {
		return poolElement;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public int getVersion() {
		return version;
	}
	
	public Integer register(GenericEntity entity) {
		Integer id = nextId++;
		visitedEntities.put(entity, id);
		return id;
	}

	public Integer getId(GenericEntity entity) {
		return visitedEntities.get(entity);
	}
	
	public Set<GenericModelType> getRequiredTypes() {
		return requiredTypes;
	}
	
	public void registerRequiredType(GenericModelType type) {
		requiredTypes.add(type);
	}
	
	@SuppressWarnings("unused")
	public boolean isPropertyValueUsedForMatching(EntityType<?> type, GenericEntity entity, Property property) {
		return true;
	}
}
