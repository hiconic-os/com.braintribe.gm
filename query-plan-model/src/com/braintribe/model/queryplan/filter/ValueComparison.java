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
package com.braintribe.model.queryplan.filter;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.queryplan.value.Value;

/**
 * 
 * represents a filter that is comparing two values
 * 
 * depending on the kind of the filter, the property path may be used or not.
 * 
 * if property path's set however, the addressedAlias must denote a valid entity.
 * 
 * @author pit & dirk
 *
 */
@Abstract
public interface ValueComparison extends Condition {

	EntityType<ValueComparison> T = EntityTypes.T(ValueComparison.class);

	Value getLeftOperand();
	void setLeftOperand(Value leftOperand);

	Value getRightOperand();
	void setRightOperand(Value rightOperand);

}
