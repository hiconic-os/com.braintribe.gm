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

import com.braintribe.model.meta.selector.DisjunctionSelector;
import com.braintribe.model.meta.selector.MetaDataSelector;
import com.braintribe.model.processing.meta.cmd.context.MdSelectorResolverImpl;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;

public class DisjunctionSelectorExpert extends JunctionSelectorExpert<DisjunctionSelector> {

	public DisjunctionSelectorExpert(MdSelectorResolverImpl mdSelectorResolver) {
		super(mdSelectorResolver);
	}

	@Override
	public boolean matches(DisjunctionSelector selector, SelectorContext context) throws Exception {
		for (MetaDataSelector mds : selector.getOperands())
			if (mdSelectorResolver.matches(mds, context))
				return true;

		return false;
	}

	@Override
	public Boolean maybeMatches(DisjunctionSelector selector, SelectorContext context) throws Exception {
		boolean nullEncountered = false;

		for (MetaDataSelector mds : selector.getOperands()) {
			Boolean maybeMatches = mdSelectorResolver.maybeMatches(mds, context);

			if (maybeMatches == null)
				nullEncountered = true;
			else if (maybeMatches)
				return true;
		}

		return nullEncountered ? null : false;
	}

}
