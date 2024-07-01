// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
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
		if (attributes == null) {
			attributes = new HashMap<>();
		}

		attributes.put((Class<TypeSafeAttribute<?>>) attribute, value);
	}
	
	@Override
	public <A extends TypeSafeAttribute<V>, V> V getAttribute(Class<A> attribute) {
		return findAttribute(attribute).orElseThrow(() -> new NoSuchElementException("No entry found for attribute: " + attribute.getName()));
	}
	
	@Override
	public <A extends TypeSafeAttribute<V>, V> Optional<V> findAttribute(Class<A> attribute) {
		return attributes != null?
				Optional.ofNullable((V)attributes.get(attribute)):
				Optional.empty();
	}
	
	@Override
	public Stream<TypeSafeAttributeEntry> streamAttributes() {
		return attributes != null?
				attributes.entrySet().stream().map(e -> TypeSafeAttributeEntry.of((Class<TypeSafeAttribute<Object>>)(Class<?>)e.getKey(), e.getValue())):
				Stream.empty();
	}
	
	public boolean hasAttributes() {
		return attributes != null? 
				attributes.isEmpty():
				false;
	}

}
