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
package com.braintribe.model.queryplan.set;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.queryplan.value.TupleComponent;

/**
 * 
 * @see IndexRange
 * @see IndexSubSet
 * 
 */
@Abstract
public interface IndexSet extends ReferenceableTupleSet {

	EntityType<IndexSet> T = EntityTypes.T(IndexSet.class);

	/**
	 * @return signature of entity which owns the index (one of it's properties is indexed). This information is needed when resolving type
	 *         information for given {@link TupleComponent}.
	 */
	String getTypeSignature();
	void setTypeSignature(String typeSignature);

	/**
	 * @return name of the indexed property; This information is not really needed, might be usable for debugging purposes.
	 */
	String getPropertyName();
	void setPropertyName(String propertyName);

}
