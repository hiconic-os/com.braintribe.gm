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
package com.braintribe.model.processing.oracle.model.evaluable;

import java.util.List;

import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.processing.meta.oracle.EntityTypeOracle;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * PGA: Originally I wanted this to derive from {@link ListObjectEvaluable}, but that's not possible. I would have to
 * use wildcards as List parameter types, which the JTA doesn't support. So the whole {@link EntityTypeOracle}
 * implementation assumes that is not possible and would such a situation consider an inconsistency.
 */
public interface ListStringEvaluable extends ObjectEvaluable {

	EntityType<ListStringEvaluable> T = EntityTypes.T(ListStringEvaluable.class);

	/** Check that List of Strings can override List of Objects */
	@Override
	EvalContext<? extends List<String>> eval(Evaluator<ServiceRequest> evaluator);

}
