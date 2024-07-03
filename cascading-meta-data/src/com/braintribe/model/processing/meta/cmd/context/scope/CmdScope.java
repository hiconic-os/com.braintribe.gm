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
package com.braintribe.model.processing.meta.cmd.context.scope;

import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.scope.ScopedAspect.MomentaryScopedAspect;
import com.braintribe.model.processing.meta.cmd.context.scope.ScopedAspect.SessionScopedAspect;
import com.braintribe.model.processing.meta.cmd.context.scope.ScopedAspect.StaticScopedAspect;
import com.braintribe.model.processing.meta.cmd.context.scope.ScopedAspect.VolatileScopedAspect;

/**
 * Scope for given {@link SelectorContextAspect}
 * 
 * @see StaticScopedAspect
 * @see SessionScopedAspect
 * @see MomentaryScopedAspect
 * @see VolatileScopedAspect
 */
public enum CmdScope {

	STATIC(StaticScopedAspect.class),
	SESSION(SessionScopedAspect.class),
	MOMENTARY(MomentaryScopedAspect.class),
	VOLATILE(VolatileScopedAspect.class);

	private Class<? extends ScopedAspect<?>> scope;

	private <T extends ScopedAspect<T>> CmdScope(Class<T> scope) {
		this.scope = scope;
	}

	public Class<? extends ScopedAspect<?>> getScope() {
		return scope;
	}

}
