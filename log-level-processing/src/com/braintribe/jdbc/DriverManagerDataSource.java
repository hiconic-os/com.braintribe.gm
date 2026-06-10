package com.braintribe.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;

public class DriverManagerDataSource implements DataSource {
	private String url;
	private String user;
	private String password;
	private String driverClass;
	private PrintWriter logWriter;
	private int loginTimeout;

	public DriverManagerDataSource() {
	}

	public DriverManagerDataSource(String url) {
		setUrl(url);
	}

	public DriverManagerDataSource(String url, String user, String password) {
		setUrl(url);
		setUser(user);
		setPassword(password);
	}

	@Override
	public Connection getConnection() throws SQLException {
		ensureDriverLoaded();

		if (user == null) {
			return DriverManager.getConnection(requireUrl());
		}

		return DriverManager.getConnection(requireUrl(), user, password);
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		ensureDriverLoaded();
		return DriverManager.getConnection(requireUrl(), username, password);
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return logWriter;
	}

	@Override
	public void setLogWriter(PrintWriter logWriter) throws SQLException {
		this.logWriter = logWriter;
		DriverManager.setLogWriter(logWriter);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		if (seconds < 0) {
			throw new SQLException("loginTimeout must not be negative: " + seconds);
		}

		this.loginTimeout = seconds;
		DriverManager.setLoginTimeout(seconds);
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("DriverManagerDataSource does not expose a parent logger");
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (isWrapperFor(iface)) {
			return iface.cast(this);
		}

		throw new SQLException(getClass().getName() + " is not a wrapper for " + iface.getName());
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return iface != null && iface.isInstance(this);
	}

	public String getUrl() {
		return url;
	}

	@Configurable
	@Required
	public void setUrl(String url) {
		this.url = nonBlank(url, "url");
	}

	public String getUser() {
		return user;
	}

	@Configurable
	public void setUser(String user) {
		this.user = trimToNull(user);
	}

	public String getPassword() {
		return password;
	}

	@Configurable
	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClass() {
		return driverClass;
	}

	@Configurable
	public void setDriverClass(String driverClass) {
		this.driverClass = trimToNull(driverClass);
	}

	private void ensureDriverLoaded() throws SQLException {
		if (driverClass == null) {
			return;
		}

		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			throw new SQLException("Could not load JDBC driver class: " + driverClass, e);
		}
	}

	private String requireUrl() {
		return Objects.requireNonNull(url, "url");
	}

	private static String nonBlank(String value, String name) {
		String trimmed = trimToNull(value);

		if (trimmed == null) {
			throw new IllegalArgumentException(name + " must not be blank");
		}

		return trimmed;
	}

	private static String trimToNull(String value) {
		if (value == null) {
			return null;
		}

		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
