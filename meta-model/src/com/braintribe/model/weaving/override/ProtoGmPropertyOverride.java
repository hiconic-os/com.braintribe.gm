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
package com.braintribe.model.weaving.override;

import com.braintribe.model.weaving.ProtoGmProperty;
import com.braintribe.model.weaving.ProtoGmType;
import com.braintribe.model.weaving.info.ProtoGmEntityTypeInfo;
import com.braintribe.model.weaving.info.ProtoGmPropertyInfo;

public interface ProtoGmPropertyOverride extends ProtoGmPropertyInfo {

	ProtoGmProperty getProperty();

	ProtoGmEntityTypeInfo getDeclaringTypeInfo();

	ProtoGmType getTypeOverride();

	@Override
	default ProtoGmProperty relatedProperty() {
		return getProperty();
	}

	@Override
	default ProtoGmEntityTypeInfo declaringTypeInfo() {
		return getDeclaringTypeInfo();
	}

}
