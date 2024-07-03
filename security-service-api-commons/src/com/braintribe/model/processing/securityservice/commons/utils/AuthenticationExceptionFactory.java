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
package com.braintribe.model.processing.securityservice.commons.utils;

import java.util.function.Function;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.ReasonException;
import com.braintribe.gm.model.security.reason.AuthenticationFailure;
import com.braintribe.gm.model.security.reason.InvalidCredentials;
import com.braintribe.gm.model.security.reason.SessionExpired;
import com.braintribe.gm.model.security.reason.SessionNotFound;
import com.braintribe.model.processing.securityservice.api.exceptions.AuthenticationException;
import com.braintribe.model.processing.securityservice.api.exceptions.ExpiredSessionException;
import com.braintribe.model.processing.securityservice.api.exceptions.InvalidCredentialsException;
import com.braintribe.model.processing.securityservice.api.exceptions.SessionNotFoundException;

public class AuthenticationExceptionFactory implements Function<Reason, RuntimeException> {
	@Override
	public RuntimeException apply(Reason reason) {
		if (reason instanceof AuthenticationFailure) {
			return transpose((AuthenticationFailure)reason);
		}
		else {
			return new ReasonException(reason);
		}
	}

	private AuthenticationException transpose(AuthenticationFailure reason) {
		
		ReasonException reasonException = new ReasonException(reason);
		
		if (reason instanceof InvalidCredentials) {
			return new InvalidCredentialsException(reason.getText(), reasonException);
		}
		else if (reason instanceof SessionNotFound) {
			return new SessionNotFoundException(reason.getText(), reasonException);
		}
		else if (reason instanceof SessionExpired) {
			return new ExpiredSessionException(reason.getText(), reasonException);
		}
		else 
			return new AuthenticationException(reason.getText(), reasonException);
	}
}
