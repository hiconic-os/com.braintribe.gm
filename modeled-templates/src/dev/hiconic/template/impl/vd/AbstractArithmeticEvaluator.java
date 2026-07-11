package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.TypeCode;

import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.ArithmeticOperation;

abstract class AbstractArithmeticEvaluator<V extends ArithmeticOperation> implements VdEvaluator<V, Number> {
	@Override
	public Reason complete(ValidationContext context, V operation) {
		GenericModelType leftType = VdValidation.getType(context, operation, ArithmeticOperation.left);
		GenericModelType rightType = VdValidation.getType(context, operation, ArithmeticOperation.right);

		if (!VdValidation.isNumeric(leftType))
			return numericTypeRequired(operation, ArithmeticOperation.left, leftType);
		if (!VdValidation.isNumeric(rightType))
			return numericTypeRequired(operation, ArithmeticOperation.right, rightType);

		GenericModelType resultType = resultType(leftType, rightType);
		operation.setTypeSignature(resultType.getTypeSignature());
		return null;
	}

	protected GenericModelType resultType(GenericModelType leftType, GenericModelType rightType) {
		return numericRank(leftType) >= numericRank(rightType) ? leftType : rightType;
	}

	private static int numericRank(GenericModelType type) {
		TypeCode code = type.getTypeCode();
		if (code == TypeCode.decimalType)
			return 5;
		if (code == TypeCode.doubleType)
			return 4;
		if (code == TypeCode.floatType)
			return 3;
		if (code == TypeCode.longType)
			return 2;
		return 1;
	}

	private Reason numericTypeRequired(V operation, CharSequence propertyName, GenericModelType actualType) {
		String signature = actualType == null ? "<unknown>" : actualType.getTypeSignature();
		return InvalidArgument.create(operation.entityType().getShortName() + "." + propertyName
				+ " must evaluate to a numeric type, but evaluates to " + signature);
	}
}
