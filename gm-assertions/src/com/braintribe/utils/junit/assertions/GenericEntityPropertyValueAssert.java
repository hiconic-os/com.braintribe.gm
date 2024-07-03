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
import org.fest.assertions.GenericAssert;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;

/**
 * {@link Assert} used to check the value of a <code>{@link GenericEntity}</code> property ( specified via
 * {@link GenericEntityProperty}). Note that this <code>Assert</code> will always get the actual property value
 * (possibly resolving {@link AbsenceInformation}). If this is not desired, one can use
 * {@link GenericEntityPropertyAssert}.
 * 
 * @author michael.lafite
 */
public class GenericEntityPropertyValueAssert extends GenericAssert<GenericEntityPropertyValueAssert, Object> {

	private final GenericEntityProperty genericEntityProperty;

	/**
	 * Creates a new {@link GenericEntityPropertyValueAssert}.
	 */
	public GenericEntityPropertyValueAssert(final GenericEntityProperty genericEntityProperty) {
		super(GenericEntityPropertyValueAssert.class, genericEntityProperty.getValue());
		this.genericEntityProperty = genericEntityProperty;

		// TODO is there a way to set a error message prefix?
	}

	/**
	 * Verifies that (before getting the value) the property was absent.
	 */
	public GenericEntityPropertyValueAssert wasAbsent() {
		if (!this.genericEntityProperty.wasAbsent()) {
			failIfCustomMessageIsSet();
			fail("Property '" + this.genericEntityProperty.getName() + "' of entity "
					+ this.genericEntityProperty.getEntity() + " was not absent!");
		}
		return this;
	}

	/**
	 * Verifies that (before getting the value) the property was not absent.
	 */
	public GenericEntityPropertyValueAssert wasNotAbsent() {
		if (this.genericEntityProperty.wasAbsent()) {
			failIfCustomMessageIsSet();
			fail("Property '" + this.genericEntityProperty.getName() + "' of entity "
					+ this.genericEntityProperty.getEntity() + " was absent!");
		}
		return this;
	}
}
