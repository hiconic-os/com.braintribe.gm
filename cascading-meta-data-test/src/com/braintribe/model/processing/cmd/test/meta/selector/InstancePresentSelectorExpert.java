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
package com.braintribe.model.processing.cmd.test.meta.selector;

import java.util.Collection;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.EntityAspect;
import com.braintribe.model.processing.meta.cmd.context.experts.SelectorExpert;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataTools;

public class InstancePresentSelectorExpert implements SelectorExpert<InstancePresentSelector> {

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(InstancePresentSelector selector)  {
		return MetaDataTools.aspects(EntityAspect.class);
	}

	@Override
	public boolean matches(InstancePresentSelector selector, SelectorContext context)  {
		GenericEntity ge = context.get(EntityAspect.class);

		return ge != null;
	}

}
