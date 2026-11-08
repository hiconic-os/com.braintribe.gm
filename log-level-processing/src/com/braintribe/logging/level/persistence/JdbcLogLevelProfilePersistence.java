package com.braintribe.logging.level.persistence;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

/** Minimal JDBC persistence for runtime profile entries and their optional active cascade. */
public class JdbcLogLevelProfilePersistence implements LogLevelProfilePersistence {
	private static final String LEVEL_TABLE = "RUNTIME_LOG_LEVEL";
	private static final String CASCADE_TABLE = "RUNTIME_LOG_LEVEL_PROFILE_CASCADE";
	private static final String VALID_TABLE_NAME_PATTERN = "[A-Za-z0-9_]+";

	private final DataSource dataSource;
	private final String levelTable;
	private final String cascadeTable;

	public JdbcLogLevelProfilePersistence(DataSource dataSource, String tablePrefix) {
		this.dataSource = dataSource;
		String prefix = tablePrefix == null || tablePrefix.trim().isEmpty() ? "" : tablePrefix.trim() + "_";
		this.levelTable = valid(prefix + LEVEL_TABLE);
		this.cascadeTable = valid(prefix + CASCADE_TABLE);
	}

	@Override
	public Map<String, Map<String, String>> getProfiles() {
		ensureTables();
		Map<String, Map<String, String>> result = new LinkedHashMap<>();
		String sql = "select PROFILE, NAME, LEVEL from " + levelTable + " order by PROFILE, NAME";
		try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rows = statement.executeQuery()) {
			while (rows.next())
				result.computeIfAbsent(rows.getString(1), p -> new LinkedHashMap<>()).put(rows.getString(2), rows.getString(3));
		} catch (SQLException e) {
			throw new RuntimeException("Could not read runtime log-level profiles", e);
		}
		return result;
	}

	@Override
	public List<String> getProfileCascade() {
		ensureTables();
		List<String> result = new ArrayList<>();
		String sql = "select PROFILE from " + cascadeTable + " order by POSITION";
		try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet rows = statement.executeQuery()) {
			while (rows.next())
				result.add(rows.getString(1));
		} catch (SQLException e) {
			throw new RuntimeException("Could not read runtime log-level profile cascade", e);
		}
		return result;
	}

	@Override
	public void updateProfileLogLevels(String profile, Map<String, String> levels, Set<String> namesToRemove) {
		String requiredProfile = requireProfile(profile);
		Map<String, String> updates = levels == null ? Collections.<String, String>emptyMap() : levels;
		Set<String> removals = namesToRemove == null ? Collections.<String>emptySet() : namesToRemove;
		if (updates.isEmpty() && removals.isEmpty())
			return;
		ensureTables();
		try (Connection connection = dataSource.getConnection()) {
			boolean autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			String deleteSql = "delete from " + levelTable + " where PROFILE = ? and NAME = ?";
			String insertSql = "insert into " + levelTable + " (PROFILE, NAME, LEVEL) values (?, ?, ?)";
			try (PreparedStatement delete = connection.prepareStatement(deleteSql); PreparedStatement insert = connection.prepareStatement(insertSql)) {
				Set<String> names = new LinkedHashSet<>(removals);
				names.addAll(updates.keySet());
				for (String name : names) {
					delete.setString(1, requiredProfile);
					delete.setString(2, name);
					delete.addBatch();
				}
				delete.executeBatch();
				for (Map.Entry<String, String> entry : updates.entrySet()) {
					insert.setString(1, requiredProfile);
					insert.setString(2, entry.getKey());
					insert.setString(3, entry.getValue());
					insert.addBatch();
				}
				if (!updates.isEmpty())
					insert.executeBatch();
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(autoCommit);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Could not update runtime log-level profile " + requiredProfile, e);
		}
	}

	@Override
	public void clearProfileLogLevels(String profile) {
		ensureTables();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement("delete from " + levelTable + " where PROFILE = ?")) {
			statement.setString(1, requireProfile(profile));
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Could not clear runtime log-level profile", e);
		}
	}

	@Override
	public void updateProfileCascade(List<String> profiles) {
		List<String> cascade = profiles == null ? Collections.<String>emptyList() : profiles;
		Set<String> unique = new LinkedHashSet<>();
		for (String profile : cascade) {
			String required = requireProfile(profile);
			if (!unique.add(required))
				throw new IllegalArgumentException("Duplicate profile in log-level cascade: " + required);
		}
		ensureTables();
		try (Connection connection = dataSource.getConnection()) {
			boolean autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			try (Statement clear = connection.createStatement();
					PreparedStatement insert = connection.prepareStatement(
							"insert into " + cascadeTable + " (POSITION, PROFILE) values (?, ?)")) {
				clear.executeUpdate("delete from " + cascadeTable);
				for (int i = 0; i < cascade.size(); i++) {
					insert.setInt(1, i);
					insert.setString(2, cascade.get(i).trim());
					insert.addBatch();
				}
				if (!cascade.isEmpty())
					insert.executeBatch();
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(autoCommit);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Could not update runtime log-level profile cascade", e);
		}
	}

	@Override
	public void clearProfileCascade() {
		updateProfileCascade(Collections.<String>emptyList());
	}

	private void ensureTables() {
		try (Connection connection = dataSource.getConnection()) {
			if (!tableExists(connection, levelTable))
				try (Statement statement = connection.createStatement()) {
					statement.executeUpdate("create table " + levelTable
							+ " (PROFILE varchar(255) not null, NAME varchar(1024) not null, LEVEL varchar(255) not null, primary key (PROFILE, NAME))");
				}
			if (!tableExists(connection, cascadeTable))
				try (Statement statement = connection.createStatement()) {
					statement.executeUpdate("create table " + cascadeTable
							+ " (POSITION integer not null primary key, PROFILE varchar(255) not null unique)");
				}
		} catch (SQLException e) {
			throw new RuntimeException("Could not ensure runtime log-level profile tables", e);
		}
	}

	private boolean tableExists(Connection connection, String table) throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();
		for (String candidate : new String[] { table, table.toUpperCase(), table.toLowerCase() })
			try (ResultSet tables = metaData.getTables(null, null, candidate, new String[] { "TABLE" })) {
				if (tables.next())
					return true;
			}
		return false;
	}

	private static String valid(String table) {
		if (!table.matches(VALID_TABLE_NAME_PATTERN))
			throw new IllegalArgumentException("Invalid log-level table name: " + table);
		return table;
	}

	private static String requireProfile(String profile) {
		String value = profile == null ? "" : profile.trim();
		if (value.isEmpty())
			throw new IllegalArgumentException("Log-level profile must not be empty");
		return value;
	}
}
