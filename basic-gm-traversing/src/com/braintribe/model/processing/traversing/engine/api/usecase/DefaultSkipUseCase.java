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
package com.braintribe.model.processing.traversing.engine.api.usecase;

import com.braintribe.model.processing.traversing.api.SkipUseCase;

//default as in use the cloned value whatever it is
/**
 * If a skip occurs, the default cloned value of the property/list/set/.. will be the same
 *
 */
public class DefaultSkipUseCase implements SkipUseCase {

	public static final DefaultSkipUseCase INSTANCE = new DefaultSkipUseCase();

	private DefaultSkipUseCase() {
	}

}
