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
package com.braintribe.model.generic.path;

import java.util.Map;

public class ProxyMapEntry<K, V> implements Map.Entry<K, V> {
	private Map<K, V> map;
	private K key;
	
	public ProxyMapEntry(Map<K, V> map, K key) {
		super();
		this.map = map;
		this.key = key;
	}
	
	@Override
	public K getKey() {
		return key;
	}
	
	@Override
	public V setValue(V value) {
		return map.put(key, value);
	}
	
	@Override
	public V getValue() {
		return map.get(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		ProxyMapEntry<K, V> other = (ProxyMapEntry<K, V>) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
}
