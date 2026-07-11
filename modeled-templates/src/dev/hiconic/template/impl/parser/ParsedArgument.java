package dev.hiconic.template.impl.parser;

import com.braintribe.model.generic.reflection.GenericModelType;

public final class ParsedArgument {
	private final String name;
	private final Object value;
	private final GenericModelType type;

	public ParsedArgument(String name, Object value, GenericModelType type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public String name() {
		return name;
	}

	public Object value() {
		return value;
	}

	public GenericModelType type() {
		return type;
	}
}
