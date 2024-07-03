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

import com.braintribe.model.securityservice.credentials.identification.EmailIdentification;
import com.braintribe.model.securityservice.credentials.identification.UserIdentification;
import com.braintribe.model.securityservice.credentials.identification.UserNameIdentification;

public class UserIdentificationComparator implements Comparator<UserIdentification> {

	public static final UserIdentificationComparator INSTANCE = new UserIdentificationComparator();

	@Override
	public int compare(UserIdentification o1, UserIdentification o2) {

		if (o1 == o2) {
			return 0;
		}

		if (o1 == null) {
			return -1;
		}

		if (o2 == null) {
			return 1;
		}

		if (o1 instanceof UserNameIdentification && o2 instanceof UserNameIdentification) {
			return compareUserNameIdentification((UserNameIdentification) o1, (UserNameIdentification) o2);
		}

		if (o1 instanceof EmailIdentification && o2 instanceof EmailIdentification) {
			return compareEmailIdentification((EmailIdentification) o1, (EmailIdentification) o2);
		}

		return -1;
	}

	private static int compareUserNameIdentification(UserNameIdentification o1, UserNameIdentification o2) {
		return compare(o1.getUserName(), o2.getUserName());
	}

	private static int compareEmailIdentification(EmailIdentification o1, EmailIdentification o2) {
		return compare(o1.getEmail(), o2.getEmail());
	}

	private static int compare(String a, String b) {
		if (a == null && b == null) {
			return 0;
		} else if (a == null) {
			return -1;
		} else if (b == null) {
			return 1;
		} else {
			return a.compareTo(b);
		}
	}

}
