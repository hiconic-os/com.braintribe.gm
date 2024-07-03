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
package com.braintribe.utils.junit.assertions;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.utils.genericmodel.GMCoreTools;

/**
 * Helper class that specifies an entity property by {@link GenericEntity} instance and property name.
 * 
 * @author michael.lafite
 */
public class GenericEntityProperty {
	private final GenericEntity entity;
	private final String name;
	private Object value;
	private boolean valueRetrieved;
	private boolean wasAbsent;

	public GenericEntityProperty(final GenericEntity entity, final String name) {
		this.entity = entity;
		this.name = name;
	}

	public GenericEntity getEntity() {
		return this.entity;
	}

	public String getName() {
		return this.name;
	}

	public Object getValue() {
		if (!this.valueRetrieved) {
			this.wasAbsent = isAbsent();
			this.value = GMCoreTools.getPropertyValue(this.entity, this.name);
			this.valueRetrieved = true;
		}
		return this.value;
	}

	public boolean isAbsent() {
		return GMCoreTools.isAbsent(this.entity, this.name);
	}

	public boolean wasAbsent() {
		return this.wasAbsent;
	}

}
