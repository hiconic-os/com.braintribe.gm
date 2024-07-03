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

import com.braintribe.model.meta.selector.NegationSelector;
import com.braintribe.model.processing.meta.cmd.context.LogicalSelectorExpert;
import com.braintribe.model.processing.meta.cmd.context.MdSelectorResolverImpl;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;

public class NegationSelectorExpert implements LogicalSelectorExpert<NegationSelector> {

	private final MdSelectorResolverImpl mdSelectorResolver;

	public NegationSelectorExpert(MdSelectorResolverImpl mdSelectorResolver) {
		this.mdSelectorResolver = mdSelectorResolver;
	}

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(NegationSelector selector) throws Exception {
		return mdSelectorResolver.getRelevantAspects(selector.getOperand());
	}

	@Override
	public boolean matches(NegationSelector selector, SelectorContext context) throws Exception {
		return !mdSelectorResolver.matches(selector.getOperand(), context);
	}

	@Override
	public Boolean maybeMatches(NegationSelector selector, SelectorContext context) throws Exception {
		Boolean maybeResult = mdSelectorResolver.maybeMatches(selector.getOperand(), context);
		return maybeResult == null ? null : !maybeResult;
	}

}
