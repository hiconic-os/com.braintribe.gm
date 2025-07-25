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
package com.braintribe.gm.jdbc.api;

/**
 * @author peter.gazdik
 */
public interface GmUpdateBuilder {

	default <T> int whereColumn(GmColumn<T> column, T value) {
		return where(column.getSingleSqlColumn() + " = ?", value);
	}

	/**
	 * Examples: <code>where(dateColumn.getSingleSqlColumn() + " > ?", yesterday())</code>
	 * 
	 * @return number of updated/deleted rows
	 */
	int where(String condition, Object... parameters);

}