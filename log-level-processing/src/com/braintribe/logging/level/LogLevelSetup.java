package com.braintribe.logging.level;

import java.io.File;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Function;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.jdbc.DriverManagerDataSource;
import com.braintribe.logging.level.persistence.FilesystemLogLevelPersistence;
import com.braintribe.logging.level.persistence.JdbcLogLevelPersistence;
import com.braintribe.logging.level.persistence.LogLevelPersistence;

public class LogLevelSetup {
	public static final String PROPERTY_JDBC_URL = "HC_LOG_LEVEL_JDBC_URL";
	public static final String PROPERTY_JDBC_USER = "HC_LOG_LEVEL_JDBC_USER";
	public static final String PROPERTY_JDBC_PASSWORD = "HC_LOG_LEVEL_JDBC_PASSWORD";
	public static final String PROPERTY_JDBC_DRIVER = "HC_LOG_LEVEL_JDBC_DRIVER";
	public static final String PROPERTY_JDBC_LOGIN_TIMEOUT = "HC_LOG_LEVEL_JDBC_LOGIN_TIMEOUT";
	public static final String PROPERTY_TABLE_PREFIX = "HC_LOG_LEVEL_TABLE_PREFIX";

	private static final String PACKAGED_LOG_LEVELS_FILE = "log-levels.properties";
	private static final String RUNTIME_LOG_LEVELS_FILE = "log-levels-rt.properties";

	private static volatile LogLevelSetup instance;

	private File confDir;
	private Function<String, String> propertyLookup;
	private LogLevelFramework logLevelFramework = new JulLogLevelFramework();

	private LogLevelManager logLevelManager;
	private LogLevelPersistence packagedLogLevelPersistence;
	private LogLevelPersistence runtimeLogLevelPersistence;

	public LogLevelSetup() {
	}

	public LogLevelSetup(File confDir, Function<String, String> propertyLookup) {
		setConfDir(confDir);
		setPropertyLookup(propertyLookup);
	}

	public static LogLevelSetup instance() {
		return Objects.requireNonNull(instance, "LogLevelSetup has not been initialized");
	}

	public static void setInstance(LogLevelSetup instance) {
		LogLevelSetup.instance = Objects.requireNonNull(instance, "instance");
	}

	public void applyEffectiveLogLevels() {
		logLevelManager().applyEffectiveLogLevels();
	}

	public LogLevelManager logLevelManager() {
		if (logLevelManager == null) {
			logLevelManager = new LogLevelManager(packagedLogLevelPersistence(), runtimeLogLevelPersistence(), logLevelFramework());
		}

		return logLevelManager;
	}

	public LogLevelPersistence packagedLogLevelPersistence() {
		if (packagedLogLevelPersistence == null) {
			packagedLogLevelPersistence = new FilesystemLogLevelPersistence(confFile(PACKAGED_LOG_LEVELS_FILE), requirePropertyLookup());
		}

		return packagedLogLevelPersistence;
	}

	public LogLevelPersistence runtimeLogLevelPersistence() {
		if (runtimeLogLevelPersistence == null) {
			runtimeLogLevelPersistence = property(PROPERTY_JDBC_URL) != null ? jdbcLogLevelPersistence() : filesystemRuntimeLogLevelPersistence();
		}

		return runtimeLogLevelPersistence;
	}

	public LogLevelFramework logLevelFramework() {
		return logLevelFramework;
	}

	@Configurable
	@Required
	public void setConfDir(File confDir) {
		this.confDir = Objects.requireNonNull(confDir, "confDir");
	}

	@Configurable
	@Required
	public void setPropertyLookup(Function<String, String> propertyLookup) {
		this.propertyLookup = Objects.requireNonNull(propertyLookup, "propertyLookup");
	}

	@Configurable
	public void setLogLevelFramework(LogLevelFramework logLevelFramework) {
		this.logLevelFramework = Objects.requireNonNull(logLevelFramework, "logLevelFramework");
		this.logLevelManager = null;
	}

	@Configurable
	public void setPackagedLogLevelPersistence(LogLevelPersistence packagedLogLevelPersistence) {
		this.packagedLogLevelPersistence = Objects.requireNonNull(packagedLogLevelPersistence, "packagedLogLevelPersistence");
		this.logLevelManager = null;
	}

	@Configurable
	public void setRuntimeLogLevelPersistence(LogLevelPersistence runtimeLogLevelPersistence) {
		this.runtimeLogLevelPersistence = Objects.requireNonNull(runtimeLogLevelPersistence, "runtimeLogLevelPersistence");
		this.logLevelManager = null;
	}

	private LogLevelPersistence filesystemRuntimeLogLevelPersistence() {
		return new FilesystemLogLevelPersistence(confFile(RUNTIME_LOG_LEVELS_FILE));
	}

	private LogLevelPersistence jdbcLogLevelPersistence() {
		return new JdbcLogLevelPersistence(driverManagerDataSource(), property(PROPERTY_TABLE_PREFIX));
	}

	private DriverManagerDataSource driverManagerDataSource() {
		DriverManagerDataSource bean = new DriverManagerDataSource();

		bean.setUrl(property(PROPERTY_JDBC_URL));
		bean.setUser(property(PROPERTY_JDBC_USER));
		bean.setPassword(property(PROPERTY_JDBC_PASSWORD));
		bean.setDriverClass(property(PROPERTY_JDBC_DRIVER));

		String loginTimeout = property(PROPERTY_JDBC_LOGIN_TIMEOUT);
		if (loginTimeout != null) {
			try {
				bean.setLoginTimeout(Integer.parseInt(loginTimeout));
			} catch (NumberFormatException | SQLException e) {
				throw new IllegalArgumentException("Invalid " + PROPERTY_JDBC_LOGIN_TIMEOUT + ": " + loginTimeout, e);
			}
		}

		return bean;
	}

	private File confFile(String fileName) {
		return new File(requireConfDir(), fileName);
	}

	private File requireConfDir() {
		return Objects.requireNonNull(confDir, "confDir");
	}

	private String property(String name) {
		String value = requirePropertyLookup().apply(name);
		return trimToNull(value);
	}

	private Function<String, String> requirePropertyLookup() {
		return Objects.requireNonNull(propertyLookup, "propertyLookup");
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}

		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
