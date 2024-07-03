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
package com.braintribe.model.weaving.info;

import com.braintribe.model.weaving.ProtoGmCustomModelElement;
import com.braintribe.model.weaving.ProtoGmCustomType;
import com.braintribe.model.weaving.ProtoGmMetaModel;
import com.braintribe.model.weaving.data.ProtoHasMetaData;
import com.braintribe.model.weaving.override.ProtoGmCustomTypeOverride;

public interface ProtoGmCustomTypeInfo extends ProtoGmCustomModelElement, ProtoHasMetaData {

	ProtoGmMetaModel getDeclaringModel();

	/**
	 * @return actual ProtoGmCustomType for given info. If we call this on a {@link ProtoGmCustomType} itself, this method returns
	 *         that same instance, if we call it on a {@link ProtoGmCustomTypeOverride}, this method returns the type we are
	 *         overriding.
	 */
	ProtoGmCustomType addressedType();

	@Override
	default ProtoGmMetaModel declaringModel() {
		return getDeclaringModel();
	}

}
