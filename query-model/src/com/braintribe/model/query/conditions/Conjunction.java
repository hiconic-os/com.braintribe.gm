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
package com.braintribe.model.query.conditions;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * An {@link AbstractJunction} where all the contained <code>operands</code> are to be treated as coupled with a logical
 * AND statement.
 */
public interface Conjunction extends AbstractJunction {

	EntityType<Conjunction> T = EntityTypes.T(Conjunction.class);

	@Override
	default ConditionType conditionType() {
		return ConditionType.conjunction;
	}
	
	static Conjunction of(Condition... conditions) {
		Conjunction conjunction = Conjunction.T.create();
		List<Condition> operands = conjunction.getOperands();
		for (Condition condition: conditions) {
			operands.add(condition);
		}
		return conjunction;
	}
	
	@Override
	default Conjunction add(Condition operand) {
		getOperands().add(operand);
		return this;
	}
}
