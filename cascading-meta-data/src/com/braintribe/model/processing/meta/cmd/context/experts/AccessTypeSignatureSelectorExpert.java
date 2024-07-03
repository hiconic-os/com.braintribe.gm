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

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.selector.AccessTypeSignatureSelector;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.AccessTypeAspect;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataTools;

/**
 */
public class AccessTypeSignatureSelectorExpert implements CmdSelectorExpert<AccessTypeSignatureSelector> {

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(AccessTypeSignatureSelector selector) throws Exception {
		return MetaDataTools.aspects(AccessTypeAspect.class);
	}

	@Override
	public boolean matches(AccessTypeSignatureSelector selector, SelectorContext context) throws Exception {
		return matches(selector.getDenotationTypeSignature(), selector.getAssignable(), context);
	}

	static boolean matches(String selectorSignature, boolean assignable, SelectorContext context) {
		String accessSignature = context.get(AccessTypeAspect.class);

		if (accessSignature == null) {
			return false;
		}

		if (!assignable) {
			return selectorSignature.equals(accessSignature);
		}

		EntityType<GenericEntity> selectorAccessType = GMF.getTypeReflection().getEntityType(selectorSignature);
		EntityType<GenericEntity> actualAccessType = GMF.getTypeReflection().getEntityType(accessSignature);

		return selectorAccessType.isAssignableFrom(actualAccessType);
	}

}
