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
package com.braintribe.gm.model.reason.meta;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.EntityTypeMetaData;

/**
 * This metadata can be placed on subtypes of com.braintribe.gm.model.reason.Reason
 * to define which HTTP status code is to be returned if an reason of the type has occurred as an error.
 * 
 * @author Dirk Scheffler
 */
public interface HttpStatusCode extends EntityTypeMetaData {
	EntityType<HttpStatusCode> T = EntityTypes.T(HttpStatusCode.class);

	String code = "code";
	
	int getCode();
	void setCode(int code);
	
}
