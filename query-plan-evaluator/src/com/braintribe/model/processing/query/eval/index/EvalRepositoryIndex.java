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
package com.braintribe.model.processing.query.eval.index;

import java.util.Collection;

import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.api.repo.IndexingRepository;
import com.braintribe.model.processing.query.eval.tools.TupleIterable;
import com.braintribe.model.queryplan.index.RepositoryIndex;

/**
 * 
 */
public class EvalRepositoryIndex implements EvalIndex {

	protected final IndexingRepository repository;
	protected final String indexId;
	protected final int componentIndex;

	public EvalRepositoryIndex(IndexingRepository repository, RepositoryIndex repositoryIndex) {
		this.repository = repository;
		this.indexId = repositoryIndex.getIndexId();
		this.componentIndex = repositoryIndex.getTupleComponentIndex();
	}

	@Override
	public Iterable<Tuple> getAllValuesForIndex(Object indexValue) {
		return new TupleIterable(componentIndex, repository.getAllValuesForIndex(indexId, indexValue));
	}

	@Override
	public Iterable<Tuple> getAllValuesForIndices(Collection<?> indexValues) {
		return new TupleIterable(componentIndex, repository.getAllValuesForIndices(indexId, indexValues));
	}

}
