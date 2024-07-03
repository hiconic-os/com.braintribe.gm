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
package com.braintribe.model.processing.manipulation.basic.oracle;

import static com.braintribe.model.processing.manipulation.api.PersistenceReferenceResolver.groupReferences;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.braintribe.cc.lcd.CodingMap;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.commons.EntRefHashingComparator;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.processing.manipulation.api.PersistenceReferenceResolver;
import com.braintribe.utils.lcd.CollectionTools2;

/**
 * @author peter.gazdik
 */
public abstract class AbstractReferenceResolver implements PersistenceReferenceResolver {

	protected final int bulkSize;

	public AbstractReferenceResolver(int bulkSize) {
		this.bulkSize = bulkSize;
	}

	@Override
	public Map<PersistentEntityReference, GenericEntity> resolve(Iterable<PersistentEntityReference> references) {
		Map<PersistentEntityReference, GenericEntity> result = CodingMap.create(EntRefHashingComparator.INSTANCE);

		Map<String, List<PersistentEntityReference>> groupedRefs = groupReferences(references);

		for (Entry<String, List<PersistentEntityReference>> e : groupedRefs.entrySet())
			result.putAll(doResolve(e.getKey(), e.getValue()));

		return result;
	}

	@Override
	public Map<PersistentEntityReference, GenericEntity> resolve(String typeSignature, Iterable<PersistentEntityReference> references) {
		return typeSignature == null ? resolve(references) : doResolve(typeSignature, references);
	}

	private Map<PersistentEntityReference, GenericEntity> doResolve(String typeSignature, Iterable<PersistentEntityReference> references) {

		Map<PersistentEntityReference, GenericEntity> result = CodingMap.create(EntRefHashingComparator.INSTANCE);

		List<Set<PersistentEntityReference>> bulks = CollectionTools2.splitToSets(references, bulkSize);

		for (Set<PersistentEntityReference> bulk : bulks)
			result.putAll(resolveBulk(typeSignature, bulk));

		return result;
	}

	protected abstract Map<PersistentEntityReference, GenericEntity> resolveBulk(String typeSignature, Set<PersistentEntityReference> references);

}
