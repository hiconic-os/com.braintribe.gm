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

import java.sql.Types;

import javax.sql.DataSource;

/**
 * @author peter.gazdik
 */
public interface JdbcDialect {

	static JdbcDialect defaultDialect() {
		return JdbcDialectMappings.DEFAULT_DIALECT;
	}

	static JdbcDialect detectDialect(DataSource dataSource) {
		return JdbcDialectAutoSense.findJdbcDialect(dataSource);
	}

	/** Name of the DB, without concrete version details. E.g. "oracle", "postgre", etc */
	String dbVariant();
	/** Same information as {@link #dbVariant()}, but type-safe, for some well known variants. */
	DbVariant knownDbVariant();
	/** Full class name of the Hibernate Dialect which this {@link JdbcDialect} is based on. */
	String hibernateDialect();

	/** Name of {@link Types#BOOLEAN} */
	String booleanType();
	/** Name of {@link Types#INTEGER} */
	String intType();
	/** Name of {@link Types#BIGINT} */
	String longType();
	/** Name of {@link Types#FLOAT} */
	String floatType();
	/** Name of {@link Types#DOUBLE} */
	String doubleType();
	/** Name of {@link Types#TIMESTAMP} */
	String timestampType();
	/** Name of {@link Types#NUMERIC} with precision 19 and scale 2 */
	default String bigDecimalType() {
		return bigDecimalType(19, 2);
	}
	/** Name of {@link Types#NUMERIC} */
	String bigDecimalType(int precision, int scale);
	/** Name of {@link Types#CLOB} */
	String clobType();
	/** Name of {@link Types#BLOB} */
	String blobType();

	/**
	 * Name of {@link Types#NVARCHAR},
	 * <p>
	 * This is tricky, as it can be pretty unlimited in some cases, and only up to 4000 bytes in Oracle.
	 * <p>
	 * In MS SQL the type is nvarchar(MAX), which cannot be used for a primary key. Use {@link #nvarchar255()} instead.
	 */
	String nvarchar(int length);

	/** Name of {@link Types#NVARCHAR} with 255 chars max. This is safe to use as a primary key. */
	String nvarchar255();

}
