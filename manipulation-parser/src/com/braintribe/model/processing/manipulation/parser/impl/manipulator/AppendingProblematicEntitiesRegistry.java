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
package com.braintribe.model.processing.manipulation.parser.impl.manipulator;

import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.manipulation.parser.api.ProblematicEntitiesRegistry;

public class AppendingProblematicEntitiesRegistry implements ProblematicEntitiesRegistry {

	private final Set<String> problematicGlobalIds = newSet();

	@Override
	public boolean isProblematic(String globalId) {
		return problematicGlobalIds.contains(globalId);
	}

	@Override
	public void recognizeProblematicEntities(Set<GenericEntity> problematicEntities) {
		problematicEntities.stream() //
				.map(GenericEntity::getGlobalId) //
				.forEach(problematicGlobalIds::add);
	}

}
