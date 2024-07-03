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
package com.braintribe.model.csa;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author peter.gazdik
 */
public interface ModuleInitializer extends CustomInitializer {

	EntityType<ModuleInitializer> T = EntityTypes.T(ModuleInitializer.class);

	/** globalId of the relevant module */
	String getModuleId();
	void setModuleId(String moduleId);

	/**
	 * In case of an accessId redirect, this holds the value of the original access id, which is important for the
	 * resolution of the initializer on CSA bootstrap.
	 * <p>
	 * For information on redirect search for QualifiedStoragePriming.
	 */
	String getRedirectedAccessId();
	void setRedirectedAccessId(String redirectedAccessId);

}
