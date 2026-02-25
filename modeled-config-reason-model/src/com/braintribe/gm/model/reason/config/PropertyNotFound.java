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
package com.braintribe.gm.model.reason.config;

import com.braintribe.gm.model.reason.essential.NotFound;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@SelectiveInformation("Property ${propertyName} not found")
public interface PropertyNotFound extends NotFound {

	EntityType<PropertyNotFound> T = EntityTypes.T(PropertyNotFound.class);

	String propertyName = "propertyName";

	String getPropertyName();
	void setPropertyName(String value);

	static PropertyNotFound create(String propertyName) {
		PropertyNotFound result = T.create();
		result.setText("Property " + propertyName + " not found");
		result.setPropertyName(propertyName);
		return result;
	}
}
