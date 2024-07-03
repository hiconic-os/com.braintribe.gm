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
package com.braintribe.model.generic.value.type;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.IntegerType;
import com.braintribe.model.generic.reflection.SimpleTypes;

/**
 * A @link{StaticallyTypedDescriptor} that will always be a @Link{Integer}
 *
 */
@Abstract
public interface IntegerDescriptor extends StaticallyTypedDescriptor {

	EntityType<IntegerDescriptor> T = EntityTypes.T(IntegerDescriptor.class);

	@Override
	default IntegerType valueType() {
		return SimpleTypes.TYPE_INTEGER;
	}

}
