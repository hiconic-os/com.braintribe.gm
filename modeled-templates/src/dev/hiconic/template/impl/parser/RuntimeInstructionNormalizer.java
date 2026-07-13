package dev.hiconic.template.impl.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.generic.collection.PlainList;
import com.braintribe.model.generic.collection.PlainSet;

import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.decl.DeclareInstruction;
import dev.hiconic.template.model.core.decl.RuntimeArguments;
import dev.hiconic.template.model.core.decl.RuntimePropertySpecification;
import dev.hiconic.template.model.core.decl.RuntimePropertyValue;
import dev.hiconic.template.model.core.decl.RuntimeTypeSpecification;
import dev.hiconic.template.model.core.instr.InvokeInstruction;
import dev.hiconic.template.model.parse.TemplateParseError;
import dev.hiconic.template.model.parse.TextRange;

public class RuntimeInstructionNormalizer {
	private final ValidationContext validationContext;
	private final ArgumentValueResolver valueResolver;

	public RuntimeInstructionNormalizer(ValidationContext validationContext) {
		this(validationContext, null);
	}

	public RuntimeInstructionNormalizer(ValidationContext validationContext, ArgumentValueResolver valueResolver) {
		this.validationContext = validationContext;
		this.valueResolver = valueResolver;
	}

	public Maybe<InvokeInstruction> normalize(DeclareInstruction declaration, List<ParsedArgument> parsedArguments) {
		if (declaration.getArgumentType() == null)
			return error("Instruction declaration has not been completed: " + declaration.getName());

		RuntimeTypeSpecification type = declaration.getArgumentType();
		Map<String, RuntimePropertySpecification> byName = new LinkedHashMap<>();
		List<RuntimePropertySpecification> positional = type.getProperties().stream()
				.filter(property -> property.getPositionalIndex() != null)
				.sorted(Comparator.comparing(RuntimePropertySpecification::getPositionalIndex))
				.toList();
		for (RuntimePropertySpecification property : type.getProperties())
			byName.put(property.getName(), property);

		RuntimeArguments arguments = RuntimeArguments.T.create();
		arguments.setTypeSpecification(type);
		arguments.setValues(new ArrayList<>());
		Set<String> supplied = new HashSet<>();
		int positionalIndex = 0;
		boolean namedSeen = false;

		for (int i = 0; i < parsedArguments.size(); i++) {
			ParsedArgument parsed = parsedArguments.get(i);
			RuntimePropertySpecification property;
			if (parsed.name() == null) {
				if (namedSeen)
					return error("Positional argument follows a named argument for instruction "
							+ declaration.getName());
				if (positionalIndex >= positional.size())
					return error("Too many positional arguments for instruction " + declaration.getName());
				property = positional.get(positionalIndex++);
			} else {
				namedSeen = true;
				property = byName.get(parsed.name());
				if (property == null)
					return error("Unknown argument '" + parsed.name() + "' for instruction " + declaration.getName());
			}

			if (!supplied.add(property.getName()))
				return error("Argument '" + property.getName() + "' was supplied more than once");
			GenericModelType expectedType = validationContext.resolveType(property.getTypeSignature());
			if (expectedType == null)
				return error("Unknown argument type: " + property.getTypeSignature());
			boolean variadic = parsed.name() == null
					&& positionalIndex == positional.size()
					&& expectedType instanceof CollectionType
					&& !(expectedType instanceof MapType)
					&& !isLinearCollectionLiteral(parsed.source());
			GenericModelType parsedExpectedType = variadic
					? ((CollectionType) expectedType).getCollectionElementType()
					: expectedType;
			if (parsed.source() != null) {
				if (valueResolver == null)
					return error("Cannot resolve argument '" + property.getName() + "'");
				Maybe<ParsedValueExpression> resolved = valueResolver.resolve(parsed.source(), parsedExpectedType, parsed.range());
				if (resolved.isUnsatisfied())
					return Maybe.empty(located(resolved.whyUnsatisfied(), parsed.range()));
				parsed = new ParsedArgument(parsed.name(), resolved.get().value(), resolved.get().type(), parsed.range());
			}
			if (variadic) {
				CollectionType collectionType = (CollectionType) expectedType;
				GenericModelType elementType = collectionType.getCollectionElementType();
				Collection<Object> values = linearCollection(collectionType);
				while (true) {
					if (!NullLiteral.is(parsed.value())
							&& (parsed.type() == null || !elementType.isAssignableFrom(parsed.type())))
						return error("Argument '" + property.getName() + "' expects elements of "
								+ elementType.getTypeSignature() + " but got "
								+ (parsed.type() == null ? "<unknown>" : parsed.type().getTypeSignature()));
					values.add(NullLiteral.materialize(parsed.value()));
					if (i + 1 >= parsedArguments.size() || parsedArguments.get(i + 1).name() != null)
						break;
					parsed = parsedArguments.get(++i);
					if (parsed.source() != null) {
						Maybe<ParsedValueExpression> resolved = valueResolver.resolve(parsed.source(), elementType, parsed.range());
						if (resolved.isUnsatisfied()) return Maybe.empty(located(resolved.whyUnsatisfied(), parsed.range()));
						parsed = new ParsedArgument(parsed.name(), resolved.get().value(), resolved.get().type(), parsed.range());
					}
				}

				RuntimePropertyValue value = RuntimePropertyValue.T.create();
				value.setSpecification(property);
				value.setValue(values);
				arguments.getValues().add(value);
				continue;
			}
			if (!NullLiteral.is(parsed.value())
					&& (parsed.type() == null || !expectedType.isAssignableFrom(parsed.type())))
				return error("Argument '" + property.getName() + "' expects " + expectedType.getTypeSignature()
						+ " but got " + (parsed.type() == null ? "<unknown>" : parsed.type().getTypeSignature()));

			RuntimePropertyValue value = RuntimePropertyValue.T.create();
			value.setSpecification(property);
			if (parsed.value() instanceof ValueDescriptor descriptor)
				RuntimePropertyValue.value.property().setVdDirect(value, descriptor);
			else
				value.setValue(NullLiteral.materialize(parsed.value()));
			arguments.getValues().add(value);
		}

		for (RuntimePropertySpecification property : type.getProperties())
			if (property.getRequired() && !supplied.contains(property.getName()))
				return error("Missing argument '" + property.getName() + "' for instruction "
						+ declaration.getName());

		InvokeInstruction invocation = InvokeInstruction.T.create();
		invocation.setName(declaration.getName().getName());
		invocation.setDeclaration(declaration);
		invocation.setArguments(arguments);
		invocation.setBody(declaration.getBlock());
		return Maybe.complete(invocation);
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

	private Maybe<InvokeInstruction> error(String message) {
		return Maybe.empty(ParseError.create(message));
	}

	private static Reason located(Reason cause, TextRange range) {
		if (range == null || cause instanceof TemplateParseError) return cause;
		TemplateParseError error = TemplateParseError.T.create();
		error.setText("Invalid declared-instruction argument at line " + range.getStart().getLine()
				+ ", column " + range.getStart().getColumn());
		error.setRange(range);
		error.causedBy(cause);
		return error;
	}
}
