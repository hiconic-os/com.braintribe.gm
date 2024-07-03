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
package com.braintribe.model.processing.session.test.data;

import com.braintribe.model.descriptive.HasName;
import com.braintribe.model.generic.StandardIdentifiable;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * This one is used for a test-case where primitive property is needed.
 * 
 * Note that the primitive property is called $active, because we want it to be the first property when sorted
 * alphabetically in order to test the use-case correctly.
 */
public interface Flag extends HasName, StandardIdentifiable {

	EntityType<Flag> T = EntityTypes.T(Flag.class);

	boolean get$active();
	void set$active(boolean $active);

	@Initializer("true")
	boolean getInitializedValue();
	void setInitializedValue(boolean initializedValue);

}
