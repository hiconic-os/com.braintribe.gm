package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.SimpleTypes;
import com.braintribe.model.generic.reflection.TypeCode;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.model.core.vd.Divide;

public class DivideEvaluator extends AbstractArithmeticEvaluator<Divide> {
	@Override
	public Maybe<Number> transform(TemplateEvaluationContext context, Divide divide) {
		try {
			return Maybe.complete(NumericOperations.divide(divide.getLeft(), divide.getRight()));
		} catch (ArithmeticException e) {
			return Maybe.empty(InvalidArgument.create(e.getMessage()));
		}
	}

	@Override
	protected GenericModelType resultType(GenericModelType leftType, GenericModelType rightType) {
		if (isIntegral(leftType) && isIntegral(rightType))
			return SimpleTypes.TYPE_DECIMAL;

		return super.resultType(leftType, rightType);
	}

	private static boolean isIntegral(GenericModelType type) {
		return type.getTypeCode() == TypeCode.integerType || type.getTypeCode() == TypeCode.longType;
	}
}
