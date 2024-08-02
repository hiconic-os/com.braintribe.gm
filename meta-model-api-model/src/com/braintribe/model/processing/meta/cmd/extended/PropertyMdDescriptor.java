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
package com.braintribe.model.processing.meta.cmd.extended;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.info.GmEntityTypeInfo;
import com.braintribe.model.meta.info.GmPropertyInfo;

/**
 * @author peter.gazdik
 */
public interface PropertyMdDescriptor extends EntityRelatedMdDescriptor {

	EntityType<PropertyMdDescriptor> T = EntityTypes.T(PropertyMdDescriptor.class);

	/**
	 * This can be null if the MD was declared on {@link GmEntityTypeInfo#getPropertyMetaData()} or as the default for
	 * the resolver.
	 */
	GmPropertyInfo getOwnerPropertyInfo();
	void setOwnerPropertyInfo(GmPropertyInfo ownerPropertyInfo);

	@Override
	default String origin() {
		GmPropertyInfo opi = getOwnerPropertyInfo();
		return opi == null ? EntityRelatedMdDescriptor.super.origin() : opi.nameWithOrigin();
	}

}
