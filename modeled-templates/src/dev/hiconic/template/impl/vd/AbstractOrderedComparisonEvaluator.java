package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.BinaryOperator;

abstract class AbstractOrderedComparisonEvaluator<V extends BinaryOperator> implements VdEvaluator<V, Boolean> {
	@Override
	public Maybe<Boolean> transform(TemplateEvaluationContext context, V operator) {
		return Maybe.complete(test(NumericOperations.compare(operator.getLeft(), operator.getRight())));
	}

	@Override
	public Reason validate(ValidationContext context, V operator) {
		return VdValidation.requireNumeric(context, operator, BinaryOperator.left, BinaryOperator.right);
	}

	protected abstract boolean test(int comparison);
}
