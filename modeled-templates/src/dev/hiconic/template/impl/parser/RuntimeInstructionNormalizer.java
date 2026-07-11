package dev.hiconic.template.impl.parser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.decl.DeclareInstruction;
import dev.hiconic.template.model.core.decl.RuntimeArguments;
import dev.hiconic.template.model.core.decl.RuntimePropertySpecification;
import dev.hiconic.template.model.core.decl.RuntimePropertyValue;
import dev.hiconic.template.model.core.decl.RuntimeTypeSpecification;
import dev.hiconic.template.model.core.instr.InvokeInstruction;

public class RuntimeInstructionNormalizer {
	private final ValidationContext validationContext;

	public RuntimeInstructionNormalizer(ValidationContext validationContext) {
		this.validationContext = validationContext;
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
					&& expectedType instanceof CollectionType;
			if (variadic) {
				CollectionType collectionType = (CollectionType) expectedType;
				GenericModelType elementType = collectionType.getCollectionElementType();
				List<Object> values = new ArrayList<>();
				while (true) {
					if (parsed.type() == null || !elementType.isAssignableFrom(parsed.type()))
						return error("Argument '" + property.getName() + "' expects elements of "
								+ elementType.getTypeSignature() + " but got "
								+ (parsed.type() == null ? "<unknown>" : parsed.type().getTypeSignature()));
					values.add(parsed.value());
					if (i + 1 >= parsedArguments.size() || parsedArguments.get(i + 1).name() != null)
						break;
					parsed = parsedArguments.get(++i);
				}

				RuntimePropertyValue value = RuntimePropertyValue.T.create();
				value.setSpecification(property);
				value.setValue(values);
				arguments.getValues().add(value);
				continue;
			}
			if (parsed.type() == null || !expectedType.isAssignableFrom(parsed.type()))
				return error("Argument '" + property.getName() + "' expects " + expectedType.getTypeSignature()
						+ " but got " + (parsed.type() == null ? "<unknown>" : parsed.type().getTypeSignature()));

			RuntimePropertyValue value = RuntimePropertyValue.T.create();
			value.setSpecification(property);
			value.setValue(parsed.value());
			arguments.getValues().add(value);
		}

		for (RuntimePropertySpecification property : type.getProperties())
			if (property.getRequired() && !supplied.contains(property.getName()))
				return error("Missing argument '" + property.getName() + "' for instruction "
						+ declaration.getName());

		InvokeInstruction invocation = InvokeInstruction.T.create();
		invocation.setName(declaration.getName());
		invocation.setDeclaration(declaration);
		invocation.setArguments(arguments);
		invocation.setBody(declaration.getBlock());
		return Maybe.complete(invocation);
	}

	private Maybe<InvokeInstruction> error(String message) {
		return Maybe.empty(ParseError.create(message));
	}
}
