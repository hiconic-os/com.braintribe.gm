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
package com.braintribe.testing.junit.assertions.gm.assertj.core.api;

import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertMaybe;

import org.junit.Test;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.Canceled;
import com.braintribe.gm.model.reason.essential.NotFound;

/**
 * Tests for {@link MaybeAssert}.
 */
public class MaybeAssertTest {

	@Test
	public void testMaybeAssert() {
		Maybe<String> complete = Maybe.complete("Hello");
		assertMaybe(complete) //
				.hasValue() //
				.isSatisfied() //
				.hasNonNullValue();

		Maybe<Object> unsatisfied = NotFound.T.create().asMaybe();
		assertMaybe(unsatisfied) //
				.isUnsatisfied() //
				.isUnsatisfiedBy(NotFound.T);

		assertMaybe(unsatisfied).hasReasonWhich() //
				.hasPropertyValue("id", null);

		Maybe<Object> incomplete = Maybe.incomplete("value", Canceled.T.create());
		assertMaybe(incomplete).isIncomplete() //
				.hasValue() //
				.isUnsatisfied() //
				.isUnsatisfiedBy(Canceled.T);
	}

}
