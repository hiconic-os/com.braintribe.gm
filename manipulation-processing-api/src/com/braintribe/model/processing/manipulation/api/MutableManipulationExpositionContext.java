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
package com.braintribe.model.processing.manipulation.api;

import com.braintribe.model.generic.manipulation.AtomicManipulation;

/**
 * This was deprecated as it does not seem to be usable at all. Typically the code that deals with manipulations defines
 * it's own extension of ManipulationExpositionContext and this interface would then also have to be duplicated, which
 * IMHO leads to a much more complex hierarchy, without any real benefit. At the time this was marked as deprecated,
 * there were no invocation of this method via this interface (in all cases the method was invoked on the implementation
 * class directly).
 */
@Deprecated
public interface MutableManipulationExpositionContext extends ManipulationExpositionContext {

	void setCurrentManipulation(AtomicManipulation currentManipulation) throws ContextInitializationException;
}
