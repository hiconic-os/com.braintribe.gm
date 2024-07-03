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
package com.braintribe.model.processing.core.expert.api;

import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.GmCoreApiInteropNamespaces;
import com.braintribe.model.generic.reflection.EntityType;

import jsinterop.annotations.JsType;

/**
 * Extension of {@link DenotationMap} interface with methods to modify the map content.
 * 
 * @author peter.gazdik
 */
@JsType(namespace = GmCoreApiInteropNamespaces.util)
public interface MutableDenotationMap<B extends GenericEntity, V> extends DenotationMap<B, V> {

	void put(EntityType<? extends B> denotationType, V value);

	void putAll(Map<EntityType<? extends B>, V> map);

	/**
	 * Removes the value that was previously "put" for given denotationType. Not that the remove only works with the exact same type that the put was
	 * done with, otherwise this operation has no effect.
	 */
	default void remove(EntityType<? extends B> denotationType) {
		throw new UnsupportedOperationException("Method 'MutableDenotationMap.remove' is not supported! Cannot remove entry for: " + denotationType);
	}

}
