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
import java.util.Collections;
import java.util.Set;

import com.braintribe.model.meta.selector.RoleSelector;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.RoleAspect;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataTools;

/** The selector is active iff some role from the context is contained in roles appended to the selector. */
public class RoleSelectorExpert implements CmdSelectorExpert<RoleSelector> {

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(RoleSelector selector) throws Exception {
		return MetaDataTools.aspects(RoleAspect.class);
	}

	@Override
	public boolean matches(RoleSelector selector, SelectorContext context) throws Exception {
		Set<String> roles = context.get(RoleAspect.class);
		Set<String> selectorRoles = selector.getRoles();

		return roles != null && selectorRoles != null && !Collections.disjoint(roles, selectorRoles);
	}

}
