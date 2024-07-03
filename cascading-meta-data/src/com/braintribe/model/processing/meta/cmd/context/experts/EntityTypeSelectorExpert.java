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

import com.braintribe.model.generic.typecondition.TypeCondition;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.selector.EntitySignatureRegexSelector;
import com.braintribe.model.meta.selector.EntityTypeSelector;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.GmEntityTypeAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.GmEnumTypeAspect;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataTools;

/**
 * @see EntitySignatureRegexSelector
 */
public class EntityTypeSelectorExpert implements SelectorExpert<EntityTypeSelector> {

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(EntityTypeSelector selector) throws Exception {
		return MetaDataTools.aspects(GmEntityTypeAspect.class);
	}

	@Override
	public boolean matches(EntityTypeSelector selector, SelectorContext context) throws Exception {
		GmType gmType = context.get(GmEntityTypeAspect.class);

		if (gmType == null) {
			gmType = context.get(GmEnumTypeAspect.class);
		}

		if (gmType == null) {
			return false;
		}

		TypeCondition typeCondition = selector.getTypeCondition();

		if (typeCondition == null) {
			return false;
		}

		return typeCondition.matches(gmType);
	}
}
