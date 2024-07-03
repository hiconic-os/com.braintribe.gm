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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.braintribe.gm.jdbc.api.GmSelectionContext;
import com.braintribe.gm.jdbc.impl.column.AbstractGmColumn.AbstractSingularGmColumn;

/**
 * @author peter.gazdik
 */
public abstract class SimpleSingularGmColumn<T> extends AbstractSingularGmColumn<T> {

	private final String sqlType;

	public SimpleSingularGmColumn(String name, String sqlType) {
		super(name);
		this.sqlType = sqlType;
	}

	@Override
	protected String sqlType() {
		return sqlType;
	}

	// @formatter:off
	@Override protected void tryBind(PreparedStatement ps, int index, T value) throws SQLException { ps.setObject(index, value); }
	@Override protected T tryGetValue(ResultSet rs, GmSelectionContext context) throws SQLException {
		// If you see an AbstractMethodError here, your JDBC driver is too old. Happens with com.oracle:ojdbc6 for example
		T result = rs.getObject(name, type());
		// MySql and Oracle tend to return default primitive values rather than null from rs.getObject(...)
		return result == null || rs.wasNull() ? null : result;
	}
	// @formatter:on
	
	
	// @formatter:off
	public static class BooleanColumn extends SimpleSingularGmColumn<Boolean> {
		public BooleanColumn(String name, String sqlType) { super(name, sqlType); }
		@Override protected Class<Boolean> type() { return Boolean.class; }
	}

	public static class IntegerColumn extends SimpleSingularGmColumn<Integer> {
		public IntegerColumn(String name, String sqlType) { super(name, sqlType); }
		@Override protected Class<Integer> type() { return Integer.class; }
	}

	public static class LongColumn extends SimpleSingularGmColumn<Long> {
		public LongColumn(String name, String sqlType) { super(name, sqlType); }
		@Override protected Class<Long> type() { return Long.class; }
	}

	public static class FloatColumn extends SimpleSingularGmColumn<Float> {
		public FloatColumn(String name, String sqlType) { super(name, sqlType); }
		@Override protected Class<Float> type() { return Float.class; }
	}

	public static class DoubleColumn extends SimpleSingularGmColumn<Double> {
		public DoubleColumn(String name, String sqlType) { super(name, sqlType); }
		@Override protected Class<Double> type() { return Double.class; }
	}

	public static class BigDecimalColumn extends SimpleSingularGmColumn<BigDecimal> {
		public BigDecimalColumn(String name, String sqlType) { super(name, sqlType); }
		@Override protected Class<BigDecimal> type() { return BigDecimal.class; }
	}

	public static class StringColumn extends SimpleSingularGmColumn<String> {
		public StringColumn(String name, String sqlType) { super(name, sqlType);  }
		@Override protected Class<String> type() { return String.class; }
	}
	// @formatter:on

}
