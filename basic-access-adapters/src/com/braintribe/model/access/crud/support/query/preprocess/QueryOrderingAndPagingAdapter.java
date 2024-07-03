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
import com.braintribe.model.access.crud.support.query.BasicQueryContext;
import com.braintribe.model.query.Ordering;
import com.braintribe.model.query.Paging;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.Restriction;

public class QueryOrderingAndPagingAdapter implements QueryPreProcessor {

	// ***************************************************************************************************
	// Static Instances & Initializations
	// ***************************************************************************************************

	public static final QueryOrderingAndPagingAdapter REMOVE_PAGING = new QueryOrderingAndPagingAdapter(false,PagingAdaption.REMOVE);
	public static final QueryOrderingAndPagingAdapter REMOVE_ORDERING = new QueryOrderingAndPagingAdapter(true,null);
	public static final QueryOrderingAndPagingAdapter REMOVE_ORDERING_AND_REMOVE_PAGING = new QueryOrderingAndPagingAdapter(true,PagingAdaption.REMOVE);
	public static final QueryOrderingAndPagingAdapter REMOVE_ORDERING_AND_ADAPT_PAGING_TO_FIRST_PAGE = new QueryOrderingAndPagingAdapter(true,PagingAdaption.ADAPT_TO_FIRST_PAGE);
	public static final QueryOrderingAndPagingAdapter ADAPT_PAGING_TO_FIRST_PAGE = new QueryOrderingAndPagingAdapter(false,PagingAdaption.ADAPT_TO_FIRST_PAGE);
	
	private enum PagingAdaption {
		REMOVE, ADAPT_TO_FIRST_PAGE
	}
	
	private boolean removeOrdering;
	private PagingAdaption pagingAdaption;
	
	
	private QueryOrderingAndPagingAdapter(boolean removeOrdering, PagingAdaption pagingAdaption) {
		this.removeOrdering = removeOrdering;
		this.pagingAdaption = pagingAdaption;
	}

	// ***************************************************************************************************
	// Query PreProcessor
	// ***************************************************************************************************

	@Override
	public QueryContext preProcess(QueryContext queryContext) {
		
		Query q = queryContext.getQuery();
		
		Ordering originalOrdering = removeOrdering(q);
		Paging originalPaging = removePaging(q);
		
		return new BasicQueryContext(q) {
			@Override
			public Ordering getOriginalOrdering() {
				return originalOrdering;
			}
			@Override
			public Paging getOriginalPaging() {
				return originalPaging;
			}
		};
	}

	// ***************************************************************************************************
	// Helper
	// ***************************************************************************************************

	private Paging removePaging(Query q) {
		Restriction restriction = q.getRestriction();
		Paging originalPaging = restriction != null ? restriction.getPaging() : null;
		if (pagingAdaption != null && originalPaging != null) {
			
			switch (pagingAdaption) {
				case REMOVE: 
					restriction.setPaging(null);
					break;
				case ADAPT_TO_FIRST_PAGE:
					Paging newPaging = Paging.T.create();
					newPaging.setStartIndex(0);
					newPaging.setPageSize(originalPaging.getPageSize());
					restriction.setPaging(newPaging);
					break;
			}
			
		}
		return originalPaging;
	}

	private Ordering removeOrdering(Query q) {
		Ordering originalOrdering = q.getOrdering();
		if (this.removeOrdering && originalOrdering != null) {
			q.setOrdering(null);
		}
		return originalOrdering;
	}
	
}
