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
package com.braintribe.model.processing.meta.cmd.context.experts;

import java.util.Collection;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.selector.PropertyOfSelector;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.GmPropertyAspect;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataTools;
import com.braintribe.model.processing.meta.oracle.EntityTypeOracle;
import com.braintribe.model.processing.meta.oracle.PropertyOracle;

/**
 * @see PropertyOfSelector
 */
public class PropertyOfSelectorExpert implements SelectorExpert<PropertyOfSelector> {

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(PropertyOfSelector selector) throws Exception {
		return MetaDataTools.aspects(GmPropertyAspect.class);
	}

	@Override
	public boolean matches(PropertyOfSelector selector, SelectorContext context) throws Exception {
		GmProperty contextProperty = context.get(GmPropertyAspect.class);
		if (contextProperty == null)
			return false;

		GmEntityType selectorEntityType = selector.getEntityType();

		// we assume the entityTypeOracle must exist
		EntityTypeOracle entityTypeOracle = context.getModelOracle().getTypeOracle(selectorEntityType);

		PropertyOracle propertyOracle = entityTypeOracle.findProperty(contextProperty);
		if (propertyOracle == null)
			return false;

		GmProperty gmProperty = propertyOracle.asGmProperty();
		if (gmProperty.getType() != contextProperty.getType())
			return false;

		return selector.getOnlyDeclared() ? gmProperty.getDeclaringType() == selectorEntityType : true;
	}

}
