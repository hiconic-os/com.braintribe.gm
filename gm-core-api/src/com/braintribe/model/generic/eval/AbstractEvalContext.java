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
package com.braintribe.model.generic.eval;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import com.braintribe.common.attribute.TypeSafeAttribute;
import com.braintribe.common.attribute.TypeSafeAttributeEntry;

@SuppressWarnings("unusable-by-js")
public abstract class AbstractEvalContext<R> implements EvalContext<R> {
	private Map<Class<TypeSafeAttribute<?>>, Object> attributes;

	@Override
	public <A extends TypeSafeAttribute<? super V>, V> void setAttribute(Class<A> attribute, V value) {
		if (attributes == null)
			attributes = new HashMap<>();

		attributes.put((Class<TypeSafeAttribute<?>>) attribute, value);
	}

	@Override
	public <A extends TypeSafeAttribute<V>, V> V getAttribute(Class<A> attribute) {
		return findAttribute(attribute).orElseThrow(() -> new NoSuchElementException("No entry found for attribute: " + attribute.getName()));
	}

	@Override
	public <A extends TypeSafeAttribute<V>, V> Optional<V> findAttribute(Class<A> attribute) {
		return attributes != null ? //
				Optional.ofNullable((V) attributes.get(attribute)) : //
				Optional.empty();
	}

	@Override
	public Stream<TypeSafeAttributeEntry> streamAttributes() {
		return attributes == null ? //
				Stream.empty() : //
				attributes.entrySet().stream()
						.map(e -> TypeSafeAttributeEntry.of((Class<TypeSafeAttribute<Object>>) (Class<?>) e.getKey(), e.getValue()));
	}

	public boolean hasAttributes() {
		return attributes != null && !attributes.isEmpty();
	}

}
