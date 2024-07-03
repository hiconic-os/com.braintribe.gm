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

import com.braintribe.model.meta.selector.NullPropertyDiscriminator;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;

public class NullPropertyDiscriminatorExpert extends SimplePropertyDiscriminatorExpert<NullPropertyDiscriminator, Object> {

	public NullPropertyDiscriminatorExpert() {
		super(Object.class);
	}

	@Override
	public boolean matches(NullPropertyDiscriminator selector, SelectorContext context) throws Exception {
		Object actualValue = getProperty(selector, context);

		/* In case you are wondering, the ^ operator is xor, which is (for booleans) equivalent to !=. For me, however,
		 * the xor is more readable than !=. */
		return (actualValue == null) ^ selector.getInverse();
	}

}
