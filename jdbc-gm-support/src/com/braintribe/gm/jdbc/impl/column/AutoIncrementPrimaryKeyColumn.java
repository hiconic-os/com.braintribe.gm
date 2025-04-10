package com.braintribe.gm.jdbc.impl.column;

import static java.util.Collections.singletonList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Stream;

import com.braintribe.gm.jdbc.api.GmSelectionContext;
import com.braintribe.util.jdbc.dialect.JdbcDialect;

/**
 * @author peter.gazdik
 */
public class AutoIncrementPrimaryKeyColumn extends AbstractGmColumn<Long> {

	private final JdbcDialect dialect;

	public AutoIncrementPrimaryKeyColumn(String name, JdbcDialect dialect) {
		super(name);
		this.dialect = dialect;
	}

	@Override
	public Stream<String> streamSqlColumnDeclarations() {
		return Stream.of(name + " " + x() + " PRIMARY KEY");
	}

	private String x() {
		switch (dialect.knownDbVariant()) {
			case H2:
				return "IDENTITY";
			case derby:
				return "INT GENERATED ALWAYS AS IDENTITY";
			case mssql:
				return "INT IDENTITY(1,1)";
			case mysql:
				return "INT AUTO_INCREMENT";
			case oracle:
				return "NUMBER GENERATED ALWAYS AS IDENTITY";
			case postgre:
				return "SERIAL";
			default:
				throw new UnsupportedOperationException("Auto-increment primary key is not supported for dialect: " + dialect.knownDbVariant());
		}
	}

	@Override
	protected Long tryGetValue(ResultSet rs, GmSelectionContext context) throws Exception {
		return rs.getLong(name);
	}

	@Override
	protected void tryBind(PreparedStatement ps, int index, Long value) throws Exception {
		ps.setLong(index, index);
	}

	// @formatter:off
	@Override public List<String> getSqlColumns() { return singletonList(name); }
	@Override protected Class<Long> type() { return Long.class; }

	@Override public boolean isPrimaryKey() { return true;	}
	@Override protected void setPrimaryKey(boolean primaryKey) {
		if (!primaryKey)
			throw new IllegalArgumentException("AutoIncrementPrimaryKeyColumn cannot be set to not be a primary key.");
	}

	@Override public boolean isNotNull() { return true;	}
	@Override public boolean isAutoIncrement() { return true; }
	@Override protected void setNotNull(boolean notNull) {
		if (!notNull)
			throw new IllegalArgumentException("AutoIncrementPrimaryKeyColumn cannot be set to not be a nullable.");
	}
	// @formatter:on
}
