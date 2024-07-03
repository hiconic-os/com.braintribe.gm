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

import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Collection;
import java.util.Set;

import com.braintribe.model.meta.selector.JunctionSelector;
import com.braintribe.model.meta.selector.MetaDataSelector;
import com.braintribe.model.processing.meta.cmd.context.LogicalSelectorExpert;
import com.braintribe.model.processing.meta.cmd.context.MdSelectorResolverImpl;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;

abstract class JunctionSelectorExpert<T extends JunctionSelector> implements LogicalSelectorExpert<T> {

	protected final MdSelectorResolverImpl mdSelectorResolver;

	public JunctionSelectorExpert(MdSelectorResolverImpl mdSelectorResolver) {
		this.mdSelectorResolver = mdSelectorResolver;
	}

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(T selector) throws Exception {
		Set<Class<? extends SelectorContextAspect<?>>> result = newSet();

		for (MetaDataSelector mds : selector.getOperands())
			result.addAll(mdSelectorResolver.getRelevantAspects(mds));

		return result;
	}

}
