package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.instr.Break;

public class BreakEvaluator implements TemplateNodeEvaluator<Break> {
	@Override
	public void evaluate(TemplateEvaluationContext context, Break node) {
		throw FlowControlSignal.breakSignal();
	}

	@Override
	public Reason validate(ValidationContext context, Break node) {
		if (Boolean.TRUE.equals(node.getFlowControlBound()))
			return null;
		if (context.canBreak()) {
			node.setFlowControlBound(true);
			return null;
		}
		return InvalidArgument.create("Break requires an enclosing breakable block");
	}
}
