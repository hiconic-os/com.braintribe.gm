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
package com.braintribe.model.version;

import java.util.Collections;
import java.util.List;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * a technical type : something that can be expressed as a {@link VersionRange}
 * @author pit/dirk
 *
 */
@Abstract
public interface VersionInterval extends VersionExpression {
	
	EntityType<VersionInterval> T = EntityTypes.T(VersionInterval.class);
	

	/**
	 * @return - a {@link VersionRange} representing this {@link VersionInterval}
	 */
	default VersionRange toRange() {		
		return VersionRange.from(lowerBound(), lowerBoundExclusive(), upperBound(), upperBoundExclusive());
	}
	
	
	Version lowerBound();
	boolean lowerBoundExclusive();
	
	Version upperBound();
	boolean upperBoundExclusive();
	
	@Override
	default List<VersionInterval> asVersionIntervalList() {	
		return Collections.singletonList(this);
	}
}
