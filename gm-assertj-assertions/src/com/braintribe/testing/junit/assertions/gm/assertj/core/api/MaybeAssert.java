package com.braintribe.testing.junit.assertions.gm.assertj.core.api;

import com.braintribe.gm.model.reason.Maybe;

/**
 * @author peter.gazdik
 */
public class MaybeAssert  extends AbstractMaybeAssert<MaybeAssert, Maybe<?>> {

	public MaybeAssert(Maybe<?> actual) {
		super(actual, MaybeAssert.class);
	}


}
