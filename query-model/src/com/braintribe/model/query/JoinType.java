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
package com.braintribe.model.query;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

/**
 * Defines the type of {@link Join} used to combine sources. One can be {@link #inner}, {@link #left}, {@link #right},
 * {@link #full}.
 */
public enum JoinType implements EnumBase {
	/**
	 * All the records from both sources where the records intersect.
	 */
	inner,
	/**
	 * All the records from the left source and only those records from the right source that intersect with the left
	 * source.
	 */
	left,
	/**
	 * All the records from the right source and only those records from the left source that intersect with the right
	 * source.
	 */
	right,
	/**
	 * All the records from both sources.
	 */
	full;

	public static final EnumType T = EnumTypes.T(JoinType.class);
	
	@Override
	public EnumType type() {
		return T;
	}	
}
