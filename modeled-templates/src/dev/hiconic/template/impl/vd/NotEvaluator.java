package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.Not;

public class NotEvaluator implements VdEvaluator<Not, Boolean> {
	@Override
	public Maybe<Boolean> transform(TemplateEvaluationContext context, Not not) {
		return Maybe.complete(!not.getOperand());
	}

	@Override
	public Reason validate(ValidationContext context, Not not) {
		return VdValidation.requireBoolean(context, not, Not.operand);
	}
}
