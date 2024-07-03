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
package com.braintribe.model.processing.core.expert.impl;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.core.expert.api.MutableDenotationMap;

/**
 * Implementation of {@link MutableDenotationMap} where values are inherited alongside the type hierarchy.
 * <p>
 * When resolving a value for given {@link EntityType}, a logic similar to how default methods are resolved for java interfaces is used:
 * <ul>
 * <li>If a value was directly associated with given type, it is returned.
 * <li>Otherwise, the value associated with the most specific super-type is returned. Most specific means that all other super-types with an
 * associated value are also super-types of the most specific one
 * <li>If no most-specific super-type with associated value exits {@link IllegalStateException} is thrown.
 * </ul>
 * <p>
 * The thread-safe version is only thread-safe when reading.
 * <p>
 * FYI: When all values for given type are desired, rather than the most specific one, use {@link PolymorphicDenotationMultiMap}.
 * 
 * @see MutableDenotationMap
 * 
 * @author peter.gazdik
 */
@SuppressWarnings("unusable-by-js")
public class PolymorphicDenotationMap<B extends GenericEntity, V> implements MutableDenotationMap<B, V> {

	private final Map<EntityType<? extends B>, V> configuration;
	private final Map<EntityType<? extends B>, Object> cache;

	private static final Object missing = new Object();

	/** Equivalent to {@code new PolymorphicDenotationMap(true)} */
	public PolymorphicDenotationMap() {
		this(true);
	}

	/** Constructor with a parameter to make the map thread-safe. Note the thread-safety is only relevant for reading. */
	public PolymorphicDenotationMap(boolean threadSafe) {
		this.configuration = new IdentityHashMap<>();
		this.cache = createMap(threadSafe);
	}

	private static <K, V> Map<K, V> createMap(boolean threadSafe) {
		return threadSafe ? new ConcurrentHashMap<>() : new IdentityHashMap<>();
	}

	@Override
	public void putAll(Map<EntityType<? extends B>, V> map) {
		for (Entry<EntityType<? extends B>, V> entry : map.entrySet())
			put(entry.getKey(), entry.getValue());
	}

	@Override
	public void put(EntityType<? extends B> denotationType, V value) {
		Objects.requireNonNull(denotationType, "Denotation type cannot be null");
		Objects.requireNonNull(value, "value cannot be null");

		configuration.put(denotationType, value);
		cache.put(denotationType, value);
	}

	/**
	 * Removes the value that was previously "put" for given denotationType. Also, any association created for any sub-type of given type is removed.
	 */
	@Override
	public void remove(EntityType<? extends B> denotationType) {
		if (denotationType != null && configuration.remove(denotationType) != null)
			cache.clear();
	}

	@Override
	public <T extends V> T get(EntityType<? extends B> denotationType) {
		T result = find(denotationType);
		if (result == null)
			throw new NoSuchElementException("No value found for denotation type: " + denotationType.getTypeSignature());

		return result;
	}

	@Override
	public <T extends V> T find(EntityType<? extends B> denotationType) {
		Object result = cache.computeIfAbsent(denotationType, this::findInSuperTypes);
		return result == missing ? null : (T) result;
	}

	private Object findInSuperTypes(EntityType<?> denotationType) {
		EntityType<?> configuredKey = null;
		Object result = null;

		for (EntityType<?> superType : denotationType.getTransitiveSuperTypes(false, true)) {
			Object value = configuration.get(superType);
			if (value == null)
				continue;

			if (configuredKey == null || configuredKey.isAssignableFrom(superType)) {
				configuredKey = superType;
				result = value;

			} else if (!superType.isAssignableFrom(configuredKey)) {
				throw new IllegalStateException("Ambigious value associated with the denotation " + denotationType
						+ ". Value configured for at least two different supertype: '" + configuredKey + "', '" + superType + "'");
			}
		}

		return result == null ? missing : result;
	}

	@Override
	public <T extends V> T get(B denotation) {
		return get(denotation.entityType());
	}

	@Override
	public <T extends V> T find(B denotation) {
		return find(denotation.entityType());
	}

	@Override
	public Stream<Entry<EntityType<? extends B>, V>> entryStream() {
		return configuration.entrySet().stream();
	}

	@Override
	public Stream<V> expertStream() {
		return configuration.values().stream();
	}

	@Override
	public boolean isEmpty() {
		return configuration.isEmpty();
	}

	@Override
	public int configurationSize() {
		return configuration.size();
	}

}
