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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.csa;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Represents a custom initializer type that is bound from a module.
 * <p>
 * This kind of initializer is used when the same type of initialization is expected to be done many times, with just minor variations. In such case
 * an implementation is bound from a module, and multiple configuration assets, which brings the input files for this implementation.
 * <p>
 * The {@link #getName() name} property represents the qualified name of the input asset. The files are located inside the data folder in a dedicated
 * sub-folder, whose name is derived the same name as the folder for {@link ManInitializer}. This means the folder to get the folder name out of the
 * name property you have to replace the illegal characters with an underscore.
 * <p>
 * For more information see the platform asset nature called DynamicInitilizerInput.
 *
 * @author peter.gazdik
 */
public interface DynamicInitializer extends CustomInitializer {

	EntityType<DynamicInitializer> T = EntityTypes.T(DynamicInitializer.class);

	String DEFUALT_FACTORY_NAME = "default";

	/** Name of the module which binds the initializer. Format: ${groupId}:${artifactId} */
	String getModuleName();
	void setModuleName(String moduleId);

}
