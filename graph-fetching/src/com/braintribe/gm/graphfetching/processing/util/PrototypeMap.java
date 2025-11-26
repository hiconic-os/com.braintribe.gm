package com.braintribe.gm.graphfetching.processing.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PrototypeMap implements Map<Object, Object> {
	private Set<Object> keys = new HashSet<>();
	private Set<Object> values = new HashSet<>();

	@Override
	public int size() {
		return keys.size() + values.size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return keys.contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return values.contains(value);
	}

	@Override
	public Object get(Object key) {
		return null;
	}

	@Override
	public Object put(Object key, Object value) {
		keys.add(key);
		values.add(value);
			
		return null;
	}

	@Override
	public Object remove(Object key) {
		return keys.remove(key);
	}

	@Override
	public void putAll(Map<? extends Object, ? extends Object> m) {
		for (Object key: m.keySet())
			keys.add(key);
		
		for (Object value: m.values())
			values.add(value);
	}

	@Override
	public void clear() {
		keys.clear();
		values.clear();
	}

	@Override
	public Set<Object> keySet() {
		return keys;
	}

	@Override
	public Collection<Object> values() {
		return values;
	}

	@Override
	public Set<Entry<Object, Object>> entrySet() {
		Set<Entry<Object, Object>> entries = new HashSet<>();
		
		for (Object key: keys)
			entries.add(new PrototypeEntry(key, null));
		
		for (Object value: values)
			entries.add(new PrototypeEntry(null, value));
		
		return entries;
	}
	
	private static class PrototypeEntry implements Map.Entry<Object,Object> {
		private Object key;
		private Object value;
		
		public PrototypeEntry(Object key, Object value) {
			super();
			this.key = key;
			this.value = value;
		}

		@Override
		public int hashCode() {
			return Objects.hash(key, value);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PrototypeEntry other = (PrototypeEntry) obj;
			return Objects.equals(key, other.key) && Objects.equals(value, other.value);
		}

		@Override
		public Object getKey() {
			return key;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public Object setValue(Object value) {
			throw new UnsupportedOperationException("PrototypMap Entry does not allow value change");
		}
	}

	
}
