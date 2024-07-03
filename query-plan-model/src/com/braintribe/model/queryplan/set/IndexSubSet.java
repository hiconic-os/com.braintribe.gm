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
package com.braintribe.model.queryplan.set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.queryplan.index.Index;
import com.braintribe.model.queryplan.value.ConstantValue;

/**
 * 
 * Represents part of the population retrieved from an index using values for indexed properties.
 * 
 * <h4>Example:</h4>
 * 
 * <tt>select * from Person p where p.father = :fatherInstance</tt>
 * 
 * <code>
 * IndexSubSet* {
 * 		typeSignature: "Person"
 * 		propertyName: father
 * 		lookupIndex: RepositoryIndex {indexId: "person#father"}
 * 		keys: StaticValue {
 * 			values: [:fatherInstance]
 * 		}
 * }
 * * - we assume there exists an index on property Person.father 
 * </code>
 * 
 * <h4>Example2:</h4>
 * 
 * <tt>select * from Person p where p.age in (10, 20, 30, 40)</tt>
 * 
 * <code>
 * IndexSubSet* {
 * 		typeSignature: "Person"
 * 		propertyName: age
 * 		lookupIndex: RepositoryIndex {indexId: "person#age"}
 * 		values = [10, 20, 30, 40]
 * }
 * * - we assume there exists an index on property Person.age 
 * </code>
 * 
 */

public interface IndexSubSet extends IndexSet {

	EntityType<IndexSubSet> T = EntityTypes.T(IndexSubSet.class);

	ConstantValue getKeys();
	void setKeys(ConstantValue keys);

	Index getLookupIndex();
	void setLookupIndex(Index lookupIndex);

	@Override
	default TupleSetType tupleSetType() {
		return TupleSetType.indexSubSet;
	}

}
