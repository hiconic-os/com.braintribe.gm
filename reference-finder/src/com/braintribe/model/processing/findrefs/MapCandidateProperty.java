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
package com.braintribe.model.processing.findrefs;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmProperty;

/**
 * A {@link MapCandidateProperty} is a {@link CandidateProperty} for a Map-property that also contains information where
 * in the parameterization of the Map references might be located
 * 
 * 
 */
public class MapCandidateProperty extends CandidateProperty {

	/**
	 * Contains the possible combinations where references could be located
	 * 
	 * 
	 */
	public enum MapRefereeType {
		KEY_REF, VALUE_REF, BOTH, NONE;

		public static MapRefereeType getType(boolean keyReferencePossible, boolean valueReferencePossible) {
			if (keyReferencePossible && valueReferencePossible) {
				return BOTH;
			} else if (keyReferencePossible) {
				return KEY_REF;
			} else if (valueReferencePossible) {
				return VALUE_REF;
			} else {
				return NONE;
			}
		}
	}

	private MapRefereeType refereeType;

	public MapCandidateProperty(GmEntityType entityType, GmProperty property, MapRefereeType refereeType) {
		super(entityType, property);
		this.refereeType = refereeType;
	}

	public MapRefereeType getRefereeType() {
		return refereeType;
	}
}
