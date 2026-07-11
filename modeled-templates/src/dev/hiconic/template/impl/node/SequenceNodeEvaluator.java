package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.SequenceNode;
import dev.hiconic.template.model.core.TemplateNode;

public class SequenceNodeEvaluator implements TemplateNodeEvaluator<SequenceNode> {
	@Override
	public void evaluate(TemplateEvaluationContext context, SequenceNode sequence) {
		for (TemplateNode node : sequence.getNodes())
			context.evaluate(node);
	}

	@Override
	public Reason validate(ValidationContext context, SequenceNode sequence) {
		if (sequence.getNodes() == null)
			return InvalidArgument.mandatoryPropertyNull(SequenceNode.T, SequenceNode.nodes.name());

		for (int i = 0; i < sequence.getNodes().size(); i++)
			if (sequence.getNodes().get(i) == null)
				return InvalidArgument.create("SequenceNode.nodes[" + i + "] must not be null");

		return null;
	}
}
