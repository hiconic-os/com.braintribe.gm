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

import org.assertj.core.api.AbstractObjectAssert;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.testing.junit.assertions.assertj.core.api.SharedAssert;
import com.braintribe.utils.CommonTools;
import com.braintribe.utils.lcd.NullSafe;

/**
 * Extensible base for {@link GenericEntity} assertions.
 *
 * @author michael.lafite
 */
public class AbstractGenericEntityAssert<S extends AbstractGenericEntityAssert<S, A>, A extends GenericEntity> extends AbstractObjectAssert<S, A>
		implements SharedAssert<S, A> {

	public AbstractGenericEntityAssert(A actual, Class<?> selfType) {
		super(actual, selfType);
	}

	/** Asserts the entity is an instance of exactly the given {@link EntityType} (i.e. not some of it's sub-types). */
	public S isExactly(EntityType<?> entityType) {
		if (actual.entityType() != entityType) {
			failWithMessage("Expected to be '" + entityType.getTypeSignature() + "' , but is '" + actual.entityType().getTypeSignature() + "'.");
		}

		return myself;
	}

	public S isInstanceOf(EntityType<?> entityType) {
		return isInstanceOf(entityType.getJavaType());
	}

	/** Asserts the id property is not <tt>null</tt>. */
	public S hasId() {
		return checkIfPropertyNull(GenericEntity.id, actual.getId(), false);
	}

	/**
	 * Asserts the <code>id</code> matches the <code>expected</code> <code>id</code>, i.e. either equal of both
	 * <code>null</code>.
	 */
	public S hasIdMatchingIdOf(GenericEntity expected) {
		Object actualId = actual.getId();
		Object expectedId = expected.getId();

		if (!(actualId == expectedId || (actualId != null && actualId.equals(expectedId)))) {
			failWithMessage("Actual id " + actualId + " does not match expected id " + expectedId + "!");
		}

		return myself;
	}

	/** Asserts the partition property is not <tt>null</tt>. */
	public S hasPartition() {
		return checkIfPropertyNull(GenericEntity.partition, actual.getPartition(), false);
	}

	/** Asserts the globalId property is not <tt>null</tt>. */
	public S hasGlobalId() {
		return checkIfPropertyNull(GenericEntity.globalId, actual.getGlobalId(), false);
	}

	/** Asserts the id property is <tt>null</tt>. */
	public S hasNoId() {
		return checkIfPropertyNull(GenericEntity.id, actual.getId(), true);
	}

	/** Asserts the partition property is <tt>null</tt>. */
	public S hasNoPartition() {
		return checkIfPropertyNull(GenericEntity.partition, actual.getPartition(), true);
	}

	/** Asserts the globalId property is <tt>null</tt>. */
	public S hasNoGlobalId() {
		return checkIfPropertyNull(GenericEntity.globalId, actual.getGlobalId(), true);
	}

	private S checkIfPropertyNull(String name, Object value, boolean expectNull) {
		if ((value == null) != expectNull) {
			if (value == null) {
				failWithMessage(name + " was not expected to be null.");
			} else {
				failWithMessage(name + " was expected to be null but was: " + value);
			}
		}

		return myself;
	}

	/**
	 * Asserts that the value of the specified property is {@link Property#isAbsent(GenericEntity) absent}.
	 */
	public S hasAbsentProperty(String propertyName) {
		return checkPropertyAbsence(propertyName, true);
	}

	/**
	 * Asserts that the value of the specified property is not {@link Property#isAbsent(GenericEntity) absent}.
	 * Alternative name for this method is {@link #hasPresentProperty(String)}.
	 */
	public S hasNotAbsentProperty(String propertyName) {
		return checkPropertyAbsence(propertyName, false);
	}

	/**
	 * Asserts that the value of the specified property is present, i.e. not {@link Property#isAbsent(GenericEntity)
	 * absent}. Alternative name for this method is {@link #hasNotAbsentProperty(String)}.
	 */
	public S hasPresentProperty(String propertyName) {
		return checkPropertyAbsence(propertyName, false);
	}

	private Property getProperty(String propertyName) {
		Property property = actual.entityType().findProperty(propertyName);
		if (property == null) {
			failWithMessage("Expected " + super.actual.type().getTypeSignature() + " to have a property named " + toString(propertyName) + ".");
		}
		return property;
	}

	private S checkPropertyAbsence(String propertyName, boolean expectedAbsent) {
		Property property = getProperty(propertyName);

		boolean actualAbsent = property.isAbsent(actual);

		if (actualAbsent != expectedAbsent) {
			failWithMessage("Expected property '%s' to be <%s> but was <%s>", propertyName, expectedAbsent ? "absent" : "present",
					actualAbsent ? "absent" : "present");
		}
		return myself;
	}

	/**
	 * Asserts that the actual {@link GenericEntity} has a session attached.
	 */
	public S hasSessionAttached() {
		if (this.actual.session() == null) {
			failWithMessage("Expected entity " + this.actual + " to have a session attached!");
		}
		return myself;
	}

	/**
	 * Asserts that the actual {@link GenericEntity} has no session attached.
	 */
	public S hasNoSessionAttached() {
		if (this.actual.session() != null) {
			failWithMessage("Expected entity " + this.actual + " not have a session attached!");
		}
		return myself;
	}

	/**
	 * Asserts that the actual value of the specified property matches the <code>expectedPropertyValue</code>.<br>
	 * This method can be used to fluently check multiple property values. However, if more options than a simple
	 * <code>equals</code> check are required, one should instead use the respective property getter method and pass the
	 * result to <code>assertThat</code>.
	 */
	public S hasPropertyValue(String propertyName, Object expectedPropertyValue) {
		Property property = getProperty(propertyName);
		Object actualPropertyValue = property.get(actual);
		if (!CommonTools.equalsOrBothNull(actualPropertyValue, expectedPropertyValue)) {
			failWithMessage("Property value assertion failed for property '" + propertyName + "!\nActual (type="
					+ NullSafe.className(actualPropertyValue) + "):\n" + toString(actualPropertyValue) + "\nExpected (type="
					+ NullSafe.className(expectedPropertyValue) + "):\n" + toString(expectedPropertyValue));
		}
		return myself;
	}

}
