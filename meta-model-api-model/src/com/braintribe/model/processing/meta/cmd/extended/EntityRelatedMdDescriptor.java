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

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEntityTypeInfo;

/**
 * @author peter.gazdik
 */
@Abstract
public interface EntityRelatedMdDescriptor extends MdDescriptor {

	EntityType<EntityRelatedMdDescriptor> T = EntityTypes.T(EntityRelatedMdDescriptor.class);

	/** The type we have given to the CMD resolver as the base for the MD resolution. */
	GmEntityType getResolvedForType();
	void setResolvedForType(GmEntityType resolvedForType);

	/** The {@link GmEntityTypeInfo} which holds our MD. Only null if the MD is configured as default. */
	GmEntityTypeInfo getOwnerTypeInfo();
	void setOwnerTypeInfo(GmEntityTypeInfo ownerTypeInfo);

	/**
	 * @return <tt>true</tt> iff this {@link MetaData} was inherited from a (proper) super-type of the type we were
	 *         resolving for (see {@link #getResolvedForType()}).
	 */
	default boolean isInherited() {
		GmEntityTypeInfo oti = getOwnerTypeInfo();
		return oti != null && getResolvedForType() != getOwnerTypeInfo().addressedType();
	}

	@Override
	default String origin() {
		GmEntityTypeInfo oti = getOwnerTypeInfo();
		return oti == null ? MdDescriptor.super.origin() : oti.nameWithOrigin();
	}

}
