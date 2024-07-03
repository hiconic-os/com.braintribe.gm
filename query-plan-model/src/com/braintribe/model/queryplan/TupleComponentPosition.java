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
package com.braintribe.model.queryplan;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.queryplan.set.SourceSet;
import com.braintribe.model.queryplan.set.join.PropertyJoin;

/**
 * 
 * Defines a slot / dimension / position in a tuple. The {@link #getIndex() index} property is a unique identifier and
 * is used by an evaluator to make access for given component value faster.
 * <p>
 * In other words, this index has to be set by the query planner and has to be unique through all the components of the
 * resulting tuple set. It determines the position of given component in the resulting tuple.
 * 
 * @see SourceSet
 * @see PropertyJoin
 * 
 * @author pit & dirk
 */
@Abstract
public interface TupleComponentPosition extends GenericEntity {

	EntityType<TupleComponentPosition> T = EntityTypes.T(TupleComponentPosition.class);

	int getIndex();
	void setIndex(int index);

}
