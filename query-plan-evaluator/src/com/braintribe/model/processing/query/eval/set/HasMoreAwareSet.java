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
package com.braintribe.model.processing.query.eval.set;

import com.braintribe.model.processing.query.eval.api.EvalTupleSet;

/**
 * Some {@link EvalTupleSet}s may themselves be responsible for cutting the results due to pagination. Not only
 * {@link EvalPaginatedSet}, but for example in smart evaluator we have DelegateQuerySet, which may delegate the
 * pagination to the underlying access. In either case, the hasMore indicates that this {@link EvalTupleSet}, or some
 * underlying one, has cut the results due to pagination.
 */
public interface HasMoreAwareSet {

	boolean hasMore();

}
