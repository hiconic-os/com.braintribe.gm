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

import java.util.List;
import java.util.stream.Collectors;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * a container that holds several {@link VersionInterval},
 * as Maven allows 
 * 
 * @author pit/dirk
 *
 */
public interface VersionIntervals extends VersionExpression {
	
	EntityType<VersionIntervals> T = EntityTypes.T(VersionIntervals.class);
	static final String elements = "elements";

	/**
	 * @return - the {@link VersionInterval} that make this up
	 */
	List<VersionInterval> getElements();
	void setElements( List<VersionInterval> elements);
	
	@Override
	default boolean matches(Version version) {
		for (VersionInterval interval : getElements()) {
			if (interval.matches(version))
				return true;
		}
		return false;
	}
	
	@Override
	default String asString() {
		return getElements().stream().map( VersionExpression::asString).collect(Collectors.joining( ","));
	}
	
	@Override
	default List<VersionInterval> asVersionIntervalList() {	
		return getElements();
	}
}
