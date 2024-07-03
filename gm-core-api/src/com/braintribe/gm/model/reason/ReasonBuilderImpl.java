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
package com.braintribe.gm.model.reason;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.braintribe.model.generic.reflection.EntityType;

public class ReasonBuilderImpl<R extends Reason> implements ReasonBuilder<R> {
	private final R reason;
	
	public ReasonBuilderImpl(EntityType<R> reasonType) {
		reason = reasonType.create();
	}
	
	@Override
	public <T> ReasonBuilder<R> assign(BiConsumer<R, T> assigner, T value) {
		assigner.accept(reason, value);
		return this;
	}

	@Override
	public ReasonBuilder<R> cause(Reason reason) {
		this.reason.causedBy(reason);
		return this;
	}

	@Override
	public ReasonBuilder<R> causes(Reason... reasons) {
		return causes(Arrays.asList(reasons));
	}

	@Override
	public ReasonBuilder<R> causes(Collection<Reason> reasons) {
		reasons.forEach(this::cause);
		return this;
	}

	@Override
	public R toReason() {
		if (reason.getText() == null) {
			String text = ReasonFormatter.buildPlainText(reason);
			reason.setText(text);
		}

		return reason;
	}
	
	@Override
	public <T> Maybe<T> toMaybe() {
		return Maybe.empty(toReason());
	}
	
	@Override
	public <T> Maybe<T> toMaybe(T value) {
		return Maybe.incomplete(value, toReason());
	}

	@Override
	public ReasonBuilder<R> text(String text) {
		reason.setText(text);
		return this;
	}

	@Override
	public ReasonBuilder<R> enrich(Consumer<R> enricher) {
		enricher.accept(reason);
		return this;
	}


}
