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
package com.braintribe.model.processing.meta.cmd.context.experts;

import java.util.Collection;

import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.selector.PropertyRegexSelector;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.GmPropertyAspect;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataTools;

/**
 * @see PropertyRegexSelector
 */
public class PropertyRegexSelectorExpert implements SelectorExpert<PropertyRegexSelector> {

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(PropertyRegexSelector selector) throws Exception {
		return MetaDataTools.aspects(GmPropertyAspect.class);
	}

	@Override
	public boolean matches(PropertyRegexSelector selector, SelectorContext context) throws Exception {
		GmProperty gmProperty = context.get(GmPropertyAspect.class);

		if (gmProperty == null)
			return false;

		String propertyName = gmProperty.getName();

		if (selector.getUseFullyQualifiedName())
			propertyName = gmProperty.getDeclaringType().getTypeSignature() + "#" + propertyName;

		return propertyName.matches(selector.getRegex());
	}

}
