package dev.hiconic.template.impl.vd;

import java.util.Objects;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import com.braintribe.model.generic.reflection.TypeCode;

import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.TypeReference;

final class VdValidation {
	private VdValidation() {
	}

	static boolean equal(Object left, Object right) {
		if (left instanceof Number && right instanceof Number)
			return NumericOperations.compare(left, right) == 0;
		if (left instanceof TypeReference leftType && right instanceof TypeReference rightType)
			return Objects.equals(leftType.getTypeSignature(), rightType.getTypeSignature());
		return Objects.equals(left, right);
	}

	static Reason requireNumeric(ValidationContext context, GenericEntity entity, PropertyLiteral... properties) {
		for (PropertyLiteral property : properties) {
			GenericModelType type = context.getType(entity, property);

			if (type == null || !isNumeric(type.getTypeCode()))
				return InvalidArgument.create(entity.entityType().getShortName() + "." + property.name()
						+ " must evaluate to a numeric type, but evaluates to " + typeSignature(type));
		}

		return null;
	}

	static GenericModelType getType(ValidationContext context, GenericEntity entity, PropertyLiteral property) {
		return context.getType(entity, property);
	}

	static boolean isNumeric(GenericModelType type) {
		return type != null && isNumeric(type.getTypeCode());
	}

	static Reason requireBoolean(ValidationContext context, GenericEntity entity, PropertyLiteral... properties) {
		for (PropertyLiteral property : properties) {
			GenericModelType type = context.getType(entity, property);

			if (type == null || type.getTypeCode() != TypeCode.booleanType)
				return InvalidArgument.create(entity.entityType().getShortName() + "." + property.name()
						+ " must evaluate to boolean, but evaluates to " + typeSignature(type));
		}

		return null;
	}

	static Reason requireBooleanCollection(ValidationContext context, GenericEntity entity, PropertyLiteral property) {
		GenericModelType type = context.getType(entity, property);

		if (!(type instanceof CollectionType)
				|| ((CollectionType) type).getCollectionElementType().getTypeCode() != TypeCode.booleanType)
			return InvalidArgument.create(entity.entityType().getShortName() + "." + property.name()
					+ " must evaluate to a collection of booleans, but evaluates to " + typeSignature(type));

		return null;
	}

	static Reason requireString(ValidationContext context, GenericEntity entity, PropertyLiteral... properties) {
		for (PropertyLiteral property : properties) {
			GenericModelType type = context.getType(entity, property);

			if (type == null || type.getTypeCode() != TypeCode.stringType)
				return InvalidArgument.create(entity.entityType().getShortName() + "." + property.name()
						+ " must evaluate to string, but evaluates to " + typeSignature(type));
		}

		return null;
	}

	private static boolean isNumeric(TypeCode typeCode) {
		return typeCode == TypeCode.integerType
				|| typeCode == TypeCode.longType
				|| typeCode == TypeCode.floatType
				|| typeCode == TypeCode.doubleType
				|| typeCode == TypeCode.decimalType;
	}

	private static String typeSignature(GenericModelType type) {
		return type == null ? "<unknown>" : type.getTypeSignature();
	}
}
