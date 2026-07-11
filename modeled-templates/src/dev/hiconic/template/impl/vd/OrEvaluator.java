package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.Or;

public class OrEvaluator implements VdEvaluator<Or, Boolean> {
	@Override
	public Maybe<Boolean> transform(TemplateEvaluationContext context, Or or) {
		for (Boolean operand : or.getOperands()) {
			if (operand != null && operand)
				return Maybe.complete(true);
		}
		
		return Maybe.complete(false);
	}

	@Override
	public Reason validate(ValidationContext context, Or or) {
		return VdValidation.requireBooleanCollection(context, or, Or.operands);
	}
}
