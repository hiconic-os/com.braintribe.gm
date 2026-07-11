package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.model.core.vd.Subtract;

public class SubtractEvaluator extends AbstractArithmeticEvaluator<Subtract> {
	@Override
	public Maybe<Number> transform(TemplateEvaluationContext context, Subtract subtract) {
		return Maybe.complete(NumericOperations.subtract(subtract.getLeft(), subtract.getRight()));
	}
}
