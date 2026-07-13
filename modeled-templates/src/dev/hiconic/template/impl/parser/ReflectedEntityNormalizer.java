package dev.hiconic.template.impl.parser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collection;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.enhance.EnhancedEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.generic.collection.PlainList;
import com.braintribe.model.generic.collection.PlainSet;

import dev.hiconic.template.impl.EvaluationPai;
import dev.hiconic.template.model.parse.TemplateParseError;
import dev.hiconic.template.model.parse.TextRange;

public class ReflectedEntityNormalizer {
	@FunctionalInterface
	public interface ArgumentTypeRecorder {
		void record(GenericEntity entity, Property property, GenericModelType type, dev.hiconic.template.model.parse.TextRange range);
	}
	@FunctionalInterface
	public interface ExpectedTypeResolver {
		GenericModelType resolve(GenericEntity entity, Property property, GenericModelType reflectedType);
	}

	private final ArgumentValueResolver valueResolver;
	private final ArgumentTypeRecorder typeRecorder;
	private final ExpectedTypeResolver expectedTypeResolver;

	public ReflectedEntityNormalizer() {
		this(null, null, null);
	}

	public ReflectedEntityNormalizer(ArgumentValueResolver valueResolver) {
		this(valueResolver, null, null);
	}

	public ReflectedEntityNormalizer(ArgumentValueResolver valueResolver, ArgumentTypeRecorder typeRecorder) {
		this(valueResolver, typeRecorder, null);
	}

	public ReflectedEntityNormalizer(ArgumentValueResolver valueResolver, ArgumentTypeRecorder typeRecorder,
			ExpectedTypeResolver expectedTypeResolver) {
		this.valueResolver = valueResolver;
		this.typeRecorder = typeRecorder;
		this.expectedTypeResolver = expectedTypeResolver;
	}

	public <E extends GenericEntity> Maybe<E> normalize(EntityType<E> type, List<ParsedArgument> arguments) {
		E entity = type.create();
		PositionalArguments annotation = type.getJavaType().getAnnotation(PositionalArguments.class);
		String[] positional = annotation == null ? new String[0] : annotation.value();
		int positionalIndex = 0;
		boolean namedSeen = false;
		Set<String> supplied = new HashSet<>();

		for (int i = 0; i < arguments.size(); i++) {
			ParsedArgument argument = arguments.get(i);
			String propertyName;
			if (argument.name() == null) {
				if (namedSeen)
					return error("Positional argument follows a named argument for " + type.getShortName(), argument.range());
				if (positionalIndex >= positional.length)
					return error("Too many positional arguments for " + type.getShortName(), argument.range());
				propertyName = positional[positionalIndex++];
			} else {
				namedSeen = true;
				propertyName = argument.name();
			}

			if (!supplied.add(propertyName))
				return error("Argument '" + propertyName + "' was supplied more than once", argument.range());
			Property property = type.findProperty(propertyName);
			if (property == null)
				return error("Unknown argument '" + propertyName + "' for " + type.getShortName(), argument.range());
			boolean variadic = argument.name() == null
					&& positionalIndex == positional.length
					&& property.getType() instanceof CollectionType
					&& !(property.getType() instanceof MapType)
					&& !isLinearCollectionLiteral(argument.source());
			GenericModelType argumentType = variadic
					? ((CollectionType) property.getType()).getCollectionElementType()
					: property.getType();
			if (!variadic && expectedTypeResolver != null) {
				GenericModelType resolved = expectedTypeResolver.resolve(entity, property, argumentType);
				if (resolved != null) argumentType = resolved;
			}
			TextRange argumentRange = argument.range();
			Maybe<ParsedArgument> resolvedArgument = resolve(argument, argumentType);
			if (resolvedArgument.isUnsatisfied())
				return resolutionError("Cannot resolve argument '" + propertyName + "' for " + type.getShortName(),
						argumentRange, resolvedArgument.whyUnsatisfied());
			argument = resolvedArgument.get();
			if (variadic) {
				CollectionType collectionType = (CollectionType) property.getType();
				GenericModelType elementType = collectionType.getCollectionElementType();
				Collection<Object> values = linearCollection(collectionType);
				while (true) {
					if (!NullLiteral.is(argument.value())
							&& (argument.type() == null || !elementType.isAssignableFrom(argument.type())))
						return error("Argument '" + propertyName + "' expects elements of "
								+ elementType.getTypeSignature() + " but got "
								+ (argument.type() == null ? "<unknown>" : argument.type().getTypeSignature()), argument.range());
					values.add(NullLiteral.materialize(argument.value()));
					if (i + 1 >= arguments.size() || arguments.get(i + 1).name() != null)
						break;
					ParsedArgument next = arguments.get(++i);
					Maybe<ParsedArgument> resolved = resolve(next, elementType);
					if (resolved.isUnsatisfied()) return resolutionError(
							"Cannot resolve variadic argument '" + propertyName + "'", next.range(), resolved.whyUnsatisfied());
					argument = resolved.get();
				}
				property.setDirect(entity, values);
				record(entity, property, property.getType(), argument.range());
				continue;
			}

			boolean nullLiteral = NullLiteral.is(argument.value());
			if (nullLiteral && !property.isNullable())
				return error("Argument '" + propertyName + "' cannot be null because "
						+ type.getShortName() + "." + propertyName + " is not nullable", argument.range());
			if (!nullLiteral && (argument.type() == null || !argumentType.isAssignableFrom(argument.type())))
				return error("Argument '" + propertyName + "' expects " + argumentType.getTypeSignature()
						+ " but got " + (argument.type() == null
								? "<unknown>"
								: argument.type().getTypeSignature()), argument.range());

			if (argument.value() instanceof ValueDescriptor descriptor)
				property.setVdDirect(entity, descriptor);
			else
				property.setDirect(entity, NullLiteral.materialize(argument.value()));
			record(entity, property, argument.type(), argument.range());
		}
		if (entity instanceof ValueDescriptor && entity instanceof EnhancedEntity enhanced)
			enhanced.pushPai(new EvaluationPai());
		return Maybe.complete(entity);
	}

	private void record(GenericEntity entity, Property property, GenericModelType type,
			dev.hiconic.template.model.parse.TextRange range) {
		if (typeRecorder != null && type != null)
			typeRecorder.record(entity, property, type, range);
	}

	/**
	 * Normalizes an entity while feeding a value into the ordinary positional
	 * binder before the textual arguments. This is the complete semantic of the
	 * pipe operator; collection-valued final positionals consequently receive the
	 * value as their next variadic element without special handling.
	 */
	public <E extends GenericEntity> Maybe<E> normalize(EntityType<E> type, ParsedValueExpression first,
			List<ParsedArgument> arguments) {
		java.util.List<ParsedArgument> seeded = new java.util.ArrayList<>(arguments.size() + 1);
		seeded.add(new ParsedArgument(null, first.value(), first.type(), first.range()));
		seeded.addAll(arguments);
		return normalize(type, seeded);
	}

	private static boolean isLinearCollectionLiteral(String source) {
		if (source == null) return false;
		String literal = source.trim();
		return literal.startsWith("[") || literal.startsWith("list<") || literal.startsWith("set<");
	}

	private static Collection<Object> linearCollection(CollectionType type) {
		return type.getCollectionKind() == CollectionType.CollectionKind.set
				? new PlainSet<>((com.braintribe.model.generic.reflection.SetType) type)
				: new PlainList<>((com.braintribe.model.generic.reflection.ListType) type);
	}

	private Maybe<ParsedArgument> resolve(ParsedArgument argument, GenericModelType expectedType) {
		if (argument.source() == null)
			return Maybe.complete(argument);
		if (valueResolver == null)
			return Maybe.empty(ParseError.create("No argument value resolver configured"));
		Maybe<ParsedValueExpression> resolved = valueResolver.resolve(argument.source(), expectedType, argument.range());
		return resolved.isSatisfied()
				? Maybe.complete(new ParsedArgument(argument.name(), resolved.get().value(), resolved.get().type(), argument.range()))
				: Maybe.empty(resolved.whyUnsatisfied());
	}

	private <E extends GenericEntity> Maybe<E> error(String message) {
		return Maybe.empty(ParseError.create(message));
	}

	private <E extends GenericEntity> Maybe<E> error(String message, TextRange range) {
		if (range == null) return error(message);
		TemplateParseError error = TemplateParseError.T.create();
		error.setText(message + " at line " + range.getStart().getLine() + ", column " + range.getStart().getColumn());
		error.setRange(range);
		return Maybe.empty(error);
	}

	private <E extends GenericEntity> Maybe<E> resolutionError(String message, TextRange range, Reason cause) {
		if (cause instanceof TemplateParseError) return Maybe.empty(cause);
		if (range == null) return Maybe.empty(cause);
		TemplateParseError error = TemplateParseError.T.create();
		error.setText(message + " at line " + range.getStart().getLine() + ", column " + range.getStart().getColumn());
		error.setRange(range);
		error.causedBy(cause);
		return Maybe.empty(error);
	}
}
