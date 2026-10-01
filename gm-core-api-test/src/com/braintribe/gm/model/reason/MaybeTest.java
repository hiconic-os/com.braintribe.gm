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
package com.braintribe.gm.model.reason;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.braintribe.model.generic.reason.model.TestReason;

/**
 * Tests for {@link Maybe#map} and {@link Maybe#flatMap}.
 */
public class MaybeTest {

	// ######################################################
	// ## . . . . . . . . . . . map . . . . . . . . . . . ##
	// ######################################################

	@Test
	public void mapComplete() {
		Maybe<Integer> result = Maybe.complete("abc").map(String::length);

		assertThat(result.isSatisfied()).isTrue();
		assertThat(result.get()).isEqualTo(3);
	}

	@Test
	public void mapEmpty_MapperNotCalled() {
		Reason reason = reason("original");

		Maybe<Integer> result = Maybe.<String> empty(reason).map(s -> {
			throw new AssertionError("Mapper must not be called for an empty Maybe");
		});

		assertThat(result.isEmpty()).isTrue();
		assertThat((Reason) result.whyUnsatisfied()).isSameAs(reason);
	}

	@Test
	public void mapIncomplete_ValueMappedAndReasonKept() {
		Reason reason = reason("original");

		Maybe<Integer> result = Maybe.incomplete("abc", reason).map(String::length);

		assertThat(result.isIncomplete()).isTrue();
		assertThat(result.value()).isEqualTo(3);
		assertThat((Reason) result.whyUnsatisfied()).isSameAs(reason);
		assertThat(reason.getReasons()).isEmpty();
	}

	@Test
	public void mapIncompleteToNull() {
		Reason reason = reason("original");

		Maybe<Object> result = Maybe.incomplete("abc", reason).map(s -> null);

		assertThat(result.isIncomplete()).isTrue();
		assertThat(result.value()).isNull();
		assertThat((Reason) result.whyUnsatisfied()).isSameAs(reason);
	}

	// ######################################################
	// ## . . . . . . . . . . flatMap . . . . . . . . . . . ##
	// ######################################################

	@Test
	public void flatMapCompleteToComplete() {
		Maybe<Integer> result = Maybe.complete("abc").flatMap(s -> Maybe.complete(s.length()));

		assertThat(result.isSatisfied()).isTrue();
		assertThat(result.get()).isEqualTo(3);
	}

	@Test
	public void flatMapCompleteToIncomplete_MappedReturned() {
		Maybe<Integer> mapped = Maybe.incomplete(3, reason("mapped"));

		Maybe<Integer> result = Maybe.complete("abc").flatMap(s -> mapped);

		assertThat(result).isSameAs(mapped);
	}

	@Test
	public void flatMapCompleteToEmpty_MappedReturned() {
		Maybe<Integer> mapped = Maybe.empty(reason("mapped"));

		Maybe<Integer> result = Maybe.complete("abc").flatMap(s -> mapped);

		assertThat(result).isSameAs(mapped);
	}

	@Test
	public void flatMapEmpty_MapperNotCalled() {
		Reason reason = reason("original");

		Maybe<Integer> result = Maybe.<String> empty(reason).flatMap(s -> {
			throw new AssertionError("Mapper must not be called for an empty Maybe");
		});

		assertThat(result.isEmpty()).isTrue();
		assertThat((Reason) result.whyUnsatisfied()).isSameAs(reason);
	}

	@Test
	public void flatMapIncompleteToComplete_OriginalReasonKept() {
		Reason reason = reason("original");

		Maybe<Integer> result = Maybe.incomplete("abc", reason).flatMap(s -> Maybe.complete(s.length()));

		assertThat(result.isIncomplete()).isTrue();
		assertThat(result.value()).isEqualTo(3);
		assertThat((Reason) result.whyUnsatisfied()).isSameAs(reason);
		assertThat(reason.getReasons()).isEmpty();
	}

	@Test
	public void flatMapIncompleteToIncomplete_ReasonsCombined() {
		Reason originalReason = reason("original");
		Reason mappedCause = reason("mapped-cause");
		Reason mappedReason = reason("mapped", mappedCause);

		Maybe<Integer> result = Maybe.incomplete("abc", originalReason).flatMap(s -> Maybe.incomplete(s.length(), mappedReason));

		assertThat(result.isIncomplete()).isTrue();
		assertThat(result.value()).isEqualTo(3);

		Reason resultReason = result.whyUnsatisfied();
		assertThat(resultReason).isNotSameAs(mappedReason);
		assertThat(resultReason.entityType()).isSameAs(TestReason.T);
		assertThat(resultReason.getText()).isEqualTo("mapped");
		assertThat(resultReason.getReasons()).containsExactly(mappedCause, originalReason);
	}

	@Test
	public void flatMapIncompleteToEmpty_ReasonsCombined() {
		Reason originalReason = reason("original");
		Reason mappedReason = reason("mapped");

		Maybe<Integer> result = Maybe.incomplete("abc", originalReason).flatMap(s -> Maybe.empty(mappedReason));

		assertThat(result.isEmpty()).isTrue();

		Reason resultReason = result.whyUnsatisfied();
		assertThat(resultReason).isNotSameAs(mappedReason);
		assertThat(resultReason.getText()).isEqualTo("mapped");
		assertThat(resultReason.getReasons()).containsExactly(originalReason);
	}

	@Test
	public void flatMapIncompleteToUnsatisfied_OriginalReasonsNotModified() {
		Reason originalCause = reason("original-cause");
		Reason originalReason = reason("original", originalCause);
		Reason mappedCause = reason("mapped-cause");
		Reason mappedReason = reason("mapped", mappedCause);

		Maybe<String> original = Maybe.incomplete("abc", originalReason);
		Maybe<Integer> mapped = Maybe.incomplete(3, mappedReason);

		original.flatMap(s -> mapped);
		original.flatMap(s -> mapped);

		assertThat(originalReason.getReasons()).containsExactly(originalCause);
		assertThat(mappedReason.getReasons()).containsExactly(mappedCause);
		assertThat((Reason) mapped.whyUnsatisfied()).isSameAs(mappedReason);
	}

	@Test
	public void flatMapIncompleteToSameReason_NoCycle() {
		Reason reason = reason("original");
		Maybe<String> original = Maybe.incomplete("abc", reason);

		Maybe<String> result = original.flatMap(s -> original);

		Reason resultReason = result.whyUnsatisfied();
		assertThat(resultReason).isNotSameAs(reason);
		assertThat(resultReason.getReasons()).containsExactly(reason);
		assertThat(reason.getReasons()).isEmpty();
	}

	@Test
	public void flatMapToNull_Throws() {
		assertThatThrownBy(() -> Maybe.complete("abc").flatMap(s -> null)) //
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	public void flatMapIncompleteToNull_Throws() {
		assertThatThrownBy(() -> Maybe.incomplete("abc", reason("original")).flatMap(s -> null)) //
				.isInstanceOf(NullPointerException.class);
	}

	private static Reason reason(String text, Reason... causes) {
		TestReason reason = Reasons.create(TestReason.T, text);
		for (Reason cause : causes)
			reason.getReasons().add(cause);
		return reason;
	}

}
