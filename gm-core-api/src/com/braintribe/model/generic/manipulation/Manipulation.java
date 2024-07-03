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
package com.braintribe.model.generic.manipulation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.ForwardDeclaration;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.EntityReference;

@Abstract
@ForwardDeclaration("com.braintribe.gm:manipulation-model")
@SuppressWarnings("unusable-by-js")
public interface Manipulation extends GenericEntity {

	final EntityType<Manipulation> T = EntityTypes.T(Manipulation.class);

	void setInverseManipulation(Manipulation inverseManipulation);
	Manipulation getInverseManipulation();

	/** @see ManipulationType */
	ManipulationType manipulationType();

	default void linkInverse(Manipulation im) {
		setInverseManipulation(im);
		im.setInverseManipulation(this);
	}

	/**
	 * @return true iff this manipulation is remote. NOTE that current implementation is 100% correct for
	 *         PropertyManipulations only, cause for others there is no way to know, without know the context..
	 */
	boolean isRemote();

	/** @return {@link Stream} of {@link AtomicManipulation}s equivalent to manipulation represented by this instance */
	Stream<AtomicManipulation> stream();

	/** @return list of {@link AtomicManipulation}s equivalent to manipulation represented by this instance */
	default List<AtomicManipulation> inline() {
		return stream().collect(Collectors.toList());
	}

	/**
	 * @return {@link Stream} of all entities (or {@link EntityReference}s if remote) that are directly referenced by
	 *         this manipulation - be it as an owner or as a value. NOTE that the stream doesn't have the "distinct"
	 *         quality, i.e. entities may occur more than once.
	 */
	Stream<GenericEntity> touchedEntities();

}
