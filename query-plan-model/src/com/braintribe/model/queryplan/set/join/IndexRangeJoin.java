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
package com.braintribe.model.queryplan.set.join;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.queryplan.index.MetricIndex;
import com.braintribe.model.queryplan.set.CartesianProduct;
import com.braintribe.model.queryplan.set.TupleSetType;
import com.braintribe.model.queryplan.value.range.Range;

/**
 * Implicit join for a case where the filtered property has an index. One can view it as a special type of filtered {@link CartesianProduct}
 * with only two operands, and a special type of filter which takes advantage of a property index.
 * 
 * <h4>Example:</h4>
 * 
 * <tt>select * from Person p, Company c where p.qualificationLevel >= c.expecedQuelificationLevel</tt>
 * 
 * <code>
 * 	IndexRangeJoin {
 * 		operand: SourceSet ;
 * 		metricIndex: RepositoryMetricIndex {
 * 			indexId: "Company_expecedQuelificationLevel" *
 * 		} 
 * 		lowerBound: ValueProperty {
 * 			value: TupleComponent {
 * 				tupleComponentPosition: SourceSet ;
 * 			}
 * 			propertyPath: "qualificationLevel"
 * 		}
 * 		upperBound: null
 * 		lowerInclusive: true
 * 		upperInclusive: true
 * 	} 
 * * - an index for Company 
 * </code>
 */

public interface IndexRangeJoin extends Join {

	EntityType<IndexRangeJoin> T = EntityTypes.T(IndexRangeJoin.class);

	MetricIndex getMetricIndex();
	void setMetricIndex(MetricIndex metricIndex);

	Range getRange();
	void setRange(Range range);

	@Override
	default TupleSetType tupleSetType() {
		return TupleSetType.indexRangeJoin;
	}

}
