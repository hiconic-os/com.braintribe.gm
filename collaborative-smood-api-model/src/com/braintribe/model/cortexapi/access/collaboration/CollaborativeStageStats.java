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
package com.braintribe.model.cortexapi.access.collaboration;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.InstantiationManipulation;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @see GetCollaborativeStageStats
 */
public interface CollaborativeStageStats extends GenericEntity {

	EntityType<CollaborativeStageStats> T = EntityTypes.T(CollaborativeStageStats.class);

	/** Number of {@link InstantiationManipulation}s */
	int getInstantiations();
	void setInstantiations(int instantiations);

	/** Number of {@link DeleteManipulation}s */
	int getDeletes();
	void setDeletes(int deletes);

	/** Number of {@link PropertyManipulation}s */
	int getUpdates();
	void setUpdates(int updates);

	default boolean isEmpty() {
		return getInstantiations() == 0 && getDeletes() == 0 && getUpdates() == 0;
	}

}
