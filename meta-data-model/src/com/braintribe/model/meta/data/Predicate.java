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
package com.braintribe.model.meta.data;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * A Predicate is a {@link MetaData} which evaluates to either <code>true</code> or <code>false</code> (e.g. Visible).
 * 
 * <h2>Basics</h2>
 * 
 * In order to configure a Predicate and to be able to remove it's effect, we use a type-hierarchy where the positive side is represented by a direct
 * sub-type of this type (or {@link ExplicitPredicate}, see later), and the negative side is represented by it's sub-type, which must also be a
 * sub-type of {@link PredicateErasure}.
 * 
 * <h2>Example</h2>
 * 
 * For configuring visibility the <tt>essential-meta-data-model</tt> contains an MD type {@code Visible} which extends {@code Predicate}, and
 * {@code Hidden}, which extends both {@code Visible} and {@code PredicateErasure}. Now, if we want to say for some entity "E" that all properties
 * except "X" are hidden, we can attach {@code Hidden} as a MD for all properties of "E", and attach {@code Visible} to the property "X".
 * 
 * <h2>Default value and ExplicitPredicate</h2>
 * 
 * We can also specify the default value of our predicate in case no MD is configured. For example {@code Visible} is <tt>true</tt> by default, but
 * {@code Unique} is <tt>false</tt> by default. In general, if the predicate type is a direct sub-type of this type ({@link Predicate}), it is
 * <tt>true</tt> by default, while if it is a sub-type of {@link ExplicitPredicate}, it is <tt>false</tt> by default. The term "explicit" means it has
 * to be configured explicitly to be <tt>true</tt>.
 * 
 * @see ExplicitPredicate
 * @see PredicateErasure
 */
@Abstract
public interface Predicate extends MetaData {

	EntityType<Predicate> T = EntityTypes.T(Predicate.class);

	default boolean isTrue() {
		return true;
	}

}
