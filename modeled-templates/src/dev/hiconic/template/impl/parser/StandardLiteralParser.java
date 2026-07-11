package dev.hiconic.template.impl.parser;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.reflection.SimpleTypes;
import com.braintribe.model.meta.GmCustomType;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.processing.meta.cmd.CmdResolver;

public class StandardLiteralParser {
	private final GenericModelTypeReflection typeReflection;
	private final CmdResolver cmdResolver;

	public StandardLiteralParser() {
		this.typeReflection = GMF.getTypeReflection();
		this.cmdResolver = null;
	}

	public StandardLiteralParser(GenericModelTypeReflection typeReflection) {
		this.typeReflection = typeReflection;
		this.cmdResolver = null;
	}

	public StandardLiteralParser(CmdResolver cmdResolver) {
		this.typeReflection = GMF.getTypeReflection();
		this.cmdResolver = cmdResolver;
	}

	public Maybe<ParsedLiteral> parse(String source) {
		return parse(source, null);
	}

	public Maybe<ParsedLiteral> parse(String source, GenericModelType expectedType) {
		String literal = source.trim();
		try {
			if (literal.contains("::"))
				return parseQualifiedEnum(literal, expectedType);
			if (literal.startsWith("\""))
				return stringLiteral(literal, SimpleTypes.TYPE_STRING);

			int colon = literal.indexOf(':');
			if (colon > 0) {
				String type = literal.substring(0, colon);
				String value = literal.substring(colon + 1);
				return parseTyped(type, value);
			}

			if ("true".equals(literal) || "false".equals(literal))
				return complete(Boolean.valueOf(literal), SimpleTypes.TYPE_BOOLEAN);
			if (literal.endsWith("L") || literal.endsWith("l"))
				return complete(Long.valueOf(literal.substring(0, literal.length() - 1)), SimpleTypes.TYPE_LONG);
			if (literal.endsWith("F") || literal.endsWith("f"))
				return complete(Float.valueOf(literal.substring(0, literal.length() - 1)), SimpleTypes.TYPE_FLOAT);
			if (literal.endsWith("D") || literal.endsWith("d"))
				return complete(Double.valueOf(literal.substring(0, literal.length() - 1)), SimpleTypes.TYPE_DOUBLE);
			if (literal.endsWith("M") || literal.endsWith("m"))
				return complete(new BigDecimal(literal.substring(0, literal.length() - 1)), SimpleTypes.TYPE_DECIMAL);
			if (literal.matches("[+-]?\\d+"))
				return complete(Integer.valueOf(literal), SimpleTypes.TYPE_INTEGER);
			if (literal.matches("[+-]?(?:\\d+\\.\\d*|\\d*\\.\\d+)(?:[eE][+-]?\\d+)?"))
				return complete(Double.valueOf(literal), SimpleTypes.TYPE_DOUBLE);
			return error("Not a literal: " + source);
		} catch (IllegalArgumentException | DateTimeParseException e) {
			return error("Invalid literal '" + source + "': " + e.getMessage());
		}
	}

	private Maybe<ParsedLiteral> parseTyped(String type, String value) {
		GenericModelType modelType = switch (type) {
			case "string" -> SimpleTypes.TYPE_STRING;
			case "boolean" -> SimpleTypes.TYPE_BOOLEAN;
			case "integer" -> SimpleTypes.TYPE_INTEGER;
			case "long" -> SimpleTypes.TYPE_LONG;
			case "float" -> SimpleTypes.TYPE_FLOAT;
			case "double" -> SimpleTypes.TYPE_DOUBLE;
			case "decimal" -> SimpleTypes.TYPE_DECIMAL;
			case "date" -> SimpleTypes.TYPE_DATE;
			default -> null;
		};
		if (modelType == null)
			return error("Unknown literal type: " + type);
		if ("null".equals(value))
			return complete(null, modelType);

		return switch (type) {
			case "string" -> stringLiteral(value, modelType);
			case "boolean" -> {
				if (!"true".equals(value) && !"false".equals(value))
					yield error("Boolean literal must be true or false");
				yield complete(Boolean.valueOf(value), modelType);
			}
			case "integer" -> complete(Integer.valueOf(value), modelType);
			case "long" -> complete(Long.valueOf(value), modelType);
			case "float" -> complete(Float.valueOf(value), modelType);
			case "double" -> complete(Double.valueOf(value), modelType);
			case "decimal" -> complete(new BigDecimal(value), modelType);
			case "date" -> {
				String decoded = decodeQuoted(value);
				yield complete(Date.from(Instant.parse(decoded)), modelType);
			}
			default -> throw new IllegalStateException(type);
		};
	}

	private Maybe<ParsedLiteral> parseQualifiedEnum(String literal, GenericModelType expectedType) {
		int separator = literal.indexOf("::");
		if (separator < 0 || separator + 2 == literal.length() || literal.indexOf("::", separator + 2) >= 0)
			return error("Enum literal must have the form Type::constant or ::constant");

		String typeName = literal.substring(0, separator);
		String constantName = literal.substring(separator + 2);
		Maybe<EnumType<?>> enumType = typeName.isEmpty()
				? inferredEnumType(expectedType)
				: resolveEnumType(typeName);
		if (enumType.isUnsatisfied())
			return Maybe.empty(enumType.whyUnsatisfied());

		Enum<?> value = enumType.get().findEnumValue(constantName);
		if (value == null)
			return error("Unknown constant '" + constantName + "' for enum " + enumType.get().getTypeSignature());
		return complete(value, enumType.get());
	}

	private Maybe<EnumType<?>> inferredEnumType(GenericModelType expectedType) {
		if (expectedType instanceof EnumType<?> enumType)
			return Maybe.complete(enumType);
		return errorEnum("Cannot infer enum type without an enum-typed binding target");
	}

	private Maybe<EnumType<?>> resolveEnumType(String typeName) {
		if (cmdResolver == null) {
			EnumType<?> enumType = typeReflection.findEnumType(typeName);
			return enumType == null ? errorEnum("Unknown enum type: " + typeName) : Maybe.complete(enumType);
		}

		if (typeName.indexOf('.') >= 0) {
			com.braintribe.model.meta.GmType type = cmdResolver.getModelOracle().findGmType(typeName);
			if (type instanceof GmEnumType enumType)
				return Maybe.complete(enumType.reflectionType());
		}

		String simpleName = typeName.substring(typeName.lastIndexOf('.') + 1);
		List<GmCustomType> matches = cmdResolver.getModelOracle().findGmTypeBySimpleName(simpleName);
		List<GmEnumType> enumMatches = matches.stream()
				.filter(GmEnumType.class::isInstance)
				.map(GmEnumType.class::cast)
				.filter(type -> typeName.equals(simpleName)
						|| type.getTypeSignature().equals(typeName)
						|| type.getTypeSignature().endsWith("." + typeName))
				.toList();
		if (enumMatches.isEmpty())
			return errorEnum("Unknown enum type in template model space: " + typeName);
		if (enumMatches.size() > 1)
			return errorEnum("Ambiguous enum type '" + typeName + "': "
					+ enumMatches.stream().map(GmEnumType::getTypeSignature).sorted().toList());
		return Maybe.complete(enumMatches.get(0).reflectionType());
	}

	private Maybe<ParsedLiteral> stringLiteral(String literal, GenericModelType type) {
		return complete(decodeQuoted(literal), type);
	}

	private String decodeQuoted(String literal) {
		if (literal.length() < 2 || literal.charAt(0) != '"' || literal.charAt(literal.length() - 1) != '"')
			throw new IllegalArgumentException("String literal must be enclosed in double quotes");
		StringBuilder result = new StringBuilder(literal.length() - 2);
		for (int i = 1; i < literal.length() - 1; i++) {
			char ch = literal.charAt(i);
			if (ch < 0x20)
				throw new IllegalArgumentException("Unescaped control character at offset " + i);
			if (ch != '\\') {
				result.append(ch);
				continue;
			}
			if (++i >= literal.length() - 1)
				throw new IllegalArgumentException("Trailing escape character");
			char escaped = literal.charAt(i);
			switch (escaped) {
				case '"', '\\', '/' -> result.append(escaped);
				case 'b' -> result.append('\b');
				case 'f' -> result.append('\f');
				case 'n' -> result.append('\n');
				case 'r' -> result.append('\r');
				case 't' -> result.append('\t');
				case 'u' -> {
					if (i + 4 >= literal.length())
						throw new IllegalArgumentException("Incomplete Unicode escape at offset " + (i - 1));
					String hex = literal.substring(i + 1, i + 5);
					try {
						result.append((char) Integer.parseInt(hex, 16));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("Invalid Unicode escape: \\u" + hex);
					}
					i += 4;
				}
				default -> throw new IllegalArgumentException("Unknown escape sequence: \\" + escaped);
			}
		}
		return result.toString();
	}

	private Maybe<ParsedLiteral> complete(Object value, GenericModelType type) {
		return Maybe.complete(new ParsedLiteral(value, type));
	}

	private Maybe<ParsedLiteral> error(String message) {
		return Maybe.empty(ParseError.create(message));
	}

	private Maybe<EnumType<?>> errorEnum(String message) {
		return Maybe.empty(ParseError.create(message));
	}
}
