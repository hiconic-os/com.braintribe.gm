package dev.hiconic.template.impl.parser;

import com.braintribe.model.generic.reflection.GenericModelType;
import dev.hiconic.template.model.parse.TextRange;

public final class ParsedValueExpression {
	private final Object value;
	private final GenericModelType type;
	private final TextRange range;

	public ParsedValueExpression(Object value, GenericModelType type) {
		this(value, type, null);
	}

	public ParsedValueExpression(Object value, GenericModelType type, TextRange range) {
		this.value = value;
		this.type = type;
		this.range = range;
	}

	public Object value() {
		return value;
	}

	public GenericModelType type() {
		return type;
	}

	public TextRange range() {
		return range;
	}
}
