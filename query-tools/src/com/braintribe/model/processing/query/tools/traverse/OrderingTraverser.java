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

import static com.braintribe.utils.lcd.CollectionTools2.nullSafe;

import java.util.function.Predicate;

import com.braintribe.model.query.CascadedOrdering;
import com.braintribe.model.query.Ordering;
import com.braintribe.model.query.SimpleOrdering;

/**
 * 
 */
public class OrderingTraverser extends OperandTraverser {

	private final OrderingVisitor orderingVisitor;

	public static void traverse(Predicate<Object> evalExcludedCheck, OrderingVisitor orderingVisitor, OperandVisitor operandVisitor,
			Ordering ordering) {

		if (ordering != null)
			new OrderingTraverser(evalExcludedCheck, orderingVisitor, operandVisitor).traverse(ordering);
	}

	public OrderingTraverser(Predicate<Object> evalExcludedCheck, OrderingVisitor orderingVisitor, OperandVisitor operandVisitor) {
		super(evalExcludedCheck, operandVisitor);

		this.orderingVisitor = orderingVisitor;
	}

	public void traverse(Ordering ordering) {
		if (ordering instanceof SimpleOrdering)
			traverse((SimpleOrdering) ordering);

		else if (ordering instanceof CascadedOrdering)
			traverse((CascadedOrdering) ordering);

		else
			throw new IllegalArgumentException("Unsupported ordering type of: " + ordering);
	}

	private void traverse(CascadedOrdering ordering) {
		for (SimpleOrdering simpleOrdering : nullSafe(ordering.getOrderings()))
			traverse(simpleOrdering);
	}

	private void traverse(SimpleOrdering simpleOrdering) {
		if (orderingVisitor == null || orderingVisitor.visit(simpleOrdering))
			traverseOperand(simpleOrdering.getOrderBy());
	}

}
