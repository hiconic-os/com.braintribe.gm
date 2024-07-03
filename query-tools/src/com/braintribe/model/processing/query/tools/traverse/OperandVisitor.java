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
package com.braintribe.model.processing.query.tools.traverse;

import com.braintribe.model.query.PropertyOperand;
import com.braintribe.model.query.Source;
import com.braintribe.model.query.functions.JoinFunction;
import com.braintribe.model.query.functions.Localize;
import com.braintribe.model.query.functions.QueryFunction;
import com.braintribe.model.query.functions.aggregate.AggregateFunction;

/**
 * 
 */
@SuppressWarnings("unused")
public interface OperandVisitor {

	default void visitStaticValue(Object operand) {
		// NO OP
	}

	default void visit(PropertyOperand operand) {
		// NO OP
	}

	default void visit(JoinFunction operand) {
		// NO OP
	}

	default void visit(Localize operand) {
		// NO OP
	}

	default void visit(AggregateFunction operand) {
		// NO OP
	}

	default void visit(QueryFunction operand) {
		// NO OP
	}

	default void visit(Source operand) {
		// NO OP
	}

}
