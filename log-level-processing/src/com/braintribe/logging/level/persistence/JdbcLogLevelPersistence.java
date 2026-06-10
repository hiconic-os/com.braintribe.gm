package com.braintribe.logging.level.persistence;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.braintribe.logging.Logger;

public class JdbcLogLevelPersistence implements LogLevelPersistence {
	private static final Logger logger = Logger.getLogger(JdbcLogLevelPersistence.class);

	private static final String TABLE_SUFFIX = "_LOG_LEVELS";
	private static final String DEFAULT_TABLE_NAME = "LOG_LEVELS";
	private static final String COLUMN_NAME = "NAME";
	private static final String COLUMN_LEVEL = "LEVEL";
	private static final String VALID_TABLE_NAME_PATTERN = "[A-Za-z0-9_]+";

	private DataSource dataSource;
	private String tableName;
	
	public JdbcLogLevelPersistence(DataSource dataSource, String tablePrefix) {
		super();
		this.dataSource = dataSource;
		this.tableName = tableName(tablePrefix);
	}

	@Override
	public Map<String, String> getLogLevels() {
		Map<String, String> levels = new LinkedHashMap<>();

		try {
			ensureTable();
		} catch (RuntimeException e) {
			logger.warn("Could not ensure JDBC log levels table. Returning no persistent log levels.", e);
			return levels;
		}

		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(selectSql());
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				levels.put(resultSet.getString(1), resultSet.getString(2));
			}
		} catch (SQLException e) {
			logger.warn("Could not read log levels from table " + tableName + ". Returning no persistent log levels.", e);
		}

		return levels;
	}

	@Override
	public void updateLogLevels(Map<String, String> levels, Set<String> namesToRemove) {
		boolean hasLevels = levels != null && levels.isEmpty() == false;
		boolean hasNamesToRemove = namesToRemove != null && namesToRemove.isEmpty() == false;

		if (hasLevels == false && hasNamesToRemove == false) {
			return;
		}

		ensureTable();

		try (Connection connection = dataSource.getConnection()) {
			boolean autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);

			try (PreparedStatement deleteStatement = connection.prepareStatement(deleteByNameSql());
					PreparedStatement insertStatement = connection.prepareStatement(insertSql())) {
				if (hasNamesToRemove) {
					for (String name: namesToRemove) {
						deleteStatement.setString(1, name);
						deleteStatement.addBatch();
					}
				}

				if (hasLevels) {
					for (Map.Entry<String, String> entry: levels.entrySet()) {
						deleteStatement.setString(1, entry.getKey());
						deleteStatement.addBatch();

						insertStatement.setString(1, entry.getKey());
						insertStatement.setString(2, entry.getValue());
						insertStatement.addBatch();
					}
				}

				deleteStatement.executeBatch();
				if (hasLevels) {
					insertStatement.executeBatch();
				}
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(autoCommit);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Could not update log levels in table " + tableName, e);
		}
	}

	@Override
	public void clearLogLevels() {
		ensureTable();

		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(deleteAllSql())) {
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Could not clear log levels from table " + tableName, e);
		}
	}

	private void ensureTable() {
		try (Connection connection = dataSource.getConnection()) {
			if (tableExists(connection)) {
				return;
			}

			try (Statement statement = connection.createStatement()) {
				statement.executeUpdate(createTableSql());
			}
		} catch (SQLException e) {
			throw new RuntimeException("Could not ensure log levels table " + tableName, e);
		}
	}

	private boolean tableExists(Connection connection) throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();
		String[] candidates = { tableName, tableName.toUpperCase(), tableName.toLowerCase() };

		for (String candidate: candidates) {
			try (ResultSet tables = metaData.getTables(null, null, candidate, new String[] { "TABLE" })) {
				if (tables.next()) {
					return true;
				}
			}
		}

		return false;
	}

	private static String tableName(String tablePrefix) {
		String name = tablePrefix == null || tablePrefix.trim().isEmpty() ? DEFAULT_TABLE_NAME : tablePrefix.trim() + TABLE_SUFFIX;

		if (name.matches(VALID_TABLE_NAME_PATTERN) == false) {
			throw new IllegalArgumentException("Invalid log levels table name: " + name);
		}

		return name;
	}

	private String selectSql() {
		return "select " + COLUMN_NAME + ", " + COLUMN_LEVEL + " from " + tableName;
	}

	private String insertSql() {
		return "insert into " + tableName + " (" + COLUMN_NAME + ", " + COLUMN_LEVEL + ") values (?, ?)";
	}

	private String deleteByNameSql() {
		return "delete from " + tableName + " where " + COLUMN_NAME + " = ?";
	}

	private String deleteAllSql() {
		return "delete from " + tableName;
	}

	private String createTableSql() {
		return "create table " + tableName + " (" + COLUMN_NAME + " varchar(1024) not null primary key, " + COLUMN_LEVEL + " varchar(255))";
	}
}
