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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.braintribe.model.processing.meta.cmd.CmdResolverImpl;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;

public class ScopeUtils {

	private static final Map<Class<? extends ScopedAspect<?>>, CmdScope> scopeMap;

	static {
		scopeMap = new HashMap<Class<? extends ScopedAspect<?>>, CmdScope>();

		for (CmdScope cs: CmdScope.values()) {
			scopeMap.put(cs.getScope(), cs);
		}
	}

	private boolean singleSession = false;

	/** @See {@link CmdResolverImpl#setSingleSession(boolean)} */
	public void setSingleSession(boolean singleSession) {
		this.singleSession = singleSession;
	}

	/**
	 * @return the most stable scope possible that does not violate the scopes of given aspects (i.e. if something is
	 *         static, we may still handle it as session (the result will always be valid), but not vice versa.)
	 */
	public CmdScope getCommonScope(Collection<Class<? extends SelectorContextAspect<?>>> aspects) {
		CmdScope result = CmdScope.STATIC;

		for (Class<? extends SelectorContextAspect<?>> aspect: aspects) {
			CmdScope cs = getScope(aspect);
			if (cs == CmdScope.VOLATILE) {
				return CmdScope.VOLATILE;
			}

			result = commonScope(result, cs);
		}

		return result;
	}

	private CmdScope commonScope(CmdScope st1, CmdScope st2) {
		return ScopeComparator.commonScope(st1, st2);
	}

	public boolean isStatic(Collection<Class<? extends SelectorContextAspect<?>>> aspects) {
		for (Class<? extends SelectorContextAspect<?>> aspect: aspects) {
			CmdScope cs = getScope(aspect);
			if (cs != CmdScope.STATIC) {
				return false;
			}
		}

		return true;
	}

	private static final CmdScope DEFAULT_SCOPE = CmdScope.VOLATILE;

	private CmdScope getScope(Class<? extends SelectorContextAspect<?>> s) {
		Class<?> sc = s.getSuperclass();

		if (sc != null) {
			CmdScope result = scopeMap.get(sc);
			if (result != null) {
				if (singleSession && result == CmdScope.SESSION) {
					return CmdScope.STATIC;
				}

				return result;
			}
		}

		return DEFAULT_SCOPE;
	}

}
