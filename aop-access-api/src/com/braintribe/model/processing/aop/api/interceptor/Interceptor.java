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
package com.braintribe.model.processing.aop.api.interceptor;

import com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint;

/**
 * Represents an action that may be "attached" to a given method, meaning it's execution is bound on the method
 * execution. For a list of possible methods see: {@link AccessJoinPoint}.
 * 
 * Note that all implementations must be thread-safe.
 * 
 * TODO please someone try to explain this with better words...
 * 
 * @author gunther.schenk, dirk, pit
 */
public interface Interceptor {
	// empty
}
