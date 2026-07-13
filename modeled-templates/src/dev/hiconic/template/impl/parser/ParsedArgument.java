package dev.hiconic.template.impl.parser;

import com.braintribe.model.generic.reflection.GenericModelType;
import dev.hiconic.template.model.parse.TextRange;

public final class ParsedArgument {
	private final String name;
	private final Object value;
	private final GenericModelType type;
	private final String source;
	private final TextRange range;

	public ParsedArgument(String name, Object value, GenericModelType type) {
		this(name, value, type, null);
	}

	public ParsedArgument(String name, Object value, GenericModelType type, TextRange range) {
		this.name = name;
		this.value = value;
		this.type = type;
		this.source = null;
		this.range = range;
	}

	public ParsedArgument(String name, String source) {
		this(name, source, (TextRange) null);
	}

	public ParsedArgument(String name, String source, TextRange range) {
		this.name = name;
		this.value = null;
		this.type = null;
		this.source = source;
		this.range = range;
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

	public String source() {
		return source;
	}

	public TextRange range() {
		return range;
	}
}
