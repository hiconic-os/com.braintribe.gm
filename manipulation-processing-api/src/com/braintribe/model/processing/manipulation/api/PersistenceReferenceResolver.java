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
package com.braintribe.model.processing.manipulation.api;

import static com.braintribe.utils.lcd.CollectionTools2.acquireList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.value.PersistentEntityReference;

/**
 * @author peter.gazdik
 */
public interface PersistenceReferenceResolver {

	/**
	 * Returns a coding map from {@link PersistentEntityReference} to the corresponding entity. Not that returned
	 * {@link PersistentEntityReference}s, i.e. the keys in the returned map, don't have to be the same instances as
	 * references given on input. The fact that the method returns a coding map ensures though that the lookup will work
	 * with the original references anyway.
	 */
	Map<PersistentEntityReference, GenericEntity> resolve(Iterable<PersistentEntityReference> references);

	/**
	 * Optimized version of {@link #resolve(Iterable)}, which already gets the references of one type only, thus it
	 * doesn't need to group the references.
	 * <p>
	 * However, for convenience it is possible to pass null as typeSignature, it which case this is equivalent to
	 * calling {@code resolve(references)}.
	 */
	Map<PersistentEntityReference, GenericEntity> resolve(String typeSignature, Iterable<PersistentEntityReference> references);

	static Map<String, List<PersistentEntityReference>> groupReferences(Iterable<PersistentEntityReference> references) {
		Map<String, List<PersistentEntityReference>> result = newMap();

		for (PersistentEntityReference ref : references)
			acquireList(result, ref.getTypeSignature()).add(ref);

		return result;
	}

}
