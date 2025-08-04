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
package com.braintribe.model.securityservice.web;

import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.gm.model.reason.essential.UnsupportedOperation;
import com.braintribe.gm.model.security.reason.AuthenticationFailure;
import com.braintribe.gm.model.security.reason.Forbidden;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.UnsatisfiedBy;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Description("Derivates of this request are used to login in web environment resulting in a http only cookie.")
@Abstract
@UnsatisfiedBy(Forbidden.class)
@UnsatisfiedBy(AuthenticationFailure.class)
@UnsatisfiedBy(InvalidArgument.class)
@UnsatisfiedBy(UnsupportedOperation.class)
public interface WebAuthenticate extends WebAuthorizationRequest {

	EntityType<WebAuthenticate> T = EntityTypes.T(WebAuthenticate.class);
	
	String getLocale();
	void setLocale(String locale);
	
	@Initializer("false")
	boolean getStaySignedIn();
	void setStaySignedIn(boolean staySignedIn);
}
