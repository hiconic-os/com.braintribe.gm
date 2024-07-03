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
package com.braintribe.model.meta.data.prompt;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.ExplicitPredicate;
import com.braintribe.model.meta.data.UniversalMetaData;

/**
 * This metadata should be set on an {@link EntityType} which should have its editions auto committed by the client.
 * This means, users would not need to click the Commit button in order to have its manipulations performed.
 * Erasure is {@link ManualCommit}
 *
 */
public interface AutoCommit extends UniversalMetaData, ExplicitPredicate {
	
	EntityType<AutoCommit> T = EntityTypes.T(AutoCommit.class);

}
