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
package com.braintribe.model.processing.manipulation.basic.normalization;

import java.util.Set;

import com.braintribe.cc.lcd.CodingSet;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.manipulation.basic.BasicMutableManipulationContext;

/**
 * 
 */
class NormalizationContext {

	final boolean globalRefs;
	final Set<EntityReference> entitiesToDelete = CodingSet.create(ElementHashingComparator.INSTANCE);
	final BasicMutableManipulationContext manipulationContext = new BasicMutableManipulationContext();

	public NormalizationContext(boolean globalRefs) {
		this.globalRefs = globalRefs;
	}

	public boolean isIdentifier(Property p) {
		return globalRefs ? p.isGlobalId() : p.isIdentifier();
	}

	public boolean isIdentifying(Property p) {
		return globalRefs ? p.isGlobalId() : p.isIdentifying();
	}

}
