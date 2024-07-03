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
package com.braintribe.model.query.conditions;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

/**
 * Defines the type of {@link Condition} used as a restriction on a query.
 */
public enum ConditionType implements EnumBase {

	// comparisons
	/**
	 * Used to compare a <code>String</code> text to a defined Source
	 */
	fulltextComparison,
	/**
	 * Used to compare a value to a value of a property
	 */
	valueComparison,

	// logical
	/**
	 * Used to combine multiple {@link Condition}s, all of which must be true before the result to be returned. This is
	 * the equivalent of an AND operator.
	 */
	conjunction,
	/**
	 * Used to combine multiple {@link Condition}s, one of which must be true before the result to be returned. This is
	 * the equivalent of an OR operator.
	 */
	disjunction,
	/**
	 * Used to negate the functionality of a {@link Condition}.
	 */
	negation;

	public static final EnumType T = EnumTypes.T(ConditionType.class);
	
	@Override
	public EnumType type() {
		return T;
	}

}
