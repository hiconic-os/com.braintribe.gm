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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import com.braintribe.gm.jdbc.api.GmColumn;
import com.braintribe.gm.jdbc.api.GmUpdateBuilder;
import com.braintribe.util.jdbc.JdbcTools;
import com.braintribe.util.jdbc.SqlStatement;

/**
 * @author peter.gazdik
 */
public class GmUpdateBuilderImpl implements GmUpdateBuilder {

	private final GmTableImpl table;
	private final Map<GmColumn<?>, Object> values;

	private final Map<GmColumn<?>, Integer> columnToBindingPosition = newIdentityMap();

	private final SqlStatement st = new SqlStatement();

	private int index = 1;
	private int updated;

	public GmUpdateBuilderImpl(GmTableImpl table, Map<GmColumn<?>, Object> values) {
		this.table = table;
		this.values = values;
	}

	@Override
	public int where(String condition, Object... parameters) {
		writeQuery();
		addConditionToQuery(condition, parameters);

		JdbcTools.withManualCommitConnection(table.db.dataSource, this::describeTask, this::doUpdate);

		return updated;
	}

	private void writeQuery() {
		StringJoiner sjColumns = new StringJoiner(", ");

		for (GmColumn<?> column : values.keySet()) {
			List<String> sqlColumns = column.getSqlColumns();

			columnToBindingPosition.put(column, index);
			index += sqlColumns.size();

			for (String sqlColumn : sqlColumns)
				sjColumns.add(sqlColumn + " = ?");
		}

		st.sql = "update " + table.getName() + " set " + sjColumns;
	}

	private void addConditionToQuery(String condition, Object... parameters) {
		st.sql += " where " + condition;
		st.parameters.addAll(Arrays.asList(parameters));
	}

	private String describeTask() {
		return "Updating rows in table '" + table.getName() + "'. Values: " + values;
	}

	private void doUpdate(Connection c) {
		GmDbTools.doUpdate(c, st.sql, this::bindAndExecute);
	}

	private void bindAndExecute(PreparedStatement ps, List<GmColumn<?>> boundColumns) throws SQLException {
		for (Entry<GmColumn<?>, Object> entry : values.entrySet()) {
			GmColumn<?> column = entry.getKey();
			Object value = entry.getValue();
			int index = columnToBindingPosition.get(column);

			((GmColumn<Object>) column).bindParameter(ps, index, value);
			boundColumns.add(column);
		}

		GmDbTools.bindParameters(ps, st.parameters, index);

		this.updated = ps.executeUpdate();
	}

}
