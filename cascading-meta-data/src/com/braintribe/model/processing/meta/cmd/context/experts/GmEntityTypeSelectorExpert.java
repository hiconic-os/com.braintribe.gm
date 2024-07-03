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
import com.braintribe.model.meta.selector.EntitySignatureRegexSelector;
import com.braintribe.model.meta.selector.GmEntityTypeSelector;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.GmEntityTypeAspect;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataTools;

/**
 * @see EntitySignatureRegexSelector
 */
public class GmEntityTypeSelectorExpert implements SelectorExpert<GmEntityTypeSelector> {

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(GmEntityTypeSelector selector) throws Exception {
		return MetaDataTools.aspects(GmEntityTypeAspect.class);
	}

	@Override
	public boolean matches(GmEntityTypeSelector selector, SelectorContext context) throws Exception {
		GmEntityType gmEntityType = context.get(GmEntityTypeAspect.class);

		return gmEntityType == selector.getGmEntityType();
	}

}
