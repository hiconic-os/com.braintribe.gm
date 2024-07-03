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
package com.braintribe.model.access.crud.support.query;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.braintribe.model.access.crud.api.read.QueryContext;
import com.braintribe.model.access.crud.api.read.QueryContextAspect;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.query.Ordering;
import com.braintribe.model.query.Paging;
import com.braintribe.model.query.Query;

public class EntityCachingQueryContext implements QueryContext {

	private QueryContext delegateContext = null;
	private Map<Object, GenericEntity> cache = new HashMap<>();
	
	public EntityCachingQueryContext(QueryContext delegateContext) {
		this.delegateContext = delegateContext;
	}
	
	@Override
	public Query getQuery() {
		return delegateContext.getQuery();
	}

	@Override
	public Ordering getOriginalOrdering() {
		return delegateContext.getOriginalOrdering();
	}

	@Override
	public Paging getOriginalPaging() {
		return delegateContext.getOriginalPaging();
	}
	
	@Override
	public <T, A extends QueryContextAspect<T>> QueryContext addAspect(Class<A> aspectClass, T value) {
		return delegateContext.addAspect(aspectClass, value);
	}
	
	@Override
	public <T, A extends QueryContextAspect<T>> T findAspect(Class<A> aspectClass) {
		return delegateContext.findAspect(aspectClass);
	}
	
	
	@Override
	public <T extends GenericEntity> T acquireEntry(Object id,  Function<? super Object, T> factory) {
		return (T) this.cache.computeIfAbsent(id, factory);
	}
	
	
}
