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
package com.braintribe.model.access;

import java.util.Iterator;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;

/**
 * 
 */
public class TypeFilteringIterable implements Iterable<GenericEntity> {

	private final EntityType<?> entityType;
	private final Iterable<GenericEntity> delegateIterable;

	private static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	public TypeFilteringIterable(String typeSignature, Iterable<GenericEntity> delegateIterable) {
		this.entityType = typeReflection.getEntityType(typeSignature);
		this.delegateIterable = delegateIterable;
	}

	@Override
	public Iterator<GenericEntity> iterator() {
		final Iterator<GenericEntity> delegateIterator = delegateIterable.iterator();

		class LocalIterator implements Iterator<GenericEntity> {
			GenericEntity next;

			public LocalIterator() {
				loadNext();
			}

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public GenericEntity next() {
				GenericEntity result = next;
				loadNext();

				return result;
			}

			private void loadNext() {
				while (delegateIterator.hasNext()) {
					GenericEntity entity = delegateIterator.next();

					if (entityType.isInstance(entity)) {
						next = entity;
						return;
					}
				}

				next = null;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		}

		return new LocalIterator();
	}

}
