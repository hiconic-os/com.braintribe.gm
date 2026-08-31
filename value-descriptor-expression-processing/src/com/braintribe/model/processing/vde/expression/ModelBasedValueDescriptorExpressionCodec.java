// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.expression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.Alias;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.template.Template;
import com.braintribe.model.generic.template.TemplateFragment;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.processing.vde.expression.api.ValueDescriptorExpressionCodec;
import com.braintribe.utils.lcd.StringTools;

/**
 * A function codec whose vocabulary is entirely derived from supplied model types. A function type must carry
 * {@link PositionalArguments}; its default function name is its uncapitalized short type name.
 */
public class ModelBasedValueDescriptorExpressionCodec implements ValueDescriptorExpressionCodec {

	private final Map<String, FunctionType> functionsByName = new LinkedHashMap<>();
	private final Map<EntityType<?>, FunctionType> functionsByType = new LinkedHashMap<>();

	@SafeVarargs
	public ModelBasedValueDescriptorExpressionCodec(EntityType<? extends ValueDescriptor>... functionTypes) {
		this(Arrays.asList(functionTypes));
	}

	public ModelBasedValueDescriptorExpressionCodec(Collection<EntityType<? extends ValueDescriptor>> functionTypes) {
		for (EntityType<? extends ValueDescriptor> functionType : functionTypes)
			register(functionType);
	}

	private void register(EntityType<? extends ValueDescriptor> type) {
		PositionalArguments positional = type.getJavaType().getAnnotation(PositionalArguments.class);
		if (positional == null)
			throw new IllegalArgumentException("Value descriptor function type has no @PositionalArguments: " + type.getTypeSignature());

		String name = StringTools.uncapitalize(type.getShortName());
		List<Property> arguments = new ArrayList<>();
		for (String propertyName : positional.value())
			arguments.add(type.getProperty(propertyName));

		FunctionType function = new FunctionType(name, type, arguments);
		registerName(name, function);
		for (Alias alias : type.getJavaType().getAnnotationsByType(Alias.class))
			registerName(alias.value(), function);
		functionsByType.put(type, function);
	}

	private void registerName(String name, FunctionType function) {
		FunctionType previous = functionsByName.putIfAbsent(name, function);
		if (previous != null)
			throw new IllegalArgumentException("Duplicate value descriptor function name '" + name + "': "
					+ previous.type.getTypeSignature() + " and " + function.type.getTypeSignature());
	}

	@Override
	public Maybe<Object> parse(String expression) {
		try {
			Template template = Template.parse(expression);
			if (template.isStaticOnly())
				return Maybe.complete(expression);

			List<Object> fragments = new ArrayList<>();
			for (TemplateFragment fragment : template.fragments())
				fragments.add(fragment.isPlaceholder() ? new ExpressionParser(fragment.getText()).parseComplete() : fragment.getText());

			if (fragments.size() == 1)
				return Maybe.complete(fragments.get(0));

			Concatenation concatenation = Concatenation.T.create();
			concatenation.setOperands(fragments);
			return Maybe.complete(concatenation);
		} catch (RuntimeException e) {
			return invalid("Invalid value descriptor expression '" + expression + "': " + e.getMessage());
		}
	}

	@Override
	public Maybe<String> render(Object value) {
		try {
			return Maybe.complete(renderTemplate(value));
		} catch (RuntimeException e) {
			return invalid("Unable to render value descriptor expression: " + e.getMessage());
		}
	}

	private String renderTemplate(Object value) {
		Object direct = unwrap(value);
		if (direct instanceof Concatenation) {
			StringBuilder result = new StringBuilder();
			for (Object operand : ((Concatenation) direct).getOperands()) {
				Object unwrapped = unwrap(operand);
				if (unwrapped instanceof ValueDescriptor)
					result.append("${").append(renderDescriptor((ValueDescriptor) unwrapped)).append('}');
				else
					result.append(escapeStatic(String.valueOf(unwrapped)));
			}
			return result.toString();
		}
		if (direct instanceof ValueDescriptor)
			return "${" + renderDescriptor((ValueDescriptor) direct) + "}";
		return escapeStatic(String.valueOf(direct));
	}

	private String renderDescriptor(ValueDescriptor descriptor) {
		if (descriptor instanceof Variable)
			return ((Variable) descriptor).getName();

		FunctionType function = functionsByType.get(descriptor.entityType());
		if (function == null)
			throw new IllegalArgumentException("No expression function model registered for " + descriptor.entityType().getTypeSignature());

		StringBuilder result = new StringBuilder(function.name).append('(');
		int argumentCount = function.arguments.size();
		while (argumentCount > 0 && function.arguments.get(argumentCount - 1).getDirect(descriptor) == null)
			argumentCount--;
		for (int i = 0; i < argumentCount; i++) {
			if (i > 0)
				result.append(", ");
			result.append(renderArgument(function.arguments.get(i).getDirect(descriptor)));
		}
		return result.append(')').toString();
	}

	private String renderArgument(Object value) {
		Object direct = unwrap(value);
		if (direct instanceof ValueDescriptor)
			return renderDescriptor((ValueDescriptor) direct);
		if (direct == null)
			return "null";
		if (direct instanceof String || direct instanceof Character || direct instanceof Enum<?>)
			return "'" + escapeLiteral(String.valueOf(direct)) + "'";
		if (direct instanceof Number || direct instanceof Boolean)
			return direct.toString();
		throw new IllegalArgumentException("Unsupported expression literal type: " + direct.getClass().getName());
	}

	private static Object unwrap(Object value) {
		return VdHolder.isVdHolder(value) ? ((VdHolder) value).vd : value;
	}

	private static String escapeStatic(String value) {
		return value.replace("${", "$${");
	}

	private static String escapeLiteral(String value) {
		return value.replace("\\", "\\\\").replace("'", "\\'");
	}

	@SuppressWarnings("unchecked")
	private static <T> Maybe<T> invalid(String message) {
		return (Maybe<T>) InvalidArgument.create(message).asMaybe();
	}

	private class ExpressionParser {
		private final String source;
		private int position;

		ExpressionParser(String source) {
			this.source = source;
		}

		Object parseComplete() {
			Object result = parseExpression();
			skipWhitespace();
			if (!end())
				fail("Unexpected character '" + current() + "'");
			return result;
		}

		private Object parseExpression() {
			skipWhitespace();
			if (end())
				fail("Expected expression");
			char current = current();
			if (current == '\'' || current == '"')
				return parseString();
			if (current == '-' || Character.isDigit(current))
				return parseNumber();
			if (current == '$')
				return parseNestedPlaceholder();
			if (isIdentifierStart(current))
				return parseIdentifierExpression();
			fail("Expected literal, variable or function");
			return null;
		}

		private Object parseIdentifierExpression() {
			String identifier = parseIdentifier();
			skipWhitespace();
			if (!end() && current() == '(')
				return parseFunction(identifier);
			if ("true".equals(identifier))
				return Boolean.TRUE;
			if ("false".equals(identifier))
				return Boolean.FALSE;
			if ("null".equals(identifier))
				return null;
			Variable variable = Variable.T.create();
			variable.setName(identifier);
			variable.setTypeSignature("string");
			return variable;
		}

		private ValueDescriptor parseFunction(String name) {
			FunctionType function = functionsByName.get(name);
			if (function == null)
				fail("Unknown value descriptor function '" + name + "'");
			position++;
			List<Object> values = new ArrayList<>();
			skipWhitespace();
			if (!end() && current() != ')') {
				do {
					values.add(parseExpression());
					skipWhitespace();
					if (end() || current() != ',')
						break;
					position++;
				} while (true);
			}
			expect(')');
			if (values.size() > function.arguments.size())
				fail("Function '" + name + "' accepts at most " + function.arguments.size() + " argument(s), got " + values.size());

			ValueDescriptor result = function.type.create();
			for (int i = 0; i < values.size(); i++)
				bind(function.arguments.get(i), result, values.get(i));
			return result;
		}

		private void bind(Property property, GenericEntity entity, Object value) {
			try {
				if (value instanceof ValueDescriptor)
					property.setVdDirect(entity, (ValueDescriptor) value);
				else
					property.setDirect(entity, value);
			} catch (RuntimeException e) {
				fail("Argument for property '" + property.getName() + "' is incompatible with " + property.getType().getTypeSignature()
						+ ": " + e.getMessage());
			}
		}

		private Object parseNestedPlaceholder() {
			expect('$');
			expect('{');
			Object result = parseExpression();
			expect('}');
			return result;
		}

		private String parseString() {
			char quote = current();
			position++;
			StringBuilder result = new StringBuilder();
			boolean escaped = false;
			while (!end()) {
				char c = current();
				position++;
				if (escaped) {
					result.append(c == 'n' ? '\n' : c == 'r' ? '\r' : c == 't' ? '\t' : c);
					escaped = false;
				} else if (c == '\\') {
					escaped = true;
				} else if (c == quote) {
					return result.toString();
				} else {
					result.append(c);
				}
			}
			fail("Unterminated string literal");
			return null;
		}

		private Number parseNumber() {
			int start = position;
			if (current() == '-')
				position++;
			while (!end() && Character.isDigit(current()))
				position++;
			boolean decimal = false;
			if (!end() && current() == '.') {
				decimal = true;
				position++;
				while (!end() && Character.isDigit(current()))
					position++;
			}
			String number = source.substring(start, position);
			if (decimal)
				return new BigDecimal(number);
			long value = Long.parseLong(number);
			return value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE ? Integer.valueOf((int) value) : Long.valueOf(value);
		}

		private String parseIdentifier() {
			int start = position++;
			while (!end() && isIdentifierPart(current()))
				position++;
			return source.substring(start, position);
		}

		private void expect(char expected) {
			skipWhitespace();
			if (end() || current() != expected)
				fail("Expected '" + expected + "'");
			position++;
		}

		private void skipWhitespace() {
			while (!end() && Character.isWhitespace(current()))
				position++;
		}

		private boolean end() {
			return position >= source.length();
		}

		private char current() {
			return source.charAt(position);
		}

		private void fail(String message) {
			throw new IllegalArgumentException(message + " at position " + position);
		}
	}

	private static boolean isIdentifierStart(char c) {
		return c == '_' || Character.isLetter(c);
	}

	private static boolean isIdentifierPart(char c) {
		return c == '_' || c == '.' || c == '-' || Character.isLetterOrDigit(c);
	}

	private static class FunctionType {
		final String name;
		final EntityType<? extends ValueDescriptor> type;
		final List<Property> arguments;

		FunctionType(String name, EntityType<? extends ValueDescriptor> type, List<Property> arguments) {
			this.name = name;
			this.type = type;
			this.arguments = arguments;
		}
	}
}
