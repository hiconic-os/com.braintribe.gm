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
package com.braintribe.model.processing.session.impl.managed;

import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.session.api.managed.SelectQueryResultConvenience;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;

public class StaticSelectQueryResultConvenience extends AbstractQueryResultConvenience<SelectQuery, SelectQueryResult, SelectQueryResultConvenience> implements SelectQueryResultConvenience{
	private SelectQueryResult result;
	
	public StaticSelectQueryResultConvenience(SelectQuery selectQuery, SelectQueryResult result) {
		super(selectQuery);
		this.result = result;
	}

	@Override
	protected SelectQueryResult resultInternal(SelectQuery query) throws GmSessionException {
		return result;
	}

}
