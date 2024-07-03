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
package com.braintribe.model.generic.manipulation.util;

import java.util.Set;
import java.util.function.Function;

import com.braintribe.cc.lcd.CodingSet;
import com.braintribe.model.generic.commons.EntRefHashingComparator;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.tools.AssemblyTools;
import com.braintribe.model.generic.value.EntityReference;


public class EntityReferenceScanner implements Function<Manipulation, Set<EntityReference>> {

	/**
	 * @deprecated use {@link #findEntityReferences(Manipulation)}
	 */
	@Override
	@Deprecated
	public Set<EntityReference> apply(Manipulation manipulation) throws RuntimeException {
		return EntityReferenceScanner.findEntityReferences(manipulation);
	}

	public static Set<EntityReference> findEntityReferences(Manipulation manipulation) {
		Set<EntityReference> references = AssemblyTools.findAll(manipulation, EntityReference.T, t -> true);
		
		Set<EntityReference> result = CodingSet.create(EntRefHashingComparator.INSTANCE);
		result.addAll(references);
		return result;
	}

}
