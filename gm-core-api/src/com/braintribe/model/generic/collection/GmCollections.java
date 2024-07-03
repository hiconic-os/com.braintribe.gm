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
package com.braintribe.model.generic.collection;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * TODO IMPLEMENT
 * 
 * This should be used later as a way to manipulation not-yet-loaded collections without the need to fully load them.
 * 
 * @author peter.gazdik
 */
public interface GmCollections {

	static <E> void add(Collection<? super E> collection, E element) {
		if (collection instanceof LinearCollectionBase)
			((LinearCollectionBase<? super E>) collection).add/* insert */(element);
		else
			collection.add(element);
	}

	static <E> void addAll(Collection<? super E> collection, Collection<? extends E> elements) {
		if (collection instanceof LinearCollectionBase)
			((LinearCollectionBase<? super E>) collection).addAll/* insertAll */(elements);
		else
			collection.addAll(elements);
	}
	
	static <E> void addAll(Collection<? super E> collection, Stream<? extends E> elements) {
		if (collection instanceof LinearCollectionBase)
			elements.forEach(collection::add);
			//((LinearCollectionBase<? super E>) collection).insertAll(elements);
		else
			elements.forEach(collection::add);
	}

}
