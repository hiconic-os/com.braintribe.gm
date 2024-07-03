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
package com.braintribe.model.processing.query.api.stringifier.experts.resolver;

import com.braintribe.model.query.Source;

public interface SelectionAliasResolver {
	/**
	 * Returns null if no alias can be determined.
	 */
	public String getAliasForSource(Source source);
	
	/**
	 * Returns the propertyName specific for the given source (for example, based on some metadata, such as Name).
	 * @param source - The Source of the query
	 */
	public default String getPropertyNameForSource(Source source, String propertyName) {
		return propertyName;
	}
}
