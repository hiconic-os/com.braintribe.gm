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

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Taken from org.hibernate.dialect.pagination.LimitHandler.
 */
public interface LimitHandler {
	/**
	 * Does this handler support some form of limiting query results
	 * via a SQL clause?
	 *
	 * @return True if this handler supports some form of LIMIT.
	 */
	boolean supportsLimit();

	/**
	 * Does this handler's LIMIT support (if any) additionally
	 * support specifying an offset?
	 *
	 * @return True if the handler supports an offset within the limit support.
	 */
	boolean supportsLimitOffset();

	/**
	 * Return processed SQL query.
	 *
     * @param sql       the SQL query to process.
     * @param selection the selection criteria for rows.
     *
	 * @return Query statement with LIMIT clause applied.
	 */
	String processSql(String sql, RowSelection selection);

	/**
	 * Bind parameter values needed by the LIMIT clause before original SELECT statement.
	 *
     * @param selection the selection criteria for rows.
	 * @param statement Statement to which to bind limit parameter values.
	 * @param index Index from which to start binding.
	 * @return The number of parameter values bound.
	 * @throws SQLException Indicates problems binding parameter values.
	 */
	int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException;

	/**
	 * Bind parameter values needed by the LIMIT clause after original SELECT statement.
	 *
     * @param selection the selection criteria for rows.
	 * @param statement Statement to which to bind limit parameter values.
	 * @param index Index from which to start binding.
	 * @return The number of parameter values bound.
	 * @throws SQLException Indicates problems binding parameter values.
	 */
	int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException;

	/**
	 * Use JDBC API to limit the number of rows returned by the SQL query. Typically handlers that do not
	 * support LIMIT clause should implement this method.
	 *
     * @param selection the selection criteria for rows.
	 * @param statement Statement which number of returned rows shall be limited.
	 * @throws SQLException Indicates problems while limiting maximum rows returned.
	 */
	void setMaxRows(RowSelection selection, PreparedStatement statement) throws SQLException;
}
