package dev.hiconic.template.impl.vd;

import java.util.List;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.Concat;

public class ConcatEvaluator implements VdEvaluator<Concat, String> {
	@Override
	public Maybe<String> transform(TemplateEvaluationContext context, Concat concat) {
		StringBuilder result = new StringBuilder();
		List<Object> operands = concat.getOperands();
		if (operands == null)
			return Maybe.empty(InvalidArgument.create("Concat.operands must not be null"));

		for (Object operand : operands) {
			if (operand != null)
				result.append(operand);
		}
		return Maybe.complete(result.toString());
	}

	@Override
	public Reason validate(ValidationContext context, Concat concat) {
		return concat.getOperands() == null
				? InvalidArgument.mandatoryPropertyNull(Concat.T, Concat.operands.name())
				: null;
	}
}
