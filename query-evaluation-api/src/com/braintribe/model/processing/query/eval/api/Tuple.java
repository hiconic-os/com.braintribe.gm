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
package com.braintribe.model.processing.query.eval.api;

import com.braintribe.model.queryplan.TupleComponentPosition;
import com.braintribe.model.queryplan.set.TupleSet;

/**
 * 
 */
public interface Tuple {

	/**
	 * Returns a value for given position, which is the index from {@link TupleComponentPosition#getIndex()}.
	 */
	Object getValue(int index);

	/**
	 * Returns a copy of this {@linkplain Tuple} that is not attached to any {@link TupleSet} or anything else. This is
	 * only for optimization purposes. In standard cases, the iterators might always return the same tuple instance,
	 * just internally it's state is changed. As long as we only use the tuples for reading the values right away, this
	 * is perfect. If, however, someone wants to e.g. put these tuples from the iterator into a {@link java.util.Set},
	 * he needs to obtain a "detached" equivalent, i.e. an instance which will not be overwritten by any of the
	 * iterators later.
	 */
	Tuple detachedCopy();
}
