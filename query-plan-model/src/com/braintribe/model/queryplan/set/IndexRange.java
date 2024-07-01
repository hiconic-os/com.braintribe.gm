// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.queryplan.set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.queryplan.index.MetricIndex;
import com.braintribe.model.queryplan.value.range.SimpleRange;

/**
 * 
 * Represents an index range (a range backed by an index in the DB), acts on a population (defined by the signature), a property and uses an index
 * defined by its id. It is basically an optimization for a {@link SourceSet} with a "range" filter on an indexed property.
 * 
 * <h4>Example:</h4>
 * 
 * <tt>select * from Person p where p.birthDate <= 2000</tt>
 * 
 * <code>
 * IndexRange* {
 * 		typeSignature: "Person"
 * 		propertyName: birthDate
 * 		metricIndex: RepositoryMetricIndex {indexId: "personBirthDateIndex"}
 * 		range: SimpleRange {
 * 			lowerBound: null
 * 			upperBound: ConstantValue(2000)
 * 			lowerBoundInclusive: false
 * 			upperBoundInclusive: true
 * 		}
 * }
 * * - we assume there exists an index on property Person.birthDate 
 * </code>
 */

public interface IndexRange extends IndexSet {

	EntityType<IndexRange> T = EntityTypes.T(IndexRange.class);

	MetricIndex getMetricIndex();
	void setMetricIndex(MetricIndex metricIndex);

	SimpleRange getRange();
	void setRange(SimpleRange range);

	@Override
	default TupleSetType tupleSetType() {
		return TupleSetType.indexRange;
	}

}
