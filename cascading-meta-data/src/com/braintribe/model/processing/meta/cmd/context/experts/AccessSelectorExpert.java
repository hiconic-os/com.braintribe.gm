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

import com.braintribe.model.meta.selector.AccessSelector;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.AccessAspect;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataTools;

/**
 */
public class AccessSelectorExpert implements CmdSelectorExpert<AccessSelector> {

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(AccessSelector selector) throws Exception {
		return MetaDataTools.aspects(AccessAspect.class);
	}

	@Override
	public boolean matches(AccessSelector selector, SelectorContext context) throws Exception {
		String externalId = context.get(AccessAspect.class);

		return externalId != null && externalId.equals(selector.getExternalId());
	}

}
