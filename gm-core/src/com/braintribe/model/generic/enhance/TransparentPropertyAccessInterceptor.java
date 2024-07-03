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
package com.braintribe.model.generic.enhance;

import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;

/**
 * A {@link PropertyAccessInterceptor} that has no side effect. This is simply useful as a first PAI in a chain (like
 * what is configured for a session), thus enabling us to change the chain without changing the first PAI, which might
 * be referenced from elsewhere.
 */
public class TransparentPropertyAccessInterceptor extends PropertyAccessInterceptor {

	// empty

}
