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

import java.util.Comparator;

import com.braintribe.model.securityservice.credentials.Credentials;
import com.braintribe.model.securityservice.credentials.HasUserIdentification;

public class CredentialsComparator implements Comparator<Credentials> {

	public static final CredentialsComparator INSTANCE = new CredentialsComparator();
	
	@Override
	public int compare(Credentials o1, Credentials o2) {
		
		if (o1 == o2) {
			return 0;
		} else if (o1 == null) {
			return -1;
		} else if (o2 == null) {
			return 1;
		}
		
		if (o1 instanceof HasUserIdentification && o2 instanceof HasUserIdentification) {
			return UserIdentificationComparator.INSTANCE.compare(((HasUserIdentification)o1).getUserIdentification(), ((HasUserIdentification)o2).getUserIdentification());
		} else {
			return Integer.compare(o1.hashCode(), o2.hashCode());
		}
	}
	
}
