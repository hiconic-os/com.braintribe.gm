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
package com.braintribe.model.processing.cmd.test.meta.selector;

import java.util.Collection;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.GmEntityTypeAspect;
import com.braintribe.model.processing.meta.cmd.context.experts.SelectorExpert;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataTools;

public class PropertyPresentSelectorExpert implements SelectorExpert<PropertyDeclaredSelector> {

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(PropertyDeclaredSelector selector)  {
		return MetaDataTools.aspects(GmEntityTypeAspect.class);
	}

	@Override
	public boolean matches(PropertyDeclaredSelector selector, SelectorContext context)  {
		GmEntityType gmEntityType = context.get(GmEntityTypeAspect.class);

		return hasProperty(gmEntityType, selector.getPropertyName());
	}

	private boolean hasProperty(GmEntityType ge, String propertyName) {
		for (GmProperty p: ge.getProperties()) {
			if (p.getName().equals(propertyName)) {
				return true;
			}
		}

		return false;
	}

}
