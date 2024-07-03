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
package com.braintribe.model.processing;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.meta.cmd.builders.PropertyMdResolver;

public interface PropertyValidationContext extends ValidationContext{

	/**
	 * The currently validated property
	 */
	Property getProperty();

	/**
	 * The value of the currently validated property and entity. Same as {@link #getValue()} but with a more expressive
	 * method name.
	 */
	Object getPropertyValue();

	/**
	 * The type of the currently validated property
	 */
	GenericModelType getPropertyType();

	/**
	 * A metadata resolver for the currently validated property
	 */
	PropertyMdResolver getPropertyMdResolver();

	/**
	 * The entity which the currently validated property belongs to
	 */
	GenericEntity getEntity();

	/**
	 * The type of the entity which the currently validated property belongs to
	 */
	EntityType<GenericEntity> getEntityType();

}