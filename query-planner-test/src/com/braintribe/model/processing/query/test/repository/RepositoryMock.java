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
package com.braintribe.model.processing.query.test.repository;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.query.eval.api.repo.IndexInfo;
import com.braintribe.model.processing.query.eval.api.repo.IndexingRepository;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.smood.population.info.IndexInfoImpl;

/**
 * 
 */
public class RepositoryMock implements IndexingRepository {

	private final Map<String, Map<String, IndexInfo>> indexInfoMap;

	private final IndexInfo globalIdIndexInfo_GenericEntity = newGlobalIdIndexInfo(GenericEntity.T);
	private final IndexInfo globalIdIndexInfo_Person = newGlobalIdIndexInfo(Person.T);

	private static IndexInfo newGlobalIdIndexInfo(EntityType<?> entityType) {
		IndexInfoImpl result = new IndexInfoImpl();
		result.setEntitySignature(entityType.getTypeSignature());
		result.setIndexId("globalIdIndex");
		result.setPropertyName(GenericEntity.globalId);
		result.setHasMetric(false);

		return result;
	}

	public RepositoryMock(final IndexConfiguration indexConfiguration) {
		this.indexInfoMap = indexConfiguration.indexInfoMap;
	}

	@Override
	public Collection<? extends GenericEntity> getIndexRange(String indexId, Object from, Boolean fromInclusive, Object to, Boolean toInclusive) {
		return null;
	}

	@Override
	public Collection<? extends GenericEntity> getFullRange(String indexId, boolean reverseOrder) {
		return null;
	}

	@Override
	public Collection<? extends GenericEntity> providePopulation(String typeSignature) {
		return null;
	}

	@Override
	public GenericEntity getValueForIndex(String indexId, Object indexValue) {
		return null;
	}

	@Override
	public Collection<? extends GenericEntity> getAllValuesForIndex(String indexId, Object indexValue) {
		return null;
	}

	@Override
	public Set<? extends GenericEntity> getAllValuesForIndices(String indexId, Collection<?> indexValues) {
		return null;
	}

	@Override
	public IndexInfo provideIndexInfo(String typeSignature, String propertyName) {
		// this is the exact way how we do this in PopulationManager in Smood
		if (GenericEntity.globalId.equals(propertyName))
			if (isPerson(typeSignature))
				return globalIdIndexInfo_Person;
			else
				return globalIdIndexInfo_GenericEntity;

		Map<String, IndexInfo> indicesForEntity = indexInfoMap.get(typeSignature);
		return indicesForEntity != null ? indicesForEntity.get(propertyName) : null;
	}

	private boolean isPerson(String typeSignature) {
		EntityType<GenericEntity> et = GMF.getTypeReflection().getEntityType(typeSignature);
		return Person.T.isAssignableFrom(et);
	}

	@Override
	public GenericEntity resolveReference(EntityReference reference) {
		throw new UnsupportedOperationException(
				"Method 'RepositoryMock.resolveReference' is not implemented yet! Till now it was not needed anyway, I wonder what you are doing...");
	}

	@Override
	public String defaultPartition() {
		return null; // this is correct, null means no default value
	}

}
