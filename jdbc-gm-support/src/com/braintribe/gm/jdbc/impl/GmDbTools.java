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

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.braintribe.common.lcd.function.XBiConsumer;
import com.braintribe.gm.jdbc.api.GmColumn;
import com.braintribe.util.jdbc.JdbcTools;

/**
 * @author peter.gazdik
 */
public class GmDbTools {

	public static int bindParameters(PreparedStatement ps, List<?> parameters, int index) throws SQLException {
		for (Object param : parameters)
			bindParameter(ps, index++, param);

		return index;
	}

	private static void bindParameter(PreparedStatement ps, int index, Object parameter) throws SQLException {
		if (parameter instanceof Date)
			ps.setTimestamp(index, toTimeStamp((Date) parameter));
		else if (parameter instanceof Enum<?>)
			ps.setString(index, ((Enum<?>) parameter).name());
		else
			ps.setObject(index, parameter);
	}

	private static Timestamp toTimeStamp(Date d) {
		return d instanceof Timestamp ? (Timestamp) d : new Timestamp(d.getTime());
	}

	public static void doUpdate(Connection c, String sql, XBiConsumer<PreparedStatement, List<GmColumn<?>>> updateTask) {
		JdbcTools.withPreparedStatement(c, sql, () -> "", ps -> {
			List<GmColumn<?>> boundColumns = newList();

			try {
				updateTask.accept(ps, boundColumns);

			} finally {
				// This really just closes possibly open streams for LOBs
				for (GmColumn<?> column : boundColumns)
					column.afterStatementExecuted(ps);
			}
		});
	}

}
