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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Stream;

/**
 * @param <T>
 *            type of the value stored in given column
 * 
 * @see GmDb
 * 
 * @author peter.gazdik
 */
public interface GmColumn<T> {

	String getGmName();

	boolean isPrimaryKey();

	boolean isNotNull();

	boolean isAutoIncrement();

	/**
	 * Returns the SQL names of all columns that back this GM column.
	 * <p>
	 * Some GM columns are backed by multiple columns in the actual DB, e.g. when the DB supports Unicode Strings with limited length (Oracle with
	 * 4000 bytes limit), we have one String column for short strings, and one CLOB column for longer ones.
	 * <p>
	 * It is important to tell GM columns backed by a single SQL column, because only such columns can be {@link #isNotNull() not null},
	 * {@link #isPrimaryKey() primary keys}, have an index, or can have conditions specified when querying.
	 */
	List<String> getSqlColumns();

	/**
	 * Simple method to get an SQL column in case this GM column is backed by only a single column in the DB.
	 * 
	 * @see #getSqlColumns()
	 */
	String getSingleSqlColumn();

	Stream<String> streamSqlColumnDeclarations();

	/** @return true if at least one of the {@link #getSqlColumns() DB columns} which back this GM column if a BLOB or a CLOB. */
	boolean storesLobs();

	boolean isStoredAsLob(ResultSet rs);

	/**
	 * Resolves a value of this column from the row represented by given {@link ResultSet}.
	 * <p>
	 * Note that if this column {@link #storesLobs() stores values as LOBs} this method might internally open a new stream to retrieve the LOB, which
	 * might impact performance if many rows are retrieved. In such cases consider doing a select query with
	 * {@link GmSelectBuilder#rowsInBatchesOf(int)}.
	 */
	T getValue(ResultSet rs, GmSelectionContext context);

	void bindParameter(PreparedStatement statement, int index, T value);

	void afterStatementExecuted(PreparedStatement statement);

	// Convenience

	/** Shortcut for {@code row.getValue(this)}. This can be conveniently used as a method reference when mapping a row to a value of the column. */
	default T getRowValue(GmRow row) {
		return row.getValue(this);
	}

}
