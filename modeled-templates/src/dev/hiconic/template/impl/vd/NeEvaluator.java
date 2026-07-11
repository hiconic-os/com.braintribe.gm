package dev.hiconic.template.impl.vd;

import java.util.Objects;

import com.braintribe.gm.model.reason.Maybe;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.Ne;

public class NeEvaluator implements VdEvaluator<Ne, Boolean> {
	@Override
	public Maybe<Boolean> transform(TemplateEvaluationContext context, Ne ne) {
		Object left = ne.getLeft();
		Object right = ne.getRight();
		boolean equal = left instanceof Number && right instanceof Number
				? NumericOperations.compare(left, right) == 0
				: Objects.equals(left, right);
		return Maybe.complete(!equal);
	}
}
