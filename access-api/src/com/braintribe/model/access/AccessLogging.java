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
import com.braintribe.logging.Logger.LogLevel;
import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.processing.query.stringifier.BasicQueryStringifier;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.SelectQuery;

/**
 * Needs {@link BasicQueryStringifier} <br>
 * Needs com.braintribe.model.processing.manipulation.marshaller.ManipulationStringifier <br>
 * 
 * @author peter.gazdik
 */
public class AccessLogging {

	protected Logger queryLogger;
	protected Logger manipulationLogger;

	protected LogLevel entityQueryLevel = LogLevel.TRACE;
	protected LogLevel propertyQueryLevel = LogLevel.TRACE;
	protected LogLevel selectQueryLevel = LogLevel.TRACE;
	protected LogLevel manipulationLevel = LogLevel.TRACE;

	public AccessLogging(String loggerRoot, Class<?> clazz) {
		queryLogger = Logger.getLogger(loggerRoot + ".query", clazz);
		manipulationLogger = Logger.getLogger(loggerRoot + ".manipulation", clazz);
	}

	public void entityQuery(EntityQuery query) {
		queryLogger.log(entityQueryLevel, () -> printQuery(query));
	}

	public void propertyQuery(PropertyQuery query) {
		queryLogger.log(propertyQueryLevel, () -> printQuery(query));
	}

	public void selectQuery(SelectQuery query) {
		queryLogger.log(selectQueryLevel, () -> printQuery(query));
	}

	public void manipulation(ManipulationRequest mr) {
		manipulationLogger.log(manipulationLevel, () -> printManipulation(mr.getManipulation()));
	}

	protected String printQuery(Query query) {
		return query.stringify();
	}

	protected String printManipulation(Manipulation m) {
		return m.stringify();
	}
}
