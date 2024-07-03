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
package com.braintribe.model.generic.reflection;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;

/**
 * This will be renamed when BTT-6211 is done. For now we use this name to avoid conflicts with existing classes.
 * 
 * @author peter.gazdik
 */
public interface EntityTypeDeprecations<T extends GenericEntity> {

	/**
	 * Returns all the sub-types which are not abstract, possibly including the {@link EntityTypeDeprecations} on which
	 * this is called.
	 * 
	 * @deprecated we want to drop this all together; why do you need this? Let us know (PGA, DSC)
	 */
	@Deprecated
	Set<EntityType<?>> getInstantiableSubTypes();

	@Deprecated
	String toString(T instance);

	/**
	 * @return direct sub types of this {@link EntityType}
	 * 
	 * @deprecated DO NOT USE, chances are whatever you are doing should be done within the scope of some model, but
	 *             this method might also return entities from other (derived) models. Retrieve this information from
	 *             your GmMetaModel instance if possible. If you really need this, let us know (DSC or PGA) and we can
	 *             "un-deprecate" it.
	 */
	@Deprecated
	Set<EntityType<?>> getSubTypes();

}
