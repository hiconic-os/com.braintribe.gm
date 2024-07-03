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
package com.braintribe.model.queryplan.value;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;


import com.braintribe.model.queryplan.TupleComponentPosition;

/**
 * A {@linkplain TupleComponent} represents a single component inside a tuple set.
 * <p>
 * It is similar to a column in SQL, but the difference between this model and hibernate (HQL).When doing a select query in hibernate, you
 * get one value for each property of an entity. In that sense you could have many tuple values for one entity. In our case, on the other
 * hand, the entire entity would be just one tuple value. In general, a tuple component may be anything modeled by a sub-type of
 * {@link TupleComponentPosition}.
 * 
 * @author pit & dirk
 */

public interface TupleComponent extends Value {

	EntityType<TupleComponent> T = EntityTypes.T(TupleComponent.class);

	int getTupleComponentIndex();

	void setTupleComponentIndex(int tupleComponentIndex);

	@Override
	default ValueType valueType() {
		return ValueType.tupleComponent;
	}

}
