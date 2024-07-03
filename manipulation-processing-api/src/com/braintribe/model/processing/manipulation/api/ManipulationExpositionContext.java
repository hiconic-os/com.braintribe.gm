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
package com.braintribe.model.processing.manipulation.api;

import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.InstantiationManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.ManipulationType;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.EntityReference;

/**
 * An interface to be used when processing {@link Manipulation}s. This provides a convenient way to access all the
 * information contained in a given {@link AtomicManipulation}.
 * 
 * The main idea behind this interface is that there exists a standard implementation of it (see the standard
 * implementation for this artifact - BasicModelProcessing), so other potential manipulation processors may take
 * advantage of it.
 */
public interface ManipulationExpositionContext {

	<T extends AtomicManipulation> T getCurrentManipulation();

	ManipulationType getCurrentManipulationType();

	/**
	 * Returns a {@link Property} instance corresponding to given {@link PropertyManipulation} or <tt>null</tt>
	 * otherwise.
	 */
	Property getTargetProperty();

	/**
	 * Returns a property name corresponding to given {@link PropertyManipulation} or <tt>null</tt> otherwise.
	 */
	String getTargetPropertyName();

	/**
	 * Returns the entity this manipulation is being executed on. In case of {@link PropertyManipulation} it is the
	 * owner of the property, in case of{@link InstantiationManipulation}/{@link DeleteManipulation} it is the entity
	 * itself.
	 */
	EntityReference getTargetReference();

	/**
	 * This method returns a valid {@link EntityReference} on given entity, but since multiple manipulations may use
	 * different instances of {@linkplain EntityReference} to access the same entity, this method is meant to always
	 * return the exact same instance (this makes more sense if you take into account that we can set various
	 * manipulations for one instance of manipulation context - and this method returns the same result if we change the
	 * manipulation to other one with equivalent entity reference).
	 */
	EntityReference getNormalizedTargetReference();

	/**
	 * Similar to {@link #getNormalizedTargetReference()}, but does this with an arbitrary entityReference passed as a
	 * parameter.
	 */
	EntityReference getNormalizeReference(EntityReference entityReference);

	/**
	 * Returns the {@link EntityType} corresponding to the {@link #getTargetReference() target reference}.
	 */
	EntityType<?> getTargetEntityType();

	/**
	 * Returns the type signature corresponding to the {@link #getTargetReference() target reference}.
	 */
	String getTargetSignature();
}
