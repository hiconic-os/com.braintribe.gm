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
package com.braintribe.model.generic.enhance;

import com.braintribe.model.generic.GenericEntity;

/**
 * 
 * @author peter.gazdik
 */
public class EntityFlags {

	public static final int SHALLOW = 1;

	public static boolean isShallow(GenericEntity ge) {
		return (((EnhancedEntity) ge).flags() & SHALLOW) != 0;
	}

	public static void setShallow(GenericEntity ge, boolean shallow) {
		setFlag((EnhancedEntity) ge, SHALLOW, shallow);
	}

	private static void setFlag(EnhancedEntity ee, int FLAG, boolean flagValue) {
		if (flagValue) {
			ee.assignFlags(ee.flags() | FLAG);
		} else {
			ee.assignFlags(ee.flags() & ~FLAG);
		}
	}

}
