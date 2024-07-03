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
package com.braintribe.model.execution.persistence;

import com.braintribe.model.generic.StandardStringIdentifiable;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Abstract
public interface HasUserDetails extends StandardStringIdentifiable {

	EntityType<HasUserDetails> T = EntityTypes.T(HasUserDetails.class);

	String clientSessionId = "clientSessionId";
	String clientUsername = "clientUsername";
	String clientAddress = "clientAddress";

	void setClientSessionId(String clientSessionId);
	@Name("Client Session Id")
	@Description("The session Id of the requestor.")
	String getClientSessionId();

	void setClientUsername(String clientUsername);
	@Name("Client Username")
	@Description("The username of the requestor.")
	String getClientUsername();

	void setClientAddress(String clientAddress);
	@Name("Client Address")
	@Description("The IP address or hostname of the requestor.")
	String getClientAddress();

}
