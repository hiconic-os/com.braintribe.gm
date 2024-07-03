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
import com.braintribe.model.processing.session.api.managed.PropertyQueryResultConvenience;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.PropertyQueryResult;

public class StaticPropertyQueryResultConvenience extends AbstractQueryResultConvenience<PropertyQuery, PropertyQueryResult, PropertyQueryResultConvenience> implements PropertyQueryResultConvenience{
	private PropertyQueryResult result;
	
	public StaticPropertyQueryResultConvenience(PropertyQuery propertyQuery, PropertyQueryResult result) {
		super(propertyQuery);
		this.result = result;
	}

	@Override
	protected PropertyQueryResult resultInternal(PropertyQuery query) throws GmSessionException {
		return result;
	}
	
}
