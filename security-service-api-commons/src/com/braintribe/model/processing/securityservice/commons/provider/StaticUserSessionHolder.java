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
package com.braintribe.model.processing.securityservice.commons.provider;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.usersession.UserSession;
import com.braintribe.provider.Holder;

public class StaticUserSessionHolder extends Holder<UserSession> {

	private static final Logger log = Logger.getLogger(StaticUserSessionHolder.class);

	@Required
	@Configurable
	public void setUserSession(UserSession userSession) {
		this.value = userSession;
	}

	@Override
	public void accept(UserSession newUserSession) throws RuntimeException {
		UserSession previousUserSession = this.value;
		this.value = newUserSession;
		
		if (log.isDebugEnabled()) {
			log.debug("Static user session holder updated. Replaced previous [ "+previousUserSession+" ] with [ "+this.value+" ]");
		}
	}
	
}
