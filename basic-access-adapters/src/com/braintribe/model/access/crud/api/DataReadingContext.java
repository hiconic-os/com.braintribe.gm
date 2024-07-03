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
package com.braintribe.model.access.crud.api;

import com.braintribe.model.access.crud.api.read.EntityReadingContext;
import com.braintribe.model.access.crud.api.read.PopulationReadingContext;
import com.braintribe.model.access.crud.api.read.PropertyReadingContext;
import com.braintribe.model.access.crud.api.read.QueryContext;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.query.Ordering;
import com.braintribe.model.query.Paging;
import com.braintribe.model.query.Query;

/**
 * Base type for all context objects passed to individual {@link DataReader}
 * implementations.
 * 
 * @see EntityReadingContext
 * @see PopulationReadingContext
 * @see PropertyReadingContext
 * 
 * @author gunther.schenk
 */
public interface DataReadingContext<T extends GenericEntity> extends CrudExpertContext<T> {
	
	QueryContext getQueryContext();
	
	default boolean hasQueryContext() {
		return getQueryContext() != null;
	}
	
	default Query originalQuery() {
		return hasQueryContext() ? getQueryContext().getQuery() : null;
	}
	
	default Ordering originalOrdering() {
		return hasQueryContext() ? getQueryContext().getOriginalOrdering() : null;
	}
	
	default Paging originalPaging() {
		return hasQueryContext() ? getQueryContext().getOriginalPaging() : null;
	}

}
