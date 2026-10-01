// ============================================================================
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
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface ExplicitConfigurationNotFound extends NotFound {

	EntityType<ExplicitConfigurationNotFound> T = EntityTypes.T(ExplicitConfigurationNotFound.class);

	String configurationType = "configurationType";

	String getConfigurationType();
	void setConfigurationType(String configurationType);

	static ExplicitConfigurationNotFound create(String configurationType) {
		ExplicitConfigurationNotFound result = T.create();
		result.setText("No configuration found for type: " + configurationType);
		result.setConfigurationType(configurationType);
		return result;
	}
}
