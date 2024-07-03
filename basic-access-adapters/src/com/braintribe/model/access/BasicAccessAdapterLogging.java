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
package com.braintribe.model.access;

import com.braintribe.logging.Logger;
import com.braintribe.model.processing.query.tools.QueryPlanPrinter;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.queryplan.QueryPlan;

/**
 * @author peter.gazdik
 */
public class BasicAccessAdapterLogging {

	private static Logger baaLogger = Logger.getLogger(BasicAccessAdapter.class);

	public static void selectQuery(SelectQuery query) {
		if (baaLogger.isTraceEnabled()) {
			baaLogger.trace("Planning select query: " + QueryPlanPrinter.printSafe(query));
		}
	}

	public static void propertyQuery(PropertyQuery query) {
		if (baaLogger.isTraceEnabled()) {
			baaLogger.trace("Smart PropetyQuery: " + QueryPlanPrinter.print(query));
		}
	}

	public static void queryPlan(QueryPlan queryPlan) {
		if (baaLogger.isTraceEnabled()) {
			baaLogger.trace(QueryPlanPrinter.printSafe(queryPlan));
		}
	}

	public static void selectQueryEvaluationFinished() {
		if (baaLogger.isTraceEnabled()) {
			baaLogger.trace("Query evaluation finished!");
		}
	}
}
