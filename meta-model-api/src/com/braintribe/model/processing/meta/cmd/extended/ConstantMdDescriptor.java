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
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.info.GmEnumConstantInfo;
import com.braintribe.model.meta.info.GmEnumTypeInfo;

/**
 * @author peter.gazdik
 */
public interface ConstantMdDescriptor extends EnumRelatedMdDescriptor {

	EntityType<ConstantMdDescriptor> T = EntityTypes.T(ConstantMdDescriptor.class);

	/**
	 * Can be null if the MD is configured as default, on {@link GmMetaModel#getEnumConstantMetaData()} or
	 * {@link GmEnumTypeInfo#getEnumConstantMetaData()}
	 */
	GmEnumConstantInfo getOwnerConstantInfo();
	void setOwnerConstantInfo(GmEnumConstantInfo ownerConstantInfo);

	@Override
	default String origin() {
		GmEnumConstantInfo oci = getOwnerConstantInfo();
		return oci == null ? EnumRelatedMdDescriptor.super.origin() : oci.nameWithOrigin();
	}

}
