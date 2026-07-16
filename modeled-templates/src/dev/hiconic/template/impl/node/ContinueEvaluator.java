package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.instr.Continue;

public class ContinueEvaluator implements TemplateNodeEvaluator<Continue> {
	@Override
	public void evaluate(TemplateEvaluationContext context, Continue node) {
		throw FlowControlSignal.continueSignal();
	}

	@Override
	public Reason validate(ValidationContext context, Continue node) {
		if (Boolean.TRUE.equals(node.getFlowControlBound()))
			return null;
		if (context.canContinue()) {
			node.setFlowControlBound(true);
			return null;
		}
		return InvalidArgument.create("Continue requires an enclosing continuable block");
	}
}
