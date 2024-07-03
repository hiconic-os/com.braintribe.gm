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
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.info.GmEnumTypeInfo;

/**
 * @author peter.gazdik
 */
@Abstract
public interface EnumRelatedMdDescriptor extends MdDescriptor {

	EntityType<EnumRelatedMdDescriptor> T = EntityTypes.T(EnumRelatedMdDescriptor.class);

	/**
	 * The {@link GmEnumTypeInfo} which holds our MD. Can be null if the MD is configured as default or in
	 * {@link GmMetaModel#getEnumTypeMetaData()} or {@link GmMetaModel#getEnumConstantMetaData()}.
	 */
	GmEnumTypeInfo getOwnerTypeInfo();
	void setOwnerTypeInfo(GmEnumTypeInfo ownerTypeInfo);

	@Override
	default String origin() {
		GmEnumTypeInfo oti = getOwnerTypeInfo();
		return oti == null ? MdDescriptor.super.origin() : oti.nameWithOrigin();
	}

}
