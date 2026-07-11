package dev.hiconic.template.impl.node;

import java.util.List;
import java.util.Objects;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.instr.Switch;
import dev.hiconic.template.model.core.instr.SwitchCase;

public class SwitchEvaluator implements TemplateNodeEvaluator<Switch> {
	@Override
	public void evaluate(TemplateEvaluationContext context, Switch node) {
		List<SwitchCase> cases = node.getCases();
		if (cases != null)
			for (SwitchCase candidate : cases)
				if (Objects.equals(node.getValue(), candidate.getValue())) {
					context.evaluate(candidate.getBlock());
					return;
				}
		if (node.getDefaultBlock() != null)
			context.evaluate(node.getDefaultBlock());
	}

	@Override
	public Reason validate(ValidationContext context, Switch node) {
		if (node.getCases() == null)
			return InvalidArgument.mandatoryPropertyNull(Switch.T, Switch.cases.name());
		for (int i = 0; i < node.getCases().size(); i++) {
			SwitchCase candidate = node.getCases().get(i);
			if (candidate == null || candidate.getBlock() == null)
				return InvalidArgument.create("Switch.cases[" + i + "] must have a block");
		}
		return null;
	}
}
