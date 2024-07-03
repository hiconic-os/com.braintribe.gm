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

import java.util.function.Function;

import com.braintribe.model.access.crud.api.read.QueryContext;

public interface QueryPreProcessor extends Function<QueryContext, QueryContext> {
	
	/**
	 * Every preProcessor is expected to provide a context instance.<br/>
	 * This can either be the instance passed in or a new created one.
	 */
	QueryContext preProcess(QueryContext context);

	@Override
	default QueryContext apply(QueryContext context) {
		return preProcess(context);
	}

}
