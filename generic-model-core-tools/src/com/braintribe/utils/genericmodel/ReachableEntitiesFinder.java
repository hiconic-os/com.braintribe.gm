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
package com.braintribe.utils.genericmodel;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.utils.lcd.GraphTools.ReachablesFinder;

/**
 * This {@link ReachablesFinder} is used to find the reachable {@link GenericEntity}s of a GenericEntity.
 *
 */
public class ReachableEntitiesFinder implements ReachablesFinder<GenericEntity> {

	@Override
	public Set<GenericEntity> findReachables(final GenericEntity rootNode, final Set<GenericEntity> nodesWhereToStopFurtherTraversing) {
		return com.braintribe.utils.genericmodel.GMCoreTools.findReachableEntities(rootNode, nodesWhereToStopFurtherTraversing);
	}

}
