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
package com.braintribe.model.exchangeapi;

import com.braintribe.model.exchange.ExchangePackage;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Abstract
public interface WriteToResourceRequest extends ExchangeRequest {
	
	EntityType<WriteToResourceRequest> T = EntityTypes.T(WriteToResourceRequest.class);
	
	@Initializer("true")
	boolean getPrettyOutput();
	void setPrettyOutput(boolean prettyOutput);
	
	@Initializer("true")
	boolean getStabilizeOrder();
	void setStabilizeOrder(boolean stabilizeOrder);

	boolean getWriteEmptyProperties();
	void setWriteEmptyProperties(boolean writeEmptyProperties);
	
	/**
	 * If set to true, this option will add the binaries for any resource of the exported assembly to the exchange-package file.
	 * This option only applies in case the exported assembly is an {@link ExchangePackage}.
	 */
	@Initializer("true")
	boolean getAddResourceBinaries();
	void setAddResourceBinaries(boolean addResourceBinaries);


}
