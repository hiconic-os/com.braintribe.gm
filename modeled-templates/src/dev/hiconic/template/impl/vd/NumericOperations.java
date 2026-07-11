package dev.hiconic.template.impl.vd;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

final class NumericOperations {
	private NumericOperations() {
	}

	static int compare(Object left, Object right) {
		return decimal(left).compareTo(decimal(right));
	}

	static Number add(Object left, Object right) {
		return narrow(decimal(left).add(decimal(right)), left, right);
	}

	static Number subtract(Object left, Object right) {
		return narrow(decimal(left).subtract(decimal(right)), left, right);
	}

	static Number multiply(Object left, Object right) {
		return narrow(decimal(left).multiply(decimal(right)), left, right);
	}

	static Number divide(Object left, Object right) {
		BigDecimal divisor = decimal(right);
		if (divisor.signum() == 0)
			throw new ArithmeticException("Division by zero");

		BigDecimal result = decimal(left).divide(divisor, MathContext.DECIMAL128);
		if (isIntegral(left) && isIntegral(right))
			return result;

		return narrow(result, left, right);
	}

	private static BigDecimal decimal(Object value) {
		if (!(value instanceof Number))
			throw new IllegalArgumentException("Expected a number, got " + (value == null ? "null" : value.getClass().getName()));
		Number number = (Number) value;
		if (number instanceof BigDecimal)
			return (BigDecimal) number;
		if (number instanceof BigInteger)
			return new BigDecimal((BigInteger) number);
		return new BigDecimal(number.toString());
	}

	private static Number narrow(BigDecimal value, Object left, Object right) {
		if (left instanceof BigDecimal || right instanceof BigDecimal)
			return value;
		if (left instanceof Double || right instanceof Double)
			return value.doubleValue();
		if (left instanceof Float || right instanceof Float)
			return value.floatValue();
		if (left instanceof BigInteger || right instanceof BigInteger)
			return value.toBigIntegerExact();
		if (left instanceof Long || right instanceof Long)
			return value.longValueExact();
		return value.intValueExact();
	}

	private static boolean isIntegral(Object value) {
		return value instanceof Byte || value instanceof Short || value instanceof Integer
				|| value instanceof Long || value instanceof BigInteger;
	}
}
