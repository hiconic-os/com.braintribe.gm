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
package com.braintribe.model.generic.eval;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.processing.async.api.AsyncCallback;

public class EvalContextTest {

	@Test
	public void executeAsyncLogsSynchronousEvaluationFailure() {
		RuntimeException failure = new RuntimeException("synchronous evaluation failure");
		FailingEvalContext context = new FailingEvalContext(failure);

		context.executeAsync();

		assertThat(context.getAttribute(IgnoreResponseAspect.class)).isTrue();
		assertThat(context.evaluationAttempted).isTrue();
	}

	private static class FailingEvalContext extends AbstractEvalContext<Object> {
		private final RuntimeException failure;
		private boolean evaluationAttempted;

		private FailingEvalContext(RuntimeException failure) {
			this.failure = failure;
		}

		@Override
		public Object get() throws EvalException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void get(AsyncCallback<? super Object> callback) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void getReasoned(AsyncCallback<? super Maybe<Object>> callback) {
			evaluationAttempted = true;
			throw failure;
		}
	}
}
