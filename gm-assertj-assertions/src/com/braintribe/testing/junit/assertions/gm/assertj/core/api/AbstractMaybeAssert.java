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

import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertEntity;

import org.assertj.core.api.AbstractObjectAssert;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.testing.junit.assertions.assertj.core.api.SharedAssert;

/**
 * Extensible base for {@link Maybe} assertions.
 */
public class AbstractMaybeAssert<S extends AbstractMaybeAssert<S, M>, M extends Maybe<?>> extends AbstractObjectAssert<S, M>
		implements SharedAssert<S, M> {

	public AbstractMaybeAssert(M actual, Class<?> selfType) {
		super(actual, selfType);
	}

	public S isSatisfied() {
		if (!actual.isSatisfied())
			failWithMessage("Expected to be satisfied, but isn't for reason: " + printReason());

		return myself;
	}

	public S hasValue() {
		if (!actual.hasValue())
			failWithMessage("Expected to have value, but only has a reason: " + printReason());

		return myself;
	}

	public S hasNonNullValue() {
		hasValue();

		if (actual.get() == null)
			failWithMessage("Expected value not to be null.");

		return myself;
	}

	public S isUnsatisfied() {
		return hasReason();
	}

	public S hasReason() {
		if (actual.whyUnsatisfied() == null)
			failWithMessage("Expected to have a reason, but has no reason. Value: " + actual.get());

		return myself;
	}

	public S isUnsatisfiedBy(EntityType<? extends Reason> reasonType) {
		return hasReasonOfType(reasonType);
	}

	public S hasReasonOfType(EntityType<? extends Reason> reasonType) {
		hasReason();

		if (!actual.isUnsatisfiedBy(reasonType))
			failWithMessage("Expected to have a reason of type [" + reasonType.getShortName() + "]," + //
					" but was [" + actual.whyUnsatisfied().entityType().getShortName() + "]\n" + //
					"Value: " + actual.whyUnsatisfied());

		return myself;
	}

	public GenericEntityAssert hasReasonWhich() {
		hasReason();
		return assertEntity(actual.whyUnsatisfied());
	}

	public S isIncomplete() {
		if (!actual.isIncomplete())
			if (!actual.hasValue())
				failWithMessage("Expected to be incomplete, but no value set. Reason: " + printReason());
			else
				failWithMessage("Expected to be incomplete, but no reason set. Value: " + actual.get());

		return myself;
	}

	private String printReason() {
		Reason whyUnsatisfied = actual.whyUnsatisfied();
		return whyUnsatisfied == null ? "null" : whyUnsatisfied.stringify();
	}

}
