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

import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

public class ProxyProperty extends AbstractProxyProperty {

	private Property actualProperty;
	private boolean resolved;

	public ProxyProperty(ProxyEntityType entityType, String name) {
		super(entityType, name);
	}

	@Override
	public GenericModelType getType() {
		return BaseType.INSTANCE;
	}

	public Property getActualProperty() {
		if (!resolved) {
			actualProperty = ((ProxyEntityType) getDeclaringType()).getResolvedType().findProperty(getName());
			resolved = true;
		}

		return actualProperty;
	}
}
