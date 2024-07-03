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
package com.braintribe.utils.junit.assertions;

import org.fest.assertions.Assert;
import org.fest.assertions.Assertions;
import org.fest.assertions.GenericAssert;

import com.braintribe.model.generic.GenericEntity;

/**
 * {@link Assert} used to check a <code>{@link GenericEntity}</code> property (specified via
 * {@link GenericEntityProperty}). Note that this <code>Assert</code> does not extend {@link GenericAssert} and thus
 * provides only a reduced set of assertion checks. If one wants to to check against the actual property value, one can
 * use {@link GenericEntityPropertyValueAssert} instead (see {@link #onValue()}).
 * 
 * @author michael.lafite
 */
public class GenericEntityPropertyAssert extends Assert {

	private final GenericEntityProperty genericEntityProperty;

	public GenericEntityPropertyAssert(final GenericEntityProperty genericEntityProperty) {
		this.genericEntityProperty = genericEntityProperty;
	}

	/**
	 * Works like {@link GenericAssert#as(String)}.
	 */
	public GenericEntityPropertyAssert as(final String description) {
		description(description);
		return this;
	}

	/**
	 * Verifies that the property is absent.
	 */
	public GenericEntityPropertyAssert isAbsent() {
		if (!this.genericEntityProperty.isAbsent()) {
			failIfCustomMessageIsSet();
			fail("Property '" + this.genericEntityProperty.getName() + "' of entity "
					+ this.genericEntityProperty.getEntity() + " is not absent!");
		}
		return this;
	}

	/**
	 * Verifies that the property is not absent.
	 */
	public GenericEntityPropertyAssert isNotAbsent() {
		if (this.genericEntityProperty.isAbsent()) {
			failIfCustomMessageIsSet();
			fail("Property '" + this.genericEntityProperty.getName() + "' of entity "
					+ this.genericEntityProperty.getEntity() + " is absent!");
		}
		return this;
	}

	/**
	 * Returns a {@link GenericEntityPropertyValueAssert} that can be used to check the property value. Note that it
	 * usually makes more sense to pass the property value directly to the respection {@link Assertions}'
	 * <code>assertThat</code> method.
	 */
	public GenericEntityPropertyValueAssert onValue() {
		final GenericEntityPropertyValueAssert genericEntityPropertyValueAssert = new GenericEntityPropertyValueAssert(
				this.genericEntityProperty);
		if (description() != null) {
			genericEntityPropertyValueAssert.as(description());
		}
		return genericEntityPropertyValueAssert;
	}
}
