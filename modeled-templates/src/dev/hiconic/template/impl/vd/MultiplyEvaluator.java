package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.model.core.vd.Multiply;

public class MultiplyEvaluator extends AbstractArithmeticEvaluator<Multiply> {
	@Override
	public Maybe<Number> transform(TemplateEvaluationContext context, Multiply multiply) {
		return Maybe.complete(NumericOperations.multiply(multiply.getLeft(), multiply.getRight()));
	}
}
