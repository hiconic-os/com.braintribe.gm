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
package com.braintribe.model.processing.query.fluent;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;
import java.util.function.Consumer;

import com.braintribe.model.query.OrderingDirection;
import com.braintribe.model.query.SimpleOrdering;

public class CascadedOrderingBuilder<T> extends OperandBuilder<CascadedOrderingBuilder<T>> {
	private final List<SimpleOrdering> orderings = newList();
	private final Consumer<List<SimpleOrdering>> _receiver;
	private final T _backLink;

	public CascadedOrderingBuilder(AbstractQueryBuilder<?> sourceRegistry, T backLink, Consumer<List<SimpleOrdering>> receiver) {
		super(sourceRegistry);
		setBackLink(this);
		setReceiver(operand -> {
			SimpleOrdering simpleOrdering = sourceRegistry.newGe(SimpleOrdering.T);
			simpleOrdering.setDirection(OrderingDirection.ascending);
			simpleOrdering.setOrderBy(operand);
			orderings.add(simpleOrdering);
		});

		this._backLink = backLink;
		this._receiver = receiver;
	}

	public OperandBuilder<CascadedOrderingBuilder<T>> dir(final OrderingDirection direction) {
		return new OperandBuilder<CascadedOrderingBuilder<T>>(sourceRegistry, this, operand -> {
			SimpleOrdering simpleOrdering = sourceRegistry.newGe(SimpleOrdering.T);
			simpleOrdering.setDirection(direction);
			simpleOrdering.setOrderBy(operand);
			orderings.add(simpleOrdering);
		});
	}

	public T close() {
		_receiver.accept(orderings);
		return _backLink;
	}
}
