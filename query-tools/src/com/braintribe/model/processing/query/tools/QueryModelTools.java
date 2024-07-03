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
package com.braintribe.model.processing.query.tools;

import com.braintribe.model.query.From;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.Source;
import com.braintribe.utils.lcd.CommonTools;

/**
 * @author peter.gazdik
 */
public class QueryModelTools {

	public static String printWithAlias(Source source) {
		if (source instanceof From)
			return printWithAlias((From) source);

		if (source instanceof Join)
			return printWithAlias((Join) source);

		if (source != null)
			return source.entityType().getShortName();

		return "null";
	}

	public static String printWithAlias(From from) {
		if (from.getName() != null)
			return from.getName();
		else
			return CommonTools.getClassNameFromFullyQualifiedClassName(from.getEntityTypeSignature());
	}

	public static String printWithAlias(Join join) {
		if (join.getName() != null)
			return join.getName();
		else
			return printWithAlias(join.getSource()) + "." + join.getProperty();
	}

}
