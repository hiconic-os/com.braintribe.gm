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
package com.braintribe.model.processing.security.query;

import com.braintribe.model.generic.typecondition.TypeCondition;
import com.braintribe.model.processing.security.query.expert.PostQueryExpert;

/**
 * One configuration entry for {@link PostQueryExpert} used during post-processing of a query (i.e. the configuration
 * consists of a set of these entries). This entry specifies a {@link TypeCondition} and a {@linkplain PostQueryExpert}
 * which together tell which experts should be applied for which entity types.
 * <p>
 * The query post processing is a process that creates a copy of a query result, but may remove some parts of it, that
 * may be resolved as not being visible.
 * 
 */
public class PostQueryExpertConfiguration {

	private TypeCondition typeCondition;
	private PostQueryExpert expert;

	public TypeCondition getTypeCondition() {
		return typeCondition;
	}

	public void setTypeCondition(TypeCondition typeCondition) {
		this.typeCondition = typeCondition;
	}

	public PostQueryExpert getExpert() {
		return expert;
	}

	public void setExpert(PostQueryExpert expert) {
		this.expert = expert;
	}

}
