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
package com.braintribe.model.csa;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * {@link SmoodInitializer} that is resolved via the configured resolver. This is a mechanism to define any sort of
 * initializer by the client. Of course, the client might also extend this
 * 
 * @author peter.gazdik
 */
@Abstract
public interface CustomInitializer extends SmoodInitializer {

	EntityType<CustomInitializer> T = EntityTypes.T(CustomInitializer.class);

}
