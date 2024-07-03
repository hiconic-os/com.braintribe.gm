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
package com.braintribe.util.jdbc.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.regex.Matcher;

import javax.sql.DataSource;

import com.braintribe.exception.Exceptions;
import com.braintribe.logging.Logger;
import com.braintribe.util.jdbc.dialect.JdbcDialectMappings.JdbcDialectMapping;

/**
 * @author peter.gazdik
 */
public class JdbcDialectAutoSense {

	private static final Logger log = Logger.getLogger(JdbcDialectAutoSense.class);

	public static String getProductNameAndVersion(DataSource dataSource) {
		try (Connection c = dataSource.getConnection()) {
			DatabaseMetaData dmd = c.getMetaData();
			String dName = dmd.getDriverName();
			String dVersion = dmd.getDriverVersion();
			int dMajorVersion = dmd.getDriverMajorVersion();
			int dMinorVersion = dmd.getDriverMinorVersion();
			String pName = dmd.getDatabaseProductName();
			String pVersion = dmd.getDatabaseProductVersion();

			log.debug(() -> "Database driver: " + dName + ", version: " + dVersion + " (major: " + dMajorVersion + ",minor: " + dMinorVersion + ")");
			log.debug(() -> "Database used: " + pName + ", version: " + pVersion);

			String nameAndVersion = pName + " Version:" + pVersion;

			return nameAndVersion.replaceAll("\n", " ");

		} catch (Exception e) {
			throw Exceptions.unchecked(e, "Could not get database metadata information.");
		}
	}

	/* package */ static JdbcDialect findJdbcDialect(DataSource dataSource) {
		return autoSenseDialect(getProductNameAndVersion(dataSource));
	}

	private static JdbcDialect autoSenseDialect(String productNameAndVersion) {
		for (JdbcDialectMapping mapping : JdbcDialectMappings.dialectMappings())
			if (matches(mapping, productNameAndVersion))
				return logEndReturnDialect(mapping);

		return JdbcDialect.defaultDialect();
	}

	private static boolean matches(JdbcDialectMapping mapping, String productNameAndVersion) {
		Matcher m = mapping.pattern.matcher(productNameAndVersion);
		boolean matches = m.matches();
		return matches;
	}

	private static JdbcDialect logEndReturnDialect(JdbcDialectMapping mapping) {
		String variant = mapping.dialect.dbVariant();
		String dialect = mapping.dialect.hibernateDialect();

		log.debug("Auto-sensed variant " + variant + ", hibernate dialect " + dialect);

		return mapping.dialect;
	}

}
