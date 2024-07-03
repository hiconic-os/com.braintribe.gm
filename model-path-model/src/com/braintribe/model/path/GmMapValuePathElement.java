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
package com.braintribe.model.path;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;




public interface GmMapValuePathElement extends GmPropertyRelatedPathElement {

	EntityType<GmMapValuePathElement> T = EntityTypes.T(GmMapValuePathElement.class);

	// @formatter:off
	void setKey(Object key);
	Object getKey();

	void setKeyTypeSignature(String keyTypeSignature);
	String getKeyTypeSignature();
	// @formatter:on

	@Override
	default GmModelPathElementType elementType() {
		return GmModelPathElementType.MapValue;
	}

}
