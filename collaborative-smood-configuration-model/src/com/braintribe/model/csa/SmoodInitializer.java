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
package com.braintribe.model.csa;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Represents a configuration entry for a single persistence initializer of the collaborative smood access (CSA). When
 * initializing CSA, a list of SmoodInitalizers is given, each is resolved to the actual initializer, which is then
 * executed..
 * 
 * @see ManInitializer
 * 
 * @author peter.gazdik
 */
@Abstract
public interface SmoodInitializer extends GenericEntity {

	EntityType<SmoodInitializer> T = EntityTypes.T(SmoodInitializer.class);

	@Mandatory
	String getName();
	void setName(String name);

	/** If true, this initializer is not executed on bootstrap */
	boolean getSkip();
	void setSkip(boolean skip);

	default void normalize() {
		// noop
	}

}
