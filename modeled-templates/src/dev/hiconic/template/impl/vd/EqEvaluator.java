package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.Eq;

public class EqEvaluator implements VdEvaluator<Eq, Boolean> {
	@Override
	public Maybe<Boolean> transform(TemplateEvaluationContext context, Eq eq) {
		Object left = eq.getLeft();
		Object right = eq.getRight();
		return Maybe.complete(VdValidation.equal(left, right));
	}
}
