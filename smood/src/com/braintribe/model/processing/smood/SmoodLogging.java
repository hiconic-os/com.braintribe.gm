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
package com.braintribe.model.processing.smood;

import com.braintribe.logging.Logger;
import com.braintribe.model.processing.query.tools.QueryPlanPrinter;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.queryplan.QueryPlan;

/**
 * @author peter.gazdik
 */
public class SmoodLogging {

	private static Logger smoodLogger = Logger.getLogger(Smood.class);

	public static void selectQuery(SelectQuery query) {
		if (smoodLogger.isTraceEnabled())
			smoodLogger.trace("Planning select query: " + QueryPlanPrinter.printSafe(query));
	}

	public static void propertyQuery(PropertyQuery query) {
		if (smoodLogger.isTraceEnabled())
			smoodLogger.trace("Smart PropetyQuery: " + QueryPlanPrinter.print(query));
	}

	public static void queryPlan(QueryPlan queryPlan) {
		if (smoodLogger.isTraceEnabled())
			smoodLogger.trace(QueryPlanPrinter.printSafe(queryPlan));
	}

	public static void selectQueryEvaluationFinished() {
		if (smoodLogger.isTraceEnabled())
			smoodLogger.trace("Query evaluation finished!");
	}
}
