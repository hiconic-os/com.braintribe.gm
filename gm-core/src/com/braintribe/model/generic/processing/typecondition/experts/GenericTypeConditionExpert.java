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
package com.braintribe.model.generic.processing.typecondition.experts;

import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.typecondition.TypeCondition;
import com.braintribe.model.meta.GmType;

/**
 * 
 * @author dirk.scheffler
 * @deprecated use {@link TypeCondition#matches(GenericModelType)} and {@link TypeCondition#matches(GmType)} instead
 */
@Deprecated
public class GenericTypeConditionExpert {
	public static GenericTypeConditionExpert defaultInstance = new GenericTypeConditionExpert();

	public static GenericTypeConditionExpert getDefaultInstance() {
			return defaultInstance;
	}

	public boolean matchesTypeCondition(TypeCondition typeCondition, GenericModelType type) {
		return typeCondition.matches(type);
	}
	
	public boolean matchesTypeCondition(TypeCondition typeCondition, GmType type) {
		return typeCondition.matches(type);
	}

}
