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

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;
import java.util.Optional;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.PreliminaryEntityReference;

/**
 * Experimental!!! DO NOT USE YET
 * 
 * @author peter.gazdik
 */
public interface ManipulationOracle {

	Manipulation getManipulation();

	Manipulation getInducedManipulation();
	
	/** Returns a {@link AtomicManipulationOracle} for this specific {@link AtomicManipulation}. */
	AtomicManipulationOracle forAtomic(AtomicManipulation manipulation);

	/**
	 * Resolves given entity reference with the underlying {@link PersistenceReferenceResolver} (typically a persistence
	 * session based). It throws an exception if the reference cannot be resolved. For resolution of multiple references
	 * use the more optimized {@link #resolveAll(Iterable)}.
	 * 
	 * <h3>Resolution rules</h3>
	 * <ul>
	 * <li>Resolution of a {@link PreliminaryEntityReference} is only possible if your oracle instance already contains
	 * a manipulation response, i.e. this is dependent on the context and it should always be documented properly what
	 * you can and cannot do with the oracle.</li>
	 * <li>Resolving the same entity reference multiple times does not cause multiple queries, as the implementation is
	 * expected to do an internal caching.</li>
	 * </ul>
	 */
	GenericEntity resolve(EntityReference reference);

	/**
	 * Optimized implementation for resolving multiple {@link EntityReference}s. Once the method has successfully
	 * finished, use {@link #getResolved(EntityReference)} or {@link #findResolved(EntityReference)} to get the actual
	 * entity for given {@link EntityReference}.
	 * <p>
	 * For more information on resolution see {@link #resolve(EntityReference)}.
	 */
	void resolveAll(Iterable<EntityReference> references);

	/**
	 * Even more optimized version of {@link #resolveAll(Iterable)}, which takes advantage of knowing all references are
	 * of the same type.
	 * <p>
	 * However, for convenience it is possible to pass null as typeSignature, it which case this is equivalent to
	 * calling {@code resolve(references)}.
	 */
	void resolveAll(String typeSignature, Iterable<EntityReference> references);

	/**
	 * Similar to {@link #findResolved(EntityReference)}, but throws an exception if no entity found.
	 * <p>
	 * This method shouldn't be called unless a we are sure the reference was resolved before, with either
	 * {@link #resolve(EntityReference)} or {@link #resolveAll(Iterable)}.
	 */
	GenericEntity getResolved(EntityReference reference);

	/** Returns an entity that was already resolved for this reference, or <tt>null</tt>, if not resolved yet. */
	Optional<GenericEntity> findResolved(EntityReference reference);

	// ##############################################
	// ## . . . . . . . . Defaults . . . . . . . . ##
	// ##############################################

	default <T extends GenericEntity> List<T> getAllResolved(Iterable<EntityReference> references) {
		List<T> result = newList();

		for (EntityReference ref : references)
			result.add((T) getResolved(ref));

		return result;
	}

}
