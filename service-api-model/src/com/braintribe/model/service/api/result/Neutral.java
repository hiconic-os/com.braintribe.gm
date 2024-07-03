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
package com.braintribe.model.service.api.result;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * <p>
 * In case a {@link ServiceRequest} does not evaluate to a meaningful value it can evaluate to {@link Neutral}. <br>
 * {@link Neutral} acts as a replacement for Java's void and is a valid GM value.
 *
 */
public enum Neutral implements EnumBase {
	NEUTRAL;
	
	public static final EnumType T = EnumTypes.T(Neutral.class);

	@Override
	public EnumType type() {
		return T;
	}
	
}
