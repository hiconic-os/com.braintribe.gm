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
package com.braintribe.model.processing.query.stringifier.experts.basic;

import java.util.Date;

import com.braintribe.model.processing.query.api.stringifier.QueryStringifierRuntimeException;
import com.braintribe.model.processing.query.api.stringifier.experts.Stringifier;
import com.braintribe.model.processing.query.api.stringifier.experts.StringifierContext;
import com.braintribe.utils.format.api.CustomDateFormat;
import com.braintribe.utils.format.lcd.FormatTool;

public class DateStringifier implements Stringifier<Date, StringifierContext> {
	@Override
	public String stringify(Date date, StringifierContext context) throws QueryStringifierRuntimeException {
		StringBuilder queryString = new StringBuilder();
		// Get CustomDateFormat from FormatTool expert
		final CustomDateFormat dateFormat = FormatTool.getExpert().getDateFormat();
		if (dateFormat != null) {
			queryString.append("date(");

			// Format string to the parser date-string
			queryString.append(dateFormat.formatDate(date, "yyyy'Y,' MM'M,' dd'D,' HH'H,' mm'm,' ss'S,' SSS's,' Z'Z'"));

			// Return parsed date-string
			queryString.append(")");
			return queryString.toString();
		} else {
			// Expert not found, throw Exception
			throw new QueryStringifierRuntimeException("Could not find format expert: " + CustomDateFormat.class.getName());
		}
	}
}
