package dev.hiconic.template.impl.parser;

import com.braintribe.model.generic.reflection.GenericModelType;

public final class ParsedValueExpression {
	private final Object value;
	private final GenericModelType type;

	public ParsedValueExpression(Object value, GenericModelType type) {
		this.value = value;
		this.type = type;
	}

	public Object value() {
		return value;
	}

	public GenericModelType type() {
		return type;
	}
}
