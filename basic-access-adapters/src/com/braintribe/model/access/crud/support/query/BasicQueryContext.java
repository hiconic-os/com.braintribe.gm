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

import com.braintribe.model.access.crud.api.read.QueryContext;
import com.braintribe.model.access.crud.api.read.QueryContextAspect;
import com.braintribe.model.query.Query;

public class BasicQueryContext implements QueryContext {
	
	private Query q; 
	protected Map<Class<? extends QueryContextAspect<?>>, Object> sessionAspects = new HashMap<>();  
	
	public BasicQueryContext(Query q) {
		this.q = q;
	}

	@Override
	public Query getQuery() {
		return q;
	}
	
	@Override
	public <T, A extends QueryContextAspect<T>> QueryContext addAspect(Class<A> aspectClass, T value) {
		this.sessionAspects.put(aspectClass, value);
		return this;
	}
	
	@Override
	public <T, A extends QueryContextAspect<T>> T findAspect(Class<A> aspectClass) {
		return (T) this.sessionAspects.get(aspectClass);
	}
	
}
