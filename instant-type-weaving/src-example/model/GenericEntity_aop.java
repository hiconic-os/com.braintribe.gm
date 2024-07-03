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
package model;

import com.braintribe.model.generic.GenericEntity;

/**
 * @author peter.gazdik
 */
public interface GenericEntity_aop extends GenericEntity {

	// @formatter:off
	@Override
	default <T> T getId() { return null; }
	@Override
	default void setId(Object id) {/*nothing*/}

	@Override
	default String getPartition() { return null; }
	@Override
	default void setPartition(String partition) {/*nothing*/}
	
	@Override
	default String getGlobalId() { return null; }
	@Override
	default void setGlobalId(String globalId) {/*nothing*/}
	// @formatter:on

}

