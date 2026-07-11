package dev.hiconic.template.impl.parser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.enhance.EnhancedEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.impl.EvaluationPai;

public class ReflectedEntityNormalizer {
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
					return error("Positional argument follows a named argument for " + type.getShortName());
				if (positionalIndex >= positional.length)
					return error("Too many positional arguments for " + type.getShortName());
				propertyName = positional[positionalIndex++];
			} else {
				namedSeen = true;
				propertyName = argument.name();
			}

			if (!supplied.add(propertyName))
				return error("Argument '" + propertyName + "' was supplied more than once");
			Property property = type.findProperty(propertyName);
			if (property == null)
				return error("Unknown argument '" + propertyName + "' for " + type.getShortName());

			boolean variadic = argument.name() == null
					&& positionalIndex == positional.length
					&& property.getType() instanceof CollectionType;
			if (variadic) {
				CollectionType collectionType = (CollectionType) property.getType();
				GenericModelType elementType = collectionType.getCollectionElementType();
				java.util.List<Object> values = new java.util.ArrayList<>();
				while (true) {
					if (argument.type() == null || !elementType.isAssignableFrom(argument.type()))
						return error("Argument '" + propertyName + "' expects elements of "
								+ elementType.getTypeSignature() + " but got "
								+ (argument.type() == null ? "<unknown>" : argument.type().getTypeSignature()));
					values.add(argument.value());
					if (i + 1 >= arguments.size() || arguments.get(i + 1).name() != null)
						break;
					argument = arguments.get(++i);
				}
				property.setDirect(entity, values);
				continue;
			}

			if (argument.type() == null || !property.getType().isAssignableFrom(argument.type()))
				return error("Argument '" + propertyName + "' expects " + property.getType().getTypeSignature()
						+ " but got " + (argument.type() == null
								? "<unknown>"
								: argument.type().getTypeSignature()));

			if (argument.value() instanceof ValueDescriptor descriptor)
				property.setVdDirect(entity, descriptor);
			else
				property.setDirect(entity, argument.value());
		}
		if (entity instanceof ValueDescriptor && entity instanceof EnhancedEntity enhanced)
			enhanced.pushPai(new EvaluationPai());
		return Maybe.complete(entity);
	}

	private <E extends GenericEntity> Maybe<E> error(String message) {
		return Maybe.empty(ParseError.create(message));
	}
}
