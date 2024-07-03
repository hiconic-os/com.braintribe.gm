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
package com.braintribe.util.jdbc.pagination;

/**
 * Taken from org.hibernate.dialect.pagination.LimitHelper.
 */
public class LimitHelper {
	/**
	 * Is a max row limit indicated?
	 *
	 * @param selection The row selection options
	 *
	 * @return Whether a max row limit was indicated
	 */
	public static boolean hasMaxRows(RowSelection selection) {
		return selection != null && selection.getMaxRows() != null && selection.getMaxRows() > 0;
	}

	/**
	 * Should limit be applied?
	 *
	 * @param limitHandler The limit handler
	 * @param selection The row selection
	 *
	 * @return Whether limiting is indicated
	 */
	public static boolean useLimit(LimitHandler limitHandler, RowSelection selection) {
		return limitHandler.supportsLimit() && hasMaxRows( selection );
	}

	/**
	 * Is a first row limit indicated?
	 *
	 * @param selection The row selection options
	 *
	 * @return Whether a first row limit in indicated
	 */
	public static boolean hasFirstRow(RowSelection selection) {
		return getFirstRow( selection ) > 0;
	}

	/**
	 * Retrieve the indicated first row for pagination
	 *
	 * @param selection The row selection options
	 *
	 * @return The first row
	 */
	public static int getFirstRow(RowSelection selection) {
		return ( selection == null || selection.getFirstRow() == null ) ? 0 : selection.getFirstRow();
	}

	private LimitHelper() {
	}
}
