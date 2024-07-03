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
package com.braintribe.model.processing.query.support;

import java.util.Collection;
import java.util.Iterator;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.query.eval.api.repo.DelegatingRepository;
import com.braintribe.model.processing.query.eval.api.repo.IndexInfo;
import com.braintribe.model.processing.query.eval.api.repo.IndexingRepository;
import com.braintribe.model.processing.query.eval.api.repo.Repository;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.query.Ordering;
import com.braintribe.model.query.conditions.Condition;

/**
 * This is a wrapper for an actual repository which does it's own
 * 
 * @author peter.gazdik
 */
public class IdentityManagedRepository implements IndexingRepository, DelegatingRepository {

	private final Repository repository;
	private final boolean canAdoptEntities;
	private final DelegatingRepository delegatingRepository;
	private final IndexingRepository indexingRepository;
	private final ManagedGmSession session;

	public IdentityManagedRepository(Repository repository, boolean canAdoptEntities, ManagedGmSession session) {
		this.repository = repository;
		this.canAdoptEntities = canAdoptEntities;
		this.delegatingRepository = repository instanceof DelegatingRepository ? (DelegatingRepository) repository : null;
		this.indexingRepository = repository instanceof IndexingRepository ? (IndexingRepository) repository : null;
		this.session = session;
	}

	// #############################################
	// ## . . . . . Repository methods . . . . . .##
	// #############################################

	@Override
	public Iterable<? extends GenericEntity> providePopulation(String typeSignature) {
		return new MergingIterable<>(repository.providePopulation(typeSignature));
	}

	@Override
	public GenericEntity resolveReference(EntityReference reference) {
		GenericEntity entity = session.query().entity(reference).find();
		if (entity != null)
			return entity;
		else
			return merge(repository.resolveReference(reference));
	}

	@Override
	public String defaultPartition() {
		return repository.defaultPartition();
	}

	// ############################################
	// ## . . Delegating Repository methods . . .##
	// ############################################

	@Override
	public Iterable<? extends GenericEntity> provideEntities(String typeSignature, Condition condition, Ordering ordering) {
		return new MergingIterable<>(delegatingRepository.provideEntities(typeSignature, condition, ordering));
	}

	@Override
	public boolean supportsFulltextSearch() {
		return delegatingRepository.supportsFulltextSearch();
	}

	// ############################################
	// ## . . . IndexingRepository methods . . . ##
	// ############################################

	@Override
	public Collection<? extends GenericEntity> getIndexRange(String indexId, Object from, Boolean fromInclusive, Object to, Boolean toInclusive) {
		return merge(indexingRepository.getIndexRange(indexId, from, fromInclusive, to, toInclusive));
	}

	@Override
	public Collection<? extends GenericEntity> getFullRange(String indexId, boolean reverseOrder) {
		return merge(indexingRepository.getFullRange(indexId, reverseOrder));
	}

	@Override
	public GenericEntity getValueForIndex(String indexId, Object indexValue) {
		return merge(indexingRepository.getValueForIndex(indexId, indexValue));
	}

	@Override
	public Collection<? extends GenericEntity> getAllValuesForIndex(String indexId, Object indexValue) {
		return merge(indexingRepository.getAllValuesForIndex(indexId, indexValue));
	}

	@Override
	public Collection<? extends GenericEntity> getAllValuesForIndices(String indexId, Collection<?> indexValues) {
		return merge(indexingRepository.getAllValuesForIndices(indexId, indexValues));
	}

	// ############################################
	// ## . . . . . . . . Helpers . . . . . . . .##
	// ############################################

	private <T> T merge(T data) {
		if (data == null)
			return null;

		try {
			return session.merge().adoptUnexposed(canAdoptEntities).doFor(data);

		} catch (GmSessionException e) {
			throw new RuntimeException("Merging entities failed!", e);
		}
	}

	@Override
	public IndexInfo provideIndexInfo(String typeSignature, String propertyName) {
		return indexingRepository != null ? indexingRepository.provideIndexInfo(typeSignature, propertyName) : null;
	}

	private class MergingIterable<T> implements Iterable<T> {
		private final Iterable<T> iterable;

		public MergingIterable(Iterable<T> iterable) {
			this.iterable = iterable;
		}

		@Override
		public Iterator<T> iterator() {
			return new MergingIterator<T>(iterable.iterator());
		}
	}

	private class MergingIterator<T> implements Iterator<T> {
		private final Iterator<T> iterator;

		public MergingIterator(Iterator<T> iterator) {
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public T next() {
			T next = iterator.next();
			return merge(next);
		}

		@Override
		public void remove() {
			iterator.remove();
		}
	}
}
