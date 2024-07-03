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
package com.braintribe.model.processing.meta.cmd.context.experts;

import java.util.Date;

import com.braintribe.model.meta.selector.DatePropertyDiscriminator;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;

public class DatePropertyDiscriminatorExpert extends SimplePropertyDiscriminatorExpert<DatePropertyDiscriminator, Date> {

	public DatePropertyDiscriminatorExpert() {
		super(Date.class);
	}

	@Override
	public boolean matches(DatePropertyDiscriminator selector, SelectorContext context) throws Exception {
		Date actualValue = getPropertyCasted(selector, context);

		if (actualValue != null) {
			Date discriminatorValue = selector.getDiscriminatorValue();
			return actualValue.equals(discriminatorValue);
		}

		return false;
	}

}
