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
package com.braintribe.model.processing.meta.cmd.tools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.braintribe.model.processing.meta.cmd.index.LRUMap;

/** Utility class for all code that would not work in GWT (must be emulated). */
public class CmdGwtUtils {

	public static <K, V> Map<K, V> newCacheMap() {
		return new ConcurrentHashMap<K, V>();
	}

	public static <K, V> Map<K, V> newWeakCacheMap(int maxSize) {
		return new LRUMap<K, V>(maxSize);
	}

	/**
	 * In GWT, this only works iff <tt>clazz</tt> is really a class, not an interface..
	 */
	public static boolean isInstanceOf(Class<?> clazz, Object o) {
		return clazz.isInstance(o);
	}

	public static <T> T cast(Class<T> clazz, Object o) {
		return clazz.cast(o);
	}

	public static <T> Class<? extends T> asSubclass(Class<?> c, Class<T> clazz) {
		return c.asSubclass(clazz);
	}

}
