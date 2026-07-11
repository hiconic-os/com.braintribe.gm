package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.model.core.vd.Add;

public class AddEvaluator extends AbstractArithmeticEvaluator<Add> {
	@Override
	public Maybe<Number> transform(TemplateEvaluationContext context, Add add) {
		return Maybe.complete(NumericOperations.add(add.getLeft(), add.getRight()));
	}
}
