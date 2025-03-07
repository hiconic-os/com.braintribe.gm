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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import com.braintribe.gm.jdbc.api.GmUpdateBuilder;
import com.braintribe.util.jdbc.JdbcTools;
import com.braintribe.util.jdbc.SqlStatement;

/**
 * @author peter.gazdik
 */
public class GmDeleteBuilderImpl implements GmUpdateBuilder {

	private final GmTableImpl table;

	private final SqlStatement st = new SqlStatement();

	private final int index = 1;
	private int deleted;

	public GmDeleteBuilderImpl(GmTableImpl table) {
		this.table = table;
	}

	@Override
	public int where(String condition, Object... parameters) {
		writeQuery(condition, parameters);

		JdbcTools.withManualCommitConnection(table.db.dataSource, this::describeTask, this::doUpdate);

		return deleted;
	}

	private void writeQuery(String condition, Object... parameters) {
		st.sql = "delete from " + table.getName() + " where " + condition;
		st.parameters.addAll(Arrays.asList(parameters));
	}

	private String describeTask() {
		return "Deleting from table '" + table.getName();
	}

	private void doUpdate(Connection c) {
		GmDbTools.doUpdate(c, st.sql, (ps, boundColumns) -> bindAndExecute(ps));
	}

	private void bindAndExecute(PreparedStatement ps) throws SQLException {
		GmDbTools.bindParameters(ps, st.parameters, index);

		this.deleted = ps.executeUpdate();
	}

}
