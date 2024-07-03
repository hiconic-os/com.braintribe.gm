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
package com.braintribe.model.processing.vde.impl.misc;

import com.braintribe.model.common.IdentifiableEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;


/**
 * Dummy class used by {@link Person}
 * 
 */

public interface Name extends IdentifiableEntity, Comparable<Name> {

	EntityType<Name> T = EntityTypes.T(Name.class);
	
	String getFirst();
	void setFirst(String first);

	String getMiddle();
	void setMiddle(String middle);

	String getLast();
	void setLast(String last);
	
	public static class DefaultMethods {
		public static int compareTo(Name me, Object o) {
			return me.getFirst().compareTo(((Name)o).getFirst());
		}
	}

}
