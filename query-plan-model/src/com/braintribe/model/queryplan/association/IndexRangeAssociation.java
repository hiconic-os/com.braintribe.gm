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
package com.braintribe.model.queryplan.association;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;



/**
 * 
 * represents an index range condition and has all the information needed to parameterize the actual index range access (i.e. the actual
 * bounds are set in the evaluation)
 * 
 * @author pit & dirk
 *
 */
@Deprecated

public interface IndexRangeAssociation extends Association {

	EntityType<IndexRangeAssociation> T = EntityTypes.T(IndexRangeAssociation.class);

	String getIndexId();

	void setIndexId(String indexId);

	boolean getLowerBoundExclusive();

	void setLowerBoundExclusive(boolean lowerBoundExclusive);

	boolean getUpperBoundExclusive();

	void setUpperBoundExclusive(boolean upperBoundExclusive);

	IndexRangeBoundsAssignment getBoundsAssignment();

	void setBoundsAssignment(IndexRangeBoundsAssignment boundsAssignment);

}
