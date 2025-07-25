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
package com.braintribe.model.security.service.config;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Description("Configures the OpenUserSession request behaviour.")
public interface OpenUserSessionConfiguration extends GenericEntity {
	EntityType<OpenUserSessionConfiguration> T = EntityTypes.T(OpenUserSessionConfiguration.class);
	
	String entryPoints = "entryPoints";
	
	@Description("Defines entry points that apply authorization to OpenUserSession requests.")
	Set<OpenUserSessionEntryPoint> getEntryPoints();
	void setEntryPoints(Set<OpenUserSessionEntryPoint> entryPoints);
}
