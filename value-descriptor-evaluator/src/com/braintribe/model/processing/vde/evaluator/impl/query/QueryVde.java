// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.vde.evaluator.impl.query;

import com.braintribe.model.bvd.query.Query;
import com.braintribe.model.bvd.query.ResultConvenience;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.session.api.managed.QueryExecution;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeEvaluationMode;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.aspects.SessionAspect;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

/**
 * {@link ValueDescriptorEvaluator} for {@link EntityReference}
 * 
 */
public class QueryVde implements ValueDescriptorEvaluator<Query> {

	private static QueryVde instance = null;

	protected QueryVde() {
		// empty
	}

	public static QueryVde getInstance() {
		if (instance == null) {
			instance = new QueryVde();
		}
		return instance;
	}
	
	@Override
	public VdeResult evaluate(VdeContext context, Query valueDescriptor) throws VdeRuntimeException {
		// get the session
		PersistenceGmSession session = (PersistenceGmSession) context.get(SessionAspect.class);

		if (session == null) {
			if (context.getEvaluationMode() == VdeEvaluationMode.Preliminary) {
				return new VdeResultImpl("No session provided in context");
			}
			throw new VdeRuntimeException("No session provided in context");
		}
		Object query = context.evaluate(valueDescriptor.getQuery());
		
		QueryExecution queryExecution = null;
		if (query instanceof String) {
			queryExecution = session.queryDetached().abstractQuery((String)query);
		} else if (query instanceof com.braintribe.model.query.Query) {
			queryExecution = session.queryDetached().abstractQuery((com.braintribe.model.query.Query) query);
		} else {
			throw new VdeRuntimeException("Invalid query: "+query+" passed");
		}
		
		ResultConvenience resultConvenience = valueDescriptor.getResultConvenience();
		if (resultConvenience == null) {
			resultConvenience = ResultConvenience.list; //Default
		}
		
		Object result = null;
		switch (resultConvenience) {
			case first:
				result = queryExecution.first();
				break;
			case list:
				result = queryExecution.list();
				break;
			case result:
				result = queryExecution.result();
				break;
			case unique:
				result = queryExecution.unique();
				break;
			case value:
				result = queryExecution.value();
				break;
			default:
				throw new IllegalArgumentException("Unsupported result convenience: "+resultConvenience);
			
		}
		
		return new VdeResultImpl(result,false);
	}

}
