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
package com.braintribe.model.cache;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

public enum CacheType implements EnumBase {

	/**
	 * {@link #noCache} indicates that returned responses must not be used for subsequent requests to the same resource
	 * before checking if server responses have changed. Therefore a cache control of this type might still include
	 * other validation information, like fingerprint or last modification date.
	 */
	noCache,

	/**
	 * {@link #noStore} indicates that client and all intermediate proxies must not store any version of the resource
	 * whatsoever. Therefore a cache control of this type might still include other validation information, like
	 * fingerprint, which would assist caching mechanisms in purging previously cached versions of the resource.
	 */
	noStore,

	/**
	 * {@link #publicCache} indicates that the resource may be cached by a shared cache (e.g.: intermediate proxies).
	 */
	publicCache,

	/**
	 * {@link #privateCache} indicates that the resource is intended for a single user and MUST NOT be cached by a
	 * shared cache.
	 */
	privateCache;

	public static final EnumType T = EnumTypes.T(CacheType.class);

	@Override
	public EnumType type() {
		return T;
	}

}
