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
package com.braintribe.model.processing.meta.oracle;

import java.util.Set;

import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.meta.GmBaseType;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmType;

/**
 * Represents a type hierarchy rooted at a given {@link GmEntityType}.
 * 
 * @author peter.gazdik
 */
public interface TypeHierarchy {

	/** @deprecated use {@link #transitive()} and {@link #includeSelf()} */
	@Deprecated
	TypeHierarchy transitive(boolean includeSelf);

	TypeHierarchy transitive();

	/** Includes the base of this hierarchy as long as it matches the "instantiability" parameter. */
	TypeHierarchy includeSelf();

	/**
	 * Includes the base of this hierarchy regardless of the "instantiability" parameter.
	 * 
	 * @see #includeSelf()
	 */
	TypeHierarchy includeSelfForce();

	TypeHierarchy onlyInstantiable();

	TypeHierarchy onlyAbstract();

	/** Includes the base type. This is only relevant when we are retrieving {@link EntityTypeOracle#getSuperTypes() superTypes}. */
	TypeHierarchy includeBaseType();

	/** @deprecated this doesn't make sense. Deprecated on 2018-11-07 */
	@Deprecated
	TypeHierarchy includeSimpleTypes();

	/** Specifies that the resulting set will sorted according to given {@link Order}. */
	TypeHierarchy sorted(Order order);

	public static enum Order {
		subFirst,
		superFirst
	}

	/**
	 * Returns an UNMODIFIABLE {@link Set} of {@link GmType}s based on given options.
	 * 
	 * The order in case of super-types is given in depth-first order, but is {@link GmBaseType} is included, that is always the last one.
	 * 
	 * If "self" is included, it is always the first (for both sub and super).
	 */
	<T extends GmType> Set<T> asGmTypes();

	/**
	 * Returns an UNMODIFIABLE {@link Set} of {@link GenericModelType}s based on given options.
	 * 
	 * Note that the order corresponds to the order of {@link #asGmTypes()} result.
	 */
	<T extends GenericModelType> Set<T> asTypes();

	/**
	 * Returns an UNMODIFIABLE {@link Set} of {@link EntityTypeOracle}s based on given options.
	 * 
	 * Note that the order corresponds to the order of {@link #asGmTypes()} result.
	 * 
	 * @throws IllegalStateException
	 *             if {@link #includeBaseType()} option was used, because this combination doesn't make sense.
	 */
	Set<EntityTypeOracle> asEntityTypeOracles();

}
