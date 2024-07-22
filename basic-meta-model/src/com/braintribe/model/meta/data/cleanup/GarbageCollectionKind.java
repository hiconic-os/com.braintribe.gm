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
package com.braintribe.model.meta.data.cleanup;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

/**
 * Used to specify how GC handles entities of the respective type.
 * 
 * @author michael.lafite
 */
public enum GarbageCollectionKind implements EnumBase<GarbageCollectionKind> {

	/**
	 * This setting marks entities from where the GC starts its reachability walks. <code>anchor</code> entities will
	 * never be {@link #collect collected} by the GC (i.e. same as {@link #hold}).
	 */
	anchor,
	/**
	 * The GC <i>holds</i> all the <code>hold</code> entities, i.e. they will never be {@link #collect collected} by
	 * the GC.
	 */
	hold,
	/**
	 * The GC <i>collects</i> (i.e. removes) all <code>collect</code> entities, unless they are reachable (directly or
	 * indirectly) from one of the {@link #anchor} entities
	 */
	collect;

	public static final EnumType<GarbageCollectionKind> T = EnumTypes.T(GarbageCollectionKind.class);
	
	@Override
	public EnumType<GarbageCollectionKind> type() {
		return T;
	}
}
