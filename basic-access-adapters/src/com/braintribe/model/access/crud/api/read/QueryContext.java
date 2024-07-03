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
package com.braintribe.model.access.crud.api.read;

import java.util.function.Function;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.query.Ordering;
import com.braintribe.model.query.Paging;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.Restriction;

public interface QueryContext {

	Query getQuery();
	
	<T, A extends QueryContextAspect<T>> QueryContext addAspect(Class<A> aspectClass, T value);
	<T, A extends QueryContextAspect<T>> T findAspect(Class<A> aspectClass);
	
	default Ordering getOriginalOrdering() {
		return getQuery().getOrdering();
	}

	default Paging getOriginalPaging() {
		Restriction r = getQuery().getRestriction();
		return r != null ? r.getPaging() : null;
	}
	
	default <T extends GenericEntity> T acquireEntry(Object id,  Function<? super Object, T> factory) {
		return factory.apply(id); // no caching by default
	}
	
}
