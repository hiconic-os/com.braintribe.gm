package dev.hiconic.template.impl.parser;

/** Parse-time presence marker; it is materialized as an ordinary {@code null}. */
enum NullLiteral {
	INSTANCE;

	static boolean is(Object value) {
		return value == INSTANCE;
	}

	static Object materialize(Object value) {
		return is(value) ? null : value;
	}
}
