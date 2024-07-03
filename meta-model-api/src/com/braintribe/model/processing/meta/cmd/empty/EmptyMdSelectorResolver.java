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
package com.braintribe.model.processing.meta.cmd.empty;

import static java.util.Collections.emptySet;

import java.util.Collection;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.selector.MetaDataSelector;
import com.braintribe.model.processing.meta.cmd.MdSelectorResolver;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.experts.SelectorExpert;

public class EmptyMdSelectorResolver implements MdSelectorResolver {

	public static final EmptyMdSelectorResolver INSTANCE = new EmptyMdSelectorResolver();

	private EmptyMdSelectorResolver() {
	}

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(MetaDataSelector selector) throws Exception {
		return emptySet();
	}

	@Override
	public boolean matches(MetaDataSelector selector, SelectorContext context) throws Exception {
		return false;
	}

	@Override
	public SelectorExpert<?> findExpertFor(EntityType<? extends MetaDataSelector> et) {
		return null;
	}

}
