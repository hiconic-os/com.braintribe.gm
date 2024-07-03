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
package com.braintribe.gm.jdbc.impl.column;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.braintribe.gm.jdbc.api.GmSelectionContext;
import com.braintribe.gm.jdbc.impl.column.AbstractGmColumn.AbstractSingularGmColumn;
import com.braintribe.util.jdbc.dialect.JdbcDialect;

/**
 * @author peter.gazdik
 */
public class DateColumn extends AbstractSingularGmColumn<Date> {

	private final JdbcDialect dialect;

	public DateColumn(String name, JdbcDialect dialect) {
		super(name);
		this.dialect = dialect;
	}

	@Override
	protected String sqlType() {
		return dialect.timestampType();
	}

	@Override
	protected Class<Date> type() {
		return Date.class;
	}

	@Override
	protected Date tryGetValue(ResultSet rs, GmSelectionContext context) throws SQLException {
		Timestamp ts = rs.getTimestamp(name);
		return ts == null ? ts : new Date(ts.getTime());
	}

	@Override
	protected void tryBind(PreparedStatement ps, int index, Date value) throws SQLException {
		Timestamp ts = value == null ? null : new Timestamp(value.getTime());
		ps.setTimestamp(index, ts);
	}

}
