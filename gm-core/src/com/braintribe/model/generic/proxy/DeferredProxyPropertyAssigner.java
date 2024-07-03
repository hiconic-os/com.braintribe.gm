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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;

public class DeferredProxyPropertyAssigner implements DeferredApplier {
	private ProxyEntity entity;
	private ProxyProperty property;
	private ProxyValue value;
	
	public DeferredProxyPropertyAssigner(ProxyEntity entity, ProxyProperty property, ProxyValue value) {
		super();
		this.entity = entity;
		this.property = property;
		this.value = value;
	}


	@Override
	public void apply() {
		Property actualProperty = property.getActualProperty();
		GenericEntity actualEntity = entity.actualValue();
		
		if (actualProperty != null && actualEntity != null) {
			actualProperty.setDirectUnsafe(actualEntity, value.actualValue());
		}
	}

}
