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
package com.braintribe.model.meta;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.info.GmCustomTypeInfo;
import com.braintribe.model.meta.info.GmEnumConstantInfo;
import com.braintribe.model.meta.info.GmPropertyInfo;
import com.braintribe.model.weaving.ProtoGmCustomModelElement;

/**
 * Common supertype for all custom model elements.
 * 
 * @see GmCustomTypeInfo
 * @see GmPropertyInfo
 * @see GmEnumConstantInfo
 */
@Abstract
public interface GmCustomModelElement extends ProtoGmCustomModelElement, GmModelElement {

	EntityType<GmCustomModelElement> T = EntityTypes.T(GmCustomModelElement.class);

	//@Override
	GmMetaModel declaringModel();

	/**
	 * EXPERIMENTAL!!
	 * 
	 * @return name and origin of given model element, so that it contains the exact location within the model.
	 */
	String nameWithOrigin();

}
