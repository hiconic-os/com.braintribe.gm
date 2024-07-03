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
package com.braintribe.utils.logging;

import com.braintribe.common.lcd.UnknownEnumException;
import com.braintribe.logging.Logger;
import com.braintribe.utils.CommonTools;

/**
 * Utility class for operations related to {@link com.braintribe.logging.Logger.LogLevel} and
 * {@link com.braintribe.model.logging.LogLevel}
 * 
 *
 */
public class LogLevels {

	/**
	 * Converts modeled {@link com.braintribe.model.logging.LogLevel} to the corresponding {@link com.braintribe.logging.Logger.LogLevel} of the
	 * {@link Logger}. If logLevel is <tt>null</tt> then <tt>null</tt> is returned.
	 */
	public static com.braintribe.logging.Logger.LogLevel convert(com.braintribe.model.logging.LogLevel logLevel) {
		return convert(logLevel, null);
	}
		
	public static com.braintribe.logging.Logger.LogLevel convert(com.braintribe.model.logging.LogLevel logLevel, com.braintribe.logging.Logger.LogLevel defaultLevel) {
		if (logLevel == null)
			return defaultLevel;

		switch (logLevel) {
			case TRACE:
				return com.braintribe.logging.Logger.LogLevel.TRACE;
			case DEBUG:
				return com.braintribe.logging.Logger.LogLevel.DEBUG;
			case INFO:
				return com.braintribe.logging.Logger.LogLevel.INFO;
			case WARN:
				return com.braintribe.logging.Logger.LogLevel.WARN;
			case ERROR:
				return com.braintribe.logging.Logger.LogLevel.ERROR;
			default:
				throw new UnknownEnumException(logLevel);
		}
	}

	/**
	 * Convert a LogLevel as {@link String} based on {@link com.braintribe.model.logging.LogLevel} to the corresponding
	 * {@link com.braintribe.logging.Logger.LogLevel} of the logger.
	 * 
	 * @param logLevelAsString
	 *            {@link com.braintribe.model.logging.LogLevel} as {@link String}
	 * @return {@link com.braintribe.logging.Logger.LogLevel}
	 */
	public static com.braintribe.logging.Logger.LogLevel convert(String logLevelAsString) {
		if (CommonTools.isEmpty(logLevelAsString)) {
			throw new IllegalArgumentException("LogLevel needs to be set but is '" + logLevelAsString + "'");
		}
		com.braintribe.model.logging.LogLevel logLevel = com.braintribe.model.logging.LogLevel.valueOf(logLevelAsString);
		return convert(logLevel);
	}

}
