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
package com.braintribe.model.meta.info;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmCustomModelElement;
import com.braintribe.model.meta.GmCustomType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.IsDeclaredInModel;
import com.braintribe.model.meta.data.HasMetaData;
import com.braintribe.model.meta.override.GmCustomTypeOverride;
import com.braintribe.model.weaving.info.ProtoGmCustomTypeInfo;

@Abstract
public interface GmCustomTypeInfo extends ProtoGmCustomTypeInfo, HasMetaData, GmCustomModelElement, IsDeclaredInModel {

	EntityType<GmCustomTypeInfo> T = EntityTypes.T(GmCustomTypeInfo.class);

	@Override
	@Mandatory
	GmMetaModel getDeclaringModel();
	void setDeclaringModel(GmMetaModel declaringModel);

	/**
	 * @return actual GmCustomType for given info. If we call this on a {@link GmCustomType} itself, this method returns
	 *         that same instance, if we call it on a {@link GmCustomTypeOverride}, this method returns the type we are
	 *         overriding.
	 *         
	 * @deprecated use {@link #addressedType()} instead.
	 */
	@Deprecated
	default <T extends GmCustomType> T relatedType() {
		return (T) addressedType();
	}

	@Override
	GmCustomType addressedType();
	
	@Override
	default GmMetaModel declaringModel() {
		return getDeclaringModel();
	}

}
