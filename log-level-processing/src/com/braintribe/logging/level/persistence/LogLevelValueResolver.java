package com.braintribe.logging.level.persistence;

import java.util.function.Function;

public final class LogLevelValueResolver {
	private LogLevelValueResolver() {
	}

	public static String resolveValue(String value, Function<String, String> propertyLookup) {
		if (propertyLookup == null || value == null) {
			return value;
		}

		String trimmed = value.trim();
		if (trimmed.startsWith("${") == false || trimmed.endsWith("}") == false) {
			return value;
		}

		String expression = trimmed.substring(2, trimmed.length() - 1);
		if (expression.isEmpty()) {
			return value;
		}

		int defaultDelimiter = expression.indexOf(':');
		String propertyName = defaultDelimiter < 0 ? expression.trim() : expression.substring(0, defaultDelimiter).trim();
		String defaultValue = defaultDelimiter < 0 ? null : expression.substring(defaultDelimiter + 1).trim();
		if (propertyName.isEmpty()) {
			return value;
		}

		String resolvedValue = propertyLookup.apply(propertyName);
		if (resolvedValue != null && resolvedValue.trim().isEmpty() == false) {
			return resolvedValue.trim();
		}

		return defaultValue != null ? defaultValue : value;
	}
}
