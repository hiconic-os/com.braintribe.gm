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
package com.braintribe.model.processing.meta.cmd.context.aspects;

import com.braintribe.model.meta.data.constraint.Mandatory;
import com.braintribe.model.meta.data.constraint.Unmodifiable;
import com.braintribe.model.processing.meta.cmd.context.scope.ScopedAspect.VolatileScopedAspect;

/**
 * Whether or not we should consider the entity preliminary, or already persisted.
 * <p>
 * For some MD this might make a difference. The prime example is {@link Unmodifiable}, which for preliminary entities
 * and properties marked as {@link Mandatory} still resolves as if the property was modifiable.
 */
public class IsEntityPreliminaryAspect extends VolatileScopedAspect<Boolean> {
	// blank
}
