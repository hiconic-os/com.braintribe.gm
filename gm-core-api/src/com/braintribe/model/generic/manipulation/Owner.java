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
package com.braintribe.model.generic.manipulation;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.ForwardDeclaration;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.EntityReference;

@Abstract
@ForwardDeclaration("com.braintribe.gm:owner-model")
public interface Owner extends GenericEntity {

	final EntityType<Owner> T = EntityTypes.T(Owner.class);

	// @formatter:off
	String getPropertyName();
	void setPropertyName(String propertyName);
	// @formatter:on

	OwnerType ownerType();

	/**
	 * @return either the {@link EntityReference} (if remote) or the actual entity (if local) that is being manipulated. 
	 */
	GenericEntity ownerEntity();
	
	EntityType<?> ownerEntityType();

	Property property();

}
