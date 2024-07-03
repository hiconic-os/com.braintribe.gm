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
import java.util.Set;

import com.braintribe.model.meta.selector.UseCaseSelector;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.UseCaseAspect;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataTools;

/**
 * Expert for the {@link UseCaseSelector}. The selector is active if it's use-case value is contained within the ones
 * currently specified in the context.
 */
public class UseCaseSelectorExpert implements CmdSelectorExpert<UseCaseSelector> {

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(UseCaseSelector selector) throws Exception {
		return MetaDataTools.aspects(UseCaseAspect.class);
	}

	@Override
	public boolean matches(UseCaseSelector selector, SelectorContext context) throws Exception {
		Set<String> useCases = context.get(UseCaseAspect.class);

		return useCases != null && useCases.contains(selector.getUseCase());
	}

}
