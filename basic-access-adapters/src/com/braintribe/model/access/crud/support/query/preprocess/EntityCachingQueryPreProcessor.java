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
package com.braintribe.model.access.crud.support.query.preprocess;

import com.braintribe.model.access.crud.api.read.QueryContext;
import com.braintribe.model.access.crud.support.query.EntityCachingQueryContext;

public class EntityCachingQueryPreProcessor implements QueryPreProcessor {

	private QueryPreProcessor delegatePreProcessor;
	
	public EntityCachingQueryPreProcessor() {
	
	}
	public EntityCachingQueryPreProcessor(QueryPreProcessor delegatePreProcessor) {
		this.delegatePreProcessor = delegatePreProcessor;
	}
	
	@Override
	public QueryContext preProcess(QueryContext context) {
		if (this.delegatePreProcessor != null) {
			context = this.delegatePreProcessor.apply(context);
		}
		return new EntityCachingQueryContext(context);
	}
	
	
}
