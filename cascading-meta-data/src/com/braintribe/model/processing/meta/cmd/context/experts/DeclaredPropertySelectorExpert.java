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

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.selector.DeclaredPropertySelector;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.GmEntityTypeAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.GmPropertyAspect;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataTools;

/**
 * @see DeclaredPropertySelector
 */
public class DeclaredPropertySelectorExpert implements SelectorExpert<DeclaredPropertySelector> {

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(DeclaredPropertySelector selector) throws Exception {
		return MetaDataTools.aspects(GmEntityTypeAspect.class, GmPropertyAspect.class);
	}

	@Override
	public boolean matches(DeclaredPropertySelector selector, SelectorContext context) throws Exception {
		GmEntityType gmEntityType = context.get(GmEntityTypeAspect.class);
		GmProperty gmProperty = context.get(GmPropertyAspect.class);

		if (gmEntityType == null || gmProperty == null)
			return false;

		return gmProperty.getDeclaringType() == gmEntityType;
	}

}
