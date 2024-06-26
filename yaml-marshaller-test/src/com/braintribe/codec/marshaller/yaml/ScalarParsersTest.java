// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.codec.marshaller.yaml;

import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_BOOLEAN;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_DECIMAL;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_DOUBLE;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_FLOAT;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_INTEGER;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_LONG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.Test;

import com.braintribe.model.generic.reflection.EnumTypes;
import com.braintribe.model.generic.reflection.ScalarType;
import com.braintribe.testing.model.test.technical.features.SimpleEnum;

public class ScalarParsersTest {
	@Test
	public void testSimpleCannonical() {
		assertResult(TYPE_BOOLEAN, "true", true);
		assertResult(TYPE_BOOLEAN, "false", false);

		assertResult(TYPE_DOUBLE, "1", 1d);
		assertResult(TYPE_FLOAT, "1.23", 1.23f);
		assertResult(TYPE_INTEGER, "1", 1);
		assertResult(TYPE_LONG, "1", 1l);
		assertResult(TYPE_DECIMAL, "1", new BigDecimal(1));
	}

	@Test
	public void testAdvancedCannonical() {
		assertResult(TYPE_FLOAT, "0.964", 0.964f);
		assertResult(TYPE_FLOAT, ".964", 0.964f);
		assertResult(TYPE_DOUBLE, "-0.00000001", -0.00000001d);
		assertResult(TYPE_DOUBLE, "-.00000001", -0.00000001d);

		assertResult(TYPE_INTEGER, "-100", -100);
		assertResult(TYPE_LONG, "-5223372036854775607", -5223372036854775607l);
		assertResult(TYPE_DECIMAL, "1234567890.123456789012345678901234567890",
				new BigDecimal("1234567890.123456789012345678901234567890"));

		assertResult(EnumTypes.T(SimpleEnum.class), "FIVE", SimpleEnum.FIVE);
	}

	@Test
	public void testNonCannonical() {
		assertResult(TYPE_BOOLEAN, "True", true);
		assertResult(TYPE_BOOLEAN, "False", false);
		assertResult(TYPE_BOOLEAN, "TRUE", true);
		assertResult(TYPE_BOOLEAN, "FALSE", false);

		assertResult(TYPE_INTEGER, "0xBFF", 0xBFF);
		assertResult(TYPE_INTEGER, "0o777", 0777);
		assertResult(TYPE_INTEGER, "+42", 42);
		assertResult(TYPE_LONG, "0xBFF", 0xBFFl);
		assertResult(TYPE_LONG, "0o777", 0777l);
		assertResult(TYPE_LONG, "+42", 42l);
	}

	@Test
	public void testSpecialFloatingpointValues() {
		assertResult(TYPE_DOUBLE, ".nan", Double.NaN);
		assertResult(TYPE_DOUBLE, ".NaN", Double.NaN);
		assertResult(TYPE_DOUBLE, ".NAN", Double.NaN);
		assertResult(TYPE_FLOAT, ".nan", Float.NaN);
		assertResult(TYPE_FLOAT, ".NaN", Float.NaN);
		assertResult(TYPE_FLOAT, ".NAN", Float.NaN);

		assertResult(TYPE_DOUBLE, ".inf", Double.POSITIVE_INFINITY);
		assertResult(TYPE_DOUBLE, ".Inf", Double.POSITIVE_INFINITY);
		assertResult(TYPE_DOUBLE, ".INF", Double.POSITIVE_INFINITY);
		assertResult(TYPE_DOUBLE, "+.inf", Double.POSITIVE_INFINITY);
		assertResult(TYPE_DOUBLE, "+.Inf", Double.POSITIVE_INFINITY);
		assertResult(TYPE_DOUBLE, "+.INF", Double.POSITIVE_INFINITY);

		assertResult(TYPE_FLOAT, ".inf", Float.POSITIVE_INFINITY);
		assertResult(TYPE_FLOAT, ".Inf", Float.POSITIVE_INFINITY);
		assertResult(TYPE_FLOAT, ".INF", Float.POSITIVE_INFINITY);
		assertResult(TYPE_FLOAT, "+.inf", Float.POSITIVE_INFINITY);
		assertResult(TYPE_FLOAT, "+.Inf", Float.POSITIVE_INFINITY);
		assertResult(TYPE_FLOAT, "+.INF", Float.POSITIVE_INFINITY);

		assertResult(TYPE_DOUBLE, "-.inf", Double.NEGATIVE_INFINITY);
		assertResult(TYPE_DOUBLE, "-.Inf", Double.NEGATIVE_INFINITY);
		assertResult(TYPE_DOUBLE, "-.INF", Double.NEGATIVE_INFINITY);
		assertResult(TYPE_FLOAT, "-.inf", Float.NEGATIVE_INFINITY);
		assertResult(TYPE_FLOAT, "-.Inf", Float.NEGATIVE_INFINITY);
		assertResult(TYPE_FLOAT, "-.INF", Float.NEGATIVE_INFINITY);
	}

	@Test
	public void testZeroes() {
		assertResult(TYPE_INTEGER, "0", 0);
		assertResult(TYPE_LONG, "0", 0l);
		assertResult(TYPE_FLOAT, "0", 0f);
		assertResult(TYPE_DOUBLE, "0", 0d);

		assertResult(TYPE_INTEGER, "+0", 0);
		assertResult(TYPE_LONG, "+0", 0l);
		assertResult(TYPE_FLOAT, "+0", 0f);
		assertResult(TYPE_DOUBLE, "+0", 0d);

		assertResult(TYPE_INTEGER, "-0", 0);
		assertResult(TYPE_LONG, "-0", 0l);
		assertResult(TYPE_FLOAT, "-0", -0f);
		assertResult(TYPE_DOUBLE, "-0", -0d);

		assertResult(TYPE_INTEGER, "00", 0);
		assertResult(TYPE_LONG, "00", 0l);
		assertResult(TYPE_FLOAT, "00", 0f);
		assertResult(TYPE_DOUBLE, "00", 0d);

		assertResult(TYPE_INTEGER, "+00", 0);
		assertResult(TYPE_LONG, "+00", 0l);
		assertResult(TYPE_FLOAT, "+00", 0f);
		assertResult(TYPE_DOUBLE, "+00", 0d);

		assertResult(TYPE_INTEGER, "-00", 0);
		assertResult(TYPE_LONG, "-00", 0l);
		assertResult(TYPE_FLOAT, "-00", -0f);
		assertResult(TYPE_DOUBLE, "-00", -0d);

		assertResult(TYPE_INTEGER, "0042", 42);
		assertResult(TYPE_LONG, "+002345", 2345l);
		assertResult(TYPE_FLOAT, "00.123", 0.123f);
		assertResult(TYPE_DOUBLE, "-00123.455", -123.455d);
	}

	@Test
	public void testEdgeCases() {
		assertResult(TYPE_INTEGER, String.valueOf(Integer.MAX_VALUE), Integer.MAX_VALUE);
		assertResult(TYPE_INTEGER, String.valueOf(Integer.MIN_VALUE), Integer.MIN_VALUE);
		assertExceptionThrown(TYPE_INTEGER, String.valueOf(Integer.MAX_VALUE + 1l));
		assertExceptionThrown(TYPE_INTEGER, String.valueOf(Integer.MIN_VALUE - 1l));

		assertResult(TYPE_LONG, String.valueOf(Long.MAX_VALUE), Long.MAX_VALUE);
		assertResult(TYPE_LONG, String.valueOf(Long.MIN_VALUE), Long.MIN_VALUE);
		assertExceptionThrown(TYPE_LONG, "9,223,372,036,854,775,808");
		assertExceptionThrown(TYPE_LONG, "-9,223,372,036,854,775,807");

		assertResult(TYPE_DOUBLE, String.valueOf(Double.MAX_VALUE), Double.MAX_VALUE);
		assertResult(TYPE_DOUBLE, String.valueOf(Double.MIN_VALUE), Double.MIN_VALUE);

		assertResult(TYPE_FLOAT, String.valueOf(Float.MAX_VALUE), Float.MAX_VALUE);
		assertResult(TYPE_FLOAT, String.valueOf(Float.MIN_VALUE), Float.MIN_VALUE);
		assertResult(TYPE_FLOAT, "340282366638528870000000000000000000000.000000", Float.POSITIVE_INFINITY);
		assertResult(TYPE_FLOAT, "-340282366638528870000000000000000000000.000000", Float.NEGATIVE_INFINITY);
	}

	@Test
	public void testWrong() {
		assertExceptionThrown(TYPE_INTEGER, "a");
		assertExceptionThrown(TYPE_INTEGER, "12l");
		assertExceptionThrown(TYPE_INTEGER, "3.14");

		assertExceptionThrown(TYPE_LONG, "a");
		assertExceptionThrown(TYPE_LONG, "12l");
		assertExceptionThrown(TYPE_LONG, "3.14");

		assertExceptionThrown(TYPE_DOUBLE, "a");
		assertExceptionThrown(TYPE_DOUBLE, "12l");
		assertExceptionThrown(TYPE_DOUBLE, "-3.14l");
		assertExceptionThrown(TYPE_DOUBLE, "-.NaN");
		assertExceptionThrown(TYPE_DOUBLE, "NAN");

		assertExceptionThrown(TYPE_FLOAT, "-a");
		assertExceptionThrown(TYPE_FLOAT, "12l");
		assertExceptionThrown(TYPE_FLOAT, "3.14l");
		assertExceptionThrown(TYPE_FLOAT, "-.nan");
		assertExceptionThrown(TYPE_FLOAT, "nan");

		assertExceptionThrown(TYPE_BOOLEAN, "tRue");
		assertExceptionThrown(TYPE_BOOLEAN, "fALSE");
		assertExceptionThrown(TYPE_BOOLEAN, "on");
		assertExceptionThrown(TYPE_BOOLEAN, "yes");
		assertExceptionThrown(TYPE_BOOLEAN, "f");
		assertExceptionThrown(TYPE_BOOLEAN, "whatever");
		assertExceptionThrown(TYPE_BOOLEAN, "");

		assertExceptionThrown(EnumTypes.T(SimpleEnum.class), "five");
		assertExceptionThrown(EnumTypes.T(SimpleEnum.class), "whatever");
		assertExceptionThrown(EnumTypes.T(SimpleEnum.class), "");
	}

	private void assertExceptionThrown(ScalarType type, String value) {
		assertThatThrownBy(() -> ScalarParsers.parse(type, value)).isInstanceOf(Exception.class);
	}

	private void assertResult(ScalarType type, String value, Object expected) {
		assertThat(ScalarParsers.parse(type, value)).isEqualTo(expected);
	}
}
