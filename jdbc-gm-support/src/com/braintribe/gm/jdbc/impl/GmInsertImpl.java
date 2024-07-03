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
package com.braintribe.gm.jdbc.impl;

import static com.braintribe.utils.lcd.CollectionTools2.newIdentityMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.braintribe.gm.jdbc.api.GmColumn;
import com.braintribe.logging.Logger;

/**
 * @author peter.gazdik
 */
public class GmInsertImpl {

	public static void insert(GmTableImpl table, Map<GmColumn<?>, Object> values, Connection connection) {
		new GmInsertImpl(table, values, connection).doInsert();
	}

	private static final Logger log = Logger.getLogger(GmInsertImpl.class);

	private final GmTableImpl table;
	private final Map<GmColumn<?>, Object> values;
	private final Connection connection;

	private final Map<GmColumn<?>, Integer> columnToSelectionPosition = newIdentityMap();

	private String sql;

	private GmInsertImpl(GmTableImpl table, Map<GmColumn<?>, Object> values, Connection connection) {
		this.table = table;
		this.values = values;
		this.connection = connection;
		verifyValueColumns();
		verifyNonNullColumnsHaveValue();
	}

	private void verifyValueColumns() {
		for (GmColumn<?> gmColumn : values.keySet())
			if (!table.getColumns().contains(gmColumn))
				throw new IllegalArgumentException("Column '" + gmColumn.getGmName() + "' is not part of table '" + table.tableName
						+ "', cannot insert value: " + values.get(gmColumn));
	}

	private void verifyNonNullColumnsHaveValue() {
		for (GmColumn<?> gmColumn : table.notNullColumns())
			if (!values.containsKey(gmColumn))
				throw new IllegalArgumentException("Column '" + gmColumn.getGmName() + "' cannot have null value in table '" + table.tableName);
	}

	private void doInsert() {
		writeQuery();
		if (connection != null)
			doInsert(connection);
		else
			table.db.withManualCommitConnection(this::describeTask, this::doInsert);
	}

	private void writeQuery() {
		StringJoiner sjColumns = new StringJoiner(", ");
		StringJoiner sjParams = new StringJoiner(",");

		int counter = 1;
		for (GmColumn<?> column : table.getColumns()) {
			List<String> sqlColumns = column.getSqlColumns();

			columnToSelectionPosition.put(column, counter);
			counter += sqlColumns.size();

			for (String sqlColumn : sqlColumns) {
				sjColumns.add(sqlColumn);
				sjParams.add("?");
			}
		}

		sql = "insert into " + table.getName() + " (" + sjColumns + ") values (" + sjParams + ")";
	}

	private String describeTask() {
		return "Inserting row into table '" + table.getName() + "'. Values: " + values;
	}

	private void doInsert(Connection c) {
		GmDbTools.doUpdate(c, sql, this::bindAndExecute);
	}

	private void bindAndExecute(PreparedStatement ps, List<GmColumn<?>> boundColumns) throws SQLException {
		for (GmColumn<?> column : table.getColumns()) {
			Integer position = columnToSelectionPosition.get(column);
			Object value = values.get(column);

			((GmColumn<Object>) column).bindParameter(ps, position, value);
			boundColumns.add(column);
		}

		int n = ps.executeUpdate();
		if (n != 1)
			log.warn("Insert might have failed, the returned value was not 1 but: " + n + ". Statement: " + sql + ", values: " + values);
	}

}
