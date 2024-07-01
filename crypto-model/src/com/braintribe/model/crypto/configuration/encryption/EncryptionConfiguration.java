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
package com.braintribe.model.crypto.configuration.encryption;


import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import com.braintribe.model.crypto.configuration.CryptoConfiguration;


public interface EncryptionConfiguration extends CryptoConfiguration {

	EntityType<EncryptionConfiguration> T = EntityTypes.T(EncryptionConfiguration.class);
	
	static final String mode = "mode";
	static final String padding = "padding";

	@Name("Mode")
	@Description("The mode of the Cipher. See https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher for possible modes. When this is not set, a default will be applied.")
	String getMode();
	void setMode(String mode);

	@Name("Padding")
	@Description("The padding of the Cipher.")
	String getPadding();
	void setPadding(String padding);
	
}
