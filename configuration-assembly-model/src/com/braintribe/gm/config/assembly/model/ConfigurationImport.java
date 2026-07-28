// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
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
package com.braintribe.gm.config.assembly.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** Declares one property which a packaged configuration intentionally imports from its deployment environment. */
public interface ConfigurationImport extends GenericEntity {

	EntityType<ConfigurationImport> T = EntityTypes.T(ConfigurationImport.class);

	String getName();
	void setName(String name);

	@Initializer("true")
	boolean getRequired();
	void setRequired(boolean required);

	boolean getConfidential();
	void setConfidential(boolean confidential);

	String getDescription();
	void setDescription(String description);
}
